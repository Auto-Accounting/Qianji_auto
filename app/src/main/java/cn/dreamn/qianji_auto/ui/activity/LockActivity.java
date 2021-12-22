package cn.dreamn.qianji_auto.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.dreamn.qianji_auto.App;
import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.setting.AppInfo;
import cn.dreamn.qianji_auto.ui.base.BaseActivity;
import cn.dreamn.qianji_auto.utils.runUtils.MultiprocessSharedPreferences;
import cn.dreamn.qianji_auto.utils.runUtils.SecurityAccess;

public class LockActivity extends BaseActivity {
    private String app = null;

    public static void doStartApplicationWithPackageName(Context activity, String packagename, Bundle data) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = activity.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = activity.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            if (data != null)
                intent.putExtras(data);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                app = bundle.getString("app");
            }
        }
        setContentView(R.layout.activity_main);
        TextView tip = findViewById(R.id.textView_tip);
        ImageView iv = findViewById(R.id.iv_logo);
        TextView appName = findViewById(R.id.tv_app_name);

        if (app != null) {
            appName.setText(AppInfo.getName(this, app));
            iv.setImageDrawable(AppInfo.getIcon(this, app));
        }
        if (SecurityAccess.Password.isSupportKey(this)) {
            SecurityAccess.Password.goToKeyUI(this);
        } else {
            tip.setText("请在系统中先配置锁屏密码再试！");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SecurityAccess.Password.onKeyReturn(requestCode, resultCode, this)) {
            openMainPage();
        }
    }

    private void openMainPage() {
        if (app == null) {
            App.isLock = false;
            Intent intent = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("unlock", "true");
            intent.putExtras(bundle);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences(this, "apps", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("lock_qianji_style_lock", "false").apply();
            doStartApplicationWithPackageName(this, app, null);
            finishAndRemoveTask();
        }

    }


}
