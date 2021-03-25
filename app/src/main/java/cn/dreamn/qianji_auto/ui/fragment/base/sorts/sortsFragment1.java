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

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.input.DialogInputExtKt;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.Assets;
import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.adapter.CategoryAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
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
    private List<Bundle> list=new ArrayList<>();
    private List<Bundle> tempList;
    private String book_id;
    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    private static final int HANDLE_REFRESH=2;

    private int topInt=-1;


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

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        layoutManager.setSpanSizeLookup(new SpecialSpanSizeLookup());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter=new CategoryAdapter(getContext(),true);
        mAdapter.setHasStableIds(true);
        mAdapter.setOnItemClickListener(this::OnItemClickListen);
        mAdapter.setOpenAnimationEnable(false);
        recyclerView.setAdapter(mAdapter);
        refreshLayout.setEnableRefresh(false);
        loadFromData();
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



    private void updateItem(int position) {

        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
        //viewHolder.

    }


    @SuppressLint("CheckResult")
    private void OnItemClickListen(View view, int position) {
        if(list==null||position >= list.size())return;
        Bundle item= list.get(position);

        if(item.getString("name")==null)return;//为null就不响应

        int left = view.getLeft();
        /*
         * 1 2 3 4 5 * 插入(6)
         * 6 7 8 9 10 * 插入
         * 7 8 9 10 11 (12)
         * 11 12 13 14  * 插入
         * 13 14 15 16 17 (18)
         * */

        int line=position/6;
        line=line+1;
        int real=line*6;


         //清除上一个控件布局 Strat

        int line2=topInt/6;
        line2=line2+1;
        int real2=line2*6;
        if(real2!=real){//不同布局刷新
            Bundle bundle1=new Bundle();
            bundle1.putString("name",null);//保留数据
            bundle1.putBoolean("change",false);//保留数据
            list.set(real2-1,bundle1);
            mAdapter.replaceNotNotify(real2-1,bundle1);
            mAdapter.notifyItemChanged(real2-1);
        }


         // 清除上一个控件布局 End

        mAdapter.setSelect(position);//选中当前
        //清除上一个样式
        mAdapter.notifyItemChanged(topInt);
        //局部刷新
        Log.d("刷新上一个按钮"+topInt+"清除");
        if(topInt==position){
            Log.d("view一致");
            Bundle bundle1=new Bundle();
            bundle1.putString("name",null);//保留数据
            bundle1.putBoolean("change",false);//保留数据
            list.set(real2-1,bundle1);
            mAdapter.replaceNotNotify(real2-1,bundle1);
            mAdapter.notifyItemChanged(real2-1);
            Log.d("刷新二级页面"+(real-1)+"");
            mAdapter.notifyItemChanged(position);
            Log.d("刷新当前按钮"+(position)+"");
            topInt=position;
            return;
        }

        topInt=position;

        Log.d("当前位置"+position+" 实际上子页面的位置为"+real);


        Handler mmHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLE_ERR:
                        //没有数据
                        break;
                    case HANDLE_OK:

                        Log.d("当前选中"+position);
                        Bundle[] bundles=(Bundle[])msg.obj;
                        Bundle bundle=new Bundle();
                        bundle.putInt("id",-1);
                        bundle.putString("name",null);
                        bundle.putString("book_id",item.getString("book_id"));
                        bundle.putInt("left", left+8);
                        bundle.putSerializable("data",bundles);
                        bundle.putBoolean("change",true);
                        Log.d("当前修改"+bundle.toString());
                        list.set(real-1,bundle);
                        mAdapter.replaceNotNotify(real-1,bundle);
                        Log.d("刷新当前按钮"+(position)+"");
                        mAdapter.notifyItemChanged(real-1);
                        Log.d("刷新二级菜单"+(real-1)+"");
                        mAdapter.notifyItemChanged(position);

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

     /*   Handler mmHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLE_ERR:
                        //没有数据
                        break;
                    case HANDLE_OK:

                      //  tempList=new ArrayList<>(list);//重新赋值
                      //  List<Bundle> bundles2=new ArrayList<>();
                      //  int size=list.size();
                       // Log.d("真实加入的位置"+real+" 实际大小"+size+" 点击位置："+position+" list中"+listPos);


                        mAdapter.setSelect(position);
                       // mAdapter.setIndex(real);
                        Bundle[] bundles=(Bundle[])msg.obj;
                        Bundle bundle=new Bundle();
                        bundle.putInt("id",-1);
                        bundle.putString("book_id",item.getString("book_id"));
                        bundle.putInt("left", left+8);
                        bundle.putSerializable("data",bundles);
                        bundle.putBoolean("change",true);
                        list.set(real,bundle);
                       // tempList.clear();

                      ///  bundles2.add(bundle);

                       // mAdapter.loadMore(bundles2);

                      //  Log.d("当前tempList"+tempList.size()+" mAdapter "+mAdapter.getCount());
                     //   mAdapter.replace();
                       mAdapter.replaceNotNotify(real,bundle);

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
*/

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

                    loadFromData();
                    break;
            }
            multiple_actions_down.setVisibility(View.VISIBLE);
        }
    };

    public void loadFromData(){

        Task.onMain(1000,()->{
            CategoryNames.getParentByPay(book_id,books -> {
                if(books==null||books.length==0){
                    mHandler.sendEmptyMessage(HANDLE_ERR);
                }else{

                    list.clear();
                   int len = books.length;
                   int line=len/5;//共有几行
                   if(len%5!=0)line=line+1;
                   int realLen=line*5+line;//取5的整数
                   // Log.d("数据总长度："+len+" 数据总共几行"+line+" 应该的数据总量"+realLen);
                    /*
                    * 1 2 3 4 5
                    * 插入
                    * 6 7 8 9 10
                    * 插入
                    * 11 12 13 14
                    * 插入
                    * */
                    for(int i=0;i<len;i++){
                    //    Log.d("当前i"+((i)%5));
                        list.add(books[i]);
                        if((i)%5==4){
                            Bundle bundle=new Bundle();
                            bundle.putString("name",null);//保留数据
                            bundle.putBoolean("change",false);//保留数据
                            list.add(bundle);
                        }


                    }
                   // Log.d("还差"+(realLen-list.size())+"条数据");
                    int len2=realLen-list.size()-1;
                    //长度补全
                    for(int j=0;j<len2;j++){
                       // Log.d("循环次数+"+j);
                        Bundle bundle=new Bundle();
                        bundle.putString("name",null);//保留数据
                       // bundle.putBoolean("change",true);//保留数据
                        list.add(bundle);
                    }
                    Bundle bundle=new Bundle();
                    bundle.putString("name",null);//保留数据
                    bundle.putBoolean("change",false);//保留数据
                    list.add(bundle);

                  //  Log.d("bundle"+list.toString());
                  //  Log.d("数据总长度："+list.size()+" 数据总共几行"+line+" 应该的数据总量"+realLen);
                    tempList=new ArrayList<>(list);
                    mHandler.sendEmptyMessage(HANDLE_OK);
                }
            });
        });
    }






}
