package net.video.trimmer.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import net.video.trimmer.R;
import net.video.trimmer.model.VideoPlayerState;
import net.video.trimmer.service.VideoTrimmingService;
import net.video.trimmer.util.Util;

public class ActivityVideoEditor extends Activity implements OnClickListener {
    protected final int LOADING_DIALOG = 1;
    protected final int MESSAGE_DIALOG = 2;
    protected final int VALIDATION_DIALOG = 3;

    private VideoView videoView;
    private TextView detailView;
    private Button btnPlayTrimmedVideo;
    private String outputFileName;
    private VideoPlayerState videoPlayerState = new VideoPlayerState();

    protected Handler completionHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            videoPlayerState.setMessageText(msg.getData().getString("text"));
            removeDialog(LOADING_DIALOG);
            showDialog(MESSAGE_DIALOG);
            stopService(new Intent(ActivityVideoEditor.this, VideoTrimmingService.class));
            if (msg.arg1 == 1) {
                btnPlayTrimmedVideo.setVisibility(View.VISIBLE);
                videoView.start();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_video);
        setVideoPlayerLastState();
        btnPlayTrimmedVideo = (Button) findViewById(R.id.playTrimmedVideo);
        videoView = (VideoView) findViewById(R.id.VideoView);
        videoView.setVideoPath(videoPlayerState.getFilename());

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.requestFocus();
        videoView.start();

        findViewById(R.id.Start).setOnClickListener(this);
        findViewById(R.id.Stop).setOnClickListener(this);
        findViewById(R.id.Trim).setOnClickListener(this);
        btnPlayTrimmedVideo.setOnClickListener(this);

        detailView = (TextView) findViewById(R.id.Details);
        refreshDetailView();
    }

    private void setVideoPlayerLastState() {
        VideoPlayerState lastState = (VideoPlayerState) getLastNonConfigurationInstance();
        if (lastState != null)
            videoPlayerState = lastState;
        else {
            Bundle extras = getIntent().getExtras();
            videoPlayerState.setFilename(extras.getString(ActivityMain.EXTRA_FILE_NAME));
        }
    }


    @Override
    public Object onRetainNonConfigurationInstance() {
        return videoPlayerState;
    }


    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(videoPlayerState.getCurrentTime());
    }


    @Override
    protected void onPause() {
        super.onPause();
        videoPlayerState.setCurrentTime(videoView.getCurrentPosition());
        if (videoView.canPause())
            videoView.pause();
    }


    private void onTrimClick() {
        if (videoView.canPause())
            videoView.pause();
        if (!videoPlayerState.isValid()) {
            showDialog(VALIDATION_DIALOG);
            return;
        }

        String inputFileName = videoPlayerState.getFilename();
        outputFileName = Util.getTargetFileName(inputFileName);
        Intent intent = new Intent(ActivityVideoEditor.this, VideoTrimmingService.class);
        intent.putExtra("inputFileName", inputFileName);
        intent.putExtra("outputFileName", outputFileName);
        intent.putExtra("start", videoPlayerState.getStart() / 1000);
        intent.putExtra("duration", videoPlayerState.getDuration() / 1000);
        intent.putExtra("messenger", new Messenger(completionHander));

        startService(intent);
        showDialog(LOADING_DIALOG);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case MESSAGE_DIALOG:
                ((AlertDialog) dialog).setMessage(videoPlayerState.getMessageText());
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog;
        switch (id) {
            case VALIDATION_DIALOG:
                dialog = simpleAlertDialog(getString(R.string.dialog_invalid_stop_time));
                break;
            case LOADING_DIALOG:
                dialog = ProgressDialog.show(ActivityVideoEditor.this, "", "Trimming...", true, true);
                break;
            case MESSAGE_DIALOG:
                dialog = simpleAlertDialog("");
                break;
            default:
                dialog = null;
        }
        return dialog;
    }


    private Dialog simpleAlertDialog(String message) {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setCancelable(true)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityVideoEditor.this.removeDialog(MESSAGE_DIALOG);
                                ActivityVideoEditor.this.removeDialog(LOADING_DIALOG);
                            }
                        });
        dialog = builder.create();
        return dialog;
    }

    private void refreshDetailView() {
        String start = Util.toFormattedTime(videoPlayerState.getStart());
        String stop = Util.toFormattedTime(videoPlayerState.getStop());
        detailView.setText("Start at " + start + "\nEnd at " + stop);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Start:
                videoPlayerState.setStart(videoView.getCurrentPosition());
                refreshDetailView();
                break;
            case R.id.Stop:
                int stop = videoView.getCurrentPosition();
                videoPlayerState.setStop(stop);
                refreshDetailView();
                break;
            case R.id.Trim:
                onTrimClick();
                break;
            case R.id.playTrimmedVideo:
                if (videoView.canPause())
                    videoView.pause();
                Intent intent = new Intent(this, ActivityVideo.class);
                intent.putExtra("fileName", outputFileName);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        btnPlayTrimmedVideo.setVisibility(View.GONE);
    }
}