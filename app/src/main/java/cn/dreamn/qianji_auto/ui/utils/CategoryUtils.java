package cn.dreamn.qianji_auto.ui.utils;

import android.content.ContentProvider;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Adapter;

import androidx.recyclerview.widget.GridLayoutManager;

import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.ui.adapter.CategoryAdapter;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;

public class CategoryUtils {
    SwipeRecyclerView recyclerView;//列表容器
    CategoryAdapter mAdapter;//数据处理工具
    private List<Bundle> list=new ArrayList<>();//数据列表
    private String book_id;//当前book
    private String type;//当前类别
    private Context mContext;//上下文
    private finishRefresh finish;//刷新结束，回调
    private int topInt=-1;//当前展开或点击的pos
    private boolean expand=false;//是否展开
    private Click click;//点击事件
    private boolean allowChange;//是否允许修改
    private static final int HANDLE_ERR = 0;
    private static final int HANDLE_OK = 1;


    //回调接口
    public interface finishRefresh{
        void onFinish(int state);
    }
    public interface Click{
        void onParentClick(Bundle bundle,int position);
        void onItemClick(Bundle bundle,Bundle parent_bundle,int position);
        void onParentLongClick(Bundle bundle,int position);
    }

    /**
     * Instances of static inner classes do not hold an implicit
     * reference to their outer class.
     */
    private static class MyHandler extends Handler {
        private final WeakReference<CategoryUtils> categoryUtilsWeakReference;

        public MyHandler(CategoryUtils categoryUtilsWeakReference1) {
            categoryUtilsWeakReference = new WeakReference<>(categoryUtilsWeakReference1);
        }

        @Override
        public void handleMessage(Message msg) {
            CategoryUtils categoryUtils = categoryUtilsWeakReference.get();
            if (categoryUtils != null) {
                if (msg.what == HANDLE_OK) {
                    List<Bundle> list=(List<Bundle>)msg.obj;
                    categoryUtils.getAdapter().refresh(list);//全部刷新
                }
                if(categoryUtils.getFinish()!=null){
                    categoryUtils.getFinish().onFinish(msg.what);
                }
            }
        }
    }

    private final MyHandler mHandler = new MyHandler(this);

    private finishRefresh getFinish() {
        return finish;
    }

    private CategoryAdapter getAdapter() {
        return mAdapter;
    }

    public CategoryUtils(SwipeRecyclerView recyclerView, String book_id, String type, Context context,Boolean allowChange){
        this.book_id=book_id;
        this.recyclerView=recyclerView;
        this.mAdapter=new CategoryAdapter(context,allowChange);
        this.allowChange=allowChange;
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



    public void show(){

        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 5);
        layoutManager.setSpanSizeLookup(new SpecialSpanSizeLookup());
        recyclerView.setLayoutManager(layoutManager);
        mAdapter=new CategoryAdapter(mContext,allowChange);

        mAdapter.setOnItemClickListener(this::OnItemClickListen);
        mAdapter.setOnItemLongClickListener(this::OnLongClickListen);
        mAdapter.setOpenAnimationEnable(false);
        recyclerView.setAdapter(mAdapter);


    }

    private void OnLongClickListen(View view, int position) {
        if(list==null||position >= list.size())return;
        Bundle item = list.get(position);
        if(item.getString("name")==null)return;//为null就不响应
        if(click!=null){
            click.onParentLongClick(item, position);
        }
    }


    private void OnItemClickListen(View view, int position) {

        if(list==null||position >= list.size())return;
        Bundle item= list.get(position);
        int left = view.getLeft();
        if(this.click!=null){
            click.onParentClick(item,position);
        }
        refreshSubData(item,position,left);
    }


    public void setOnClick(Click item){
        this.click=item;
        mAdapter.setOnItemListener((bundle, parent, parentPos) -> {
            if(click!=null){
                click.onItemClick(bundle,parent,parentPos);
            }
        });
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

    public void clean(){//全部清除
        list.clear();
        topInt=-1;
        if(mAdapter!=null){
            mAdapter.setSelect(-1);
            mAdapter.refresh(null);
        }
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
        clean();
        finish=f;
        CategoryNames.getParents(book_id,type,books -> {
            Log.d("books"+ Arrays.toString(books));
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
                Message message=new Message();
                message.what=HANDLE_OK;
                message.obj=list;
                mHandler.sendMessage(message);
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
                bundle.putBoolean("change", bundles.length != 0);


                list.set(real - 1, bundle);
                mAdapter.replaceNotNotify(real - 1, bundle);
                mAdapter.notifyItemChanged(real - 1);

                mAdapter.notifyItemChanged(position);
            }
        };

        CategoryNames.getChildrens(item.getString("self_id"),book_id,item.getString("type"),allowChange,books -> {
            Log.d("子类"+ Arrays.toString(books));
            Message message=new Message();
            message.obj=books;
            message.what=HANDLE_OK;
            mmHandler.sendMessage(message);
        });
    }

    private void closeItem(int position){
      //  Bundle item=list.get(position);
        expand=false;
        int real=getItemPos(position);
        Bundle bundle1=new Bundle();
        bundle1.putString("name",null);//保留数据
        bundle1.putBoolean("change",false);//保留数据
       try{
           list.set(real-1,bundle1);
           mAdapter.replaceNotNotify(real-1,bundle1);
           mAdapter.notifyItemChanged(real-1);
       }catch (Exception e){
           Log.d("发生越界");
       }



    }
}
