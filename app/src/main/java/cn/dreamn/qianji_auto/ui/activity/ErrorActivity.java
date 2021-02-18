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

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.xuexiang.xutil.XUtil;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.db.Helper.Caches;
import cn.dreamn.qianji_auto.core.utils.Tools;
import cn.dreamn.qianji_auto.utils.tools.Logs;

public class ErrorActivity extends AppCompatActivity {


    TextView exce_title;


    TextView exce_text;


    TextView exce_cancel;


    TextView exce_send;


    private String filePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception);
        initView();
        initListen();

    }

    //处理数据残留
    private void initData() {
        Caches.AddOrUpdate("float_lock", "false");
    }

    private void initView() {
        exce_title = findViewById(R.id.exce_title);
        exce_text = findViewById(R.id.exce_text);
        exce_cancel = findViewById(R.id.exce_cancel);
        exce_send = findViewById(R.id.exce_send);

        exce_title.setText("自动记账已崩溃");
        exce_text.setText(getString(R.string.err));
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            filePath = bundle.getString("fileName");
            //  exce_text.setText(filePath);
        }
    }

    private void initListen() {
        exce_cancel.setOnClickListener(view -> {
            XUtil.exitApp();
        });
        exce_send.setOnClickListener(view -> {
            Logs.d(filePath);
            // Uri uri = Uri.fromFile(new File(filePath));
            Tools.shareFile(this, filePath);
        });

    }
}