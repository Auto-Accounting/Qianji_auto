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

package cn.dreamn.qianji_auto.core.db;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface AutoBillDao {
    @Query("SELECT * FROM autobill order by id desc")
    AutoBill[] getAll();
    @Query("SELECT * FROM autobill WHERE id=:id")
    AutoBill[] get(int id);
    @Query("DELETE FROM autobill WHERE id=:id")
    void del(int id);
    @Query("INSERT INTO autobill(billInfo,type,money,time,remark,catename,catechoose,bookname,accountname,accountname2,shopAccount,shopRemark,source) values(:billInfo,:type,:money,:time,:remark,:catename,:catechoose,:bookname,:accountname,:accountname2,:shopAccount,:shopRemark,:source)")
    void add(String billInfo,String type,String money,String time,String remark,String catename,String catechoose,String bookname,String accountname,String accountname2,String shopAccount,String shopRemark,String source);
    @Query("UPDATE  autobill SET billInfo=:billInfo, type=:type,money=:money,time=:time,remark=:remark,catename=:catename,catechoose=:catechoose,bookname=:bookname,accountname=:accountname,accountname2=:accountname2,shopAccount=:shopAccount,shopRemark=:shopRemark,source=:source WHERE id=:id")
    void update(int id,String billInfo, String type,String money,String time,String remark,String catename,String catechoose,String bookname,String accountname,String accountname2,String shopAccount,String shopRemark,String source);
    @Query("DELETE FROM autobill")
    void delAll();
}

