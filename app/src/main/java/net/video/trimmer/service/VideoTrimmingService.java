package net.video.trimmer.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import net.video.trimmer.natives.VideoTrimmer;

public class VideoTrimmingService extends IntentService {
    private static final String TAG = "VideoTrimmingService";
    private VideoTrimmer trimmer;

    public VideoTrimmingService() {
        super(TAG);
        this.trimmer = new VideoTrimmer();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        String inputFileName = extras.getString("inputFileName");
        String outFileName = extras.getString("outputFileName");
        int start = extras.getInt("start");
        int duration = extras.getInt("duration");

        Messenger messenger = (Messenger) extras.get("messenger");
        Log.i(TAG, "Starting trimming");
        System.gc();
        boolean error;
        try {
            int returnStatus = trimmer.trim_(inputFileName, outFileName, start, duration);
            error = returnStatus != 0;
        } catch (Exception e) {
            error = true;
        }
        System.gc();
        String messageText = error ? "Unable to trim the video. Check the error logs." : "Trimmed video succesfully to " + outFileName;
        Log.i(TAG, messageText);
        try {
            Message message = new Message();
            message.arg1 = error ? -1 : 1;
            message.getData().putString("text", messageText);
            messenger.send(message);
        } catch (RemoteException e) {
            Log.i(TAG, "Exception while sending message");
        }
    }
}
