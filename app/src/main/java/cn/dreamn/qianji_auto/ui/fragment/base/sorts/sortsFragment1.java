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
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.input.DialogInputExtKt;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.niv.NiceImageView;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.Assets;
import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.adapter.CategoryAdapter;
import cn.dreamn.qianji_auto.ui.adapter.CategoryItemAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.utils.ScreenUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import es.dmoral.toasty.Toasty;


/**
 * @author xuexiang
 * @since 2018/11/7 下午1:16
 */
@Page(name = "收入UI", anim = CoreAnim.slide)
public class sortsFragment1 extends BaseFragment {



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




    private CategoryAdapter mAdapter;
    private List<Bundle> list;
    private List<Bundle> tempList;
    private String book_id;
    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    private static final int HANDLE_REFRESH=2;
    private List<View> lastView=new ArrayList<>();
    private View topView=null;

    private int point=-1;//上一次点击位置
    public sortsFragment1(String book_id) {
        super();
        this.book_id=book_id;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_category1;
    }


    @Override
    protected void initViews() {
        statusView.setEmptyView(R.layout.empty_view);
        statusView.setLoadingView(R.layout.loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> viewHolder.setText(R.id.empty_info,"你的分类呢？这样记不了账的哦！\n赶紧添加一个吧！"));
        statusView.setOnLoadingViewConvertListener(viewHolder -> viewHolder.setText(R.id.load_info,"正在加载分类信息"));
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
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
        });


        action_cate.setOnClickListener(v->{
            multiple_actions_down.collapse();
            MaterialDialog dialog =  new MaterialDialog(getContext(),MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(null,"请输入分类名称");
            DialogInputExtKt.input(dialog, "指的是记账app中的分类名称", null, null, null,
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS ,
                    null, true, false, (materialDialog, text) -> {
                CategoryNames.insert(text.toString(), "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png", "1", "0", null, null, book_id, null, new CategoryNames.getCateNameBoolean() {
                    @Override
                    public void onGet(boolean isSucceed) {
                        Message message=new Message();
                        message.what=HANDLE_REFRESH;

                        if(isSucceed){
                            message.arg1=1;
                            message.obj="添加成功!";
                        }else{
                            message.arg1=0;
                            message.obj="添加失败！可能该分类已存在！";
                        }
                        mHandler.sendMessage(message);
                    }
                });


                        return null;
                    });



            dialog.show();
        });

    }
    class SpecialSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        @Override
        public int getSpanSize(int i) {
            Bundle bundle=tempList.get(i);
            return bundle.containsKey("change") ?5:1;
        }
    }

    private void initLayout(){
        mAdapter=new CategoryAdapter(getContext(),true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        layoutManager.setSpanSizeLookup(new SpecialSpanSizeLookup());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

      //  recyclerView.setLongPressDragEnabled(false);

        mAdapter.setOnItemClickListener(this::OnItemClickListen);
        //refreshLayout.setOnRefreshListener(this::loadFromData);
        refreshLayout.setEnableRefresh(false);
        loadFromData(refreshLayout);
    }
    public void onItemLongClick(Bundle item) {
        Log.d("item-long",item.getString("name"));
        MaterialDialog dialog =  new MaterialDialog(getContext(),MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(null,"请输入二级分类名称");
        DialogInputExtKt.input(dialog, "指的是记账app中的二级分类名称", null, null, null,
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS ,
                null, true, false, (materialDialog, text) -> {
                    CategoryNames.insert(text.toString(), "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png", "2", "0", null, item.getString("self_id"), book_id, null, new CategoryNames.getCateNameBoolean() {
                        @SuppressLint("CheckResult")
                        @Override
                        public void onGet(boolean isSucceed) {
                            Message message=new Message();
                            message.what=HANDLE_REFRESH;

                            if(isSucceed){
                                message.arg1=1;
                                message.obj="添加成功!";
                            }else{
                                message.arg1=0;
                                message.obj="添加失败！可能该分类已存在！";
                            }
                            mHandler.sendMessage(message);
                        }
                    });


                    return null;
                });
    }




    @SuppressLint("CheckResult")
    private void OnItemClickListen(View view, int position) {
        if(tempList==null||position >= tempList.size())return;
        Bundle item= tempList.get(position);

        if(topView==view){
            mAdapter.setIndex(-1);
            mAdapter.setSelect(-1);
            mAdapter.refresh(list);
            topView=null;
            return;
        }else{
            topView=view;

        }






        int left = view.getLeft();
        int width = view.getWidth();
        int width2=25;
        int three=left;

        int line=position/5 + 1;
        int index2=position%5;
        int real=line*5;


        Handler mmHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLE_ERR:
                        //没有数据
                        break;
                    case HANDLE_OK:
                        int listPos=list.indexOf(item);//获取在list中的pos
                        tempList=new ArrayList<>(list);//重新赋值
                        int size=list.size();
                        Log.d("真实加入的位置"+real+" 实际大小"+size+" 点击位置："+position+" list中"+listPos);

                        if(real>size){
                            for(int i=0;i<real-size;i++){
                                Bundle bundle=new Bundle();
                                tempList.add(bundle);
                            }
                        }
                        mAdapter.setSelect(listPos);
                        mAdapter.setIndex(real);
                        Bundle[] bundles=(Bundle[])msg.obj;
                        Bundle bundle=new Bundle();
                        bundle.putString("book_id",item.getString("book_id"));
                        bundle.putInt("left",three);
                        bundle.putSerializable("data",bundles);
                        bundle.putBoolean("change",true);
                        tempList.add(real,bundle);
                        mAdapter.refresh(tempList);

                        break;
                }
            }
        };

        CategoryNames.getChildrens(item.getString("self_id"),book_id,item.getString("type"),true,books -> {
          //  Message m=new Message();
          //  m.obj=view;
            if(books==null||books.length==0){

            //    m.what=HANDLE_ERR;
                mmHandler.sendEmptyMessage(HANDLE_ERR);
            }else{
                Message message=new Message();
                message.obj=books;
                message.what=HANDLE_OK;
                mmHandler.sendMessage(message);
            }
        });


    }
    @SuppressLint("CheckResult")
    private void change(Bundle assest) {
        MaterialDialog dialog =  new MaterialDialog(getContext(),MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(null,"请修改资产名称");
        DialogInputExtKt.input(dialog, "指的是记账app中的资产名称", null, assest.getString("name"), null,
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS ,
                null, true, false, (materialDialog, text) -> {
                    Assets.updAsset(assest.getInt("id"),text.toString() , () -> {
                        Message message=new Message();
                        message.obj="修改成功!";
                        message.what=HANDLE_REFRESH;
                        mHandler.sendMessage(message);
                    });

                    return null;
                });



        dialog.show();
    }

    private void del(Bundle assest) {
        MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
        dialog.title(null, "删除确认");
        dialog.message(null, "确定要删除（"+assest.getString("name")+"）吗？", null);
        dialog.positiveButton(null, "确定", materialDialog -> {
            Assets.delAsset(assest.getInt("id"),()->{
                Message message=new Message();
                message.obj="删除成功!";
                message.what=HANDLE_REFRESH;
                mHandler.sendMessage(message);
            });
            return null;
        });
        dialog.negativeButton(null, "取消", materialDialog -> {

            return null;
        });
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
                    statusView.showContentView();
                    break;
                case HANDLE_REFRESH:
                    String d=(String)msg.obj;
                    if((d!=null&& !d.equals(""))){
                        if(msg.arg1==1)
                            Toasty.success(getContext(), d, Toast.LENGTH_LONG).show();
                        else
                            Toasty.warning(getContext(), d, Toast.LENGTH_LONG).show();
                    }

                    loadFromData(refreshLayout);
                    break;
            }
            multiple_actions_down.setVisibility(View.VISIBLE);
        }
    };

    public void loadFromData(RefreshLayout refreshLayout){

        Task.onMain(1000,()->{
            CategoryNames.getParentByPay(book_id,books -> {
                if(books==null||books.length==0){
                    mHandler.sendEmptyMessage(HANDLE_ERR);
                }else{

                    list=Arrays.asList(books);
                    // assests=asset2s;
                    tempList=new ArrayList<>(list);
                    mHandler.sendEmptyMessage(HANDLE_OK);
                }
            });
        });
    }






}
