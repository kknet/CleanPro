package com.xiaoniu.cleanking.ui.main.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebViewClient;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.Constant;
import com.xiaoniu.cleanking.app.injector.module.ApiModule;
import com.xiaoniu.cleanking.base.SimpleFragment;
import com.xiaoniu.cleanking.ui.main.activity.MainActivity;
import com.xiaoniu.cleanking.ui.main.widget.SPUtil;
import com.xiaoniu.cleanking.ui.usercenter.activity.UserLoadH5Activity;
import com.xiaoniu.cleanking.utils.StatisticsUtils;
import com.xiaoniu.cleanking.utils.ToastUtils;
import com.xiaoniu.cleanking.widget.statusbarcompat.StatusBarCompat;
import com.xiaoniu.statistic.NiuDataAPI;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 首页tab H5页面（商城页、生活页）
 * Created on 2018/3/21.
 */
public class ShoppingMallFragment extends SimpleFragment implements MainActivity.OnKeyBackListener {
    @BindView(R.id.web_container)
    RelativeLayout mRootView;
    @BindView(R.id.layout_not_net)
    LinearLayout mLayoutNetError;
    private String url = ApiModule.SHOPPING_MALL;
    private boolean isFirst = true;
    private boolean isFirstPause = true;

    private AgentWeb mAgentWeb;

    private boolean isSuccess = false;
    private boolean isError = false;
    private boolean mCanGoBack = true;
    private final int REQUEST_SDCARD = 638;
    public final static int SHARE_SUCCESS = 0;
    public final static int SHARE_CANCEL = 1;
    public final static int SHARE_WECHAT = 2;
    public final static int SHARE_QQ = 3;
    public final static int SHARE_SINA = 4;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHARE_SUCCESS:
//                    showToast("分享成功");
                    break;
                case SHARE_CANCEL:
//                    showToast("已取消");
                    break;
                case SHARE_WECHAT:
                    showToast("没有安装微信，请先安装应用");
                    break;
                case SHARE_QQ:
                    showToast("没有安装QQ，请先安装应用");
                    break;
                case SHARE_SINA:
                    showToast("没有安装新浪微博，请先安装应用");
                    break;

            }
        }
    };

    public static ShoppingMallFragment getIntance(String url) {
        ShoppingMallFragment fragment = new ShoppingMallFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constant.URL, url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_shopping_mall;
    }

    @Override
    protected void initView() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            url = arguments.getString(Constant.URL, ApiModule.SHOPPING_MALL);
        }
