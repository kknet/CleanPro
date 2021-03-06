package com.xiaoniu.cleanking.ui.main.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by lang.chen on 2019/8/7
 */
public class RecyclerViewMesure extends RecyclerView {

    public RecyclerViewMesure(Context context) {
        super(context);
    }


    public RecyclerViewMesure(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewMesure(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
