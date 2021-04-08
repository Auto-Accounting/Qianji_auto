package cn.dreamn.qianji_auto.ui.listData.otherListData;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.ui.listData.IList;


public class OtherList {
    private static OtherList mDataList;
    private final List<IList> mList = new ArrayList<>();

    public OtherList() {
        mList.clear();
        mList.add(backup.getInstance());
        mList.add(teach.getInstance());
        mList.add(videoTeach.getInstance());
        mList.add(github.getInstance());
        mList.add(about.getInstance());


    }

    public synchronized static OtherList getInstance() {
        if (mDataList == null) {
            mDataList = new OtherList();
        }
        return mDataList;
    }

    public List<IList> getList() {
        return mList;
    }
}
