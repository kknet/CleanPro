package com.xiaoniu.cleanking.midas;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.constant.Constant;
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.common.utils.DisplayUtils;
import com.xiaoniu.statistic.HeartbeatCallBack;
import com.xiaoniu.statistic.NiuDataAPI;
import com.xiaoniu.unitionadbase.abs.AbsAdBusinessCallback;
import com.xiaoniu.unitionadbase.config.AdConfig;
import com.xiaoniu.unitionadbase.config.AdParameter;
import com.xiaoniu.unitionadbase.config.WebViewConfig;
import com.xiaoniu.unitionadbase.impl.IUnitaryListener;
import com.xiaoniu.unitionadproxy.MidasAdSdk;

import org.json.JSONObject;

/**
 * @author zhengzhihao
 * @date 2020/7/3 13
 * @mail：zhengzhihao@hellogeek.com
 */
public class MidasRequesCenter {

    /**
     * 初始化广告SDK
     */
    public static void init(Application application) {

        WebViewConfig webViewConfig = new WebViewConfig();
        webViewConfig.statusBarColor = "#06C581";//webView状态栏颜色
        webViewConfig.titleBarColor = "#06C581";//webView标题栏背景颜色
        webViewConfig.isStatusBarDarkFont = false;//webView栏状态字体颜色是否黑色
        webViewConfig.backImageViewRes = R.mipmap.icon_left_arrow_white;//webView标题栏返回图标
        webViewConfig.titleTextViewColor = "#FFFFFFFF";//webView标题栏字体颜色
        AdConfig configBuild = new AdConfig.Build()
                .setAppId(MidasConstants.APP_ID)//应用ID
                .setProductId(MidasConstants.PRODUCT_ID)//大数据业务线ID
                .setChannel(ChannelUtil.getChannel())//渠道名
                .setServerUrl(BuildConfig.BIGDATA_MD)//业务埋点地址
                .setBusinessUrl(BuildConfig.MIDAS_NIU_DATA_SERVER_URL)//商业变现埋点地址
                .setIsFormal(true)//是否是正式环境 true 线上环境
//                .setDrawFeedAdPositions("adpos_7538614881", "adpos_8941326481")//draw信息流广告位id
//                .setDrawFeedWidthPx(DisplayUtils.dp2px(this, 300))//设置draw信息流宽度  单位像素
//                .setDrawFeedHeightPx(DisplayUtils.dp2px(this, 500))//设置draw信息流高度  单位像素
                .setSplashBottomHeightDp(100)//设置启动页半屏底部高度 单位dp
//        .setDisableAllianceInitVariableArray(UnionConstants.AD_SOURCE_FROM_CSJ,UnionConstants.AD_SOURCE_FROM_YLH)//设置禁用联盟初始化可变数组
                .setWebViewConfig(webViewConfig)//设置webview配置
                .setHotFlashIntervalTime(AppHolder.getInstance().getHotTime())//设置热起时间间隔
//                .setShowLogWindow(true)//设置是否显示日志窗口
                .build();
        //初始化广告SDK
        MidasAdSdk.init(application, configBuild);

        //注册单一元监听
        MidasAdSdk.registerUnitaryListener(new IUnitaryListener() {
            @Override
            public void onConfirmExit() {
//                Toast.makeText(getApplicationContext(),"确认退出",Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onContinueBrowsing() {
//                Toast.makeText(getApplicationContext(),"继续浏览",Toast.LENGTH_SHORT).show();
            }
        });

        NiuDataAPI.setHeartbeatCallback(new HeartbeatCallBack() {
            @Override
            public void onHeartbeatStart(JSONObject eventProperties) {
                //这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段
                Log.d("onHeartbeatStart", "onHeartbeatStart: " + "这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段");
            }
        });

    }

    /**
     * 请求并显示广告
     * @param activity  当前页面
     * @param adPositionId  广告位ID
     * @param absAdBusinessCallback 业务回调 [成功、失败、曝光、展示、激励等等]
     */
    public static void requestAndShowAd(Activity activity, String adPositionId, AbsAdBusinessCallback absAdBusinessCallback){
        if (absAdBusinessCallback == null){
            absAdBusinessCallback = new AbsAdBusinessCallback() {};
        }
        if (activity == null){
            return;
        }
        AdParameter adParameter = new AdParameter.
                Builder(activity, adPositionId)
                .build();
        MidasAdSdk.loadAd(adParameter, absAdBusinessCallback);
    }

    /**
     * 预加载广告
     * @param activity  当前页面
     * @param adPositionId  广告位ID
     */
    public static void preloadAd(Activity activity,String adPositionId){
        if (activity == null){
            return;
        }
        AdParameter adParameter = new AdParameter.
                Builder(activity, adPositionId)
                .build();
        //预加载广告
        MidasAdSdk.preLoad(adParameter);
    }


}
