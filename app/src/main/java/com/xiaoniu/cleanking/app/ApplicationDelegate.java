package com.xiaoniu.cleanking.app;

import android.app.Application;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.geek.push.GeekPush;
import com.geek.push.core.PushConstants;
import com.tencent.bugly.Bugly;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xiaoniu.cleanking.AppConstants;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.AppComponent;
import com.xiaoniu.cleanking.app.injector.component.DaggerAppComponent;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.app.injector.module.AppModule;
import com.xiaoniu.cleanking.jpush.JPushNotificationManager;
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager;
import com.xiaoniu.cleanking.utils.NotificationUtils;
import com.xiaoniu.common.base.IApplicationDelegate;
import com.xiaoniu.common.utils.ChannelUtil;
import com.xiaoniu.statistic.Configuration;
import com.xiaoniu.statistic.HeartbeatCallBack;
import com.xiaoniu.statistic.NiuDataAPI;

import org.json.JSONObject;

/**
 * Created by admin on 2017/6/8.
 */

public class ApplicationDelegate implements IApplicationDelegate {

    private static final String TAG = "Tinker.ApplicationDelegate";

    @Override
    public void onCreate(Application application) {
        initNiuData(application);
        PlatformConfig.setWeixin("wx19414dec77020d03", "090f560fa82e0dfff2f0cb17e43747c2");
        PlatformConfig.setQQZone("1109516379", "SJUCaQdURyRd8Dfi");
        PlatformConfig.setSinaWeibo("1456333364", "bee74e1ccd541f657875803a7eb32b1b", "http://xiaoniu.com");
        Bugly.init(application, "bdd6fe23ab", false);

        UMShareAPI.get(application);

        initInjector(application);

        //初始化sdk
        initGeekPush(application);
        if (BuildConfig.DEBUG) {
            UMConfigure.setLogEnabled(true);
            ARouter.openLog();     // Print log
            ARouter.openDebug();   // Turn on debugging mode (If you are running in InstantRun mode, you must turn on debug mode! Online version needs to be closed, otherwise there is a security risk)
        }
        ARouter.init(application);
        UMConfigure.init(application, "5d230f2f4ca357bdb700106d", ChannelUtil.getChannel(), UMConfigure.DEVICE_TYPE_PHONE, "");
        NotificationUtils.createNotificationChannel();
        NotifyCleanManager.getInstance().sendRebindServiceMsg();
    }

    private static AppComponent mAppComponent;

    private void initInjector(Application application) {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .apiModule(new ApiModule(application))
                .build();
        mAppComponent.inject(application);
    }

    private void initGeekPush(Application application) {
        GeekPush.setDebug(false);

        GeekPush.init(application, ((platformCode, platformName) -> {
            boolean result = (platformCode == PushConstants.PLATFORMCODE_JPUSH);
            return result;
        }));
        GeekPush.register();
        JPushNotificationManager.customPushNotification(application,1, R.layout.layout_notivition,R.id.image,R.id.title,R.id.text, R.mipmap.applogo, R.mipmap.applogo);
    }

    public void initNiuData(Application application) {
        //测试环境
        NiuDataAPI.init(application, new Configuration()
                //切换到sdk默认的测试环境地址
                .setHeartbeatMode(Configuration.HEARTBEAT_MODE_FOREGROUND)
                .serverUrl(AppConstants.BIGDATA_MD)
                .setHeartbeatUrl(AppConstants.BIGDATA_MD)
                //打开sdk日志信息
                .logOpen()
                .setHeartbeatInterval(5000)
                .channel(ChannelUtil.getChannel())
        );

        NiuDataAPI.setHeartbeatCallback(new HeartbeatCallBack() {
            @Override
            public void onHeartbeatStart(JSONObject eventProperties) {
                //这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段
                Log.d("onHeartbeatStart", "onHeartbeatStart: " + "这里可以给心跳事件 追加额外字段  在每次心跳启动的时候，会带上额外字段");
            }
        });
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int level) {

    }

}
