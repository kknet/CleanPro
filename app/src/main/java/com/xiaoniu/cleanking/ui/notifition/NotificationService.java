package com.xiaoniu.cleanking.ui.notifition;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.ui.main.activity.CleanBigFileActivity;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneAccessActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneCoolingActivity;
import com.xiaoniu.cleanking.ui.main.activity.PhoneSuperPowerActivity;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.tool.notify.activity.NotifyCleanGuideActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * 常住通知栏服务
 */
public class NotificationService extends Service {

    private final int NOTIFICATION_ID = 0xa01;
    private final int REQUEST_CODE = 0xb01;
    private Context context;
    private NotificationManager notificationManager;
    private RemoteViews contentView;
    private Notification notification;
    private Notification.Builder mBuilder = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        context = this;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new Notification.Builder(context);
        // 此处设置的图标仅用于显示新提醒时候出现在设备的通知栏
        mBuilder.setSmallIcon(R.mipmap.applogo);
        // 当用户下来通知栏时候看到的就是RemoteViews中自定义的Notification布局
        contentView = new RemoteViews(context.getPackageName(), R.layout.notification_bar_layout);

        //如果版本号低于（3.0），那么不显示按钮
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            //TODO

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // android 8.0 适配    需要配置 通知渠道NotificationChannel
            NotificationChannel notificationChannel = new NotificationChannel("1", "推送通知", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setLightColor(context.getColor(R.color.color_D8D8D8));//指定闪光的灯光颜色
            notificationChannel.enableLights(true);//闪光
            notificationChannel.enableVibration(true);//震动
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);//锁屏显示通知
            notificationManager.createNotificationChannel(notificationChannel);
            mBuilder.setChannelId("1");
        }
        contentView.setOnClickPendingIntent(R.id.iv_app_icon, PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        contentView.setOnClickPendingIntent(R.id.ll_clean, PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, CleanBigFileActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        contentView.setOnClickPendingIntent(R.id.ll_speed, PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, PhoneAccessActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        contentView.setOnClickPendingIntent(R.id.ll_temperature, PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, PhoneCoolingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        contentView.setOnClickPendingIntent(R.id.ll_power, PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, PhoneSuperPowerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        contentView.setOnClickPendingIntent(R.id.ll_notification, PendingIntent.getActivity(context, REQUEST_CODE, new Intent(context, NotifyCleanGuideActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        mBuilder.setContent(contentView);
        mBuilder.setSmallIcon(R.mipmap.applogo);
        mBuilder.setOngoing(true);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.applogo));
        mBuilder.setAutoCancel(true);
        notification = mBuilder.build();
        // 发送到手机的通知栏
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Subscribe
    public void cleanFinish(NotificationEvent notificationEvent) {
        Log.d("cleanFinish", "cleanFinish: " + notificationEvent);
        if ("clean".equals(notificationEvent.getType())) {
            if (notificationEvent.getFlag() == 0) {
                contentView.setImageViewResource(R.id.iv_clean, R.mipmap.icon_notifi_clean_nor);
            } else if (notificationEvent.getFlag() == 2) {
                contentView.setImageViewResource(R.id.iv_clean, R.mipmap.icon_notifi_clean_pre2);
            } else if (notificationEvent.getFlag() == 3) {
                contentView.setImageViewResource(R.id.iv_clean, R.mipmap.icon_notifi_clean_pre3);
            }
        } else if ("cooling".equals(notificationEvent.getType())) {
            if (notificationEvent.getFlag() == 0) {
                contentView.setImageViewResource(R.id.iv_temperature, R.mipmap.icon_notifi_temperature_nor);
            } else if (notificationEvent.getFlag() == 2) {
                contentView.setImageViewResource(R.id.iv_temperature, R.mipmap.icon_notifi_temperature_pre2);
            } else if (notificationEvent.getFlag() == 3) {
                contentView.setImageViewResource(R.id.iv_temperature, R.mipmap.icon_notifi_temperature_pre3);
            }
        } else if ("speed".equals(notificationEvent.getType())) {
            if (notificationEvent.getFlag() == 0) {
                contentView.setImageViewResource(R.id.iv_speed, R.mipmap.icon_notifi_one_key_no);
            } else if (notificationEvent.getFlag() == 2) {
                contentView.setImageViewResource(R.id.iv_speed, R.mipmap.icon_notifi_one_key_pre2);
            } else if (notificationEvent.getFlag() == 3) {
                contentView.setImageViewResource(R.id.iv_speed, R.mipmap.icon_notifi_one_key_pre3);
            }
        } else if ("power".equals(notificationEvent.getType())) {
            if (notificationEvent.getFlag() == 0) {
                contentView.setImageViewResource(R.id.iv_power, R.mipmap.icon_notifi_power_no);
            } else if (notificationEvent.getFlag() == 2) {
                contentView.setImageViewResource(R.id.iv_power, R.mipmap.icon_notifi_power_pre2);
            } else if (notificationEvent.getFlag() == 3) {
                contentView.setImageViewResource(R.id.iv_power, R.mipmap.icon_notifi_power_pre3);
            }
        } else if ("notification".equals(notificationEvent.getType())) {
            if (notificationEvent.getFlag() == 0) {
                contentView.setImageViewResource(R.id.iv_notification, R.mipmap.icon_notifi_notification_nor);
            } else if (notificationEvent.getFlag() == 2) {
                contentView.setImageViewResource(R.id.iv_notification, R.mipmap.icon_notifi_notification_pre2);
            } else if (notificationEvent.getFlag() == 3) {
                contentView.setImageViewResource(R.id.iv_notification, R.mipmap.icon_notifi_notification_pre3);
            }
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
