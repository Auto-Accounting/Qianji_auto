/*
 * Copyright (C) 2019 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.fragment;


import android.content.Intent;
import android.net.Uri;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.xuexiang.xutil.app.PathUtils;

import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.utils.file.Storage;
import cn.dreamn.qianji_auto.utils.file.myFile;

import static android.app.Activity.RESULT_OK;


/**
 * 这个只是一个空壳Fragment，只是用于演示而已
 *
 * @author xuexiang
 * @since 2019-07-08 00:52
 */
@Page(name = "备份&恢复")
public class BackupFragment extends BaseFragment {
    @BindView(R.id.bak_all)
    SuperTextView bak_all;
    @BindView(R.id.bak_plugin)
    SuperTextView bak_plugin;
    @BindView(R.id.bak_map)
    SuperTextView bak_map;
    @BindView(R.id.bak_sort)
    SuperTextView bak_sort;
    @BindView(R.id.bak_learn)
    SuperTextView bak_learn;
    private static final int FILE_SELECT_CODE = 0;

    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_bak;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

        initListen();
    }


    private void initListen(){

        bak_all.setOnSuperTextViewClickListener(superTextView -> {
            showMenu(R.id.bak_all);
        });
        bak_plugin.setOnSuperTextViewClickListener(superTextView -> {
            showMenu(R.id.bak_plugin);
        });
        bak_map.setOnSuperTextViewClickListener(superTextView -> {
            showMenu(R.id.bak_map);
        });
        bak_sort.setOnSuperTextViewClickListener(superTextView -> {
            showMenu(R.id.bak_sort);
        });
        bak_learn.setOnSuperTextViewClickListener(superTextView -> {
            showMenu(R.id.bak_learn);
        });


    }
    //展示选择
    private void showMenu(int type){
        new MaterialDialog.Builder(Objects.requireNonNull(getContext()))
                .title(R.string.tip_options)
                .items(R.array.menu_values_backup)
                .itemsCallback((dialog, itemView, position, text) ->{
                    switch (position){
                        case 0:
                            String name="";
                            String[] files;
                            switch (type){
                                case R.id.bak_all:name="all";files= new String[]{Storage.Set,Storage.Map,Storage.Learn,Storage.AliMap,Storage.WeMap}; break;
                                case R.id.bak_plugin:name="plugin";files= new String[]{Storage.Set};break;
                                case R.id.bak_map:name="map";files= new String[]{Storage.Map};break;
                                case R.id.bak_sort:name="sort";files= new String[]{Storage.AliMap,Storage.WeMap};break;
                                case R.id.bak_learn:name="learn";files= new String[]{Storage.Learn};break;
                                default:
                                    throw new IllegalStateException("Unexpected value: " + type);
                            }
                            name=myFile.zip(files,name);
                            SnackbarUtils.Long(getView(), String.format(getString(R.string.bak_success),name)).info().show();
                            break;
                        case 1:
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("*/*");
                            intent.addCategory(Intent.CATEGORY_OPENABLE);

                            try {
                                startActivityForResult( Intent.createChooser(intent, getString(R.string.bak_select)), FILE_SELECT_CODE);
                            } catch (android.content.ActivityNotFoundException ignored) {

                            }

                            break;
                    }


                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)  {
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                assert uri != null;
                String path = PathUtils.getFilePathByUri(getContext(), uri);

                myFile.unzip(path);
                SnackbarUtils.Long(getView(), getString(R.string.bak_success_2)).info().show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
