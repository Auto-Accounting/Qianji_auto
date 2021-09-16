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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.permission.PermissionUtils;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.Loading.LoadingDialog;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.utils.files.BackupManager;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Task;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


@Page(name = "备份与恢复", anim = CoreAnim.slide)
public class BackUpFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    cn.dreamn.qianji_auto.ui.components.TitleBar title_bar;
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


        rl_site_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomArea.list(getContext(), getString(R.string.please_select), Arrays.asList(getString(R.string.jgy), getString(R.string.ct_pri), getString(R.string.ct_pub), getString(R.string.TeraCloud), getString(R.string.source_custom)), new BottomArea.ListCallback() {
                    @Override
                    public void onSelect(int position) {
                        if (position == 4) {
                            BottomArea.input(getContext(), getString(R.string.input_webdav), "", getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
                                @Override
                                public void input(String data) {
                                    if (data.length() == 0) {
                                        ToastUtils.show(R.string.no_webdav);
                                        return;
                                    }
                                    mmkv.encode("webdav_url", data);
                                    initViews();
                                }

                                @Override
                                public void cancel() {

                                }
                            });
                        } else {
                            mmkv.encode("webdav_url", String.valueOf(position));
                        }
                    }
                });
            }
        });


        rl_site_username.setOnClickListener(v -> {
            BottomArea.input(getContext(), getString(R.string.webdav_name), mmkv.getString("webdav_name", ""), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
                @Override
                public void input(String data) {
                    if (data.length() == 0) {
                        ToastUtils.show(R.string.no_account);
                        return;
                    }
                    mmkv.encode("webdav_name", data);
                    initViews();
                }

                @Override
                public void cancel() {

                }
            });
        });
        rl_site_password.setOnClickListener(v -> {
            BottomArea.input(getContext(), getString(R.string.webdav_passwd), mmkv.getString("webdav_password", ""), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
                @Override
                public void input(String data) {
                    if (data.length() == 0) {
                        ToastUtils.show(R.string.no_passwd);
                        return;
                    }
                    mmkv.encode("webdav_password", data);
                    initViews();
                }

                @Override
                public void cancel() {

                }
            });
        });

        rl_backup_webdav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackupManager.init(getContext());
                LoadingDialog dialog = new LoadingDialog(getContext(), getString(R.string.backup_loading));
                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        dialog.close();
                        if (msg.what == -1) {
                            //失败
                            ToastUtils.show(R.string.backup_failed);
                        } else {
                            ToastUtils.show(R.string.backup_success);
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
        rl_backup_local.setOnClickListener(v -> {
            BackupManager.init(getContext());
            LoadingDialog dialog = new LoadingDialog(getContext(), getString(R.string.backup_loading));
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    dialog.close();
                    if (msg.what == -1) {
                        //失败
                        ToastUtils.show(R.string.backup_failed);
                    } else {
                        ToastUtils.show(String.format(getString(R.string.backup_success_tip), msg.obj));
                    }
                }
            };
            dialog.show();
            Task.onThread(() -> BackupManager.backUpToLocal(getContext(), mHandler));
        });
        rl_restore_local.setOnClickListener(v -> {
            PermissionUtils permissionUtils = new PermissionUtils(getContext());
            permissionUtils.grant(PermissionUtils.Storage);
            try {

                final DialogProperties properties = new DialogProperties();

                FilePickerDialog dialog = new FilePickerDialog(getContext(), properties);
                dialog.setTitle(getString(R.string.select_backup));
                dialog.setPositiveBtnName(getString(R.string.set_select));
                dialog.setNegativeBtnName(getString(R.string.set_cancle));
                properties.extensions = new String[]{".auto.backup"};
                properties.root = Environment.getExternalStorageDirectory();
                properties.offset = Environment.getExternalStorageDirectory();
                properties.show_hidden_files = false;
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.error_dir = Environment.getExternalStorageDirectory();
                dialog.setProperties(properties);
                dialog.show();

                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {

                        if (msg.what == -1) {
                            //失败
                            ToastUtils.show(R.string.restore_back_failed);
                        } else {
                            ToastUtils.show(R.string.restore_back_success);
                            Tool.restartApp(getActivity());
                        }

                    }
                };

                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        dialog.dismiss();
                        Task.onThread(() -> BackupManager.restoreFromLocal(files[0], getContext(), mHandler));
                    }
                });
            } catch (Exception | Error e) {
                e.printStackTrace();
                Log.d("出错了，可能是权限未给全！" + e.toString());
            }


        });

        rl_restore_webdav.setOnClickListener(v -> {
            BackupManager.init(getContext());
            final LoadingDialog[] dialog = {new LoadingDialog(getContext(), getString(R.string.restore_loading))};
            Handler mHandler = new Handler(Looper.getMainLooper()) {
                @SuppressLint("CheckResult")
                @Override
                public void handleMessage(@NonNull Message msg) {
                    dialog[0].close();
                    if (msg.what == -1) {
                        //失败
                        ToastUtils.show(R.string.restore_back_failed);
                    } else if (msg.what == 2) {
                        ToastUtils.show(R.string.restore_back_success);
                        Tool.restartApp(getActivity());

                    } else {


                        BottomArea.list(getContext(), getString(R.string.select_cloud), (List<String>) msg.obj, (position, text) -> {
                            Task.onThread(() -> {
                                BackupManager.restoreFromWebDavByBackground(getContext(), mmkv.getString("webdav_url", ""), mmkv.getString("webdav_name", ""), mmkv.getString("webdav_password", ""), text, this);

                            });
                            dialog[0] = new LoadingDialog(getContext(), getString(R.string.restore_tip));
                            dialog[0].show();
                        });
                    }

                }
            };
            dialog[0].show();
            Task.onThread(() -> {
                BackupManager.restoreFromWebDav(getContext(), mmkv.getString("webdav_url", ""), mmkv.getString("webdav_name", ""), mmkv.getString("webdav_password", ""), mHandler);
            });
        });

    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    protected void initTitle() {
        title_bar.setInner(getActivity());
        title_bar.setLeftIconOnClickListener(v -> {
            popToBack();
        });
    }



}
