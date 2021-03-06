package com.xiaoniu.common.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Method;

public class SystemUtils {
    /**
     * 回到home，后台运行
     */
    public static void goHome(Context context) {
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(mHomeIntent);
    }

    /**
     * 得到现在运行activityd 类名
     *
     * @param context
     * @return
     */
    public static String getCurrentTopActivity(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            return cn.getPackageName() + cn.getShortClassName();
        } catch (Exception e) {
            return "";
        }
    }

    //关闭statusbar，自定义notification按钮时，很多机器在点击按钮时statusbar不会关闭，需强制关闭
    @SuppressLint("WrongConstant")
    public static void collapseStatusBar(Context context) {
        if (context == null) {
            return;
        }
        Object sbservice = context.getSystemService("statusbar");
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        try {
            Class<?> statusBarManager = Class.forName("android.app.StatusBarManager");
            Method collapse;
            if (currentApiVersion <= 16) {
                collapse = statusBarManager.getMethod("collapse");
            } else {
                collapse = statusBarManager.getMethod("collapsePanels");
            }
            collapse.setAccessible(true);
            collapse.invoke(sbservice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
