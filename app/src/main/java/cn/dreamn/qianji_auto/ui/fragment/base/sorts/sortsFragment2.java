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
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.BookNames;
import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.utils.CategoryUtils;
import es.dmoral.toasty.Toasty;


/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "收入UI", anim = CoreAnim.slide)
public class sortsFragment2 extends BaseFragment {


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


    public sortsFragment2(Bundle book, String type) {
        super();
        this.book = book;
        this.type = type;
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
            BookNames.showBookSelect(getContext(), "请选择账本", bundle -> {
                book = bundle;
                setSwitchData();
                refreshData(book.getString("book_id"));

            });
        });
        action_cate.setOnClickListener(v -> {
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
                                message.obj = "添加成功!";
                            } else {
                                message.arg1 = 0;
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


    private void initLayout() {
        categoryUtils = new CategoryUtils(recyclerView, book.getString("book_id"), type, getContext(), true);
        categoryUtils.show();
        refreshData();
    }

    private void refreshData(String book_id) {
        categoryUtils.refreshData(book_id, () -> mHandler.sendEmptyMessage(HANDLE_OK));
    }

    private void refreshData() {
        categoryUtils.refreshData(() -> mHandler.sendEmptyMessage(HANDLE_OK));
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    statusView.showEmptyView();
                    break;
                case HANDLE_OK:
                    statusView.showContentView();
                    break;
                case HANDLE_REFRESH:
                    String d = (String) msg.obj;
                    if ((d != null && !d.equals(""))) {
                        if (msg.arg1 == 1)
                            Toasty.success(getContext(), d, Toast.LENGTH_LONG).show();
                        else
                            Toasty.warning(getContext(), d, Toast.LENGTH_LONG).show();
                    }
                    refreshData();
                    break;
            }
            multiple_actions_down.setVisibility(View.VISIBLE);
        }
    };


}
