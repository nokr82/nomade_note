package com.devstories.nomadnote_android.base;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore.Images;
import android.widget.BaseAdapter;

import java.util.Hashtable;
import java.util.Stack;

/**
 * Created by dev1 on 2018-02-08.
 */

public class ImageLoader {

    Hashtable<Integer, Bitmap> loadImages;
    Hashtable<Integer, String> positionRequested;
    BaseAdapter listener;
    int runningCount = 0;
    Stack<ItemPair> queue;
    ContentResolver resolver;

    public ImageLoader(ContentResolver r) {
        loadImages = new Hashtable<Integer, Bitmap>();
        positionRequested = new Hashtable<Integer, String>();
        queue = new Stack<ItemPair>();
        resolver = r;
    }

    public void setListener(BaseAdapter adapter) {
        listener = adapter;
        reset();
    }

    public void reset() {
        positionRequested.clear();
        runningCount = 0;
        queue.clear();
    }

    public Bitmap getImage(int uid, String path, int orientation) {
        Bitmap image = loadImages.get(uid);
        if (image != null) {
            return image;
        }

        if (!positionRequested.containsKey(uid)) {
            positionRequested.put(uid, path);
            if (runningCount >= 15) {
                queue.push(new ItemPair(uid, path, orientation));
            } else {
                runningCount++;
                new LoadImageAsyncTask().execute(uid, path, orientation);
            }
        }
        return null;
    }

    public void getNextImage() {
        if (!queue.isEmpty()) {
            ItemPair item = queue.pop();
            new LoadImageAsyncTask().execute(item.uid, item.path, item.orientation);
        }
    }

    public class LoadImageAsyncTask extends AsyncTask<Object, Void, Bitmap> {

        Integer uid;

        @Override
        protected Bitmap doInBackground(Object... params) {

            this.uid = (Integer) params[0];
            String photoPath = (String) params[1];
            Integer orientation = (Integer) params[2];

            String[] proj = { Images.Thumbnails.DATA };

            Cursor mini = Images.Thumbnails.queryMiniThumbnail(resolver, uid, Images.Thumbnails.MINI_KIND, proj);
            if (mini != null && mini.moveToFirst()) {
                photoPath = mini.getString(mini.getColumnIndex(proj[0]));
            } else {
                mini = Images.Thumbnails.queryMiniThumbnail(resolver, uid, Images.Thumbnails.MINI_KIND, proj);
                if (mini != null && mini.moveToFirst()) {
                    photoPath = mini.getString(mini.getColumnIndex(proj[0]));
                }
            }

            Bitmap bitmap = Utils.getImage(resolver, photoPath, 300);

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            runningCount--;
            if (result != null) {
                loadImages.put(uid, result);
                listener.notifyDataSetChanged();
                getNextImage();
            }
        }
    }

    public static class ItemPair {

        Integer uid;
        String path;
        Integer orientation;

        public ItemPair(Integer uid, String path, Integer orientation) {
            this.uid = uid;
            this.path = path;
            this.orientation = orientation;
        }
    }
}
