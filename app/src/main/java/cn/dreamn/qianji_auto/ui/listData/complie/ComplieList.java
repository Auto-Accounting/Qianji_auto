package cn.dreamn.qianji_auto.ui.listData.complie;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.ui.listData.IList;


public class ComplieList {
    private static ComplieList mDataList;
    private final List<IList> mList = new ArrayList<>();

    public ComplieList() {
        mList.clear();
        mList.add(sort.getInstance());
        mList.add(appData.getInstance());
        mList.add(sms.getInstance());
        mList.add(notice.getInstance());


    }

    public synchronized static ComplieList getInstance() {
        if (mDataList == null) {
            mDataList = new ComplieList();
        }
        return mDataList;
    }

    public List<IList> getList() {
        return mList;
    }
}
