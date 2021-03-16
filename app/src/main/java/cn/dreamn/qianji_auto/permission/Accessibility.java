package cn.dreamn.qianji_auto.permission;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;

import java.util.ArrayList;
import java.util.List;

public class Accessibility {

    public static boolean isAccessibilityEnabled(Context context) throws RuntimeException{
        if (context == null) {
            return false;
        }

        // 检查AccessibilityService是否开启
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(android.content.Context.ACCESSIBILITY_SERVICE);
        boolean isAccessibilityEnabled_flag = am.isEnabled();

        boolean isExploreByTouchEnabled_flag = false;

        // 检查无障碍服务是否以语音播报的方式开启
        isExploreByTouchEnabled_flag = isScreenReaderActive(context);

        return (isAccessibilityEnabled_flag && isExploreByTouchEnabled_flag);

    }
    private final static String SCREEN_READER_INTENT_ACTION = "android.accessibilityservice.AccessibilityService";
    private final static String SCREEN_READER_INTENT_CATEGORY = "android.accessibilityservice.category.FEEDBACK_SPOKEN";

    private static boolean isScreenReaderActive(Context context) {

        // 通过Intent方式判断是否存在以语音播报方式提供服务的Service，还需要判断开启状态
        Intent screenReaderIntent = new Intent(SCREEN_READER_INTENT_ACTION);
        screenReaderIntent.addCategory(SCREEN_READER_INTENT_CATEGORY);
        @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> screenReaders = context.getPackageManager().queryIntentServices(screenReaderIntent, 0);
        // 如果没有，返回false
        if (screenReaders == null || screenReaders.size() <= 0) {
            return false;
        }

        boolean hasActiveScreenReader = false;
        if (Build.VERSION.SDK_INT >= 26) {
            // 高版本可以直接判断服务是否处于开启状态
            for (ResolveInfo screenReader : screenReaders) {
                hasActiveScreenReader |= isAccessibilitySettingsOn(context, screenReader.serviceInfo.packageName + "/" + screenReader.serviceInfo.name);
            }

        } else {
            // 判断正在运行的Service里有没有上述存在的Service
            List<String> runningServices = new ArrayList<String>();

            android.app.ActivityManager manager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (android.app.ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                runningServices.add(service.service.getPackageName());
            }

            for (ResolveInfo screenReader : screenReaders) {
                if (runningServices.contains(screenReader.serviceInfo.packageName)) {
                    hasActiveScreenReader = true;
                    break;
                }
            }
        }

        return hasActiveScreenReader;
    }

    // To check if service is enabled
    private static boolean isAccessibilitySettingsOn(Context context, String service) {

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        String settingValue = Settings.Secure.getString(
                context.getApplicationContext().getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (settingValue != null) {
            mStringColonSplitter.setString(settingValue);
            while (mStringColonSplitter.hasNext()) {
                String accessibilityService = mStringColonSplitter.next();
                if (accessibilityService.equalsIgnoreCase(service)) {
                    return true;
                }
            }
        }

        return false;
    }
}
