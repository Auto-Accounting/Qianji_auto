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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tencent.mmkv.MMKV;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Helper.BookNames;
import cn.dreamn.qianji_auto.core.db.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.adapter.AssetAdapter;
import cn.dreamn.qianji_auto.ui.adapter.MapAdapter;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;

/**
 * @author xuexiang
 * @since 2020/4/21 12:24 AM
 */
public class TabFragmentBase extends BaseFragment {


    private static final String KEY_TITLE = "title";

    @BindView(R.id.expandableListView)
    ExpandableListView expandableListView;

    CateChoose cateChoose;
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



    private void initView() {


        cateChoose = new CateChoose(expandableListView, getContext(), title, true);

        cateChoose.refresh();


        cateChoose.setOnClick(new CateChoose.CallBack() {
            @Override
            public void OnLongClickGroup(Bundle parent) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.tip_options)
                        .items(R.array.menu_values)
                        .itemsCallback((dialog, itemView, position, text) -> {
                            if (position == 0) {

                                new MaterialDialog.Builder(getContext())
                                        .title("一级分类删除警告")
                                        .content("删除该一级分类的同时会删除该分类下所有二级分类，是否确认？")
                                        .negativeText("确认")
                                        .positiveText("取消")
                                        .onNegative((dialog2, which) -> {
                                            CategoryNames.del(parent.getInt("id"));
                                            refresh();
                                        })
                                        .show();

                            } else {

                                change(parent.getInt("id"), "-1", "1", parent.getString("type"), parent.getString("name"));

                            }

                        })
                        .show();
            }

            @Override
            public void OnLongClickChild(Bundle parent, Bundle child) {

            }


            @Override
            public void OnClickChild(Bundle parent, Bundle child) {
                if (child.getString("name").equals("添加子类")) {
                    change(-1, child.getString("parent_id"), "2", child.getString("type"), "");
                } else {
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.tip_options)
                            .items(R.array.menu_values)
                            .itemsCallback((dialog, itemView, position, text) -> {
                                if (position == 0) {
                                    CategoryNames.del(child.getInt("id"));
                                    refresh();
                                } else {

                                    change(child.getInt("id"), child.getString("parent_id"), "2", child.getString("type"), child.getString("name"));

                                }

                            })
                            .show();
                }
            }
        });


        //refresh();

    }

    protected void refresh() {
        if (cateChoose != null) {
            cateChoose.refresh();

        }

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

