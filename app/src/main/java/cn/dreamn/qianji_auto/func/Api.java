package cn.dreamn.qianji_auto.func;

import android.content.Context;


import static cn.dreamn.qianji_auto.func.Fun.browser;


public class Api {
    public static final String TYPE_PAY="0";
    public static final String TYPE_GAIN="1";


    public static void Send2Qianji(Context context, String type, String money, String remark, String accountname) {

        String bookName=Storage.type(Storage.Set).get("set_bookname","默认账本");
        String add="&bookname="+bookName;
        if(bookName.equals("默认账本"))add="";

        String url="qianji://publicapi/addbill?&catetheme=auto&type="+type+"&money="+money+"&remark="+remark+"&catechoose=1&accountname="+accountname+add;
        Log.i("钱迹Uri-Scheme",url);
        browser(context,url);

    }
    public static String getQianji( String type, String money, String remark, String accountname) {
        String bookName=Storage.type(Storage.Set).get("set_bookname","默认账本");
        String add="&bookname="+bookName;
        if(bookName.equals("默认账本"))add="";
        return "qianji://publicapi/addbill?&catetheme=auto&type="+type+"&money="+money+"&remark="+remark+"&catechoose=1&accountname="+accountname+add;
    }
    public static void Send2Qianji(Context context, String type, String money, String remark, String accountname, String sort){
        String bookName=Storage.type(Storage.Set).get("set_bookname","默认账本");
        String add="&bookname="+bookName;
        if(bookName.equals("默认账本"))add="";
        String url="qianji://publicapi/addbill?&catetheme=auto&type="+type+"&money="+money+"&remark="+remark+"&catename="+sort+"&accountname="+accountname+add;
        Log.i("钱迹Uri-Scheme",url);
        browser(context,url);
    }
}
