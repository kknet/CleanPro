package com.hellogeek.permission.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hellogeek.permission.R;
import com.hellogeek.permission.R2;
import com.hellogeek.permission.base.BaseActivity;
import com.hellogeek.permission.bean.ASBase;
import com.hellogeek.permission.bean.CustomSharedPreferences;
import com.hellogeek.permission.Integrate.Permission;
import com.hellogeek.permission.Integrate.PermissionIntegrate;
import com.hellogeek.permission.provider.PermissionProvider;
import com.hellogeek.permission.server.AccessibilityServiceMonitor;
import com.hellogeek.permission.server.interfaces.IAccessibilityServiceMonitor;
import com.hellogeek.permission.statusbarcompat.StatusBarCompat;
import com.hellogeek.permission.strategy.AutoFixAction;
import com.hellogeek.permission.strategy.ExternalInterface;
import com.hellogeek.permission.strategy.IGetManfactureExample;
import com.hellogeek.permission.strategy.PathEvent;
import com.hellogeek.permission.strategy.ServiceEvent;
import com.hellogeek.permission.usagerecord.UsageBuider;
import com.hellogeek.permission.usagerecord.UsageRecordHelper;
import com.hellogeek.permission.usagerecord.UsageRecordType;
import com.hellogeek.permission.util.AccessibilitUtil;
import com.hellogeek.permission.util.Constant;
import com.hellogeek.permission.util.DialogUtil;
import com.hellogeek.permission.util.PermissionConvertUtils;
import com.hellogeek.permission.util.PhoneRomUtils;
import com.hellogeek.permission.util.UIUtil;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;


import static com.hellogeek.permission.util.Constant.*;


/**
 * Desc:
 * <p>
 *
 * @author: ZhouTao
 * Date: 2019/7/1
 * Copyright: Copyright (c) 2016-2020
 * Company: @小牛科技
 * Update
 */
public class PermissionAutoFixActivity extends BaseActivity implements IAccessibilityServiceMonitor {
    public static final String TAG = PermissionAutoFixActivity.class.getSimpleName();

    @BindView(R2.id.ivHintIcon)
    ImageView ivHintIcon;
    @BindView(R2.id.imTop)
    ImageView imTop;
    @BindView(R2.id.clHintIcon)
    ConstraintLayout clHintIcon;
    @BindView(R2.id.tvHintTitle)
    TextView tvHintTitle;
    @BindView(R2.id.listFixHint)
    RecyclerView listFixHint;
    @BindView(R2.id.ivClose)
    ImageView ivClose;
    @BindView(R2.id.tvFix)
    TextView tvFix;
    BaseQuickAdapter mAdapter;
    boolean fixing = false;
    @BindView(R2.id.iv_pb_bg)
    ImageView ivPbBg;
    @BindView(R2.id.iv_pb)
    ImageView ivPb;
    @BindView(R2.id.tv_pb_text)
    TextView tvPbText;
    @BindView(R2.id.tvClick)
    TextView tvClick;
    @BindView(R2.id.tvOneKeyFix)
    TextView tvOneKeyFix;
    @BindView(R2.id.tvHintWait)
    TextView tvHintWait;
    @BindView(R2.id.cl_title)
    ConstraintLayout clTitle;
    @BindView(R2.id.line)
    View line;
    @BindView(R2.id.tv_add_qq)
    TextView tvAddQQ;
    @BindView(R2.id.tvHintFail)
    TextView tvHintFail;
    @BindView(R2.id.cot_default)
    ConstraintLayout cot_default;
    private static final int mStartAFMGuide = 1000;
    private AutoFixAction autoFixAction;
    private AccessibilityServiceMonitor mService;
    public static final String PAGE = "authority_repair_page";
    private String sourcePage;

    @Override
    protected void initParams(Bundle savedInstanceState) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Constant.inExecution = false;
        isFixing = false;
        if (PermissionIntegrate.getPermission().getPermissionCallBack() != null) {
            PermissionIntegrate.getPermission().getPermissionCallBack().permissionSetSuccess(isAllOpen);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isBack = false;
    }

