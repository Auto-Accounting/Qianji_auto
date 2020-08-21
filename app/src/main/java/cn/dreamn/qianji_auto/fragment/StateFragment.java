package cn.dreamn.qianji_auto.fragment;


import android.widget.TextView;

import cn.dreamn.qianji_auto.R;

import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.func.Log;
import cn.dreamn.qianji_auto.func.model;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.enums.CoreAnim;

import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import butterknife.BindView;

/**
 * 状态信息
 *
 * @author xuexiang
 * @since 2019-10-30 00:15
 */
@Page(anim = CoreAnim.none)
public class StateFragment extends BaseFragment{
    @BindView(R.id.menu_active)
    SuperTextView menu_active;
    @BindView(R.id.menu_desc)
    TextView menu_desc;

    /**
     * @return 返回为 null意为不需要导航栏
     */
    @Override
    protected TitleBar initTitle() {
        return null;
    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_state;
    }

    @Override
    protected void initViews() {
        boolean isActive=model.isInstallFarmeWork(getActivity())&&model.isActvive(getActivity());
        if(isActive){
            Log.i("System","插件已激活");
            menu_active.setCenterString("插件已激活");
            menu_active.setCenterTextColor(getResources().getColor(R.color.state_succ));
            menu_active.setBackgroundColor(getResources().getColor(R.color.state_bg));


            menu_desc.setBackgroundColor(getResources().getColor(R.color.state_bg));
            if(model.farmework.equals("taichi"))
                menu_desc.setText("如果太极用户出现激活后插件未生效问题，请检查：\n 1. 版本是否支持。 \n 2. 重新勾选模块，并强制停止支付宝。");
            else
                menu_desc.setText("插件已激活，欢迎使用。\n如有问题，请加群提问并提供对应的日志。");
        }else{
            Log.i("System","插件未激活");
            menu_active.setCenterString("插件未激活");
            menu_active.setCenterTextColor(getResources().getColor(R.color.state_err));
            menu_active.setBackgroundColor(getResources().getColor(R.color.state_warn));

            menu_desc.setBackgroundColor(getResources().getColor(R.color.state_bg));
            menu_desc.setText("1. 本插件依赖于Xposed框架/Edxposed框架/太极阳。请前往对应的App勾选激活以使用。\n\n2. 其他非指定Xposed框架理论上也支持，如果显示未激活，功能正常就不必管他。\n\n3.如果确定已经正确操作但仍然未激活请加群并提供对应框架的日志。");
        }
    }



}
