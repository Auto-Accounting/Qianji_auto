package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.dreamn.qianji_auto.database.Helper.Category;
import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.adapter.CategoryAdapter;
import cn.dreamn.qianji_auto.ui.fragment.base.sorts.sortsFragment1;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import es.dmoral.toasty.Toasty;

public class CategoryUtils {
    SwipeRecyclerView recyclerView;
    CategoryAdapter mAdapter;
    private List<Bundle> list;
    private List<Bundle> tempList;
    private String book_id=null;
    private String type;
    private Context mContext;

    private int topInt=-1;

    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    private static final int HANDLE_REFRESH=2;
    CategoryUtils(SwipeRecyclerView recyclerView,CategoryAdapter mAdapter,String book_id,String type,Context context){
        this.book_id=book_id;
        this.recyclerView=recyclerView;
        this.mAdapter=mAdapter;
        this.type=type;
        mContext=context;
    }
    class SpecialSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        @Override
        public int getSpanSize(int i) {
            Bundle bundle=tempList.get(i);
            return bundle.containsKey("change") ?5:1;
        }
    }

    public void show(){


        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 5);
        layoutManager.setSpanSizeLookup(new SpecialSpanSizeLookup());
        recyclerView.setLayoutManager(layoutManager);


        mAdapter=new CategoryAdapter(mContext,true);
        mAdapter.setHasStableIds(true);
        mAdapter.setOnItemClickListener(this::OnItemClickListen);
        mAdapter.setOpenAnimationEnable(false);
        recyclerView.setAdapter(mAdapter);

    }

    private void OnItemClickListen(View view, int position) {
        if(tempList==null||position >= tempList.size())return;
        Bundle item= tempList.get(position);
        int listPos=list.indexOf(item);//获取在list中的pos
        if(listPos==-1)return;//哥屋恩吧
        if(topInt==item.getInt("id")){
            Log.d("view一致,收起面板");
            mAdapter.setIndex(-1);
            mAdapter.setSelect(position);
            tempList=new ArrayList<>(list);
            mAdapter.refresh(tempList);
            topInt=-1;
            return;
        }

        topInt=item.getInt("id");






        int left = view.getLeft();


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

                        tempList=new ArrayList<>(list);//重新赋值
                        //  List<Bundle> bundles2=new ArrayList<>();
                        int size=list.size();
                        // Log.d("真实加入的位置"+real+" 实际大小"+size+" 点击位置："+position+" list中"+listPos);

                        if(real>size){
                            for(int i=0;i<real-size;i++){
                                Bundle bundle=new Bundle();
                                tempList.add(bundle);
                                //  bundles2.add(bundle);
                            }
                        }
                        mAdapter.setSelect(listPos);
                        mAdapter.setIndex(real);
                        Bundle[] bundles=(Bundle[])msg.obj;
                        Bundle bundle=new Bundle();
                        bundle.putInt("id",-1);
                        bundle.putString("book_id",item.getString("book_id"));
                        bundle.putInt("left", left+8);
                        bundle.putSerializable("data",bundles);
                        bundle.putBoolean("change",true);
                        tempList.add(bundle);
                        // tempList.clear();

                        ///  bundles2.add(bundle);

                        // mAdapter.loadMore(bundles2);

                        //  Log.d("当前tempList"+tempList.size()+" mAdapter "+mAdapter.getCount());

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
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    break;
                case HANDLE_OK:
                    mAdapter.refresh(list);
                    break;
                case HANDLE_REFRESH:
                    refreshData();
                    break;
            }
        }
    };
    private void refreshData(){
        CategoryNames.getParentByPay(book_id, books -> {
            if(books==null||books.length==0){
                mHandler.sendEmptyMessage(HANDLE_ERR);
            }else{
                list= Arrays.asList(books);
                tempList=new ArrayList<>(list);
                mHandler.sendEmptyMessage(HANDLE_OK);
            }
        });
    }
}
