package cn.dreamn.qianji_auto.data.data;

import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Table.Regular;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.JsEngine;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class RegularCenter {
    private static RegularCenter regularCenter;
    private String type = "";

    public static RegularCenter getInstance(String type) {
        if (regularCenter == null) {
            regularCenter = new RegularCenter();
        }
        regularCenter.setType(type);
        return regularCenter;
    }

    //准备自动规则
    public static void setCateJs(BillInfo billInfo, String sort) {
        //这两种类型不需要
        if (billInfo.getType(true).equals(BillInfo.TYPE_CREDIT_CARD_PAYMENT) || billInfo.getType().equals(BillInfo.TYPE_TRANSFER_ACCOUNTS)) {
            return;
        }

        String str = "";
        // String pattern = "([(（])\\d+月\\d+日([)）])";
        String shopRemark = billInfo.getShopRemark();
        //    str += String.format("time = %s && ", time);
        str += String.format("shopName.indexOf('%s')!=-1 && ", billInfo.getShopAccount());
        str += String.format("shopRemark.indexOf('%s')!=-1 && ", shopRemark);
        str += String.format("type == '%s' && ", BillInfo.getTypeName(billInfo.getType(true)));

        String regular = "if(%s)return '%s';";

        int last = str.lastIndexOf('&');
        if (last != -1 && last != 0)
            str = str.substring(0, last - 1);

        regular = String.format(regular, str, sort);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("regular_name", billInfo.getShopAccount());
        jsonObject.put("regular_remark", billInfo.getShopRemark());
        jsonObject.put("regular_time1_link", "");
        jsonObject.put("regular_time1", "");
        jsonObject.put("regular_time2_link", "");
        jsonObject.put("regular_time2", "");
        jsonObject.put("regular_money1_link", "");
        jsonObject.put("regular_money1", "");
        jsonObject.put("regular_money2_link", "");
        jsonObject.put("regular_money2", "");
        jsonObject.put("regular_shopName_link", "包含");
        jsonObject.put("regular_shopName", billInfo.getShopAccount());
        jsonObject.put("regular_shopRemark_link", "包含");
        jsonObject.put("regular_shopRemark", shopRemark);
        jsonObject.put("regular_type", BillInfo.getTypeName(billInfo.getType()));

        jsonObject.put("iconImg", "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png");
        jsonObject.put("regular_sort", sort);
        String dataId = Tool.getRandomString(32);
        jsonObject.put("dataId", dataId);
        jsonObject.put("version", "0");

        String finalRegular = regular;
        TaskThread.onThread(() -> Db.db.RegularDao().add(finalRegular, billInfo.getShopAccount(), jsonObject.toJSONString(), "[自动生成]", dataId, "0", "", "category"));
    }

    private void setType(String type) {
        this.type = type;
        Log.init("regular");
    }

    public void run(BillInfo billInfo, String addJs, TaskThread.TaskResult taskResult) {
        //执行分类
        TaskThread.onThread(() -> {
            StringBuilder dataList = new StringBuilder();
            Regular[] regulars = Db.db.RegularDao().loadUse(type, null, 0, 500);
            if (addJs != null) {
                dataList.append(addJs);
            }
            for (Regular value : regulars) {
                dataList.append(value.regular);
            }
            String jsInner = "const isInTimeInner=function(a,b,c,d){regT=/([01\\b]\\d|2[0-3]):([0-5]\\d)/;const e=a.match(regT),f=b.match(regT);if(null==e||null==f||e.length<3||f.length<3)return!1;const g=parseInt(e[1],10),h=parseInt(f[1],10),i=parseInt(e[2],10),j=parseInt(f[2],10);return g>h?c===g&&d>=i||c>g||h>c||c===h&&j>=d:h>g?c===g&&d>=i||c>g&&h>c||c===h&&j>=d:g===h?j>i?c===g&&d>=i&&j>=d:i>j?c===g&&d>=i||j>=d||c!==g:i===j&&d===i&&c===g:void 0};";
            String js = "function getCategory(shopName,shopRemark,type,hour,minute,money){%s  %s return 'NotFound';} getCategory('%s','%s','%s',%s,%s,'%s');";

            long stamp = billInfo.getTimeStamp();
            String hour = DateUtils.getTime("HH", stamp);
            String minute = DateUtils.getTime("mm", stamp);

            String testJs = String.format(js, jsInner, dataList.toString(), billInfo.getShopAccount(), billInfo.getShopRemark(), BillInfo.getTypeName(billInfo.getType()), hour, minute, billInfo.getMoney());

            try {
                String result = JsEngine.run(testJs);
                Log.i("自动分类结果：" + result);
                if (result.contains("Undefined")) {
                    taskResult.onEnd("NotFound");
                } else taskResult.onEnd(result);
            } catch (Exception e) {
                Log.d(" 自动分类执行出错！" + e.toString());
                e.printStackTrace();
                taskResult.onEnd("NotFound");
            }

        });
    }

    public void run(String app, String data, String addJs, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            Regular[] regulars = Db.db.RegularDao().loadUse(type, app, 0, 500);
            StringBuilder dataList = new StringBuilder();
            String reg = ";try{pattern=/%s/;if(pattern.test(a)){var array=pattern.exec(a);var remark='%s',account='%s',type='%s',money='%s',shopName='%s',account2='%s',fee='%s',time='%s';for(var i=array.length-1;i>=1;i--){var rep=\"$\"+i.toString();var repStr=array[i];remark=remark.replace(rep,repStr);account=account.replace(rep,repStr);type=type.replace(rep,repStr);money=money.replace(rep,repStr);shopName=shopName.replace(rep,repStr);account2=account2.replace(rep,repStr);fee=fee.replace(rep,repStr);time=time.replace(rep,repStr)}return remark+'##'+account+'##'+type+'##'+money+'##'+account2+'##'+shopName+'##'+fee+'##'+time+'##%s'}}catch(e){console.log(e)};";
            for (Regular value : regulars) {
                JSONObject jsonObject = JSONObject.parseObject(value.data);
                String j = String.format(reg, value.regular, jsonObject.getString("shopRemark"), jsonObject.getString("account1"), jsonObject.getString("type"), jsonObject.getString("money"), jsonObject.getString("shopName"), jsonObject.getString("account2"), jsonObject.getString("fee"), jsonObject.getString("time"), jsonObject.getString("auto"));
                dataList.append(j);
            }

            if (addJs != null) {
                JSONObject jsonObject = JSONObject.parseObject(addJs);
                String j = String.format(reg, jsonObject.getString("regular"), jsonObject.getString("shopRemark"), jsonObject.getString("account1"), jsonObject.getString("type"), jsonObject.getString("money"), jsonObject.getString("shopName"), jsonObject.getString("account2"), jsonObject.getString("fee"), jsonObject.getString("time"), jsonObject.getString("auto"));
                dataList.append(j);
            }

            String js = ";function getData(a){a=(a.replace(/\\n/g,\"\\\\n\"));var b,account,type,money,shopName,account2,fee,time;%s return b+'##'+account+'##'+type+'##'+money+'##'+account2+'##'+shopName+'##'+fee+'##'+time+'##0'};;getData('%s')";

            String testJs = String.format(js, dataList.toString(), data);

            //获得所有Js
            String result = "";
            try {
                result = JsEngine.run(testJs);
            } catch (Throwable ex) {
                Log.i("错误：" + ex.toString());
            }
            if (!result.startsWith("undefined")) {
                String[] strs = result.split("##", -1);
                Log.i("解析结果", Arrays.toString(strs));
                if (strs.length < 9) {
                    taskResult.onEnd(null);
                    return;
                }
                BillInfo billInfo = new BillInfo();
                Log.i(strs[0]);
                billInfo.setShopRemark(strs[0]);
                billInfo.setRawAccount(strs[1]);
                billInfo.setType(strs[2]);
                billInfo.setMoney(strs[3]);
                billInfo.setShopAccount(strs[5]);
                billInfo.setRawAccount2(strs[4]);
                billInfo.setFee(strs[6]);
                billInfo.setTimeStamp(DateUtils.getAnyTime(strs[7]));
                billInfo.setAuto(strs[8].equals("0"));
                taskResult.onEnd(billInfo);
            } else {
                Log.i("js执行结果为NULL");
                taskResult.onEnd(null);
            }

        });
    }
}
