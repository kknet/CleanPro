package com.xiaoniu.cleanking.ui.tool.notify.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.AppManager;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.interfac.AnimationStateListener;
import com.xiaoniu.cleanking.ui.newclean.activity.ScreenFinishBeforActivity;
import com.xiaoniu.cleanking.ui.tool.notify.adapter.NotifyCleanAdapter;
import com.xiaoniu.cleanking.ui.tool.notify.bean.NotificationInfo;
import com.xiaoniu.cleanking.ui.tool.notify.event.FinishCleanFinishActivityEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.FunctionCompleteEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.NotificationCleanEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.NotificationSetEvent;
import com.xiaoniu.cleanking.ui.tool.notify.event.ResidentUpdateEvent;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.CleanUtil;
import com.xiaoniu.cleanking.utils.ExtraConstant;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.StatusBarUtil;
import com.xiaoniu.common.widget.xrecyclerview.XRecyclerView;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * 授权后通知栏详情页面
 */
public class NotifyCleanDetailActivity extends BaseActivity {
    private XRecyclerView mRecyclerView;
    private View mHeaderView;
    private TextView mTvNotificationCount;
    private NotifyCleanAdapter mNotifyCleanAdapter;
    private boolean mIsClearNotification;
    private TextView mTvDelete;
    private LinearLayout ll_list;
    private NotityCleanAnimView mCleanAnimView;

    private LinearLayout mTitleBar;
    private ImageView mIvBack;
    private ImageView mIvSet;
    private boolean isCleanFinish = false;

    String sourcePage = "";
    String currentPage = "";
    String pageviewEventCode = "";
    String pageviewEventName = "";
    String returnEventName = "";
    String sysReturnEventName = "";
    private boolean mIsFinish; //是否点击了返回键

