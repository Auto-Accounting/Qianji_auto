package cn.dreamn.qianji_auto.ui.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.afollestad.materialdialogs.list.DialogListExtKt;
import com.alibaba.fastjson.JSONArray;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.adapter.ListArrayAdapter;

public class BottomArea {
    //显示底部信息
    static public void msg(Context context, String title, String msg) {
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.cornerRadius(15f, null);
        dialog.title(null, title);
        dialog.message(null, msg, null);
        dialog.show();
    }

    //底部信息，带有开关
    static public void msg(Context context, String title, String msg, String rightName, String leftName, MsgCallback callback) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_msg, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.title(null, title);

        TextView textView_body = textEntryView.findViewById(R.id.textView_body);
        textView_body.setText(msg);


        Button button_next = textEntryView.findViewById(R.id.button_next);
        Button button_last = textEntryView.findViewById(R.id.button_last);
        button_next.setText(rightName);
        button_next.setOnClickListener(v -> {
            callback.sure();
            dialog.dismiss();
        });
        button_last.setText(leftName);
        button_last.setOnClickListener(v -> {
            callback.cancel();
            dialog.dismiss();
        });

        DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                false, true, false, false);

        dialog.cornerRadius(15f, null);
        dialog.show();
    }

    static public void input(Context context, String title, String msg, String rightName, String leftName, InputCallback inputCallback) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_input, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.title(null, title);

        TextInputEditText md_input_message = textEntryView.findViewById(R.id.md_input_message);

        md_input_message.setText(msg);

        Button button_next = textEntryView.findViewById(R.id.button_next);
        Button button_last = textEntryView.findViewById(R.id.button_last);
        button_next.setText(rightName);
        button_last.setText(leftName);
        button_next.setOnClickListener(v2 -> {
            inputCallback.input(md_input_message.getText().toString());
            dialog.dismiss();
        });

        button_last.setOnClickListener(v2 -> {
            inputCallback.cancel();
            dialog.dismiss();
        });

        DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                false, true, false, false);
        dialog.cornerRadius(15f, null);
        dialog.show();
    }

    @SuppressLint("CheckResult")
    static public void list(Context context, String title, List<String> list, ListCallback listCallback) {
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.cornerRadius(15f, null);
        dialog.title(null, title);
        DialogListExtKt.listItems(dialog, null, list, null, true, (materialDialog, index, text) -> {
            listCallback.onSelect(index);
            return null;
        });
        dialog.show();
    }

    @SuppressLint("CheckResult")
    static public void list(Context context, String title, List<String> list, ListCallback2 listCallback) {
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.cornerRadius(15f, null);
        dialog.title(null, title);
        DialogListExtKt.listItems(dialog, null, list, null, true, (materialDialog, index, text) -> {
            listCallback.onSelect(index, text.toString());
            return null;
        });
        dialog.show();
    }

    @SuppressLint("CheckResult")
    static public void listLong(Context context, String title, List<String> list, ListCallback listCallback) {
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        bottomSheet.setRatio(1f);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.cornerRadius(15f, null);
        dialog.title(null, title);
        DialogListExtKt.listItems(dialog, null, list, null, true, (materialDialog, index, text) -> {
            listCallback.onSelect(index);
            return null;
        });
        dialog.show();
    }

    static public void list(Context context, String title, JSONArray list, ListCallback listCallback) {

        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.include_list_data, null);
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
        MaterialDialog dialog = new MaterialDialog(context, bottomSheet);
        dialog.title(null, title);
        dialog.cornerRadius(15f, null);
        ListView listView = textEntryView.findViewById(R.id.list_view);
        ListArrayAdapter listAdapter = new ListArrayAdapter(context, R.layout.components_supertext, list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            listCallback.onSelect(position);
            dialog.dismiss();
        });


        DialogCustomViewExtKt.customView(dialog, null, textEntryView,
                false, true, false, false);
        dialog.cornerRadius(15f, null);
        dialog.show();
    }

    public interface MsgCallback {
        void cancel();

        void sure();
    }

    public interface InputCallback {
        void input(String data);

        void cancel();
    }

    public interface ListCallback {
        void onSelect(int position);
    }

    public interface ListCallback2 {
        void onSelect(int position, String text);
    }
    //
}
