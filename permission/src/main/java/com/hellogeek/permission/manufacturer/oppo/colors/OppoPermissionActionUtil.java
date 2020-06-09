package com.hellogeek.permission.manufacturer.oppo.colors;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;


import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.oppo.OppoPermissionBase;
import com.hellogeek.permission.manufacturer.oppo.colors.permissionlist.*;


public class OppoPermissionActionUtil {
    private Context mContext;
    private SuspendedToastPermission suspendedToastPermission;
    private SelfStartingPermission selfStartingPermission;
    private SystemSettingPermission systemSettingPermission;
    private NoticeOfTakeoverPermission noticeOfTakeoverPermission;
    private NotifiCationBarPermission notifiCationBarPermission;

    public OppoPermissionActionUtil(Context mContext) {
        this.mContext = mContext;
        suspendedToastPermission = new SuspendedToastPermission(mContext);
        selfStartingPermission = new SelfStartingPermission(mContext);
        systemSettingPermission = new SystemSettingPermission(mContext);
        noticeOfTakeoverPermission = new NoticeOfTakeoverPermission(mContext);
        notifiCationBarPermission = new NotifiCationBarPermission(mContext);
    }

    /**
     * 悬浮窗权限
     */
    public void actionSuspendedToast(AccessibilityNodeInfo info, AccessibilityService service, OppoPermissionBase.VERSION mVersion) {
        if (mVersion == OppoPermissionBase.VERSION.V2 || mVersion == OppoPermissionBase.VERSION.V3 || mVersion == OppoPermissionBase.VERSION.V5) {
            suspendedToastPermission.openSuspendedToast1(mContext, info, service);
        } else {
            suspendedToastPermission.openSuspendedToast(mContext, info, service);
        }
    }

    /**
     * 锁屏权限
     */
    public void actionLockDisplay(AccessibilityNodeInfo info, AccessibilityService service) {
    }


    /**
     * 后台弹出
     */
    public void actionBackstatePopUp(AccessibilityNodeInfo info, AccessibilityService service) {
    }


    /**
     * 系统设置
     */
    public void actionSystemSetting(AccessibilityNodeInfo info, AccessibilityService service) {
        systemSettingPermission.openSystemSetting(mContext, info, service);
    }

    /**
     * 接管通知
     */
    public void actionNoticeOfTakeover(AccessibilityNodeInfo info, AccessibilityService service, OppoPermissionBase.VERSION mVersion) {
        if (mVersion == OppoPermissionBase.VERSION.V7_1){
            noticeOfTakeoverPermission.openNoticeOfTakeoverV71(mContext, info, service);
        }else{
            noticeOfTakeoverPermission.openNoticeOfTakeover(mContext, info, service);
        }

    }

    /**
     * 通知栏
     */
    public void actionNotifiCationBar(AccessibilityNodeInfo info, AccessibilityService service) {
        notifiCationBarPermission.openNotifiCationBar(mContext,info,service);
    }


    /**
     * 自启动权限
     */
    public void actionSelfStaring(AccessibilityNodeInfo info, AccessibilityService service, OppoPermissionBase.VERSION mVersion) {
        if (mVersion == OppoPermissionBase.VERSION.V2 || mVersion == OppoPermissionBase.VERSION.V3 || mVersion == OppoPermissionBase.VERSION.V5) {
            selfStartingPermission.openSelfStartingV2V3V4(mContext, info, service);
        } else {
            selfStartingPermission.openSelfStarting(mContext, info, service);
    }
    }


    public void clearList(Permission permission) {
        switch (permission) {
            case SUSPENDEDTOAST:
                suspendedToastPermission.clearList();
                break;
            case SELFSTARTING:
                selfStartingPermission.clearList();
                break;
            case LOCKDISPALY:
                break;
            case BACKSTAGEPOPUP:
                break;
            case SYSTEMSETTING:
                systemSettingPermission.clearList();

                break;
            case REPLACEACLLPAGE:
                break;
            case NOTIFICATIONBAR:
                notifiCationBarPermission.clearList();
                break;
            case NOTICEOFTAKEOVER:
                noticeOfTakeoverPermission.clearList();
                break;


        }
    }


}
