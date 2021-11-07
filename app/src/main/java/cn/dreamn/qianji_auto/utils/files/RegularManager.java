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

                if (jsonObject1.containsKey("tableList")) {
                    //老版本转换
                    jsonObject1 = convert(jsonObject1);
                }

                String dataId = jsonObject1.getString("dataId");
                Regular[] regular = null;
                if (!dataId.equals(""))
                    regular = Db.db.RegularDao().getByDataId(dataId);

                if (!dataId.equals("") && regular != null && regular.length != 0) {
                    Db.db.RegularDao().update(
                            regular[0].id,
                            jsonObject1.getString("regular"),
                            jsonObject1.getString("name"),
                            jsonObject1.getString("data"),
                            jsonObject1.getString("remark"),
                            jsonObject1.getString("dataId"),
                            jsonObject1.getString("version"),
                            jsonObject1.getString("app"),
                            jsonObject1.getString("identify")
                    );
                } else {
                    Db.db.RegularDao().add(
                            jsonObject1.getString("regular"),
                            jsonObject1.getString("name"),
                            jsonObject1.getString("data"),
                            jsonObject1.getString("remark"),
                            jsonObject1.getString("dataId"),
                            jsonObject1.getString("version"),
                            jsonObject1.getString("app"),
                            jsonObject1.getString("identify")
                    );
                }


            }

            HandlerUtil.send(mHandler, context.getString(R.string.restore_success), 1);
        });

    }


    public static JSONObject convert(JSONObject jsonObject) {
        JSONObject jsonObject1 = new JSONObject();

        String string = jsonObject.getString("tableList");
        String regular = jsonObject.getString("regular");
        JSONObject jsonObject3 = new JSONObject();
        if (!string.equals("")) {
            JSONObject jsonObject2 = JSONObject.parseObject(string);
            if (jsonObject2.containsKey("shopRemark")) {
                jsonObject3.put("id", "");
                jsonObject3.put("dataId", "");
                jsonObject3.put("identify", jsonObject.getString("identify"));
                jsonObject3.put("version", "0");
                jsonObject3.put("regular_name", jsonObject.getString("name"));
                jsonObject3.put("regular_remark", jsonObject.getString("des"));
                jsonObject3.put("regular_app", jsonObject.getString("fromApp"));
                jsonObject3.put("regex_input", jsonObject.getString("regular"));
                jsonObject3.put("str_input", jsonObject.getString("text"));
                jsonObject3.put("type", jsonObject2.getString("type"));
                jsonObject3.put("account_name1", jsonObject2.getString("account1"));
                jsonObject3.put("account_name2", jsonObject2.getString("account2"));
                jsonObject3.put("money", jsonObject2.getString("money"));
                jsonObject3.put("fee", jsonObject2.getString("fee"));
                jsonObject3.put("shopName", jsonObject2.getString("shopName"));
                jsonObject3.put("shopRemark", jsonObject2.getString("shopRemark"));
                jsonObject3.put("time", jsonObject2.getString("time"));
                jsonObject3.put("auto", "0");

                String reg = ";try{pattern=/%s/;if(pattern.test(a)){var array=pattern.exec(a);var remark='%s',account='%s',type='%s',money='%s',shopName='%s',account2='%s',fee='%s',time='%s';for(var i=array.length-1;i>=1;i--){var rep=\"$\"+i.toString();var repStr=array[i];remark=remark.replace(rep,repStr);account=account.replace(rep,repStr);type=type.replace(rep,repStr);money=money.replace(rep,repStr);shopName=shopName.replace(rep,repStr);account2=account2.replace(rep,repStr);fee=fee.replace(rep,repStr);time=time.replace(rep,repStr)}return remark+'##'+account+'##'+type+'##'+money+'##'+account2+'##'+shopName+'##'+fee+'##'+time+'##%s'}}catch(e){console.log(e)};";

                regular = String.format(reg, jsonObject.getString("regular"), jsonObject3.getString("shopRemark"), jsonObject3.getString("account_name1"), jsonObject3.getString("type"), jsonObject3.getString("money"), jsonObject3.getString("shopName"), jsonObject3.getString("account_name2"), jsonObject3.getString("fee"), jsonObject3.getString("time"), jsonObject3.getString("auto"));

            } else {

                jsonObject3.put("id", "");
                jsonObject3.put("dataId", "");
                jsonObject3.put("version", "0");
                jsonObject3.put("regular_name", jsonObject2.getString("regular_name"));
                jsonObject3.put("regular_remark", jsonObject2.getString("regular_remark"));
                jsonObject3.put("regular_time1", jsonObject2.getString("regular_time1"));
                jsonObject3.put("regular_time2", jsonObject2.getString("regular_time2"));
                jsonObject3.put("regular_money1_link", jsonObject2.getString("regular_money1_link"));
                jsonObject3.put("regular_money1", jsonObject2.getString("regular_money1"));
                jsonObject3.put("regular_money2_link", jsonObject2.getString("regular_money2_link"));
                jsonObject3.put("regular_money2", jsonObject2.getString("regular_money2"));
                jsonObject3.put("regular_shopName_link", jsonObject2.getString("regular_shopName_link"));
                jsonObject3.put("regular_shopName", jsonObject2.getString("regular_shopName"));
                jsonObject3.put("regular_shopRemark_link", jsonObject2.getString("regular_shopRemark_link"));
                jsonObject3.put("regular_shopRemark", jsonObject2.getString("regular_shopRemark"));
                jsonObject3.put("regular_type", jsonObject2.getString("regular_type"));
                jsonObject3.put("regular_sort", jsonObject2.getString("regular_sort"));
                jsonObject3.put("iconImg", "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png");
            }
        } else {
            jsonObject3.put("id", "");
            jsonObject3.put("dataId", "");
            jsonObject3.put("version", "0");
            jsonObject3.put("regular_name", jsonObject.getString("name"));
            jsonObject3.put("regular_remark", jsonObject.getString("des"));
            jsonObject3.put("auto_wrap", "false");
            jsonObject3.put("code", jsonObject.getString("regular"));

        }

        jsonObject1.put("data", jsonObject3.toJSONString());

        jsonObject1.put("remark", jsonObject.getString("des"));


        jsonObject1.put("regular", regular);
        jsonObject1.put("name", jsonObject.getString("name"));

        jsonObject1.put("version", "0");
        jsonObject1.put("dataId", Tool.getRandomString(32));
        String s1 = jsonObject.getString("fromApp");
        if (s1 == null)
            s1 = "";
        jsonObject1.put("app", s1);
        String s = jsonObject.getString("identify");
        if (s == null)
            s = "category";
        jsonObject1.put("identify", s);
        return jsonObject1;
    }

    public static void outputReg(Context context, String name, String type, int index, String app) {
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
            Regular[] regulars;
            if (app != null) {
                regulars = Db.db.RegularDao().load(type, app, 0, 200);
            } else {
                regulars = Db.db.RegularDao().load(type, 0, 200);
            }
            JSONArray jsonArray = new JSONArray();
            for (Regular regular : regulars) {
                jsonArray.add(Tool.class2JSONObject(regular));
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

    public static void output(Context mContext, String name, String type, String app) {
        PermissionUtils permissionUtils = new PermissionUtils(mContext);
        permissionUtils.grant(PermissionUtils.Storage);
        BottomArea.list(mContext, mContext.getString(R.string.share_title), Arrays.asList(mContext.getString(R.string.share_download), mContext.getString(R.string.share_share), mContext.getString(R.string.share_clipboard)), new BottomArea.ListCallback() {
            @Override
            public void onSelect(int position) {
                outputReg(mContext, name, type, position, app);
            }
        });
    }

    public interface End {
        void onFinish(int code);
    }

}
