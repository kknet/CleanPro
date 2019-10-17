package com.xiaoniu.cleanking.ui.newclean.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.RouteConstants;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.activity.FileManagerHomeActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.bean.NewsListInfo;
import com.xiaoniu.cleanking.ui.main.bean.NewsType;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.bean.VideoItemInfo;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.cleanking.ui.main.event.CleanEvent;
import com.xiaoniu.cleanking.ui.main.presenter.CleanFinishPresenter;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.news.adapter.NewsListAdapter;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.cleanking.ui.tool.wechat.activity.WechatCleanHomeActivity;
import com.xiaoniu.cleanking.utils.AndroidUtil;
import com.xiaoniu.cleanking.utils.NiuDataAPIUtil;
import com.xiaoniu.cleanking.utils.NumberUtils;
import com.xiaoniu.cleanking.utils.update.PreferenceUtil;
import com.xiaoniu.common.http.EHttp;
import com.xiaoniu.common.http.callback.ApiCallback;
import com.xiaoniu.common.http.request.HttpRequest;
import com.xiaoniu.common.utils.NetworkUtils;
import com.xiaoniu.common.utils.StatisticsUtils;
import com.xiaoniu.common.utils.ToastUtils;
import com.xiaoniu.statistic.NiuDataAPI;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cn.jzvd.Jzvd;

/**
 * 1.2.0 版本以后清理完成 显示资讯
 */
public class NewCleanFinishActivity extends BaseActivity<CleanFinishPresenter> implements View.OnClickListener {

    private static final String TAG = "AD_DEMO";
    private String mTitle;
    private TextView mTitleTv;
    private TextView mTvSize;
    private TextView mTvGb;
    private TextView mTvQl;

    private XRecyclerView mRecyclerView;
    private NewsListAdapter mNewsAdapter;
    private NewsType mType = NewsType.TOUTIAO;
    private static final int PAGE_NUM = 20;//每一页数据
    private boolean mIsRefresh = true;
    private TextView mIvSpeedClean;
    private TextView mIvPowerClean;
    private TextView mIvNotificationClean;
    private TextView mIvWeChatClean;
    private TextView mIvFileClean;
    private TextView mIvCooling;
    private ImageView mBtnLeft;

    public View v_quicken, v_power, v_notification, v_wechat, v_file, v_cool;
    public View line_quicken, line_power, line_notification, line_wechat, line_file;
    private ImageView iv_advert_logo, iv_advert, iv_advert_logo2, iv_advert2;
    private TextView tv_advert, tv_advert_content, tv_advert2, tv_advert_content2, tv_download2;
    private View v_advert, v_advert2;

    private NativeUnifiedADData mNativeUnifiedADData, mNativeUnifiedADData2;
    private NativeUnifiedAD mAdManager, mAdManager2;
    private MediaView mMediaView, mMediaView2;
    private NativeAdContainer mContainer, mContainer2;

    private String mAdvertId = ""; //广告位1
    private String mAdvertId2 = ""; //广告位2

