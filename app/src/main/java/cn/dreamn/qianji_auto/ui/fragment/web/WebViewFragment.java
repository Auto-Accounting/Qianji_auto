package cn.dreamn.qianji_auto.ui.fragment.web;

import static cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment.KEY_URL;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.afollestad.materialdialogs.LayoutMode;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.tencent.mmkv.MMKV;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;

import net.ankio.timepicker.listener.OnTimeSelectListener;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.bills.BillInfo;
import cn.dreamn.qianji_auto.database.Helper.BookNames;
import cn.dreamn.qianji_auto.database.Helper.Category;
import cn.dreamn.qianji_auto.database.Helper.CategoryNames;
import cn.dreamn.qianji_auto.database.Helper.identifyRegulars;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.components.TitleBar;
import cn.dreamn.qianji_auto.ui.utils.BottomArea;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.DateUtils;
import cn.dreamn.qianji_auto.utils.runUtils.Log;
import cn.dreamn.qianji_auto.utils.runUtils.Tool;


@Page(name = "WebView", params = {KEY_URL}, anim = CoreAnim.slide)
public class WebViewFragment extends BaseFragment {
    public static final String KEY_URL = "KEY_URL";

    @BindView(R.id.title_bar)
    TitleBar title_bar;
    @BindView(R.id.webView)
    WebView webView;
    /**
     * 和浏览器相关，包括和JS的交互
     */
    protected WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            //网页加载进度
            // if(progressBar!=null)
            //     progressBar.setProgress(newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (title_bar != null && !TextUtils.isEmpty(title)) {
                if (title.length() > 20) {
                    title = title.substring(0, 20).concat("...");
                }
                title_bar.setTitle(title);

            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_webview;
    }

    @Override
    protected void initTitle() {
        // super.initTitle();
        title_bar.setInner(getActivity());


        title_bar.setLeftIconOnClickListener(v -> popToBack());
    }

    /**
     * 和网页url加载相关，统计加载时间
     */
    protected WebViewClient mWebViewClient = new WebViewClient() {
        private final HashMap<String, Long> mTimer = new HashMap<>();

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mTimer.put(url, System.currentTimeMillis());
          /*  if (url.equals(getUrl())) {
                //   progressBar.setVisibility(View.GONE);
                //  pageNavigator(View.GONE);
            } else {
                //   progressBar.setVisibility(View.VISIBLE);
                //  pageNavigator(View.VISIBLE);
            }*/
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Long startTime = mTimer.get(url);
            if (startTime != null) {
                long overTime = System.currentTimeMillis();
                //统计页面的使用时长
                Log.i("Qianji", " page mUrl:" + url + "  used time:" + (overTime - startTime));
            }

        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }


    };
    Handler mHandler;



    public static void openUrl(BaseFragment baseFragment, String url) {
        PageOption.to(WebViewFragment.class)
                .setNewActivity(true)
                .putString(KEY_URL, url)
                .open(baseFragment);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initViews() {
        String url = getUrl();
        if (!url.startsWith("file:///android_asset/")) {
            title_bar.setRightIcon("&#xe60c;", 16);
            title_bar.setRightIconOnClickListener(v -> {
                //创建弹出式菜单对象（最低版本11）
                PopupMenu popup = new PopupMenu(getContext(), v);//第二个参数是绑定的那个view
                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();
                //填充菜单
                inflater.inflate(R.menu.webview, popup.getMenu());
                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.copy:
                            Tool.clipboard(getContext(), webView.getUrl());
                            ToastUtils.show(R.string.copied);

                            break;
                        case R.id.web:
                            Tool.goUrl(getContext(), webView.getUrl());
                            break;
                    }
                    return false;
                });
                //显示(这一行代码不要忘记了)
                popup.show();
            });
        }
        webView.loadUrl(url);
        webView.setWebChromeClient(mWebChromeClient);
        webView.setWebViewClient(mWebViewClient);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js
        webSettings.setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        //  webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//用handler访问让方法在主进程内处理
        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {

                // String[] args = (String[]) msg.obj;

                webView.loadUrl("javascript:" + msg.getData().getString("url"));

            }
        };

        if (url.startsWith("file:///android_asset/html/cate/")) {
            //== webview 与js交互=========================
            //定义提供html页面调用的方法
            Object appToJsObject = new Object() {
                @JavascriptInterface
                public void save(String js, String data) {
                    JSONObject jsonObject = JSONObject.parseObject(data);
                    int version = Integer.parseInt(jsonObject.getString("version"));
                    String regular_name = jsonObject.getString("regular_name");
                    String regular_remark = jsonObject.getString("regular_remark");
                    String data_id = jsonObject.getString("data_id");
                    String id = jsonObject.getString("data_id");
                    if (data_id.equals("")) {
                        jsonObject.put("data_id", Tool.getRandomString(32));
                    }
                    version++;
                    jsonObject.put("version", version);
                    if (id.equals("")) {
                        //存储规则
                        Category.addCategory(js, regular_name, data, regular_remark, () -> {
                            ToastUtils.show(R.string.save_success);
                            popToBack();
                        });
                    } else {
                        //存储规则
                        Category.changeCategory(Integer.parseInt(id), js, regular_name, data, regular_remark, () -> {
                            ToastUtils.show(R.string.change_success);
                            popToBack();
                        });
                    }

                }

                @JavascriptInterface
                public void toast(String msg) {
                    ToastUtils.show(msg);
                }

                @JavascriptInterface
                public void selectTime(String dom) {
                    BottomArea.selectTime(getContext(), true, false, new OnTimeSelectListener() {
                        @Override
                        public void onTimeSelect(Date date, View v) {
                            String time = DateUtils.getTime("HH:mm", date.getTime());
                            doJsFunction(String.format("webviewCallback.setTime('%s','%s')", dom, time));
                        }
                    });
                }

                @SuppressLint("SetTextI18n")
                @JavascriptInterface
                public void testCategory(String js) {
                    LayoutInflater factory = LayoutInflater.from(getContext());
                    View mView = factory.inflate(R.layout.float_test_sort, null);
                    BottomSheet bottomSheet = new BottomSheet(LayoutMode.WRAP_CONTENT);
                    MaterialDialog dialog = new MaterialDialog(getContext(), bottomSheet);
                    dialog.cancelable(false);
                    DialogCustomViewExtKt.customView(dialog, null, mView,
                            false, true, false, false);
                    dialog.cornerRadius(15f, null);
                    dialog.show();

                    LinearLayout layout_money = mView.findViewById(R.id.layout_money);
                    TextView tv_money = mView.findViewById(R.id.tv_money);

                    LinearLayout ll_type = mView.findViewById(R.id.ll_type);
                    TextView tv_type = mView.findViewById(R.id.tv_type);

                    LinearLayout ll_time = mView.findViewById(R.id.ll_time);
                    TextView tv_time = mView.findViewById(R.id.tv_time);

                    LinearLayout ll_shopname = mView.findViewById(R.id.ll_shopname);
                    TextView tv_shopname = mView.findViewById(R.id.tv_shopname);

                    LinearLayout ll_remark = mView.findViewById(R.id.ll_remark);
                    TextView tv_remark = mView.findViewById(R.id.tv_remark);

                    Button button_last = mView.findViewById(R.id.button_last);
                    Button button_next = mView.findViewById(R.id.button_next);

                    MMKV mmkv = MMKV.defaultMMKV();
                    JSONObject jsonObject = JSONObject.parseObject(mmkv.getString("cache_category", "{}"));
                    try {
                        String m = jsonObject.getString("tv_money");
                        if (m == null) m = "0";
                        tv_money.setText("￥" + m);

                        m = jsonObject.getString("tv_type");
                        if (m == null) m = "支出";
                        tv_type.setText(m);

                        tv_time.setText(jsonObject.getString("tv_time"));
                        tv_shopname.setText(jsonObject.getString("tv_shopname"));
                        tv_remark.setText(jsonObject.getString("tv_remark"));
                    } catch (Throwable ignored) {
                    }
                    layout_money.setOnClickListener(view -> BottomArea.input(getContext(), getString(R.string.input_money), tv_money.getText().toString().replace("￥", ""), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
                        @Override
                        public void input(String data) {
                            tv_money.setText("￥" + data);
                            jsonObject.put("tv_money", data);
                        }

                        @Override
                        public void cancel() {

                        }
                    }));
                    List<String> list = Arrays.asList("支出", "收入", "报销");
                    ll_type.setOnClickListener(view -> BottomArea.list(getContext(), getString(R.string.select_type), list, new BottomArea.ListCallback() {
                        @Override
                        public void onSelect(int i) {
                            tv_type.setText(list.get(i));
                            jsonObject.put("tv_type", list.get(i));

                        }

                    }));
                    ll_time.setOnClickListener(view -> {
                        BottomArea.selectTime(getContext(), true, false, (date, v) -> {
                            tv_time.setText(Tool.getTime("yyyy-MM-dd HH:mm:ss", date.getTime()));
                            jsonObject.put("tv_time", Tool.getTime("yyyy-MM-dd HH:mm:ss", date.getTime()));
                        });
                    });
                    ll_shopname.setOnClickListener(view -> BottomArea.input(getContext(), getString(R.string.input_data), tv_shopname.getText().toString(), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
                        @Override
                        public void input(String data) {
                            tv_shopname.setText(data);
                            jsonObject.put("tv_shopname", data);
                        }

                        @Override
                        public void cancel() {

                        }
                    }));
                    ll_remark.setOnClickListener(view -> BottomArea.input(getContext(), getString(R.string.input_data), tv_remark.getText().toString(), getString(R.string.set_sure), getString(R.string.set_cancle), new BottomArea.InputCallback() {
                        @Override
                        public void input(String data) {
                            tv_remark.setText(data);
                            jsonObject.put("tv_remark", data);
                        }

                        @Override
                        public void cancel() {

                        }
                    }));

                    button_last.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mmkv.encode("cache_category", jsonObject.toJSONString());
                            dialog.dismiss();
                        }
                    });

                    button_next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mmkv.encode("cache_category", jsonObject.toJSONString());
                            BillInfo billInfo = new BillInfo();
                            billInfo.setShopRemark(tv_remark.getText().toString());
                            billInfo.setTime(tv_time.getText().toString());
                            billInfo.setShopAccount(tv_shopname.getText().toString());
                            billInfo.setType(BillInfo.getTypeId(tv_type.getText().toString()));
                            billInfo.setMoney(tv_money.getText().toString().replace("￥", ""));
                            Handler handler = new Handler(Looper.getMainLooper()) {
                                @Override
                                public void handleMessage(@NonNull Message msg) {
                                    BottomArea.msg(getContext(), "分类结果", (String) msg.obj);
                                }
                            };
                            Category.getCategory(billInfo, js, str -> HandlerUtil.send(handler, str, -1));
                        }
                    });

                }

                @JavascriptInterface
                public void selectCategory(String type) {
                    switch (type) {
                        case "支出":
                        case "报销":
                            type = "0";
                            break;
                        case "收入":
                            type = "1";
                            break;
                    }
                    //选择分类
                    String finalType = type;
                    BookNames.showBookSelect(getContext(), getString(R.string.set_choose_book), false, bundle -> {
                        //Log.d("账本信息", bundle.toString());
                        CategoryNames.showCategorySelect(getContext(), getString(R.string.set_choose_category), bundle.getString("book_id"), finalType, false, categoryNames -> {
                            doJsFunction(String.format("webviewCallback.setCategory('%s')", categoryNames.getString("name")));
                        });
                    });
                }
            };


            webView.addJavascriptInterface(appToJsObject, "AndroidJS");
        } else if (url.startsWith("file:///android_asset/html/Regulars/")) {
            //== webview 与js交互=========================
            //定义提供html页面调用的方法
            Object appToJsObject = new Object() {


                @JavascriptInterface
                public void Save(String id, String data, String identify) {

                    // Json2["name"]=$("#sort_name").val();
                    //        Json2["text"]=$("#str_input").val();
                    //        Json2["regular"]=$("#regex_input").val();
                    //        Json2["fromApp"]=$("#sort_package").val();
                    //        Json2["des"]=$("#sort_remark").val();
                    //        Json2["identify"]=getQueryVariable("type");
                    //        Json2["tableList"]=Json;

                    JSONObject jsonObject = JSONObject.parseObject(data);
                    String regular = jsonObject.getString("regular");
                    String name = jsonObject.getString("name");
                    String text = jsonObject.getString("text");
                    String fromApp = jsonObject.getString("fromApp");
                    String des = jsonObject.getString("des");
                    String tableList = jsonObject.getJSONObject("tableList").toJSONString();
                    if (id.equals("undefined")) {
                        identifyRegulars.add(
                                regular,
                                name,
                                text,
                                tableList,
                                identify,
                                fromApp,
                                des,
                                () -> {
                                    ToastUtils.show(R.string.save_success);
                                    popToBack();
                                });

                    } else {
                        identifyRegulars.change(
                                Integer.parseInt(id),
                                regular,
                                name,
                                text,
                                tableList,
                                identify,
                                fromApp,
                                des,
                                () -> {
                                    ToastUtils.show(R.string.change_success);
                                    popToBack();
                                });

                    }

                }


            };

            webView.addJavascriptInterface(appToJsObject, "AndroidJS");

        }

    }

    //定义公共方法调用页面js方法
    public void doJsFunction(String _url) {
        Message msg = new Message();
        msg.what = 0;
        Bundle bundle = new Bundle();
        bundle.putString("url", _url);  //往Bundle中存放数据
        msg.setData(bundle);//mes利用Bundle传递数据
        mHandler.sendMessage(msg);//用activity中的handler发送消息
    }

    public String getUrl() {
        String target = "";
        Bundle bundle = getArguments();
        if (bundle != null) {
            target = bundle.getString(KEY_URL);
        }

        if (TextUtils.isEmpty(target)) {
            target = getString(R.string.githubUrl);
        }
        return target;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            //释放资源
            webView.destroy();
            webView = null;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack())
                webView.goBack();
            else popToBack();
        }
        return true;
    }
}
