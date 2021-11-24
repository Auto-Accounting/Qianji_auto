package cn.dreamn.qianji_auto.bills;

import android.os.Handler;

import com.tencent.mmkv.MMKV;

import cn.dreamn.qianji_auto.data.data.RegularCenter;
import cn.dreamn.qianji_auto.data.database.Helper.Assets;
import cn.dreamn.qianji_auto.data.database.Helper.BookNames;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;

public class BillReplace {


    public static void addMoreInfo(Handler mHandler, BillInfo billInfo) {


        TaskThread.onThread(() -> {
            if (billInfo.getBookName().equals("") || billInfo.getBookName().equals("默认账本")) {
                billInfo.setBookName(BookNames.getDefault());//设置自动记账的账本名
            }

            if (billInfo.getRawAccount() == null || billInfo.getRawAccount().equals("null") || billInfo.getRawAccount().equals("undefined")) {
                billInfo.setRawAccount("未识别资产（" + billInfo.getFromApp() + "）");
            }
            Assets.getMap(billInfo.getRawAccount(), mapName -> {
                billInfo.setAccountName((String) mapName);
                Assets.getMap(billInfo.getRawAccount2(), mapName2 -> {
                    billInfo.setAccountName2((String) mapName2);
                    MMKV mmkv = MMKV.defaultMMKV();
                    if (mmkv.getBoolean("need_cate", true)) {
                        RegularCenter
                                .getInstance("category")
                                .run(billInfo, null, obj -> {
                                    String cate = (String) obj;
                                    if (cate.equals("NotFound")) {
                                        billInfo.setCateName("其它");//设置自动分类
                                    } else {
                                        billInfo.setCateName(cate);//设置自动分类
                                    }
                                    HandlerUtil.send(mHandler, billInfo, 2);
                                });
                    } else {
                        billInfo.setCateName("其它");//设置自动分类
                        HandlerUtil.send(mHandler, billInfo, 2);
                    }
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
               .replace("[原始资产1]", billInfo.getRawAccount() == null ? "" : billInfo.getRawAccount())
               .replace("[原始资产2]", billInfo.getRawAccount2() == null ? "" : billInfo.getRawAccount2())
               .replace("[替换资产1]", billInfo.getAccountName() == null ? "" : billInfo.getAccountName())
               .replace("[替换资产2]", billInfo.getAccountName2() == null ? "" : billInfo.getAccountName2())
               .replace("[来源App]", billInfo.getFromApp() == null ? "" : billInfo.getFromApp())
               .replace("[商户名]", billInfo.getShopAccount() == null ? "" : billInfo.getShopAccount())
               .replace("[商户备注]",billInfo.getShopRemark()==null?"":billInfo.getShopRemark());
       billInfo.setRemark(remark);
    }


}
