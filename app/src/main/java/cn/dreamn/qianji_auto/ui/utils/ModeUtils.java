package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import androidx.cardview.widget.CardView;

import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.adapter.ListAdapter;
import cn.dreamn.qianji_auto.ui.adapter.ModeAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.listData.ListManager;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class ModeUtils {
    private final BaseFragment baseFragment;
    private final ListView lv_permission;
    private final GridView mode_list;

    public ModeUtils(BaseFragment baseFragment1, GridView mode_list1, ListView lv_permission1) {
        baseFragment = baseFragment1;
        mode_list = mode_list1;
        lv_permission = lv_permission1;
    }

    public interface onModeSet {
        void onSet();

        void onPermission();
    }

    public void setMode(onModeSet setModeEnd){
        Bundle[] bundles= new Bundle[2];
        Bundle bundle=new Bundle();
        bundle.putString("appIcon","&#xe65c;");
        bundle.putString("appName","Xposed模式");
        bundle.putString("appValue","xposed");
        bundles[0]=bundle;
        Bundle bundle2=new Bundle();
        bundle2.putString("appIcon","&#xe668;");
        bundle2.putString("appName","无障碍模式");
        bundle2.putString("appValue","helper");
        bundles[1]=bundle2;

        int width= ScreenUtils.getScreenWidth(baseFragment.getActivity());
        width=ScreenUtils.px2dip(baseFragment.getContext(),width);
        width=width-40;//真实可分配大小
        mode_list.setColumnWidth(ScreenUtils.dip2px(baseFragment.getContext(),(float) width/2));
        mode_list.setNumColumns(2);
        MMKV mmkv=MMKV.defaultMMKV();

        List<CardView> cardViews=new ArrayList<>();
        ModeAdapter modeAdapter=new ModeAdapter(baseFragment.getContext(), R.layout.adapter_grid_item, bundles, (item, cardView) -> {
            if(item!=null&&item.equals(mmkv.getString("helper_choose","xposed"))){
                if(cardView==null)
                    return;
                cardView.setCardElevation(15);
                if(!cardViews.contains(cardView))
                    cardViews.add(cardView);
                setModeEnd.onSet();
               // ButtonUtils.enable(button_next,baseFragment.getContext());
            }
        });
        mode_list.setAdapter(modeAdapter);


        mode_list.setOnItemClickListener((parent, view, position, id) -> {
            String appName=bundles[position].getString("appName");
            mmkv.encode("helper_choose",(appName.equals("无障碍模式") ?"helper":"xposed"));
            CardView cardView= view.findViewById(R.id.card_shadow);

            for(int i=0;i<cardViews.size();i++){
                cardViews.get(i).setCardElevation(0);
            }
            if(!cardViews.contains(cardView))
                cardViews.add(cardView);
            cardView.setCardElevation(15);
           //
            setModeEnd.onSet();
            //ButtonUtils.enable(button_next,getContext());
           setPermission(mmkv.getString("helper_choose","xposed"),setModeEnd);
        });
    }

    public static void setListViewHeight(ListView listView, Context context) {
        ListAdapter listAdapter = (ListAdapter) listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            Log.m("mode", i + "个");
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(1, 1);
            totalHeight += listItem.getMeasuredHeight();

        }
        totalHeight=ScreenUtils.px2dip(context,totalHeight);
      //  Log.d("mode",totalHeight+"高度");
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + listView.getPaddingTop() + listView.getPaddingBottom();
        listView.setLayoutParams(params);
    }
    public void setPermission(String mode,onModeSet set){
        Bundle[] list;
        if(mode.equals("xposed")){
            list = ListManager.getPermissionXposedLists();
        }else{
            list = ListManager.getPermissionHelperLists();
        }
     //   Log.d("mode",mode);

        ListAdapter listAdapter=new ListAdapter(baseFragment.getContext(),R.layout.components_supertext, list);
        lv_permission.setAdapter(listAdapter);
        lv_permission.setOnItemClickListener((parent, view, position, id) -> {
            PermissionUtils permissionUtils=new PermissionUtils(baseFragment.getContext());
            permissionUtils.grant(list[position].getInt("appId"));
        });
        set.onPermission();
    }

}
