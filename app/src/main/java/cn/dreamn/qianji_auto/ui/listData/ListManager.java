package cn.dreamn.qianji_auto.ui.listData;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.CustomListData.CustomList;
import cn.dreamn.qianji_auto.ui.listData.baseListData.BaseListManager;
import cn.dreamn.qianji_auto.ui.listData.complie.ComplieList;
import cn.dreamn.qianji_auto.ui.listData.logListData.LogList;
import cn.dreamn.qianji_auto.ui.listData.otherListData.OtherList;
import cn.dreamn.qianji_auto.ui.listData.permissionData.PermissionList;

public class ListManager {
    /**
     * 获取自动记账支持的所有APP
     * @return
     */
    public static Bundle[] getBaseLists() {
        return getList(BaseListManager.getInstance().getList());
    }

    public static Bundle[] getLogLists(){
        return getList(LogList.getInstance().getList());
    }

    public static Bundle[] getComplieLists(){
        return getList(ComplieList.getInstance().getList());
    }
    public static Bundle[] getOtherLists(){
        return getList(OtherList.getInstance().getList());
    }

    public static Bundle[] getPermissionXposedLists() {
        return getList(PermissionList.getInstance().getXposedList());
    }

    public static Bundle[] getPermissionHelperLists() {
        return getList(PermissionList.getInstance().getHelperList());
    }

    public static Bundle[] geCustomLists() {
        return getList(CustomList.getInstance().getList());
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


    public static void onListClick(int pos, BaseFragment baseFragment,int from){
        List<IList> data;
      switch (from){
          case 1:data=BaseListManager.getInstance().getList();break;
          case 2:data = LogList.getInstance().getList();break;
          case 3:data = ComplieList.getInstance().getList();break;
          case 4:data = OtherList.getInstance().getList();break;
          default:
              throw new IllegalStateException("Unexpected value: " + from);
      }
      if(data!=null){
          data.get(pos).onClick(baseFragment);
      }
    }


}
