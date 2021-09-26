package cn.dreamn.qianji_auto.utils.runUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

/**
 * Glide 加载 简单判空封装 防止异步加载数据时调用Glide 抛出异常
 * Created by Li_Xavier on 2017/6/20 0020.
 */
public class GlideLoadUtils {
    private final String TAG = "ImageLoader";

    /**
     * 借助内部类 实现线程安全的单例模式
     * 属于懒汉式单例，因为Java机制规定，内部类SingletonHolder只有在getInstance()
     * 方法第一次调用的时候才会被加载（实现了lazy），而且其加载过程是线程安全的。
     * 内部类加载的时候实例化一次instance。
     */
    public GlideLoadUtils() {
    }

    public static GlideLoadUtils getInstance() {
        return GlideLoadUtilsHolder.INSTANCE;
    }

    private void setImg(Context context, String url, Object imageView, int default_image) {
        if (imageView instanceof ImageView) {
            Glide.with(context).load(url).centerCrop().error(default_image).into((ImageView) imageView);
        } else if (imageView instanceof View) {
            Glide.with(context)
                    .load(url)
                    .centerCrop()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            ((View) imageView).setBackground(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }

    }

    /**
     * Glide 加载 简单判空封装 防止异步加载数据时调用Glide 抛出异常
     *
     * @param context
     * @param url           加载图片的url地址  String
     * @param imageView     加载图片的ImageView 控件
     * @param default_image 图片展示错误的本地图片 id
     */
    public void glideLoad(Context context, String url, View imageView, int default_image) {
        if (context != null) {
            setImg(context, url, imageView, default_image);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void glideLoad(Activity activity, String url, ImageView imageView, int default_image) {
        if (!activity.isDestroyed()) {
            setImg(activity, url, imageView, default_image);
        } else {
            Log.i(TAG, "Picture loading failed,activity is Destroyed");
        }
    }

    public void glideLoad(Fragment fragment, String url, ImageView imageView, int default_image) {
        if (fragment != null && fragment.getActivity() != null) {
            setImg(fragment.getContext(), url, imageView, default_image);
        } else {
            Log.i(TAG, "Picture loading failed,fragment is null");
        }
    }


    private static class GlideLoadUtilsHolder {
        private final static GlideLoadUtils INSTANCE = new GlideLoadUtils();
    }
}
