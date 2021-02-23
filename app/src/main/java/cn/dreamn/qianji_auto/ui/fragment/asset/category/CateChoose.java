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

public class CateChoose {
    private Bundle[] parentArr = new Bundle[0];
    private Bundle[][] childArr = new Bundle[0][];
    private final ExpandableListView expandableListView;
    private final Context context;
    private final String title;

    public CateChoose(ExpandableListView expandableListView, Context context, String title) {
        this.expandableListView = expandableListView;
        this.context = context;
        this.title = title;
    }

    private Bundle getBundle(CategoryName categoryName) {
        Bundle bundle = new Bundle();
        bundle.putString("name", categoryName.name);
        bundle.putInt("id", categoryName.id);
        bundle.putString("icon", categoryName.icon);
        bundle.putString("level", categoryName.level);
        bundle.putString("type", categoryName.type);
        bundle.putString("self_id", categoryName.self_id);
        bundle.putString("parent_id", categoryName.parent_id);
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

        if (title.equals("收入")) {
            CategoryName[] categoryNames = CategoryNames.getParentByIncome();
            for (CategoryName categoryName : categoryNames) {
                Bundle bundle = getBundle(categoryName);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByIncome(categoryName.self_id);
                Bundle[] childs = new Bundle[categoryNames1.length + 1];
                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = getBundle(categoryNames1[i]);
                    childs[i] = bundle2;
                }
                Bundle bundle3 = new Bundle();
                bundle3.putString("name", "添加子类");
                bundle3.putInt("id", -2);
                bundle3.putString("icon", "");
                bundle3.putString("type", categoryName.type);
                bundle3.putString("parent_id", categoryName.self_id);
                childs[categoryNames1.length] = bundle3;
                child.add(childs);
            }
        } else if (title.equals("支出")) {
            CategoryName[] categoryNames = CategoryNames.getParentByPay();
            for (CategoryName categoryName : categoryNames) {

                Bundle bundle = getBundle(categoryName);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByPay(categoryName.self_id);
                Bundle[] childs = new Bundle[categoryNames1.length + 1];
                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = getBundle(categoryNames1[i]);
                    childs[i] = bundle2;
                }
                Bundle bundle3 = new Bundle();
                bundle3.putString("name", "添加子类");
                bundle3.putInt("id", -2);
                bundle3.putString("type", categoryName.type);
                bundle3.putString("parent_id", categoryName.self_id);
                bundle3.putString("icon", "");
                childs[categoryNames1.length] = bundle3;
                child.add(childs);
            }
        } else {
            CategoryName[] categoryNames = CategoryNames.getParentByIncome();
            for (CategoryName categoryName : categoryNames) {
                Bundle bundle = getBundle(categoryName);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByIncome(categoryName.self_id);
                Bundle[] childs = new Bundle[categoryNames1.length + 1];
                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = getBundle(categoryNames1[i]);
                    childs[i] = bundle2;
                }
                Bundle bundle3 = new Bundle();
                bundle3.putString("name", "添加子类");
                bundle3.putInt("id", -2);
                bundle3.putString("icon", "");
                bundle3.putString("type", categoryName.type);
                bundle3.putString("parent_id", categoryName.self_id);
                childs[categoryNames1.length] = bundle3;
                child.add(childs);
            }
            categoryNames = CategoryNames.getParentByPay();
            for (CategoryName categoryName : categoryNames) {

                Bundle bundle = getBundle(categoryName);
                parent.add(bundle);
                CategoryName[] categoryNames1 = CategoryNames.getChildrenByPay(categoryName.self_id);
                Bundle[] childs = new Bundle[categoryNames1.length + 1];
                for (int i = 0; i < categoryNames1.length; i++) {
                    Bundle bundle2 = getBundle(categoryNames1[i]);
                    childs[i] = bundle2;
                }
                Bundle bundle3 = new Bundle();
                bundle3.putString("name", "添加子类");
                bundle3.putInt("id", -2);
                bundle3.putString("type", categoryName.type);
                bundle3.putString("parent_id", categoryName.self_id);
                bundle3.putString("icon", "");
                childs[categoryNames1.length] = bundle3;
                child.add(childs);
            }
        }

        parentArr = parent.toArray(new Bundle[0]);
        childArr = child.toArray(new Bundle[0][0]);
    }

}
