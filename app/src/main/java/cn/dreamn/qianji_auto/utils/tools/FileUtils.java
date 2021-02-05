package cn.dreamn.qianji_auto.utils.tools;

import android.annotation.SuppressLint;
import android.content.Context;

import com.xuexiang.xutil.file.ZipUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import cn.dreamn.qianji_auto.core.utils.App;
import cn.dreamn.qianji_auto.core.utils.DataUtils;


/**
 * 文件存储读写操作
 */

public class FileUtils {

    @SuppressLint("SdCardPath")
    private static final String dataPath="/data/data/cn.dreamn.qianji_auto/";

    //创建根目录
    private static void makeRootDirectory(String filePath) {
        File file;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            android.util.Log.i("error:", e + "");
        }
    }

    //读取文件并返回
    public static String get(String fileName){
        try{
            InputStream is = new FileInputStream(fileName);
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            is.close();
            return new String(bytes);

        }catch(Exception e){
            return "";
        }
    }

    public static void backUp(Context context)  {


        @SuppressLint("SdCardPath") String backUp = "/sdcard/Download/QianJiAuto";
        makeRootDirectory(backUp);


        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        @SuppressLint("DefaultLocale") String filename=String.format("%s/Qianji_Auto_%02d%02d%02d%02d%02d%02d.backup", backUp,year,month,day,hour, minute, second);
        String filename2=context.getExternalCacheDir().getPath()+"/raw.bp";

        //压缩到缓存
        MyZipUtils.zip(dataPath, filename2);


        DataUtils dataUtils=new DataUtils();
        dataUtils.put("code", App.getAppVerCode());
        dataUtils.put("name", App.getAppVerName());

       try{
           //添加注释
           ZipUtils.zipFile(filename2,filename,dataUtils.toString());
           Logs.i("配置已备份到该路径",filename);
           //删掉！
           File file = new File(filename2);
           if (file.isFile() && file.exists()) {
               file.delete();
           }
       }catch (Exception e){
           Logs.i("备份出错"+e.toString());
       }



    }

    public static String restore(String filePath,Context context) {

        try{
            Logs.i("配置恢复文件来源",filePath);

            List<String> comments=ZipUtils.getComments(filePath);

            String comment=comments.get(0);
            if(comment==null)return "该配置文件非自动记账配置文件。";
            DataUtils dataUtils=new DataUtils();
            dataUtils.parse(comment);
            if(Integer.parseInt(dataUtils.get("code"))<43){
                return String.format("该配置文件为低版本自动记账(%s)所备份，无法恢复。",dataUtils.get("name"));
            }
            String filename2=context.getExternalCacheDir().getPath();
            List<File> s=ZipUtils.unzipFile(filePath,filename2);

            MyZipUtils.unzip(s.get(0).toString(), dataPath);
            File file = new File(s.get(0).toString());
            if (file.isFile() && file.exists()) {
                file.delete();

            }
            return "ok";
        }catch (Exception e){
            Logs.i("自动记账恢复备份出错："+e.toString());
            return "发生错误";
        }

    }

    public static String getAssetsData(Context context,String fileName){
        InputStream inputStream;
        try {
            if(context==null)return null;

            inputStream = context.getAssets().open(fileName);
            int size = inputStream.available();
            byte[] bytes = new byte[size];
            inputStream.read(bytes);
            inputStream.close();
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }






}
