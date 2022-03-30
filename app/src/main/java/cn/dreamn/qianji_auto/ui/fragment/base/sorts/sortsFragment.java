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

import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_ERR;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_REFRESH;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.hjq.toast.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Helper.BookNames;
import cn.dreamn.qianji_auto.data.database.Helper.Categorys;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LVCircularRing;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.CategoryUtils;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.Log;


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
    @BindView(R.id.action_import)
    FloatingActionButton action_switch;

    @BindView(R.id.view_hide)
    View view_hide;

    CategoryUtils categoryUtils;
    Bundle book;
    String type;


    private sortsFragment other;

    public sortsFragment(Bundle book, String type) {
        super();
        this.book = book;
        this.type = type;
    }

    Handler mHandler;

    public void setBook(Bundle bookData) {
        this.book = bookData;
        setSwitchData();
        refreshData(book.getString("book_id"), -2);
    }

    public void setObj(sortsFragment other) {
        this.other = other;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_category;
    }

    @Override
    protected void initViews() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case HANDLE_ERR:
                        if (statusView != null) statusView.showEmptyView();
                        break;
                    case HANDLE_OK:
                        //mAdapter.refresh(list);
                        if (statusView != null) statusView.showContentView();
                        break;
                    case HANDLE_REFRESH:
                        refreshData(book.getString("book_id"), msg.arg1);
                        if (statusView != null) statusView.showContentView();
                        break;
                }
                String d = (String) msg.obj;
                if ((d != null && !d.equals("")))
                    ToastUtils.show(d);
            }
        };
        setSwitchData();
        statusView.setEmptyView(R.layout.fragment_empty_view);
        statusView.setLoadingView(R.layout.fragment_loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> viewHolder.setText(R.id.empty_info, getString(R.string.sort_empty)));
        statusView.setOnLoadingViewConvertListener(viewHolder -> {
            LVCircularRing lv_circularring = viewHolder.getView(R.id.lv_circularring);
            lv_circularring.startAnim();
            viewHolder.setText(R.id.loading_text, getString(R.string.main_loading));
        });

        view_hide.setVisibility(View.GONE);

        initLayout();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        // 点击浮层按钮时，view_hide就会变为visible，这时点击它，就会将浮层收起
        view_hide.setOnClickListener(v -> multiple_actions_down.collapse());
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
            BookNames.showBookSelect(getContext(), getString(R.string.set_choose_book), false, obj -> {
                Bundle bundle = (Bundle) obj;
                this.setBook(bundle);
                other.setBook(bundle);
                view_hide.setVisibility(View.GONE);
            });
        });
        action_cate.setOnClickListener(v -> {
            view_hide.setVisibility(View.GONE);
            multiple_actions_down.collapse();

            BottomArea.input(getContext(), getString(R.string.sort_input), "", getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
                @Override
                public void input(String data) {
                    Categorys.insert(data, "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png", "1", type, null, null, book.getString("book_id"), null, obj -> {
                        Boolean isSucceed = (Boolean) obj;
                        if (isSucceed) {
                            HandlerUtil.send(mHandler, getString(R.string.add_success), -2, HANDLE_REFRESH);
                        } else {
                            HandlerUtil.send(mHandler, getString(R.string.sort_add_failed), -2, HANDLE_OK);
                        }
                    });
                }

                @Override
                public void cancel() {

                }
            });


        });

    }

    private void setSwitchData() {
        action_switch.setTitle(String.format(getString(R.string.sort_switch_book), book.getString("name")));
    }

    @SuppressLint("CheckResult")
    private void initLayout() {
        categoryUtils = new CategoryUtils(recyclerView, book.getString("book_id"), type, getContext(), true);
        categoryUtils.show();

        categoryUtils.setOnClick(new CategoryUtils.Click() {
            @Override
            public void onParentClick(Bundle bundle, int position) {

            }

            @Override
            public void onItemClick(Bundle bundle, Bundle parent, int position) {
                Log.i("当前点击数据：" + bundle.toString() + "\n父类数据：" + parent.toString());
                if (bundle.getInt("id") == -2) {
                    multiple_actions_down.collapse();
                    BottomArea.input(getContext(), getString(R.string.sort_input_child), "", getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
                        @Override
                        public void input(String data) {
                            Categorys.insert(data, "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png", "2", type, null, parent.getString("self_id"), parent.getString("book_id"), null, obj -> {
                                Boolean isSucceed = (Boolean) obj;
                                if (isSucceed) {
                                    HandlerUtil.send(mHandler, getString(R.string.add_success), position, HANDLE_REFRESH);
                                } else {
                                    HandlerUtil.send(mHandler, getString(R.string.sort_add_failed), position, HANDLE_OK);
                                }
                            });
                        }

                        @Override
                        public void cancel() {

                        }
                    });



                }else {

                    BottomArea.list(getContext(), String.format(getString(R.string.sort_edit), parent.getString("name"), bundle.getString("name")), Arrays.asList(getString(R.string.del), getString(R.string.change)), new BottomArea.ListCallback() {
                        @Override
                        public void onSelect(int index) {
                            switch (index) {
                                case 0:
                                    del(bundle, position);
                                    break;
                                case 1:
                                    change(bundle, parent.getString("name"), position);
                                    break;
                            }
                        }
                    });
                }
            }

            @Override
            public void onParentLongClick(Bundle bundle, int position) {

                BottomArea.list(getContext(), String.format(getString(R.string.assert_change), bundle.getString("name")), Arrays.asList(getString(R.string.del), getString(R.string.change)), new BottomArea.ListCallback() {
                    @Override
                    public void onSelect(int index) {
                        switch (index) {
                            case 0:
                                del(bundle, -2);
                                break;
                            case 1:
                                change(bundle, null, -2);
                                break;
                        }
                    }
                });
            }
        });

    }

    @SuppressLint("CheckResult")
    private void change(Bundle bundle, String parent,int parentPos) {
        BottomArea.input(getContext(), String.format(getString(R.string.sort_edit), parent, bundle.getString("name")), bundle.getString("name"), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
            @Override
            public void input(String data) {
                Categorys.update(bundle.getInt("id"), data, type, bundle.getString("book_id"), obj -> {
                    Boolean isSucceed = (Boolean) obj;
                    if (isSucceed) {
                        HandlerUtil.send(mHandler, getString(R.string.assert_change_success), parentPos, HANDLE_REFRESH);
                    } else {
                        HandlerUtil.send(mHandler, getString(R.string.sort_failed), parentPos, HANDLE_OK);
                    }
                });
            }

            @Override
            public void cancel() {

            }
        });

    }

    private void del(Bundle bundle,int position) {

        if(bundle.getString("parent_id")==null) {
            BottomArea.msg(getContext(), getString(R.string.sort_warn), getString(R.string.sort_warn_tip), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.MsgCallback() {
                @Override
                public void cancel() {

                }

                @Override
                public void sure() {
                    Categorys.del(bundle.getInt("id"));
                    HandlerUtil.send(mHandler, getString(R.string.del_success), position, HANDLE_REFRESH);
                }
            });
        }else{
            Categorys.del(bundle.getInt("id"));
            HandlerUtil.send(mHandler, getString(R.string.del_success), position, HANDLE_REFRESH);
        }
    }

    private void refreshData(String book_id,int parentPos) {
        if (statusView != null) statusView.showLoadingView();
        Log.i("book_id", book_id);
        Log.i("book_parent", String.valueOf(parentPos));
        categoryUtils.refreshData(book_id, parentPos, (state) -> {
            HandlerUtil.send(mHandler, state);
        });
    }

    private void refreshData() {
        if (statusView != null) statusView.showLoadingView();
        categoryUtils.refreshData((state) -> HandlerUtil.send(mHandler, state));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }

}
