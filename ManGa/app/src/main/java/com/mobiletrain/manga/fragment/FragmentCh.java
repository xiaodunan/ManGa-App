package com.mobiletrain.manga.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.mobiletrain.manga.R;
import com.mobiletrain.manga.activitys.MangaWebpage;
import com.mobiletrain.manga.adapter.CategoryBeanAdapter;
import com.mobiletrain.manga.config.Config;
import com.mobiletrain.manga.model.CategoryBean;
import com.mobiletrain.manga.util.HttpUtil;
import com.mobiletrain.manga.util.JsonUtil;
import com.mobiletrain.manga.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by qf on 2016/10/21.
 */
public class FragmentCh extends Fragment {
    public static final String TAG="test";
    View view;
    @BindView(R.id.ptr)
    PtrFrameLayout ptr;
    @BindView(R.id.gv_kbmh)
    GridView gvKbmh;
    private List<CategoryBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> data = new ArrayList<>();
    private int dataSize = 30;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.MSG_WHAT_RESPONSE_FAILE:
                    Log.e(TAG, "MSG_WHAT_RESPONSE_FAILE: " );
                    onDataAcquisitionFail();
                    break;
                case Config.MSG_WHAT_RESPONSE_SUCCESS:
                    onDataAcquisitionSuccess(msg);
                    Log.e(TAG, "MSG_WHAT_RESPONSE_SUCCESS: " );
                    break;
            }
        }
    };

    private MaterialHeader materialHeader;
    private CategoryBeanAdapter adapter;
    private int currentPage=1;
    private int scrollType =-1;
    private float touchY=0;
    private float moveY=0;
    private int addCount=0;
    private boolean isAdded;
    private String updataType="";
    private boolean firstTimeToUpData=true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.contentlist_fragment_layout, container, false);
            ButterKnife.bind(this, view);
            initData(currentPage);
            initGridView();
            initPtr();
            setGvOnScrollListener();
        }
        return view;
    }

    private void setGvOnScrollListener() {
        gvKbmh.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i){
                    case SCROLL_STATE_TOUCH_SCROLL:
                        scrollType=1;
                        break;
                    case SCROLL_STATE_FLING:
                        scrollType=2;
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (fsListener!=null){
                    fsListener.onFramentScroll(scrollType);
                }
                //有网络又能看见最后一个item的时候刷新列表
                if(HttpUtil.isNetworkConnected(getContext())&&firstVisibleItem+visibleItemCount==totalItemCount){
                    currentPage++;
                    updateData(currentPage,Config.PTR_UPDATA_TYPE_GET_NEXT_PAGE_DATA);//刷新列表
                }

            }
        });

        gvKbmh.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        touchY=event.getY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        moveY=event.getY();
                        Log.e(TAG, "ACTION_MOVE: moveY-touchY"+(moveY-touchY)+"--scrollType"+scrollType);
                        if(moveY-touchY>0&&scrollType==2){
                            //下抛显示toolbar
                            scrollType=1;
                            getContext().sendBroadcast(new Intent(Config.ACTION_TYPE_FILLING_DOWN));
                        }else if(moveY-touchY<0&&scrollType==2){
                            //上抛隐藏toolbar
                            scrollType=1;
                            getContext().sendBroadcast(new Intent(Config.ACTION_TYPE_FILLING_UP));
                        }
                        break;
                }
                return false;
            }
        });
    }

    private void initPtr() {
        materialHeader = new MaterialHeader(getActivity());
        materialHeader.setColorSchemeColors(new int[]{Color.RED, Color.GREEN, Color.BLUE});
        ptr.setHeaderView(materialHeader);
        ptr.addPtrUIHandler(materialHeader);
        ptr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                updateData(1,Config.PTR_UPDATA_TYPE_UP_DATA);//刷新列表
            }
        });

    }

    private void initGridView() {
        adapter = new CategoryBeanAdapter(data, getActivity());
        gvKbmh.setAdapter(adapter);
        gvKbmh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(data.size()!=0){
                    takenToMangaWebpage(position);
                }
            }
        });
    }

    private void takenToMangaWebpage(int position) {
        Intent intent = new Intent(getActivity(), MangaWebpage.class);
        String mangaLink = data.get(position).getLink();
        String title = data.get(position).getTitle();
        String mangaId = data.get(position).getId();
        String thumbnail = data.get(position).getThumbnailList().get(0);
        intent.putExtra("type","插画");
        intent.putExtra("title",title);
        intent.putExtra("mangaId",mangaId);
        intent.putExtra("link",mangaLink);
        intent.putExtra("thumbnail",thumbnail);
        startActivity(intent);
    }

    private void initData(final int page) {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                HttpUtil.getCategory(Config.MANGA_TYPE_CHAHUA, page, handler,getContext());
            }
        });
    }

    private void updateData(int page,String upDataType) {
        updataType=upDataType;
        initData(page);
    }

    private void onDataAcquisitionSuccess(Message msg) {
        String json = (String) msg.obj;
        if ("".equals(json) || json == null) {
            if(ptr.isRefreshing()){
                ptr.refreshComplete();
                Toast.makeText(getActivity(),"∑(っ °Д °;)っ报告大王，加载xi败啦...",Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                CategoryBean categoryBean = JsonUtil.getCategoryBean(json);
                if (categoryBean != null) {
                    List<CategoryBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> contentlist = categoryBean.getShowapi_res_body().getPagebean().getContentlist();
                    addCount=0;
                    for (int i = 0; i < contentlist.size(); i++) {
                        isAdded=false;
                        CategoryBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean bean = contentlist.get(i);
                        for (int j = 0; j < data.size(); j++) {
                            if(data.get(j).getTitle().equals(bean.getTitle())){
                                isAdded=true;
                            }
                        }
                        if(!isAdded){
                            if(firstTimeToUpData){
                                data.add(bean);
                            }else{
                                if(Config.PTR_UPDATA_TYPE_UP_DATA.equals(updataType)){
                                    data.add(0,bean);
                                }else{
                                    data.add(bean);
                                }
                            }
                            addCount++;
                        }
                    }
                    if(addCount>0){
                        if(firstTimeToUpData){
                            firstTimeToUpData=false;
                        }
                        adapter.setData(data);
                    }
                    Log.e(TAG, "onDataAcquisitionSuccess: " );
                    Intent intent = new Intent(Config.ACTION_TYPE_UPDATA_SUCCESS);
                    intent.putExtra("addCount",addCount);
                    getContext().sendBroadcast(intent);
                    if("updata".equals(updataType)){

                    }else if("getNextPageData".equals(updataType)){
                        currentPage++;
                    }
                }else {
                    Log.e(TAG, "categoryBean == null: " );
                }

                if(ptr.isRefreshing()){
                    ptr.refreshComplete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                ptr.refreshComplete();
                Toast.makeText(getActivity(),"∑(っ °Д °;)っ报告大王，加载xi败啦...",Toast.LENGTH_LONG).show();
            }

        }
    }

    private void onDataAcquisitionFail() {
        Toast.makeText(getActivity(),"∑(っ °Д °;)っ报告大王，加载xi败啦...",Toast.LENGTH_LONG).show();
        if(ptr.isRefreshing()){
            ptr.refreshComplete();
        }
    }


    private onFragmentScrollListener fsListener;

    public void setOnFragmentScrollListener(onFragmentScrollListener fsListener){
        this.fsListener=fsListener;
    }

    public interface onFragmentScrollListener{
        void onFramentScroll(int SCROLLTYPE);
    }

}


