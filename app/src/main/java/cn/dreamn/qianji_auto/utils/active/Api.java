/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.dreamn.qianji_auto.utils.active;

import android.content.Context;

import cn.dreamn.qianji_auto.utils.file.Log;
import cn.dreamn.qianji_auto.utils.file.Storage;

import static cn.dreamn.qianji_auto.utils.active.Fun.browser;


public class Api {
    public static final String TYPE_PAY="0";
    public static final String TYPE_GAIN="1";


    public static void Send2Qianji(Context context, String type, String money, String remark, String accountname) {
        String url=getQianji(type,money,remark,accountname,"");
        Log.i("钱迹Uri-Scheme",url);
        browser(context,url);

    }
    public static String getQianji( String type, String money, String remark, String accountname, String sort) {
        String bookName=Storage.type(Storage.Set).get("bookname","默认账本");
        String add="&bookname="+bookName;
        if(bookName.equals("默认账本"))add="";
        String add2="&catechoose=1";
        if(sort!=null&& !sort.equals(""))add2="&catename="+sort;
        return "qianji://publicapi/addbill?&catetheme=auto&type="+type+"&money="+money+"&remark="+remark+"&accountname="+accountname+add+add2;
    }
    public static void Send2Qianji(Context context, String type, String money, String remark, String accountname, String sort){
        String url=getQianji(type,money,remark,accountname,sort);
        Log.i("钱迹Uri-Scheme",url);
        browser(context,url);
    }
}
