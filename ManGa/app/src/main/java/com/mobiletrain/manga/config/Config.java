package com.mobiletrain.manga.config;

import android.app.Activity;
import android.os.Build;
import android.view.WindowManager;

/**
 * Created by qf on 2016/10/20.
 */
public class Config {
    public static final String MANGA_TYPE_KONGBUMANHUA="kbmh";
    public static final String MANGA_TYPE_GUSHIMANHUA="gushimanhua";
    public static final String MANGA_TYPE_DIANYING="dianying";
    public static final String MANGA_TYPE_DUANZISHOU="duanzishou";
    public static final String MANGA_TYPE_GAOXIAO="gaoxiao";
    public static final String MANGA_TYPE_CHAHUA="chahua";
    public static final String MANGA_TYPE_SHEYING="sheying";
    public static final String ACTION_TYPE_FILLING_UP="ACTION_TYPE_FILLING_UP";
    public static final String ACTION_TYPE_FILLING_DOWN="ACTION_TYPE_FILLING_DOWN";
    public static final String ACTION_TYPE_UPDATA_SUCCESS="ACTION_TYPE_UPDATA_SUCCESS";
    public static final String PTR_UPDATA_TYPE_GET_NEXT_PAGE_DATA="getNextPageData";
    public static final String PTR_UPDATA_TYPE_UP_DATA="updata";
    public static  String CurrenUserLoveID="";
    public static  final String START_SERVICE_TO_DOWNLOAD_IMAGE="downloadImages";
    public static  final int START_SERVICE_TO_CONTINIUE_DOWNLOAD_IMAGE=111;

    public static final int MSG_WHAT_DB_DATA_GOT=11;
    public static final int MSG_WHAT_RESPONSE_FAILE =-1;
    public static final int MSG_WHAT_RESPONSE_SUCCESS=10;
    public static final int MSG_WHAT_BITMAP_GOT_SUCCESS=20;
    public static final int MSG_WHAT_BITMAP_DOWNLOAD_FINNESHED=30;
    public static void transparentStatusBar(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
