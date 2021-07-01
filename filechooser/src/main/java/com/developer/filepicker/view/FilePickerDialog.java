package com.developer.filepicker.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.ContextThemeWrapper;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.developer.filepicker.R;
import com.developer.filepicker.controller.DialogSelectionListener;
import com.developer.filepicker.controller.NotifyItemChecked;
import com.developer.filepicker.controller.adapters.FileListAdapter;
import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.model.FileListItem;
import com.developer.filepicker.model.MarkedItemList;
import com.developer.filepicker.utils.ExtensionFilter;
import com.developer.filepicker.utils.Utility;
import com.developer.filepicker.widget.MaterialCheckbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author akshay sunil masram
 */
public class FilePickerDialog implements AdapterView.OnItemClickListener {

    private final Context context;
    private ListView listView;
    private TextView dname, dir_path, title;
    private DialogProperties properties;
    private DialogSelectionListener callbacks;
    private ArrayList<FileListItem> internalList;
    private ExtensionFilter filter;
    private FileListAdapter mFileListAdapter;
    private Button select;
    private String titleStr = null;
    private String positiveBtnNameStr = null;
    private String negativeBtnNameStr = null;

    private MaterialDialog dialog = null;

    public FilePickerDialog(Context context) {
        this.context = context;
        properties = new DialogProperties();
        filter = new ExtensionFilter(properties);
        internalList = new ArrayList<>();
        initViews();
    }


    public FilePickerDialog(Context context, DialogProperties properties, int themeResId) {
        this.context = new ContextThemeWrapper(context, themeResId);
        this.properties = properties;
        filter = new ExtensionFilter(properties);
        internalList = new ArrayList<>();
        initViews();
    }

    public FilePickerDialog(Context context, DialogProperties properties) {
        this.context = context;
        this.properties = properties;
        filter = new ExtensionFilter(properties);
        internalList = new ArrayList<>();
        initViews();
    }


