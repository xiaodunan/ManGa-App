package com.mobiletrain.manga.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by qf on 2016/11/12.
 */
public class PictureDownloadTask extends AsyncTask<String, Integer, Bitmap> {
    private Context context;

    public PictureDownloadTask(Context context) {
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        ByteArrayOutputStream baos = null;
        InputStream bitmapStream =null;
        try {
            Log.e("test", "doInBackground: "+ params[0]);
//            bitmapStream = HttpUtil.getBitmapStream(params[0], context);
            if(bitmapStream!=null){
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = bitmapStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                byte[] bytes = baos.toByteArray();
                Log.e("test", "doInBackground: return Bitmap" +bytes.length);
                HttpUtil.inputStream=null;
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
            Log.e("test", "doInBackground: return null" );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("test", "doInBackground:Exception " );
        }finally {
            if(baos!=null){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bitmapStream!=null){
                try {
                    bitmapStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        if (callback != null) {
            callback.onPreExecute();
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (callback != null) {
            callback.onPostExecute(bitmap);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (callback != null) {
            callback.onProgressUpdate(values);
        }
    }

    PictureDownloadCallback callback;

    public void setPictureDownloadCallback(PictureDownloadCallback callback) {
        this.callback = callback;
    }

    public interface PictureDownloadCallback {
        void onPreExecute();

        void onProgressUpdate(Integer... values);

        void onPostExecute(Bitmap bitmap);
    }
}
