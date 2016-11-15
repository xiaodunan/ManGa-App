package com.mobiletrain.manga.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mobiletrain.manga.config.Config;
import com.mobiletrain.manga.model.MangaBean;
import com.mobiletrain.manga.util.HttpUtil;
import com.mobiletrain.manga.util.JsonUtil;
import com.mobiletrain.manga.util.MyFileUtil;
import com.mobiletrain.manga.util.PictureDownloadTask;
import com.mobiletrain.manga.util.ThreadUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DownloadService extends Service  {

    private String filesDir;
    private String title;
    private List<String> imgList;
    private int currentDownloadPage = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.MSG_WHAT_RESPONSE_SUCCESS:
                    String json = (String) msg.obj;
                    if ("".equals(json) || json == null) {
                        Toast.makeText(DownloadService.this, "∑(っ °Д °;)っ报告大王，下载败啦...", Toast.LENGTH_LONG).show();
                    } else {
                        MangaBean mangabean = JsonUtil.getMangabean(json);
                        if (mangabean != null) {
                            imgList = mangabean.getShowapi_res_body().getItem().getImgList();
                            ThreadUtil.execute(new Runnable() {
                                @Override
                                public void run() {
                                    HttpUtil.getBitmapStream(imgList.get(currentDownloadPage), DownloadService.this,handler);
                                }
                            });
                        }
                    }
                    break;
                case Config.MSG_WHAT_BITMAP_GOT_SUCCESS:
                    final InputStream bitmapStream = (InputStream) msg.obj;
                    ThreadUtil.execute(new Runnable() {
                        @Override
                        public void run() {
                            downLoadImage(bitmapStream);
                        }
                    });
                    break;
                case Config.MSG_WHAT_RESPONSE_FAILE:
                    Toast.makeText(DownloadService.this, "∑(っ °Д °;)っ报告大王，下载失败啦...", Toast.LENGTH_LONG).show();
                    break;
                    case Config.MSG_WHAT_BITMAP_DOWNLOAD_FINNESHED:
                        Toast.makeText(DownloadService.this, "漫画《" + title + "》下载完成", Toast.LENGTH_SHORT).show();
                        break;
            }
        }
    };



    public DownloadService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        String mangaId = intent.getStringExtra("mangaId");
        title = intent.getStringExtra("title");
        filesDir = getExternalFilesDir(null).getAbsolutePath() + File.separator + title;
        if (Config.START_SERVICE_TO_DOWNLOAD_IMAGE.equals(action)) {
            currentDownloadPage=0;
            HttpUtil.getItem(mangaId, handler, this);
        }
        super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;
    }

    private void downLoadImage(InputStream inputStream) {
        InputStream bitmapStream = null;
        ByteArrayOutputStream baos = null;
        try {
            if (inputStream != null) {
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                byte[] bytes = baos.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                MyFileUtil.writeBitmap(DownloadService.this, bitmap, filesDir, title + "-" + currentDownloadPage + ".jpg", Bitmap.CompressFormat.JPEG);
                if (currentDownloadPage == imgList.size() - 1) {
                    handler.sendEmptyMessage(Config.MSG_WHAT_BITMAP_DOWNLOAD_FINNESHED);
                } else {
                    currentDownloadPage++;
                    HttpUtil.getBitmapStream(imgList.get(currentDownloadPage),DownloadService.this,handler);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("test", "handler download exception ");
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(baos!=null){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
