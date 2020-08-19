package com.xiaoniu.cleanking.utils

import android.content.Context
import com.jess.arms.utils.FileUtils
import com.xiaoniu.clean.deviceinfo.EasyBatteryMod
import com.xiaoniu.clean.deviceinfo.EasyMemoryMod
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig
import com.xiaoniu.cleanking.utils.update.MmkvUtil
import com.xiaoniu.cleanking.utils.update.PreferenceUtil
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Created by xinxiaolong on 2020/8/18.
 * email：xinxiaolong123@foxmail.com
 *
 * 需求文档：https://shimo.im/docs/BJJCJkNZaecnp1O9/read
 */
class HomeDeviceInfoStore {

    private var memoryPercent: Int = 0
    private var cleanedMemoryPercent: Int = 0

    companion object {
        @Volatile
        private var instance: HomeDeviceInfoStore? = null
        fun getInstance() =
                instance ?: synchronized(this) {
                    instance
                            ?: HomeDeviceInfoStore().also { instance = it }
                }
    }


    private constructor() {

    }


    /*
     *************************************************************************************************************************************************
     **********************************************************memory info****************************************************************************
     *************************************************************************************************************************************************
     */

    /**
     * 获取总内存大小
     */
    fun getTotalMemory(context: Context): Float {
        return MemoryInfoStore.getInstance().getTotalMemory(context)
    }

    /**
     * 获取已使用内存大小
     */
    fun getUsedMemory(context: Context): Float {
        var total = getTotalMemory(context)
        var percent = getUsedMemoryPercent().toDouble() / 100
        return format(total * percent)
    }

    /**
     * 获取清理后已使用内存大小
     */
    fun getCleanedUsedMemory(context: Context): Float {
        var total = getTotalMemory(context)
        return format(total * (getCleanedUsedMemoryPercent().toDouble() / 100))
    }

    /**
     * 获取内存使用百分比
     */
    fun getUsedMemoryPercent(): Int {
        if (memoryPercent == 0) {
            memoryPercent = NumberUtils.mathRandom(70, 85).toInt()
        }
        return memoryPercent
    }

    /**
     * 获取清理后内存使用百分比
     */
    fun getCleanedUsedMemoryPercent(): Int {
        if (cleanedMemoryPercent == 0) {
            cleanedMemoryPercent = getUsedMemoryPercent() - PreferenceUtil.getOneKeySpeedNum().toInt()
        }
        return cleanedMemoryPercent
    }

    fun format(value: Double): Float {
        var bd = BigDecimal(value)
        bd = bd.setScale(1, RoundingMode.HALF_UP)
        return bd.toFloat()
    }


    /*
     *************************************************************************************************************************************************
     *****************************************************storage info********************************************************************************
     *************************************************************************************************************************************************
     */

    /**
     * 获取总硬盘大小
     */
    fun getTotalStorage(context: Context): Float {
        var easyMemoryMod = EasyMemoryMod(context)
        var total = easyMemoryMod.getTotalInternalMemorySize().toFloat()
        return FileUtils.getUnitGB(total).toFloat()
    }

    /**
     * 获取已使用总硬盘大小
     */
    fun getUsedStorage(context: Context): Float {
        var easyMemoryMod = EasyMemoryMod(context)
        var total = easyMemoryMod.getTotalInternalMemorySize().toFloat()
        var used = total - easyMemoryMod.getAvailableInternalMemorySize().toFloat()
        return FileUtils.getUnitGB(used).toFloat()
    }

    /**
     * 获取已使用硬盘占比
     */
    fun getUsedStoragePercent(context: Context): Double {
        var easyMemoryMod = EasyMemoryMod(context)
        var total = easyMemoryMod.getTotalInternalMemorySize().toDouble()
        var used = total - easyMemoryMod.getAvailableInternalMemorySize().toDouble()
        return (used / total) * 100
    }

    /**
     * 获取已使用总硬盘大小
     */
    fun getCleanedUsedStorage(context: Context): Float {
        var diff=MmkvUtil.getLong(SpCacheConfig.MKV_KEY_HOME_CLEANED_DATA_B,0)

        log("getCleanedUsedStorage() diff"+diff)
        var cleaned=FileUtils.getUnitGB(diff.toFloat())
        log("getCleanedUsedStorage() cleaned"+cleaned)

        return getUsedStorage(context)-FileUtils.getUnitGB(diff.toFloat()).toFloat()
    }

    /**
     * 获取已使用硬盘占比
     */
    fun getCleanedUsedStoragePercent(context: Context): Float {
        var used=getCleanedUsedStorage(context)
        var total=getTotalStorage(context)
        var percent= (used / total) * 100
        log("getCleanedUsedStoragePercent() percent="+percent+"  used="+used+"   total="+total)
        return percent
    }

    /*
     *************************************************************************************************************************************************
     *****************************************************temperature info****************************************************************************
     *************************************************************************************************************************************************
     */

    /**
     * 获取真实的电池温度
     */
    fun getBatteryTemperature(context: Context):Float{
       var easyBatteryMod = EasyBatteryMod(context)
        return easyBatteryMod.batteryTemperature
    }


    /**
     * 需求：部分机型获取不到cpu温度情况下，取随机值【40，60】
     * 实现：部分毛！全都随机!!!
     */
    fun getCPUTemperature(context: Context):Float{
        return NumberUtils.mathRandomInt(40, 60).toFloat()
    }

    /**
     * 获取清理后的电池温度
     * 需求：若用户在核心功能区域使用完手机降温功能，降温完成页降温数值3°，同时手机状态监控电池温度和cpu温度降低3°。
     */
    fun getCleanedBatteryTemperature(context: Context):Float{
        return getBatteryTemperature(context)-PreferenceUtil.getCleanCoolNum()
    }

    /**
     * 获取清理后的cpu温度
     * 需求：若用户在核心功能区域使用完手机降温功能，降温完成页降温数值3°，同时手机状态监控电池温度和cpu温度降低3°。
     */
    fun getCleanedCPUTemperature(context: Context):Float{
        return getCPUTemperature(context)-PreferenceUtil.getCleanCoolNum()
    }


    fun log(text:String){
        LogUtils.e("HomeDeviceInfoStore:======"+text)
    }
}
