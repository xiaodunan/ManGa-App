package com.mobiletrain.manga.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.mobiletrain.manga.config.Config;
import com.mobiletrain.manga.widget.CacheInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by qf on 2016/10/20.
 *
 * 分类列表
 https://route.showapi.com/958-1?page=1&showapi_appid=25288&showapi_timestamp=20161020114238&type=/category/weimanhua/xinqi&showapi_sign=29592e39e269767a187ae23f6947cb46
 详细
 https://route.showapi.com/958-2?id=/dianying/112294.html&showapi_appid=25288&showapi_timestamp=20161020125152&showapi_sign=2a7530e2084018ed0816c235f3209507
 25957
 702efde9767a4072abd454fcf25772f7
 */


public class HttpUtil  {
    private static String APP_ID="25957";
    private static String APP_SIGN="702efde9767a4072abd454fcf25772f7";
    private static OkHttpClient okHttpClient;
    private static String itemjson;
    public static String getCategory(String category,int page,Handler handler, Context context){
        String url="https://route.showapi.com/958-1?page="+page+"&showapi_appid=25957&type=/category/weimanhua/"+category+"&showapi_sign=702efde9767a4072abd454fcf25772f7";
        Log.e("test", "http-getCategory: " );
        return getJson(url,handler,context);
    }

    public static String getItem(String itemId, Handler handler, Context context){
        String url="https://route.showapi.com/958-2?id="+itemId+"&showapi_appid=25957&showapi_sign=702efde9767a4072abd454fcf25772f7";
//        Log.e("test", "getItem: "+url );
        return getJson(url,handler,context);
    }



    public static String getJson(String url, final Handler handler,Context context){
        String json = "";

        if(okHttpClient==null){
            okHttpClient=new OkHttpClient.Builder()
                    .addNetworkInterceptor(new CacheInterceptor())
                    .cache(new Cache(context.getCacheDir(),1024*1024*30))
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20,TimeUnit.SECONDS)
                    .build();
        }
        CacheControl cacheControl ;
        //设置缓存控制器，当有网络时从网络获取数据，没有网络时从缓存获取
        if(isNetworkConnected(context)){
            cacheControl = CacheControl.FORCE_NETWORK;
        }else{
            cacheControl = CacheControl.FORCE_CACHE;
        }

        Request request= new Request.Builder()
                .cacheControl(cacheControl)
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message=handler.obtainMessage();
                message.obj="报告大王！找不到目标，maybe网络数据异常！！！";
                message.what= Config.MSG_WHAT_RESPONSE_FAILE;
                handler.sendMessage(message);
                Log.e("test", "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message=handler.obtainMessage();
                String string = response.body().string();
                message.obj= string;
                message.what=Config.MSG_WHAT_RESPONSE_SUCCESS;
                handler.sendMessage(message);
                Log.e("test", "onResponse: "+string );
                itemjson=string;
                Log.e("test", "onResponse: Config.MSG_WHAT_RESPONSE_SUCCESS");
            }
        });
        return itemjson;
    }

    public static InputStream inputStream = null;
    public static InputStream getBitmapStream(String url, Context context, final Handler handler){
        inputStream = null;
        if(okHttpClient==null){
            okHttpClient=new OkHttpClient.Builder()
                    .addNetworkInterceptor(new CacheInterceptor())
                    .cache(new Cache(context.getCacheDir(),1024*1024*30))
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20,TimeUnit.SECONDS)
                    .build();
        }
//        CacheControl cacheControl ;
//        //设置缓存控制器，当有网络时从网络获取数据，没有网络时从缓存获取
//        if(isNetworkConnected(context)){
//            cacheControl = CacheControl.FORCE_NETWORK;
//        }else{
//            cacheControl = CacheControl.FORCE_CACHE;
//        }

        Request request= new Request.Builder()
//                .cacheControl(cacheControl)
                .url(url)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("test", "onFailure:getBitmapStream ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                inputStream=response.body().byteStream();
                Message msg = handler.obtainMessage(Config.MSG_WHAT_BITMAP_GOT_SUCCESS);
                msg.obj=response.body().byteStream();
                handler.sendMessage(msg);
                Log.e("test", "onResponse: Config.MSG_WHAT_BITMAP_GOT_SUCCESS" );
            }
        });
        return inputStream;
    }

//判断网络状态，需要添加获取wifi和network的权限
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
}
