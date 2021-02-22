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

package cn.dreamn.qianji_auto.ui.fragment.asset.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatImageView;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.utils.picture.MyBitmapUtils;

class AdapterData extends BaseExpandableListAdapter {

    Bundle[] groups;
    Bundle[][] childs;

    private Context mContext;
    private LayoutInflater mInflater = null;

    public AdapterData(Context context, Bundle[] group, Bundle[][] child) {
        groups = group;
        childs = child;
        mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /*一级列表个数*/
    @Override
    public int getGroupCount() {
        return groups.length;
    }

    /*每个二级列表的个数*/
    @Override
    public int getChildrenCount(int groupPosition) {
        return childs[groupPosition].length;
    }

    /*一级列表中单个item*/
    @Override
    public Object getGroup(int groupPosition) {
        return groups[groupPosition];
    }

    /*二级列表中单个item*/
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childs[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /*每个item的id是否固定，一般为true*/
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
     * isExpanded 是否已经展开
     * */
    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_data, null);
        }
        FrameLayout fl_title = convertView.findViewById(R.id.fl_title);
        ImageView iv_img = convertView.findViewById(R.id.iv_img);
        TextView tv_title = convertView.findViewById(R.id.tv_title);
        AppCompatImageView iv_indicator = convertView.findViewById(R.id.iv_indicator);
        tv_title.setText(groups[groupPosition].get("name").toString());
        MyBitmapUtils myBitmapUtils = new MyBitmapUtils(mContext);
        myBitmapUtils.disPlay(iv_img, groups[groupPosition].get("icon").toString());
        iv_img.setColorFilter(mContext.getColor(R.color.xui_btn_blue_normal_color));
        //控制是否展开图标
        if (isExpanded) {
            iv_indicator.setRotation(90);
        } else {
            iv_indicator.setRotation(0);
        }
        return convertView;
    }

    /*#TODO 填充二级列表*/
    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.adapter_data, null);
        }
        FrameLayout fl_title = convertView.findViewById(R.id.fl_title);
        ImageView iv_img = convertView.findViewById(R.id.iv_img);
        TextView tv_title = convertView.findViewById(R.id.tv_title);
        AppCompatImageView iv_indicator = convertView.findViewById(R.id.iv_indicator);
        fl_title.setBackgroundColor(mContext.getColor(R.color.background));
        tv_title.setText(childs[groupPosition][childPosition].get("name").toString());

        MyBitmapUtils myBitmapUtils = new MyBitmapUtils(mContext);
        myBitmapUtils.disPlay(iv_img, childs[groupPosition][childPosition].get("icon").toString());
        iv_img.setColorFilter(mContext.getColor(R.color.xui_btn_blue_normal_color));
        // tv.setText(childs[groupPosition][childPosition]);
        iv_indicator.setVisibility(View.GONE);
        return convertView;
    }

    /*二级列表中每个能否被选中，如果有点击事件一定要设为true*/
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}