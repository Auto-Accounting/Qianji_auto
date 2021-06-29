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
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.utils.AutoBillWeb;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class ErrorActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_err_msg);
        initView();

    }

    private void initView() {

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            LayoutInflater factory = LayoutInflater.from(this);
            final View textEntryView = factory.inflate(R.layout.list_msg, null);
            BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
            MaterialDialog dialog = new MaterialDialog(this, bottomSheet);
            dialog.title(null, "自动记账发生异常 ");

            TextView textView_body = textEntryView.findViewById(R.id.textView_body);
            textView_body.setText(bundle.getString("errorInfo"));


            Button button_next = textEntryView.findViewById(R.id.button_next);
            Button button_last = textEntryView.findViewById(R.id.button_last);
            button_next.setText("发送错误报告");
            button_last.setText("重启自动记账");

            button_next.setOnClickListener(v -> {
                //  Tool.goUrl(context, jsonObject.getString("download"));
                Activity context = this;
                Handler mHandler = new Handler(getMainLooper()) {
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        Tool.restartApp(context);
                    }
                };
                dialog.dismiss();
                AutoBillWeb.sendLog(bundle.getString("errorInfo"), new AutoBillWeb.WebCallback() {
                    @Override
                    public void onFailure() {

                    }

                    @Override
                    public void onSuccessful(String data) {
                        mHandler.sendEmptyMessage(0);
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