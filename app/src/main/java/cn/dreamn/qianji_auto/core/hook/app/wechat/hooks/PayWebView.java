package cn.dreamn.qianji_auto.core.hook.app.wechat.hooks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;

import java.io.IOException;
import java.util.Objects;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PayWebView {

    public static void init(Utils utils) {
        //hook底层
        // hook(utils);
        // hookShare(utils);

        hookActivity(utils);
        hookUIJump(utils);
        hook(utils);
    }


    public static void hookActivity(Utils utils) {
        utils.log("hook activity");

        // public static void a(Context arg9, dyp arg10, Bundle arg11, i arg12) {
        XposedHelpers.findAndHookMethod(Activity.class, "startActivity", Intent.class, Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Intent intent = (Intent) param.args[0];
                        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        //   intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        Bundle bundle = (Bundle) param.args[1];
                        String target = intent.getComponent().getClassName();
                        Bundle bundle1 = intent.getExtras();
                        String log = "启动activity\n";
                        if (bundle1 != null) {
                            log += intent.getExtras().toString() + "\n";
                        } else {
                            log += "null\n";
                        }
                        log += "\ntarget:" + target;
                        if (bundle != null)
                            log += "\nBundle数据" + bundle.toString();
                        utils.log(log);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws IllegalAccessException {

                    }
                });
    }

    //com.tencent.mm.wallet_core.ui.g;
    public static void hookUIJump(Utils utils) {
        utils.log("hook WebViewUI");

        //  utils.printClassAndFunctions("com.tencent.mm.plugin.webview.stub.WebViewStubService");
        // private static void b(Context arg11, String arg12, String arg13, Intent arg14, Bundle arg15) {
        XposedHelpers.findAndHookMethod("com.tencent.mm.ui.MMActivity", utils.getClassLoader(), "onCreate", Bundle.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        utils.log("hooked  ViewCreate");
                        Activity activity = (Activity) param.thisObject;

                        Intent intent = new Intent();
                        intent.putExtra("rawUrl", "https://wx.tenpay.com/userroll/readtemplate?t=userroll/index_tmpl");
                        intent.putExtra("startTime", "1626140743042");
                        intent.putExtra("allow_mix_content_mode", true);
                        intent.putExtra("showShare", false);
                        intent.putExtra("no_support_dark_mode", true);
                        intent.putExtra("start_activity_time", "1626140743042");
                        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        intent.setClassName("com.tencent.mm", "com.tencent.mm.plugin.webview.ui.tools.WebViewUI");
                        activity.startActivity(intent, null);
                        utils.log("hooked  启动账单");
                        /*Bundle bundle1=(Bundle)param.args[0];
                        if(bundle1!=null){
                            utils.log("携带参数"+bundle1.toString());
                        }

                        Intent intent = (Intent) XposedHelpers.callMethod(activity, "getIntent");
                        Bundle bundle=intent.getExtras();
                        if(bundle!=null)
                            utils.log("intent:"+intent.getExtras().toString());
                        else{
                            utils.log("intent:null");
                        }*/

                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {


                    }
                });
    }

    static void hook(Utils utils) {
        //public boolean onTransact(int arg10, Parcel arg11, Parcel arg12, int arg13)
        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.webview.stub.e$a", utils.getClassLoader(), "onTransact", int.class,
                Parcel.class, Parcel.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        String log = "";
                        log += "参数1(switch)：" + param.args[0].toString() + "\n";
                        log += "参数4：" + param.args[3].toString() + "\n";


                        Parcel parcel = (Parcel) param.args[1];
                        if (parcel != null)
                            log += "参数3：" + parcel.toString() + "\n";
                        Parcel parcel2 = (Parcel) param.args[1];
                        if (parcel2 != null)
                            log += "参数4：" + parcel2.toString() + "\n";
                        utils.log(log);
                    }
                });
    }

    static void getPayKeys(Utils utils) {
        //获取微信支付账单页面的相关关键信息
        String url = "https://wx.tenpay.com/userroll/readtemplate?t=userroll/index_tmpl";
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .get()//默认就是GET请求，可以不写
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                utils.log("失败");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String log = response.code() + "\n" +
                        response.protocol().toString() + "\n" +
                        response.headers().toString() + "\n\n" +
                        Objects.requireNonNull(response.body()).string();
                utils.log(log);
            }
        });
    }

    public static void getOrderInfo(Utils utils, String OrderId) {

    }
}