    private void initViews() {
        BottomSheet bottomSheet = new BottomSheet(LayoutMode.MATCH_PARENT);
        bottomSheet.setRatio(1f);
        dialog = new MaterialDialog(context, bottomSheet);
        //   bottomSheet.getDialogLayout(dialog.getView()).setButtonsLayout();
        dialog.cornerRadius(15f, null);
        LayoutInflater factory = LayoutInflater.from(context);
        final View dialog_main = factory.inflate(R.layout.dialog_main, null);
        DialogCustomViewExtKt.customView(dialog, null, dialog_main, false, true, false, false);
        listView = dialog_main.findViewById(R.id.fileList);
        select = dialog_main.findViewById(R.id.select);

        int size = MarkedItemList.getFileCount();
        if (size == 0) {
            select.setEnabled(false);
            int color;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                color = context.getResources().getColor(R.color.colorAccent, context.getTheme());
            } else {
                color = context.getResources().getColor(R.color.colorAccent);
            }
            select.setTextColor(Color.argb(128, Color.red(color), Color.green(color),
                    Color.blue(color)));
        }
        dname = dialog_main.findViewById(R.id.dname);
        title = dialog_main.findViewById(R.id.title);
        dir_path = dialog_main.findViewById(R.id.dir_path);
        Button cancel = dialog_main.findViewById(R.id.cancel);
        if (negativeBtnNameStr != null) {
            cancel.setText(negativeBtnNameStr);
        }
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] paths = MarkedItemList.getSelectedPaths();
                if (callbacks != null) {
                    callbacks.onSelectedFilePaths(paths);
                }
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        mFileListAdapter = new FileListAdapter(internalList, context, properties);
        mFileListAdapter.setNotifyItemCheckedListener(new NotifyItemChecked() {
            @Override
            public void notifyCheckBoxIsClicked() {
                positiveBtnNameStr = positiveBtnNameStr == null ?
                        context.getResources().getString(R.string.choose_button_label) : positiveBtnNameStr;
                int size = MarkedItemList.getFileCount();
                if (size == 0) {
                    select.setEnabled(false);
                    int color;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        color = context.getResources().getColor(R.color.colorAccent,
                                context.getTheme());
                    } else {
                        color = context.getResources().getColor(R.color.colorAccent);
                    }
                    select.setTextColor(Color.argb(128, Color.red(color), Color.green(color),
                            Color.blue(color)));
                    select.setText(positiveBtnNameStr);
                } else {
                    select.setEnabled(true);
                    int color;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        color = context.getResources().getColor(R.color.colorAccent,
                                context.getTheme());
                    } else {
                        color = context.getResources().getColor(R.color.colorAccent);
                    }
                    select.setTextColor(color);
                    String button_label = positiveBtnNameStr + " (" + size + ") ";
                    select.setText(button_label);
                }
                if (properties.selection_mode == DialogConfigs.SINGLE_MODE) {
                    /*  If a single file has to be selected, clear the previously checked
                     *  checkbox from the list.
                     */
                    mFileListAdapter.notifyDataSetChanged();
                }
            }
        });
        listView.setAdapter(mFileListAdapter);
        setTitle();
        dialog.cancelable(false);
        dialog.cornerRadius(15f, null);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    //Log.i("filechooser", "返回键响应");
                    onBackPressed();
                }
                return false;
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                onStart();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dismiss();
            }
        });
    }

    private void setTitle() {
        if (title == null || dname == null) {
            return;
        }
        if (titleStr != null) {
            if (title.getVisibility() == View.INVISIBLE) {
                title.setVisibility(View.VISIBLE);
            }
            title.setText(titleStr);
            if (dname.getVisibility() == View.VISIBLE) {
                dname.setVisibility(View.INVISIBLE);
            }
        } else {
            if (title.getVisibility() == View.VISIBLE) {
                title.setVisibility(View.INVISIBLE);
            }
            if (dname.getVisibility() == View.INVISIBLE) {
                dname.setVisibility(View.VISIBLE);
            }
        }
    }


    protected void onStart() {

        positiveBtnNameStr = (
                positiveBtnNameStr == null ?
                        context.getResources().getString(R.string.choose_button_label) :
                        positiveBtnNameStr
        );
        select.setText(positiveBtnNameStr);
        File currLoc;
        internalList.clear();
        FileListItem parent = null;
        if (properties.offset.isDirectory() && validateOffsetPath()) {
            parent = new FileListItem();
            currLoc = new File(properties.offset.getAbsolutePath());
            parent.setFilename(context.getString(R.string.label_parent_dir));
            parent.setDirectory(true);
            parent.setLocation(Objects.requireNonNull(currLoc.getParentFile())
                    .getAbsolutePath());
            parent.setTime(currLoc.lastModified());
        } else if (properties.root.exists() && properties.root.isDirectory()) {
            currLoc = new File(properties.root.getAbsolutePath());
        } else {
            currLoc = new File(properties.error_dir.getAbsolutePath());
        }
        dname.setText(currLoc.getName());
        dir_path.setText(currLoc.getAbsolutePath());
        setTitle();
        internalList = Utility.prepareFileListEntries(internalList, currLoc, filter,
                properties.show_hidden_files);

        if (parent != null) {
            internalList.add(0, parent);
        }

        mFileListAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(this);
    }

    private boolean validateOffsetPath() {
        String offset_path = properties.offset.getAbsolutePath();
        String root_path = properties.root.getAbsolutePath();
        return !offset_path.equals(root_path) && offset_path.contains(root_path);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (internalList.size() > i) {
            FileListItem fitem = internalList.get(i);
            if (fitem.isDirectory()) {
                if (new File(fitem.getLocation()).canRead()) {
                    File currLoc = new File(fitem.getLocation());
                    dname.setText(currLoc.getName());
                    setTitle();
                    dir_path.setText(currLoc.getAbsolutePath());
                    internalList.clear();
                    FileListItem parent = null;
                    if (!currLoc.getName().equals(properties.root.getName())) {
                        parent = new FileListItem();
                        parent.setFilename(context.getString(R.string.label_parent_dir));
                        parent.setDirectory(true);
                        parent.setLocation(Objects.requireNonNull(currLoc
                                .getParentFile()).getAbsolutePath());
                        parent.setTime(currLoc.lastModified());
                    }
                    internalList = Utility.prepareFileListEntries(internalList, currLoc, filter,
                            properties.show_hidden_files);

                    if (parent != null) {
                        internalList.add(0, parent);
                    }

                    mFileListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, R.string.error_dir_access,
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                MaterialCheckbox fmark = view.findViewById(R.id.file_mark);
                fmark.performClick();
            }
        }
    }

    public DialogProperties getProperties() {
        return properties;
    }

    public void setProperties(DialogProperties properties) {
        this.properties = properties;
        filter = new ExtensionFilter(properties);
    }

    public void setDialogSelectionListener(DialogSelectionListener callbacks) {
        this.callbacks = callbacks;
    }


    public void setTitle(CharSequence titleStr) {
        if (titleStr != null) {
            this.titleStr = titleStr.toString();
        } else {
            this.titleStr = null;
        }
        setTitle();
    }

    public void setPositiveBtnName(CharSequence positiveBtnNameStr) {
        if (positiveBtnNameStr != null) {
            this.positiveBtnNameStr = positiveBtnNameStr.toString();
        } else {
            this.positiveBtnNameStr = null;
        }
    }

    public void setNegativeBtnName(CharSequence negativeBtnNameStr) {
        if (negativeBtnNameStr != null) {
            this.negativeBtnNameStr = negativeBtnNameStr.toString();
        } else {
            this.negativeBtnNameStr = null;
        }
    }

    public void markFiles(List<String> paths) {
        if (paths != null && paths.size() > 0) {
            if (properties.selection_mode == DialogConfigs.SINGLE_MODE) {
                File temp = new File(paths.get(0));
                switch (properties.selection_type) {
                    case DialogConfigs.DIR_SELECT:
                        if (temp.exists() && temp.isDirectory()) {
                            FileListItem item = new FileListItem();
                            item.setFilename(temp.getName());
                            item.setDirectory(temp.isDirectory());
                            item.setMarked(true);
                            item.setTime(temp.lastModified());
                            item.setLocation(temp.getAbsolutePath());
                            MarkedItemList.addSelectedItem(item);
                        }
                        break;

                    case DialogConfigs.FILE_SELECT:
                        if (temp.exists() && temp.isFile()) {
                            FileListItem item = new FileListItem();
                            item.setFilename(temp.getName());
                            item.setDirectory(temp.isDirectory());
                            item.setMarked(true);
                            item.setTime(temp.lastModified());
                            item.setLocation(temp.getAbsolutePath());
                            MarkedItemList.addSelectedItem(item);
                        }
                        break;

                    case DialogConfigs.FILE_AND_DIR_SELECT:
                        if (temp.exists()) {
                            FileListItem item = new FileListItem();
                            item.setFilename(temp.getName());
                            item.setDirectory(temp.isDirectory());
                            item.setMarked(true);
                            item.setTime(temp.lastModified());
                            item.setLocation(temp.getAbsolutePath());
                            MarkedItemList.addSelectedItem(item);
                        }
                        break;
                }
            } else {
                for (String path : paths) {
                    switch (properties.selection_type) {
                        case DialogConfigs.DIR_SELECT:
                            File temp = new File(path);
                            if (temp.exists() && temp.isDirectory()) {
                                FileListItem item = new FileListItem();
                                item.setFilename(temp.getName());
                                item.setDirectory(temp.isDirectory());
                                item.setMarked(true);
                                item.setTime(temp.lastModified());
                                item.setLocation(temp.getAbsolutePath());
                                MarkedItemList.addSelectedItem(item);
                            }
                            break;

                        case DialogConfigs.FILE_SELECT:
                            temp = new File(path);
                            if (temp.exists() && temp.isFile()) {
                                FileListItem item = new FileListItem();
                                item.setFilename(temp.getName());
                                item.setDirectory(temp.isDirectory());
                                item.setMarked(true);
                                item.setTime(temp.lastModified());
                                item.setLocation(temp.getAbsolutePath());
                                MarkedItemList.addSelectedItem(item);
                            }
                            break;

                        case DialogConfigs.FILE_AND_DIR_SELECT:
                            temp = new File(path);
                            if (temp.exists() && (temp.isFile() || temp.isDirectory())) {
                                FileListItem item = new FileListItem();
                                item.setFilename(temp.getName());
                                item.setDirectory(temp.isDirectory());
                                item.setMarked(true);
                                item.setTime(temp.lastModified());
                                item.setLocation(temp.getAbsolutePath());
                                MarkedItemList.addSelectedItem(item);
                            }
                            break;
                    }
                }
            }
        }
    }


    public void show() {
        dialog.show();
        positiveBtnNameStr = positiveBtnNameStr == null ?
                context.getResources().getString(R.string.choose_button_label) : positiveBtnNameStr;
        select.setText(positiveBtnNameStr);
        int size = MarkedItemList.getFileCount();
        if (size == 0) {
            select.setText(positiveBtnNameStr);
        } else {
            String button_label = positiveBtnNameStr + " (" + size + ") ";
            select.setText(button_label);
        }
    }

    public void onBackPressed() {
        String currentDirName = dname.getText().toString();
        if (internalList.size() > 0) {
            FileListItem fitem = internalList.get(0);
            File currLoc = new File(fitem.getLocation());
            if (currentDirName.equals(properties.root.getName()) ||
                    !currLoc.canRead()) {
                dialog.onBackPressed();
            } else {
                dname.setText(currLoc.getName());
                dir_path.setText(currLoc.getAbsolutePath());
                internalList.clear();
                FileListItem parent = null;
                if (!currLoc.getName().equals(properties.root.getName())) {
                    parent = new FileListItem();
                    parent.setFilename(context.getString(R.string.label_parent_dir));
                    parent.setDirectory(true);
                    parent.setLocation(Objects.requireNonNull(currLoc.getParentFile())
                            .getAbsolutePath());
                    parent.setTime(currLoc.lastModified());
                }
                internalList = Utility.prepareFileListEntries(internalList, currLoc, filter,
                        properties.show_hidden_files);
                if (parent != null) {
                    internalList.add(0, parent);
                }
                mFileListAdapter.notifyDataSetChanged();
            }
            setTitle();
        } else {
            dialog.onBackPressed();
        }
    }


    public void dismiss() {
        MarkedItemList.clearSelectionList();
        internalList.clear();
    }
}
