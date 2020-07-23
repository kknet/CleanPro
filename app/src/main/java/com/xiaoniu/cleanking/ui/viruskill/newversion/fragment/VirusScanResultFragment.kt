package com.xiaoniu.cleanking.ui.viruskill.newversion.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.jess.arms.base.SimpleFragment
import com.jess.arms.di.component.AppComponent
import com.xiaoniu.cleanking.R
import com.xiaoniu.cleanking.ui.viruskill.newversion.ITransferPagePerformer
import com.xiaoniu.cleanking.ui.viruskill.newversion.fragment.VirusScanResultFragment.IntentKey.N_LIST
import com.xiaoniu.cleanking.ui.viruskill.newversion.fragment.VirusScanResultFragment.IntentKey.P_LIST
import com.xiaoniu.cleanking.ui.viruskill.newversion.model.ScanTextItemModel
import kotlinx.android.synthetic.main.fragment_virus_scan_result_layout.*

/**
 * Created by xinxiaolong on 2020/7/23.
 * email：xinxiaolong123@foxmail.com
 */
class VirusScanResultFragment : SimpleFragment() {

    lateinit var transfer: ITransferPagePerformer
    lateinit var pList: ArrayList<ScanTextItemModel>
    lateinit var nList: ArrayList<ScanTextItemModel>

    object IntentKey {
        const val P_LIST = "P_LIST"
        const val N_LIST = "N_LIST"
    }

    override fun setupFragmentComponent(appComponent: AppComponent) {

    }

    override fun setData(data: Any?) {

    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_virus_scan_result_layout
    }


    override fun initData(savedInstanceState: Bundle?) {
        pList = arguments!!.getParcelableArrayList(P_LIST)
        nList = arguments!!.getParcelableArrayList(N_LIST)
        initView()
        initEvent();
    }

    private fun initView() {
        var pRiskNum = pList.size;
        var nRiskNum = nList.size;
        var sumNum = pRiskNum + nRiskNum;

        var text = "发现 " + sumNum + " 项严重问题"
        tv_virus_result_title.setText(biggerText(text, sumNum.toString()))

        var pText = pRiskNum.toString() + " 项隐私风险"
        tv_virus_result_risk_count_privacy.setText(biggerText(pText, pRiskNum.toString()))

        var nText = nRiskNum.toString() + " 项网络风险"
        tv_virus_result_risk_count_network.setText(biggerText(nText, nRiskNum.toString()))

        initPItemView()
        initNItemView()
    }

    private fun initEvent() {
        btn_clear_virus_result.setOnClickListener({
            transfer.onTransferCleanPage(pList, nList)
        })
    }

    private fun initPItemView() {
        for (mode in pList) {
            var view = LayoutInflater.from(mContext).inflate(R.layout.item_virus_scan_result_datils_text_layout, null);
            var textView = view.findViewById<TextView>(R.id.tv_virus_result_item)
            textView.setText(addPointerHead(mode.name))
            linear_virus_result_risk_detail_privacy.addView(view)
        }
    }

    private fun initNItemView() {
        for (mode in nList) {
            var view = LayoutInflater.from(mContext).inflate(R.layout.item_virus_scan_result_datils_text_layout, null);
            var textView = view.findViewById<TextView>(R.id.tv_virus_result_item)
            textView.setText(addPointerHead(mode.name))
            linear_virus_result_risk_detail_network.addView(view)
        }
    }

    private fun biggerText(text: String, bigText: String): SpannableString {
        var sp = SpannableString(text)
        var start = text.indexOf(bigText)
        var end = start + bigText.length
        var size = AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.dp_24))
        sp.setSpan(size, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sp
    }

    private fun addPointerHead(text: String): SpannableString {
        var sp = SpannableString("* " + text)
        var sColor = ForegroundColorSpan(resources.getColor(R.color.home_content_red))
        sp.setSpan(sColor, 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        return sp
    }

    fun setTransferPagePerformer(transfer: ITransferPagePerformer) {
        this.transfer = transfer
    }

}
