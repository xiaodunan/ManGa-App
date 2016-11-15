package com.mobiletrain.manga.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobiletrain.manga.R;
import com.mobiletrain.manga.model.CategoryBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qf on 2016/10/22.
 */
public class CategoryBeanAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    List<CategoryBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> data;
    Context context;

    public CategoryBeanAdapter(List<CategoryBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> data, Context context) {
        this.data = data;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    public void setData(List<CategoryBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size()==0?10:data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_rvadapter_grid, parent, false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        if(data.size()!=0){
            CategoryBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean bean = data.get(position);
            holder.listItemTitle.setText(bean.getTitle());
            holder.listItemTime.setText(bean.getTime());
            holder.listItemThumbnai.setImageURI(Uri.parse(bean.getThumbnailList().get(0)));
            holder.listItemWatchTimes.setText(((int) (Math.random() * 5000))+"");
        }

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.list_item_thumbnai)
        ImageView listItemThumbnai;
        @BindView(R.id.list_item_title)
        TextView listItemTitle;
        @BindView(R.id.list_item_time)
        TextView listItemTime;
        @BindView(R.id.list_item_watch_times)
        TextView listItemWatchTimes;
        @BindView(R.id.cvRoot)
        CardView cvRoot;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
