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

package cn.dreamn.qianji_auto.data.database.Helper;

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
import com.hjq.toast.Toaster;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Table.Asset;
import cn.dreamn.qianji_auto.data.database.Table.AssetMap;
import cn.dreamn.qianji_auto.ui.adapter.DataSelectListAdapter;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


public class Assets {

    public static void isInAsset(String string, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            Asset[] assets = Db.db.AssetDao().get(string);
            if (assets == null || assets.length == 0) {
                taskResult.onEnd(false);
            } else taskResult.onEnd(true);
        });
    }

    public static void getQid(String string, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            Asset[] assets = Db.db.AssetDao().get(string);
            if (assets == null || assets.length == 0) {
                taskResult.onEnd("-1");
            } else taskResult.onEnd(assets[0].qid);
        });
    }

    public static void getAllAccount(TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            Asset[] assets = Db.db.AssetDao().getAll(0, 200);
            taskResult.onEnd(assets);
        });
    }


    public static void getPic(String name, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            Asset[] assets = Db.db.AssetDao().get(name);
            if (assets != null && assets.length != 0) {
                taskResult.onEnd(assets[0].icon);
                return;
            }
            taskResult.onEnd("https://pic.dreamn.cn/uPic/2021032022075916162492791616249279427UY2ok6支付.png");
        });
    }

    public static void getAllMap(TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            AssetMap[] assetMaps = Db.db.AssetMapDao().getAll(0, 500);
            if (assetMaps == null || assetMaps.length <= 0) {
                taskResult.onEnd(null);
                return;
            }
            List<Bundle> list = new ArrayList<>();
            for (AssetMap assetMap : assetMaps) {
                Bundle bundle = Tool.class2Bundle(assetMap);
                list.add(bundle);
            }
            taskResult.onEnd(list);
        });
    }


    public static void getMap(String assetName, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            if (assetName == null || assetName.equals("") || assetName.equals("null") || assetName.equals("undefined")) {
                taskResult.onEnd("无账户");
                return;
            }
            // 正则匹配内容  需要在内容中以regex:开头
            AssetMap[] assetsArr = Db.db.AssetMapDao().getAllFromRegex();
            if (assetsArr.length > 0) {
                for (AssetMap item : assetsArr) {
                    // 替换掉 'regex:'
                    String pattern = item.name.replaceFirst("reg:", "");
                    if (pattern.equals("")) return;
                    boolean isMatch = Pattern.matches(pattern, assetName);
                    if (isMatch) {
                        Log.i("资产匹配", String.format("源内容：[%s]，被正则表达式：[%s]，成功匹配。", assetName, pattern));
                        taskResult.onEnd(item.mapName);
                        return;
                    }
                }
            }
            AssetMap[] assetMaps = Db.db.AssetMapDao().get(assetName);
            if (assetMaps.length <= 0) {
                taskResult.onEnd(assetName);
                return;
            }
            String mapName = assetMaps[0].mapName;
            if (mapName == null) {
                taskResult.onEnd("无账户");
            } else taskResult.onEnd(mapName);
        });

    }

    public static void showAssetSelect(Context context, String title, boolean isFloat, boolean add, TaskThread.TaskResult taskResult) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_data, null);

        final ListView list_view = textEntryView.findViewById(R.id.list_view);

        final Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Asset[] assets = (Asset[]) msg.obj;
                int length = 0;
                if (assets != null) {
                    length = assets.length;
                } else {
                    assets = new Asset[0];
                }
                if (add) {
                    Asset[] assets2 = java.util.Arrays.copyOf(assets, length + 1);
                    assets2[length] = new Asset();
                    assets2[length].icon = "https://pic.dreamn.cn/uPic/2021032022075916162492791616249279427UY2ok6支付.png";
                    assets2[length].id = -1;
                    assets2[length].name = "无账户";
                    assets2[length].sort = 0;
                    assets = assets2;
                }

                if (assets.length == 0) {
                    Toaster.show(R.string.no_assets);
                    return;
                }
                DataSelectListAdapter adapter = new DataSelectListAdapter(context, assets);
                list_view.setAdapter(adapter);
                BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
                MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
                if (isFloat) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
                    } else {
                        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                    }
                }
                dialog.title(null, title);
                dialog.cornerRadius(15f, null);
                DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                        false, true, false, false);
                dialog.show();
                Asset[] finalAssets = assets;
                list_view.setOnItemClickListener((parent, view, position, id) -> {
                    taskResult.onEnd(finalAssets[position]);
                    dialog.dismiss();
                });
            }
        };
        TaskThread.onThread(() -> {
            Asset[] assets = Db.db.AssetDao().getAll(0, 200);
            HandlerUtil.send(mHandler, assets, 0);
        });

    }
    public static void showAssetSelect(Context context, String title, boolean isFloat, TaskThread.TaskResult taskResult) {
        showAssetSelect(context, title, isFloat, false, taskResult);

    }

    public static void setSort(int id, int fromPosition) {
        TaskThread.onThread(() -> Db.db.AssetDao().setSort(id, fromPosition));
    }

}
