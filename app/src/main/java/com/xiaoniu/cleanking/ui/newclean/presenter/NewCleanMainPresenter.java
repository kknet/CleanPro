package com.xiaoniu.cleanking.ui.newclean.presenter;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.xiaoniu.cleanking.base.RxPresenter;
import com.xiaoniu.cleanking.ui.main.bean.FirstJunkInfo;
import com.xiaoniu.cleanking.ui.main.bean.HomeRecommendEntity;
import com.xiaoniu.cleanking.ui.main.bean.ImageAdEntity;
import com.xiaoniu.cleanking.ui.main.bean.InteractionSwitchList;
import com.xiaoniu.cleanking.ui.main.config.PositionId;
import com.xiaoniu.cleanking.ui.newclean.fragment.NewCleanMainFragment;
import com.xiaoniu.cleanking.ui.newclean.model.NewScanModel;
import com.xiaoniu.cleanking.utils.FileQueryUtils;
import com.xiaoniu.cleanking.utils.net.Common4Subscriber;
import com.xiaoniu.cleanking.utils.net.CommonSubscriber;
import com.xiaoniu.cleanking.utils.update.MmkvUtil;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NewCleanMainPresenter extends RxPresenter<NewCleanMainFragment, NewScanModel> {

    @Inject
    public NewCleanMainPresenter() {
    }

    /**
     * 底部广告接口
     */
    public void requestBottomAd() {
        mModel.getBottomAd(new CommonSubscriber<ImageAdEntity>() {
            @Override
            public void getData(ImageAdEntity imageAdEntity) {
                if (imageAdEntity == null) return;
                ArrayList<ImageAdEntity.DataBean> dataList = imageAdEntity.getData();
                if (dataList != null && dataList.size() > 0) {
                    mView.showFirstAd(dataList.get(0), 0);
                }
            }

            @Override
            public void showExtraOp(String message) {

            }

            @Override
            public void netConnectError() {

            }
        });
    }

    /**
     * 互动式广告开关
     */
    public void getInteractionSwitch() {
        mModel.getInteractionSwitch(new Common4Subscriber<InteractionSwitchList>() {
            @Override
            public void showExtraOp(String code, String message) {

            }

            @Override
            public void getData(InteractionSwitchList switchInfoList) {
                for (InteractionSwitchList.DataBean dataBean : switchInfoList.getData()) {
                    if (TextUtils.equals(dataBean.getSwitcherKey(), "page_lock")) {
                        try {
                            MmkvUtil.saveString(PositionId.LOCK_INTERACTIVE, JSONObject.toJSONString(dataBean));
                            Log.e("dong",JSONObject.toJSONString(dataBean));
                        } catch (Exception e) {
                        }
                    }
                }
                mView.getInteractionSwitchSuccess(switchInfoList);
            }

            @Override
            public void showExtraOp(String message) {
            }

            @Override
            public void netConnectError() {
            }
        });
    }

    /**
     * 获取到可以加速的应用名单Android O以下的获取最近使用情况
     */
    @SuppressLint("CheckResult")
    public void getAccessListBelow() {
//        mView.showLoadingDialog();
        Observable.create((ObservableOnSubscribe<ArrayList<FirstJunkInfo>>) e -> {
            //获取到可以加速的应用名单
            FileQueryUtils mFileQueryUtils = new FileQueryUtils();
            //文件加载进度回调
            mFileQueryUtils.setScanFileListener(new FileQueryUtils.ScanFileListener() {
                @Override
                public void currentNumber() {

                }

                @Override
                public void increaseSize(long p0) {

                }

                @Override
                public void scanFile(String p0) {

                }
            });
            ArrayList<FirstJunkInfo> listInfo = mFileQueryUtils.getRunningProcess();
            if (listInfo == null) {
                listInfo = new ArrayList<>();
            }
            e.onNext(listInfo);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(strings -> {
                    if (mView == null) return;
//                    mView.cancelLoadingDialog();
                    try {
                        mView.getAccessListBelow(strings);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    /**
     * 推荐列表
     */
    public void getRecommendList() {
        mModel.getRecommendList(new Common4Subscriber<HomeRecommendEntity>() {
            @Override
            public void showExtraOp(String code, String message) {
                mView.getRecommendListFail();
            }

            @Override
            public void getData(HomeRecommendEntity entity) {
                mView.getRecommendListSuccess(entity);
            }

            @Override
            public void showExtraOp(String message) {
                mView.getRecommendListFail();
            }

            @Override
            public void netConnectError() {
                mView.getRecommendListFail();
            }
        });
    }
}
