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

package cn.dreamn.qianji_auto.data.database.Helper;


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
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Table.Category;
import cn.dreamn.qianji_auto.ui.utils.CategoryUtils;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;

public class Categorys {
    private static Bundle toBundle(Category category) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", category.id);
        bundle.putString("book_id", category.book_id);
        bundle.putString("icon", category.icon);
        bundle.putString("level", category.level);
        bundle.putString("name", category.name);
        bundle.putString("parent_id", category.parent_id);
        bundle.putString("sort", category.sort);
        bundle.putString("self_id", category.self_id);
        bundle.putString("type", category.type);
        if (bundle.getString("icon") == null || !bundle.getString("icon").startsWith("http")) {
            bundle.putString("icon", "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png");
        }
        return bundle;
    }

    private static Bundle[] toBundles(Category[] categories, Bundle add) {
        ArrayList<Bundle> bundleArrayList = new ArrayList<>();
        for (Category category : categories) {
            bundleArrayList.add(toBundle(category));
        }
        if (add != null)
            bundleArrayList.add(add);
        return bundleArrayList.toArray(new Bundle[0]);
    }

    public static void getParentByPay(String book_id, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> taskResult.onEnd(toBundles(Db.db.CategoryDao().get("0", book_id), null)));

    }

    public static void getParents(String book_id, String type, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            taskResult.onEnd(toBundles(Db.db.CategoryDao().get(type, book_id), null));
        });

    }

    public static void getParentByIncome(String book_id, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> taskResult.onEnd(toBundles(Db.db.CategoryDao().get("1", book_id), null)));
    }

    public static void getChildrenByPay(String parent_id, String book_id, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> taskResult.onEnd(toBundles(Db.db.CategoryDao().get("0", parent_id, book_id), null)));
    }

    public static void getChildrenByIncome(String parent_id, String book_id, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> taskResult.onEnd(toBundles(Db.db.CategoryDao().get("1", parent_id, book_id), null)));
    }

    public static void getChildrens(String parent_id, String book_id, String type, Boolean allow, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            Bundle bundle = null;
            if (allow) {
                bundle = new Bundle();
                bundle.putInt("id", -2);
                bundle.putString("book_id", "-2");
                bundle.putString("icon", "https://pic.dreamn.cn/uPic/2021032314475916164820791616482079785ssWEen添加.png");
                bundle.putString("level", "2");
                bundle.putString("name", "添加子类");
                bundle.putString("parent_id", "-2");
                bundle.putString("self_id", "-2");
                bundle.putString("sort", "1000");
                bundle.putString("type", type);
            }
            taskResult.onEnd(toBundles(Db.db.CategoryDao().get(type, parent_id, book_id), bundle));
        });
    }

    public static void getChildren(String parent_id, String book_id, String type, Boolean allow, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            Bundle bundle = null;
            if (allow) {
                bundle = new Bundle();
                bundle.putInt("id", -2);
                bundle.putString("book_id", "-2");
                bundle.putString("icon", "https://pic.dreamn.cn/uPic/2021032314475916164820791616482079785ssWEen添加.png");
                bundle.putString("level", "2");
                bundle.putString("name", "添加子类");
                bundle.putString("parent_id", "-2");
                bundle.putString("self_id", "-2");
                bundle.putString("sort","1000");
                bundle.putString("type",type);
            }
            taskResult.onEnd(toBundles(Db.db.CategoryDao().getOne(type, parent_id, book_id), bundle));
        });
    }

    public static void getPic(String name, String type, String book_id, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            Category[] categories = Db.db.CategoryDao().getByName(name, type, book_id);
            if (categories != null && categories.length != 0) {
                String imgSrc = categories[0].icon;
                if (imgSrc == null || !imgSrc.startsWith("http")) {
                    imgSrc = "https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png";
                }
                //"https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png"
                taskResult.onEnd(imgSrc);
                return;
            }
            taskResult.onEnd("https://pic.dreamn.cn/uPic/2021032310470716164676271616467627123WiARFwd8b1f5bdd0fca9378a915d8531cb740b.png");
        });

    }

    public static void insert(String name, String icon, String level, String type, String self_id, String parent_id, String book_id, String sort, TaskThread.TaskResult taskResult) {
        if (self_id == null || self_id.equals("")) {
            self_id = String.valueOf(System.currentTimeMillis());
        }
        if (sort == null || sort.equals("")) {
            sort = "500";
        }
        String self = self_id;
        String s = sort;
        TaskThread.onThread(() -> {
            Category[] categories = Db.db.CategoryDao().getByName(name, type, book_id);
            if (categories.length != 0) {
                taskResult.onEnd(false);
                return;
            }
            Db.db.CategoryDao().add(name, icon, level, type, self, parent_id, book_id, s);
            taskResult.onEnd(true);
        });


    }


    public static void update(int id, String name, String type, String book_id, TaskThread.TaskResult taskResult) {
        TaskThread.onThread(() -> {
            Category[] categories = Db.db.CategoryDao().getByName(name, type, book_id);
            if (categories.length != 0) {
                taskResult.onEnd(false);
                return;
            }
            Db.db.CategoryDao().update(id, name);
            taskResult.onEnd(true);
        });
    }

    public static void del(int id) {
        TaskThread.onThread(() -> {
            Category[] categories = Db.db.CategoryDao().get(id);
            if (categories.length == 0) return;

            String self_id = categories[0].self_id;
            String type = categories[0].type;
            String book_id = categories[0].book_id;
            Db.db.CategoryDao().del(id);

            Category[] categoryNames2 = Db.db.CategoryDao().get(type, book_id, self_id);
            for (Category category : categoryNames2) {
                Db.db.CategoryDao().del(category.id);
            }
        });

    }

    public static void clean() {
        TaskThread.onThread(() -> Db.db.CategoryDao().clean());
    }


    public static void showCategorySelect(Context context, String title, String book_id, String type, boolean isFloat, TaskThread.TaskResult taskResult) {

        AtomicReference<Bundle> cateName = new AtomicReference<>();

        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_category, null);

        //final TextView list_title = textEntryView.findViewById(R.id.list_title);

        final SwipeRecyclerView recycler_view = textEntryView.findViewById(R.id.recycler_view);
        final Button button = textEntryView.findViewById(R.id.button_next);

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

                taskResult.onEnd(cate);
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
