// Generated code from Butter Knife. Do not modify!
package cn.dreamn.qianji_auto.fragment;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import cn.dreamn.qianji_auto.R;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SettingsFragment_ViewBinding implements Unbinder {
  private SettingsFragment target;

  @UiThread
  public SettingsFragment_ViewBinding(SettingsFragment target, View source) {
    this.target = target;

    target.set_pay = Utils.findRequiredViewAsType(source, R.id.set_pay, "field 'set_pay'", SuperTextView.class);
    target.set_earn = Utils.findRequiredViewAsType(source, R.id.set_earn, "field 'set_earn'", SuperTextView.class);
    target.set_bookname = Utils.findRequiredViewAsType(source, R.id.set_bookname, "field 'set_bookname'", SuperTextView.class);
    target.set_alipay_qr = Utils.findRequiredViewAsType(source, R.id.set_alipay_qr, "field 'set_alipay_qr'", SuperTextView.class);
    target.set_alipay_friend = Utils.findRequiredViewAsType(source, R.id.set_alipay_friend, "field 'set_alipay_friend'", SuperTextView.class);
    target.set_alipay_refund = Utils.findRequiredViewAsType(source, R.id.set_alipay_refund, "field 'set_alipay_refund'", SuperTextView.class);
    target.set_remark = Utils.findRequiredViewAsType(source, R.id.set_remark, "field 'set_remark'", SuperTextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SettingsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.set_pay = null;
    target.set_earn = null;
    target.set_bookname = null;
    target.set_alipay_qr = null;
    target.set_alipay_friend = null;
    target.set_alipay_refund = null;
    target.set_remark = null;
  }
}
