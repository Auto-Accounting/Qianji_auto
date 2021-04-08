package cn.dreamn.qianji_auto.ui.listData.CustomListData;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.ui.listData.IList;


public class CustomList {
    private static CustomList mDataList;
    private final List<IList> mList = new ArrayList<>();

    public CustomList() {
        mList.clear();
        mList.add(floatCustom.getInstance());
        mList.add(skinCustom.getInstance());

    }

    public synchronized static CustomList getInstance() {
        if (mDataList == null) {
            mDataList = new CustomList();
        }
        return mDataList;
    }

    public List<IList> getList() {
        return mList;
    }
}
