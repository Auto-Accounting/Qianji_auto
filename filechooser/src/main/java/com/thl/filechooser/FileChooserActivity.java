package com.thl.filechooser;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FileChooserActivity extends AppCompatActivity {

    private boolean showFile = true;
    private boolean showHideFile = true;
    public static FileChooser mFileChooser;
    private String mChoosenFilePath;

    private FileTourController tourController;
    private FileAdapter adapter;
    private CurrentFileAdapter currentFileAdapter;


    private RecyclerView fileRv;
    private int firstItemPosition = 0;
    private int lastItemPosition = 0;
    private HashMap<Integer, Integer> firstItemPositionMap;
    private HashMap<Integer, Integer> lastItemPositionMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        initListener();
    }

    private void initListener() {
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.rightText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRightText();
            }
        });

        this.showFile = getIntent().getBooleanExtra("showFile", true);
        this.showHideFile = getIntent().getBooleanExtra("showHideFile", true);
        this.mChoosenFilePath = getIntent().getStringExtra("currentPath");
        String title = getIntent().getStringExtra("title");
        String doneText = getIntent().getStringExtra("doneText");
        int backIconRes = getIntent().getIntExtra("backIconRes", -1);
        int chooseCount = getIntent().getIntExtra("chooseCount", 1);
        String chooseType = getIntent().getStringExtra("chooseType");
        int themeColorRes = getIntent().getIntExtra("themeColorRes", -1);

        tourController = new FileTourController(this, mChoosenFilePath, chooseType, showHideFile, showFile);
        tourController.setShowFile(this.showFile);
        tourController.setShowHideFile(this.showHideFile);
        ImageView back = (ImageView) findViewById(R.id.back);
        TextView tvTitle = (TextView) findViewById(R.id.title);
        TextView tvRightText = (TextView) findViewById(R.id.rightText);
        View bgView = (View) findViewById(R.id.bg_title);
        if (backIconRes != -1) {
            back.setImageResource(backIconRes);
        }
        tvTitle.setText(title);
        tvRightText.setText(doneText);
        if (themeColorRes != -1) {
            bgView.setBackgroundResource(themeColorRes);
        }
//        close.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_baseline_close_24));

        adapter = new FileAdapter(this, (ArrayList<FileInfo>) tourController.getCurrenFileInfoList(), R.layout.item_file, chooseType, chooseCount);
        fileRv = (RecyclerView) findViewById(R.id.fileRv);
        fileRv.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        fileRv.setAdapter(adapter);

        final RecyclerView currentPath = (RecyclerView) findViewById(R.id.currentPath);
        currentFileAdapter = new CurrentFileAdapter(this, (ArrayList<File>) tourController.getCurrentFolderList(), R.layout.item_current_file);
        currentPath.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        currentPath.setAdapter(currentFileAdapter);
        currentPath.scrollToPosition(tourController.getCurrentFolderList().size() - 1);
        firstItemPositionMap = new HashMap<>();
        lastItemPositionMap = new HashMap<>();

        fileRv.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (recyclerView.getLayoutManager() != null && layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    lastItemPosition = linearManager.findLastVisibleItemPosition();
                }
            }
        });
        adapter.setItemClickListener(new FileAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position, FileInfo data) {
                File selectFile = new File(tourController.getCurrenFileInfoList().get(position).getFilePath());
                if (selectFile.isDirectory()) {
                    ArrayList<FileInfo> childFileInfoList = (ArrayList<FileInfo>) tourController.addCurrentFile(selectFile);
                    adapter.setData(childFileInfoList);
                    adapter.notifyData();

                    currentFileAdapter.setData(tourController.getCurrentFolderList());
                    currentFileAdapter.notifyDataSetChanged();
                    int sign = tourController.getCurrentFolderList().size() - 1;
                    currentPath.scrollToPosition(sign);
                    firstItemPositionMap.put(sign, firstItemPosition);
                    lastItemPositionMap.put(sign, lastItemPosition);

                } else {
                    adapter.notifyClick(data, position);
                }
                Log.e("草你吗>>>", data.getFilePath());
                EventBus.getDefault().post(data);
            }
        });

        currentFileAdapter.setItemClickListener(new CommonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                List<FileInfo> fileInfoList = tourController.resetCurrentFile(position);
                adapter.setData(fileInfoList);
                adapter.notifyData();

                currentFileAdapter.setData(tourController.getCurrentFolderList());
                currentFileAdapter.notifyDataSetChanged();

                currentPath.scrollToPosition(tourController.getCurrentFolderList().size() - 1);
            }
        });

        findViewById(R.id.switchSdcard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ListPopupWindow listPopupWindow = new ListPopupWindow(FileChooserActivity.this);
                listPopupWindow.setAnchorView(v);
                ArrayList<String> sdcardList = new ArrayList<>();
                sdcardList.add("手机存储");
                if (FileTourController.getStoragePath(FileChooserActivity.this, true) != null)
                    sdcardList.add("SD卡");

                SdCardAdapter sdCardAdapter = new SdCardAdapter(FileChooserActivity.this, sdcardList);
                listPopupWindow.setAdapter(sdCardAdapter);
                listPopupWindow.setWidth(sdCardAdapter.getItemViewWidth());
                listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        firstItemPositionMap.clear();
                        lastItemPositionMap.clear();
                        firstItemPosition = 0;
                        lastItemPosition = 0;
                        if (tourController != null)
                            tourController.switchSdCard(position);
                        if (adapter != null) {
                            adapter.setData(tourController.getCurrenFileInfoList());
                            adapter.notifyDataSetChanged();
                        }
                        if (currentFileAdapter != null) {
                            currentFileAdapter.setData(tourController.getCurrentFolderList());
                            currentFileAdapter.notifyDataSetChanged();
                        }
                        listPopupWindow.dismiss();
                    }
                });
                listPopupWindow.show();

            }
        });
    }

    public void clickRightText() {
        if (adapter != null && adapter.getSign() <= 0) {
            Toast.makeText(this, "请选择文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tourController != null) {
            mChoosenFilePath = tourController.getCurrentFile().getAbsolutePath();
        }
        if (mFileChooser != null) {
            mFileChooser.finish(adapter.getChooseFilePath());
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFileChooser = null;
    }

    @Override
    public void onBackPressed() {
        if (!tourController.isRootFile()) {
            List<FileInfo> currentList = tourController.backToParent();
            adapter.setData(currentList);
            adapter.notifyDataSetChanged();
            currentFileAdapter.setData(tourController.getCurrentFolderList());
            currentFileAdapter.notifyDataSetChanged();

            int sign = tourController.getCurrentFolderList().size();
            Integer firstposition = firstItemPositionMap.get(sign);
            int first = firstposition == null ? 0 : firstposition;

            Integer lastItemPosition = lastItemPositionMap.get(sign);
            int last = lastItemPosition == null ? 0 : lastItemPosition;

            int rectification = dp2px(15); //纠偏
            if (fileRv.getLayoutManager() != null) {
                ((LinearLayoutManager) fileRv.getLayoutManager()).scrollToPositionWithOffset(first, last);
            }

        } else {
            super.onBackPressed();
        }
    }

    public int dp2px(float dpValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
