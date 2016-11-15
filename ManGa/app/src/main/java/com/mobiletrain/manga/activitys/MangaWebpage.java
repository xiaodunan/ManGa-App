package com.mobiletrain.manga.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobiletrain.manga.R;
import com.mobiletrain.manga.config.Config;
import com.mobiletrain.manga.services.DownloadService;
import com.mobiletrain.manga.util.HttpUtil;
import com.mobiletrain.manga.util.ThreadUtil;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class MangaWebpage extends AppCompatActivity {

    public static final String TAG = "manga";
    @BindView(R.id.wv)
    WebView wv;
    @BindView(R.id.tbiv)
    ImageView tbiv;
    @BindView(R.id.ctl)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.abl)
    AppBarLayout abl;
    @BindView(R.id.nsv)
    NestedScrollView nsv;
    @BindView(R.id.fabMenu)
    FloatingActionButton fabMenu;
    @BindView(R.id.fabShare)
    FloatingActionButton fabShare;
    @BindView(R.id.fabDownload)
    FloatingActionButton fabDownload;
    @BindView(R.id.fabFavorite)
    FloatingActionButton fabFavorite;

    private boolean ismMenuShowing = false;
    private ViewPropertyAnimator fabShareAnimator;
    private ViewPropertyAnimator fabDownloadAnimator;
    private ViewPropertyAnimator fabMenuAnimator;
    private SQLiteDatabase db;
    private String link;
    private String title;
    private String mangaId;
    private String thumbnail;
    private String type;
    private String time;
    private ViewPropertyAnimator fabFavoriteAnimator;
    private final int MSG_WHAT_DB_FAVORITE_IS_ADDED = 33;
    private final int MSG_WHAT_DB_FAVORITE_ADD_SUCCESS = 44;

    private Handler handler = new Handler() {
        private List<String> imgList;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_DB_FAVORITE_ADD_SUCCESS:
                    Toast.makeText(MangaWebpage.this, "添加收藏成功！", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_WHAT_DB_FAVORITE_IS_ADDED:
                    Toast.makeText(MangaWebpage.this, "您已经收藏过啦！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_webpage);
        ButterKnife.bind(this);
        Config.transparentStatusBar(this);
        ShareSDK.initSDK(this, "18f73bc10af4c");
        initData();
        setFabMenuClickListener();
        setFabShareClickListener();
        setFabDownloadClickListener();
        setFabFavoriteClickListener();
        recordTrack();
    }

    private void recordTrack() {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                db = openOrCreateDatabase("track.db", MODE_PRIVATE, null);
                db.execSQL("create table if not exists user_track (_id integer primary key autoincrement,type text,title text,link text,mangaId text,thumbnail text,time text)");
                db.execSQL("insert into user_track(type,title,link,mangaId,thumbnail,time) values('" + type + "','" + title + "','" + link + "','" + mangaId + "','" + thumbnail + "','" + time + "')");
                db.close();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        time = ((String) formatter.format(new Date(System.currentTimeMillis())));
        link = intent.getStringExtra("link");
        type = intent.getStringExtra("type");
        title = intent.getStringExtra("title");
        mangaId = intent.getStringExtra("mangaId");
        thumbnail = intent.getStringExtra("thumbnail");

        fabShareAnimator = fabShare.animate();
        fabDownloadAnimator = fabDownload.animate();
        fabMenuAnimator = fabMenu.animate();
        fabFavoriteAnimator = fabFavorite.animate();

        if (thumbnail != null || !"".equals(thumbnail)) {
            Picasso.with(this).load(Uri.parse(thumbnail))
                    .into(tbiv);
        }

        wv.loadUrl(link);
        ctl.setTitle(title);
    }

    private void setFabFavoriteClickListener() {
        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Config.CurrenUserLoveID != null && !"".equals(Config.CurrenUserLoveID)) {
                    ThreadUtil.execute(new Runnable() {
                        @Override
                        public void run() {
                            db = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
                            db.execSQL("create table if not exists " + Config.CurrenUserLoveID + " (_id integer primary key autoincrement,type text,title text,link text,mangaId text,thumbnail text,time text)");
                            Cursor cursor = db.rawQuery("select * from " + Config.CurrenUserLoveID + " where title = ?", new String[]{title});
                            if (cursor.moveToNext()) {
                                handler.sendEmptyMessage(MSG_WHAT_DB_FAVORITE_IS_ADDED);
                            } else {
                                db.execSQL("insert into " + Config.CurrenUserLoveID + "(type,title,link,mangaId,thumbnail,time) values('" + type + "','" + title + "','" + link + "','" + mangaId + "','" + thumbnail + "','" + time + "')");
                                handler.sendEmptyMessage(MSG_WHAT_DB_FAVORITE_ADD_SUCCESS);
                            }
                            cursor.close();
                            db.close();
                        }
                    });
                } else {
                    Toast.makeText(MangaWebpage.this, "大爷~还没登陆呢！", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setFabShareClickListener() {
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShare();
//                Toast.makeText(MangaWebpage.this, "fabShare!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFabDownloadClickListener() {
        fabDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(HttpUtil.isNetworkConnected(MangaWebpage.this)){
                    Toast.makeText(MangaWebpage.this, "Download!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MangaWebpage.this, DownloadService.class);
                    intent.setAction(Config.START_SERVICE_TO_DOWNLOAD_IMAGE);
                    intent.setPackage(getPackageName());
                    intent.putExtra("mangaId",mangaId);
                    intent.putExtra("title",title);
                    startService(intent);
                }else{
                    Toast.makeText(MangaWebpage.this, "网络异常!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setFabMenuClickListener() {
        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ismMenuShowing) {
                    Log.e(TAG, "ismMenuShowing=false ");
                    fabMenuAnimator.setDuration(200).rotation(-90f);
                    fabShareAnimator.setDuration(200).translationY(180f).rotation(-0f);
                    fabDownloadAnimator.setDuration(300).translationY(360f).rotation(-0f);
                    fabFavoriteAnimator.setDuration(400).translationY(540f).rotation(-0f);
                    ismMenuShowing = true;
                } else {
                    Log.e(TAG, "ismMenuShowing=true ");
                    fabMenuAnimator.setDuration(200).rotation(0f);
                    fabShareAnimator.setDuration(350).translationY(0f).rotation(90f);
                    fabDownloadAnimator.setDuration(500).translationY(0f).rotation(90f);
                    fabFavoriteAnimator.setDuration(650).translationY(0f).rotation(90f);
                    ismMenuShowing = false;
                }

            }
        });
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
//关闭sso授权
        oks.disableSSOWhenAuthorize();
// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle(title);
// titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl(link);
// text是分享文本，所有平台都需要这个字段
        oks.setText(title);
// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl(thumbnail);
// url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(link);
// comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
// site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
// siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(link);
// 启动分享GUI
        oks.show(this);
    }

}
