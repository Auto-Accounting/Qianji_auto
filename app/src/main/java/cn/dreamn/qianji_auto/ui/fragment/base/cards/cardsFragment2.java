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

package cn.dreamn.qianji_auto.ui.fragment.base.cards;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.input.DialogInputExtKt;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.statusview.StatusView;
import com.shehuan.statusview.StatusViewConvertListener;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.BookNames;
import cn.dreamn.qianji_auto.ui.adapter.BookListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import es.dmoral.toasty.Toasty;


/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "账本", anim = CoreAnim.slide)
public class cardsFragment2 extends BaseFragment {

    @BindView(R.id.status)
    StatusView statusView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recyclerView;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;
    private BookListAdapter mAdapter;
    private List<Bundle> list;

    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    private static final int HANDLE_REFRESH=2;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_base_cards_page1;
    }


    @Override
    protected void initViews() {
        statusView.setEmptyView(R.layout.empty_view);
        statusView.setLoadingView(R.layout.loading_view);

        statusView.setOnEmptyViewConvertListener((StatusViewConvertListener) viewHolder -> {
            viewHolder.setText(R.id.empty_info,"你还没有任何账本哦！\n钱迹免费用户不需要添加账本。");
        });
        statusView.setOnLoadingViewConvertListener((StatusViewConvertListener) viewHolder -> {
            viewHolder.setText(R.id.load_info,"正在加载账本信息");
        });
        floatingActionButton.setVisibility(View.GONE);
        statusView.showLoadingView();
        initLayout();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void initListeners() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        });
        floatingActionButton.setOnClickListener(v->{
            MaterialDialog dialog =  new MaterialDialog(getContext(),MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(null,"请输入账本名称");
            DialogInputExtKt.input(dialog, "指的是记账app中的账本名称", null, null, null,
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS ,
                    null, true, false, (materialDialog, text) -> {
                        BookNames.add(text.toString(), () -> {
                            Message message=new Message();
                            message.obj="添加成功!";
                            message.what=HANDLE_REFRESH;
                            mHandler.sendMessage(message);
                        });

                        return null;
                    });



            dialog.show();
        });

    }


    private void initLayout(){
        mAdapter=new BookListAdapter(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this::OnItemClickListen);
        refreshLayout.setOnRefreshListener(this::loadFromData);
        refreshLayout.setEnableRefresh(true);
        loadFromData(refreshLayout);
    }
    @SuppressLint("CheckResult")
    private void OnItemClickListen(View view, int position) {
        if(list==null||position >= list.size())return;

        Bundle bookName=(Bundle)list.get(position);

        MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(null, "请选择操作("+bookName.getString("name")+")");
        DialogListExtKt.listItems(dialog, null, Arrays.asList("删除", "修改"), null, true, (materialDialog, index, text) -> {
            switch (index){
                case 0:del(bookName);break;
                case 1:change(bookName);break;
            }
            return null;
        });
        dialog.show();

    }
    @SuppressLint("CheckResult")
    private void change(Bundle bookName) {
        MaterialDialog dialog =  new MaterialDialog(getContext(),MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(null,"请修改账本名称");
        DialogInputExtKt.input(dialog, "指的是记账app中的账本名称", null, bookName.getString("name"), null,
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS ,
                null, true, false, (materialDialog, text) -> {
                    BookNames.upd(bookName.getInt("id"),text.toString() , () -> {
                        Message message=new Message();
                        message.obj="修改成功!";
                        message.what=HANDLE_REFRESH;
                        mHandler.sendMessage(message);
                    });

                    return null;
                });



        dialog.show();
    }

    private void del(Bundle bookName) {
        MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(null, "删除确认");
        dialog.message(null, "确定要删除（"+bookName.getString("name")+"）吗？", null);
        dialog.positiveButton(null, "确定", materialDialog -> {
            BookNames.del(bookName.getInt("id"),()->{
                Message message=new Message();
                message.obj="删除成功!";
                message.what=HANDLE_REFRESH;
                mHandler.sendMessage(message);
            });
            return null;
        });
        dialog.negativeButton(null, "取消", materialDialog -> null);
        dialog.show();
    }


    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    statusView.showEmptyView();
                    break;
                case HANDLE_OK:
                    mAdapter.refresh(list);
                    Task.onMain(1000,()->statusView.showContentView());
                    break;
                case HANDLE_REFRESH:
                    String d=(String)msg.obj;
                    if((d!=null&& !d.equals("")))
                        Toasty.success(getContext(), d, Toast.LENGTH_LONG).show();
                    loadFromData(refreshLayout);
                    break;
            }
            floatingActionButton.setVisibility(View.VISIBLE);
        }
    };

    public void loadFromData(RefreshLayout refreshLayout){

        Task.onMain(1000,()->{
            BookNames.getAllIcon(false,books -> {
                if(books==null||books.length==0){
                    mHandler.sendEmptyMessage(HANDLE_ERR);
                }else{
                    list= Arrays.asList(books);
                    // assests=asset2s;

                    mHandler.sendEmptyMessage(HANDLE_OK);
                }
            });
        });
    }







}
