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

package cn.dreamn.qianji_auto.data.database.Dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.concurrent.CompletableFuture;

import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.data.database.Table.Category;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;


@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category WHERE type=:type AND level='1' AND book_id=:book_id order by CAST(sort as int)")
    Category[] get(String type, String book_id);

    @Query("SELECT * FROM Category WHERE type=:type AND level='2' AND parent_id=:parent  AND book_id=:book_id  order by CAST(sort as int)")
    Category[] get(String type, String parent, String book_id);

    @Query("SELECT * FROM Category WHERE type=:type AND level='2' AND parent_id=:parent  AND book_id=:book_id limit 1")
    Category[] getOne(String type, String parent, String book_id);

    @Query("DELETE FROM Category WHERE id=:id")
    void del(int id);

    @Query("SELECT * FROM Category WHERE id=:id limit 1")
    Category[] get(int id);

    @Query("SELECT * FROM Category WHERE self_id=:self_id limit 1")
    Category[] get(String self_id);

    @Query("INSERT INTO Category(name,icon,level,type,self_id,parent_id,book_id,sort) values(:name,:icon,:level,:type,:self_id,:parent_id,:book_id,:sort)")
    void add(String name, String icon, String level, String type, String self_id, String parent_id, String book_id, String sort);

    @Query("UPDATE  Category SET name=:name WHERE id=:id")
    void update(int id, String name);

    @Query("Select * from Category where name=:name and type=:type AND book_id=:book_id")
    Category[] getByName(String name, String type, String book_id);

    @Query("DELETE  FROM Category")
    void clean();

    default CompletableFuture<String> getParentCategory(BillInfo billInfo) {
        return CompletableFuture.supplyAsync(() -> {
            String bookId = Db.db.BookNameDao().get(billInfo.getBookName())[0].book_id;
            String parentId = getByName(billInfo.getCateName(), billInfo.getType(), bookId)[0].parent_id;
            Category[] parentData = get(parentId);
            if (parentData.length > 0) {
                return parentData[0].name;
            } else {
                return billInfo.getCateName();
            }

        });
    }
}

