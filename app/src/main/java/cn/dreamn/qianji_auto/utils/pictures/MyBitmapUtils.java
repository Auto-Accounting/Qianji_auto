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

package cn.dreamn.qianji_auto.utils.pictures;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.shehuan.niv.NiceImageView;

import cn.dreamn.qianji_auto.R;

/**
 * 自定义的BitmapUtils,实现三级缓存
 */
public class MyBitmapUtils {

    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private Context mContext;
    private Handler mHandler;

    public MyBitmapUtils(Context context, Handler mHandler) {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils(context);
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils, context,mHandler);
        mContext = context;
        this.mHandler=mHandler;
    }

    public static void setImage(Context mContext,View ivPic, Bitmap bitmap) {

        // Logs.d("设置图片");
        if (bitmap == null) {
            if (ivPic instanceof ImageView) {
                ((ImageView) ivPic).setImageResource(R.drawable.ic_empty);
            } else {
                ivPic.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_empty));
            }
        } else {

            if (ivPic instanceof ImageView) {
                ((ImageView) ivPic).setImageBitmap(bitmap);
            } else {
                //      Logs.d("设置背景");
                ivPic.setBackground(new BitmapDrawable(mContext.getResources(), bitmap));
            }

        }

    }

    public void disPlay(View ivPic, String url) {

        if (url == null || url.equals("")) {
            Message message= mHandler.obtainMessage() ;
            message.obj = new Object[]{ivPic,null};
            mHandler.sendMessage(message);
            return;
        }
        Bitmap bitmap = getBitmap(url);
        if (bitmap == null) {
            mNetCacheUtils.getBitmapFromNet(ivPic, url);
        } else {
            Message message= mHandler.obtainMessage() ;
            message.obj = new Object[]{ivPic,bitmap};
            mHandler.sendMessage(message);
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
