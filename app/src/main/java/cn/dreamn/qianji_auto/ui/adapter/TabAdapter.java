package cn.dreamn.qianji_auto.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

//创建一个内部类作为适配器
public class TabAdapter extends FragmentPagerAdapter {
    private final List<Fragment> list;
    private final String[] table;
    public TabAdapter(FragmentManager fm, List<Fragment> list,String[] table) {
        super(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.list=list;
        this.table=table;
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    //重写getPageTitle()方法，获取页标题
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return table[position];
    }
}
