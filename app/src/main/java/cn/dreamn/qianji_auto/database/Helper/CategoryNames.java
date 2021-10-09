/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
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

package cn.dreamn.qianji_auto.database.Helper;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.hjq.toast.ToastUtils;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.database.Table.CategoryName;
import cn.dreamn.qianji_auto.ui.utils.CategoryUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Task;

public class CategoryNames {
    public interface getCateNameObj{
        void onGet(Bundle[] categoryNames);
    }
    public interface getCateNameOneObj{
        void onGet(Bundle categoryNames);
    }
    public interface getCateNameStr{
        void onGet(String categoryNames);
    }
    public interface getCateNameBoolean{
        void onGet(boolean isSucceed);
    }
    private static Bundle toBundle(CategoryName categoryName){
        Bundle bundle = new Bundle();
        bundle.putInt("id", categoryName.id);
        bundle.putString("book_id", categoryName.book_id);
        bundle.putString("icon", categoryName.icon);
        bundle.putString("level", categoryName.level);
        bundle.putString("name", categoryName.name);
        bundle.putString("parent_id", categoryName.parent_id);
        bundle.putString("sort", categoryName.sort);
        bundle.putString("self_id", categoryName.self_id);
        bundle.putString("type", categoryName.type);
        if (bundle.getString("icon") == null || !bundle.getString("icon").startsWith("http")) {
            bundle.putString("icon", "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png");
        }
        return bundle;
    }
    private static Bundle[] toBundles(CategoryName[] categoryNames,Bundle add){
        ArrayList<Bundle> bundleArrayList = new ArrayList<>();
        for (CategoryName category:categoryNames) {
            bundleArrayList.add(toBundle(category));
        }
        if(add!=null)
         bundleArrayList.add(add);
        return bundleArrayList.toArray(new Bundle[0]);
    }
    public static void getParentByPay(String book_id,getCateNameObj getCateName) {
        Task.onThread(()-> {
            getCateName.onGet(toBundles(DbManger.db.CategoryNameDao().get("0", book_id),null));
        });

    }
    public static void getParents(String book_id,String type,getCateNameObj getCateName) {
        Task.onThread(()-> {
            getCateName.onGet(toBundles(DbManger.db.CategoryNameDao().get(type, book_id),null));
        });

    }

    public static void getParentByIncome(String book_id,getCateNameObj getCateName) {
        Task.onThread(()-> getCateName.onGet(toBundles(DbManger.db.CategoryNameDao().get("1", book_id),null)));
    }

    public static void getChildrenByPay(String parent_id, String book_id,getCateNameObj getCateName) {
        Task.onThread(()-> getCateName.onGet(toBundles(DbManger.db.CategoryNameDao().get("0", parent_id, book_id),null)));
    }

    public static void getChildrenByIncome(String parent_id, String book_id,getCateNameObj getCateName) {
        Task.onThread(()-> getCateName.onGet(toBundles(DbManger.db.CategoryNameDao().get("1", parent_id, book_id),null)));
    }

    public static void getChildrens(String parent_id, String book_id,String type,Boolean allow,getCateNameObj getCateName) {
        Task.onThread(()->{
            Bundle bundle=null;
            if(allow){
         bundle=new Bundle();
        bundle.putInt("id",-2);
        bundle.putString("book_id","-2");
        bundle.putString("icon","https://pic.dreamn.cn/uPic/2021032314475916164820791616482079785ssWEen添加.png");
        bundle.putString("level","2");
        bundle.putString("name","添加子类");
        bundle.putString("parent_id","-2");
                bundle.putString("self_id","-2");
        bundle.putString("sort","1000");
        bundle.putString("type",type);
            }
        getCateName.onGet(toBundles(DbManger.db.CategoryNameDao().get(type, parent_id, book_id),bundle));
    });
    }
    public static void getChildren(String parent_id, String book_id,String type,Boolean allow,getCateNameObj getCateName) {
        Task.onThread(()->{
            Bundle bundle=null;
            if(allow){
                bundle=new Bundle();
                bundle.putInt("id",-2);
                bundle.putString("book_id","-2");
                bundle.putString("icon","https://pic.dreamn.cn/uPic/2021032314475916164820791616482079785ssWEen添加.png");
                bundle.putString("level","2");
                bundle.putString("name","添加子类");
                bundle.putString("parent_id","-2");
                bundle.putString("self_id","-2");
                bundle.putString("sort","1000");
                bundle.putString("type",type);
            }
            getCateName.onGet(toBundles(DbManger.db.CategoryNameDao().getOne(type, parent_id, book_id),bundle));
        });
    }

