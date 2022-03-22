package com.dl.playfun.ui.mine.setredpackagevideo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.dl.playfun.app.AppViewModelFactory;
import com.dl.playfun.ui.base.BaseToolbarFragment;
import com.dl.playfun.BR;
import com.dl.playfun.R;
import com.dl.playfun.databinding.FragmentSetRedPackageVideoBinding;

/**
 * 設置紅包視頻
 *
 * @author wulei
 */
public class SetRedPackageVideoFragment extends BaseToolbarFragment<FragmentSetRedPackageVideoBinding, SetRedPackageVideoViewModel> {

    @Override
    public int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return R.layout.fragment_set_red_package_video;
    }

    @Override
    public void initParam() {
        super.initParam();
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public SetRedPackageVideoViewModel initViewModel() {
        //使用自定义的ViewModelFactory来创建ViewModel，如果不重写该方法，则默认会调用LoginViewModel(@NonNull Application application)构造方法
        AppViewModelFactory factory = AppViewModelFactory.getInstance(mActivity.getApplication());
        return ViewModelProviders.of(this, factory).get(SetRedPackageVideoViewModel.class);
    }

    @Override
    public void initData() {
        super.initData();
    }

}
