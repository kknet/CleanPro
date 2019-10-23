package com.xiaoniu.cleanking.ui.main.presenter;

import android.util.Log;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.xiaoniu.cleanking.base.AppHolder;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.activity.SplashADActivity;
import com.xiaoniu.cleanking.ui.main.bean.AuditSwitch;
import com.xiaoniu.cleanking.ui.main.bean.SwitchInfoList;
import com.xiaoniu.cleanking.ui.main.model.MainModel;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.prefs.NoClearSPHelper;

import javax.inject.Inject;

/**
 * Created by tie on 2017/5/15.
 */
public class SplashPresenter extends RxPresenter<SplashADActivity, MainModel> {

    private final RxAppCompatActivity mActivity;
    @Inject
    NoClearSPHelper mSPHelper;

    @Inject
    public SplashPresenter(RxAppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * 冷启动、热启动、完成页广告开关
     */
    public void getSwitchInfoList() {
        mModel.getSwitchInfoList(new Common4Subscriber<SwitchInfoList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(SwitchInfoList switchInfoList) {
                mView.getSwitchInfoListSuccess(switchInfoList);
                AppHolder.getInstance().setSwitchInfoList(switchInfoList);
            }

            @Override
            public void showExtraOp(String message) {
                mView.getSwitchInfoListFail();
            }

            @Override
            public void netConnectError() {
                mView.getSwitchInfoListFail();
            }
        });
    }

    /**
     * 过审
     */
    public void getAuditSwitch() {
        mModel.queryAuditSwitch(new Common4Subscriber<AuditSwitch>() {

            @Override
            public void getData(AuditSwitch auditSwitch) {
                Log.d("XiLei", "过审成功");
                mView.getAuditSwitch(auditSwitch);
            }

            @Override
            public void showExtraOp(String code, String message) {
                Log.d("XiLei", "过审---showExtraOp");
                mView.getAuditSwitchFail();
            }

            @Override
            public void showExtraOp(String message) {
                Log.d("XiLei", "过审---showExtraOp22");
                mView.getAuditSwitchFail();
            }

            @Override
            public void netConnectError() {
                Log.d("XiLei", "过审---netConnectError");
                mView.getAuditSwitchFail();
            }
        });
    }
}