    private TextView tv_quicken, tv_power, tv_notification;
    private ImageView iv_quicken, iv_power, iv_notification;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_finish_layout;
    }

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
    }

    @Override
    protected void initView() {
        mPresenter.getSwitchInfoList();
        mBtnLeft = (ImageView) findViewById(R.id.btnLeft);
        mTitleTv = (TextView) findViewById(R.id.tvTitle);
        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);

        mNewsAdapter = new NewsListAdapter(this);
        View header = LayoutInflater.from(this).inflate(R.layout.layout_finish_head, findViewById(android.R.id.content), false);
        View headerTool = LayoutInflater.from(this).inflate(R.layout.layout_finish_head_tool, findViewById(android.R.id.content), false);
        mRecyclerView.addHeaderView(header);
        mRecyclerView.addHeaderView(headerTool);
        mRecyclerView.setLimitNumberToCallLoadMore(1);
        mRecyclerView.setAdapter(mNewsAdapter);
        mRecyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);

        mTvSize = header.findViewById(R.id.tv_size);
        mTvGb = header.findViewById(R.id.tv_clear_finish_gb_title);
        mTvQl = header.findViewById(R.id.tv_ql);

        mContainer = header.findViewById(R.id.native_ad_container);
        mMediaView = header.findViewById(R.id.gdt_media_view);
        v_advert = header.findViewById(R.id.v_advert);
        iv_advert_logo = header.findViewById(R.id.iv_advert_logo);
        iv_advert = header.findViewById(R.id.iv_advert);
        tv_advert = header.findViewById(R.id.tv_advert);
        tv_advert_content = header.findViewById(R.id.tv_advert_content);

        mContainer2 = headerTool.findViewById(R.id.native_ad_container);
        mMediaView2 = headerTool.findViewById(R.id.gdt_media_view);
        v_advert2 = headerTool.findViewById(R.id.v_advert);
        iv_advert_logo2 = headerTool.findViewById(R.id.iv_advert_logo);
        iv_advert2 = headerTool.findViewById(R.id.iv_advert);
        tv_advert2 = headerTool.findViewById(R.id.tv_advert);
        tv_advert_content2 = headerTool.findViewById(R.id.tv_advert_content);
        tv_download2 = headerTool.findViewById(R.id.tv_download);

        v_quicken = headerTool.findViewById(R.id.v_quicken);
        v_power = headerTool.findViewById(R.id.v_power);
        v_notification = headerTool.findViewById(R.id.v_notification);
        v_wechat = headerTool.findViewById(R.id.v_wechat);
        v_file = headerTool.findViewById(R.id.v_file);
        v_cool = headerTool.findViewById(R.id.v_cool);
        line_quicken = headerTool.findViewById(R.id.line_quicken);
        line_power = headerTool.findViewById(R.id.line_power);
        line_notification = headerTool.findViewById(R.id.line_notification);
        line_wechat = headerTool.findViewById(R.id.line_wechat);
        line_file = headerTool.findViewById(R.id.line_file);

        mIvSpeedClean = headerTool.findViewById(R.id.iv_speed_clean);
        mIvPowerClean = headerTool.findViewById(R.id.iv_power_clean);
        mIvNotificationClean = headerTool.findViewById(R.id.iv_notification_clean);
        mIvWeChatClean = headerTool.findViewById(R.id.iv_wechat_clean);
        mIvFileClean = headerTool.findViewById(R.id.iv_file_clean);
        mIvCooling = headerTool.findViewById(R.id.iv_cooling);

        tv_quicken = headerTool.findViewById(R.id.tv_quicken);
        tv_power = headerTool.findViewById(R.id.tv_power);
        tv_notification = headerTool.findViewById(R.id.tv_notification);

        iv_quicken = headerTool.findViewById(R.id.iv_quicken);
        iv_power = headerTool.findViewById(R.id.iv_power);
        iv_notification = headerTool.findViewById(R.id.iv_notification);
        setListener();
        loadData();
    }

    /**
     * 拉取广告开关成功
     *
     * @return
     */
    public void getSwitchInfoListSuccess(SwitchInfoList list) {
        Log.d(TAG, "getSwitchInfoListSuccess -- list.getData()=" + list.getData().size());
        for (SwitchInfoList.DataBean switchInfoList : list.getData()) {

            if (getString(R.string.tool_one_key_speed).contains(mTitle)) { //一键加速
                if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
                if (PositionId.KEY_JIASU.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                    initNativeUnifiedAD2();
                }
            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) { //超强省电
                if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
                if (PositionId.KEY_CQSD.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                    initNativeUnifiedAD2();
                }
            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {//通知栏清理
                if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
                if (PositionId.KEY_NOTIFY.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                    initNativeUnifiedAD2();
                }
            } else if (getString(R.string.tool_chat_clear).contains(mTitle) || getString(R.string.tool_chat_clear_n).contains(mTitle)) {//微信专情
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
                if (PositionId.KEY_WECHAT.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                    initNativeUnifiedAD2();
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) { //手机降温
                if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
                if (PositionId.KEY_COOL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                    initNativeUnifiedAD2();
                }
            } else { //立即清理
                if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_ONE_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId = switchInfoList.getAdvertId();
                    initNativeUnifiedAD();
                }
                if (PositionId.KEY_CLEAN_ALL.equals(switchInfoList.getConfigKey()) && PositionId.DRAW_TWO_CODE.equals(switchInfoList.getAdvertPosition()) && switchInfoList.isOpen()) {
                    mAdvertId2 = switchInfoList.getAdvertId();
                    initNativeUnifiedAD2();
                }
            }
        }
        Log.d(TAG, "mAdvertId=" + mAdvertId);
        Log.d(TAG, "mAdvertId2=" + mAdvertId2);
    }

    /**
     * 拉取广告开关失败
     *
     * @return
     */
    public void getSwitchInfoListFail() {

    }

    private void initNativeUnifiedAD() {
        mAdManager = new NativeUnifiedAD(this, PositionId.APPID, mAdvertId, new NativeADUnifiedListener() {

            @Override
            public void onNoAD(AdError adError) {
                mContainer.setVisibility(View.GONE);
                Log.d(TAG, "onNoAd error code: " + adError.getErrorCode() + ", error msg: " + adError.getErrorMsg());
            }

            @Override
            public void onADLoaded(List<NativeUnifiedADData> ads) {
                Log.d(TAG, "ads.size()=" + ads.size());
                if (ads != null && ads.size() > 0) {
                    mContainer.setVisibility(View.VISIBLE);
                    Message msg = Message.obtain();
                    msg.what = MSG_INIT_AD;
                    mNativeUnifiedADData = ads.get(0);
                    msg.obj = mNativeUnifiedADData;
                    mHandler.sendMessage(msg);
                }
            }
        });
        /**
         * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前，调用下面两个方法，有助于提高视频广告的eCPM值 <br/>
         * 如果广告位仅支持图文广告，则无需调用
         */

        /**
         * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
         *
         * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
         *
         * 例如开发者设置了VideoOption.AutoPlayPolicy.NEVER，表示从不自动播放 <br/>
         * 但满足某种条件(如晚上10点)时，开发者调用了startVideo播放视频，这在用户看来仍然是自动播放的
         */
        mAdManager.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO); // 本次拉回的视频广告，从用户的角度看是自动播放的

        /**
         * 设置在视频广告播放前，用户看到显示广告容器的渲染者是SDK还是开发者 <p/>
         *
         * 一般来说，用户看到的广告容器都是SDK渲染的，但存在下面这种特殊情况： <br/>
         *
         * 1. 开发者将广告拉回后，未调用bindMediaView，而是用自己的ImageView显示视频的封面图 <br/>
         * 2. 用户点击封面图后，打开一个新的页面，调用bindMediaView，此时才会用到SDK的容器 <br/>
         * 3. 这种情形下，用户先看到的广告容器就是开发者自己渲染的，其值为VideoADContainerRender.DEV
         * 4. 如果觉得抽象，可以参考NativeADUnifiedDevRenderContainerActivity的实现
         */
        mAdManager.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK); // 视频播放前，用户看到的广告容器是由SDK渲染的
        mAdManager.loadData(AD_COUNT);
    }

    private void initNativeUnifiedAD2() {
        mAdManager2 = new NativeUnifiedAD(this, PositionId.APPID, mAdvertId2, new NativeADUnifiedListener() {
            @Override
            public void onNoAD(AdError adError) {
                mContainer2.setVisibility(View.GONE);
                Log.d(TAG, "onNoAd error code222: " + adError.getErrorCode() + ", error msg: " + adError.getErrorMsg());
            }

            @Override
            public void onADLoaded(List<NativeUnifiedADData> ads) {
                Log.d(TAG, "ads.size()2222=" + ads.size());
                if (ads != null && ads.size() > 0) {
                    mContainer2.setVisibility(View.VISIBLE);
                    Message msg2 = Message.obtain();
                    msg2.what = MSG_INIT_AD2;
                    mNativeUnifiedADData2 = ads.get(0);
                    msg2.obj = mNativeUnifiedADData2;
                    mHandler.sendMessage(msg2);
                }
            }
        });
        /**
         * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前，调用下面两个方法，有助于提高视频广告的eCPM值 <br/>
         * 如果广告位仅支持图文广告，则无需调用
         */

        /**
         * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
         *
         * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
         *
         * 例如开发者设置了VideoOption.AutoPlayPolicy.NEVER，表示从不自动播放 <br/>
         * 但满足某种条件(如晚上10点)时，开发者调用了startVideo播放视频，这在用户看来仍然是自动播放的
         */
        mAdManager2.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO); // 本次拉回的视频广告，从用户的角度看是自动播放的

        /**
         * 设置在视频广告播放前，用户看到显示广告容器的渲染者是SDK还是开发者 <p/>
         *
         * 一般来说，用户看到的广告容器都是SDK渲染的，但存在下面这种特殊情况： <br/>
         *
         * 1. 开发者将广告拉回后，未调用bindMediaView，而是用自己的ImageView显示视频的封面图 <br/>
         * 2. 用户点击封面图后，打开一个新的页面，调用bindMediaView，此时才会用到SDK的容器 <br/>
         * 3. 这种情形下，用户先看到的广告容器就是开发者自己渲染的，其值为VideoADContainerRender.DEV
         * 4. 如果觉得抽象，可以参考NativeADUnifiedDevRenderContainerActivity的实现
         */
        mAdManager2.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK); // 视频播放前，用户看到的广告容器是由SDK渲染的
        mAdManager2.loadData(AD_COUNT);
    }

    private void changeUI(Intent intent) {
        if (PreferenceUtil.isNoFirstOpenCLeanFinishApp()) {
            if (AppHolder.getInstance().getSwitchInfoList() != null) {
//                refreshAd();
//                refreshAd2();
                /*for (SwitchInfoList.DataBean switchInfoList : AppHolder.getInstance().getSwitchInfoList().getData()) {
                    if (PositionId.FINISH_ID.equals(switchInfoList.getId())) {
                        if (switchInfoList.isIsOpen()) {
                            //加载广告
                            refreshAd();
                        }
                    }
                }*/ //暂时注释
            }
        } else {
            PreferenceUtil.saveFirstOpenCLeanFinishApp();
        }

        if (intent != null) {
            mTitle = intent.getStringExtra("title");
            String num = intent.getStringExtra("num");
            String unit = intent.getStringExtra("unit");
            mTvSize.setText(num);
            mTvGb.setText(unit);
            if (TextUtils.isEmpty(mTitle))
                mTitle = getString(R.string.app_name);
            if (getString(R.string.app_name).contains(mTitle)) {
                //悟空清理
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
                CleanEvent cleanEvent = new CleanEvent();
                cleanEvent.setCleanAminOver(true);
                EventBus.getDefault().post(cleanEvent);
                //建议清理
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
                //QQ专清
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("手机很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }
            } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
                //一键加速
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已加速");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快试试其他功能吧！");
                }


            } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
                //超强省电
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已达到最佳状态");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他功能");
                }

            } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
                //通知栏清理
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("通知栏很干净");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快去体验其他炫酷功能");
                }
            } else if (getString(R.string.tool_chat_clear).contains(mTitle) || getString(R.string.tool_chat_clear_n).contains(mTitle)) {
                //微信专情
                if (TextUtils.isEmpty(num) || num.equals("0.0") || num.equals("0")) {
                    mTvSize.setText("");
                    mTvGb.setText("已清理");
                    mTvGb.setTextSize(20);
                    mTvQl.setText("快试试其他功能吧！");
                }
            } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
                //手机降温
                mTvSize.setText("");
                int tem = new Random().nextInt(3) + 1;
                mTvGb.setText("成功降温" + tem + "°C");
                mTvGb.setTextSize(20);
                mTvQl.setText("60s后达到最佳降温效果");
            }
            mTitleTv.setText(mTitle);
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                tv_quicken.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(70, 85)) + "%");
            } else {
                tv_quicken.setText(getString(R.string.internal_storage_scale, NumberUtils.mathRandom(40, 100)) + "%");
            }

            iv_power.setImageResource(R.drawable.icon_power_r);
            tv_power.setTextColor(ContextCompat.getColor(this, R.color.color_FF4545));
            tv_power.setText(getString(R.string.power_consumption_num, NumberUtils.mathRandom(5, 15)));

            if (!NotifyUtils.isNotificationListenerEnabled()) {
                iv_notification.setImageResource(R.drawable.icon_home_qq_o);
                tv_notification.setTextColor(ContextCompat.getColor(this, R.color.color_FFAC01));
                tv_notification.setText(R.string.find_harass_notify);
            } else {
                iv_notification.setImageResource(R.drawable.icon_home_qq_r);
                tv_notification.setTextColor(ContextCompat.getColor(this, R.color.color_FF4545));
                tv_notification.setText(getString(R.string.find_harass_notify_num, NumberUtils.mathRandom(5, 20)));
            }
        }

        //是否显示推荐功能（一键加速，超强省电，通知栏清理，微信专清，文件清理，手机降温）
        showTool();
    }

    private int mShowCount;

    /**
     * 是否显示推荐功能项
     */
    private void showTool() {

        if (!getString(R.string.tool_one_key_speed).contains(mTitle) && PreferenceUtil.getCleanTime()) {
            // 一键加速间隔时间至少3分钟  否则隐藏功能项
            mShowCount++;
            v_quicken.setVisibility(View.VISIBLE);
            line_quicken.setVisibility(View.VISIBLE);
        }
        if (!getString(R.string.tool_super_power_saving).contains(mTitle) && PreferenceUtil.getPowerCleanTime()) {
            // 超强省电间隔时间至少3分钟 否则隐藏
            mShowCount++;
            v_power.setVisibility(View.VISIBLE);
            line_power.setVisibility(View.VISIBLE);
        }

        if (!getString(R.string.tool_notification_clean).contains(mTitle) && PreferenceUtil.getNotificationCleanTime()) {
            // 通知栏清理间隔时间至少3分钟 否则隐藏
            mShowCount++;
            v_notification.setVisibility(View.VISIBLE);
            line_notification.setVisibility(View.VISIBLE);
        }

        if (PreferenceUtil.getWeChatCleanTime()) {
            if (!getString(R.string.tool_chat_clear).contains(mTitle) || !getString(R.string.tool_chat_clear_n).contains(mTitle)) {
                // 微信清理间隔时间至少3分钟 否则隐藏功能项
                if (mShowCount >= 3) return;
                mShowCount++;
                v_wechat.setVisibility(View.VISIBLE);
                line_wechat.setVisibility(View.VISIBLE);
            }
        }

        if (mShowCount >= 3) return;
        mShowCount++;
        v_file.setVisibility(View.VISIBLE);
        line_file.setVisibility(View.VISIBLE);

        if (!getString(R.string.tool_phone_temperature_low).contains(mTitle) && PreferenceUtil.getCoolingCleanTime()) {
            // 手机降温间隔时间至少3分钟 否则隐藏
            if (mShowCount >= 3) return;
            mShowCount++;
            v_cool.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        changeUI(intent);
    }

    @Override
    public void onClick(View v) {
        String functionName = "";
        String functionPosition = "";
        switch (v.getId()) {
            case R.id.iv_speed_clean:
                //一键加速
                functionName = "一键加速";
                functionPosition = "1";
                speedClean();
                break;
            case R.id.iv_power_clean:
                //超强省电
                functionName = "超强省电";
                functionPosition = "2";
                powerClean();
                break;
            case R.id.iv_notification_clean:
                //通知栏清理
                functionName = "通知栏清理";
                functionPosition = "3";
                notificationClean();
                break;
            case R.id.iv_wechat_clean:
                //微信专清
                functionName = "微信专清";
                functionPosition = "4";
                weChatClean();
                break;
            case R.id.iv_file_clean:
                //文件清理
                functionName = "文件清理";
                functionPosition = "5";
                fileClean();
                break;
            case R.id.iv_cooling:
                //手机降温
                functionName = "手机降温";
                functionPosition = "6";
                coolingClean();
                break;
        }
        //1.21 版本推荐清理_标识sourcePage,其他""
        String sourcePage = getString(R.string.tool_suggest_clean).contains(mTitle) ? "scanning_result_page" : "";
        StatisticsUtils.trackFunctionClickItem("recommendation_function_click", functionName, getIntent().hasExtra("home") ? "home_page" : sourcePage, "home_page_clean_up_page", functionName, functionPosition);
//        finish();
    }

    /**
     * 一键加速
     */
    public void speedClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("boost_click");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.ONKEY);
