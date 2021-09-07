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

package cn.dreamn.qianji_auto.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseActivity;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class ErrorActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        setContentView(R.layout.activty_error_msg);
        initView();
    }

    private void initView() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            Log.i("Error", bundle.getString("errorInfo"));//记录错误日志


            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.include_list_msg, null);
            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            bottomSheet.setRatio(1f);
            MaterialDialog dialog = new MaterialDialog(this, bottomSheet);
            dialog.title(null, getString(R.string.error_title));

            TextView textView_body = textEntryView.findViewById(R.id.textView_body);
            textView_body.setText(bundle.getString("errorInfo"));


            Button button_next = textEntryView.findViewById(R.id.button_next);
            Button button_last = textEntryView.findViewById(R.id.button_last);
            button_next.setText(R.string.error_submit);
            button_last.setText(R.string.error_restart);

            button_next.setOnClickListener(v -> {
                //  Tool.goUrl(context, jsonObject.getString("download"));
                Activity context = this;
                dialog.dismiss();
                BottomArea.msg(context, getString(R.string.error_report_title), getString(R.string.error_report_msg), getString(R.string.error_report_submit), getString(R.string.error_report_cancle), new BottomArea.MsgCallback() {
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void sure() {
                        Tool.goUrl(context, getString(R.string.github_issue_error));
                        Tool.restartApp(context);
                    }
                });

            });

            button_last.setOnClickListener(v -> {

                dialog.dismiss();
                Tool.restartApp(this);
            });

            DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                    false, true, false, false);

            dialog.cornerRadius(15f, null);
            dialog.cancelable(false);
            dialog.show();

        }
    }


}