package cn.dreamn.qianji_auto.permission;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;

import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class PermissionList {
    public static JSONObject get(Context context) {
        String json = Tool.getJson(context, "permission");
        return JSONObject.parseObject(json);
    }
}
