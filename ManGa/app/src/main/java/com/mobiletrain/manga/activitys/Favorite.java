package com.mobiletrain.manga.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiletrain.manga.R;
import com.mobiletrain.manga.adapter.TrackAdapter;
import com.mobiletrain.manga.config.Config;
import com.mobiletrain.manga.model.TrackBean;
import com.mobiletrain.manga.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Favorite extends AppCompatActivity {

    public static final String TAG = "Favorite";
    @BindView(R.id.tvBack_Track)
    TextView tvBackTrack;
    @BindView(R.id.lv_Track)
    ListView lvTrack;
    @BindView(R.id.tvWarning)
    TextView tvWarning;
    @BindView(R.id.tvTitlt)
    TextView tvTitlt;
    private SQLiteDatabase db;
    private List<TrackBean> trackBeans = new ArrayList<>();
    private TrackAdapter trackAdapter;
    private final int MSG_WHAT_DATA_GOT = 10;
    private final int MSG_WHAT_DATA_DELETE = 30;



    private Handler hander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_DATA_GOT:
                    if (trackBeans.size() == 0) {
                        tvWarning.setVisibility(View.VISIBLE);
                    } else {
                        trackAdapter = new TrackAdapter(trackBeans, Favorite.this);
                        lvTrack.setAdapter(trackAdapter);
                        tvWarning.setVisibility(View.GONE);
                    }
                    break;
                case MSG_WHAT_DATA_DELETE:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        ButterKnife.bind(this);

        setTvBackTrackClickListener();
        initData();
        setLvTrackItemClickListener();
        setLvTrackItemLongClickListener();
    }

    private void setLvTrackItemLongClickListener() {
        lvTrack.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                final TrackBean bean = trackBeans.get(i);
                Toast.makeText(Favorite.this, "取消收藏:" + bean.getTitle(), Toast.LENGTH_SHORT).show();
                trackBeans.remove(i);
                trackAdapter.notifyDataSetChanged();
                ThreadUtil.execute(new Runnable() {
                    @Override
                    public void run() {
                        db = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
                        db.execSQL("delete from "+ Config.CurrenUserLoveID+" where _id = " + bean.getId());
                        db.close();
                    }
                });
                return true;
            }
        });
    }

    private void setLvTrackItemClickListener() {
        lvTrack.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Favorite.this, MangaWebpage.class);
                TrackBean bean = trackBeans.get(i);
                intent.putExtra("type", bean.getType());
                intent.putExtra("title", bean.getTitle());
                intent.putExtra("mangaId", bean.getMangaId());
                intent.putExtra("link", bean.getLink());
                intent.putExtra("thumbnail", bean.getThumbnail());
                startActivity(intent);
            }
        });
    }

    private void initData() {
        tvTitlt.setText("收藏");
        Intent intent = getIntent();
        final String loveId = intent.getStringExtra("loveId");
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    db = openOrCreateDatabase("user.db", MODE_PRIVATE, null);
                    Cursor cursor = db.query(loveId, null, null, null, null, null, null);
                    while (cursor.moveToNext()) {
                        String type = cursor.getString(cursor.getColumnIndex("type"));
                        String title = cursor.getString(cursor.getColumnIndex("title"));
                        String link = cursor.getString(cursor.getColumnIndex("link"));
                        String mangaId = cursor.getString(cursor.getColumnIndex("mangaId"));
                        String thumbnail = cursor.getString(cursor.getColumnIndex("thumbnail"));
                        String time = cursor.getString(cursor.getColumnIndex("time"));
                        int id = cursor.getInt(cursor.getColumnIndex("_id"));
                        TrackBean bean = new TrackBean(id, type, title, link, mangaId, thumbnail, time);
                        trackBeans.add(bean);
                    }
                    db.close();
                    hander.sendEmptyMessage(MSG_WHAT_DATA_GOT);
                }
            });
    }


    private void setTvBackTrackClickListener() {
        tvBackTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Favorite.this, MainActivity.class));
            }
        });
    }

}
