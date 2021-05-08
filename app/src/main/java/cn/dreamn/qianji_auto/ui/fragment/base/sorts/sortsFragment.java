/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.ui.fragment.base.sorts;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.input.DialogInputExtKt;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.BookNames;
import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.utils.CategoryUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import es.dmoral.toasty.Toasty;


@Page(name = "收入UI", anim = CoreAnim.slide)
public class sortsFragment extends BaseFragment {


    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;

    @BindView(R.id.multiple_actions_down)
    FloatingActionsMenu multiple_actions_down;
    @BindView(R.id.action_cate)
    FloatingActionButton action_cate;
    @BindView(R.id.action_switch)
    FloatingActionButton action_switch;

    @BindView(R.id.view_hide)
    View view_hide;

    CategoryUtils categoryUtils;

    Bundle book;
    String type;
    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    private static final int HANDLE_REFRESH = 2;

    private sortsFragment other;

    public sortsFragment(Bundle book, String type) {
        super();
        this.book = book;
        this.type = type;
    }

    public void setObj(sortsFragment other){
        this.other=other;
    }

    public void setBook(Bundle bookData){
        this.book = bookData;
        setSwitchData();
        refreshData(book.getString("book_id"),-2);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_category1;
    }


    @Override
    protected void initViews() {
        setSwitchData();
        statusView.setEmptyView(R.layout.empty_view);
        statusView.setLoadingView(R.layout.loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> viewHolder.setText(R.id.empty_info, "你的分类呢？这样记不了账的哦！\n赶紧添加一个吧！"));
        statusView.setOnLoadingViewConvertListener(viewHolder -> viewHolder.setText(R.id.load_info, "正在加载分类信息"));
        multiple_actions_down.setVisibility(View.GONE);
        view_hide.setVisibility(View.GONE);
        statusView.showLoadingView();
        initLayout();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        multiple_actions_down.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                view_hide.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                view_hide.setVisibility(View.GONE);
            }
        });
        refreshLayout.setOnRefreshListener(RefreshLayout::finishRefresh);

        action_switch.setOnClickListener(v -> {
            BookNames.showBookSelect(getContext(), "请选择账本",false, bundle -> {
                this.setBook(bundle);
                other.setBook(bundle);
                view_hide.setVisibility(View.GONE);
            });
        });
        action_cate.setOnClickListener(v -> {
            view_hide.setVisibility(View.GONE);
            multiple_actions_down.collapse();
            MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(null, "请输入分类名称");
            DialogInputExtKt.input(dialog, "指的是记账app中的分类名称", null, null, null,
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                    null, true, false, (materialDialog, text) -> {
                        CategoryNames.insert(text.toString(), "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png", "1", type, null, null, book.getString("book_id"), null, isSucceed -> {
                            Message message = new Message();
                            message.what = HANDLE_REFRESH;

                            if (isSucceed) {
                                message.arg1 = 1;
                                message.arg2 = -2;
                                message.obj = "添加成功!";
                            } else {
                                message.arg1 = 0;
                                message.arg2 = -2;
                                message.obj = "添加失败！可能该分类已存在！";
                            }
                            mHandler.sendMessage(message);
                        });
                        return null;
                    });


            dialog.show();

        });

    }

    private void setSwitchData() {
        action_switch.setTitle("切换账本（" + book.getString("name") + "）");
    }


    @SuppressLint("CheckResult")
    private void initLayout() {
        categoryUtils = new CategoryUtils(recyclerView, book.getString("book_id"), type, getContext(), true);
        categoryUtils.show();
        refreshData();
        categoryUtils.setOnClick(new CategoryUtils.Click() {
            @Override
            public void onParentClick(Bundle bundle, int position) {

            }

            @Override
            public void onItemClick(Bundle bundle, Bundle parent, int position) {
                Log.d("当前点击数据："+bundle.toString()+"\n父类数据："+parent.toString());
                if(bundle.getInt("id")==-2){
                    multiple_actions_down.collapse();
                    MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
                    dialog.title(null, "请输入子类名称");
                    DialogInputExtKt.input(dialog, "指的是当前分类下的子类名称", null, null, null,
                            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                            null, true, false, (materialDialog, text) -> {
                                CategoryNames.insert(text.toString(), "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png", "2", type, null, parent.getString("self_id"), parent.getString("book_id"), null, isSucceed -> {
                                    Message message = new Message();
                                    message.what = HANDLE_REFRESH;

                                    if (isSucceed) {
                                        message.arg1 = 1;
                                        message.arg2 = position;
                                        message.obj = "添加成功!";
                                    } else {
                                        message.arg1 = 0;
                                        message.arg2 = position;
                                        message.obj = "添加失败！可能该分类已存在！";
                                    }
                                    mHandler.sendMessage(message);
                                });
                                return null;
                            });


                    dialog.show();
                }else{
                    //其他选择~
                    MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
                    dialog.title(null, "请选择操作("+parent.getString("name")+"-"+bundle.getString("name")+")");
                    DialogListExtKt.listItems(dialog, null, Arrays.asList("删除", "修改"), null, true, (materialDialog, index, text) -> {
                        switch (index){
                            case 0:del(bundle,position);break;
                            case 1:change(bundle,parent.getString("name"),position);break;
                        }
                        return null;
                    });
                    dialog.show();
                }
            }

            @Override
            public void onParentLongClick(Bundle bundle, int position) {
                MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
                dialog.title(null, "请选择操作("+bundle.getString("name")+")");
                DialogListExtKt.listItems(dialog, null, Arrays.asList("删除", "修改"), null, true, (materialDialog, index, text) -> {
                    switch (index){
                        case 0:del(bundle,-2);break;
                        case 1:change(bundle,null,-2);break;
                    }
                    return null;
                });
                dialog.show();
            }
        });

    }

    @SuppressLint("CheckResult")
    private void change(Bundle bundle, String parent,int parentPos) {
        MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(null, "请编辑("+parent+"-"+bundle.getString("name")+")");
        DialogInputExtKt.input(dialog, "请修改分类名称", null, bundle.getString("name"), null,
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS,
                null, true, false, (materialDialog, text) -> {
                    CategoryNames.update(bundle.getInt("id"),text.toString(), type, bundle.getString("book_id"), isSucceed -> {
                        Message message = new Message();
                        message.what = HANDLE_REFRESH;

                        if (isSucceed) {
                            message.arg1 = 1;
                            message.arg2 = parentPos;
                            message.obj = "修改成功!";
                        } else {
                            message.arg1 = 0;
                            message.arg2 = parentPos;
                            message.obj = "修改失败！可能该分类已存在！";
                        }
                        mHandler.sendMessage(message);
                    });
                    return null;
                });

        dialog.show();
    }

    private void del(Bundle bundle,int position) {
        Message message = new Message();
        message.what = HANDLE_REFRESH;

        message.arg1 = 1;
        message.arg2 = position;
        message.obj = "删除成功!";
        if(bundle.getString("parent_id")==null){
            MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(null, "⚠️警告");
            dialog.message(null,"删除该分类后，其二级分类也将被删除。",null);
            dialog.positiveButton(null, "确定删除", materialDialog -> {
                CategoryNames.del(bundle.getInt("id"));
                mHandler.sendMessage(message);
                return null;
            });
            dialog.negativeButton(null, "取消删除", materialDialog -> {
                return null;
            });

            dialog.show();
        }else{
            CategoryNames.del(bundle.getInt("id"));
            mHandler.sendMessage(message);
        }
    }

    private void refreshData(String book_id,int parentPos) {
        categoryUtils.refreshData(book_id,parentPos, (state) -> mHandler.sendEmptyMessage(state));
    }

    private void refreshData() {
        categoryUtils.refreshData((state) -> mHandler.sendEmptyMessage(state));

    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    statusView.showEmptyView();
                    break;
                case HANDLE_OK:
                    Task.onMain(1000,()->statusView.showContentView());
                    break;
                case HANDLE_REFRESH:
                    String d = (String) msg.obj;
                    if ((d != null && !d.equals(""))) {
                        if (msg.arg1 == 1)
                            Toasty.success(getContext(), d, Toast.LENGTH_LONG).show();
                        else
                            Toasty.warning(getContext(), d, Toast.LENGTH_LONG).show();
                    }
                    refreshData(book.getString("book_id"),msg.arg2);
                    break;
            }
            multiple_actions_down.setVisibility(View.VISIBLE);
        }
    };


}
