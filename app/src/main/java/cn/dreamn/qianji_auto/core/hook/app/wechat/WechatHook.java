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

package cn.dreamn.qianji_auto.core.hook.app.wechat;

import cn.dreamn.qianji_auto.core.hook.HookBase;

public class WechatHook extends HookBase {

    @Override
    public void hookFirst() throws Error {
        //添加设置
       try{
           hookSetting.init(utils);
       }catch(Error |Exception e){
           utils.log("微信设置hook失败："+e.toString());
       }
        //获取用户昵称
        try{
            hookNickName.init(utils);
        }catch(Error |Exception e){
            utils.log("微信用户昵称hook失败："+e.toString());
        }
        try{
            //获取支付的一些信息
            hookPayTools.init(utils);
        }catch(Error |Exception e){
            utils.log("微信支付信息hook失败："+e.toString());
        }
        try{
            //获取收红包的信息
            hookRedPackage.init(utils);
        }catch(Error |Exception e){
            utils.log("微信红包hook失败："+e.toString());
        }
        try{
            //获取插入数据库的信息
            hookMsg.init(utils);
        }catch(Error |Exception e){
            utils.log("微信数据库hook失败："+e.toString());
        }




    }

    @Override
    public String getPackPageName() {
        return "com.tencent.mm";
    }

    @Override
    public String getAppName() {
        return "微信";
    }

    @Override
    public String[] getAppVer() {
        return null;
    }




}
