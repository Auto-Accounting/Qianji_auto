package cn.dreamn.qianji_auto.ui.fragment.web;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.widget.PopupMenu;

import com.hjq.toast.ToastUtils;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xpage.core.PageOption;
import com.xuexiang.xpage.enums.CoreAnim;

import java.util.HashMap;

import butterknife.BindView;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseFragment;
import cn.dreamn.qianji_auto.ui.utils.StatusBarUtil;
import cn.dreamn.qianji_auto.ui.views.TitleBar;
import cn.dreamn.qianji_auto.utils.Tool;

import static cn.dreamn.qianji_auto.ui.fragment.web.WebViewFragment.KEY_URL;

@Page(name="WebView", params = {KEY_URL}, anim = CoreAnim.slide)
public class WebViewFragment extends BaseFragment {
    public static final String KEY_URL = "KEY_URL" ;
    @BindView(R.id.title_bar)
    TitleBar title_bar;
    @BindView(R.id.webView)
    WebView webView;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_webview;
    }

    @Override
    protected com.xuexiang.xpage.utils.TitleBar initTitle() {
        title_bar.setInner(getActivity());


        title_bar.setLeftIconOnClickListener(v -> popToBack());
        title_bar.setRightIcon("&#xe63d;",16);
        title_bar.setRightIconOnClickListener(v->{
            //创建弹出式菜单对象（最低版本11）
            PopupMenu popup = new PopupMenu(getContext(), v);//第二个参数是绑定的那个view
            //获取菜单填充器
            MenuInflater inflater = popup.getMenuInflater();
            //填充菜单
            inflater.inflate(R.menu.webview, popup.getMenu());
            //绑定菜单项的点击事件
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case R.id.copy:
                        Tool.clipboard(getContext(),webView.getUrl());
                        ToastUtils.show("已复制到剪切板。");
                        break;
                    case R.id.web:
                        Tool.goUrl(getContext(),webView.getUrl());
                        break;
                }
                return false;
            });
            //显示(这一行代码不要忘记了)
            popup.show();
        });
        return null;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void initViews() {

        webView.loadUrl(getUrl());
        webView.setWebChromeClient(mWebChromeClient);
        webView.setWebViewClient(mWebViewClient);
        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);//允许使用js
      //  webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);



    }



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
    /**
     * 和网页url加载相关，统计加载时间
     */
    protected WebViewClient mWebViewClient = new WebViewClient() {
        private HashMap<String, Long> mTimer = new HashMap<>();

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }





        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            mTimer.put(url, System.currentTimeMillis());
            if (url.equals(getUrl())) {
             //   progressBar.setVisibility(View.GONE);
              //  pageNavigator(View.GONE);
            } else {
             //   progressBar.setVisibility(View.VISIBLE);
              //  pageNavigator(View.VISIBLE);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Long startTime = mTimer.get(url);
            if (startTime != null) {
                long overTime = System.currentTimeMillis();
                //统计页面的使用时长
                Log.i("Qianji"," page mUrl:" + url + "  used time:" + (overTime - startTime));
            }
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }


    };

    public static void openUrl(BaseFragment baseFragment, String url){
         PageOption.to(WebViewFragment.class)
                .setNewActivity(true)
                .putString(KEY_URL, url)
                .open(baseFragment);
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
        if(webView!=null){
            //释放资源
            webView.destroy();
            webView=null;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(webView.canGoBack())
                webView.goBack();
            else popToBack();
        }
        return true;
    }
}
