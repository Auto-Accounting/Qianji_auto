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

package cn.dreamn.qianji_auto.core.db.Helper;

import cn.dreamn.qianji_auto.core.db.DbManger;
import cn.dreamn.qianji_auto.core.db.Table.CategoryName;

public class CategoryNames {
    public static CategoryName[] getParentByPay() {
        return DbManger.db.CategoryNameDao().get("0");
    }

    public static CategoryName[] getParentByIncome() {
        return DbManger.db.CategoryNameDao().get("1");
    }

    public static CategoryName[] getChildrenByPay(String parent_id) {
        return DbManger.db.CategoryNameDao().get("0", parent_id);
    }

    public static CategoryName[] getChildrenByIncome(String parent_id) {
        return DbManger.db.CategoryNameDao().get("1", parent_id);
    }

    public static String getPic(String name, String type) {
        CategoryName[] categoryNames = DbManger.db.CategoryNameDao().getByName(name, type);
        if (categoryNames != null && categoryNames.length != 0)
            return categoryNames[0].icon;
        return "";
    }

    public static boolean insert(String name, String icon, String level, String type, String self_id, String parent_id) {
        if (self_id.equals("")) {
            self_id = String.valueOf(System.currentTimeMillis());
        }
        CategoryName[] categoryNames = DbManger.db.CategoryNameDao().getByName(name, type);
        if (categoryNames.length != 0) return false;
        DbManger.db.CategoryNameDao().add(name, icon, level, type, self_id, parent_id);
        return true;
    }

    public static boolean update(int id, String name, String type) {
        CategoryName[] categoryNames = DbManger.db.CategoryNameDao().getByName(name, type);
        if (categoryNames.length != 0) return false;
        DbManger.db.CategoryNameDao().update(id, name);
        return true;
    }

    public static void del(int id) {
        CategoryName[] categoryNames = DbManger.db.CategoryNameDao().get(id);
        String parent_id = "-1";
        String type = "0";
        if (categoryNames.length != 0) {
            parent_id = categoryNames[0].parent_id;
            type = categoryNames[0].type;
        }
        DbManger.db.CategoryNameDao().del(id);
        CategoryName[] categoryNames2 = DbManger.db.CategoryNameDao().get(type, parent_id);
        for (CategoryName categoryName : categoryNames2) {
            DbManger.db.CategoryNameDao().del(categoryName.id);
        }

    }

    public static void clean() {
        DbManger.db.CategoryNameDao().clean();
    }
}
