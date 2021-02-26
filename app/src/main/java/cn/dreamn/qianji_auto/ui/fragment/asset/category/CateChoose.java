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

package cn.dreamn.qianji_auto.ui.fragment.asset.category;

import android.content.Context;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import cn.dreamn.qianji_auto.core.db.Helper.CategoryNames;
import cn.dreamn.qianji_auto.core.db.Table.CategoryName;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class CateChoose {
    private Bundle[] parentArr = new Bundle[0];
    private Bundle[][] childArr = new Bundle[0][];
    private final ExpandableListView expandableListView;
    private final Context context;
    private final String title;
    private final Boolean isAdd;
    private final String book_id;

    public CateChoose(ExpandableListView expandableListView, Context context, String title, Boolean isAdd, String book_id) {
        this.expandableListView = expandableListView;
        this.context = context;
        this.title = title;
        this.isAdd = isAdd;
        this.book_id = book_id;
        Logs.d("数据域被创建！bookid" + book_id);

    }

    public boolean isEmpty() {
        return parentArr.length == 0 || childArr.length == 0;
    }

    private Bundle getBundle(CategoryName categoryName) {
        Bundle bundle = new Bundle();
        bundle.putString("name", categoryName.name == null ? "" : categoryName.name);
        bundle.putInt("id", categoryName.id);
        bundle.putString("icon", categoryName.icon == null ? "" : categoryName.icon);
        bundle.putString("level", categoryName.level == null ? "" : categoryName.level);
        bundle.putString("type", categoryName.type == null ? "" : categoryName.type);
        bundle.putString("self_id", categoryName.self_id == null ? "" : categoryName.self_id);
        bundle.putString("parent_id", categoryName.parent_id == null ? "" : categoryName.parent_id);
        bundle.putString("book_id", categoryName.book_id == null ? "-1" : categoryName.book_id);
        return bundle;
    }

    public void refresh() {
        AdapterData adapterData = new AdapterData();
        getAdapterData();
        adapterData.setData(context, parentArr, childArr);
        expandableListView.setGroupIndicator(null);
        expandableListView.setAdapter(adapterData);

    }

    public interface CallBack {
        void OnLongClickGroup(Bundle parent);

        void OnLongClickChild(Bundle parent, Bundle child);

        void OnClickChild(Bundle parent, Bundle child);
    }

    public void setOnClick(CallBack callBack) {
        expandableListView.setOnItemLongClickListener((arg0, arg1, arg2, arg3) -> {

            if (ExpandableListView.getPackedPositionType(arg3) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                long packPos = ((ExpandableListView) arg0).getExpandableListPosition(arg2);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packPos);
                int childPosition = ExpandableListView.getPackedPositionChild(packPos);
                callBack.OnLongClickChild(parentArr[groupPosition], childArr[groupPosition][childPosition]);

            } else {
                long packPos = ((ExpandableListView) arg0).getExpandableListPosition(arg2);
                int groupPosition = ExpandableListView.getPackedPositionGroup(packPos);
                Bundle bundle = parentArr[groupPosition];
                callBack.OnLongClickGroup(bundle);

            }

            return true;
        });
        expandableListView.setOnChildClickListener((a, v, groupPos, childPos, id) -> {
            callBack.OnClickChild(parentArr[groupPos], childArr[groupPos][childPos]);
            return true;
        });

    }


    private void getAdapterData() {
        ArrayList<Bundle> parent = new ArrayList<>();
        ArrayList<Bundle[]> child = new ArrayList<>();
        Logs.d("Qianji-catechoose", "book_id " + book_id + "title " + title);
        if (title.equals("收入")) {
            CategoryName[] categoryNames = CategoryNames.getParentByIncome(book_id);
            for (CategoryName categoryName : categoryNames) {
                Bundle bundle = getBundle(categoryName);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByIncome(categoryName.self_id, book_id);
                Bundle[] childs;
                if (isAdd)
                    childs = new Bundle[categoryNames1.length + 1];
                else
                    childs = new Bundle[categoryNames1.length];

                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = getBundle(categoryNames1[i]);
                    childs[i] = bundle2;
                }
                if (isAdd) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putString("name", "添加子类");
                    bundle3.putInt("id", -2);
                    bundle3.putString("icon", "");
                    bundle3.putString("book_id", categoryName.book_id);
                    bundle3.putString("type", categoryName.type);
                    bundle3.putString("parent_id", categoryName.self_id);
                    childs[categoryNames1.length] = bundle3;
                }

                child.add(childs);
            }
        } else if (title.equals("支出")) {
            CategoryName[] categoryNames = CategoryNames.getParentByPay(book_id);
            for (CategoryName categoryName : categoryNames) {

                Bundle bundle = getBundle(categoryName);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByPay(categoryName.self_id, book_id);
                Bundle[] childs;
                if (isAdd)
                    childs = new Bundle[categoryNames1.length + 1];
                else
                    childs = new Bundle[categoryNames1.length];
                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = getBundle(categoryNames1[i]);
                    childs[i] = bundle2;
                }
                if (isAdd) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putString("name", "添加子类");
                    bundle3.putInt("id", -2);
                    bundle3.putString("icon", "");
                    bundle3.putString("book_id", categoryName.book_id);
                    bundle3.putString("type", categoryName.type);
                    bundle3.putString("parent_id", categoryName.self_id);
                    childs[categoryNames1.length] = bundle3;
                }
                child.add(childs);
            }
        } else {
            CategoryName[] categoryNames = CategoryNames.getParentByIncome(book_id);
            for (CategoryName categoryName : categoryNames) {
                Bundle bundle = getBundle(categoryName);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByIncome(categoryName.self_id, book_id);
                Bundle[] childs;
                if (isAdd)
                    childs = new Bundle[categoryNames1.length + 1];
                else
                    childs = new Bundle[categoryNames1.length];
                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = getBundle(categoryNames1[i]);
                    childs[i] = bundle2;
                }
                if (isAdd) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putString("name", "添加子类");
                    bundle3.putInt("id", -2);
                    bundle3.putString("icon", "");
                    bundle3.putString("book_id", categoryName.book_id);
                    bundle3.putString("type", categoryName.type);
                    bundle3.putString("parent_id", categoryName.self_id);
                    childs[categoryNames1.length] = bundle3;
                }
                child.add(childs);
            }
            categoryNames = CategoryNames.getParentByPay(book_id);
            for (CategoryName categoryName : categoryNames) {

                Bundle bundle = getBundle(categoryName);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByPay(categoryName.self_id, book_id);
                Bundle[] childs;
                if (isAdd)
                    childs = new Bundle[categoryNames1.length + 1];
                else
                    childs = new Bundle[categoryNames1.length];
                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = getBundle(categoryNames1[i]);
                    childs[i] = bundle2;
                }
                if (isAdd) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putString("name", "添加子类");
                    bundle3.putInt("id", -2);
                    bundle3.putString("icon", "");
                    bundle3.putString("book_id", categoryName.book_id);
                    bundle3.putString("type", categoryName.type);
                    bundle3.putString("parent_id", categoryName.self_id);
                    childs[categoryNames1.length] = bundle3;
                }
                child.add(childs);
            }
        }

        parentArr = parent.toArray(new Bundle[0]);
        childArr = child.toArray(new Bundle[0][0]);
    }

}
