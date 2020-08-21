// Generated code from Butter Knife. Do not modify!
package cn.dreamn.qianji_auto.fragment;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import cn.dreamn.qianji_auto.R;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LogFragment_ViewBinding implements Unbinder {
  private LogFragment target;

  @UiThread
  public LogFragment_ViewBinding(LogFragment target, View source) {
    this.target = target;

    target.map_layout = Utils.findRequiredViewAsType(source, R.id.map_layout, "field 'map_layout'", SwipeRefreshLayout.class);
    target.map_error = Utils.findRequiredViewAsType(source, R.id.map_error, "field 'map_error'", SuperTextView.class);
    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.recycler_view, "field 'recyclerView'", SwipeRecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    LogFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.map_layout = null;
    target.map_error = null;
    target.recyclerView = null;
  }
}
