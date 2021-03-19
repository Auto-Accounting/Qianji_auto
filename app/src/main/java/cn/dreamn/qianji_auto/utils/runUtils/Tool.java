package cn.dreamn.qianji_auto.utils.runUtils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tool {
    public static void clipboard(Context context, String text) {

        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (manager == null) {
            return;
        }
        manager.setPrimaryClip(ClipData.newPlainText(null, text));
    }

    public static void goUrl(Context context, String url) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri content_url = Uri.parse(url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    public static void goToMarket(String packageName){
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception ignored) {
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime(String s) {
        return (new SimpleDateFormat(s)).format(new Date(System.currentTimeMillis()));
    }

}