    private boolean isBack = false;

    //    private void addNotifiList(){
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_PACKAGE_ADDED);//应用加入
//        filter.addDataScheme("package");
//        this.registerReceiver(mPackageReceiver, filter);
//    }


    @Override
    protected int getLayoutResID() {
        return R.layout.activity_permission_auto_fix;
    }

    @Override
    protected void initContentView() {
        sourcePage = PermissionIntegrate.getPermission().getPermissionSourcePage();
        Constant.inExecution = true;
        StatusBarCompat.translucentStatusBarForImage(this, true, false);
        EventBus.getDefault().register(this);
        listFixHint.setLayoutManager(new LinearLayoutManager(this));
        listFixHint.setAdapter(mAdapter = new BaseQuickAdapter<ASBase, BaseViewHolder>(R.layout.per_item_auto_fix_hint) {
            @Override
            protected void convert(BaseViewHolder helper, ASBase item) {
                if (item == null) {
                    return;
                }
                helper.setText(R.id.hintText, PermissionConvertUtils.getTitleStr(item.permission));
                int defaultColor = PermissionIntegrate.getPermission().getPermissionDefaultColor() != 0 ? PermissionIntegrate.getPermission().getPermissionDefaultColor() : getResources().getColor(R.color.permission_title);
//                int openColor = PermissionIntegrate.getPermission().getPermissionOpenColor() != 0 ? PermissionIntegrate.getPermission().getPermissionOpenColor() : Color.parseColor("#FF3B8E");
                helper.setTextColor(R.id.hintText, defaultColor);

                ImageView hintIcon = helper.getView(R.id.hintIcon);
                hintIcon.setImageResource(PermissionConvertUtils.getRes(item.permission));

                ImageView allowIcon = helper.getView(R.id.allowIcon);
//                allowIcon.setImageResource(item.isAllow ? R.mipmap.icon_fix_success : R.mipmap.icon_fix_fail);
                allowIcon.setImageResource(item.isAllow ? R.mipmap.icon_fix_success_new : R.mipmap.icon_fix_fail);

            }
        });

        new UsageBuider().setUsageType(UsageRecordType.TYPE_VIEW_PAGE.getValue())
                .setPage(PAGE)
                .setSourcePage(sourcePage)
                .setEventCode("authority_repair_page_view_page")
                .setEventName("权限一键修复页面浏览")
                .send();

    }

    private List<ASBase> base;
    private boolean isAllOpen = true;
    private int successNum = 0;

    private void setPermissionList() {
        base = new ArrayList<>();
        if (autoFixAction != null) {
            ArrayList<Permission> list = autoFixAction.getPermissionList();
            for (Permission permission : list) {
                ASBase asBase = new ASBase();
                asBase.permission = permission;
                asBase.isNecessary = isNecessary(permission);
                asBase.isAllow = AccessibilitUtil.isOpenPermission(this, permission);
                isAllOpen = isAllOpen & asBase.isAllow;
                if (!asBase.isAllow) {
                    successNum++;
                }
                base.add(asBase);
            }
            if (base != null) {
                mAdapter.setNewData(base);
            }
        }
    }


    private boolean isNecessary(Permission permission) {
        switch (permission) {
            case NOTIFICATIONBAR:
                return false;
            case REPLACEACLLPAGE:
                return PhoneRomUtils.isHuawei() ? true : false;
            case BACKSTAGEPOPUP:
                return (PhoneRomUtils.isVivo() || PhoneRomUtils.isXiaoMi()) ? true : false;
            case SYSTEMSETTING:
                return true;

            case SELFSTARTING:
                return (PhoneRomUtils.isOppo()) ? true : false;

            case LOCKDISPALY:
                return (PhoneRomUtils.isVivo() || PhoneRomUtils.isXiaoMi()) ? true : false;
            case SUSPENDEDTOAST:
                return true;
            case NOTICEOFTAKEOVER:
                return false;
        }
        return false;
    }

    @Override
    protected void initData() {
        autoFixAction = IGetManfactureExample.getManfactureExample(this);
        setPermissionList();
        if (CustomSharedPreferences.getBooleanValue(this, CustomSharedPreferences.isPermissionShow)) {
            openSuccessExhibition(isAllOpen, true);
        }
    }

    @Override
    protected boolean onGoBack() {
        return false;
    }

    @OnClick({R2.id.tvFix, R2.id.ivClose, R2.id.tv_add_qq})
    public void onclickView(View view) {
        int id = view.getId();
        if (id == R.id.tvFix) {
            if (autoFixAction == null) {
                return;
            }
            boolean isAllopen = false;
            if (PermissionIntegrate.getPermission().getIsNecessary()) {
                isAllopen = !ExternalInterface.getInstance(this).isOpenNecessaryPermission(this);
            } else {
                isAllopen = !ExternalInterface.getInstance(this).isOpenAllPermission(this);
            }
            if (isAllopen) {
                if (!AccessibilitUtil.isAccessibilitySettingsOn(this, AccessibilityServiceMonitor.class.getCanonicalName())) {
                    handler.sendEmptyMessage(1);
                    AccessibilitUtil.showSettingsUI(this);
                    isFixing = true;
                } else {
                    if (AccessibilityServiceMonitor.getInstance() != null) {
                        startAnimation();
                        if (mService == null) {
                            mService = AccessibilityServiceMonitor.getInstance();
                            mService.setAccessibilityEvent(this);
                        }

                        if (autoFixAction == null) {
                            autoFixAction = IGetManfactureExample.getManfactureExample(this);
                        }
                        if (autoFixAction != null) {
                            autoFixAction.configAccessbility(mService);
                            request();
                            isFixing = true;
                        }
                    } else {
                        AccessibilitUtil.showSettingsUI(this);
                        handler.sendEmptyMessage(1);
                        isFixing = true;
                    }
                }
            }
            new UsageBuider().setUsageType(UsageRecordType.TYPE_CLICK.getValue())
                    .setPage(PAGE)
                    .setSourcePage(sourcePage)
                    .setEventCode("once_repair_click")
                    .setEventName("权限一键修复按钮点击")
                    .setExtra("number_name_of_authority", UsageRecordHelper.getPermissionClickJson(base))
                    .send();
        } else if (id == R.id.ivClose) {
            if (isFixing) {
                new UsageBuider().setUsageType(UsageRecordType.TYPE_CLICK.getValue())
                        .setPage(PAGE)
                        .setSourcePage(sourcePage)
                        .setEventCode("close_click")
                        .setEventName("关闭按钮点击")
                        .setExtra("authorization_result", UsageRecordHelper.getPermissionResultJson(base))
                        .send();
            }
            finish();
        } else if (id == R.id.tv_add_qq) {
            Log.i(TAG, "click add QQ");
            isFixing = false;
            if (PermissionIntegrate.getPermission().getPermissionAddQQCallback() != null) {
                new UsageBuider().setUsageType(UsageRecordType.TYPE_CLICK.getValue())
                        .setPage(PAGE)
                        .setSourcePage(sourcePage)
                        .setEventCode("qq_feedback_click")
                        .setEventName("加QQ群点击")
                        .send();
                PermissionIntegrate.getPermission().getPermissionAddQQCallback().addQQCallback();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (isBack) {
            super.onBackPressed();
            if (isFixing) {
                new UsageBuider().setUsageType(UsageRecordType.TYPE_CLICK.getValue())
                        .setPage(PAGE)
                        .setSourcePage(sourcePage)
                        .setEventCode("back_click")
                        .setEventName("物理返回按钮点击")
                        .setExtra("authorization_result", UsageRecordHelper.getPermissionResultJson(base))
                        .send();
            }
        }
    }

    private boolean isFixing;

    private String getPermissionProvider(Permission permission) {
        switch (permission) {
            case NOTIFICATIONBAR:
                return PROVIDER_NOTIFICATIONBAR;
            case REPLACEACLLPAGE:
                return PROVIDER_REPLACEACLLPAGE;
            case BACKSTAGEPOPUP:
                return PROVIDER_BACKSTAGEPOPUP;
            case SYSTEMSETTING:
                return PROVIDER_SYSTEMSETTING;

            case SELFSTARTING:
                return PROVIDER_SELFSTARTING;

            case LOCKDISPALY:
                return PROVIDER_LOCKDISPLAY;
            case SUSPENDEDTOAST:
                return PROVIDER_SUSPENDEDTOAST;
            case NOTICEOFTAKEOVER:
                return PROVIDER_NOTICEOFTAKEOVER;
        }
        return "";
    }

    @Override
    protected void onStart() {
        super.onStart();
        CustomSharedPreferences.setValue(this, CustomSharedPreferences.isActivity, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBack = true;
//        EventBus.getDefault().post(new RemoveEvent());
        if (mService != null && isFixing && autoFixAction != null) {
            if (CustomSharedPreferences.getBooleanValue(this, CustomSharedPreferences.isManual) && !AccessibilitUtil.isOpenPermission(this, permission)) {
                DialogUtil.showChangeCallToolsDialog(this, PermissionConvertUtils.getTitleStr(permission), new DialogUtil.CallToolsDialogDismissListener() {
                    @Override
                    public void open() {
                        PermissionProvider.save(PermissionAutoFixActivity.this, getPermissionProvider(permission), true);
                        setIsOpen(new PathEvent(permission, true));
                        request();
                    }

                    @Override
                    public void dismiss() {
                        PermissionProvider.save(PermissionAutoFixActivity.this, getPermissionProvider(permission), false);
                        request();
                    }
                });
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        request();
                    }
                }, 1000);
            }
        }
    }

    Random rand = new Random();


    private void request() {

        if (!CustomSharedPreferences.getBooleanValue(this, CustomSharedPreferences.isPermissionShow)) {
            CustomSharedPreferences.setValue(this, CustomSharedPreferences.isPermissionShow, true);
        }
        if (base == null) {
            return;
        }
        if (tvPbText != null) {
            if (getAllowCount() == base.size()) {
                tvPbText.setText(100 + "%");
            } else {
                tvPbText.setText((getAllowCount() * (100 / base.size())) + "%");
            }
        }
        boolean isAllDone = true;

        if (PermissionIntegrate.getPermission().getIsNecessary()) {
            for (ASBase asBase : base) {
                if (asBase.isNecessary) {
                    isAllDone = isAllDone && asBase.isAllow;
                }
            }
        } else {
            for (ASBase asBase : base) {
                isAllDone = isAllDone && asBase.isAllow;
            }
        }

        if (isAllDone) {
            isAllOpen = isAllDone;
            if (PermissionIntegrate.getPermission().getIsNecessary()) {
                PermissionProvider.save(this, PROVIDER_NECESSARY_PERMISSIONALLOPEN, true);
            } else {
                PermissionProvider.save(this, PROVIDER_PERMISSIONALLOPEN, true);
            }
            new UsageBuider().setUsageType(UsageRecordType.TYPE_CUSTOM.getValue())
                    .setPage(PAGE)
                    .setSourcePage(sourcePage)
                    .setEventCode("one_click_repair_complete")
                    .setEventName("“一键修复完成”结果上报")
                    .setExtra("authorization_result", UsageRecordHelper.getPermissionResultJson(base))
                    .send();
            finish();
        } else {
            boolean isExecute = false;
            for (ASBase asBase : base) {
                if (!asBase.isAllow && asBase.executeNumber == 0) {
                    permission = asBase.permission;
                    asBase.executeNumber++;
                    isExecute = true;
                    autoFixAction.permissionAction(asBase.permission);
                    break;
                }
            }
            if (!isExecute) {
                openSuccessExhibition(isAllDone, false);
            }
        }
    }

    private void openSuccessExhibition(boolean isAllDone, boolean isFirst) {
        if (ivPb.getAnimation() != null) {
            ivPb.clearAnimation();
        }
        if (ivPbBg.getAnimation() != null) {
            ivPbBg.clearAnimation();
        }
        if (isAllDone) {
            tvHintTitle.setText("权限已全部开启成功");
            tvFix.setVisibility(View.GONE);
            tvAddQQ.setVisibility(View.VISIBLE);
            cot_default.setVisibility(View.GONE);
            clHintIcon.setVisibility(View.GONE);
            imTop.setVisibility(View.VISIBLE);
            imTop.setImageDrawable(getResources().getDrawable(R.mipmap.permission_sucess));
            tvHintFail.setVisibility(View.VISIBLE);
            tvHintFail.setText(getString(R.string.sucess_toast));
        } else if (!isFirst) {
            tvHintTitle.setText("很抱歉！一键修复失败啦！");
            tvFix.setVisibility(View.VISIBLE);
            tvAddQQ.setVisibility(View.VISIBLE);
            cot_default.setVisibility(View.GONE);
            clHintIcon.setVisibility(View.VISIBLE);
            imTop.setVisibility(View.GONE);
            tvHintFail.setVisibility(View.VISIBLE);
            tvHintFail.setText(getString(R.string.fail_toast));
        } else {
            SpannableStringBuilder style = new SpannableStringBuilder(successNum + " 项权限待开启");
            style.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_FF3B84)), 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色 textView.setText(style);
            tvHintTitle.setText(style);
            tvFix.setVisibility(View.VISIBLE);
            tvAddQQ.setVisibility(View.VISIBLE);
            cot_default.setVisibility(View.GONE);
            clHintIcon.setVisibility(View.GONE);
            imTop.setVisibility(View.VISIBLE);
            imTop.setImageDrawable(getResources().getDrawable(R.mipmap.permission_ing));
            tvHintFail.setVisibility(View.VISIBLE);
            tvHintFail.setText(getString(R.string.first_fail_toast));
        }
        for (ASBase asBase : base) {
            asBase.executeNumber = 0;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setService(ServiceEvent event) {
        mService = event.getAccessibilityServiceMonitor();
        mService.setAccessibilityEvent(this);
        startAnimation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setIsOpen(PathEvent event) {
        if (event.getIsOpen()) {
            for (ASBase asBase : base) {
                if (event.getPermission() == asBase.permission) {
                    asBase.isAllow = true;
                    UsageRecordHelper.recordItemData(new UsageBuider().setUsageType(UsageRecordType.TYPE_CUSTOM.getValue()).setPage(PAGE), event.getIsAuto(), asBase);
                }
            }
            mAdapter.notifyDataSetChanged();
        }
        if (event.getIsBack()) {
            onResume();
        }
    }

    private int getAllowCount() {
        int allowCount = 0;
        for (ASBase sASBase : base) {
            if (sASBase.isAllow) {
                allowCount++;
            }
        }
        return allowCount;
    }

    private Permission permission;

    private void startAnimation() {
        ivPbBg.setVisibility(View.VISIBLE);
        tvHintTitle.setText("最后一步了");
        cot_default.setVisibility(View.VISIBLE);
        tvHintWait.setVisibility(View.VISIBLE);
        tvPbText.setVisibility(View.VISIBLE);
        tvHintFail.setVisibility(View.GONE);
        Animation animation = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(5000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new LinearInterpolator());
        ivPbBg.startAnimation(animation);
        ivPbBg.setImageResource(R.mipmap.img_progressbar_bg);

        Animation animation2 = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation2.setDuration(4000);
        animation2.setRepeatCount(Animation.INFINITE);
        animation2.setInterpolator(new LinearInterpolator());
        ivPb.startAnimation(animation2);
        ivPb.setVisibility(View.VISIBLE);
        tvPbText.setVisibility(View.VISIBLE);

    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    startActivity(new Intent(PermissionAutoFixActivity.this, ASMGuideActivity.class));
                    break;
                default:
            }

        }
    };


    @SuppressLint("NewApi")
    @Override
    public void onEvent(AccessibilityEvent event) {
        if (mService != null && autoFixAction != null) {
            autoFixAction.permissionHandlerEvent(mService, event);
            try {
                if (mService.getRootInActiveWindow() != null) {
                    mService.getRootInActiveWindow().recycle();
                } else {
                }
            } catch (Exception e) {
            }
        }
    }


}