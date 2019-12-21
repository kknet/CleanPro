package com.xiaoniu.cleanking.utils.update;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;
import com.xiaoniu.cleanking.BuildConfig;
import com.xiaoniu.cleanking.scheme.utils.ActivityCollector;
import com.xiaoniu.cleanking.ui.lockscreen.LockActivity;
import com.xiaoniu.cleanking.ui.lockscreen.PopLayerActivity;
import com.xiaoniu.cleanking.ui.main.activity.RedPacketHotActivity;
import com.xiaoniu.cleanking.ui.main.activity.ScreenInsideActivity;
import com.xiaoniu.cleanking.ui.main.config.SpCacheConfig;
import com.xiaoniu.common.utils.NetworkUtils;

/**
 * @author zhengzhihao
 * @date 2019/12/11 13
 * @mail：zhengzhihao@hellogeek.com
 */
public class MmkvUtil {

    //(string数据保存)
    public static void saveString(String key, String data) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.APPLICATION_ID, MMKV.MULTI_PROCESS_MODE);
        kv.encode(key, data);
    }

    //(string数据获取)
    public static String getString(String key, String def) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.APPLICATION_ID, MMKV.MULTI_PROCESS_MODE);
        return kv.containsKey(key) ? kv.decodeString(key) : def;
    }

    //(int数据保存)
    public static void saveInt(String key, int data) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.APPLICATION_ID, MMKV.MULTI_PROCESS_MODE);
        kv.encode(key, data);
    }

    //(int数据获取)
    public static int getInt(String key, int def) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.APPLICATION_ID, MMKV.MULTI_PROCESS_MODE);
        return kv.containsKey(key) ? kv.decodeInt(key) : def;
    }


    //(boolean数据保存)
    public static void saveBool(String key, boolean data) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.APPLICATION_ID, MMKV.MULTI_PROCESS_MODE);
        kv.encode(key, data);
    }

    //(boolean数据获取)
    public static boolean getBool(String key, boolean def) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.APPLICATION_ID, MMKV.MULTI_PROCESS_MODE);
        return kv.containsKey(key) ? kv.decodeBool(key) : def;
    }


    //(long数据保存)
    public static void saveLong(String key, long data) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.APPLICATION_ID, MMKV.MULTI_PROCESS_MODE);
        kv.encode(key, data);
    }

    //(long数据获取)
    public static long getLong(String key, long def) {
        MMKV kv = MMKV.mmkvWithID(BuildConfig.APPLICATION_ID, MMKV.MULTI_PROCESS_MODE);
        return kv.decodeLong(key, def);
    }


    //全屏插屏时间展示逻辑(跨进程)
    public static boolean fullInsertPageIsShow(int showTimes) {
        MMKV kv = MMKV.mmkvWithID("switch", MMKV.MULTI_PROCESS_MODE);
        long pretime = TextUtils.isEmpty(kv.decodeString(SpCacheConfig.POP_FULL_LAYER_TIME)) ? 0 : Long.valueOf(kv.decodeString(SpCacheConfig.POP_FULL_LAYER_TIME));
        int number = kv.decodeInt(SpCacheConfig.POP_FULL_LAYER_NUMBERS);
        //一小时内showTimes次
        if (pretime <= 0 || (System.currentTimeMillis() - pretime) > (60 * 60 * 1000) || ((System.currentTimeMillis() - pretime) <= (60 * 60 * 1000) && number < showTimes)) {
            Logger.i(number+"---zz---times---"+showTimes+"---"+(System.currentTimeMillis() - pretime));
            if ((System.currentTimeMillis() - pretime) > (60 * 60 * 1000)) {//超过一小时重置次数
                kv.encode(SpCacheConfig.POP_FULL_LAYER_NUMBERS,0);
            }
            if (NetworkUtils.isNetConnected()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void saveFullInsert(){
        MMKV kv = MMKV.mmkvWithID("switch", MMKV.MULTI_PROCESS_MODE);
        int number = kv.decodeInt(SpCacheConfig.POP_FULL_LAYER_NUMBERS);
        kv.encode(SpCacheConfig.POP_FULL_LAYER_NUMBERS,number+1);
        kv.encode(SpCacheConfig.POP_FULL_LAYER_TIME, String.valueOf(System.currentTimeMillis()));
        Logger.i("zz--zhanshi" + (number + 1));
    }


    public static void saveHaseUpdateVersionMK(boolean isShow) {
        MMKV kv = MMKV.mmkvWithID("update_info", MMKV.MULTI_PROCESS_MODE);
        kv.encode(SpCacheConfig.HASE_UPDATE_VERSION, isShow);
    }


    //外部全屏插屏冲突展示时机判断
    public static boolean isShowFullInsert(){
        //是否在更新
        MMKV kv = MMKV.mmkvWithID("update_info", MMKV.MULTI_PROCESS_MODE);
        boolean isUpdate =  kv.decodeBool(SpCacheConfig.HASE_UPDATE_VERSION);
        return !ActivityCollector.isActivityExistMkv(PopLayerActivity.class) && !ActivityCollector.isActivityExistMkv(LockActivity.class) && !ActivityCollector.isActivityExistMkv(ScreenInsideActivity.class)&& !ActivityCollector.isActivityExistMkv(RedPacketHotActivity.class) && !isUpdate;
    }

    //(插屏开关数据获取)
    public static String getInsertSwitchInfo(){
        MMKV kv = MMKV.mmkvWithID("switch", MMKV.MULTI_PROCESS_MODE);
        return kv.decodeString("insert_ad_switch");
    }

    //(插屏开关数据保存)
    public static void setInsertSwitchInfo(String info){
        MMKV kv = MMKV.mmkvWithID("switch", MMKV.MULTI_PROCESS_MODE);
        kv.encode("insert_ad_switch", info);
    }


    //(总开关数据获取)
    public static String getSwitchInfo(){
        MMKV kv = MMKV.mmkvWithID("switch", MMKV.MULTI_PROCESS_MODE);
        return kv.decodeString("ad_switch");
    }

    //(总开关数据保存)
    public static void setSwitchInfo(String info){
        MMKV kv = MMKV.mmkvWithID("switch", MMKV.MULTI_PROCESS_MODE);
        kv.encode("ad_switch", info);
    }


    //(打底开关数据获取)
    public static String getBottoomAdInfo(){
        MMKV kv = MMKV.mmkvWithID("switch", MMKV.MULTI_PROCESS_MODE);
        return kv.decodeString("ad_bottom");
    }

    //(打底开关数据保存)
    public static void setBottoomAdInfo(String info){
        MMKV kv = MMKV.mmkvWithID("switch", MMKV.MULTI_PROCESS_MODE);
        kv.encode("ad_bottom", info);
    }



}
