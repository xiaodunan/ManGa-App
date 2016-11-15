package com.mobiletrain.manga.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobiletrain.manga.R;
import com.mobiletrain.manga.model.TrackBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by qf on 2016/10/29.
 */
public class TrackAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private List<TrackBean> trackBeans;
    Context context;

    public TrackAdapter(List<TrackBean> trackBeans, Context context) {
        this.trackBeans = trackBeans;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return trackBeans.size();
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
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.track_item, parent, false);
            holder=new Holder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (Holder) convertView.getTag();
        }
        TrackBean trackBean = trackBeans.get(position);

        holder.tvTrackTitle.setText(trackBean.getTitle());
        holder.tvTrackType.setText(trackBean.getType());
        holder.tvTrackTime.setText(trackBean.getTime());
        if(position%2!=0){
            holder.rlTrackRoot.setBackgroundColor(context.getResources().getColor(R.color.track_item_background_ii));
        }
        return convertView;
    }

    static class Holder {
        @BindView(R.id.tvTrackTitle)
        TextView tvTrackTitle;
        @BindView(R.id.tvTrackType)
        TextView tvTrackType;
        @BindView(R.id.tvTrackTime)
        TextView tvTrackTime;
        @BindView(R.id.rlTrackRoot)
        RelativeLayout rlTrackRoot;

        Holder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
