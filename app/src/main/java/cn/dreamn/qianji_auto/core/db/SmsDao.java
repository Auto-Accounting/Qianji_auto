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
public interface SmsDao {
    @Query("SELECT * FROM sms WHERE use=1")
    public Sms[] load();
    @Query("SELECT * FROM sms")
    public Sms[] loadAll();
    @Query("DELETE FROM sms WHERE id=:id")
    public void delete(int id);
    @Query("UPDATE  sms SET regular=:regular,name=:name,smsNum=:num WHERE id=:id")
    public void update(int id,String regular, String name, String num);
    @Query("UPDATE  sms SET use=1 WHERE id=:id")
    public void enable(int id);
    @Query("UPDATE  sms SET use=0 WHERE id=:id")
    public void deny(int id);
    @Query("INSERT INTO sms(regular,name,smsNum,use) values(:regex,:name,:num,1)")
    void add(String regex, String name, String num);
    @Query("DELETE FROM sms")
    void clean();
}

