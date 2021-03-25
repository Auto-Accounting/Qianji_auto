package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.adapter.CategoryAdapter;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class CategoryUtils {
    SwipeRecyclerView recyclerView;
    CategoryAdapter mAdapter;
    private List<Bundle> list=new ArrayList<>();
    private String book_id=null;
    private String type;
    private Context mContext;
    private finishRefresh ff;
    private int topInt=-1;

    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    break;
                case HANDLE_OK:
                    mAdapter.refresh(list);//全部刷新
                    if(ff!=null){
                        ff.onFinish();
                    }
                    break;
            }
        }
    };
    public CategoryUtils(SwipeRecyclerView recyclerView, String book_id, String type, Context context,Boolean allowChange){
        this.book_id=book_id;
        this.recyclerView=recyclerView;
        this.mAdapter=new CategoryAdapter(context,allowChange);
        this.type=type;
        mContext=context;
    }
    class SpecialSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        @Override
        public int getSpanSize(int i) {
            Bundle bundle=list.get(i);
            return bundle.containsKey("change") ?5:1;
        }
    }

    public interface finishRefresh{
        void onFinish();
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
           refreshSub(real2);
        }


        // 清除上一个控件布局 End

        mAdapter.setSelect(position);//选中当前
        //清除上一个样式
        mAdapter.notifyItemChanged(topInt);
        //局部刷新
       // Log.d("刷新上一个按钮"+topInt+"清除");
        if(topInt==position){
            Log.d("view一致");
            refreshSub(real2);
            Log.d("刷新二级页面"+(real-1)+"");
           // mAdapter.notifyItemChanged(position);
            Log.d("刷新当前按钮"+(position)+"");
            topInt=position;
            return;
        }

        topInt=position;

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
                        bundle.putInt("left", left+11);
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
            if(books==null||books.length==0){
                mmHandler.sendEmptyMessage(HANDLE_ERR);
            }else{
                Message message=new Message();
                message.obj=books;
                message.what=HANDLE_OK;
                mmHandler.sendMessage(message);
            }
        });
    }

    private void refreshSub(int position){
        Bundle bundle1=new Bundle();
        bundle1.putString("name",null);//保留数据
        bundle1.putBoolean("change",false);//保留数据
        list.set(position-1,bundle1);
        mAdapter.replaceNotNotify(position-1,bundle1);
        mAdapter.notifyItemChanged(position-1);
    }
    public void refreshData(String book_id,finishRefresh f){
        this.book_id=book_id;
        refreshData(f);
    }
    public void refreshData(finishRefresh f){
        ff=f;
        CategoryNames.getParents(book_id,type,books -> {
            if(books==null||books.length==0){
                mHandler.sendEmptyMessage(HANDLE_ERR);
            }else{

                list.clear();
                int len = books.length;
                int line=len/5;//共有几行
                if(len%5!=0)line=line+1;
                int realLen=line*5+line;//取5的整数
                for(int i=0;i<len;i++){
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
                mHandler.sendEmptyMessage(HANDLE_OK);
            }
        });
    }
}
