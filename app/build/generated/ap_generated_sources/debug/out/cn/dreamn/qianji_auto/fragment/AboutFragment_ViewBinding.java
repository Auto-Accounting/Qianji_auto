// Generated code from Butter Knife. Do not modify!
package cn.dreamn.qianji_auto.fragment;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import cn.dreamn.qianji_auto.R;
import com.xuexiang.xui.widget.grouplist.XUIGroupListView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AboutFragment_ViewBinding implements Unbinder {
  private AboutFragment target;

  @UiThread
  public AboutFragment_ViewBinding(AboutFragment target, View source) {
    this.target = target;

    target.mVersionTextView = Utils.findRequiredViewAsType(source, R.id.version, "field 'mVersionTextView'", TextView.class);
    target.descTextView = Utils.findRequiredViewAsType(source, R.id.desc, "field 'descTextView'", TextView.class);
    target.mAboutGroupListView = Utils.findRequiredViewAsType(source, R.id.about_list, "field 'mAboutGroupListView'", XUIGroupListView.class);
    target.mCopyrightTextView = Utils.findRequiredViewAsType(source, R.id.copyright, "field 'mCopyrightTextView'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AboutFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mVersionTextView = null;
    target.descTextView = null;
    target.mAboutGroupListView = null;
    target.mCopyrightTextView = null;
  }
}
