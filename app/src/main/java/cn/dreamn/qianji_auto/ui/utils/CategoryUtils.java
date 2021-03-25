package cn.dreamn.qianji_auto.ui.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Adapter;

import androidx.recyclerview.widget.GridLayoutManager;

import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
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
    private boolean expand=false;
    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLE_OK) {
                mAdapter.refresh(list);//全部刷新
            }
            if(ff!=null){
                ff.onFinish(msg.what);
            }
        }
    };
    private LongClick longClick;

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
        void onFinish(int state);
    }

    public void show(){

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 5);
        layoutManager.setSpanSizeLookup(new SpecialSpanSizeLookup());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter=new CategoryAdapter(mContext,true);

        mAdapter.setOnItemClickListener(this::OnItemClickListen);
        mAdapter.setOnItemLongClickListener(this::OnLongClickListen);
        mAdapter.setOpenAnimationEnable(false);
        recyclerView.setAdapter(mAdapter);


    }

    private void OnLongClickListen(View view, int position) {
        if(list==null||position >= list.size())return;
        Bundle item= list.get(position);
        if(item.getString("name")==null)return;//为null就不响应
        if(longClick!=null){
            longClick.onLongClick(item, position);
        }
    }


    private void OnItemClickListen(View view, int position) {
        if(list==null||position >= list.size())return;
        Bundle item= list.get(position);
        int left = view.getLeft();

        refreshSubData(item,position,left);
    }

    public void setOnItemClick(CategoryAdapter.Item item){
        mAdapter.setOnItemListener(item);
    }
    public interface LongClick{
        void onLongClick(Bundle bundle,int pos);
    }
    public void setOnLongClick(LongClick longClick){
        this.longClick=longClick;
    }



    private void refreshSubData(Bundle item,int position,int left){

        if(item.getString("name")==null)return;//为null就不响应

        int real=getItemPos(position);//当前的item
        int real2=getItemPos(topInt);//上一个item

        Log.d("real "+real+" real2 "+real2);

        if(real2!=real){//item不同布局则清除上一个
            closeItem(topInt);
        }

        // 当前布局设置选中
        mAdapter.setSelect(position);
        // 清除上一个布局
        mAdapter.notifyItemChanged(topInt);
        //再次点击，如果已展开则收起
        if(topInt==position&&isOpenItem()){
            closeItem(topInt);

            topInt=position;
            return;
        }

        topInt=position;
        //点击默认展开
        openItem(position,left);

    }

    public void refreshData(String book_id,int parent,finishRefresh f){
        if(parent!=-2){
            Bundle data=list.get(parent);
            Bundle dataItem=data.getBundle("item");
            int index=list.indexOf(dataItem);
            closeItem(index);//清除
            topInt=-1;
           refreshSubData(dataItem,index,data.getInt("leftRaw"));
        }else refreshData(book_id,f);

    }
    public void refreshData(String book_id,finishRefresh f){
        this.book_id=book_id;
        refreshData(f);
    }
    public void refreshData(finishRefresh f){
        ff=f;
        CategoryNames.getParents(book_id,type,books -> {
            Log.d("books"+ Arrays.toString(books));
            mAdapter.setSelect(-1);
            expand=false;//默认关闭
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
                Log.d("输出"+list.toString());
                mHandler.sendEmptyMessage(HANDLE_OK);
            }

        });
    }


    private boolean isOpenItem(){
        return expand;
    }

    private int getItemPos(int position){
        int line=position/6;
        line=line+1;
        return line*6;
    }

    private void openItem(int position,int left){

        Bundle item=list.get(position);
        Log.d("postion:"+position + " data"+item.toString());
        if(item==null)return;
       int real=getItemPos(position);

        Handler mmHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == HANDLE_OK) {
                    expand = true;
                    Bundle[] bundles = (Bundle[]) msg.obj;
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", -1);
                    bundle.putBundle("item", item);
                    bundle.putString("name", null);
                    bundle.putString("book_id", item.getString("book_id"));
                    bundle.putInt("left", left + 13);
                    bundle.putInt("leftRaw", left);
                    bundle.putSerializable("data", bundles);
                    bundle.putBoolean("change", true);

                    list.set(real - 1, bundle);
                    mAdapter.replaceNotNotify(real - 1, bundle);

                    mAdapter.notifyItemChanged(real - 1);

                    mAdapter.notifyItemChanged(position);
                }
            }
        };

        CategoryNames.getChildrens(item.getString("self_id"),book_id,item.getString("type"),true,books -> {
            Log.d("子类"+ Arrays.toString(books));
            if(books!=null&&books.length!=0){
                Message message=new Message();
                message.obj=books;
                message.what=HANDLE_OK;
                mmHandler.sendMessage(message);
            }
        });
    }

    private void closeItem(int position){

        int real=getItemPos(position);
        Bundle bundle1=new Bundle();
        bundle1.putString("name",null);//保留数据
        bundle1.putBoolean("change",false);//保留数据
        list.set(real-1,bundle1);
        mAdapter.replaceNotNotify(real-1,bundle1);
        mAdapter.notifyItemChanged(real-1);

        expand=false;
    }
}
