package com.comm.jksdk.ad.view.chjview;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFullScreenVideoAd;
import com.comm.jksdk.R;
import com.comm.jksdk.config.TTAdManagerHolder;
import com.comm.jksdk.http.utils.LogUtils;
import com.comm.jksdk.utils.CodeFactory;

/**
 * 穿山甲全屏视频广告view<p>
 *
 * @author zixuefei
 * @since 2019/11/16 14:01
 */
public class CsjFullScreenVideoView extends CHJAdView {
    private TTAdNative mTTAdNative;

    protected CsjFullScreenVideoView(Context context) {
        super(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.csj_full_screen_video_ad_view;
    }

    @Override
    public void initView() {
//        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get(mAppId).requestPermissionIfNecessary(mContext);
//        //step3:创建TTAdNative对象,用于调用广告请求接口
//        mTTAdNative = TTAdManagerHolder.get(mAppId).createAdNative(mContext.getApplicationContext());
    }

    /**
     * 获取全屏视屏广告并展示
     */
    public void loadFullScreenVideoAd(final Activity activity, String adId, int orientation) {
        mTTAdNative = TTAdManagerHolder.get(mAppId).createAdNative(mContext.getApplicationContext());
        if (activity == null) {
            throw new NullPointerException("loadFullScreenVideoAd activity is null");
        }
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(adId)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(720, 1280)
                .setOrientation(orientation)//必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build();
        //step5:请求广告
        mTTAdNative.loadFullScreenVideoAd(adSlot, new TTAdNative.FullScreenVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                LogUtils.e(TAG, "loadFullScreenVideoAd error:" + code + " message:" + message);
                adError(code, message);
                firstAdError(code, message);
                Toast.makeText(mContext, "loadFullScreenVideoAd error:" + code + " message:" + message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFullScreenVideoAdLoad(TTFullScreenVideoAd ad) {
                if (ad != null) {
                    adSuccess();
                    //step6:在获取到广告后展示
                    ad.showFullScreenVideoAd(activity);
                    ad.setFullScreenVideoAdInteractionListener(new TTFullScreenVideoAd.FullScreenVideoAdInteractionListener() {

                        @Override
                        public void onAdShow() {
                            adExposed();
                        }

                        @Override
                        public void onAdVideoBarClick() {
                            adClicked();
                        }

                        @Override
                        public void onAdClose() {

                        }

                        @Override
                        public void onVideoComplete() {

                        }

                        @Override
                        public void onSkippedVideo() {

                        }
                    });
                } else {
                    adError(CodeFactory.UNKNOWN, "视频广告数据为空");
                }
            }

            @Override
            public void onFullScreenVideoCached() {
            }
        });
    }
}