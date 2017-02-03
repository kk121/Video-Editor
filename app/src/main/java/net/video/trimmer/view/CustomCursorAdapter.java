package net.video.trimmer.view;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import net.video.trimmer.R;
import net.video.trimmer.util.Util;

public final class CustomCursorAdapter extends ResourceCursorAdapter {

    CustomCursorAdapter(Context context, int layout, Cursor c) {
        super(context, layout, c, true);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView videoPreview = (ImageView) view.findViewById(R.id.image_preview);
        fetchAndSetThumbnail(context, videoPreview, getId(cursor, MediaStore.Video.Media._ID));
        String fileName = getString(cursor, MediaStore.Video.Media.DISPLAY_NAME);
        TextView tvFileName = (TextView) view.findViewById(R.id.tv_file_name);
        tvFileName.setText(fileName);
        TextView tvDuration = (TextView) view.findViewById(R.id.tv_duration);
        tvDuration.setText(getTime(cursor, MediaStore.Video.Media.DURATION));
        TextView tvDateAdded = (TextView) view.findViewById(R.id.tv_added_date);
        tvDateAdded.setText(getString(cursor, MediaStore.Video.Media.DATE_ADDED));
    }

    private void fetchAndSetThumbnail(final Context context, final ImageView videoPreview, final int videoId) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... voids) {
                Bitmap thumbnail = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(),
                        videoId, MediaStore.Video.Thumbnails.MICRO_KIND, new BitmapFactory.Options());
                return thumbnail;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null && videoPreview != null)
                    videoPreview.setImageBitmap(bitmap);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private int getId(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        return cursor.getInt(index);
    }

    private String getString(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndexOrThrow(columnName);
        return cursor.getString(index);
    }

    private String getTime(Cursor cursor, String columnName) {
        int time = getId(cursor, columnName);
        return Util.toFormattedTime(time);
    }
}