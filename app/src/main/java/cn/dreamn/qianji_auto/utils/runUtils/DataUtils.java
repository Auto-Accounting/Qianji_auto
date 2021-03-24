/*
 * Copyright (C) 2021 dreamn(dream@dreamn.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cn.dreamn.qianji_auto.utils.runUtils;

import android.net.Uri;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Set;

public class DataUtils {
    private final Bundle bundle;

    public DataUtils() {
        bundle = new Bundle();
    }

    public void put(String name, Object data) {
        if (data == null) bundle.putString(name, "");
        else bundle.putString(name, data.toString());
    }

    public String get(String name) {
        return bundle.getString(name);
    }

    public void remove(String name) {
        bundle.remove(name);
    }

    public void parse(String url) throws UnsupportedEncodingException {
        Uri mUri = Uri.parse(url);
        Set<String> params = mUri.getQueryParameterNames();
        for (String param : params) {
            String value = mUri.getQueryParameter(param);
          //  Logs.d(param);

            bundle.putString(param, URLDecoder.decode(value, "UTF-8"));
        }
    }

    public String toString() {
        StringBuilder ret = new StringBuilder("data://string?");
        Set<String> keySet = bundle.keySet();
        for (String key : keySet) {
            String value = (String) bundle.get(key);
            try {
                ret.append("&").append(key).append("=").append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return ret.toString();
    }
}
