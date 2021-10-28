package cn.dreamn.qianji_auto.utils.files;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.hjq.toast.ToastUtils;

import java.util.Arrays;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Table.Regular;
import cn.dreamn.qianji_auto.data.local.FileUtils;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class RegularManager {
    public static void importReg(Context context, String name, String type, End end) {
        PermissionUtils permissionUtils = new PermissionUtils(context);
        permissionUtils.grant(PermissionUtils.Storage);
        try {
            final DialogProperties properties = new DialogProperties();
            FilePickerDialog dialog = new FilePickerDialog(context, properties);
            dialog.setTitle(String.format(context.getString(R.string.select_title), name));
            dialog.setPositiveBtnName(context.getString(R.string.select_check));
            dialog.setNegativeBtnName(context.getString(R.string.set_cancle));
            properties.extensions = new String[]{".backup_" + type + "_ankio"};
            properties.root = Environment.getExternalStorageDirectory();
            properties.offset = Environment.getExternalStorageDirectory();
            properties.show_hidden_files = false;
            properties.selection_mode = DialogConfigs.SINGLE_MODE;
            properties.error_dir = Environment.getExternalStorageDirectory();
            dialog.setProperties(properties);
            dialog.show();

            dialog.setDialogSelectionListener(files -> {
                dialog.dismiss();
                String file = files[0];
                String data = FileUtils.get(file);
                restoreFromData(context, name, type, data, end);
            });


        } catch (Exception | Error e) {
            ToastUtils.show(R.string.error_permission);
            e.printStackTrace();
            Log.i("出错了，可能是权限未给全！" + e.toString());
        }
    }

    public static void restoreFromData(Context context, String name, String type, String data, End end) {
        LoadingDialog loadDialog = new LoadingDialog(context, context.getString(R.string.data_loading));
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                loadDialog.close();
                if (msg.what == -1) {
                    //失败
                    ToastUtils.show(R.string.restore_failed);
                } else {
                    end.onFinish(msg.what);
                    ToastUtils.show(R.string.restore_success);
                    //   Tool.restartApp((Activity) context);
                }

            }
        };
        JSONObject jsonObject = JSONObject.parseObject(data);
        String from = jsonObject.getString("from");
        Log.i("当前恢复类型：" + name + "  type:" + type + " from:" + from);
        if (from == null || !from.equals(type)) {
            ToastUtils.show(String.format(context.getString(R.string.set_error), name));
            return;
        }
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        restore(jsonArray, loadDialog, mHandler, context);


    }

    private static void restore(JSONArray array, LoadingDialog loadDialog, Handler mHandler, Context context) {
        Log.i("当前恢复类型：" + array.toJSONString());
        loadDialog.show();
        TaskThread.onThread(() -> {
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject1 = array.getJSONObject(i);
                Db.db.RegularDao().add(
                        jsonObject1.getString("regular"),
                        jsonObject1.getString("name"),
                        jsonObject1.getString("data"),
                        jsonObject1.getString("remark"),
                        jsonObject1.getString("dataId"),
                        jsonObject1.getString("version"),
                        jsonObject1.getString("app"),
                        jsonObject1.getString("type")
                );
            }

            HandlerUtil.send(mHandler, context.getString(R.string.restore_success), 1);
        });

    }


    public static void outputReg(Context context, String name, String type, int index) {
        LoadingDialog loadDialog = new LoadingDialog(context, context.getString(R.string.output));
        loadDialog.show();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", type);
        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                JSONObject jsonObject1 = (JSONObject) msg.obj;
                String fileName = name + "_" + DateUtils.getTime("yyyyMMddHHmmss") + ".backup_" + type + "_ankio";
                Tool.writeToCache(context, fileName, jsonObject1.toJSONString());
                switch (index) {
                    case 0:
                        String newFileName = Environment.getExternalStorageDirectory().getPath() + "/Download/QianJiAuto/" + fileName;
                        FileUtils.makeRootDirectory(Environment.getExternalStorageDirectory().getPath() + "/Download/QianJiAuto/");
                        FileUtils.copyFile(context.getExternalCacheDir().getPath() + "/" + fileName, newFileName);
                        Log.i(fileName);
                        FileUtils.del(fileName);
                        break;
                    case 1:
                        Tool.shareFile(context, context.getExternalCacheDir().getPath() + "/" + fileName);
                        FileUtils.del(fileName);
                        break;
                    case 2:

                        Tool.clipboard(context, jsonObject1.toJSONString());
                        FileUtils.del(fileName);
                        break;
                }
                loadDialog.close();
                ToastUtils.show(R.string.output_success);
            }
        };
        TaskThread.onThread(() -> {
            Regular[] regulars = Db.db.RegularDao().load(type, 0, 500);
            JSONArray jsonArray = new JSONArray();
            for (Regular regular : regulars) {
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("name", regular.name);
                jsonObject1.put("regular", regular.regular);
                jsonObject1.put("data", regular.data);
                jsonObject1.put("remark", regular.remark);
                jsonObject1.put("app", regular.app);
                jsonObject1.put("identify", regular.identify);
                jsonObject1.put("version", regular.version);
                jsonObject1.put("dataId", regular.dataId);
                jsonArray.add(jsonObject1);
            }
            jsonObject.put("data", jsonArray);
            HandlerUtil.send(mHandler, jsonObject, 1);
        });

    }

    public static void outputRegOne(Context context, String name, String type, String dataId, String version, JSONObject js, boolean share) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("from", type);
        jsonObject.put("id", dataId);
        jsonObject.put("version", version);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(js);
        jsonObject.put("data", jsonArray);
        if (share) {
            String fileName = name + "_" + DateUtils.getTime("yyyyMMddHHmmss") + ".backup_" + type + "_ankio";
            Tool.writeToCache(context, fileName, jsonObject.toJSONString());
            Tool.shareFile(context, context.getExternalCacheDir().getPath() + "/" + fileName);
        } else {
            Tool.clipboard(context, jsonObject.toJSONString());
            ToastUtils.show(R.string.output_clipboard);
        }


    }

    public static void outputRegOne(Context context, String name, String type, String dataId, String version, JSONObject js) {
        outputRegOne(context, name, type, dataId, version, js, false);
    }

    public static void output(Context mContext, String name, String type) {
        PermissionUtils permissionUtils = new PermissionUtils(mContext);
        permissionUtils.grant(PermissionUtils.Storage);
        BottomArea.list(mContext, mContext.getString(R.string.share_title), Arrays.asList(mContext.getString(R.string.share_download), mContext.getString(R.string.share_share), mContext.getString(R.string.share_clipboard)), new BottomArea.ListCallback() {
            @Override
            public void onSelect(int position) {
                outputReg(mContext, name, type, position);
            }
        });
    }

    public interface End {
        void onFinish(int code);
    }

}
