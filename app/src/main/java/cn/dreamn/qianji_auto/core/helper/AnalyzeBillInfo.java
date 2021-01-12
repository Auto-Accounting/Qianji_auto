/*
package cn.dreamn.qianji_auto.core.helper;

import android.content.Context;

import java.util.List;

import cn.dreamn.qianji_auto.core.db.Cache;
import cn.dreamn.qianji_auto.core.db.DbManger;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.BillTools;
import cn.dreamn.qianji_auto.core.utils.CallAutoActivity;
import cn.dreamn.qianji_auto.core.utils.Category;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class AnalyzeBillInfo {


    public static BillInfo wechatTransferTemp(List<String> list) {
        if (list.size() <= 6) {
            return null;
        }

        Logs.d("Qianji_Analyze","分析 -> 转账页面 ");
        BillInfo billInfo = new BillInfo();

        billInfo.setTime();
        billInfo.setType(BillInfo.TYPE_PAY);
        billInfo.setAccountName("微信");

        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if(i==1&& list.get(0).equals("转账") && list.get(2).equals("转账金额")){
                billInfo.setShopAccount(str);
            }

            if(i==4){
                if(str.equals("添加转账说明"))continue;
                String money=BillInfo.BillTools.getMoney(str);
                if(money.equals("0")){
                    String remark=str.replace(" 修改","");
                    billInfo.setRemark(remark);
                    billInfo.setShopRemark(remark);
                }

            }

            if(i==5){
                if(str.equals("添加转账说明")||str.equals("1"))continue;
                String remark=str.replace(" 修改","");
                billInfo.setRemark(remark);
                billInfo.setShopRemark(remark);
            }
            Logs.d("Qianji_Analyze",i+"  "+str);

        }

        DbManger.db.CacheDao().add("wechat",billInfo.getQianJi());

        return billInfo;
    }
    public static BillInfo wechatTransferTemp2(List<String> list) {
        if (list.size() <= 0) {
            return null;
        }
        Logs.d("Qianji_Analyze","分析 -> 转账页面2 ");
        Cache[] caches=DbManger.db.CacheDao().loadOne("wechat");
        if(caches.length<=0)return null;

        BillInfo billInfo = BillInfo.parse(caches[0].cacheData);

        Logs.d("Qianji_Analyze","缓存 -> " + caches[0].cacheData);


        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if(i==4&& list.get(3).equals("支付方式")){
                billInfo.setAccountName(str);
            }
            if(i==1){
                if(billInfo.getShopRemark()==null||billInfo.getShopRemark().equals("")){
                    billInfo.setShopRemark(str);
                    billInfo.setRemark(str);
                }
            }
            if(i==2){
                if(billInfo.getMoney().equals("0")){
                    billInfo.setMoney(BillTools.getMoney(str));
                }
            }
            Logs.d("Qianji_Analyze",i+"  "+str);

        }

        DbManger.db.CacheDao().update(caches[0].id,billInfo.getQianJi());

        return billInfo;
    }

    public static void wechatTransferSucceed(Context context,List<String> list) {
        Logs.d("Qianji_Analyze","分析 -> 转账成功页面 ");
        Cache[] caches=DbManger.db.CacheDao().loadOne("wechat");
        if(caches.length<=0)return;
        BillInfo billInfo = BillInfo.parse(caches[0].cacheData);
        for (int i = 0; i < list.size(); i++) {
            Logs.d("Qianji_Analyze",i+"  "+list.get(i));
            String str=list.get(i);
            if(i==2){
                if(billInfo.getMoney().equals("0")){
                    billInfo.setMoney(BillInfo.BillTools.getMoney(str));
                }
            }
        }
        Category category=new Category(context);
        category.getCategory(billInfo.getShopAccount(),billInfo.getShopRemark(),cateName->{
            billInfo.setCateName(cateName);
            billInfo.dump();
            CallAutoActivity.call(context, billInfo,caches[0].id);
        });

    }




}
*/
