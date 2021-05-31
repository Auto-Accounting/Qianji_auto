/*
 * Copyright (C) 2021 xuexiangjys(xuexiangjys@163.com)
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

package cn.dreamn.qianji_auto.ui.fragment.data;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.shehuan.statusview.StatusView;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.database.Helper.AppDatas;
import cn.dreamn.qianji_auto.database.Helper.identifyRegulars;
import cn.dreamn.qianji_auto.ui.adapter.ItemListAdapter;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.utils.AutoBillWeb;
import cn.dreamn.qianji_auto.ui.views.LoadingDialog;
import cn.dreamn.qianji_auto.utils.runUtils.DataUtils;
import cn.dreamn.qianji_auto.utils.runUtils.JsEngine;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import es.dmoral.toasty.Toasty;

import static cn.dreamn.qianji_auto.ui.fragment.data.NoticeFragment.KEY_DATA;


@Page(name = "通知列表", params = {KEY_DATA}, anim = CoreAnim.slide)

public class NoticeFragment extends BaseFragment {

    public static final String KEY_DATA = "KEY_DATA";
    private static final int HANDLE_REFRESH = 2;
    private static final int HANDLE_OK = 0;
    private static final int HANDLE_ERR = -1;
    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.views.TitleBar title_bar;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_tip)
    TextView tv_tip;
    @BindView(R.id.recycler_view)
    SwipeRecyclerView recycler_view;
    @BindView(R.id.status)
    StatusView statusView;
    private ItemListAdapter mAdapter;
    private List<Bundle> list;
    Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ERR:
                    statusView.showEmptyView();
                    break;
                case HANDLE_OK:
                    mAdapter.refresh(list);
                    Log.m("Qianji-list", list.toString());
                    Task.onMain(1000, () -> statusView.showContentView());
                    break;
                case HANDLE_REFRESH:
                    String d = (String) msg.obj;
                    if ((d != null && !d.equals("")))
                        Toasty.success(getContext(), d, Toast.LENGTH_LONG).show();
                    loadFromData(refreshLayout);
                    break;
            }
        }
    };

    public static void openWithType(BaseFragment baseFragment, String type) {
        //sms notice app
        PageOption.to(NoticeFragment.class)
                .setNewActivity(true)
                .putString(KEY_DATA, type)
                .open(baseFragment);
    }

    private String getType() {
        String target = "";
        Bundle bundle = getArguments();
        if (bundle != null) {
            target = bundle.getString(KEY_DATA);
        }
        return target;
    }

    private String getName() {
        switch (getType()) {
            case "sms":
                return "短信";
            case "notice":
                return "通知";
            case "app":
                return "app";
        }
        return "";
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notice;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initViews() {

        switch (getType()) {
            case "sms":
                title_bar.setTitle("短信列表");
                tv_tip.setText("捕获短信信息，用于从银行短信中获取交易信息。");
                break;
            case "notice":
                title_bar.setTitle("通知列表");
                tv_tip.setText("捕获通知信息，用于从通知中获取交易信息。");
                break;
            case "app":
                title_bar.setTitle("App数据列表");
                tv_tip.setText("捕获App内部信息，用于从App中获取交易信息。");
                break;
        }


        statusView.setEmptyView(R.layout.empty_view);
        statusView.setLoadingView(R.layout.loading_view);

        statusView.setOnEmptyViewConvertListener(viewHolder -> viewHolder.setText(R.id.empty_info, "没有任何" + getName() + "信息"));
        statusView.setOnLoadingViewConvertListener(viewHolder -> viewHolder.setText(R.id.load_info, "正在加载" + getName() + "信息"));

        statusView.showLoadingView();
        initLayout();
    }

    private void initLayout() {
        mAdapter = new ItemListAdapter(getContext());

        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recycler_view.setLayoutManager(linearLayout);
        recycler_view.setAdapter(mAdapter);

        refreshLayout.setOnRefreshListener(this::loadFromData);
//        recycler_view.setOnItemClickListener(this::itemClick);
        mAdapter.setOnItemClickListener(this::itemClick);
        refreshLayout.setEnableRefresh(true);
        loadFromData(refreshLayout);
    }

    @SuppressLint("CheckResult")
    private void itemClick(View view, int i) {
        //点击click
        if (list != null && list.size() > i) {
            Bundle item = list.get(i);
            String[] strings;
            String bool=item.getString("cloud");
            if(bool!=null){
                strings = new String[]{"删除", "创建识别规则", "下载规则"};
            }else{
                bool=item.getString("local");
                if(bool!=null){
                    strings = new String[]{"删除", "编辑识别规则", "上传规则"};
                }else{
                     strings = new String[]{"删除", "创建识别规则", "申请适配"};
                }
            }



            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
            dialog.title(null, "请选择操作");


            DialogListExtKt.listItems(dialog, null, Arrays.asList(strings), null, true, (materialDialog, index, text) -> {
                Log.m("click " + text);
                if (index == 0) {
                    AppDatas.del(item.getInt("id"));
                    Message message = new Message();
                    message.what = HANDLE_REFRESH;
                    message.obj = "删除成功！";
                    mHandler.sendMessage(message);
                } else if(text=="编辑识别规则") {
                   //TODO open a fragment to edit
                }else if(text=="创建识别规则") {
                   // TODO open a fragment to create
                }else if(text=="下载规则") {
                    Bundle bundle = item.getBundle("cloud_data");
                    DataUtils dataUtils = new DataUtils();
                    dataUtils.parse(bundle.getString("tableList"));
                    identifyRegulars.add(
                            bundle.getString("regular"),
                            bundle.getString("name"),
                            bundle.getString("text"),
                            dataUtils.get("account1"),
                            dataUtils.get("account2"),
                            dataUtils.get("type"),
                            dataUtils.get("silent"),
                            dataUtils.get("money"),
                            dataUtils.get("fee"),
                            dataUtils.get("shopName"),
                            dataUtils.get("shopRemark"),
                            dataUtils.get("source"),
                            bundle.getString("identify"),
                            bundle.getString("fromApp"),
                            bundle.getString("des"));
                    Toasty.info(getContext(), "导入成功").show();
                    mHandler.sendEmptyMessage(HANDLE_REFRESH);
                }else if(text=="上传规则") {
                    //TODO Support
                }else if(text=="申请适配") {

                    LoadingDialog dialog1 = new LoadingDialog(getContext(), "正在提交申请，请稍候...");
                    dialog1.show();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", "适配申请");
                    jsonObject.put("text", item.getString("rawData"));
                    jsonObject.put("data", "");
                    jsonObject.put("tableList", "");
                    jsonObject.put("identify", item.getString("identify"));
                    jsonObject.put("fromApp", item.getString("fromApp"));
                    jsonObject.put("isCate", "0");
                    jsonObject.put("description", "适配申请");
                    String result = Base64.encodeToString(jsonObject.toString().getBytes(), Base64.NO_WRAP);
                    AutoBillWeb.httpSend(getContext(), this, "support", result);
                }
                return null;
            });


            dialog.cornerRadius(15f, null);
            dialog.show();
        }
    }

    private void loadFromData(RefreshLayout refreshLayout) {
        Task.onMain(1000, () -> {
            AppDatas.getAll(getType(), datas -> {
                if (datas == null || datas.size() == 0) {
                    mHandler.sendEmptyMessage(HANDLE_ERR);
                } else {

                    AutoBillWeb.getDataWeb(null, getType(), null, new AutoBillWeb.WebCallback() {
                        @Override
                        public void onFailure() {
                            //失败就不显示了
                            mHandler.sendEmptyMessage(HANDLE_OK);
                        }

                        @Override
                        public void onSuccessful(String data) {
                            Log.m("网页返回结果->  " + data);
                            try {
                                JSONObject jsonObject = JSONObject.parseObject(data);
                                if(jsonObject.getInteger("code")==0){
                                    JSONArray jsonArray=jsonObject.getJSONArray("data");
                                    //获取数据部分
                                    StringBuilder code= new StringBuilder();
                                    for(int i=0;i<jsonArray.size();i++){
                                        code.append(jsonArray.getJSONObject(i).getString("data")).append("index++;\n");
                                    }
                                    for(int i=0;i<datas.size();i++){
                                        try {
                                            String result = JsEngine.run( identifyRegulars.getFunction(datas.get(i).getString("rawData"), code.toString()));
                                            Log.i("Qianji-Could", "自动云规则执行结果：" + result);
                                            if(!result.startsWith("undefined")){
                                                datas.get(i).putString("cloud","true");
                                                int index=Integer.parseInt(result.substring(result.lastIndexOf("|")));
                                                Bundle bundle=new Bundle();
                                                bundle.putString("name",jsonArray.getJSONObject(i).getString("name"));
                                                bundle.putString("text",jsonArray.getJSONObject(i).getString("text"));
                                                bundle.putString("regular",jsonArray.getJSONObject(i).getString("data"));
                                                bundle.putString("tableList",jsonArray.getJSONObject(i).getString("tableList"));
                                                bundle.putString("identify",jsonArray.getJSONObject(i).getString("identify"));
                                                bundle.putString("fromApp", jsonArray.getJSONObject(i).getString("fromApp"));
                                                bundle.putString("des", jsonArray.getJSONObject(i).getString("description"));
                                               // bundle.putString("name");
                                                datas.get(i).putBundle("cloud_data",bundle);
                                            }
                                            int finalI = i;
                                            identifyRegulars.getAllRegularJs(datas.get(i).getString("rawData"), "notice", null, str -> {
                                               String result2 = JsEngine.run(str);
                                                if(!result.startsWith("undefined")){
                                                    datas.get(finalI).putString("local","true");
                                                    int index=Integer.parseInt(result.substring(result.lastIndexOf("|")));
                                                    datas.get(finalI).putBundle("local_data",identifyRegulars.getIndexRegular(index));
                                                }

                                                Log.i("Qianji-Local", "自动本地规则执行结果：" + result2);
                                            });

                                        } catch (Exception e) {
                                            Log.i("js执行出错！" + e.toString());
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }catch(Exception|Error e){
                                Log.i("JSON解析错误！！" + e.toString());
                                e.printStackTrace();
                            }
                            list = datas;
                            Log.m("数据" + list.toString());
                            mHandler.sendEmptyMessage(HANDLE_OK);
                        }
                    });

                }
            });
        });
    }

    @Override
    protected void initListeners() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            refreshlayout.finishRefresh(0/*,false*/);//传入false表示刷新失败
        });
        title_bar.setRightIconOnClickListener(v -> {
            MaterialDialog dialog = new MaterialDialog(getContext(), MaterialDialog.getDEFAULT_BEHAVIOR());
            dialog.title(null, "是否清空通知列表");
            dialog.message(null, "即将清空所有通知数据", null);
            dialog.positiveButton(null, "确定清空", materialDialog -> {

                AppDatas.delAll("notice", () -> {
                    Message message = new Message();
                    message.obj = "清除成功！";
                    message.what = HANDLE_REFRESH;
                    mHandler.sendMessage(message);
                });
                return null;
            });
            dialog.negativeButton(null, "取消清空", materialDialog -> {
                return null;
            });

            dialog.show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected TitleBar initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> {
            popToBack();
        });
        return null;
    }


}
