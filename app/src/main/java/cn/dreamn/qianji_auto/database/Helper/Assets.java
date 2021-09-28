/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.dreamn.qianji_auto.database.Helper;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.hjq.toast.ToastUtils;

import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.database.Table.Asset;
import cn.dreamn.qianji_auto.database.Table.Asset2;
import cn.dreamn.qianji_auto.ui.adapter.DataSelectListAdapter;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;


public class Assets {
    public interface getAssets2{
        void onGet(Asset2[] asset2s);
    }
    public interface getAssets{
        void onGet(Bundle[] asset2s);
    }
    public interface getAssetOne{
        void onGet(Bundle asset2s);
    }
    public interface getAssets2Strings{
        void onGet(String[] asset2s);
    }

    public interface getAssets2String {
        void onGet(String asset2s);
    }

    public interface getAssets2Bundle {
        void onGet(Bundle[] asset2s);
    }

    public interface whenFinish {
        void onFinish();
    }

    public static void isInAsset2(String string, isIn get) {
        Task.onThread(() -> {
            Asset2[] asset2s = DbManger.db.Asset2Dao().get(string);
            if (asset2s == null || asset2s.length == 0) {
                get.in(false);
            }
            get.in(true);
        });
    }

    public static void getAllAccount(getAssets2 getAsset) {
        Task.onThread(() -> getAsset.onGet(DbManger.db.Asset2Dao().getAll()));
    }

    public static void getAllAccountName(getAssets2Strings getAssets) {
        Task.onThread(() -> {
            Asset2[] assets = DbManger.db.Asset2Dao().getAll();
            if (assets.length <= 0) {
                getAssets.onGet(null);
                return;
            }
            String[] result = new String[assets.length];
            for (int i = 0; i < assets.length; i++) {
                result[i] = assets[i].name;
            }
            getAssets.onGet(result);
        });
    }

