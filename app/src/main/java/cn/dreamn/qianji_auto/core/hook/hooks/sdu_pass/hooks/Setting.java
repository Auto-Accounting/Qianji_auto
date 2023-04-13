package cn.dreamn.qianji_auto.core.hook.hooks.sdu_pass.hooks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.ui.utils.ScreenUtils;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * @author JiunnTarn
 */
public class Setting {
    public static void init(Utils utils) {
        XposedHelpers.findAndHookMethod(Activity.class, "onCreate", Bundle.class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) {
                final Activity activity = (Activity) param.thisObject;
                final String activityClzName = activity.getClass().getName();
                if (activityClzName.contains(".SystemSettingActivity")) {
                    TaskThread.onMain(100, () -> doSettingsMenuInject(activity, utils));
                }
            }
        });
    }

    private static void doSettingsMenuInject(Activity activity, Utils utils) {
        Context mContext = utils.getContext();
        utils.log("hook 山大v卡通设置");

        RelativeLayout qianjiAutoListTile = new RelativeLayout(activity);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.dip2px(activity, 45));
        layoutParams.setMargins(0, ScreenUtils.dip2px(activity, 15), 0, 0);
        qianjiAutoListTile.setLayoutParams(layoutParams);
        qianjiAutoListTile.setVerticalGravity(Gravity.CENTER_VERTICAL);
        qianjiAutoListTile.setBackgroundColor(0xFFFFFFFF);
        qianjiAutoListTile.setOnClickListener(view -> {
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(BuildConfig.APPLICATION_ID);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        TextView title = new TextView(activity);
        title.setText("自动记账");
        title.setPadding(ScreenUtils.dip2px(activity, 15), 0, 0, 0);
        title.setTextSize(16);
        RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        title.setLayoutParams(titleParams);

        TextView version = new TextView(activity);
        version.setText(BuildConfig.VERSION_NAME);
        version.setPadding(0, 0, ScreenUtils.dip2px(activity, 15), 0);
        version.setTextSize(16);
        RelativeLayout.LayoutParams versionParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        versionParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        version.setLayoutParams(versionParams);

        qianjiAutoListTile.addView(title);
        qianjiAutoListTile.addView(version);

        int settingListId = activity.getResources().getIdentifier("ll_setting_item", "id", "com.synjones.xuepay.sdu");
        LinearLayout settingList = activity.findViewById(settingListId);

        settingList.addView(qianjiAutoListTile);
    }
}