//        if (url.contains("?")) {
//            url = url + "&xn_data=" + AndroidUtil.getXnData();
//        } else {
//            url = url + "?xn_data=" + AndroidUtil.getXnData();
//        }
        initWebView();
    }

    private void initWebView() {

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mRootView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
                .closeIndicator()
                .setMainFrameErrorView(R.layout.web_error_layout, R.id.tv_reload)
                .setWebChromeClient(new CustomWebChromeClient())
                .setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        if (!isSuccess) {
                            showLoadingDialog();
                        }
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        getWebView().setLayerType(View.LAYER_TYPE_HARDWARE, null);
                        cancelLoadingDialog();
                        if (!isError) {
                            isSuccess = true;
                            //回调成功后的相关操作
                            if (mLayoutNetError != null) {
                                mLayoutNetError.setVisibility(View.GONE);
                            }
                            if (mRootView != null) {
                                mRootView.setVisibility(View.VISIBLE);
                            }
                        }
                        isError = false;
                    }

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                        // 不要使用super，否则有些手机访问不了，因为包含了一条 handler.cancel()
                        // super.onReceivedSslError(view, handler, error);

                        // 接受所有网站的证书，忽略SSL错误，执行访问网页
                        handler.proceed();
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        super.onReceivedError(view, errorCode, description, failingUrl);
                        isError = true;
                        isSuccess = false;
                        if (mLayoutNetError != null) {
                            mLayoutNetError.setVisibility(View.VISIBLE);
                        }
                        if (mRootView != null) {
                            mRootView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        // 如下方案可在非微信内部WebView的H5页面中调出微信支付
                        try {
                            if (url.startsWith("weixin://wap/pay?")) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                startActivity(intent);
                                return true;
                            } else {
                                Map<String, String> extraHeaders = new HashMap<String, String>();
                                extraHeaders.put("Referer", "http://chinamrgift.com.cn");
                                view.loadUrl(url, extraHeaders);
                            }
                        } catch (Exception e) {
                            ToastUtils.showShort("请安装微信最新版!");
                        }
                        return super.shouldOverrideUrlLoading(view, url);
                    }
                })
                .createAgentWeb()
                .ready()
                .go(url);

        mAgentWeb.getJsInterfaceHolder().addJavaObject("cleanPage",new Javascript());
        mAgentWeb.getJsInterfaceHolder().addJavaObject("sharePage",new Javascript());
    }

    private class CustomWebChromeClient extends com.just.agentweb.WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (view != null && !TextUtils.isEmpty(view.getUrl())
                    && view.getUrl().contains(title)) {
                return;
            }
            if (getActivity() != null) {
                mCanGoBack = !"悟空清理商城".equals(title);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mAgentWeb.getWebLifeCycle().onResume();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), true);
            } else {
                StatusBarCompat.setStatusBarColor(getActivity(), getResources().getColor(R.color.color_4690FD), false);
            }
            if (isFirst) {
                getWebView().loadUrl(url);
                isFirst = false;
            }
        }else {
            if (!isFirstPause) {
                mAgentWeb.getWebLifeCycle().onPause();
            }else {
                isFirstPause = false;
            }

        }
    }

    public WebView getWebView() {
        return mAgentWeb.getWebCreator().getWebView();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isHidden()) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
        NiuDataAPI.onPageEnd("information_iew_page", "信息页面");
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!isHidden()) {
//            mWebView.reload();
//        }
        if (!isHidden()) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
        NiuDataAPI.onPageStart("information_iew_page", "信息页面");
    }

    private long firstTime;

    @Override
    public void onKeyBack() {
        if (getWebView().canGoBack() && mCanGoBack) {
            getWebView().goBack();
            firstTime = 0;
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - firstTime > 1500) {
                Toast.makeText(getActivity(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                firstTime = currentTimeMillis;
            } else {
                SPUtil.setInt(getContext(), "turnask", 0);
//                AppManager.getAppManager().AppExit(getContext(), false);
            }
        }
    }

    @Override
    public void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    public class Javascript {

        @JavascriptInterface
        public void toOtherPage(String url) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.URL, url);
            bundle.putString(Constant.Title, "");
            bundle.putBoolean(Constant.NoTitle, false);
            startActivity(UserLoadH5Activity.class, bundle);
        }

        @JavascriptInterface
        public void onTitleClick(String id, String name) {
            StatisticsUtils.trackClickH5("content_cate_click", "资讯页分类点击", "home_page", "information_page", id, name);
        }


        @JavascriptInterface
        public void shareLink(String picurl, String linkurl, String title, String content, String activityEvtType) {
            //动态权限申请
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SDCARD);
            } else {
                share(picurl, linkurl, title, content, -1);
            }
        }

        @JavascriptInterface
        public void modularShareLink(String picurl, String linkurl, String title, String content, int type) {
            //动态权限申请
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SDCARD);
            } else {
                share(picurl, linkurl, title, content, type);
            }
        }

        @JavascriptInterface
        public void canGoBack(boolean canGoBack) {
            mCanGoBack = canGoBack;
        }
    }

    private SHARE_MEDIA[] platform = {SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA};

    public void share(String picurl, String linkurl, String title, String content, int type) {
        UMWeb web = new UMWeb(linkurl);//分享链接
        web.setTitle(title);//标题
        if (TextUtils.isEmpty(picurl)) {
            web.setThumb(new UMImage(getContext(), R.mipmap.logo_share));  //缩略图
        } else {
            web.setThumb(new UMImage(getContext(), picurl));
        }
        web.setDescription(content);//描述
        ShareAction shareAction = new ShareAction(getActivity()).withMedia(web);
        shareAction.setCallback(new UMShareListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
            }

            @Override
            public void onResult(SHARE_MEDIA share_media) {
                handler.sendEmptyMessage(SHARE_SUCCESS);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                if (share_media == SHARE_MEDIA.WEIXIN || share_media == SHARE_MEDIA.WEIXIN_CIRCLE) {
                    handler.sendEmptyMessage(SHARE_WECHAT);
                } else if (share_media == SHARE_MEDIA.QQ || share_media == SHARE_MEDIA.QZONE) {
                    handler.sendEmptyMessage(SHARE_QQ);
                } else if (share_media == SHARE_MEDIA.SINA) {
                    handler.sendEmptyMessage(SHARE_SINA);
                }

            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                handler.sendEmptyMessage(SHARE_CANCEL);
            }
        });
        switch (type) {
            case -1:
                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE/*, SHARE_MEDIA.SINA*/);
                shareAction.open();
                break;
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                shareAction.setPlatform(platform[type]);
                shareAction.share();
                break;
            default:
                shareAction.setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE/*, SHARE_MEDIA.SINA*/);
                shareAction.open();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getContext()).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.layout_not_net)
    public void onTvRefreshClicked() {
        getWebView().loadUrl(url);
    }

    private void refresh() {
        getWebView().loadUrl(url);
    }

    private void showToast(String msg) {
        ToastUtils.showShort(msg);
    }
}
