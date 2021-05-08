package cn.dreamn.qianji_auto.ui.listData;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;


import cn.dreamn.qianji_auto.ui.listData.permissionData.PermissionList;

public class ListManager {
    /**
     * 获取自动记账支持的所有APP
     * @return
     */


    public static Bundle[] getPermissionXposedLists() {
        return getList(PermissionList.getInstance().getXposedList());
    }

    public static Bundle[] getPermissionHelperLists() {
        return getList(PermissionList.getInstance().getHelperList());
    }



    public static Bundle[] getList(List<IList> iLists) {
        try {
            List<Bundle> mList = new ArrayList<>();
            for (IList iApp : iLists) {
                Bundle bundle = new Bundle();
                bundle.putString("appName", iApp.getName());
                bundle.putString("appIcon", iApp.getIcon());
                bundle.putString("appSubName", iApp.getSubName());
                bundle.putInt("appColor",iApp.getColor());
                bundle.putInt("appId",iApp.getAppId());
                bundle.putInt("appSize",iApp.getFontSize());
                mList.add(bundle);
            }
            return mList.toArray(new Bundle[0]);
        }catch (Exception ignored){

        }

        return null;

    }





}