//        StatisticsUtils.trackClick("boost_click", "用户在首页点击【一键加速】按钮", "home_page", "home_page");
        Bundle bundle = new Bundle();
        bundle.putString(SpCacheConfig.ITEM_TITLE_NAME, getString(R.string.tool_one_key_speed));
        startActivity(PhoneAccessActivity.class, bundle);
    }

    /**
     * 超强省电
     */
    public void powerClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("powersave_click");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.SUPER_POWER_SAVING);
        startActivity(PhoneSuperPowerActivity.class);
//        StatisticsUtils.trackClick("powersave_click", "用户在首页点击【超强省电】按钮", "home_page", "home_page");
    }

    /**
     * 通知栏清理
     */
    public void notificationClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("notification_clean_click");
        //通知栏清理
        NotifyCleanManager.startNotificationCleanActivity(this, 0);
//        StatisticsUtils.trackClick("notification_clean_click", "用户在首页点击【通知清理】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");
    }

    /**
     * 微信专清
     */
    public void weChatClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("wxclean_click");
        AppHolder.getInstance().setOtherSourcePageId(SpCacheConfig.WETCHAT_CLEAN);

//        StatisticsUtils.trackClick("wxclean_click", "用户在首页点击【微信专清】按钮", "home_page", "home_page");
        if (!AndroidUtil.isAppInstalled(SpCacheConfig.CHAT_PACKAGE)) {
            ToastUtils.showShort(R.string.tool_no_install_chat);
            return;
        }
        if (PreferenceUtil.getWeChatCleanTime()) {
            // 每次清理间隔 至少3秒
            startActivity(WechatCleanHomeActivity.class);
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.tool_chat_clear));
            bundle.putString("num", "");
            bundle.putString("unit", "");
            startActivity(NewCleanFinishActivity.class, bundle);
        }
    }

    /**
     * 文件清理
     */
    public void fileClean() {
//        StatisticsUtils.trackClick("file_clean_click", "用户在首页点击【文件清理】按钮", "home_page", "home_page");
        startActivity(FileManagerHomeActivity.class);
    }

    /**
     * 手机降温
     */
    public void coolingClean() {
        AppHolder.getInstance().setCleanFinishSourcePageId("cooling_click");
        startActivity(RouteConstants.PHONE_COOLING_ACTIVITY);
//        StatisticsUtils.trackClick("cooling_click", "用户在首页点击【手机降温】按钮", AppHolder.getInstance().getSourcePageId(), "home_page");
    }

    protected void setListener() {
        mIvSpeedClean.setOnClickListener(this);
        mIvPowerClean.setOnClickListener(this);
        mIvNotificationClean.setOnClickListener(this);
        mIvWeChatClean.setOnClickListener(this);
        mIvFileClean.setOnClickListener(this);
        mIvCooling.setOnClickListener(this);

        mBtnLeft.setOnClickListener(v -> {
            if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
                StatisticsUtils.trackClick("return_back", "\"一键加速返回\"点击", AppHolder.getInstance().getSourcePageId(), "one_click_acceleration_clean_up_page");
            } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
                String sourcePage = getString(R.string.tool_suggest_clean).contains(mTitle) ? "scanning_result_page" : "";
                StatisticsUtils.trackClick("return_back_click", "用户在垃圾清理完成页点击【建议清理】返回", getIntent().hasExtra("home") ? "home_page" : sourcePage, "home_page_clean_up_page");
            }
            finish();
        });

        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mIsRefresh = true;
                startLoadData();
            }

            @Override
            public void onLoadMore() {
                mIsRefresh = false;
                startLoadData();
            }
        });
    }

    protected void loadData() {
        Intent intent = getIntent();
        changeUI(intent);

        mIsRefresh = false;
        startLoadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            StatisticsUtils.trackClick("return_back", "\"一键加速返回\"点击", "selected_page", "one_click_acceleration_clean_up_page");
        } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
            String sourcePage = getString(R.string.tool_suggest_clean).contains(mTitle) ? "scanning_result_page" : "";
            StatisticsUtils.trackClick("system_return_back_click", "用户在垃圾清理完成页点击【建议清理】返回", getIntent().hasExtra("home") ? "home_page" : sourcePage, "home_page_clean_up_page");
        }

        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        if (getString(R.string.app_name).contains(mTitle)) {
            //悟空清理
            NiuDataAPI.onPageStart("clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            //一键加速
            NiuDataAPI.onPageStart("boost_success_page_view_page", "加速结果出现时");
        } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
            //1.2.1清理完成页面
            NiuDataAPI.onPageStart("home_page_clean_up_page_view_page", "用户在垃圾清理完成页浏览");
        } else if (getString(R.string.tool_phone_clean).contains(mTitle)) {
            //手机清理
            NiuDataAPI.onPageStart("clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
            //超强省电
            NiuDataAPI.onPageStart("powersave_success_page_view_page", "省电结果出现时");
        } else if (getString(R.string.tool_chat_clear).contains(mTitle) || getString(R.string.tool_chat_clear_n).contains(mTitle)) {
            //微信专情
            NiuDataAPI.onPageStart("wxclean_success_page_view_page", "微信清理结果页出现时");
        } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
            //QQ专清
            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
            //通知栏清理
            NiuDataAPI.onPageStart("notification_clean_success_page_view_page", "通知栏清理结果页出现时");
        } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
            //手机降温
            NiuDataAPI.onPageStart("cooling_success_page_view_page", "降温结果页出现时");
        } else {
            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        }
        super.onResume();
        if (mNativeUnifiedADData != null) {
            // 必须要在Actiivty.onResume()时通知到广告数据，以便重置广告恢复状态
            mNativeUnifiedADData.resume();
        }
        if (mNativeUnifiedADData2 != null) {
            // 必须要在Actiivty.onResume()时通知到广告数据，以便重置广告恢复状态
            mNativeUnifiedADData2.resume();
        }
    }

    @Override
    protected void onPause() {
        Jzvd.releaseAllVideos();
        if (getString(R.string.app_name).contains(mTitle)) {
            //悟空清理
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "clean_success_page", "clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_one_key_speed).contains(mTitle)) {
            //一键加速
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "boost_success_page", "boost_success_page_view_page", "加速结果出现时");
        } else if (getString(R.string.tool_suggest_clean).contains(mTitle)) {
            //1.2.1清理完成页面
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "home_page_clean_up_page", "home_page_clean_up_page_view_page", "用户在垃圾清理完成页浏览");
        } else if (getString(R.string.tool_phone_clean).contains(mTitle)) {
            //手机清理
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "clean_success_page", "clean_success_page_view_page", "清理结果出现时");
        } else if (getString(R.string.tool_super_power_saving).contains(mTitle)) {
            //超强省电
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "powersave_success_page", "powersave_success_page_view_page", "省电结果出现时");
        } else if (getString(R.string.tool_chat_clear).contains(mTitle) || getString(R.string.tool_chat_clear_n).contains(mTitle)) {
            //微信专情
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "wxclean_success_page", "wxclean_success_page_view_page", "微信清理结果页出现时");
        } else if (getString(R.string.tool_qq_clear).contains(mTitle)) {
            //QQ专清
            NiuDataAPI.onPageStart("clean_up_page_view_immediately", "清理完成页浏览");
        } else if (getString(R.string.tool_notification_clean).contains(mTitle)) {
            //通知栏清理
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "notification_clean_success_page", "notification_clean_success_page_view_page", "通知栏清理结果页出现时");
        } else if (getString(R.string.tool_phone_temperature_low).contains(mTitle)) {
            //手机降温
            NiuDataAPIUtil.onPageEnd(AppHolder.getInstance().getCleanFinishSourcePageId(), "cooling_success_page", "cooling_success_page_view_page", "降温结果页出现时");
        } else {
            NiuDataAPI.onPageEnd("clean_up_page_view_immediately", "清理完成页浏览");
        }
        super.onPause();
    }

    /**
     * 获取播放器实例
     * <p>
     * 仅当视频回调{@link NativeExpressMediaListener#onVideoInit(NativeExpressADView)}调用后才会有返回值
     *
     * @param videoPlayer
     * @return
     */
    private String getVideoInfo(AdData.VideoPlayer videoPlayer) {
        if (videoPlayer != null) {
            StringBuilder videoBuilder = new StringBuilder();
            videoBuilder.append("{state:").append(videoPlayer.getVideoState()).append(",")
                    .append("duration:").append(videoPlayer.getDuration()).append(",")
                    .append("position:").append(videoPlayer.getCurrentPosition()).append("}");
            return videoBuilder.toString();
        }
        return null;
    }

    private NativeExpressMediaListener mediaListener = new NativeExpressMediaListener() {
        @Override
        public void onVideoInit(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoInit: " + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoLoading(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoLoading");
        }

        @Override
        public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
            Log.i(TAG, "onVideoReady");
        }

        @Override
        public void onVideoStart(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoStart: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoPause(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPause: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoComplete(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoComplete: "
                    + getVideoInfo(nativeExpressADView.getBoundData().getProperty(AdData.VideoPlayer.class)));
        }

        @Override
        public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
            Log.i(TAG, "onVideoError");
        }

        @Override
        public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPageOpen");
        }

        @Override
        public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
            Log.i(TAG, "onVideoPageClose");
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mNativeUnifiedADData != null) {
            // 必须要在Actiivty.destroy()时通知到广告数据，以便释放内存
            mNativeUnifiedADData.destroy();
        }
        if (mNativeUnifiedADData2 != null) {
            // 必须要在Actiivty.destroy()时通知到广告数据，以便释放内存
            mNativeUnifiedADData2.destroy();
        }

        if (mRecyclerView != null) {
            mRecyclerView.destroy(); // this will totally release XR's memory
            mRecyclerView = null;
        }
    }

    public void startLoadData() {
        if (!NetworkUtils.isNetConnected()) {
            if (mIsRefresh) {
                if (mRecyclerView != null)
                    mRecyclerView.refreshComplete();
            } else {
                if (mRecyclerView != null)
                    mRecyclerView.loadMoreComplete();
            }
            return;
        }

        if (mType != null) {
            if (mType == NewsType.VIDEO) {
                loadVideoData();
            } else {
                loadNewsData();
            }
        }
    }

    private void loadNewsData() {
        String type = mType.getValue();
        String lastId = SPUtil.getLastNewsID(mType.getName());
        if (mIsRefresh) {
            lastId = "";
        }
        String url = SpCacheConfig.NEWS_BASEURL + "&type=" + type + "&startkey=" + lastId + "&num=" + PAGE_NUM;
        EHttp.get(this, url, new ApiCallback<NewsListInfo>(null) {
            @Override
            public void onFailure(Throwable e) {
            }

            @Override
            public void onSuccess(NewsListInfo result) {
                if (result != null && result.data != null && result.data.size() > 0) {
                    SPUtil.setLastNewsID(mType.getName(), result.data.get(result.data.size() - 1).rowkey);
                    if (mIsRefresh) {
                        mNewsAdapter.setData(result.data);
                    } else {
                        mNewsAdapter.addData(result.data);
                    }
                }
            }

            @Override
            public void onComplete() {
                if (mIsRefresh) {
                    if (mRecyclerView != null)
                        mRecyclerView.refreshComplete();
                } else {
                    if (mRecyclerView != null)
                        mRecyclerView.loadMoreComplete();
                }
            }
        });
    }

    private void loadVideoData() {
        //请求参数设置：比如一个json字符串
        JSONObject jsonObject = new JSONObject();
        try {
            String lastId = SPUtil.getLastNewsID(mType.getName());
            jsonObject.put("pageSize", PAGE_NUM);
            jsonObject.put("lastId", lastId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpRequest request = new HttpRequest.Builder()
                .addBodyParams(jsonObject.toString())
                .build();

        EHttp.post(this, BuildConfig.VIDEO_BASE_URL, request, new ApiCallback<ArrayList<VideoItemInfo>>() {

            @Override
            public void onComplete() {
                if (mIsRefresh) {
                    if (mRecyclerView != null)
                        mRecyclerView.refreshComplete();
                } else {
                    if (mRecyclerView != null)
                        mRecyclerView.loadMoreComplete();
                }
            }

            @Override
            public void onFailure(Throwable e) {
                Log.i("123", e.toString());
            }

            @Override
            public void onSuccess(ArrayList<VideoItemInfo> result) {
                if (result != null && result.size() > 0) {
                    SPUtil.setLastNewsID(mType.getName(), result.get(result.size() - 1).videoId);
                    if (mIsRefresh) {
                        mNewsAdapter.setData(result);
                    } else {
                        mNewsAdapter.addData(result);
                    }
                }
            }
        });
    }

    private H mHandler = new H();
    private static final int AD_COUNT = 1;
    private static final int MSG_INIT_AD = 0;
    private static final int MSG_VIDEO_START = 1;
    private static final int MSG_INIT_AD2 = 3;
    private static final int MSG_VIDEO_START2 = 4;

    @Override
    public void netError() {

    }

    private class H extends Handler {
        public H() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_AD:
                    NativeUnifiedADData ad = (NativeUnifiedADData) msg.obj;
                    Log.d(TAG, String.format(Locale.getDefault(), "(pic_width,pic_height) = (%d , %d)", ad
                                    .getPictureWidth(),
                            ad.getPictureHeight()));
                    initAd(ad);
                    Log.d(TAG, "eCPM = " + mNativeUnifiedADData.getECPM() + " , eCPMLevel = " + mNativeUnifiedADData.getECPMLevel());
                    break;
                case MSG_VIDEO_START:
                    Log.d("AD_DEMO", "handleMessage");
                    iv_advert.setVisibility(View.GONE);
                    mMediaView.setVisibility(View.VISIBLE);
                    break;
                case MSG_INIT_AD2:
                    NativeUnifiedADData ad2 = (NativeUnifiedADData) msg.obj;
                    Log.d(TAG, String.format(Locale.getDefault(), "(pic_width,pic_height) = (%d , %d)", ad2
                                    .getPictureWidth(),
                            ad2.getPictureHeight()));
                    initAd2(ad2);
                    Log.d(TAG, "eCPM = " + mNativeUnifiedADData2.getECPM() + " , eCPMLevel = " + mNativeUnifiedADData2.getECPMLevel());
                    break;
                case MSG_VIDEO_START2:
                    Log.d("AD_DEMO", "handleMessage2222222");
                    iv_advert2.setVisibility(View.GONE);
                    mMediaView2.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    //加载广告
    private void initAd(final NativeUnifiedADData ad) {
        renderAdUi(ad);

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(v_advert);
        // 将布局与广告进行绑定
        ad.bindAdToView(this, mContainer, null, clickableViews);
        // 设置广告事件监听
        ad.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                Log.d(TAG, "广告曝光");
            }

            @Override
            public void onADClicked() {
                Log.d(TAG, "onADClicked: " + " clickUrl: " + ad.ext.get("clickUrl"));
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", PositionId.CLEAN_FINISH_ID, "优量汇", "success_page", "success_page");
            }

            @Override
            public void onADError(AdError error) {
                Log.d(TAG, "错误回调 error code :" + error.getErrorCode()
                        + "  error msg: " + error.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                Log.d(TAG, "广告状态变化");
//                updateAdAction(mDownloadButton, ad);
            }
        });

        if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) { //视频类型
            mHandler.sendEmptyMessage(MSG_VIDEO_START);

            VideoOption.Builder builder = new VideoOption.Builder();
            builder.setAutoPlayMuted(false) //设置视频广告在预览页自动播放时是否静音
                    .setEnableDetailPage(false) //点击视频是否跳转到详情页
                    .setEnableUserControl(true) //设置是否允许用户在预览页点击视频播放器区域控制视频的暂停或播放
                    .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI);
            VideoOption videoOption = builder.build();
            // 视频广告需对MediaView进行绑定，MediaView必须为容器mContainer的子View
            ad.bindMediaView(mMediaView, videoOption,
                    // 视频相关回调
                    new NativeADMediaListener() {
                        @Override
                        public void onVideoInit() {
                            Log.d(TAG, "onVideoInit: ");
                        }

                        @Override
                        public void onVideoLoading() {
                            Log.d(TAG, "onVideoLoading: ");
                        }

                        @Override
                        public void onVideoReady() {
                            Log.d(TAG, "onVideoReady: ");
                        }

                        @Override
                        public void onVideoLoaded(int videoDuration) {
                            Log.d(TAG, "onVideoLoaded: ");

                        }

                        @Override
                        public void onVideoStart() {
                            Log.d(TAG, "onVideoStart: ");
                        }

                        @Override
                        public void onVideoPause() {
                            Log.d(TAG, "onVideoPause: ");
                        }

                        @Override
                        public void onVideoResume() {
                            Log.d(TAG, "onVideoResume: ");
                        }

                        @Override
                        public void onVideoCompleted() {
                            Log.d(TAG, "onVideoCompleted: ");
                        }

                        @Override
                        public void onVideoError(AdError error) {
                            Log.d(TAG, "onVideoError: ");
                        }

                        @Override
                        public void onVideoStop() {

                        }

                        @Override
                        public void onVideoClicked() {

                        }
                    });
        }
    }

    //加载广告2
    private void initAd2(final NativeUnifiedADData ad) {
        renderAdUi2(ad);

        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(v_advert2);
        // 将布局与广告进行绑定
        ad.bindAdToView(this, mContainer2, null, clickableViews);
        // 设置广告事件监听
        ad.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                Log.d(TAG, "广告曝光");
            }

            @Override
            public void onADClicked() {
                Log.d(TAG, "onADClicked: " + " clickUrl: " + ad.ext.get("clickUrl"));
                StatisticsUtils.clickAD("ad_click", "广告点击", "1", PositionId.CLEAN_FINISH_ID, "优量汇", "success_page", "success_page");
            }

            @Override
            public void onADError(AdError error) {
                Log.d(TAG, "错误回调 error code :" + error.getErrorCode()
                        + "  error msg: " + error.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                Log.d(TAG, "广告状态变化");
                updateAdAction(tv_download2, ad);
            }
        });
        updateAdAction(tv_download2, ad);

        if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) { //视频类型
            mHandler.sendEmptyMessage(MSG_VIDEO_START2);

            VideoOption.Builder builder = new VideoOption.Builder();
            builder.setAutoPlayMuted(false) //设置视频广告在预览页自动播放时是否静音
                    .setEnableDetailPage(false) //点击视频是否跳转到详情页
                    .setEnableUserControl(true) //设置是否允许用户在预览页点击视频播放器区域控制视频的暂停或播放
                    .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.NEVER); //不自动播放
            VideoOption videoOption = builder.build();
            // 视频广告需对MediaView进行绑定，MediaView必须为容器mContainer的子View
            ad.bindMediaView(mMediaView2, videoOption,
                    // 视频相关回调
                    new NativeADMediaListener() {
                        @Override
                        public void onVideoInit() {
                            Log.d(TAG, "onVideoInit: ");
                        }

                        @Override
                        public void onVideoLoading() {
                            Log.d(TAG, "onVideoLoading: ");
                        }

                        @Override
                        public void onVideoReady() {
                            Log.d(TAG, "onVideoReady: ");
                        }

                        @Override
                        public void onVideoLoaded(int videoDuration) {
                            Log.d(TAG, "onVideoLoaded: ");

                        }

                        @Override
                        public void onVideoStart() {
                            Log.d(TAG, "onVideoStart: ");
                        }

                        @Override
                        public void onVideoPause() {
                            Log.d(TAG, "onVideoPause: ");
                        }

                        @Override
                        public void onVideoResume() {
                            Log.d(TAG, "onVideoResume: ");
                        }

                        @Override
                        public void onVideoCompleted() {
                            Log.d(TAG, "onVideoCompleted: ");
                        }

                        @Override
                        public void onVideoError(AdError error) {
                            Log.d(TAG, "onVideoError: ");
                        }

                        @Override
                        public void onVideoStop() {

                        }

                        @Override
                        public void onVideoClicked() {

                        }
                    });
        }
    }

    public static void updateAdAction(TextView button, NativeUnifiedADData ad) {
        if (!ad.isAppAd()) {
            button.setText("浏览");
            return;
        }
        switch (ad.getAppStatus()) {
            case 0:
                button.setText("立即下载");
                break;
            case 1:
                button.setText("立即启动");
                break;
            case 2:
                button.setText("立即更新");
                break;
            case 4:
                button.setText(ad.getProgress() + "%");
                break;
            case 8:
                button.setText("立即安装");
                break;
            case 16:
                button.setText("下载失败");
                break;
            default:
                button.setText("浏览");
                break;
        }
    }

    // 获取广告资源并加载到UI
    private void renderAdUi(NativeUnifiedADData ad) {
        int patternType = ad.getAdPatternType();
        Log.d("AD_DEMO", "广告类型=" + patternType);

        tv_advert.setText(ad.getTitle());
        tv_advert_content.setText(ad.getDesc());
        Glide.with(this).load(ad.getIconUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(iv_advert_logo);
        if (patternType == AdPatternType.NATIVE_VIDEO) {
            iv_advert.setVisibility(View.GONE);
        } else {
            Glide.with(this).load(ad.getImgUrl()).into(iv_advert);
        }
    }

    // 获取广告资源并加载到UI
    private void renderAdUi2(NativeUnifiedADData ad) {
        int patternType = ad.getAdPatternType();
        Log.d("AD_DEMO", "广告类型222=" + patternType);

        tv_advert2.setText(ad.getTitle());
        tv_advert_content2.setText(ad.getDesc());
        Glide.with(this).load(ad.getIconUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(20)))
                .into(iv_advert_logo2);
        if (patternType == AdPatternType.NATIVE_VIDEO) {
            iv_advert2.setVisibility(View.GONE);
        } else {
            Glide.with(this).load(ad.getImgUrl()).into(iv_advert2);
        }
    }
}
