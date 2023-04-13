package cn.dreamn.qianji_auto.core.hook.hooks.sdu_pass.hooks;

import android.annotation.SuppressLint;

import com.alibaba.fastjson.JSONObject;

import java.net.URLDecoder;

import cn.dreamn.qianji_auto.core.hook.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * @author JiunnTarn
 */
public class Pay {
    public static void init(Utils utils) {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        Class<?> ScanQrcodePayActivity = XposedHelpers.findClass("synjones.commerce.views.ScanQrcodePayActivity.14", mAppClassLoader);
        Class<?> Okhttp3Am = XposedHelpers.findClass("okhttp3.am", mAppClassLoader);

        XposedHelpers.findAndHookMethod(ScanQrcodePayActivity, "a", Okhttp3Am, String.class, new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        String data = URLDecoder.decode(param.args[1].toString(), "UTF-8");

                        try {
                            analyze(utils, data);
                        } catch (Exception e) {
                            utils.log("SDUPassErr: " + e.toString(), false);
                        }
                    }
                }
        );
    }

    @SuppressLint("DefaultLocale")
    private static void analyze(Utils utils, String raw) {
        // 开门信息，无需记账
        if (raw.startsWith("synjones_0")) return;

        utils.log("山大v卡通支付信息原始数据：" + raw, true);

        String[] splits = raw.split("&");
        JSONObject data = new JSONObject();

        // 交易金额
        data.put("transaction_amount", String.format("%.2f", ((Integer.parseInt(splits[1]) * 1d) / 100d)));
        data.put("boarding_expenses", splits[2]);
        // 订单号
        data.put("order_number", splits[3].substring(13, splits[3].length() - 2));
        data.put("trjn_info_id", splits[4]);
        // 商户号
        data.put("merchant_number", splits[5]);
        // 账号
        data.put("account_number", splits[6]);
        // 流水号
        data.put("serial_number", splits[7]);
        // 商户名
        data.put("merchant_name", splits[8]);
        // 付款方式
        data.put("payment_method", splits[9]);
        // 交易时间
        data.put("transaction_time", splits[10]);

        if (splits.length == 12) {
            data.put("has_authority", splits[11]);
        }

        utils.send(data);

    }
}
