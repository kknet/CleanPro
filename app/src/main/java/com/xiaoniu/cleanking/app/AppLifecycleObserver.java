package com.xiaoniu.cleanking.app;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.LockActivity;
import com.xiaoniu.cleanking.ui.main.activity.SplashADHotActivity;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.event.LifecycEvent;
import com.xiaoniu.cleanking.utils.AppLifecycleUtil;
import com.xiaoniu.cleanking.utils.LogUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * @author XiLei
 * @date 2019/10/15.
 * description：监听Application生命周期
 */
public class AppLifecycleObserver implements LifecycleObserver {
    private Context mContext;
    private boolean mIsBack; //mIsBack = true 记录当前已经进入后台

    public AppLifecycleObserver(Context context) {
        this.mContext = context;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onEnterForeground() {
//        LogUtils.i("zzh===="+mIsBack);
        if (null == mContext || !mIsBack || ActivityCollector.isActivityExist(LockActivity.class)) return;
        if (null != AppHolder.getInstance().getSwitchInfoList() && null != AppHolder.getInstance().getSwitchInfoList().getData()
                && AppHolder.getInstance().getSwitchInfoList().getData().size() > 0) {
            for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                if (PreferenceUtil.getHomeBackTime() && PositionId.HOT_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClass(mContext, SplashADHotActivity.class);
                    mContext.startActivity(intent);
                    mIsBack = false;
                }
            }
        }
        EventBus.getDefault().post(new LifecycEvent(true));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onEnterBackground() {
        if (!AppLifecycleUtil.isAppOnForeground(mContext)) {
            //app 进入后台
            mIsBack = true;
            PreferenceUtil.saveHomeBackTime();
        }
    }

}
