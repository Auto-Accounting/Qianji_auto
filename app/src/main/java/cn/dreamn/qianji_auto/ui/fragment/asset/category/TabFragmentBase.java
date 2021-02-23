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
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Helper.BookNames;
import cn.dreamn.qianji_auto.core.db.Helper.CategoryNames;
import cn.dreamn.qianji_auto.core.db.Table.CategoryName;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.tools.Logs;

/**
 * @author xuexiang
 * @since 2020/4/21 12:24 AM
 */
public class TabFragmentBase extends BaseFragment {


    private static final String KEY_TITLE = "title";

    @BindView(R.id.expandableListView)
    ExpandableListView expandableListView;


    private Unbinder mUnbinder;

    AdapterData adapterData;
    Bundle[] parentArr = new Bundle[0];
    Bundle[][] childArr = new Bundle[0][];

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

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void initViews() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_content, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }


    private Bundle getBundle(CategoryName categoryName) {
        Bundle bundle = new Bundle();
        bundle.putString("name", categoryName.name);
        bundle.putInt("id", categoryName.id);
        bundle.putString("icon", categoryName.icon);
        bundle.putString("level", categoryName.level);
        bundle.putString("type", categoryName.type);
        bundle.putString("self_id", categoryName.self_id);
        bundle.putString("parent_id", categoryName.parent_id);
        return bundle;
    }

    private void initView() {

        adapterData = new AdapterData();


        expandableListView.setOnItemLongClickListener((arg0, arg1, arg2, arg3) -> {

            if (ExpandableListView.getPackedPositionType(arg3) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                long packPos = ((ExpandableListView) arg0).getExpandableListPosition(arg2);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packPos);
                int childPosition = ExpandableListView.getPackedPositionChild(packPos);


            } else {
                long packPos = ((ExpandableListView) arg0).getExpandableListPosition(arg2);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packPos);
                Bundle bundle = parentArr[groupPosition];
                change(bundle.getInt("id"), "-1", "1", bundle.getString("type"), bundle.getString("name"));

            }

            return true;
        });
        expandableListView.setOnChildClickListener((a, v, groupPos, childPos, id) -> {
            Bundle bundle = childArr[groupPos][childPos];
            if (bundle.getString("name").equals("添加子类")) {
                change(-1, bundle.getString("parent_id"), "2", bundle.getString("type"), "");
            } else {
                change(bundle.getInt("id"), bundle.getString("parent_id"), "2", bundle.getString("type"), bundle.getString("name"));
            }

            return true;
        });


        refresh();
    }

    protected void refresh() {
        getAdapterData();
        adapterData.setData(getContext(), parentArr, childArr);
        expandableListView.setGroupIndicator(null);
        expandableListView.setAdapter(adapterData);

    }

    public void getAdapterData() {
        ArrayList<Bundle> parent = new ArrayList<>();
        ArrayList<Bundle[]> child = new ArrayList<>();

        if (title.equals("收入")) {
            CategoryName[] categoryNames = CategoryNames.getParentByIncome();
            for (CategoryName categoryName : categoryNames) {
                Bundle bundle = getBundle(categoryName);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByIncome(categoryName.self_id);
                Bundle[] childs = new Bundle[categoryNames1.length + 1];
                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = getBundle(categoryNames1[i]);
                    childs[i] = bundle2;
                }
                Bundle bundle3 = new Bundle();
                bundle3.putString("name", "添加子类");
                bundle3.putInt("id", -2);
                bundle3.putString("icon", "");
                bundle3.putString("type", categoryName.type);
                bundle3.putString("parent_id", categoryName.self_id);
                childs[categoryNames1.length] = bundle3;
                child.add(childs);
            }
        } else {
            CategoryName[] categoryNames = CategoryNames.getParentByPay();
            for (CategoryName categoryName : categoryNames) {

                Bundle bundle = getBundle(categoryName);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByPay(categoryName.self_id);
                Bundle[] childs = new Bundle[categoryNames1.length + 1];
                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = getBundle(categoryNames1[i]);
                    childs[i] = bundle2;
                }
                Bundle bundle3 = new Bundle();
                bundle3.putString("name", "添加子类");
                bundle3.putInt("id", -2);
                bundle3.putString("type", categoryName.type);
                bundle3.putString("parent_id", categoryName.self_id);
                bundle3.putString("icon", "");
                childs[categoryNames1.length] = bundle3;
                child.add(childs);
            }
        }

        parentArr = parent.toArray(new Bundle[0]);
        childArr = child.toArray(new Bundle[0][0]);
    }

    public void change(int id, String parent_id, String level, String type, String def) {

        if (type == null || type.equals("-1")) {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.tip_options)
                    .items(R.array.menu_cate)
                    .itemsCallback((dialog, itemView, position, text) -> {

                        change(id, parent_id, level, String.valueOf(position), def);

                    })
                    .show();
        } else {
            showInputDialog("添加分类", "请输入分类名称", def, str -> {

                if (id != -1) {
                    if (!CategoryNames.update(id, str, type)) {
                        SnackbarUtils.Long(getView(), getString(R.string.set_failed)).info().show();
                    } else {
                        SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                        refresh();
                    }
                } else {
                    if (!CategoryNames.insert(str, "", level, type, "", parent_id)) {
                        SnackbarUtils.Long(getView(), getString(R.string.set_failed)).info().show();
                    } else {
                        SnackbarUtils.Long(getView(), getString(R.string.set_success)).info().show();
                        refresh();
                    }
                }


                // refresh();
            });
        }
    }

    @Override
    public void onDestroyView() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        super.onDestroyView();
        // Log.e(TAG, "onDestroyView:" + title);

    }

    public void showInputDialog(String title, String tip, String def, CallBack callBack) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .content(tip)
                .input(
                        getString(R.string.input_tip),
                        def,
                        false,
                        ((dialog, input) -> {
                        })
                )
                .positiveText(getString(R.string.input_ok))
                .negativeText(getString(R.string.set_cancel))
                .onPositive((dialog, which) -> callBack.onResponse(dialog.getInputEditText().getText().toString()))
                .show();
    }

    // 回调接口
    public interface CallBack {
        void onResponse(String data);
    }
}

