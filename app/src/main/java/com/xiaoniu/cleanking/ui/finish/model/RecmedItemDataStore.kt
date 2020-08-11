package com.xiaoniu.cleanking.ui.finish.model

import android.text.SpannableString
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.app.AppApplication
import com.xiaoniu.cleanking.base.ScanDataHolder
import com.xiaoniu.cleanking.ui.tool.notify.manager.NotifyCleanManager
import com.xiaoniu.cleanking.utils.AndroidUtil
import com.xiaoniu.cleanking.utils.CleanUtil
import com.xiaoniu.cleanking.utils.NumberUtils
import com.xiaoniu.cleanking.utils.update.PreferenceUtil

/**
 * Created by xinxiaolong on 2020/8/4.
 * email：xinxiaolong123@foxmail.com
 * 推荐功能数据组装算法
 */
public class RecmedItemDataStore {

    var itemArray = arrayOf("一键清理", "病毒查杀", "一键加速", "超强省电", "微信清理", "手机降温", "通知栏清理")

    var click_clean=false;
    var click_virus=false;
    var click_acc=false;
    var click_power=false;
    var click_wx=false;
    var click_cool=false;
    var click_notify=false;

    var memory: String = ""
    var powerNum: String = ""
    var temperature: String = ""
    var modelIndex = -1

    companion object {
        @Volatile
        private var instance: RecmedItemDataStore? = null
        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: RecmedItemDataStore().also { instance = it }
                }
    }

    private constructor() {

    }

    fun resetIndex() {
        modelIndex = -1
    }

    fun popModel(): RecmedItemModel? {
        modelIndex++;
        if (modelIndex >= itemArray.size) {
            return null
        }
        var name = itemArray[modelIndex]

        when (name) {
            "一键清理" -> {
                if (PreferenceUtil.getNowCleanTime()&&!click_clean) {
                    return assembleOneKeyClean()
                }
            }
            "病毒查杀" -> {
                if (PreferenceUtil.getVirusKillTime()&&!click_virus) {
                    return assembleCleanVirus()
                }
            }
            "一键加速" -> {
                if (PreferenceUtil.getCleanTime()&&!click_acc) {
                    return assembleOneKeyAcc()
                }
            }
            "超强省电" -> {
                if (PreferenceUtil.getPowerCleanTime()&&!click_power) {
                    return assembleBattery()
                }
            }
            "微信清理" -> {
                if (PreferenceUtil.getWeChatCleanTime()&&!click_wx) {
                    return assembleCleanWeChat()
                }
            }
            "手机降温" -> {
                if (PreferenceUtil.getCoolingCleanTime()&&!click_cool) {
                    return assembleTemperature()
                }
            }
            "通知栏清理" -> {
                if (PreferenceUtil.getNotificationCleanTime()&&!click_notify) {
                    return assembleNotify()
                }
            }
        }
        return popModel();
    }

    /**
     * 一键清理数据
     */
    fun assembleOneKeyClean(): RecmedItemModel {
        var title = "垃圾文件过多"

        var content1: SpannableString
        if (ScanDataHolder.getInstance().getTotalSize() <= 0) {
            content1 = SpannableString("已发现大量缓存垃圾")
        } else {
            val countEntity = CleanUtil.formatShortFileSize(ScanDataHolder.getInstance().getTotalSize())
            var storageNum = countEntity.totalSize + countEntity.unit
            var content = "已发现" + storageNum + "缓存垃圾文件"
            content1 = AndroidUtil.inertColorText(content, content.indexOf(storageNum), content.indexOf(storageNum) + storageNum.length, getRedColor())
        }
        var content2 = SpannableString("存储空间即将不足")
        var imageIcon = R.drawable.icon_finish_recommed_clean_stroage
        var buttonText = "立即清理"
        return RecmedItemModel(title, content1, content2, imageIcon, buttonText)
    }

    /**
     * 病毒查杀数据
     */
    fun assembleCleanVirus(): RecmedItemModel {
        var title = "病毒查杀"
        var content1 = SpannableString("网络环境存在安全风险")
        var content2 = SpannableString("存在隐私泄露风险")
        var imageIcon = R.drawable.icon_finish_recommed_clean_virus
        var buttonText = "立即杀毒"
        return RecmedItemModel(title, content1, content2, imageIcon, buttonText)
    }

    /**
     * 一键加速数据
     */
    fun assembleOneKeyAcc(): RecmedItemModel {
        var title = "手机加速"

        if (memory.length == 0) {
            memory = NumberUtils.mathRandom(70, 85) + "%"
        }
        var head = "内存占用已超过"
        var content = head + memory
        var content1 = AndroidUtil.inertColorText(content, head.length, head.length + memory.length, getRedColor())

        var content2 = SpannableString("不清理将导致手机卡慢")
        var imageIcon = R.drawable.icon_finish_recommed_clean_memory
        var buttonText = "立即清理"
        return RecmedItemModel(title, content1, content2, imageIcon, buttonText)
    }

    /**
     * 超强省电数据
     */
    fun assembleBattery(): RecmedItemModel {
        var title = "超强省电"

        if (powerNum.length == 0) {
            powerNum = NumberUtils.mathRandom(5, 15) + "个"
        }
        var content = powerNum + "应用正在大量耗电"
        var content1 = AndroidUtil.inertColorText(content, 0, powerNum.length, getRedColor())

        var content2 = SpannableString("将导致电池待机时间缩短")
        var imageIcon = R.drawable.icon_finish_recommed_clean_battery
        var buttonText = "立即省电"
        return RecmedItemModel(title, content1, content2, imageIcon, buttonText)
    }

    /**
     * 微信清理数据
     */
    fun assembleCleanWeChat(): RecmedItemModel {
        var title = "微信清理"
        var content1 = SpannableString("缓存过期文件过多")
        var content2 = SpannableString("不处理将导致聊天卡顿")
        var imageIcon = R.drawable.icon_finish_recommed_clean_wechat
        var buttonText = "立即清理"
        return RecmedItemModel(title, content1, content2, imageIcon, buttonText)
    }

    /**
     * 手机降温数据
     */
    fun assembleTemperature(): RecmedItemModel {
        var title = "手机降温"

        if (temperature.length == 0) {
            temperature = "37°C"
        }
        var head = "手机温度已超过"
        var content = head + temperature
        var content1 = AndroidUtil.inertColorText(content, head.length, head.length + temperature.length, getRedColor())

        var content2 = SpannableString("手机过热会损伤电池")
        var imageIcon = R.drawable.icon_finish_recommed_clean_cool
        var buttonText = "立即降温"
        return RecmedItemModel(title, content1, content2, imageIcon, buttonText)
    }

    /**
     * 通知栏清理数据
     */
    fun assembleNotify(): RecmedItemModel {
        var title = "通知栏清理"
        var content1 = SpannableString("通知栏常驻消息过多")
        var content2 = SpannableString("有效拦截诈骗骚扰通知")
        var imageIcon = R.drawable.icon_finish_recommed_clean_notify
        var buttonText = "立即清理"
        return RecmedItemModel(title, content1, content2, imageIcon, buttonText)
    }

    fun getRedColor(): Int {
        return AppApplication.getInstance().getResources().getColor(R.color.home_content_red)
    }
}