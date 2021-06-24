package cn.dreamn.qianji_auto.core.hook.app.wechat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.dreamn.qianji_auto.core.hook.Utils;

public class Info {

    //微信各个版本的适配文件情况
    public static class redPackage {
        //
        private static final HashMap<String, List<String>> hashMap = new HashMap<String, List<String>>() {
            {
                put("default", Arrays.asList(
                        "AcI",//money
                        "zXl",//remark
                        "AcD",//shop
                        "AcB",//Status
                        "rid"
                ));
                put("8.0.2", Arrays.asList(
                        "AcI",//money
                        "zXl",//remark
                        "AcD",//shop
                        "AcB",//Status
                        "rid"
                ));
                put("8.0.6", Arrays.asList(
                        "AZb",//money
                        "ATE",//remark
                        "AYW",//shop
                        "AYU",//Status
                        "rid"
                ));
            }
        };

        private static List<String> map(Utils utils) {
            List<String> ret = hashMap.get(utils.getVerName());
            if (ret == null)
                ret = hashMap.get("default");
            return ret;
        }

        public static String money(Utils utils) {
            List<String> ret = map(utils);
            return ret.get(0);
        }

        public static String remark(Utils utils) {
            List<String> ret = map(utils);
            return ret.get(1);
        }

        public static String shop(Utils utils) {
            List<String> ret = map(utils);
            return ret.get(2);
        }

        public static String status(Utils utils) {
            List<String> ret = map(utils);
            return ret.get(3);
        }

        public static String groups(Utils utils) {
            List<String> ret = map(utils);
            return ret.get(4);
        }

    }
}

