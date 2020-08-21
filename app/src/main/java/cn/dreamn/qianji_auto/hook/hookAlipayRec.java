package cn.dreamn.qianji_auto.hook;

import android.app.AndroidAppHelper;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.func.Api;
import cn.dreamn.qianji_auto.func.Fun;
import cn.dreamn.qianji_auto.func.Log;
import cn.dreamn.qianji_auto.func.Storage;
import cn.dreamn.qianji_auto.func.core;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static cn.dreamn.qianji_auto.func.Api.getQianji;

public class hookAlipayRec implements IXposedHookLoadPackage {
    ClassLoader classLoader;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.eg.android.AlipayGphone")) return;
        classLoader = lpparam.classLoader;
        hookRec(lpparam);
        hookRecRed(lpparam);

    }

    /**
     * hook支付宝交易接口
     * @param lpparam
     * @throws Throwable
     */
    private void hookRec(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Class<?> MsgboxInfoServiceImpl = classLoader.loadClass("com.alipay.android.phone.messageboxstatic.biz.sync.d");
        Class<?> SyncMessage = classLoader.loadClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage");
        XposedHelpers.findAndHookMethod(MsgboxInfoServiceImpl,"onReceiveMessage", SyncMessage, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String data=param.args[0].toString();
                data=data.substring(data.indexOf("msgData=[")+"msgData=[".length(),data.indexOf("], pushData=,"));
                JSONObject jsonObject= JSON.parseObject(data);
                if(!jsonObject.containsKey("pl"))return;
                String str=jsonObject.getString("pl");
                JSONObject jsonObject1= JSON.parseObject(str);

                if(!jsonObject1.containsKey("templateType"))return;
                if(!jsonObject1.containsKey("title"))return;
                if(!jsonObject1.containsKey("content"))return;
                Log.i("支付宝数据捕获",str);
                Context context = AndroidAppHelper.currentApplication();
                if(jsonObject1.getString("templateType").equals("BN")){
                    JSONObject content=jsonObject1.getJSONObject("content");
                    switch (jsonObject1.getString("title")){
                        case "转账收款到余额宝":
                        case "转账到账成功":return;
                        case "付款成功":doPay(content,context);break;
                        case "转账成功":doReturn(content,context);break;
                        case "退款通知":
                        case "收到一笔转账":doTransfer(content,context);break;//转账收款或退款
                        default:doNothing(content);
                    }
                }else if(jsonObject1.getString("templateType").equals("S")){
                    JSONObject content=jsonObject1.getJSONObject("extraInfo");
                    switch (jsonObject1.getString("title")){
                        case "商家服务":
                            return;
                        case "商家服务·收款到账":
                            content.put("mainAmount",Fun.getMoney(jsonObject1.getString("content")));
                            doRec(content,context);break;
                        case "网商银行·余利宝":doMoney(content,context);break;
                        case "蚂蚁财富·我的余额宝":
                            content.put("mainAmount",Fun.getMoney(jsonObject1.getString("content")));
                            doAnt(content,context);break;
                        default:doNothing(content);
                    }
                }




            }
        });
    }
    //余额宝
    private void doAnt(JSONObject jsonObject, Context context) {
        String product=Storage.type(Storage.Set).get("set_remark","[交易对象] - [说明]");
        if(product.contains("[交易对象]"))
            product=product.replace("[交易对象]","余额宝");
        if(product.contains("[说明]"))
            product=product.replace("[说明]",jsonObject.getString("assistMsg1"));

        String cost = jsonObject.getString("mainAmount");
        String payTool = core.getPayTools("支付宝余额");
        Log.i("支付宝余额宝收益信息捕获",
                " 信息："+product+
                        "\n 金额：￥"+cost+
                        "\n 收款渠道："+payTool);

        if(Storage.type(Storage.Set).getBoolean( "set_earn", false)){
            String sort=Storage.type(Storage.Set).get("set_alipay_ant", "其它");
            Api.Send2Qianji(context,Api.TYPE_GAIN,cost,product,payTool,sort);
        }else{
            Log.i("支付宝余额宝收益", "通知栏记账提醒");
            Fun.sendNotify(context,"记账提醒","￥"+cost+" "+product,getQianji(   Api.TYPE_GAIN,  cost,product,payTool));
        }
    }
    //余利宝
    private void doMoney(JSONObject jsonObject, Context context) {
        String product=Storage.type(Storage.Set).get("set_remark","[交易对象] - [说明]");
        if(product.contains("[交易对象]"))
            product=product.replace("[交易对象]","余利宝");
        if(product.contains("[说明]"))
            product=product.replace("[说明]",jsonObject.getString("assistMsg2")+"余利宝收益");

        String cost = Fun.getMoney(jsonObject.getString("assistMsg1"));
        String payTool = core.getPayTools("支付宝余额");
        Log.i("支付宝余利宝收益信息捕获",
                " 信息："+product+
                        "\n 金额：￥"+cost+
                        "\n 收款渠道："+payTool);

        if(Storage.type(Storage.Set).getBoolean( "set_earn", false)){
            String sort=Storage.type(Storage.Set).get("set_alipay_money", "其它");
            Api.Send2Qianji(context,Api.TYPE_GAIN,cost,product,payTool,sort);
        }else{
            Log.i("支付宝余利宝收益", "通知栏记账提醒");
            Fun.sendNotify(context,"记账提醒","￥"+cost+" "+product,getQianji(   Api.TYPE_GAIN,  cost,product,payTool));
        }
    }

    private void doNothing(JSONObject content) {
        Log.i("未识别的账单信息",content.toString());
    }

    private void hookRecRed(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        ClassLoader classLoader = lpparam.classLoader;
        Class<?> proguard = classLoader.loadClass("com.alipay.mobile.redenvelope.proguard.c.b");
        Class<?> SyncMessage = classLoader.loadClass("com.alipay.mobile.rome.longlinkservice.syncmodel.SyncMessage");
        XposedHelpers.findAndHookMethod(proguard,"onReceiveMessage", SyncMessage, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String data=param.args[0].toString();
                data=data.substring(data.indexOf("msgData=[")+"msgData=[".length(),data.indexOf("], pushData=,"));
                JSONObject jsonObject= JSON.parseObject(data);
                if(!jsonObject.containsKey("pl"))return;
                String str=jsonObject.getString("pl");
                JSONObject jsonObject1= JSON.parseObject(str);
                if(!jsonObject1.containsKey("templateJson"))return;
                String templateJson=jsonObject1.getString("templateJson");
                JSONObject jsonObject2= JSON.parseObject(templateJson);
                if(!jsonObject2.containsKey("statusLine1Text"))return;
                if(!jsonObject2.containsKey("title"))return;
                if(!jsonObject2.containsKey("subtitle"))return;
                if(!jsonObject2.getString("subtitle").contains("来自"))return;

                Log.i("支付宝红包捕获",
                        jsonObject2.getString("statusLine1Text")
                        +"\n"+jsonObject2.getString("title")
                        +"\n"+jsonObject2.getString("subtitle")
                        );

                Context context = AndroidAppHelper.currentApplication();
                String product ="红包 "+ jsonObject2.getString("subtitle").replaceAll("&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "").replaceAll("[(/>)<]", "");
                String cost = Fun.getMoney(jsonObject2.getString("statusLine1Text"));
                String payTool = "支付宝";
                Api.Send2Qianji(context,Api.TYPE_GAIN,cost,product,payTool);
            }
        });
    }

    //支付宝发生支付时，进行处理
    private void doPay(JSONObject jsonObject, Context context){
        JSONObject jsonObject1=getResult(jsonObject.getJSONArray("content"));

        String product = jsonObject1.getString("product");
        String cost = jsonObject.getString("money");
        String payTool = core.getPayTools(jsonObject1.getString("payTool"));
        Log.i("支付宝支付信息捕获",
                " 商品："+product+
                "\n 金额：￥"+cost+
                "\n 支付渠道："+payTool
                );
        //发出请求
        String shopId=jsonObject1.getString("shopId");
        String remark=jsonObject1.getString("remark");
        String sort=core.getSort(shopId,remark);
        if(sort.equals("其它")&&Storage.type(Storage.Set).getBoolean("set_pay",false))
            Api.Send2Qianji(context, Api.TYPE_PAY, cost, product, payTool,sort);
        else
            Api.Send2Qianji(context, Api.TYPE_PAY, cost, product, payTool);

    }
    //支付宝发生转账时，进行处理
    private void doReturn(JSONObject jsonObject, Context context){
        JSONObject jsonObject1=getResult(jsonObject.getJSONArray("content"));

        String product = jsonObject1.getString("product");
        String cost = jsonObject.getString("money");
        String payTool = core.getPayTools(jsonObject1.getString("payTool"));
        Log.i("支付宝转账信息捕获",
                " 转账信息："+product+
                        "\n 金额：￥"+cost+
                        "\n 支付渠道："+payTool
        );
        //发出请求
        String shopId=jsonObject1.getString("shopId");
        String remark=jsonObject1.getString("remark");
        String sort=core.getSort(shopId,remark);
        if(sort.equals("其它")&&Storage.type(Storage.Set).getBoolean("set_pay",false))
            Api.Send2Qianji(context, Api.TYPE_PAY, cost, product, payTool,sort);
        else
            Api.Send2Qianji(context, Api.TYPE_PAY, cost, product, payTool);
    }
    //收款
    private void doRec(JSONObject jsonObject, Context context){
        String product=Storage.type(Storage.Set).get("set_remark","[交易对象] - [说明]");
        if(product.contains("[交易对象]"))
            product=product.replace("[交易对象]",jsonObject.getString("assistName1"));
        if(product.contains("[说明]"))
            product=product.replace("[说明]",jsonObject.getString("assistMsg1"));

        String cost = jsonObject.getString("mainAmount");
        String payTool = core.getPayTools("支付宝余额");
        Log.i("支付宝二维码收款信息捕获",
                " 信息："+product+
                "\n 金额：￥"+cost+
                "\n 收款渠道："+payTool);

        if(Storage.type(Storage.Set).getBoolean( "set_earn", false)){
            String sort=Storage.type(Storage.Set).get("set_alipay_qr", "其它");
            Api.Send2Qianji(context,Api.TYPE_GAIN,cost,product,payTool,sort);
        }else{
            Log.i("支付宝二维码收款", "通知栏记账提醒");
            Fun.sendNotify(context,"记账提醒","￥"+cost+" "+product,getQianji(   Api.TYPE_GAIN,  cost,product,payTool));
        }
    }
    //支付宝收到转账进行处理
    private void doTransfer(JSONObject jsonObject, Context context){

        JSONObject jsonObject1=getResult(jsonObject.getJSONArray("content"));
        String product = jsonObject1.getString("product");
        String cost = jsonObject.getString("money");
        String payTool = core.getPayTools(jsonObject1.getString("payTool"));
        Log.i("支付宝收到转账或退款信息","" +
                " 信息："+product+
                "\n 金额：￥"+cost+
                "\n 收款渠道："+payTool
              );

        if(Storage.type(Storage.Set).getBoolean( "set_earn", false)){
            String sort=Storage.type(Storage.Set).get("set_alipay_friend", "其它");
            Api.Send2Qianji(context,Api.TYPE_GAIN,cost,product,payTool,sort);
        }else{
            Log.i("支付宝二维码收款", "通知栏记账提醒");
            Fun.sendNotify(context,"记账提醒","￥"+cost+" "+product,getQianji(   Api.TYPE_GAIN,  cost,product,payTool));
        }
    }

    private JSONObject getResult(JSONArray jsonArray){
        JSONObject jsonObject=new JSONObject();

        String shopName="";
        String remark="";

        for(int i=0;i<jsonArray.size();i++) {
            String title=jsonArray.getJSONObject(i).getString("title");
            String content=jsonArray.getJSONObject(i).getString("content");
            switch (title){
                case "退款去向：":
                case "付款方式：":jsonObject.put("payTool", content.equals("余额")?"支付宝余额":content);break;
                case "已用优惠：":jsonObject.put("payTool","花呗支付");break;

                case "转账到：":
                case "付款方：":
                case "对方账户：":
                case "户号：":
                case "还款到：":
                case "交易对象：":shopName=content;break;

                case "充值说明：":
                case "转账说明：":
                case "转账备注：":
                case "缴费说明：":
                case "退款说明：":
                case "还款说明：":
                case "商品说明：":remark=content;break;
                default:Log.i("未定义标题名",title);
            }
        }
        if(!jsonObject.containsKey("payTool"))
            jsonObject.put("payTool", "支付宝余额");
        String product=Storage.type(Storage.Set).get("set_remark","[交易对象] - [说明]");
        if(product.contains("[交易对象]"))
            product=product.replace("[交易对象]",shopName);
        if(product.contains("[说明]"))
            product=product.replace("[说明]",remark);
        jsonObject.put("product",product);
        jsonObject.put("shopId",shopName);
        jsonObject.put("remark",remark);
        return jsonObject;
    }



}
