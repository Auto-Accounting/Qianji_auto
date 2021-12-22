package cn.dreamn.qianji_auto.utils.runUtils;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;

public class SecurityAccess {
    public static final int LOCK_NONE = 0;

    public static final int LOCK_PASSWORD = 1;

    public static class Password {
        public static boolean isSupportKey(Context context) {
            KeyguardManager mKeyguardMgr = context.getSystemService(KeyguardManager.class);
            Intent intent = mKeyguardMgr.createConfirmDeviceCredentialIntent(null, null);
            return intent != null;
        }

        public static void goToKeyUI(Activity context) {
            KeyguardManager mKeyguardMgr = context.getSystemService(KeyguardManager.class);
            Intent intent = mKeyguardMgr.createConfirmDeviceCredentialIntent("请进行解锁", null);
            if (intent != null) {
                context.startActivityForResult(intent, 1101);
            }
        }

        public static boolean onKeyReturn(int requestCode, int resultCode, Activity activity) {
            if (requestCode == 1101) {
                if (resultCode == RESULT_OK) {
                    return true;
                    //Toast.makeText(this, "校验成功", Toast.LENGTH_LONG).show();
                } else {
                    goToKeyUI(activity);
                }
            }
            return false;
        }
    }
}
