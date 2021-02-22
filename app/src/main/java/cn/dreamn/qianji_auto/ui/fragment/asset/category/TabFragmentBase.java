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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Helper.CategoryNames;
import cn.dreamn.qianji_auto.core.db.Table.CategoryName;

/**
 * @author xuexiang
 * @since 2020/4/21 12:24 AM
 */
public class TabFragmentBase extends Fragment {


    private static final String KEY_TITLE = "title";

    @BindView(R.id.expandableListView)
    ExpandableListView expandableListView;


    private Unbinder mUnbinder;

    @AutoWired(name = KEY_TITLE)
    String title;


    public static TabFragmentBase newInstance(String title) {
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        TabFragmentBase fragment = new TabFragmentBase();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XRouter.getInstance().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_content, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {

        ArrayList<Bundle> parent = new ArrayList<>();
        ArrayList<Bundle[]> child = new ArrayList<>();

        if (title.equals("收入")) {
            CategoryName[] categoryNames = CategoryNames.getParentByIncome();
            for (CategoryName categoryName : categoryNames) {
                Bundle bundle = new Bundle();
                bundle.putString("name", categoryName.name);
                bundle.putInt("id", categoryName.id);
                bundle.putString("icon", categoryName.icon);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByIncome(categoryName.self_id);
                Bundle[] childs = new Bundle[categoryNames1.length + 1];
                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("name", categoryNames1[i].name);
                    bundle2.putInt("id", categoryNames1[i].id);
                    bundle2.putString("icon", categoryNames1[i].icon);
                    childs[i] = bundle2;
                }
                Bundle bundle3 = new Bundle();
                bundle3.putString("name", "添加子类");
                bundle3.putInt("id", -2);
                bundle3.putString("icon", "");
                childs[categoryNames1.length] = bundle3;
                child.add(childs);
            }
        } else {
            CategoryName[] categoryNames = CategoryNames.getParentByPay();
            for (CategoryName categoryName : categoryNames) {
                Bundle bundle = new Bundle();
                bundle.putString("name", categoryName.name);
                bundle.putInt("id", categoryName.id);
                bundle.putString("icon", categoryName.icon);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByPay(categoryName.self_id);
                Bundle[] childs = new Bundle[categoryNames1.length + 1];
                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("name", categoryNames1[i].name);
                    bundle2.putInt("id", categoryNames1[i].id);
                    bundle2.putString("icon", categoryNames1[i].icon);
                    childs[i] = bundle2;
                }
                Bundle bundle3 = new Bundle();
                bundle3.putString("name", "添加子类");
                bundle3.putInt("id", -2);
                bundle3.putString("icon", "");
                childs[categoryNames1.length] = bundle3;
                child.add(childs);
            }
        }


        expandableListView.setGroupIndicator(null);
        expandableListView.setAdapter(new AdapterData(getContext(), parent.toArray(new Bundle[0]), child.toArray(new Bundle[0][0])));
    }

    @Override
    public void onDestroyView() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroyView();
        // Log.e(TAG, "onDestroyView:" + title);

    }
}