    public static void startNotificationCleanActivity(Context context) {
        if (context != null) {
            Intent intent = new Intent(context, NotifyCleanDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_notifaction_clean;
    }

    @Override
    protected void initVariable(Intent intent) {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTransparentForWindow(NotifyCleanDetailActivity.this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        currentPage = "notification_scan_result_page";
        pageviewEventName = "用户在通知清理诊断页浏览";
        pageviewEventCode = "notification_scan_result_page_view_page";
        returnEventName = "用户在通知清理诊断页返回";
        sysReturnEventName = "用户在通知清理诊断页返回";
        sourcePage = AppManager.getAppManager().preActivityName().contains("MainActivity") ? "home_page" : "";

        mTitleBar = findViewById(R.id.title_bar);
        ll_list = findViewById(R.id.ll_list);
        mRecyclerView = findViewById(R.id.notify_recyclerView);
        mTvDelete = findViewById(R.id.tv_delete);
        mIvBack = findViewById(R.id.iv_back_notity);
        mIvSet = findViewById(R.id.iv_set);
        StatusBarUtil.setPaddingTop(mContext, mTitleBar);
        StatusBarUtil.setPaddingTop(mContext, ll_list);

        mHeaderView = mInflater.inflate(R.layout.layout_notification_clean_header, null);
        mTvNotificationCount = mHeaderView.findViewById(R.id.tvNotificationCount);
        mTvNotificationCount.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/D-DIN.otf"));
        mHeaderView.findViewById(R.id.lay_notify_clean_tips).setVisibility(View.VISIBLE);
        mRecyclerView.setHeaderView(mHeaderView);
        hideToolBar();
        mCleanAnimView = findViewById(R.id.view_clean_anim);
        //已清理圆形动画监控
        mCleanAnimView.setAnimationStateListener(new AnimationStateListener() {
            @Override
            public void onAnimationEnd() {
                showCleanFinishView();
            }

            @Override
            public void onAnimationStart() {

                currentPage = "notification_clean_success_page";
                pageviewEventName = "通知清理结果页展示页浏览";
                pageviewEventCode = "notification_clean_success_page_view_page";
                returnEventName = "通知清理结果页展示页返回";
                sysReturnEventName = "通知清理结果页展示页返回";
                sourcePage = "notification_clean_animation_page";
                NiuDataAPI.onPageStart(pageviewEventCode, pageviewEventName);
                NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, pageviewEventCode, pageviewEventName);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //通知栏清理浏览"
        NiuDataAPI.onPageStart(pageviewEventCode, pageviewEventName);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, pageviewEventCode, pageviewEventName);
    }

    @Override
    protected void setListener() {
        mIvBack.setOnClickListener(v -> {
            mIsFinish = true;
            finish();
            //通知栏清理返回 点击"
            StatisticsUtils.trackClick("return_click", returnEventName, sourcePage, currentPage);
        });

        mIvSet.setOnClickListener(v -> {
            NotifyCleanSetActivity.start(NotifyCleanDetailActivity.this);
            //通知栏清理返回 点击",
            StatisticsUtils.trackClick("whitelist_click", "用户在通知清理诊断页点击白名单按钮", sourcePage, currentPage);
        });

        mTvDelete.setOnClickListener(v -> {
            StatisticsUtils.trackClick("notification_cleaning_button_click", "用户在通知栏清理扫描结果页点击【清理】按钮", sourcePage, currentPage);
            //通知栏清理
            AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.NOTITY);
            mIsClearNotification = true;
            NotifyCleanManager.getInstance().cleanAllNotification();
            EventBus.getDefault().post(new ResidentUpdateEvent(true));
            mNotifyCleanAdapter.clear();

            mCleanAnimView.setData(CleanUtil.formatShortFileSize(100), NotityCleanAnimView.page_junk_clean);
            mCleanAnimView.setVisibility(View.VISIBLE);
            //清理动画
            mCleanAnimView.startTopAnim(false);
            //title bar
//            showBarColor(getResources().getColor(R.color.color_06C581));
            mTitleBar.setVisibility(View.GONE);
            //----进入动画页
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, pageviewEventCode, pageviewEventName);


            currentPage = "notification_clean_animation_page";
            pageviewEventName = "用户在通知清理动画页浏览";
            pageviewEventCode = "notification_clean_animation_page_view_page";
            returnEventName = "用户在通知清理动画页返回";
            sysReturnEventName = "用户在通知清理动画页返回";
            sourcePage = "notification_clean_result_page";
            NiuDataAPI.onPageStart(pageviewEventCode, pageviewEventName);
            NiuDataAPIUtil.onPageEnd(sourcePage, currentPage, pageviewEventCode, pageviewEventName);

        });
    }

    /**
     * 状态栏颜色变化
     *
     * @param animatedValue
     */
    public void showBarColor(int animatedValue) {
        mTitleBar.setBackgroundColor(animatedValue);
        mTitleBar.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarCompat.setStatusBarColor(this, animatedValue, true);
        } else {
            StatusBarCompat.setStatusBarColor(this, animatedValue, false);
        }
    }

    @Override
    protected void loadData() {
        ArrayList<NotificationInfo> notificationList = NotifyCleanManager.getInstance().getAllNotifications();
        mNotifyCleanAdapter = new NotifyCleanAdapter(mContext);
        mNotifyCleanAdapter.setData(notificationList);
        mRecyclerView.setAdapter(mNotifyCleanAdapter);
        mTvNotificationCount.setText(notificationList.size() + "");

        if (notificationList.size() <= 0) {
            showCleanFinishView();
        }
    }

    @Subscribe
    public void onEventMainThread(NotificationCleanEvent event) {
        if (!mIsClearNotification && event != null) {
            ArrayList<NotificationInfo> notificationList = NotifyCleanManager.getInstance().getAllNotifications();
            mNotifyCleanAdapter.setData(notificationList);
            mTvNotificationCount.setText(notificationList.size() + "");
            if (notificationList.size() <= 0) {
                showCleanFinishView();
            }
        }
    }

    @Subscribe
    public void onEventMainThread(NotificationSetEvent event) {
        if (event != null && !event.isEnable()) {
            NotifyCleanManager.getInstance().cleanAllNotification();
            EventBus.getDefault().post(new ResidentUpdateEvent(false));
            mNotifyCleanAdapter.clear();
            showCleanFinishView();
        }
    }

    @Override
    public void onBackPressed() {
        mIsFinish = true;
        StatisticsUtils.trackClick("system_return_click", sysReturnEventName, sourcePage, currentPage);
        super.onBackPressed();
    }

    private void showCleanFinishView() {
//        isCleanFinish = true;
//        /*显示完成页*/
//        mCleanAnimView.setVisibility(View.VISIBLE);
//        showBarColor(getResources().getColor(R.color.color_06C581));
//        mCleanAnimView.setViewTrans();
        //通知栏清理完成浏览
//        StatisticsUtils.trackClick("Notice_Bar_Cleaning_Completed_view_page", "\"通知栏清理完成\"浏览", "Notice_Bar_Cleaning_page", "Notice_Bar_Cleaning_Completed_page");


        if (mIsFinish) return;
        //保存通知栏清理完成时间
        if (PreferenceUtil.getNotificationCleanTime()) {
            PreferenceUtil.saveNotificationCleanTime();
        }
        PreferenceUtil.saveCleanNotifyUsed(true);
        AppHolder.getInstance().setCleanFinishSourcePageId("notification_clean_success_page");
        EventBus.getDefault().post(new FinishCleanFinishActivityEvent());

        EventBus.getDefault().post(new FunctionCompleteEvent(getString(R.string.tool_notification_clean)));

        startActivity(new Intent(this, ScreenFinishBeforActivity.class)
                .putExtra(ExtraConstant.TITLE, getString(R.string.tool_notification_clean)));
        finish();
    }

}
