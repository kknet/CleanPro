package com.xiaoniu.cleanking.ui.tool.notify.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.ui.main.event.NotificationEvent;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.tool.notify.utils.NotifyUtils;
import com.xiaoniu.common.base.BaseActivity;
import com.xiaoniu.common.utils.StatisticsUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 通知栏清理
 */
public class NotifyCleanGuideActivity extends BaseActivity {
    private TextView mTvClean;
    private boolean mRequestPermission;

    public static void startNotificationGuideActivity(Context context) {
        if (context != null) {
            if (!NotifyUtils.isNotificationListenerEnabled() || !SPUtil.isCleanNotificationEnable()) {
                Intent intent = new Intent(context, NotifyCleanGuideActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                NotifyCleanDetailActivity.startNotificationCleanActivity(context);
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_notifaction_clean_guide;
    }

    @Override
    protected void initVariable(Intent intent) {
        setLeftTitle("通知栏清理");
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

        Intent intent = getIntent();
        if (intent != null){
            String notification = intent.getStringExtra("NotificationService");
            if ("clean".equals(notification)){
                AppHolder.getInstance().setCleanFinishSourcePageId("toggle_noti_clean_click");
                StatisticsUtils.trackClick("toggle_noti_clean_click", "常驻通知栏点击通知清理", "", "toggle_page");
            }
        }

        mTvClean = findViewById(R.id.tv_clean);
    }

    @Override
    protected void setListener() {
        mTvClean.setOnClickListener(v -> {
            if (NotifyUtils.isNotificationListenerEnabled()) {
                SPUtil.setCleanNotificationEnable(true);
                NotifyCleanDetailActivity.startNotificationCleanActivity(NotifyCleanGuideActivity.this);
                finish();
            } else {
                mRequestPermission = true;
                NotifyUtils.openNotificationListenerSettings(NotifyCleanGuideActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationEvent event = new NotificationEvent();
        event.setType("notification");
        EventBus.getDefault().post(event);

        if (mRequestPermission) {
            mRequestPermission = false;
            if (NotifyUtils.isNotificationListenerEnabled()) {
                SPUtil.setCleanNotificationEnable(true);
                NotifyCleanDetailActivity.startNotificationCleanActivity(this);
                finish();
            }
        }
    }

    @Override
    protected void loadData() {

    }

}
