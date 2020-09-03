package com.xiaoniu.cleanking.ui.securitycenter.entrance.model

import com.xiaoniu.cleanking.R

/**
 * Created by xinxiaolong on 2020/9/3.
 * email：xinxiaolong123@foxmail.com
 */
class RecmedBarDataStore {

    private var modelList = arrayOf(
            RecommendModel(R.drawable.icon_account_detection, "您的微信账号未检测"),
            RecommendModel(R.drawable.icon_pay_detection, "您的支付账号未检测"),
            RecommendModel(R.drawable.icon_wifi_detection, "您的网络环境未检测"))

    private var USED_MARKS = arrayOf(false, false, false)
    private var index = -1;

    companion object {

        const val accountIndex = 0
        const val payIndex = 1
        const val wifiIndex = 2

        @Volatile
        private var instance: RecmedBarDataStore? = null
        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: RecmedBarDataStore().also { instance = it }
                }
    }

    private constructor()


    /**
     * 重置数据状态
     */
    fun reset() {
        index = -1
        for (n in USED_MARKS.indices) {
            USED_MARKS[n] = false
        }
    }

    /**
     * 顺序pop出推荐的功能
     */
    fun pop(): RecommendModel? {
        index++
        if (index >= modelList.size) {
            return null
        }
        if (notUsed(index)&&outCoolTime(index)) {
            return modelList[index]
        }
        return pop()
    }

    fun markAccountDetectionUsed(){
        USED_MARKS[accountIndex]=true
    }

    fun markPayDetectionUsed(){
        USED_MARKS[payIndex]=true
    }

    fun markWifiDetectionUsed(){
        USED_MARKS[wifiIndex]=true
    }

    /**
     * 此功能用户没有点击过
     * 注：虽然写的是used但这里只要用户点击了改推荐功能，就认为是已使用，下次不再推荐
     */
    fun notUsed(index: Int): Boolean {
        return !USED_MARKS[index]
    }

    /**
     * 判断该功能位否在冷却时间外
     */
    fun outCoolTime(index: Int) :Boolean{
        return false
    }

}
