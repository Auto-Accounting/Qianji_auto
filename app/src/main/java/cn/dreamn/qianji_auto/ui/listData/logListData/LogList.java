package cn.dreamn.qianji_auto.ui.listData.logListData;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.ui.listData.IList;


public class LogList {
    private static LogList mDataList;
    private final List<IList> mList = new ArrayList<>();

    public LogList() {
        mList.clear();
        mList.add(async.getInstance());
        mList.add(moneyList.getInstance());
        mList.add(notice.getInstance());
        mList.add(sms.getInstance());
        mList.add(appData.getInstance());
        mList.add(logs.getInstance());


    }

    public synchronized static LogList getInstance() {
        if (mDataList == null) {
            mDataList = new LogList();
        }
        return mDataList;
    }

    public List<IList> getList() {
        return mList;
    }
}
