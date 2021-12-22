package cn.dreamn.qianji_auto.ui.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class Recent {
    private static ImageView imageView;

    public static Bitmap activityShot(Activity activity) {
        /*获取windows中最顶层的view*/
        View view = activity.getWindow().getDecorView();

        //允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        //获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        WindowManager windowManager = activity.getWindowManager();

        //获取屏幕宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        //去掉状态栏
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeight, width,
                height - statusBarHeight);

        //销毁缓存信息
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);


        return bitmap;
    }

    public static Bitmap rsBlur(Context context, Bitmap source, int radius) {
        Bitmap inputBmp = source;
        //(1)
        //初始化一个RenderScript Context
        RenderScript renderScript = RenderScript.create(context);

//        Log.i(TAG,"setDrawingCacheEnabledle size:"+inputBmp.getWidth()+"*"+inputBmp.getHeight());

        // Allocate memory for Renderscript to work with
        //(2)
        //创建输入输出的allocation
        final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        //(3)
        // Load up an instance of the specific script that we want to use.
        //创建ScriptIntrinsic
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        //(4)
        //填充数据
        scriptIntrinsicBlur.setInput(input);
        //(5)
        // Set the blur radius
        //设置模糊半径
        scriptIntrinsicBlur.setRadius(radius);
        //(6)
        // Start the ScriptIntrinisicBlur
        //启动内核
        scriptIntrinsicBlur.forEach(output);
        //(7)
        // Copy the output to the blurred bitmap
        //copy数据
        output.copyTo(inputBmp);
        //(8)
        //销毁renderScript
        renderScript.destroy();
        return inputBmp;

    }

    public static void onRecentClick(Activity activity) {
//        Toast.makeText(this,"按了多任务键",Toast.LENGTH_LONG).show();
        ViewGroup group = (ViewGroup) activity.getWindow().getDecorView();
        if (imageView != null)
            group.removeView(imageView);
//      创建imageview
        imageView = new ImageView(activity);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);

//      截图，并将图片高斯模糊处理
        Bitmap bitmap = rsBlur(activity, activityShot(activity), 20);
//      将处理后的图片填充到imageview
        imageView.setImageBitmap(bitmap);
        group.addView(imageView);

    }

    public static void onRecentEnd(Activity activity) {
        ViewGroup group = (ViewGroup) activity.getWindow().getDecorView();
        for (int i = 0; i < group.getChildCount(); i++) {
            group.removeView(imageView);
        }
    }
}
