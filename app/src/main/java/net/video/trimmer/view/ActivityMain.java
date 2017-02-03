package net.video.trimmer.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import net.video.trimmer.R;
import net.video.trimmer.util.Util;

public class ActivityMain extends Activity {
    private static final String TAG = "ActivityMain";
    public static final String EXTRA_FILE_NAME = "file_name";
    private ListView listView;

    /* Load libraries */
    static {
        System.loadLibrary("ffmpeg");
        System.loadLibrary("video-trimmer");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_main);

        String[] columns = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_ADDED,
        };


        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(videoClickListener);
        queryForVideos(columns);
    }

    private void queryForVideos(String[] columns) {
        new AsyncTask<String[], Void, Cursor>() {

            @Override
            protected Cursor doInBackground(String[]... strings) {
                Cursor videocursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        strings[0], null, null, MediaStore.Video.Media.DATE_TAKEN + " DESC");
                return videocursor;
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                super.onPostExecute(cursor);
                if (cursor != null) {
                    CustomCursorAdapter cursorAdapter = new CustomCursorAdapter(ActivityMain.this, R.layout.item_video, cursor);
                    listView.setAdapter(cursorAdapter);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, columns);
    }

    private OnItemClickListener videoClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);

            int fileNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            String filename = cursor.getString(fileNameIndex);
            Log.d(TAG, "onItemClick:input:  " + filename);
            Log.d(TAG, "onItemClick:output:  " + Util.getTargetFileName(filename));
            Intent intent = new Intent(ActivityMain.this, ActivityVideoEditor.class);
            intent.putExtra(EXTRA_FILE_NAME, filename);
            startActivity(intent);
        }
    };
}
