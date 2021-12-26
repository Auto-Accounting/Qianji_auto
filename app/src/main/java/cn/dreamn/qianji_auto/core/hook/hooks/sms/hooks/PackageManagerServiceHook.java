package cn.dreamn.qianji_auto.core.hook.hooks.sms.hooks;

import static cn.dreamn.qianji_auto.core.hook.hooks.sms.hooks.PermissionManagerServiceHook30.PermConst.PACKAGE_PERMISSIONS;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class PackageManagerServiceHook {
    private static final String CLASS_PACKAGE_MANAGER_SERVICE = "com.android.server.pm.PackageManagerService";
    private static final String CLASS_PACKAGE_PARSER_PACKAGE = "android.content.pm.PackageParser.Package";

    public static void init(Utils utils) {
        try {
            hookGrantPermissionsLPw(utils);
        } catch (Throwable e) {
            utils.log("Failed to hook PackageManagerService", e.toString());
        }
    }

    private static void hookGrantPermissionsLPw(Utils utils) {
        Class<?> pmsClass = XposedHelpers.findClass(CLASS_PACKAGE_MANAGER_SERVICE, utils.getClassLoader());
        Method method;
        // Android 5.0 +
        utils.log("Hooking grantPermissionsLPw() for Android 21+");
        method = XposedHelpers.findMethodExact(pmsClass, "grantPermissionsLPw",
                /* PackageParser.Package pkg */ CLASS_PACKAGE_PARSER_PACKAGE,
                /* boolean replace           */ boolean.class,
                /* String packageOfInterest  */ String.class);

        XposedBridge.hookMethod(method, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                Object pkg = param.args[0];

                final String _packageName = (String) XposedHelpers.getObjectField(pkg, "packageName");

                Set<String> packageSet = PACKAGE_PERMISSIONS.keySet();
                for (String packageName : packageSet) {
                    if (packageName.equals(_packageName)) {
                        utils.log("PackageName: %s", packageName);
                        // PackageParser$Package.mExtras 实际上是 com.android.server.pm.PackageSetting mExtras 对象
                        final Object extras = XposedHelpers.getObjectField(pkg, "mExtras");
                        // com.android.server.pm.PermissionsState 对象
                        final Object permissionsState = XposedHelpers.callMethod(extras, "getPermissionsState");

                        // Manifest.xml 中声明的permission列表
                        final List<String> requestedPermissions = (List<String>)
                                XposedHelpers.getObjectField(pkg, "requestedPermissions");

                        // com.android.server.pm.Settings mSettings 对象
                        final Object settings = XposedHelpers.getObjectField(param.thisObject, "mSettings");
                        // ArrayMap<String, com.android.server.pm.BasePermission> mPermissions 对象
                        final Object permissions = XposedHelpers.getObjectField(settings, "mPermissions");

                        List<String> permissionsToGrant = PACKAGE_PERMISSIONS.get(packageName);
                        for (String permissionToGrant : permissionsToGrant) {
                            if (!requestedPermissions.contains(permissionToGrant)) {
                                boolean granted = (boolean) XposedHelpers.callMethod(
                                        permissionsState, "hasInstallPermission", permissionToGrant);
                                if (!granted) {
                                    // com.android.server.pm.BasePermission bpToGrant
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

}
