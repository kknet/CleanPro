package com.comm.jksdk.ad.listener;

import com.comm.jksdk.ad.entity.AdInfo;

/**
 * 视频广告回调器<p>
 *
 * @author zixuefei
 * @since 2019/11/17 13:52
 */
public interface VideoAdListener extends AdListener {
    /**
     * 视频恢复
     */
    void onVideoResume(AdInfo info);

    /**
     * 激励视频获得激励回调(视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称)
     */
    void onVideoRewardVerify(AdInfo info, boolean rewardVerify, int rewardAmount, String rewardName);

    void onVideoComplete(AdInfo info);
}