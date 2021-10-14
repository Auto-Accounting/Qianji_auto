package net.ankio.timepicker.view;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;

import net.ankio.timepicker.R;
import net.ankio.timepicker.configure.PickerOptions;
import net.ankio.timepicker.listener.OnDismissListener;

/**
 * Created by Sai on 15/11/22.
 * 精仿iOSPickerViewController控件
 */
public class BasePickerView {

    private final Context context;
    private final boolean isAnim = true;
    protected ViewGroup contentContainer;
    protected ViewGroup dialogView;//附加Dialog 的 根View
    protected PickerOptions mPickerOptions;
    protected View clickView;//是通过哪个View弹出的
    protected MaterialDialog dialog = null;
    private final View.OnKeyListener onKeyBackListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == MotionEvent.ACTION_DOWN && isShowing()) {
                dismiss();
                return true;
            }
            return false;
        }
    };
    private OnDismissListener onDismissListener;


    public BasePickerView(Context context) {
        this.context = context;
    }

    protected void initViews(boolean isFloat) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        // bottomSheet.setRatio(0.6f);
        dialog = new MaterialDialog(context, bottomSheet);
        dialogView = (ViewGroup) layoutInflater.inflate(R.layout.layout_basepickerview, null, false);
        contentContainer = dialogView.findViewById(R.id.content_container);
        dialog.cancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (onDismissListener != null) {
                    onDismissListener.onDismiss(BasePickerView.this);
                }
            }
        });
        DialogCustomViewExtKt.customView(dialog, null, dialogView, false, true, false, false);
        dialog.cornerRadius(15f, null);
        if (isFloat) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
            } else {
                dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
            }
        }


    }

    /**
     * 添加View到根视图
     */
    public void show() {
        showDialog();
    }

    /**
     * 检测该View是不是已经添加到根视图
     *
     * @return 如果视图已经存在该View返回true
     */
    public boolean isShowing() {
        return false;

    }

    public void dismiss() {
        dismissDialog();


    }

    public BasePickerView setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }

    public View findViewById(int id) {
        return contentContainer.findViewById(id);
    }

    private void showDialog() {
        if (dialog != null) {
            dialog.show();
        }
    }

    private void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


}
