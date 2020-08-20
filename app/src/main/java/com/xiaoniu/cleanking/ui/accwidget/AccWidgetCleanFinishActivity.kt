package com.xiaoniu.cleanking.ui.accwidget

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.base.AppHolder
import com.xiaoniu.cleanking.midas.MidasRequesCenter
import com.xiaoniu.cleanking.midas.abs.SimpleViewCallBack
import com.xiaoniu.cleanking.ui.main.config.PositionId
import com.xiaoniu.cleanking.ui.newclean.activity.NowCleanActivity
import com.xiaoniu.cleanking.utils.NumberUtils
import kotlinx.android.synthetic.main.activity_acc_widget_clean_finish_layout.*

/**
 * Created by xinxiaolong on 2020/8/17.
 * email：xinxiaolong123@foxmail.com
 */
class AccWidgetCleanFinishActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc_widget_clean_finish_layout)
        initView()
    }

    fun initView() {
        var memoryLower = NumberUtils.mathRandomInt(10, 30)
        tv_cleaned_memory.text = "释放内存" + memoryLower + "%"

        var storageGarbage = NumberUtils.mathRandomInt(300, 800)
        tv_storage_garbage.text = storageGarbage.toString() + "MB"

        initEvent()
        loadAdv()
    }

    fun initEvent() {
        tv_goCleanStorage.setOnClickListener({ goToCleanStorage() })
        scene_close.setOnClickListener({ finish() })
    }

    fun goToCleanStorage() {
        var intent = Intent()
        intent.setClass(this, NowCleanActivity::class.java)
        startActivity(intent)

        finish()
    }

    fun loadAdv() {
        var isOpenOne = AppHolder.getInstance().checkAdSwitch(PositionId.KEY_AD_PAGE_WIDGET_ACC_FINISH, PositionId.DRAW_ONE_CODE)
        //todo 调试时，开关先打开
        isOpenOne=true
        if(!isOpenOne){
            return
        }

        //todo 这里需要对广告id进行替换
        //var adId=AppHolder.getInstance().getMidasAdId(PositionId.KEY_AD_PAGE_WIDGET_ACC_FINISH, PositionId.DRAW_ONE_CODE)
        var adId = AppHolder.getInstance().getMidasAdId(PositionId.KEY_AD_PAGE_FINISH, PositionId.DRAW_ONE_CODE)
        MidasRequesCenter.requestAndShowAd(this, adId, object : SimpleViewCallBack(findViewById(R.id.ad_container)) {

        })
    }
}