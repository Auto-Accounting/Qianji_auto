package cn.dreamn.qianji_auto.utils.files;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.thegrizzlylabs.sardineandroid.DavResource;
import com.thegrizzlylabs.sardineandroid.Sardine;
import com.thegrizzlylabs.sardineandroid.impl.OkHttpSardine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import cn.dreamn.qianji_auto.App;
import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.local.FileUtils;
import cn.dreamn.qianji_auto.data.local.ZipUtils;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class BackupManager {
    private static String CACHE_PATH;

    public static void init(Context context) {
        CACHE_PATH = context.getExternalCacheDir().getPath() + "/";
        Log.init("备份恢复");
    }

    @SuppressLint("SdCardPath")
    public static String backUpToCache(Context context) {

        PermissionUtils permissionUtils = new PermissionUtils(context);
        permissionUtils.grant(PermissionUtils.Storage);
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        @SuppressLint("DefaultLocale") String filename = String.format("%s/Qianji_Auto_%02d%02d%02d%02d%02d%02d.auto.backup", CACHE_PATH, year, month, day, hour, minute, second);
        String filename2 = context.getExternalCacheDir().getPath() + "/raw.bp";

        try {
            //压缩到缓存
            ZipUtils.zip("/data/data/" + BuildConfig.APPLICATION_ID + "/", filename2);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", App.getAppVerCode());
            jsonObject.put("name", App.getAppVerName());
            jsonObject.put("package", App.getAppPackage());
            //添加注释
            ZipUtils.zipFile(filename2, filename, jsonObject.toJSONString());
            Log.i("配置已备份到该路径", filename);
            //删掉！
            /*ConsumptionTask task=new ConsumptionTask();
            task.taskNo=String.valueOf(App.index++);
            task.runnable= (context1, task1) -> {
                FileUtils.del(filename2);
                App.lineUpTaskHelp.exOk(task1);
            };
            App.lineUpTaskHelp.addTask(task);*/
            //  FileUtils.del(filename2);
            return filename;
        } catch (Exception e) {
            Log.i("备份出错" + e.toString());
            e.printStackTrace();
        }


        return "";
    }

    public static void backUpToLocal(Context context, Handler mHandler) {
        String fileName = backUpToCache(context);
        if (fileName.equals("")) {
            mHandler.sendEmptyMessage(-1);
            return;
        }
        String newFileName = Environment.getExternalStorageDirectory().getPath() + "/Download/QianJiAuto/" + FileUtils.getFileName(fileName);
        FileUtils.makeRootDirectory(Environment.getExternalStorageDirectory().getPath() + "/Download/QianJiAuto/");
        FileUtils.copyFile(fileName, newFileName);
        Log.i(fileName);
        FileUtils.del(fileName);
        HandlerUtil.send(mHandler, newFileName, 0);
    }

    public static void backUpToWebDav(Context context, String url, String username, String password, Handler mHandler) {
        String fileName = backUpToCache(context);
        if (fileName.equals("")) {
            mHandler.sendEmptyMessage(-1);
            return;
        }
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(username, password);
        url = getUrl(url);
        String dav = url + "/qianji_auto/";
        dav = dav.replace("//", "/");
        try {
            if (!sardine.exists(dav)) {
                sardine.createDirectory(dav);
            }
            InputStream fis = new FileInputStream(new File(fileName));
            int iAvail = fis.available();
            byte[] bytes = new byte[iAvail];
            fis.read(bytes);
            fis.close();
            sardine.put(dav + FileUtils.getFileName(fileName), bytes);
            mHandler.sendEmptyMessage(0);
        } catch (Exception e) {
            Log.d(e.toString());
            mHandler.sendEmptyMessage(-1);
        }
    }


    @SuppressLint("SdCardPath")
    public static String restoreFromCache(Context context, String filePath) {
        try {
            // Log.i("配置恢复文件来源", filePath);

            List<String> comments = ZipUtils.getComments(filePath);

            String comment = comments.get(0);
            if (comment == null) return context.getString(R.string.restore_not_auto);

            JSONObject jsonObject = JSONObject.parseObject(comment);

            if (jsonObject.getInteger("code") < 146) {
                return String.format(context.getString(R.string.restore_not_support), jsonObject.getString("name"));
            }
            String filename2 = context.getExternalCacheDir().getPath();
            List<File> s = ZipUtils.unzipFile(filePath, filename2);
            ZipUtils.unzip(s.get(0).toString(), "/data/data/" + BuildConfig.APPLICATION_ID + "/");
            FileUtils.del(s.get(0).toString());
            FileUtils.del(filePath);
            return "ok";
        } catch (Exception e) {
            Log.i("自动记账恢复备份出错：" + e.toString());
            return context.getString(R.string.restore_error);
        }
    }

    public static void restoreFromLocal(String filePath, Context context, Handler mHandler) {
        String fileName = CACHE_PATH + FileUtils.getFileName(filePath);
        Log.i("原始文件：" + filePath);
        Log.i("文件：" + fileName);
        FileUtils.copyFile(filePath, fileName);//从外部拷贝到内部
        String data = restoreFromCache(context, fileName);

        if (data.equals("ok")) {
            HandlerUtil.send(mHandler, data, 0);
        } else {
            HandlerUtil.send(mHandler, data, -1);
        }
    }

    @SuppressLint("CheckResult")
    public static void restoreFromWebDav(Context context, String url, String username, String password, Handler mHandler) {
        url = getUrl(url);
        Sardine sardine = new OkHttpSardine();
        sardine.setCredentials(username, password);
        String dav = url + "/qianji_auto/";
        dav = dav.replace("//", "/");
        try {
            try {
                if (!sardine.exists(dav)) {
                    sardine.createDirectory(dav);
                }

                List<DavResource> resources;
                resources = sardine.list(dav);//如果是目录一定别忘记在后面加上一个斜杠
                List<String> data = new ArrayList<>();
                for (DavResource res : resources) {
                    if (!res.isDirectory() && res.getName().endsWith(".auto.backup"))
                        data.add(0, res.getName());
                }
                Message message = new Message();
                message.what = 0;
                message.obj = data;
                mHandler.sendMessage(message);
            } catch (IOException e) {
                Log.d(e.toString());
                mHandler.sendEmptyMessage(-1);
            }
        } catch (Exception e) {
            Log.d(e.toString());
            mHandler.sendEmptyMessage(-1);
        }
    }


    public static void restoreFromWebDavByBackground(Context context, String url, String username, String password, String text, Handler mHandler) {
        url = getUrl(url);
        String dav = url + "/qianji_auto/";
        dav = dav.replace("//", "/");
        try {
            Sardine sardine = new OkHttpSardine();
            sardine.setCredentials(username, password);

            InputStream fis = sardine.get(dav + text);

            String fileName = CACHE_PATH + "/" + text;
            File targetfile = new File(fileName);
            OutputStream os;
            os = new FileOutputStream(targetfile);
            int ch;
            while ((ch = fis.read()) != -1) {
                os.write(ch);
            }
            os.flush();

            String data2 = restoreFromCache(context, fileName);

            Message message = new Message();
            message.obj = data2;
            if (data2.equals("ok")) {
                message.what = 2;
            } else {
                message.what = -1;
            }

            mHandler.sendMessage(message);


        } catch (IOException e) {
            Log.d(e.toString());
            mHandler.sendEmptyMessage(-1);
        }
    }

    public static String getUrl(String text) {
        switch (text) {
            case "坚果云":
                return "https://dav.jianguoyun.com/dav/";
            case "城通网盘私有云":
                return "https://webdav.ctfile.com/";
            case "城通网盘公有云":
                return "https://pubdav.ctfile.com/";
            case "TeraCloud":
                return "https://seto.teracloud.jp/dav/";
        }
        return text;
    }
}
