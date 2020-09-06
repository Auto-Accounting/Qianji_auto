package cn.dreamn.qianji_auto.utils.tools;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * Mobile Info Utils
 * create by heliquan at 2017年3月23日
 */
public class mobileInfoUtil {

    /**
     * Get Mobile Type
     *
     * @return
     */
    private static String getMobileType() {
        return Build.MANUFACTURER;
    }

    /**
     * GoTo Open Self Setting Layout
     * Compatible Mainstream Models 兼容市面主流机型
     * adb shell dumpsys activity top
     *
     * @param context
     */
    public static void jumpStartInterface(Context context) {
        Intent intent = new Intent();
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("HLQ_Struggle", "******************当前手机型号为：" + getMobileType());
            ComponentName componentName = null;
            switch (getMobileType()) {
                case "Xiaomi":  // 红米Note4测试通过
                    componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
                    break;
                case "Letv":  // 乐视2测试通过
                    intent.setAction("com.letv.android.permissionautoboot");
                    break;
                case "samsung":  // 三星Note5测试通过
                    componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.ram.AutoRunActivity");
                    break;
                case "HUAWEI":  // 华为测试通过
                    componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
                    break;
                case "vivo":  // VIVO测试通过
                    componentName = ComponentName.unflattenFromString("com.iqoo.secure/.safeguard.PurviewTabActivity");
                    break;
                case "Meizu":  //万恶的魅族
                    componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity");
                    break;
                case "OPPO":  // OPPO R8205测试通过
                    componentName = ComponentName.unflattenFromString("com.oppo.safe/.permission.startup.StartupAppListActivity");
                    break;
                case "ulong":  // 360手机 未测试
                    componentName = new ComponentName("com.yulong.android.coolsafe", ".ui.activity.autorun.AutoRunListActivity");
                    break;
                default:
                    // 将用户引导到系统设置页面
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                    break;
            }
            intent.setComponent(componentName);
            context.startActivity(intent);
        } catch (Exception e) {//抛出异常就直接打开设置页面
            intent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(intent);
        }
    }

}