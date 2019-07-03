package com.xiaoniu.cleanking.ui.main.activity;

import android.os.Environment;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoniu.cleanking.R;
import com.xiaoniu.cleanking.app.injector.component.ActivityComponent;
import com.xiaoniu.cleanking.base.BaseActivity;
import com.xiaoniu.cleanking.ui.main.adapter.InstallPackageManageAdapter;
import com.xiaoniu.cleanking.ui.main.bean.AppInfoBean;
import com.xiaoniu.cleanking.ui.main.fragment.dialog.DelDialogFragment;
import com.xiaoniu.cleanking.ui.main.presenter.CleanInstallPackagePresenter;
import com.xiaoniu.cleanking.utils.FileSizeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 安装包清理
 * Created by lang.chen on 2019/7/2
 */
public class CleanInstallPackageActivity extends BaseActivity<CleanInstallPackagePresenter> implements InstallPackageManageAdapter.OnCheckListener {


    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.txt_install)
    TextView mTxtInstall;
    @BindView(R.id.view_line_intall)
    View mViewLineIntall;
    @BindView(R.id.txt_uninstall)
    TextView mTxtUninstall;
    @BindView(R.id.view_line_uninstall)
    View mViewLineUninstall;
    @BindView(R.id.btn_del)
    Button mBtnDel;
    @BindView(R.id.check_all)
    AppCompatCheckBox mCheckBoxAll;

    //tab类型  0 已安装，1 未安装
    private int mType;
    /**
     *列表选项的 checkbox关联全选，如果是选择关联的路径 is ture ,else false;  如果为true 不做重复操作
     */
    private boolean isRelevancy=false;
    private InstallPackageManageAdapter mAdapter;

    String path = Environment.getExternalStorageDirectory().getPath();

    @Override
    public void inject(ActivityComponent activityComponent) {
        activityComponent.inject(this);
        mPresenter.getApkList(path);
    }

    @Override
    public void netError() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_clean_install_packeage;
    }

    @Override
    protected void initView() {
        setStatusView(mType);

        mAdapter = new InstallPackageManageAdapter(this.getBaseContext());
        LinearLayoutManager mLlManger = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(mLlManger);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setTabType(mType);
        mAdapter.setOnCheckListener(this);

        mCheckBoxAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isRelevancy){
                    checkAll(isChecked);
                }
                //每设置完成就回归原位
                isRelevancy=false;
            }
        });
    }

    @OnClick({R.id.img_back, R.id.txt_install, R.id.txt_uninstall
            , R.id.btn_del})
    public void onViewClick(View view) {
        int id = view.getId();

        if (id == R.id.img_back) {
            finish();
        } else if (id == R.id.txt_install) {
            //已安装应用
            mType = 0;
            setStatusView(mType);
            List<AppInfoBean> lists = mPresenter.getApkList(path, mType);
            mAdapter.clear();
            mAdapter.setTabType(mType);
            mAdapter.modifyList(lists);
        } else if (id == R.id.txt_uninstall) {
            //未安装应用
            mType = 1;
            setStatusView(mType);
            List<AppInfoBean> lists = mPresenter.getApkList(path, mType);
            mAdapter.clear();
            mAdapter.setTabType(mType);
            mAdapter.modifyList(lists);

        } else if (id == R.id.btn_del) { //删除文件

            DelDialogFragment dialogFragment = DelDialogFragment.newInstance();
            dialogFragment.show(getFragmentManager(), "");
            dialogFragment.setDialogClickListener(new DelDialogFragment.DialogClickListener() {
                @Override
                public void onCancel() {

                }

                @Override
                public void onConfirm() {
                    List<AppInfoBean> lists = mAdapter.getLists();
                    //过滤选中的文件删除
                    List<AppInfoBean> appInfoSelect = new ArrayList<>();
                    for (AppInfoBean appInfoBean : lists) {
                        if (appInfoBean.isSelect) {
                            appInfoSelect.add(appInfoBean);
                            //从列表中移除即将要删除文件
                           // lists.remove(appInfoBean);
                        }
                    }
                   mPresenter.delFile(appInfoSelect);

                }
            });
        }

    }


    /**
     * 全选
     *
     * @param isCheck
     */
    private void checkAll(boolean isCheck) {
        List<AppInfoBean> lists = mAdapter.getLists();

        for (AppInfoBean appInfoBean : lists) {
            appInfoBean.isSelect = isCheck;
        }
        mAdapter.notifyDataSetChanged();

    }

    /**
     * 更新app
     *
     * @param appInfoBeans
     */
    public void updateData(List<AppInfoBean> appInfoBeans) {
        mType = 0;
        setStatusView(mType);
        List<AppInfoBean> lists = mPresenter.getApkList(path, mType);
        mAdapter.clear();
        mAdapter.modifyList(lists);

    }


    public void updateDelFileView(List<AppInfoBean> appInfoBeans) {
        totalSelectFiles();
        Toast.makeText(mContext, "成功删除" + FileSizeUtils.formatFileSize(totalSize), Toast.LENGTH_SHORT).show();
        //
        List<AppInfoBean> listsNew=new ArrayList<>();
        List<AppInfoBean> lists=mAdapter.getLists();
        //过滤删除的文件
        for(AppInfoBean appInfoBean: lists){
            boolean isConstain=false;
            for(AppInfoBean appInfoBean1 :appInfoBeans){
                if(appInfoBean.packageName.equals(appInfoBean1.packageName)){
                    isConstain=true;
                }
            }
            //没有包含
            if(isConstain==false){
                listsNew.add(appInfoBean);
            }
        }
        mAdapter.clear();
        mAdapter.modifyList(listsNew);
        //更新缓存
        mPresenter.updateRemoveCache(appInfoBeans);

    }

    private void setStatusView(int type) {
        if (type == 0) {
            if(null!=mTxtInstall){
                mTxtInstall.setTextColor(getResources().getColor(R.color.white));
            }
            if(null!=mViewLineIntall){
                mViewLineIntall.setVisibility(View.VISIBLE);
            }
            if(null!=mViewLineUninstall){
                mViewLineUninstall.setVisibility(View.INVISIBLE);
            }
        } else {
            if(null!=mViewLineIntall){
                mViewLineIntall.setVisibility(View.INVISIBLE);
            }
            if(null!=mViewLineUninstall){
                mViewLineUninstall.setVisibility(View.VISIBLE); }
        }


    }

    /**
     * 每次选择列表选择都去底部大小
     * <p>
     * 1.列表有选择更新底部按钮，并且统计文件大小
     */
    private long totalSize = 0L;

    @Override
    public void onCheck(String id, boolean isChecked) {
        List<AppInfoBean> lists = mAdapter.getLists();
        //文件总大小
        totalSize = 0L;
        boolean isCheckAll = true;
        for (AppInfoBean appInfoBean : lists) {
            if (appInfoBean.packageName.equals(id)) {
                appInfoBean.isSelect = isChecked;
            }
        }

        for (AppInfoBean appInfoBean : lists) {
            if (appInfoBean.isSelect) {
                totalSize += appInfoBean.packageSize;
            } else {
                isCheckAll = false;
            }
        }

        isRelevancy=true;
        mCheckBoxAll.setChecked(isCheckAll);
        if (totalSize > 0) {
            mBtnDel.setText("删除" + FileSizeUtils.formatFileSize(totalSize));
            mBtnDel.setSelected(true);
            mBtnDel.setClickable(true);
        } else {
            mBtnDel.setText("删除");
            mBtnDel.setSelected(false);
            mBtnDel.setClickable(false);
        }


    }

    /**
     * 统计文件的大小
     */
    private  void totalSelectFiles(){
        totalSize=0L;
        List<AppInfoBean> lists = mAdapter.getLists();
        for (AppInfoBean appInfoBean : lists) {
            if (appInfoBean.isSelect) {
                totalSize += appInfoBean.packageSize;
            }
        }
        if (totalSize > 0) {
            mBtnDel.setText("删除" + FileSizeUtils.formatFileSize(totalSize));
            mBtnDel.setSelected(true);
            mBtnDel.setClickable(true);
        } else {
            mBtnDel.setText("删除");
            mBtnDel.setSelected(false);
            mBtnDel.setClickable(false);
        }
    }
}
