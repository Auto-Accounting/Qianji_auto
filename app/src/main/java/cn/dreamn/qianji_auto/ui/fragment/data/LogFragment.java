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

import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_ERR;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_OK;
import static cn.dreamn.qianji_auto.ui.utils.HandlerUtil.HANDLE_REFRESH;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuInflater;

import androidx.appcompat.widget.PopupMenu;

import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.data.database.Db;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.components.LogScreen;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.TaskThread;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

@Page(name = "日志", anim = CoreAnim.slide)
public class LogFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.components.TitleBar title_bar;

    @BindView(R.id.logScreen)
    LogScreen logScreen;


    StringBuilder logData = new StringBuilder();
    ArrayList<String> list;
    Handler mHandler;


    LoadingDialog loadingDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_data_log;
    }

    @Override
    protected void initViews() {

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case HANDLE_ERR:
                        break;
                    case HANDLE_OK:
                        if (loadingDialog != null) {
                            loadingDialog.close();
                        }
                        String fileName = "temp.log";
                        Tool.writeToCache(getContext(), fileName, logData.toString());
                        logScreen.printLog(list);
                        break;
                    case HANDLE_REFRESH:
                        loadFromData();
                        break;
                }
                String d = (String) msg.obj;
                if ((d != null && !d.equals("")))
                    ToastUtils.show(d);
            }
        };

    }


    protected void initData() {
        MMKV mmkv = MMKV.defaultMMKV();
        if (mmkv.getBoolean("show_log_tip", true)) {
            BottomArea.msg(getContext(), getString(R.string.log_cache_title), getString(R.string.log_cache_body), getString(R.string.log_cache_know), getString(R.string.log_cache_no_show), new BottomArea.MsgCallback() {
                @Override
                public void cancel() {

                }

                @Override
                public void sure() {
                    mmkv.encode("show_log_tip", false);
                }
            });
        }
    }

    @Override
    protected void initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> popToBack());
        title_bar.setRightIconOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);//第二个参数是绑定的那个view
            //获取菜单填充器
            MenuInflater inflater = popup.getMenuInflater();
            //填充菜单
            inflater.inflate(R.menu.log, popup.getMenu());
            //绑定菜单项的点击事件
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.sendLog:
                        String fileName = "temp.log";
                        Tool.shareFile(getContext(), Tool.getCacheFileName(getContext(), fileName));
                        break;
                    case R.id.cleanLog:
                        BottomArea.msg(getContext(), getString(R.string.log_clean_title), getString(R.string.log_clean_body), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.MsgCallback() {
                            @Override
                            public void cancel() {

                            }

                            @Override
                            public void sure() {
                                TaskThread.onThread(() -> {
                                    Db.db.LogDao().delAll();
                                    HandlerUtil.send(mHandler, getString(R.string.log_clean_success), HANDLE_REFRESH);

                                });
                            }
                        });
                        break;
                    case R.id.logMode:
                        MMKV mmkv = MMKV.defaultMMKV();
                        int mode = mmkv.getInt("log_mode", 1);
                        String modeName = getString(R.string.log_no_log);
                        switch (mode) {
                            case 0:
                                modeName = getString(R.string.log_no_log);
                                break;
                            case 1:
                                modeName = getString(R.string.log_simple);
                                break;
                            case 2:
                                modeName = getString(R.string.log_more);
                                break;
                        }
                        BottomArea.list(getContext(), String.format(getString(R.string.log_mode), modeName), Arrays.asList(getString(R.string.log_no_log), getString(R.string.log_simple), getString(R.string.log_more)), new BottomArea.ListCallback() {
                            @Override
                            public void onSelect(int position) {
                                mmkv.encode("log_mode", position);
                                ToastUtils.show(getString(R.string.log_set_success));
                            }
                        });

                        break;
                }
                return false;
            });
            //显示(这一行代码不要忘记了)
            popup.show();
        });
        //  return null;
    }



    public void loadFromData() {
        loadingDialog = new LoadingDialog(getContext(), getString(R.string.log_loading));
        loadingDialog.show();
        list = new ArrayList<>();
        logData = new StringBuilder();
        TaskThread.onThread(() -> {
            cn.dreamn.qianji_auto.data.database.Table.Log[] logs = Db.db.LogDao().loadAll(0, Log.getLimit());
            if (logs == null || logs.length == 0) {
                list.add("no log");
            } else {
                for (cn.dreamn.qianji_auto.data.database.Table.Log log : logs) {
                    logData.append("[").append(DateUtils.getTime(DateUtils.getAnyTime(log.time))).append("]").append("[").append(log.body).append("]").append(log.title.replace("\n", "\\n")).append("\n");

                    list.add("[" + DateUtils.getTime(DateUtils.getAnyTime(log.time)) + "]" + "[" + log.body + "]" + log.title.replace("\n", "\\n"));
                }
            }
            HandlerUtil.send(mHandler, HANDLE_OK);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFromData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }
}
