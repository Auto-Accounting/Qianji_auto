package cn.dreamn.qianji_auto.utils.runUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class RootUtils {
    public static String result;

    public static boolean hasRootPermission() {
        PrintWriter PrintWriter;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            PrintWriter = new PrintWriter(process.getOutputStream());
            PrintWriter.flush();
            PrintWriter.close();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    ///data/system_ce/0/shortcut_service/
    public static String getResult() {
        return result;
    }

    public static boolean exec(String[] cmds) {
        PrintWriter PrintWriter;
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");

            PrintWriter = new PrintWriter(process.getOutputStream());
            for (String cmd : cmds) {
                PrintWriter.println(cmd);
            }
            PrintWriter.flush();
            PrintWriter.close();

            //获取返回值
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder strb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                strb.append(line).append("\n");
            }
            is.close();
            reader.close();
            result = strb.toString();
            int value = process.waitFor();
            return returnResult(value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return false;
    }

    private static boolean returnResult(int value) {
        // 代表成功
        if (value == 0) {
            return true;
        } else if (value == 1) { // 失败
            return false;
        } else { // 未知情况
            return false;
        }
    }

}
