package cn.dreamn.qianji_auto.core.hook.app.wechat.hooks;

import android.util.Log;

import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.utils.files.FileUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class OpenLog {
    public static void init(Utils utils) throws ClassNotFoundException {
        hookTinker(utils);
        //启用日志出现闪退或者卡死的初始化页面：
        /*
        进入 data/user/0/com.tencent.mm/tinker/patch-dbe0bec8/dex/tinker_class.apk
        删除apk，这是微信热补丁文件，会导致
        * */

        //Xlog开关打开
        hookXLogSet(utils);


       /*

       这是tinker的日志输出，没啥用

        hookAllFunctions2(utils, "i");
        hookAllFunctions2(utils, "d");

        hookAllFunctions2(utils, "e");


        hookAllFunctions2(utils, "v");

        hookAllFunctions2(utils, "w");*/
        hookAllFunctions(utils, "i");

        hookAllFunctions(utils, "d");

        hookAllFunctions(utils, "e");

        hookAllFunctions(utils, "f");

        hookAllFunctions(utils, "v");

        hookAllFunctions(utils, "w");

    }

    private static void hookXLogSet(Utils utils) {
        XposedHelpers.findAndHookMethod("com.tencent.mm.xlog.app.XLogSetup", utils.getClassLoader(), "keep_setupXLog",
                boolean.class, String.class, String.class, Integer.class, Boolean.class,
                Boolean.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        param.args[4] = true;
                        param.args[5] = true;
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.i("微信", "keep_setupXLog参数isLogcatOpen：" + param.args[5]);
                    }
                });
    }

  /*  private static void hookXLogSet(Utils utils) {
        XposedHelpers.findAndHookMethod("com.tencent.mm.xlog.app.XLogSetup", utils.getClassLoader(), "keep_setupXLog",
                boolean.class, String.class, String.class, Integer.class, Boolean.class,
                Boolean.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[4]  = true;
                        param.args[5]  = true;
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        //param.args[5] = true;
                        Log.i("微信", "keep_setupXLog参数isLogcatOpen：" + param.args[5]);
                    }
                });
    }*/

    private static void hookAllFunctions(Utils utils, String functionName) {

        XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.platformtools.Log", utils.getClassLoader(), functionName,
                String.class, String.class, Object[].class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String str = (String) param.args[0];
                        String str2 = (String) param.args[1];
                        Object[] objArr = (Object[]) param.args[2];
                        String format = objArr == null ? str2 : String.format(str2, objArr);
                        XposedBridge.log("微信[" + functionName + "] " + str + "  " + format);
                        super.beforeHookedMethod(param);
                    }
                });
    }


    private static void hookTinker(Utils utils) {
        FileUtils.deleteDirectory("/data/user/0/com.tencent.mm/tinker/");//删除tinker缓存，留之无用，阻碍我调试。

       /* ClassLoader mAppClassLoader=utils.getClassLoader();
        Class<?> ShareSecurityCheck=mAppClassLoader.loadClass("com.tencent.tinker.loader.shareutil.ShareSecurityCheck");
        //垃圾Tinker框架~
        XposedHelpers.findAndHookMethod("com.tencent.tinker.loader.TinkerDexLoader", mAppClassLoader, "checkComplete", String.class,ShareSecurityCheck,String.class, Intent.class, XC_MethodReplacement.returnConstant(true));*/
    }

    private static void hookAllFunctions2(Utils utils, String functionName) {

        XposedHelpers.findAndHookMethod(" com.tencent.tinker.loader.shareutil.ShareTinkerLog", utils.getClassLoader(), functionName,
                String.class, String.class, Object[].class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String str = (String) param.args[0];
                        String str2 = (String) param.args[1];
                        Object[] objArr = (Object[]) param.args[2];
                        String format = objArr == null ? str2 : String.format(str2, objArr);
                        XposedBridge.log("微信[" + functionName + "] " + str + "  " + format);
                        super.beforeHookedMethod(param);
                    }
                });
    }


}