    public static void getPic(String name, String type, String book_id,getCateNameStr getCateName) {
        Task.onThread(()->{
            CategoryName[] categoryNames = DbManger.db.CategoryNameDao().getByName(name, type, book_id);
            if (categoryNames != null && categoryNames.length != 0) {
                String imgSrc = categoryNames[0].icon;
                if (imgSrc == null || !imgSrc.startsWith("http")) {
                    imgSrc = "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png";
                }
                //"https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png"
                getCateName.onGet(imgSrc);
                return;
            }
            getCateName.onGet("https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png");
        });

    }

    public static void insert(String name, String icon, String level, String type, String self_id, String parent_id, String book_id, String sort,getCateNameBoolean getCateName) {
        if (self_id == null || self_id.equals("")) {
            self_id = String.valueOf(System.currentTimeMillis());
        }
        if (sort == null || sort.equals("")) {
            sort = "500";
        }
        String self=self_id;
        String s=sort;
        Task.onThread(()->{
            CategoryName[] categoryNames = DbManger.db.CategoryNameDao().getByName(name, type, book_id);
            if (categoryNames.length != 0) {
                getCateName.onGet(false);
                return;
            }
            DbManger.db.CategoryNameDao().add(name, icon, level, type, self, parent_id, book_id, s);
            getCateName.onGet(true);
        });


    }


    public static void update(int id, String name, String type, String book_id,getCateNameBoolean getCateName) {
        Task.onThread(()-> {
            CategoryName[] categoryNames = DbManger.db.CategoryNameDao().getByName(name, type, book_id);
            if (categoryNames.length != 0) {
                getCateName.onGet(false);
                return;
            }
            DbManger.db.CategoryNameDao().update(id, name);
            getCateName.onGet(true);
        });
    }

    public static void del(int id) {
        Task.onThread(()-> {
            CategoryName[] categoryNames = DbManger.db.CategoryNameDao().get(id);
            if (categoryNames.length == 0) return;

            String self_id = categoryNames[0].self_id;
            String type = categoryNames[0].type;
            String book_id = categoryNames[0].book_id;
            DbManger.db.CategoryNameDao().del(id);

            CategoryName[] categoryNames2 = DbManger.db.CategoryNameDao().get(type, book_id, self_id);
            for (CategoryName categoryName : categoryNames2) {
                DbManger.db.CategoryNameDao().del(categoryName.id);
            }
        });

    }

    public static void clean() {
        Task.onThread(()->DbManger.db.CategoryNameDao().clean());
    }


    public static void showCategorySelect(Context context, String title,String book_id,String type, boolean isFloat,getCateNameOneObj  getOne ) {

        AtomicReference<Bundle> cateName=new AtomicReference<>();

        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_category, null);

        //final TextView list_title = textEntryView.findViewById(R.id.list_title);

        final SwipeRecyclerView recycler_view = textEntryView.findViewById(R.id.recycler_view);
        final Button button=textEntryView.findViewById(R.id.button_next);

        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle cate = cateName.get();
                if (cate == null) {
                    cate = new Bundle();
                    cate.putInt("id", -2);
                    cate.putString("book_id", "-2");
                    cate.putString("icon", "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png");
                    cate.putString("level", "2");
                    cate.putString("name", "其他");
                    cate.putString("parent_id", "-2");
                    cate.putString("self_id", "-2");
                    cate.putString("sort", "1000");
                    cate.putString("type", type);
                }

                getOne.onGet(cate);
                dialog.dismiss();
            }
        });
        dialog.title(null,title);
        CategoryUtils categoryUtils=new CategoryUtils(recycler_view,book_id,type,context,false);

        categoryUtils.show();
        categoryUtils.refreshData(state -> {
            if(state==0) {
                ToastUtils.show(R.string.category_error);

            }else{

                DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                        false, true, false, false);
                if(isFloat){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
                    } else {
                        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
                    }
                }

                dialog.cornerRadius(15f,null);
                dialog.show();
                dialog.cancelable(false);

            }
        });



        categoryUtils.setOnClick(new CategoryUtils.Click() {
            @Override
            public void onParentClick(Bundle bundle, int position) {
                cateName.set(bundle);
            }

            @Override
            public void onItemClick(Bundle bundle, Bundle parent_bundle, int position) {
                cateName.set(bundle);
            }

            @Override
            public void onParentLongClick(Bundle bundle, int position) {

            }
        });





    }
}
