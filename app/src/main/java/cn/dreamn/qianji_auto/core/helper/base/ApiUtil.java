package cn.dreamn.qianji_auto.core.helper.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * api 内部需要用到的类
 */
public class ApiUtil {

    /**
     * 判断指定的应用的辅助功能是否开启,
     *
     * @param context 上下文
     * @param
     * @return 是否开启
     */
    public static boolean isAccessibilityServiceOn(@NonNull Context context, Class cls) {
        int ok = 0;
        String serName = context.getPackageName() + "/" + cls.getCanonicalName();

        try {
            ok = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {
        }

        TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
        if (ok == 1) {
            String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                ms.setString(settingValue);
                while (ms.hasNext()) {
                    String accessibilityService = ms.next();
                    if (accessibilityService.equalsIgnoreCase(serName)) {
                        return true;
                    }

                }
            }
        }

        return false;
    }


    /**
     * 触发系统rebind通知监听服务
     *
     * @param context      上下文
     * @param serviceClass 辅助功能服务的类
     */
    public static void rebindAccessibilityService(Context context, Class<?> serviceClass) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(
                new ComponentName(context, serviceClass),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
        );
        pm.setComponentEnabledSetting(
                new ComponentName(context, serviceClass),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );
    }

}
