package com.hellogeek.permission.manufacturer.oppo.colors.permissionlist;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.manufacturer.ManfacturerBase;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.util.AccessibilitUtil;
import com.hellogeek.permission.util.AppUtils;
import com.hellogeek.permission.util.NodeInfoUtil;

import org.greenrobot.eventbus.EventBus;

import static com.hellogeek.permission.util.Constant.PROVIDER_SUSPENDEDTOAST;

/**
 * 悬浮窗-oppo
 */
public class SuspendedToastPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public SuspendedToastPermission(Context context) {
        super(context);
        mContext = context;
    }

    public void openSuspendedToast(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SUSPENDEDTOAST) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,0));
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);
                back(service);
            }
            return;
        }else if (mIsOpen||result){
            return;
        }else {
            if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗管理") && !getList().contains(SIGN1)) {
                NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "悬浮窗管理");
                addSIGN(SIGN1);
            } else if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗管理") && NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext)) && getList().contains(SIGN1) && !getList().contains(SIGN2)) {
                mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, AppUtils.getAppName(mContext));
                addSIGN(SIGN1);
                if (mIsOpen) {
                    PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);
                    EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true,1));
                    back(service);
                }
            } else  if (NodeInfoUtil.pageContains(nodeInfo, "悬浮窗管理")){
                NodeInfoUtil.scrollableList(nodeInfo);
            }

        }
    }

    public void openSuspendedToast1(Context mContext, AccessibilityNodeInfo nodeInfo, AccessibilityService service) {
        if (nodeInfo == null) return;
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.SUSPENDEDTOAST) && !mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true));
                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        }
        if (NodeInfoUtil.pageContains(nodeInfo, "应用信息") && NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext)) && !getList().contains(SIGN1)) {
            mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, "允许显示悬浮窗");
            addSIGN(SIGN1);

            if (mIsOpen) {
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true));
                back(service);
            }
        } else if (NodeInfoUtil.pageContains(nodeInfo, AppUtils.getAppName(mContext)) && !getList().contains(SIGN1)) {
            mIsOpen = NodeInfoUtil.clickNodeInfoAll(mContext, nodeInfo, AppUtils.getAppName(mContext));
            addSIGN(SIGN1);
            if (mIsOpen) {
                PermissionProvider.save(mContext, PROVIDER_SUSPENDEDTOAST, true);
                EventBus.getDefault().post(new PathEvent(Permission.SUSPENDEDTOAST, true));
                back(service);
            }
        } else {
            NodeInfoUtil.scrollableList(nodeInfo);
        }
    }

}