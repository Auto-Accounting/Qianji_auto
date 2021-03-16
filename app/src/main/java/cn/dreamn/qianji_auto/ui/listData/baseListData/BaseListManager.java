package cn.dreamn.qianji_auto.ui.listData.baseListData;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.ui.listData.IList;


public class BaseListManager {
    private static BaseListManager mDataList;
    private final List<IList> mList = new ArrayList<>();

    public BaseListManager() {
        mList.clear();
        mList.add(plugin.getInstance());
        mList.add(cards.getInstance());
        mList.add(map.getInstance());
        mList.add(sort.getInstance());

    }

    public synchronized static BaseListManager getInstance() {
        if (mDataList == null) {
            mDataList = new BaseListManager();
        }
        return mDataList;
    }

    public List<IList> getList() {
        return mList;
    }
}
