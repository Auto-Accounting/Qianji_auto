package cn.dreamn.qianji_auto.core.hook.hooks.wangc.hooks;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;

import cn.dreamn.qianji_auto.BuildConfig;
import cn.dreamn.qianji_auto.core.hook.Utils;
import cn.dreamn.qianji_auto.ui.theme.ThemeManager;
import cn.dreamn.qianji_auto.ui.utils.ScreenUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Setting {
    public static void init(@NonNull Utils utils) throws ClassNotFoundException {
        ClassLoader mAppClassLoader = utils.getClassLoader();
        XC_MethodHook methodHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(@NonNull MethodHookParam param) throws Throwable {
                // 获取到Fragment的Context
                Context context = ((View) param.args[1]).getContext();

                // 获取资源以解析ID
                Resources resources = context.getResources();
                int dataLayoutId = resources.getIdentifier("data_layout", "id", "com.wangc.bill");

                // 获取Fragment的根布局
                ViewGroup rootView = (ViewGroup) param.getResult();
                ViewGroup dataLayout = rootView.findViewById(dataLayoutId);
                assert dataLayout != null;
                AddView(dataLayout, context);
            }
        };

        String cls ="com.wangc.bill.Fragment.MyFragment";
        String method = "onCreateView";

        try {
            utils.log("一木记账 Seting.init Hook<" + cls + "." + method + ">");
            XposedHelpers.findAndHookMethod(cls, mAppClassLoader, method, LayoutInflater.class, ViewGroup.class, android.os.Bundle.class, methodHook);

        } catch (Exception e) {
            utils.log("一木记账 Seting.init Hook <" + cls + "." + method + "> HookError " + e);
        }
    }

    public static void AddView(@NonNull ViewGroup dataLayout, Context context) {

        RelativeLayout existingLayout = (RelativeLayout) dataLayout.getChildAt(2);
        assert existingLayout != null;
        // 复制现有的RelativeLayout的布局参数
        ViewGroup.LayoutParams layoutParams = existingLayout.getLayoutParams();
        // 创建一个新的RelativeLayout
        RelativeLayout newLayout = new RelativeLayout(context);
        newLayout.setLayoutParams(layoutParams);
        int[] colors = new int[]{Color.parseColor("#80CCCCCC"), Color.parseColor("#80CCCCCC")}; // 水波纹颜色
        RippleDrawable rippleDrawable = new RippleDrawable(ColorStateList.valueOf(colors[0]), null, new ColorDrawable(Color.WHITE));
        newLayout.setBackground(rippleDrawable); // 设置水波纹效果

        // 创建一个新的TextView实例
        TextView textView = new TextView(context);
        textView.setText("💰    自动记账");
        textView.setTextColor(Color.BLACK); // 设置文本颜色为黑色
        // 设置TextView的布局参数
        RelativeLayout.LayoutParams textViewLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        textViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL); // 设置文本视图垂直居中
        textViewLayoutParams.leftMargin = 50; // 设置左边距
        textView.setLayoutParams(textViewLayoutParams);
        newLayout.addView(textView);

        ImageView NewImageView = new ImageView(context);
        RelativeLayout.LayoutParams ImageViewLayoutParams = new RelativeLayout.LayoutParams(
                54, 54
        );
        ImageViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        ImageViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        ImageViewLayoutParams.rightMargin = 50; // 设置右边距
        ImageView childImageView = (ImageView) existingLayout.getChildAt(2);
        Drawable drawable = childImageView.getDrawable(); // 获取图片资源
        assert drawable != null;
        NewImageView.setImageDrawable(drawable); // 将图片资源设置到目标 ImageView 中
        // 设置ImageView的布局参数
        NewImageView.setLayoutParams(ImageViewLayoutParams);
        newLayout.addView(NewImageView);

        // 设置点击事件
        newLayout.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClassName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID+".ui.activity.MainActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        // 将布局添加到 data_layout 中
        dataLayout.addView(newLayout);
    }
}
