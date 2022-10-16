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

package cn.dreamn.qianji_auto.core.hook.hooks.wechat.hooks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.core.hook.ui.ViewUtil;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;
import cn.dreamn.qianji_auto.ui.utils.ScreenUtils;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Setting {
    public static void init(Utils utils) throws ClassNotFoundException {
        Class<?> setting = utils.getClassLoader().loadClass("com.tencent.mm.plugin.setting.ui.setting.SettingsUI");
        XposedHelpers.findAndHookMethod(setting, "onCreate", Bundle.class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam param) throws IllegalAccessException {
                Activity activity = (Activity) param.thisObject;
                TaskThread.onMain(100, () -> doSettingsMenuInject(activity, utils));
            }
        });
    }


    private static void doSettingsMenuInject(Activity activity, Utils utils) {
        Context mContext = utils.getContext();
        ListView itemView = (ListView) ViewUtil.findViewByName(activity, "android", "list");
        if (ViewUtil.findViewByText(itemView, "💰    自动记账") != null) {
            return;
        }

        boolean isDarkMode = ThemeManager.isDarkMode(activity);

        LinearLayout settingsItemRootLLayout = new LinearLayout(activity);
        settingsItemRootLLayout.setOrientation(LinearLayout.VERTICAL);
        settingsItemRootLLayout.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        settingsItemRootLLayout.setPadding(0, 0, 0, 0);

        LinearLayout settingsItemLinearLayout = new LinearLayout(activity);
        settingsItemLinearLayout.setOrientation(LinearLayout.VERTICAL);

        settingsItemLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        LinearLayout itemHlinearLayout = new LinearLayout(activity);
        itemHlinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemHlinearLayout.setWeightSum(1);
        itemHlinearLayout.setBackground(ViewUtil.genBackgroundDefaultDrawable(isDarkMode ? 0xFF191919 : Color.WHITE, isDarkMode ? 0xFF1D1D1D : 0xFFE5E5E5));
        itemHlinearLayout.setGravity(Gravity.CENTER_VERTICAL);
        itemHlinearLayout.setClickable(true);
        itemHlinearLayout.setOnClickListener(view -> {
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        int defHPadding = ScreenUtils.dip2px(activity, 15);

        TextView itemNameText = new TextView(activity);
        itemNameText.setTextColor(isDarkMode ? 0xFFD3D3D3 : 0xFF353535);
        itemNameText.setText("💰    自动记账");
        itemNameText.setGravity(Gravity.CENTER_VERTICAL);
        itemNameText.setPadding(ScreenUtils.dip2px(activity, 16), 0, 0, 0);
        itemNameText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        TextView itemSummerText = new TextView(activity);
        //  StyleUtil.apply(itemSummerText);
        itemSummerText.setText(BuildConfig.VERSION_NAME);
        itemSummerText.setGravity(Gravity.CENTER_VERTICAL);
        itemSummerText.setPadding(0, 0, defHPadding, 0);
        itemSummerText.setTextColor(isDarkMode ? 0xFF656565 : 0xFF999999);


        itemHlinearLayout.addView(itemNameText, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        itemHlinearLayout.addView(itemSummerText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View lineView = new View(activity);
        lineView.setBackgroundColor(isDarkMode ? 0xFF2E2E2E : 0xFFD5D5D5);
        settingsItemLinearLayout.addView(lineView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        settingsItemLinearLayout.addView(itemHlinearLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(activity, 55)));

        settingsItemRootLLayout.addView(settingsItemLinearLayout);
        settingsItemRootLLayout.setTag(BuildConfig.APPLICATION_ID);

        itemView.addFooterView(settingsItemRootLLayout);
    }
}
