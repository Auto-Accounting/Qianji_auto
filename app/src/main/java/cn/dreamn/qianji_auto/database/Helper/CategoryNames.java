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


import android.os.Bundle;

import java.util.ArrayList;

import cn.dreamn.qianji_auto.database.DbManger;
import cn.dreamn.qianji_auto.database.Table.CategoryName;
import cn.dreamn.qianji_auto.utils.runUtils.Task;

public class CategoryNames {
    public interface getCateNameObj{
        void onGet(Bundle[] categoryNames);
    }
    public interface getCateNameStr{
        void onGet(String categoryNames);
    }
    public interface getCateNameBoolean{
        void onGet(boolean isSucceed);
    }
    private static Bundle toBundle(CategoryName categoryName){
        Bundle bundle=new Bundle();
        bundle.putInt("id",categoryName.id);
        bundle.putString("book_id",categoryName.book_id);
        bundle.putString("icon",categoryName.icon);
        bundle.putString("level",categoryName.level);
        bundle.putString("name",categoryName.name);
        bundle.putString("parent_id",categoryName.parent_id);
        bundle.putString("sort",categoryName.sort);
        bundle.putString("type",categoryName.type);

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
        bundle.putString("name","添加");
        bundle.putString("parent_id","-2");
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
                bundle.putString("name","添加");
                bundle.putString("parent_id","-2");
                bundle.putString("sort","1000");
                bundle.putString("type",type);
            }
            getCateName.onGet(toBundles(DbManger.db.CategoryNameDao().getOne(type, parent_id, book_id),bundle));
        });
    }

    public static void getPic(String name, String type, String book_id,getCateNameStr getCateName) {
        Task.onThread(()->{
            CategoryName[] categoryNames = DbManger.db.CategoryNameDao().getByName(name, type, book_id);
            if (categoryNames != null && categoryNames.length != 0){
                getCateName.onGet( categoryNames[0].icon);
                return;
            }
            getCateName.onGet("");
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
}
