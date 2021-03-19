package cn.dreamn.qianji_auto.ui.listData.permissionData;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.ui.listData.IList;


public class PermissionList {
    private static PermissionList mDataList;
    private final List<IList> mList = new ArrayList<>();

    public PermissionList() {

    }

    public synchronized static PermissionList getInstance() {
        if (mDataList == null) {
            mDataList = new PermissionList();
        }
        return mDataList;
    }

    public List<IList> getXposedList()
    {
        mList.clear();
        mList.add(floatData.getInstance());
        mList.add(autostart.getInstance());
        mList.add(smsRead.getInstance());
        return mList;
    }
    public List<IList> getHelperList() {
        mList.clear();

        mList.add(helper.getInstance());
        mList.add(floatData.getInstance());
        mList.add(autostart.getInstance());
        mList.add(smsRead.getInstance());
        mList.add(background.getInstance());
        mList.add(battery.getInstance());
        mList.add(hide.getInstance());
        mList.add(Lock.getInstance());
        return mList;
    }
}
