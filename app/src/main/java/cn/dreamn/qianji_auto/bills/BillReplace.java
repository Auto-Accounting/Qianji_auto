package cn.dreamn.qianji_auto.bills;

import android.os.Handler;
import android.os.Message;

import cn.dreamn.qianji_auto.database.Helper.Assets;
import cn.dreamn.qianji_auto.database.Helper.BookNames;
import cn.dreamn.qianji_auto.database.Helper.Category;

public class BillReplace {



    public static void addMoreInfo(Handler mHandler, BillInfo billInfo){
        Category.getCategory(billInfo, str -> {
            if (str.equals("NotFound")) {
                billInfo.setCateName("其它");//设置自动分类
            } else {
                billInfo.setCateName(str);//设置自动分类
            }

            billInfo.setBookName(BookNames.getDefault());//设置自动记账的账本名

            Assets.getMap(billInfo.getrawAccount(),mapName->{
                billInfo.setAccountName(mapName);
                Assets.getMap(billInfo.getrawAccount2(),mapName2->{
                    billInfo.setAccountName2(mapName2);
                    Message message=new Message();
                    message.what=2;
                    message.obj=billInfo;
                    mHandler.sendMessage(message);
                });
            });


        });
    }

    public static void replaceRemark(BillInfo billInfo){
       String remark = Remark.getRemarkTpl()
               .replace("[分类]", billInfo.getCateName() == null ? "" : billInfo.getCateName())
               .replace("[金额]", billInfo.getMoney() == null ? "" : billInfo.getMoney())
               .replace("[手续费]", billInfo.getFee() == null ? "" : billInfo.getFee())
               .replace("[账本]", billInfo.getBookName() == null ? "" : billInfo.getBookName())
               .replace("[原始资产1]", billInfo.getrawAccount() == null ? "" : billInfo.getrawAccount())
               .replace("[原始资产2]", billInfo.getrawAccount2() == null ? "" : billInfo.getrawAccount2())
               .replace("[替换资产1]", billInfo.getAccountName() == null ? "" : billInfo.getAccountName())
               .replace("[替换资产2]", billInfo.getAccountName2() == null ? "" : billInfo.getAccountName2())
               .replace("[来源App]", billInfo.getFromApp() == null ? "" : billInfo.getFromApp())
               .replace("[商户名]", billInfo.getShopAccount() == null ? "" : billInfo.getShopAccount())
               .replace("[商户备注]",billInfo.getShopRemark()==null?"":billInfo.getShopRemark());

       billInfo.setRemark(remark);
    }


}
