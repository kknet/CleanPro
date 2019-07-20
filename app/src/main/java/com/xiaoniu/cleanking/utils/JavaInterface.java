package com.xiaoniu.cleanking.utils;

import android.os.Bundle;
import android.webkit.JavascriptInterface;

import com.alibaba.android.arouter.launcher.ARouter;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.RouteConstants;

public class JavaInterface {

    @JavascriptInterface
    public void toOtherPage(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL, url);
        bundle.putString(Constant.Title, "");
        bundle.putBoolean(Constant.NoTitle, false);
        ARouter.getInstance().build(RouteConstants.USER_LOAD_H5_ACTIVITY).with(bundle).navigation();

    }
    @JavascriptInterface
    public void onTitleClick(String id, String name) {
        StatisticsUtils.trackClickH5("content_cate_click", "资讯页分类点击", "home_page", "information_page", id, name);
    }
}