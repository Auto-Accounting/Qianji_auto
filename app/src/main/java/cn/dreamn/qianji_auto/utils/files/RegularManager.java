package cn.dreamn.qianji_auto.utils.files;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.hjq.toast.ToastUtils;

import cn.dreamn.qianji_auto.database.Helper.identifyRegulars;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.utils.B64;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class RegularManager {
    public static void importReg(Context context, String name, String type) {
        PermissionUtils permissionUtils = new PermissionUtils(context);
        permissionUtils.grant(PermissionUtils.Storage);
        try {

            final DialogProperties properties = new DialogProperties();

            FilePickerDialog dialog = new FilePickerDialog(context, properties);
            dialog.setTitle("请选择自动记账" + name + "识别规则配置文件");
            dialog.setPositiveBtnName("选中");
            dialog.setNegativeBtnName("关闭");
            properties.extensions = new String[]{".auto." + type + ".ankio"};
            properties.root = Environment.getExternalStorageDirectory();
            properties.offset = Environment.getExternalStorageDirectory();
            properties.show_hidden_files = false;
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.error_dir = Environment.getExternalStorageDirectory();
            dialog.setProperties(properties);
            dialog.show();


            dialog.setDialogSelectionListener(new DialogSelectionListener() {
                @Override
                public void onSelectedFilePaths(String[] files) {
                    dialog.dismiss();
                    String file = files[0];
                    String data = FileUtils.get(file);
                    restoreFromData(context, name, type, data);
                }
            });


        } catch (Exception | Error e) {
            ToastUtils.show("出错了，可能是权限未给全！");
            e.printStackTrace();
            Log.i("出错了，可能是权限未给全！" + e.toString());
        }
    }

    public static void restoreFromData(Context context, String name, String type, String data) {
        LoadingDialog loadDialog = new LoadingDialog(context, "数据导入中...");
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                loadDialog.close();
                if (msg.what == -1) {
                    //失败
                    ToastUtils.show("恢复失败！");
                } else {
                    ToastUtils.show("恢复成功！");
                    Tool.restartApp((Activity) context);
                }

            }
        };
        JSONObject jsonObject = JSONObject.parseObject(data);
        String from = jsonObject.getString("from");
        if (!from.equals(type)) {
            ToastUtils.show("该文件不是有效的" + name + "配置数据文件");
            return;
        }
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        BottomSheet bottomSheet2 = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog2 = new MaterialDialog(context, bottomSheet2);
        dialog2.cornerRadius(15f, null);
        dialog2.title(null, "（" + name + "配置）恢复提醒");
        dialog2.message(null, "是否覆盖原有数据（清空不保留）？", null);
        dialog2.negativeButton(null, "不覆盖", (a) -> null);
        dialog2.positiveButton(null, "覆盖", (a) -> {
            identifyRegulars.clear(type);
            return null;
        });
        dialog2.setOnDismissListener(dialog1 -> {
            dialog2.dismiss();
            loadDialog.show();
            Task.onThread(() -> {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    identifyRegulars.add(
                            jsonObject1.getString("regular"),
                            jsonObject1.getString("name"),
                            jsonObject1.getString("text"),
                            jsonObject1.getString("tableList"),
                            jsonObject1.getString("identify"),
                            jsonObject1.getString("fromApp"),
                            jsonObject1.getString("des"),
                            new identifyRegulars.Finish() {
                                @Override
                                public void onFinish() {
                                    Log.d("finish data" + jsonObject1.toString());
                                }
                            });
                }
                Message message = new Message();
                message.what = 1;
                message.obj = "恢复成功！";

                mHandler.sendMessage(message);
            });


        });
        dialog2.show();
    }

    public static void outputReg(Context context, String name, String type, int index) {
        LoadingDialog loadDialog = new LoadingDialog(context, "数据导出中...");
        loadDialog.show();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", type);
        identifyRegulars.getAll(type, null, bundle -> {
            JSONArray jsonArray = new JSONArray();
            for (Bundle regular : bundle) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("name", (regular.getString("name")));
                jsonObject1.put("regular", B64.encode(regular.getString("regular")));
                jsonObject1.put("tableList", B64.encode(regular.getString("tableList")));
                jsonObject1.put("des", (regular.getString("des")));
                jsonObject1.put("fromApp", (regular.getString("fromApp")));
                jsonObject1.put("identify", (regular.getString("identify")));
                jsonObject1.put("text", (regular.getString("text")));
                jsonArray.add(jsonObject1);
            }
            jsonObject.put("data", jsonArray);
            String fileName = Tool.getTime("yyyyMMddHHmmss") + ".auto." + type + ".ankio";
            Tool.writeToCache(context, fileName, jsonObject.toJSONString());
            switch (index) {
                case 0:
                    String newFileName = Environment.getExternalStorageDirectory().getPath() + "/Download/QianJiAuto/" + fileName;
                    FileUtils.makeRootDirectory(Environment.getExternalStorageDirectory().getPath() + "/Download/QianJiAuto/");
                    FileUtils.copyFile(context.getExternalCacheDir().getPath() + "/" + fileName, newFileName);
                    Log.m(fileName);
                    FileUtils.del(fileName);
                    break;
                case 1:
                    Tool.shareFile(context, context.getExternalCacheDir().getPath() + "/" + fileName);
                    FileUtils.del(fileName);
                    break;
            }
            loadDialog.close();
            ToastUtils.show("数据导出成功");

        });
    }

    public static void outputRegOne(Context context, String name, String type, JSONObject js) {
        LoadingDialog loadDialog = new LoadingDialog(context, "数据导出中...");
        loadDialog.show();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", type);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(js);
        jsonObject.put("data", jsonArray);
        String fileName = name + ".auto." + type + ".ankio";
        Tool.writeToCache(context, fileName, jsonObject.toJSONString());
        String newFileName = Environment.getExternalStorageDirectory().getPath() + "/Download/QianJiAuto/" + fileName;
        FileUtils.makeRootDirectory(Environment.getExternalStorageDirectory().getPath() + "/Download/QianJiAuto/");
        FileUtils.copyFile(context.getExternalCacheDir().getPath() + "/" + fileName, newFileName);
        Log.m(fileName);
        FileUtils.del(fileName);
        loadDialog.close();
        ToastUtils.show("数据导出成功");


    }

}
