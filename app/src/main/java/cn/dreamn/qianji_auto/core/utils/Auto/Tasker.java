/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
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

package cn.dreamn.qianji_auto.core.utils.Auto;

import android.content.Context;
import android.os.Handler;

import com.tencent.mmkv.MMKV;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.dreamn.qianji_auto.core.db.Cache;
import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.core.utils.Caches;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class Tasker {
    public static void  add(Context context,BillInfo billInfo){
        String md5=getMD5Str(billInfo.toString());
        Caches.add(md5,billInfo.toString(),"tasker_bill");
        run(context);
    }

    public static void run(Context context){

        Cache cache = Caches.getOne("float_lock","0");
        Logs.d("Qianji_check","记账检查...");
        if(cache!=null && cache.cacheData.equals("true")) {
            Logs.d("Qianji_check","记账已锁定...退出中");
            return;
        }

        Logs.d("Qianji_check","检查通过...");
        Caches.AddOrUpdate("float_lock","true");
        Logs.d("Qianji_check","重新锁定...");
        Cache[] caches=Caches.getType("tasker_bill");


        update(context,caches,0);



    }
    private static void update(Context context,Cache[] caches,int i){
        if(i>=caches.length) {
            Caches.AddOrUpdate("float_lock","false");
            return;
        }
        BillInfo billInfo = BillInfo.parse(caches[i].cacheData);
        CallAutoActivity.run(context, billInfo);
        new Handler().postDelayed(() -> {
            Caches.del(caches[i].cacheName);

            Cache[] caches2 = Caches.getType("tasker_bill");
            int h;
            for(h=0;h<caches2.length;h++){
                if(caches[i].id==caches2[h].id)break;
            }

            int j= 1 + h;
           update( context, caches2, j);
        },10000);

    }
    /**
     * MD5加密
     * @param str 转换前的字符串
     * @return 转换后的字符串
     */
    private static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            //返回实现指定摘要算法的 MessageDigest对象 例如：MD5、SHA算法
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset(); //重置MessageDigest对象
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));//指定byte数组更新
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        }
        //完成哈希算法，完成后   MessageDigest对象被设为初始状态，而且该方法只能调用一次
        byte[] byteArray = messageDigest.digest();
        StringBuilder md5StrBuff = new StringBuilder();
        for (byte b : byteArray) {
            if (Integer.toHexString(0xFF & b).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & b));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & b));
        }
        //16位加密，从第9位到25位
//          return md5StrBuff.substring(8, 24).toString().toUpperCase();
        return md5StrBuff.toString();
    }
}
