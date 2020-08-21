// Generated code from Butter Knife. Do not modify!
package cn.dreamn.qianji_auto.fragment;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import cn.dreamn.qianji_auto.R;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class StateFragment_ViewBinding implements Unbinder {
  private StateFragment target;

  @UiThread
  public StateFragment_ViewBinding(StateFragment target, View source) {
    this.target = target;

    target.menu_active = Utils.findRequiredViewAsType(source, R.id.menu_active, "field 'menu_active'", SuperTextView.class);
    target.menu_desc = Utils.findRequiredViewAsType(source, R.id.menu_desc, "field 'menu_desc'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    StateFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.menu_active = null;
    target.menu_desc = null;
  }
}
