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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.dreamn.qianji_auto.R;

/**
 * 三级缓存之网络缓存
 */
public class NetCacheUtils {

    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private Context mContext;

    public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils, Context context) {
        mLocalCacheUtils = localCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
        mContext = context;
    }

    /**
     * 从网络下载图片
     *
     * @param ivPic 显示图片的imageview
     * @param url   下载图片的网络地址
     */
    public void getBitmapFromNet(View ivPic, String url) {

        new BitmapTask().execute(ivPic, url);//启动AsyncTask

    }

    /**
     * AsyncTask就是对handler和线程池的封装
     * 第一个泛型:参数类型
     * 第二个泛型:更新进度的泛型
     * 第三个泛型:onPostExecute的返回结果
     */
    class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

        private View ivPic;
        private String url;

        /**
         * 后台耗时操作,存在于子线程中
         *
         * @param params
         * @return
         */
        @Override
        protected Bitmap doInBackground(Object[] params) {
            ivPic = (View) params[0];
            url = (String) params[1];

            return downLoadBitmap(url);
        }

        /**
         * 更新进度,在主线程中
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Void[] values) {
            super.onProgressUpdate(values);
        }

        /**
         * 耗时方法结束后执行该方法,主线程中
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                setImage(ivPic, result);
                //从网络获取图片后,保存至本地缓存
                mLocalCacheUtils.setBitmapToLocal(url, result);
                //保存至内存中
                mMemoryCacheUtils.setBitmapToMemory(url, result);

            }
        }

        public void setImage(View ivPic, Bitmap bitmap) {
            if (bitmap == null) {
                if (ivPic instanceof ImageView) {
                    ((ImageView) ivPic).setImageResource(R.drawable.ic_null);
                } else {
                    ivPic.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_null));
                }
            } else {
                if (ivPic instanceof ImageView) {
                    ((ImageView) ivPic).setImageBitmap(bitmap);
                } else {
                    ivPic.setBackground(new BitmapDrawable(mContext.getResources(), bitmap));
                }
            }

        }
    }

    /**
     * 网络下载图片
     *
     * @param url
     * @return
     */
    private Bitmap downLoadBitmap(String url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Encoding", "identity");
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                //图片压缩
                BitmapFactory.Options options = new BitmapFactory.Options();
                if (conn.getContentLength() > 2048) {
                    options.inSampleSize = 2;//宽高压缩为原来的1/2
                }
                //
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                //options.inPreferredConfig = Bitmap.Config.RGB_565;
                return BitmapFactory.decodeStream(conn.getInputStream(), null, options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert conn != null;
            conn.disconnect();
        }

        return null;
    }
}