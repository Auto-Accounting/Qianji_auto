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

package cn.dreamn.qianji_auto.ui.fragment;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.core.BaseFragment;


@Page(name = "状态显示页面")
public class StateFragment extends BaseFragment {
    @BindView(R.id.Empty)
    LinearLayout Empty;
    @BindView(R.id.Content)
    LinearLayout Content;
    @BindView(R.id.Loading)
    LinearLayout Loading;

    @BindView(R.id.imageView_Load)
    ImageView imageView_Load;

    @BindView(R.id.textView_empty)
    TextView textView_empty;
    @BindView(R.id.textView_load)
    TextView textView_load;
    /**
     * 布局的资源id
     *
     * @return
     */
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_state;
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initViews() {

    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    @Override
    protected TitleBar initTitle() {

        return super.initTitle();


    }





    @Override
    protected void initListeners() {

    }

    protected void showEmpty(String tips){
        disableAll();
        Empty.setVisibility(View.VISIBLE);
        textView_empty.setText(tips);
    }
    protected void showContent(){
        disableAll();
        Content.setVisibility(View.VISIBLE);
    }

    protected void showLoading(String tips){
        disableAll();
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.img_animation);
        LinearInterpolator lin = new LinearInterpolator();//设置动画匀速运动
        animation.setInterpolator(lin);
        imageView_Load.startAnimation(animation);
        Loading.setVisibility(View.VISIBLE);
        textView_load.setText(tips);
    }

    private void disableAll(){
        Empty.setVisibility(View.GONE);
        Loading.setVisibility(View.GONE);
        Content.setVisibility(View.GONE);
    }

}