    public static void getAllIcon(getAssets2Bundle getAsset) {
        Task.onThread(()-> {
        Asset2[] assets =  DbManger.db.Asset2Dao().getAll();
        if (assets==null||assets.length <= 0){
            getAsset.onGet(null);
            return;
        }

        ArrayList<Bundle> bundleArrayList = new ArrayList<>();
            for (Asset2 asset : assets) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", asset.id);
                bundle.putString("name", asset.name);
                bundle.putString("icon", asset.icon);
                bundleArrayList.add(bundle);
            }
            getAsset.onGet(bundleArrayList.toArray(new Bundle[0]));
        });
    }

    public static void addAsset(String assetName, whenFinish finish) {
        Task.onThread(() -> {

            DbManger.db.Asset2Dao().add(assetName);
            finish.onFinish();
        });
    }

    public static void getPic(String name, getAssets2String getAssets) {
        Task.onThread(() -> {
            Asset2[] asset2s = DbManger.db.Asset2Dao().get(name);
            if (asset2s != null && asset2s.length != 0) {
                getAssets.onGet(asset2s[0].icon);
                return;
            }
            getAssets.onGet("https://pic.dreamn.cn/uPic/2021032022075916162492791616249279427UY2ok6支付.png");
        });
    }

    public static void getMap(getAssets2Strings getAssets) {
        Task.onThread(()-> {
        Asset[] assets =  DbManger.db.AssetDao().getAll();
        if (assets==null||assets.length <= 0) {
            getAssets.onGet(null);
            return;
        }
        String[] result = new String[assets.length];
        for (int i = 0; i < assets.length; i++) {
            result[i] = assets[i].mapName;
        }
            getAssets.onGet(result);
        });
    }

    public static void getAllMap(getAssets getAsset) {
        Task.onThread(()-> {
            Asset[] asset=DbManger.db.AssetDao().getAll();
            if (asset==null||asset.length <= 0){
                getAsset.onGet(null);
                return;
            }

            ArrayList<Bundle> bundleArrayList = new ArrayList<>();
            for (Asset asset1 : asset) {
                Bundle bundle = new Bundle();
                bundle.putInt("id", asset1.id);
                bundle.putString("name", asset1.name);
                bundle.putString("mapName", asset1.mapName);
                bundleArrayList.add(bundle);
            }
            getAsset.onGet(bundleArrayList.toArray(new Bundle[0]));
        });
    }

    public static void delAsset(int id,whenFinish when) {
        Task.onThread(()->{
            DbManger.db.Asset2Dao().del(id);
            when.onFinish();
        });
    }

    public static void addMap(String assetName, String mapName, whenFinish when) {
        Log.i("添加映射：" + assetName + "->" + mapName);
        Task.onThread(() -> {
            DbManger.db.AssetDao().del(assetName);//删掉已有的，再添加没有的
            DbManger.db.AssetDao().add(assetName, mapName);
            when.onFinish();
        });
    }

    public static void addAsset(String assetName, String icon, int sort,whenFinish finish) {
        Task.onThread(()-> {
            DbManger.db.Asset2Dao().add(assetName, icon, sort);
            finish.onFinish();
        });
    }

    public static void cleanAsset() {
        Task.onThread(()-> DbManger.db.Asset2Dao().clean());
    }

    public static void updAsset(int id, String assetName,whenFinish when) {
        Task.onThread(()-> {
            DbManger.db.Asset2Dao().update(id, assetName);
            when.onFinish();
        });
    }

    public static void delMap(int id,whenFinish when) {
        Task.onThread(()->{
            DbManger.db.AssetDao().del(id);
            when.onFinish();
        });
    }

    public static void getMap(String assetName,getAssets2String getAssets) {
        Task.onThread(() -> {
            if (assetName == null || assetName.equals("")) {
                getAssets.onGet("");
                return;
            }
            // 正则匹配内容  需要在内容中以regex:开头
            Asset[] assetsArr = DbManger.db.AssetDao().getAllFromRegex();
            if (assetsArr.length > 0) {
                for (Asset item : assetsArr) {
                    // 替换掉 'regex:'
                    String pattern = item.name.replaceFirst("reg:", "");
                    if (pattern.equals("")) return;
                    boolean isMatch = Pattern.matches(pattern, assetName);
                    if (isMatch) {
                        Log.i("资产匹配", String.format("源内容：[%s]，被正则表达式：[%s]，成功匹配。", assetName, pattern));
                        getAssets.onGet(item.mapName);
                        return;
                    }
                }
            }
            Asset[] assets = DbManger.db.AssetDao().get(assetName);
            //没有资产创造资产
            if (assets.length <= 0) {
                //  DbManger.db.AssetDao().add(assetName, assetName);
                getAssets.onGet(assetName);
                return;
            }
            String mapName = assets[0].mapName;
            if (mapName == null) {
                getAssets.onGet(assetName);
            } else getAssets.onGet(mapName);
        });

    }

    public static void updMap(int id, String assetName, String mapName) {
        Task.onThread(() -> DbManger.db.AssetDao().update(id, assetName, mapName));
    }

    public static void showAssetSelect(Context context, String title, boolean isFloat, getAssetOne getOne) {

        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_data, null);

        //final TextView list_title = textEntryView.findViewById(R.id.list_title);

        final ListView list_view = textEntryView.findViewById(R.id.list_view);

        final Handler mHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                Bundle[] assets=(Bundle[])msg.obj;
                if(assets==null){
                    ToastUtils.show(R.string.no_assets);
                    return;
                }
                DataSelectListAdapter adapter = new DataSelectListAdapter(context,assets);//listdata和str均可
                list_view.setAdapter(adapter);

                BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
                MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
                if(isFloat){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
                    } else {
                        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                    }
                }
                dialog.title(null,title);
                dialog.cornerRadius(15f,null);
                DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                        false, true, false, false);
                dialog.show();


                list_view.setOnItemClickListener((parent, view, position, id) -> {
                    getOne.onGet(assets[position]);
                    dialog.dismiss();
                });
            }
        };

        getAllIcon(asset2s -> {
            Message message = new Message();
            message.obj = asset2s;
            mHandler.sendMessage(message);
        });


    }

    public static void setSort(int id, int fromPosition) {
        Task.onThread(() -> DbManger.db.Asset2Dao().setSort(id, fromPosition));
    }

    public interface isIn {
        void in(boolean isIn);
    }
}
