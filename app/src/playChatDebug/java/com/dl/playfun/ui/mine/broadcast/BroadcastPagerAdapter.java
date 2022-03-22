package com.dl.playfun.ui.mine.broadcast;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.dl.playfun.R;
import com.dl.playfun.ui.mine.broadcast.myall.MyAllBroadcastFragment;
import com.dl.playfun.ui.mine.broadcast.myprogram.MyprogramFragment;
import com.dl.playfun.ui.mine.broadcast.mytrends.MyTrendsFragment;

public class BroadcastPagerAdapter extends FragmentStatePagerAdapter {

    @StringRes
    public static final int[] TAB_TITLES = new int[]{R.string.tab_broadcast_3,R.string.tab_broadcast_1, R.string.tab_broadcast_2};
    private final Context mContext;

    public BroadcastPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {//全部
            return new MyAllBroadcastFragment();
        }else if (position == 1) {//我的动态
            return new MyTrendsFragment();
        } else {//我的约会
            return new MyprogramFragment();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return TAB_TITLES.length;
    }
}