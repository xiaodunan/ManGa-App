package com.mobiletrain.manga.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by qf on 2016/10/21.
 */
public class MyLinearLayoutManager extends LinearLayoutManager {
    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }
    private int[] mMeasureDimension=new int[2];

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int widthMode = View.MeasureSpec.getMode(widthSpec);
        int heightMode = View.MeasureSpec.getMode(heightSpec);
        int widthSize = View.MeasureSpec.getSize(widthSpec);
        int heightSize = View.MeasureSpec.getSize(heightSpec);
        int width=0;
        int height=0;

        for (int i = 0; i < getItemCount(); i++) {
            if(getOrientation()==HORIZONTAL){
                measureScrapChild(recycler,i,View.MeasureSpec.makeMeasureSpec(i,View.MeasureSpec.UNSPECIFIED),heightSpec,mMeasureDimension);
                width=width+mMeasureDimension[0];
                if(i==0){
                    height=mMeasureDimension[1];
                }else{
                    measureScrapChild(recycler,i,widthSpec,View.MeasureSpec.makeMeasureSpec(i,View.MeasureSpec.UNSPECIFIED),mMeasureDimension);
                    height= height+mMeasureDimension[1];
                    if(i==0){
                        width=mMeasureDimension[0];
                    }
                }
            }
            switch (widthMode){
                case  View.MeasureSpec.EXACTLY:
                    width=widthSize;
                case  View.MeasureSpec.AT_MOST:
                case  View.MeasureSpec.UNSPECIFIED:
            }
            switch (heightMode){
                case  View.MeasureSpec.EXACTLY:
                    height=heightSize;
                case  View.MeasureSpec.AT_MOST:
                case  View.MeasureSpec.UNSPECIFIED:
            }
            setMeasuredDimension(width,height);
        }


//        super.onMeasure(recycler, state, widthSpec, heightSpec);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler,int position, int widthSpec, int heightSpec,int[]mMeasureDimension){
        View view = recycler.getViewForPosition(position);
        recycler.bindViewToPosition(view,position);
        if(view!=null){
            RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) view.getLayoutParams();
            int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec, getPaddingLeft() + getPaddingRight(), p.width);
            int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec, getPaddingTop() + getPaddingBottom(), p.height);
            view.measure(childWidthSpec,childHeightSpec);
           mMeasureDimension[0]= view.getMeasuredWidth()+p.leftMargin+p.rightMargin;
           mMeasureDimension[1]= view.getMeasuredHeight()+p.topMargin+p.bottomMargin;
            recycler.recycleView(view);

        }
    }

}
