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

package cn.dreamn.qianji_auto.ui.fragment.about;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.afollestad.materialdialogs.files.DialogFileChooserExtKt;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.google.android.material.textfield.TextInputEditText;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;
import com.xuexiang.xpage.utils.TitleBar;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.floats.AutoFloat2;
import cn.dreamn.qianji_auto.ui.views.LoadingDialog;
import cn.dreamn.qianji_auto.utils.files.BackupManager;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;
import es.dmoral.toasty.Toasty;


@Page(name = "备份与恢复", anim = CoreAnim.slide)
public class BackUpFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.views.TitleBar title_bar;
    @BindView(R.id.rl_site_url)
    RelativeLayout rl_site_url;
    @BindView(R.id.rl_site_username)
    RelativeLayout rl_site_username;
    @BindView(R.id.rl_site_password)
    RelativeLayout rl_site_password;
    @BindView(R.id.rl_backup_local)
    RelativeLayout rl_backup_local;
    @BindView(R.id.rl_backup_webdav)
    RelativeLayout rl_backup_webdav;
    @BindView(R.id.rl_restore_webdav)
    RelativeLayout rl_restore_webdav;
    @BindView(R.id.rl_restore_local)
    RelativeLayout rl_restore_local;

    @BindView(R.id.site_url)
    TextView site_url;
    @BindView(R.id.site_username)
    TextView site_username;
    @BindView(R.id.site_password)
    TextView site_password;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_backup;
    }


    @Override
    protected void initViews() {
        MMKV mmkv = MMKV.defaultMMKV();
        site_url.setText(mmkv.getString("webdav_url", ""));
        site_username.setText(mmkv.getString("webdav_name", ""));
        site_password.setText(mmkv.getString("webdav_password", ""));
    }

    @Override
    protected void initListeners() {
        MMKV mmkv = MMKV.defaultMMKV();
        rl_site_url.setOnClickListener(v -> list("请选择", new String[]{"选择WebDav平台", "输入URL地址"}, (index, text) -> {
            if (index == 0) {
                list("请选择WebDav平台", new String[]{"坚果云", "城通网盘私有云", "城通网盘公有云", "TeraCloud"}, new listRes() {
                    @Override
                    public void onSelect(int index, String text) {
                        mmkv.encode("webdav_url", text);
                        initViews();
                    }
                });
            } else {
                input("请输入WebDavUrl", mmkv.getString("webdav_url", ""), new AutoFloat2.InputData() {
                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onOK(String data) {
                        if (data.length() == 0) {
                            Toasty.error(Objects.requireNonNull(getContext()), "未输入WebDav网址！", Toasty.LENGTH_LONG).show();
                            return;
                        }
                        mmkv.encode("webdav_url", data);
                        initViews();
                    }
                });

            }
        }));
        rl_site_username.setOnClickListener(v -> {
            input("请输入WebDav的账号", mmkv.getString("webdav_name", ""), new AutoFloat2.InputData() {
                @Override
                public void onClose() {

                }

                @Override
                public void onOK(String data) {
                    if (data.length() == 0) {
                        Toasty.error(getContext(), "未输入账号！", Toasty.LENGTH_LONG).show();
                        return;
                    }
                    mmkv.encode("webdav_name", data);
                    initViews();
                }
            });
        });
        rl_site_password.setOnClickListener(v -> {
            input("请输入WebDav的密码", mmkv.getString("webdav_password", ""), new AutoFloat2.InputData() {
                @Override
                public void onClose() {

                }

                @Override
                public void onOK(String data) {
                    if (data.length() == 0) {
                        Toasty.error(getContext(), "未输入密码！", Toasty.LENGTH_LONG).show();
                        return;
                    }
                    mmkv.encode("webdav_password", data);
                    initViews();
                }
            });
        });
        rl_backup_webdav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackupManager.init(getContext());
                LoadingDialog dialog = new LoadingDialog(getContext(), "数据备份中...");
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        dialog.close();
                        if (msg.what == -1) {
                            //失败
                            Toasty.error(getContext(), "备份失败！", Toasty.LENGTH_LONG).show();
                        } else {
                            Toasty.success(getContext(), "备份成功！", Toasty.LENGTH_LONG).show();
                        }

                    }
                };
                dialog.show();

                Task.onThread(() -> {
                    String url = mmkv.getString("webdav_url", "");
                    String name = mmkv.getString("webdav_name", "");
                    String password = mmkv.getString("webdav_password", "");
                    BackupManager.backUpToWebDav(getContext(), url, name, password, mHandler);
                });
            }
        });
        rl_backup_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackupManager.init(getContext());
                LoadingDialog dialog = new LoadingDialog(getContext(), "数据备份中...");
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        dialog.close();
                        if (msg.what == -1) {
                            //失败
                            Toasty.error(getContext(), "备份失败！", Toasty.LENGTH_LONG).show();
                        } else {
                            Toasty.success(getContext(), "备份成功，备份文件到" + msg.obj + "！", Toasty.LENGTH_LONG).show();
                        }

                    }
                };
                dialog.show();
                Task.onThread(() -> BackupManager.backUpToLocal(getContext(), mHandler));
            }
        });
        rl_restore_local.setOnClickListener(v -> {

            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
            dialog.title(null, "请选择自动记账配置文件");

            DialogFileChooserExtKt.fileChooser(dialog, getContext(), Environment.getExternalStorageDirectory(), file -> {
                        return file.isDirectory() || (file.isFile() && file.getName().endsWith("backup"));
                    },
                    true, R.string.files_default_empty_text, false, null,
                    (materialDialog, file) -> {
                        //Log.d(file.getAbsolutePath());
                        BackupManager.init(getContext());
                        LoadingDialog dialog2 = new LoadingDialog(getContext(), "数据恢复中...");
                        Handler mHandler = new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                dialog2.close();
                                if (msg.what == -1) {
                                    //失败
                                    Toasty.error(getContext(), "恢复失败！", Toasty.LENGTH_LONG).show();
                                } else {
                                    Toasty.success(getContext(), "恢复成功！", Toasty.LENGTH_LONG).show();
                                    Tool.restartApp(getActivity());
                                }

                            }
                        };
                        dialog2.show();
                        Task.onThread(() -> BackupManager.restoreFromLocal(file.getAbsolutePath(), getContext(), mHandler));
                        return null;
                    });

            dialog.show();


        });

        rl_restore_webdav.setOnClickListener(v -> {
            BackupManager.init(getContext());
            final LoadingDialog[] dialog = {new LoadingDialog(getContext(), "数据加载中...")};
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @SuppressLint("CheckResult")
                @Override
                public void handleMessage(@NonNull Message msg) {
                    dialog[0].close();
                    if (msg.what == -1) {
                        //失败
                        Toasty.error(getContext(), "恢复失败！", Toasty.LENGTH_LONG).show();
                    } else if (msg.what == 2) {
                        Toasty.success(getContext(), "恢复成功！", Toasty.LENGTH_LONG).show();
                        Tool.restartApp(getActivity());

                    } else {

                        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
                        MaterialDialog dialog1 = new MaterialDialog(getContext(), bottomSheet);
                        dialog1.title(null, "请选择恢复的云数据");

                        DialogListExtKt.listItems(dialog1, null, (List<String>) msg.obj, null, true, (materialDialog, index, text) -> {
                            dialog1.dismiss();
                            Task.onThread(() -> {
                                BackupManager.restoreFromWebDavByBackground(getContext(), mmkv.getString("webdav_url", ""), mmkv.getString("webdav_name", ""), mmkv.getString("webdav_password", ""), text.toString(), this);

                            });
                            dialog[0] = new LoadingDialog(getContext(), "数据恢复中...");
                            dialog[0].show();
                            return null;
                        });

                        dialog1.cornerRadius(15f, null);
                        dialog1.show();


                    }

                }
            };
            dialog[0].show();
            Task.onThread(() -> {
                BackupManager.restoreFromWebDav(getContext(), mmkv.getString("webdav_url", ""), mmkv.getString("webdav_name", ""), mmkv.getString("webdav_password", ""), mHandler);
            });
        });

    }

    @SuppressLint("CheckResult")
    public void list(String title, String[] strings, listRes inputData) {

        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
        dialog.cornerRadius(15f, null);
        dialog.title(null, title);


        DialogListExtKt.listItems(dialog, null, Arrays.asList(strings), null, true, (materialDialog, index, text) -> {
            inputData.onSelect(index, (String) text);
            return null;
        });


        dialog.show();
    }

    public void input(String title, String defData, AutoFloat2.InputData inputData) {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View textEntryView = factory.inflate(R.layout.list_input, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
        dialog.title(null, title);

        TextInputEditText md_input_message = textEntryView.findViewById(R.id.md_input_message);

        md_input_message.setText(defData);

        Button button_next = textEntryView.findViewById(R.id.button_next);
        Button button_last = textEntryView.findViewById(R.id.button_last);

        button_next.setOnClickListener(v -> {
            inputData.onOK(md_input_message.getText().toString());
            dialog.dismiss();
        });

        button_last.setOnClickListener(v -> {
            inputData.onClose();
            dialog.dismiss();
        });

        DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                false, true, false, false);

        dialog.cornerRadius(15f, null);
        dialog.show();
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

    interface listRes {
        void onSelect(int index, String text);
    }


}
