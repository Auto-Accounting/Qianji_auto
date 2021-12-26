package cn.dreamn.qianji_auto.core.hook.hooks.sms.hooks;

import static cn.dreamn.qianji_auto.core.hook.hooks.sms.hooks.PermissionManagerServiceHook30.PermConst.PACKAGE_PERMISSIONS;

import android.Manifest;
import android.os.Build;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class PermissionManagerServiceHook30 {
    // IMPORTANT: There are two types of permissions: install and runtime.

    // Android 11, API 30
    private static final String CLASS_PERMISSION_MANAGER_SERVICE = "com.android.server.pm.permission.PermissionManagerService";

    private static final String CLASS_ANDROID_PACKAGE = "com.android.server.pm.parsing.pkg.AndroidPackage";
    private static final String CLASS_PERMISSION_CALLBACK = "com.android.server.pm.permission.PermissionManagerServiceInternal.PermissionCallback";


    public static void init(Utils utils) {
        hookGrantPermissions(utils);
    }


    private static void hookGrantPermissions(Utils utils) {
        utils.log("Hooking grantPermissions() for Android 30+");
        Method method = findTargetMethod(utils);
        if (method == null) {
            utils.log("Cannot find the method to grant relevant permission");
        }
        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object pkg = param.args[0];

                // final String _packageName = (String) XposedHelpers.getObjectField(pkg, "packageName");
                final String _packageName = (String) XposedHelpers.callMethod(pkg, "getPackageName");

                Set<String> packageSet = PACKAGE_PERMISSIONS.keySet();
                for (String packageName : packageSet) {
                    if (packageName.equals(_packageName)) {
                        utils.log("PackageName: %s", packageName);

                        // PermissionManagerService 对象
                        Object permissionManagerService = param.thisObject;
                        // PackageManagerInternal 对象 mPackageManagerInt
                        Object mPackageManagerInt = XposedHelpers.getObjectField(permissionManagerService, "mPackageManagerInt");

                        // PackageSetting 对象 ps
                        // final PackageSetting ps = (PackageSetting) mPackageManagerInt.getPackageSetting(pkg.getPackageName());
                        final Object ps = XposedHelpers.callMethod(mPackageManagerInt, "getPackageSetting", packageName);

                        // com.android.server.pm.permission.PermissionsState 对象
                        final Object permissionsState = XposedHelpers.callMethod(ps, "getPermissionsState");

                        // Manifest.xml 中声明的permission列表
                        // List<String> requestPermissions = pkg.getRequestPermissions();
                        final List<String> requestedPermissions = (List<String>) XposedHelpers.callMethod(pkg, "getRequestedPermissions");

                        // com.android.server.pm.permission.PermissionSettings mSettings 对象
                        final Object settings = XposedHelpers.getObjectField(permissionManagerService, "mSettings");
                        // ArrayMap<String, com.android.server.pm.permission.BasePermission> mPermissions 对象
                        final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

                        List<String> permissionsToGrant = PACKAGE_PERMISSIONS.get(packageName);
                        for (String permissionToGrant : permissionsToGrant) {
                            if (!requestedPermissions.contains(permissionToGrant)) {
                                boolean granted = (boolean) XposedHelpers.callMethod(
                                        permissionsState, "hasInstallPermission", permissionToGrant);
                                // grant permissions
                                if (!granted) {
                                    // com.android.server.pm.permission.BasePermission bpToGrant
                                    final Object bpToGrant = XposedHelpers.callMethod(permissions, "get", permissionToGrant);
                                    int result = (int) XposedHelpers.callMethod(permissionsState, "grantInstallPermission", bpToGrant);
                                    utils.log("Add " + bpToGrant + "; result = " + result);
                                } else {
                                    utils.log("Already have " + permissionToGrant + " permission");
                                }

                            }
                        }
                    }
                }
            }


        });
    }

    private static Method findTargetMethod(Utils utils) {
        Class<?> pmsClass = XposedHelpers.findClass(CLASS_PERMISSION_MANAGER_SERVICE, utils.getClassLoader());
        Class<?> androidPackageClass = XposedHelpers.findClass(CLASS_ANDROID_PACKAGE, utils.getClassLoader());
        Class<?> callbackClass = XposedHelpers.findClassIfExists(CLASS_PERMISSION_CALLBACK, utils.getClassLoader());

        Method method = XposedHelpers.findMethodExactIfExists(pmsClass, "restorePermissionState",
                /* AndroidPackage pkg   */ androidPackageClass,
                /* boolean replace             */ boolean.class,
                /* String packageOfInterest    */ String.class,
                /* PermissionCallback callback */ callbackClass);

        if (method == null) { // method restorePermissionState() not found
            Method[] _methods = XposedHelpers.findMethodsByExactParameters(pmsClass, Void.TYPE,
                    /* AndroidPackage pkg   */ androidPackageClass,
                    /* boolean replace             */ boolean.class,
                    /* String packageOfInterest    */ String.class,
                    /* PermissionCallback callback */ callbackClass);
            if (_methods != null && _methods.length > 0) {
                method = _methods[0];
            }
        }
        return method;
    }

    public static class PermConst {

        public final static Map<String, List<String>> PACKAGE_PERMISSIONS;

        static {
            PACKAGE_PERMISSIONS = new HashMap<>();

            List<String> smsCodePermissions = new ArrayList<>();

            // Backup import or export
            smsCodePermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            smsCodePermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            smsCodePermissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
            if (Build.VERSION.SDK_INT >= 30) {
                smsCodePermissions.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            }

            String smsCodePackage = BuildConfig.APPLICATION_ID;
            PACKAGE_PERMISSIONS.put(smsCodePackage, smsCodePermissions);

            List<String> phonePermissions = new ArrayList<>();
            // permission for InputManager#injectInputEvent();
            phonePermissions.add("android.permission.INJECT_EVENTS");

            // permission for kill background process - ActivityManagerService#
            phonePermissions.add(Manifest.permission.KILL_BACKGROUND_PROCESSES);

            // READ_SMS for Mark SMS as read & Delete extracted verification SMS
            phonePermissions.add(Manifest.permission.READ_SMS);
            // api version < android M
            phonePermissions.add("android.permission.WRITE_SMS");

            // Permission for grant AppOps permissions
            if (Build.VERSION.SDK_INT >= 28) {
                // Android P
                phonePermissions.add("android.permission.MANAGE_APP_OPS_MODES");
            } else {
                // android 4.4 ~ 8.1
                phonePermissions.add("android.permission.UPDATE_APP_OPS_STATS");
            }

            PACKAGE_PERMISSIONS.put("com.android.phone", phonePermissions);
        }

    }


}
