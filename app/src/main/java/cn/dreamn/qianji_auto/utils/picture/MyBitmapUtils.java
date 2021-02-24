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

package cn.dreamn.qianji_auto.utils.picture;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.hook.Task;
import cn.dreamn.qianji_auto.utils.tools.Logs;
import cn.dreamn.qianji_auto.utils.tools.ViewUtil;

/**
 * 自定义的BitmapUtils,实现三级缓存
 */
public class MyBitmapUtils {

    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private Context mContext;

    public MyBitmapUtils(Context context) {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils(context);
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils, context);
        mContext = context;
    }

    public void setImage(View ivPic, Bitmap bitmap) {
        // Logs.d("设置图片");
        if (bitmap == null) {
            if (ivPic instanceof ImageView) {
                ((ImageView) ivPic).setImageResource(R.drawable.ic_null);
            } else {
                ivPic.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_null));
            }
        } else {

            // Logs.d("图片不为空");
            //图片缩放
            Task.onMain(() -> {
                if (ivPic instanceof ImageView) {
                    ((ImageView) ivPic).setImageBitmap(bitmap);
                } else {
                    //      Logs.d("设置背景");
                    ivPic.setBackground(new BitmapDrawable(mContext.getResources(), bitmap));
                }
            });


        }

    }

    public void disPlay(View ivPic, String url) {
        setImage(ivPic, null);

        if (url == null || url.equals("")) return;
        Bitmap bitmap = getBitmap(url);
        if (bitmap == null) {
            mNetCacheUtils.getBitmapFromNet(ivPic, url);
        } else {
            setImage(ivPic, bitmap);
        }
    }

    public Bitmap getBitmap(String url) {
        Bitmap bitmap;
        //内存缓存
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap != null) {

            return bitmap;
        }

        //本地缓存
        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if (bitmap != null) {
            mMemoryCacheUtils.setBitmapToMemory(url, bitmap);//缓存一个到内存
            return bitmap;
        }
        return null;
    }
}
