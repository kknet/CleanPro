package com.hellogeek.permission.manufacturer.vivo.permissionlist;

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

import static com.hellogeek.permission.util.Constant.PROVIDER_NOTICEOFTAKEOVER;

/**
 * 来电通知-vivo
 * */
public class NoticeOfTakeoverPermission extends ManfacturerBase {

    private Context mContext;
    private boolean result;
    private boolean mIsOpen;

    public NoticeOfTakeoverPermission(Context context) {
        super(context);
        this.mContext = context;
    }

    public void openNoticeOfTakeover(Context mContext, AccessibilityNodeInfo info, AccessibilityService service) {
        if (AccessibilitUtil.isOpenPermission(mContext, Permission.NOTICEOFTAKEOVER)&&!mIsOpen) {
            if (!result) {
                result = true;
                EventBus.getDefault().post(new PathEvent(Permission.NOTICEOFTAKEOVER, true,0));
                PermissionProvider.save(mContext, PROVIDER_NOTICEOFTAKEOVER, true);

                back(service);
            }
            return;
        } else if (mIsOpen||result){
            return;
        } else if (NodeInfoUtil.pageContains(info, "设置") && !getList().contains(SIGN1)) {
            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext,info, AppUtils.getAppName(mContext));
                    addSIGN(SIGN1);
            } else if (!getList().contains(SIGN1)) {
                NodeInfoUtil.scrollableList(info);
            }

        } else if ((NodeInfoUtil.pageContains(info, AppUtils.getStringWithAppName(mContext, "允许%s获取通知访问权限吗"))||NodeInfoUtil.pageContains(info, "要启用")||NodeInfoUtil.pageContains(info, AppUtils.getStringWithAppName(mContext, "允许%s读取通知吗"))) && !getList().contains(SIGN1)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext,info, "确定");
            addSIGN(SIGN1);
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_NOTICEOFTAKEOVER, true);
                EventBus.getDefault().post(new PathEvent(Permission.NOTICEOFTAKEOVER, mIsOpen,1));
                back(service);
            }
        } else if (NodeInfoUtil.pageContains(info, "通知使用权") && !getList().contains(SIGN1)) {
            if (NodeInfoUtil.pageContains(info, AppUtils.getAppName(mContext))) {
                boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext,info, AppUtils.getAppName(mContext));
                    addSIGN(SIGN1);
            } else if (!getList().contains(SIGN1)) {
                NodeInfoUtil.scrollableList(info);
            }
        } else if ((NodeInfoUtil.pageContains(info, AppUtils.getStringWithAppName(mContext, "允许%s获取通知访问权限吗"))||NodeInfoUtil.pageContains(info, AppUtils.getStringWithAppName(mContext, "允许%s读取通知吗"))||NodeInfoUtil.pageContains(info, "要启用"))&&NodeInfoUtil.pageContains(info, "允许") && getList().contains(SIGN1) && !getList().contains(SIGN2)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext,info, "允许");
            addSIGN(SIGN2);
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_NOTICEOFTAKEOVER, true);
                EventBus.getDefault().post(new PathEvent(Permission.NOTICEOFTAKEOVER, true,1));
                back(service);
            }
        } else if (NodeInfoUtil.pageContains(info, "确定") && getList().contains(SIGN1) && !getList().contains(SIGN2)) {
            boolean isOpen = NodeInfoUtil.clickNodeInfoAll(mContext,info, "确定");
            addSIGN(SIGN2);
            if (isOpen) {
                mIsOpen = true;
                PermissionProvider.save(mContext, PROVIDER_NOTICEOFTAKEOVER, true);
                EventBus.getDefault().post(new PathEvent(Permission.NOTICEOFTAKEOVER, true,1));
                back(service);
            }
        } else if (!getList().contains(SIGN1)) {
            NodeInfoUtil.scrollableList(info);
        }
    }

}
