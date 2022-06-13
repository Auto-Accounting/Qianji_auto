package cn.dreamn.qianji_auto.data.data;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;

import org.apache.commons.lang3.time.StopWatch;

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

    }

    public void run(BillInfo billInfo, String addJs, TaskThread.TaskResult taskResult) {
        //执行分类
        TaskThread.onThread(() -> {

            StringBuilder dataList = new StringBuilder();
            Regular[] regulars = Db.db.RegularDao().loadUse(type, null, 0, 200);
            if (addJs == null && (regulars == null || regulars.length == 0)) {
                taskResult.onEnd("NotFound");
                return;
            }
            if (addJs != null) {
                dataList.append(addJs);
            } else {
                for (Regular value : regulars) {
                    dataList.append(value.regular);
                }
            }


            String jsInner = "const isInTimeInner=function(a,b,c,d){regT=/([01\\b]\\d|2[0-3]):([0-5]\\d)/;const e=a.match(regT),f=b.match(regT);if(null==e||null==f||e.length<3||f.length<3)return!1;const g=parseInt(e[1],10),h=parseInt(f[1],10),i=parseInt(e[2],10),j=parseInt(f[2],10);return g>h?c===g&&d>=i||c>g||h>c||c===h&&j>=d:h>g?c===g&&d>=i||c>g&&h>c||c===h&&j>=d:g===h?j>i?c===g&&d>=i&&j>=d:i>j?c===g&&d>=i||j>=d||c!==g:i===j&&d===i&&c===g:void 0};";
            String js = "function getCategory(shopName,shopRemark,type,hour,minute,money){%s  %s return 'NotFound';} getCategory('%s','%s','%s',%s,%s,'%s');";

            long stamp = billInfo.getTimeStamp();
            String hour = DateUtils.getTime("HH", stamp);
            String minute = DateUtils.getTime("mm", stamp);

            String testJs = String.format(js, jsInner, dataList.toString(), billInfo.getShopAccount(), billInfo.getShopRemark(), BillInfo.getTypeName(billInfo.getType()), hour, minute, billInfo.getMoney());

            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                String result = JsEngine.run(testJs);
                Log.i("自动分类结果：" + result);
                stopWatch.split();
                long elapsedTime = stopWatch.getSplitTime();
                stopWatch.stop();
                Log.i(String.format("自动分类结果：%s.耗时: %dms", result, elapsedTime));
                if (elapsedTime >= 2000) {
                    Log.i("自动分类执行时间超过2秒，请检查规则.");
                    ToastUtils.show("自动分类执行时间超过2秒，请检查规则.");
                    // 添加超时  为了充分展示Toast
                    Thread.sleep(3000);
                }
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
        data = data.replace("'", "\\'");
        String finalData = data;
        TaskThread.onThread(() -> {

            Regular[] regulars = Db.db.RegularDao().loadUse(type, app, 0, 200);
            StringBuilder dataList = new StringBuilder();
            //  String reg = ";try{pattern=/%s/;if(pattern.test(a)){var array=pattern.exec(a);var remark='%s',account='%s',type='%s',money='%s',shopName='%s',account2='%s',fee='%s',time='%s';for(var i=array.length-1;i>=1;i--){var rep=\"$\"+i.toString();var repStr=array[i];remark=remark.replace(rep,repStr);account=account.replace(rep,repStr);type=type.replace(rep,repStr);money=money.replace(rep,repStr);shopName=shopName.replace(rep,repStr);account2=account2.replace(rep,repStr);fee=fee.replace(rep,repStr);time=time.replace(rep,repStr)}return remark+'##'+account+'##'+type+'##'+money+'##'+account2+'##'+shopName+'##'+fee+'##'+time+'##%s'}}catch(e){console.log(e)};";

            if (addJs == null && (regulars == null || regulars.length == 0)) {
                taskResult.onEnd(null);
                return;
            }
            if (addJs != null) {
                Log.i(addJs);
                // JSONObject jsonObject = JSONObject.parseObject(addJs);
                // String j = String.format(reg, jsonObject.getString("regex_input"), jsonObject.getString("shopRemark"), jsonObject.getString("account_name1"), jsonObject.getString("type"), jsonObject.getString("money"), jsonObject.getString("shopName"), jsonObject.getString("account_name2"), jsonObject.getString("fee"), jsonObject.getString("time"), jsonObject.getString("auto"));
                dataList.append(addJs);
            } else {
                for (Regular value : regulars) {
                    //   JSONObject jsonObject = JSONObject.parseObject(value.data);
                    //  String j = String.format(reg, value.regular, jsonObject.getString("shopRemark"), jsonObject.getString("account1"), jsonObject.getString("type"), jsonObject.getString("money"), jsonObject.getString("shopName"), jsonObject.getString("account2"), jsonObject.getString("fee"), jsonObject.getString("time"), jsonObject.getString("auto"));
                    String j = value.regular;
                    dataList.append(j);
                }
            }


            /**
             function getData(a) {
             function Calculate(array, value, symbol) {
             if(typeof value != "string" ) return value;
             var isNumber = (symbol != "$|");
             if(symbol == "$|")value=value+"$|";
             if (value.indexOf(symbol) != -1) {
             var addList = value.split(symbol);
             var result = 0;
             var last = "";
             for (var k = 0; k < addList.length; k++) {
             try {
             var repStr = addList[k];

             if (addList[k].indexOf("$") != -1) {
             var index = parseInt(addList[k].replace("$", ""));
             if (index < array.length) {
             if (isNumber) {
             repStr = parseFloat(array[index].replace(",", ""));
             } else repStr = array[index];
             }
             } else if (isNumber) {
             repStr = parseFloat(addList[k].replace(",", ""));
             }
             if (result == 0) {
             last = repStr;
             result = last;
             continue;
             }
             if (symbol == "$-") {
             result = last - repStr;
             } else if (symbol == "$+") {
             result = last + repStr;
             } else {
             result = last || repStr;
             }
             last = result;

             } catch(e) {
             console.log(e);
             }
             }
             return result;
             } else {
             return value;
             }
             }
             function retData(array, ret) {
             for (var i = array.length - 1; i >= 1; i--) {
             var rep = '$' + i.toString();
             var repStr = array[i];
             while (ret.indexOf(rep) != -1) {
             ret = ret.replace(rep, repStr);
             }
             }
             return ret;
             }
             try {
             pattern = /%s/;
             //pattern = /date:(.*?),unit:元,goto:.*?&tradeNO=([\d_]+)&bizType=(TRADE|FASCORE|[DB]_TRANSFER|.*PAY|PUC_CHARGE),[^,]+,money:(\d+\.\d{2}),.*,title:(付款|自动扣款|转账|转入)成功,actions:\[.*\],content:\[(title:扣款说明：,content:.*?,)?(title:付款方式：,content:(.*?),)?(title:已用优惠：,content:.*?,)?title:(交易对象|对方账户|户号|申请人)：,content:(.*?),title:.{2}(说明|备注|理由)：,content:([^|]*).*?\],status:\5成功,amountTip:/
             if (pattern.test(a)) {
             var array = pattern.exec(a);
             var ret = '%s##%s##%s##%s##%s##%s##%s##%s##%s';
             //   var ret = '$1##$2$+$2$+666$-0.233##$3##$9$|$5##$5##$6##$7##$0##1';
             // remark + '##' + account + '##' + type + '##' + money + '##' + account2 + '##' + shopName + '##' + fee + '##' + time + '##%s'
             // $+ $- $|
             var list = ret.split("##");
             console.log(list, list.length);
             for (var j = 0; j < list.length; j++) {
             var value = list[j];
             var plus = Calculate(array, value, "$+");
             ret = ret.replace(value, plus);
             var min = Calculate(array, plus, "$-");
             ret = ret.replace(plus, min);
             var xor = Calculate(array, min, "$|");
             ret = ret.replace(min, xor);

             }

             return retData(array, ret);
             }
             } catch(e) {
             console.log(e)
             }
             return 'undefined##undefined##undefined##undefined##undefined##undefined##undefined##undefined##0';
             }
             getData('%s');
             //console.log(getData('date:10月06日,unit:元,goto:alipays://platformapi/startapp?appId=20000003&actionType=toBillDetails&tradeNO=20211006281036886905&bizType=PREAUTHPAY,ad:[],money:11.25,failTip:,infoTip:,title:付款成功,actions:[name:,url:,name:查看详情,url:alipays://platformapi/startapp?appId=20000003&actionType=toBillDetails&tradeNO=20211006281036886905&bizType=PREAUTHPAY],content:[title:付款方式：,content:花呗支付,title:交易对象：,content:众安在线财产保险股份有限公司,title:商品说明：,content:保险承保-好医保·住院医疗|21080382152307210961|期数|202110|续期交易付款],status:付款成功,amountTip:'));
             */

            String js = "function getData(a){function b(a,b,c){var d,e,f,g,h,i,j;if(\"string\"!=typeof b)return b;if(d=\"$|\"!=c,\"$|\"==c&&(b+=\"$|\"),-1!=b.indexOf(c)){for(e=b.split(c),f=0,g=\"\",h=0;h<e.length;h++)try{if(i=e[h],-1!=e[h].indexOf(\"$\")?(j=parseInt(e[h].replace(\"$\",\"\")),j<a.length&&(i=d?parseFloat(a[j].replace(\",\",\"\")):a[j])):d&&(i=parseFloat(e[h].replace(\",\",\"\"))),0==f){g=i,f=g;continue}f=\"$-\"==c?g-i:\"$+\"==c?g+i:g||i,g=f}catch(k){console.log(k)}return f}return b}function c(a,b){var c,d,e;for(c=a.length-1;c>=1;c--)for(d=\"$\"+c.toString(),e=a[c];-1!=b.indexOf(d);)b=b.replace(d,e);return b};%s;return\"undefined##undefined##undefined##undefined##undefined##undefined##undefined##undefined##0\"}getData('%s');";

            String testJs = String.format(js, dataList.toString(), finalData);

            //获得所有Js
            String result = "";
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                result = JsEngine.run(testJs);
                stopWatch.split();
                long elapsedTime = stopWatch.getSplitTime();
                stopWatch.stop();
                Log.i(String.format("%s 解析结果：%s.耗时: %dms", app, result, elapsedTime));
                if (elapsedTime >= 2000) {
                    Log.i(app + " App解析执行时间超过2秒，请检查规则.");
                    ToastUtils.show(app + " App解析执行时间超过2秒，请检查规则.");
                    // 添加超时  为了充分展示Toast
                    Thread.sleep(3000);
                }
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

                billInfo.setShopRemark(strs[0]);
                billInfo.setRawAccount(strs[1]);
                billInfo.setType(strs[2]);
                billInfo.setMoney(strs[3]);
                billInfo.setRawAccount2(strs[4]);
                billInfo.setShopAccount(strs[5]);
                billInfo.setFee(strs[6]);
                billInfo.setTimeStamp(DateUtils.getAnyTime(strs[7]));
                billInfo.setAuto(strs[8].equals("1"));
                Log.i(billInfo.toString());
                taskResult.onEnd(billInfo);
            } else {
                Log.i("js执行结果为NULL");
                taskResult.onEnd(null);
            }

        });
    }
}
