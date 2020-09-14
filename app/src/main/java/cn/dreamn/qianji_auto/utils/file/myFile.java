package cn.dreamn.qianji_auto.utils.file;

import android.annotation.SuppressLint;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import cn.dreamn.qianji_auto.utils.tools.ZIP;

/**
 * 文件存储读写操作
 */

public class myFile {
    @SuppressLint("SdCardPath")
    private static String sdPath = "/sdcard/Android/data/cn.dreamn.qianji_auto/";//当前的数据路径
    @SuppressLint("SdCardPath")
    private static String backup="/sdcard/Download/QianJiAuto";//默认备份路径
    public static void write(String fileName, String string, boolean rm){
        if(rm){
            del(fileName);//写之前是否需要删除该文件
        }
        makeFilePath(sdPath,fileName);//创建对应的文件
        try {
            String strFilePath=getPath(fileName);
            File file = new File(strFilePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(string.getBytes());
            raf.close();
        } catch (Exception err) {
            Log.i(err.toString());
        }
    }
    //写入文件信息
    private static String getPath(String fileName){
        return sdPath+fileName;
    }
    //创建文件路径
    private static void makeFilePath(String filePath, String fileName) {
        makeRootDirectory(filePath);
        try {
            File file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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
    //删除文件
    public static void del(String fileName) {
        fileName=getPath(fileName);
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
    //读取文件并返回
    public static String get(String fileName){
        try{
            InputStream is = new FileInputStream(getPath(fileName));
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            is.close();
            return new String(bytes);

        }catch(Exception e){
            return "";
        }
    }

    public static void zip(String[] Files,String type){
        makeRootDirectory(backup);
        File[] srcfile = new File[0];
        for (String string : Files){
            File f=new File(sdPath+string+".json");
            srcfile= Arrays.copyOf(srcfile, srcfile.length+1);
            srcfile[srcfile.length-1]=f;

        }
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);
        //压缩后的文件
        @SuppressLint("DefaultLocale") File zipfile=new File(String.format("%s/%s%02d%02d%02d%02d%02d%02d.zip",backup, type,year,month,day,hour, minute, second));
        ZIP.zipFiles(srcfile, zipfile);
        //需要解压缩的文件

    }
    public static void unzip(String filePath){
        File file=new File(filePath);
        //解压后的目标目录
        String dir=sdPath;
        ZIP.unZipFiles(file, dir);
    }

}
