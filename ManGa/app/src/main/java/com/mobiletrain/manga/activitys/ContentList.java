package com.mobiletrain.manga.activitys;

import android.content.Context;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mobiletrain.manga.R;
import com.mobiletrain.manga.config.Config;
import com.mobiletrain.manga.model.MangaBean;
import com.mobiletrain.manga.util.HttpUtil;
import com.mobiletrain.manga.util.JsonUtil;
import com.mobiletrain.manga.util.ThreadUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ContentList extends AppCompatActivity {
    public static final String TAG = "test";
    List<ImageView> imgs = new ArrayList<>();

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.MSG_WHAT_RESPONSE_FAILE:
                    Toast.makeText(ContentList.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case Config.MSG_WHAT_RESPONSE_SUCCESS:
                    onResponseSuccess(msg);
                    break;
            }
        }
    };



    @BindView(R.id.tbiv)
    ImageView tbiv;
    @BindView(R.id.ctl)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.abl)
    AppBarLayout abl;
    @BindView(R.id.nsv)
    NestedScrollView nsv;
    @BindView(R.id.fabMenu)
    FloatingActionButton fab;
    @BindView(R.id.llForImg)
    LinearLayout llForImg;
    private WindowManager windowManager;
    private int width;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list);
        ButterKnife.bind(this);
        Config.transparentStatusBar(this);
        Intent intent = getIntent();
        final String manGaId = intent.getStringExtra("id");
        Log.e(TAG, "id: " + manGaId);

        getScreenWidth();
        loadImage(manGaId);

    }

    private void getScreenWidth() {
        windowManager = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
        width = windowManager.getDefaultDisplay().getWidth();
    }

    private void loadImage(final String manGaId) {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                HttpUtil.getItem(manGaId, handler,ContentList.this);
            }
        });
    }

    private void onResponseSuccess(Message msg) {
        String json = (String) msg.obj;
        if ("".equals(json) || json == null) {
        } else {
            try {
                MangaBean mangabean = JsonUtil.getMangabean(json);
                if (mangabean != null) {
                    List<String> imgList = mangabean.getShowapi_res_body().getItem().getImgList();

                    for (int i = 0; i < imgList.size(); i++) {
                        ImageView imageView = new ImageView(ContentList.this);
                        llForImg.addView(imageView);
                        imgs.add(imageView);
                    }
                    Log.e(TAG, "imgs.size: "+imgs.size() );
                    Log.e(TAG, "llForImg.getChildCount: "+llForImg.getChildCount() );
                    for (int i = 0; i < imgs.size(); i++) {
                        ImageView img = imgs.get(i);
                        Picasso.with(ContentList.this)
                                .load(Uri.parse(imgList.get(i)))
                                .error(R.mipmap.failure_image)
                                .placeholder(R.mipmap.ic_launcher)
                                .resize(width,img.getHeight())
                                .into(img);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}



