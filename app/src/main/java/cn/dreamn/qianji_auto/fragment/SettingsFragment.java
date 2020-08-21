package cn.dreamn.qianji_auto.fragment;


import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.core.BaseFragment;
import cn.dreamn.qianji_auto.func.Log;
import cn.dreamn.qianji_auto.func.Storage;
import cn.dreamn.qianji_auto.utils.XToastUtils;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;


@Page(name = "插件设置")
public class SettingsFragment extends BaseFragment implements SuperTextView.OnSuperTextViewClickListener {

    @BindView(R.id.set_pay)
    SuperTextView set_pay;
    @BindView(R.id.set_earn)
    SuperTextView set_earn;
    @BindView(R.id.set_bookname)
    SuperTextView set_bookname;
    @BindView(R.id.set_alipay_qr)
    SuperTextView set_alipay_qr;
    @BindView(R.id.set_alipay_friend)
    SuperTextView set_alipay_friend;
    @BindView(R.id.set_alipay_refund)
    SuperTextView set_alipay_refund;
    @BindView(R.id.set_alipay_money)
    SuperTextView set_alipay_money;
    @BindView(R.id.set_alipay_ant)
    SuperTextView set_alipay_ant;
    @BindView(R.id.set_remark)
    SuperTextView set_remark;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initViews() {
        set_pay.setOnSuperTextViewClickListener(this);
        set_pay.setSwitchCheckedChangeListener((buttonView, isChecked)->{
            Storage.type(Storage.Set).set("set_pay",isChecked);
        });
        set_earn.setOnSuperTextViewClickListener(this);
        set_earn.setSwitchCheckedChangeListener((buttonView, isChecked)->{
            Storage.type(Storage.Set).set("set_earn",isChecked);
        });
        set_bookname.setOnSuperTextViewClickListener(this);
        set_alipay_qr.setOnSuperTextViewClickListener(this);
        set_alipay_friend.setOnSuperTextViewClickListener(this);
        set_alipay_refund.setOnSuperTextViewClickListener(this);
        set_remark.setOnSuperTextViewClickListener(this);
        setData();

    }

    private void setData(){
        set_pay.setSwitchIsChecked(Storage.type(Storage.Set).getBoolean("set_pay",false));
        set_earn.setSwitchIsChecked(Storage.type(Storage.Set).getBoolean("set_earn",false));
        set_bookname.setCenterTopString(Storage.type(Storage.Set).get("set_bookname","默认账本"));
        set_alipay_qr.setRightString(Storage.type(Storage.Set).get("set_alipay_qr","支付宝"));
        set_alipay_friend.setRightString(Storage.type(Storage.Set).get("set_alipay_friend","支付宝"));
        set_alipay_refund.setRightString(Storage.type(Storage.Set).get("set_alipay_refund","支付宝"));
        set_alipay_ant.setRightString(Storage.type(Storage.Set).get("set_alipay_ant","支付宝"));
        set_alipay_money.setRightString(Storage.type(Storage.Set).get("set_alipay_money","支付宝"));
        set_remark.setCenterTopString(Storage.type(Storage.Set).get("set_remark","[交易对象] - [说明]"));
    }

    @SingleClick
    @Override
    public void onClick(SuperTextView superTextView) {
        switch(superTextView.getId()) {
            case R.id.set_pay:
                boolean check=!set_pay.getSwitchIsChecked();
                set_pay.setSwitchIsChecked(check);
                Storage.type(Storage.Set).set("set_pay",check);
                break;
            case R.id.set_earn:
                boolean check2=!set_earn.getSwitchIsChecked();
                set_earn.setSwitchIsChecked(check2);
                Storage.type(Storage.Set).set("set_earn",check2);
                break;
            case R.id.set_bookname:
                showInputDialog("账本","请输入需要记账的账本名称",Storage.type(Storage.Set).get("set_bookname","默认账本"),(str)->{
                    Storage.type(Storage.Set).set("set_bookname",str);
                    XToastUtils.success("设置成功，当前账本为："+str);
                    set_bookname.setCenterTopString(Storage.type(Storage.Set).get("set_bookname","默认账本"));
                });

                break;
            case R.id.set_alipay_qr:
                showInputDialog("支付宝 - 二维码收款","请选择分类",Storage.type(Storage.Set).get("set_alipay_qr","支付宝"),(str)->{
                    Storage.type(Storage.Set).set("set_alipay_qr",str);
                    XToastUtils.success("设置成功，支付宝 - 二维码收款分类为："+str);
                    set_alipay_qr.setRightString(Storage.type(Storage.Set).get("set_alipay_qr","支付宝"));
                });
                break;
            case R.id.set_alipay_friend:
                showInputDialog("支付宝 - 转账收款","请选择分类",Storage.type(Storage.Set).get("set_alipay_friend","支付宝"),(str)->{
                    Storage.type(Storage.Set).set("set_alipay_friend",str);
                    XToastUtils.success("设置成功，支付宝 - 转账收款分类为："+str);
                    set_alipay_friend.setRightString(Storage.type(Storage.Set).get("set_alipay_friend","支付宝"));
                });
                break;
            case R.id.set_alipay_ant:
                showInputDialog("支付宝 - 余额宝收益","请选择分类",Storage.type(Storage.Set).get("set_alipay_ant","支付宝"),(str)->{
                    Storage.type(Storage.Set).set("set_alipay_ant",str);
                    XToastUtils.success("设置成功，支付宝 - 余额宝收益分类为："+str);
                    set_alipay_ant.setRightString(Storage.type(Storage.Set).get("set_alipay_ant","支付宝"));
                });
                break;
            case R.id.set_alipay_money:
                showInputDialog("支付宝 - 余利宝收益","请选择分类",Storage.type(Storage.Set).get("set_alipay_money","支付宝"),(str)->{
                    Storage.type(Storage.Set).set("set_alipay_money",str);
                    XToastUtils.success("设置成功，支付宝 - 余利宝收益分类为："+str);
                    set_alipay_money.setRightString(Storage.type(Storage.Set).get("set_alipay_money","支付宝"));
                });
                break;
            case R.id.set_alipay_refund:
                showInputDialog("支付宝 - 退款到账","请选择分类",Storage.type(Storage.Set).get("set_alipay_refund","支付宝"),(str)->{
                    Storage.type(Storage.Set).set("set_alipay_refund",str);
                    XToastUtils.success("设置成功，支付宝 - 退款到账分类为："+str);
                    set_alipay_refund.setRightString(Storage.type(Storage.Set).get("set_alipay_refund","支付宝"));
                });
                break;
            case R.id.set_remark:
                showInputDialog("自动记账备注格式","支持：[交易对象] 、 [说明]",Storage.type(Storage.Set).get("set_remark","[交易对象] - [说明]"),(str)->{
                    Storage.type(Storage.Set).set("set_remark",str);
                    XToastUtils.success("设置成功，自动记账备注格式为："+str);

                    set_remark.setCenterTopString(Storage.type(Storage.Set).get("set_remark","[交易对象] - [说明]"));
                });
                break;
            default:
                break;
        }
    }

    // 回调接口
    interface CallBack {
        void onResponse(String data);
    }
    private void showInputDialog(String title,String tip,String def,CallBack callBack) {
        new MaterialDialog.Builder(getContext())
                .title(title)
                .content(tip)
                .input(
                        "请输入内容",
                        def,
                        false,
                        ((dialog, input) -> {}))
                .positiveText("确认")
                .onPositive((dialog, which) -> callBack.onResponse(dialog.getInputEditText().getText().toString()))
                .cancelable(false)
                .show();
    }
}
