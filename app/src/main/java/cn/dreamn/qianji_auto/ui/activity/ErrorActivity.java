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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hjq.toast.Toaster;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseActivity;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;

public class ErrorActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_error_msg);
        initView();
    }

    private void initView() {

        Bundle bundle = getIntent().getExtras();
        Activity context = this;
        if (bundle != null) {
            String errorInfo = bundle.getString("errorInfo");
            Log.i("Error", errorInfo);//记录错误日志

            TextView textView = findViewById(R.id.textView_body2);
            textView.setText(bundle.getString("errorInfo"));

            Button button_last = findViewById(R.id.button_last);
            Button button_next = findViewById(R.id.button_next);
            Activity activity = this;
            button_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BottomArea.msg(context, getString(R.string.error_report_title), getString(R.string.error_report_msg), getString(R.string.error_report_submit), getString(R.string.error_report_cancle), new BottomArea.MsgCallback() {
                        @Override
                        public void cancel() {

                        }

                        @Override
                        public void sure() {
                            Tool.clipboard(activity, errorInfo);
                            Toaster.show(R.string.copied);
                            Tool.goUrl(context, getString(R.string.github_issue_error));
                            // Tool.restartApp(context);
                        }
                    });
                }
            });

            button_last.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Tool.restartApp(context);
                }
            });

        } else {
            Tool.restartApp(context);
        }
    }


}