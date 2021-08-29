package cn.dreamn.qianji_auto.ui.utils;

import android.os.Handler;
import android.os.Message;


public class HandlerUtil {
    public static final int HANDLE_ERR = 0;
    public static final int HANDLE_OK = 1;
    public static final int HANDLE_REFRESH = 2;

    public static void send(Handler handler, Object obj, int single) {
        Message message = new Message();
        message.obj = obj;
        message.what = single;
        handler.sendMessage(message);
    }
}
