package cn.dreamn.qianji_auto.ui.utils;

import android.os.Handler;
import android.os.Message;


public class HandlerUtil {
    public static final int HANDLE_ERR = 0;
    public static final int HANDLE_OK = 1;
    public static final int HANDLE_REFRESH = 2;

    public static void send(Handler handler, int single) {
        send(handler, null, single);
    }

    public static void send(Handler handler, Object obj, int single) {
        send(handler, obj, 0, single);
    }

    public static void send(Handler handler, Object obj, int arg1, int single) {
        send(handler, obj, arg1, 0, single);
    }

    public static void send(Handler handler, Object obj, int arg1, int arg2, int single) {
        Message message = new Message();
        message.arg1 = arg1;
        message.arg2 = arg2;
        message.obj = obj;
        message.what = single;
        handler.sendMessage(message);
    }
}
