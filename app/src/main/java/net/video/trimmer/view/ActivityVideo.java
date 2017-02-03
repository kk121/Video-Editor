package net.video.trimmer.view;

import android.app.Activity;
import android.os.Bundle;
import android.widget.VideoView;

import net.video.trimmer.R;

public class ActivityVideo extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        VideoView videoView = (VideoView) findViewById(R.id.VideoView);

        String filePath = getIntent().getStringExtra("fileName");
        if (filePath != null && !filePath.isEmpty()) {
            videoView.setVideoPath(filePath);
            videoView.start();
        }
    }
}
