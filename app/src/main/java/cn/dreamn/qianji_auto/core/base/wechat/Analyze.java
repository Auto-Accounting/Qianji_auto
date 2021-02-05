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

package cn.dreamn.qianji_auto.core.base.wechat;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.core.utils.BillInfo;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class Analyze {

    private static String TAG="Qianji-Xp-Analyze";
    /*
     * 设置需要分析的内容
     * */
    protected JSONObject setContent(String content){
        try{
            return JSONObject.parseObject(content);
        }catch (Exception e){
            Logs.i(TAG,e.toString());
            return null;
        }
    }

    /**
     * 从json中获取分析结果
     * @param jsonObject jsonArray
     * @param billInfo billInfo
     * @return billInfo
     */
    BillInfo getResult(JSONObject jsonObject, BillInfo billInfo){
        return null;
    }

    /**
     * 分析数据
     */
    public  void tryAnalyze(String content, Context context){

    }
}
