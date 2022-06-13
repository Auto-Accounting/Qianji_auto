package cn.dreamn.qianji_auto.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;
import cn.dreamn.qianji_auto.ui.components.IconView;
import cn.dreamn.qianji_auto.ui.utils.HandlerUtil;
import cn.dreamn.qianji_auto.utils.runUtils.AutoBillWeb;
import cn.dreamn.qianji_auto.utils.runUtils.Log;

public class CateItemListAdapter extends BaseAdapter {
    private final Context mContext;

    private MoreClick moreClick;
    private UpdateClick updateClick;
    private ImportClick importClick;
    private boolean isWeb = false;
    private String type = "";
    private String app = "";

    public CateItemListAdapter(Context context, boolean isWeb, String type, String app) {
        super(R.layout.adapter_cate_list_item);
        mContext = context;
        this.isWeb = isWeb;
        this.type = type;
        this.app = app;
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {
        // Log.i("cadapter","数据："+item.toString());

        View view_status = holder.findView(R.id.view_status);
        View view_auto = holder.findView(R.id.view_auto);
        TextView tv_title = (TextView) holder.findView(R.id.tv_title);
        TextView tv_des = (TextView) holder.findView(R.id.tv_des);
        IconView icon_info = (IconView) holder.findView(R.id.icon_info);
        TextView tv_update = (TextView) holder.findView(R.id.update);
        tv_title.setText(item.getString("name"));
        String des = item.getString("remark");
        String reg = item.getString("regular");

        boolean isAuto = false;
        if (des != null) {
            if (des.startsWith("[自动生成]")) {
                isAuto = true;
            }
            tv_des.setText(des);
            if (reg != null) {
                if (reg.contains("+time+'##1'}}catch(e){console.log(e)};")) {
                    isAuto = true;
                }
            }

        } else {
            icon_info.setVisibility(View.GONE);
            tv_des.setVisibility(View.GONE);
        }
        Drawable drawable_top = mContext.getDrawable(R.drawable.bg_cate_green);
        Drawable drawable_bottom = mContext.getDrawable(R.drawable.bg_cate_green_bottom);

        if (item.getInt("use") != 1) {
            drawable_top = mContext.getDrawable(R.drawable.bg_cate_gray);
            drawable_bottom = mContext.getDrawable(R.drawable.bg_cate_gray_bottom);
        }
        view_status.setBackground(drawable_top);
        if (isAuto) {
            view_auto.setBackground(mContext.getDrawable(R.drawable.bg_cate_auto));
        } else {
            view_auto.setBackground(drawable_bottom);
        }
        icon_info.setOnClickListener(v -> {
            if (moreClick != null)
                moreClick.onClick(item);
        });
        tv_update.setVisibility(View.INVISIBLE);
        String dataId = item.getString("dataId");
        if (dataId == null || dataId.equals("")) {
            //   Log.i("dataId=null");
            return;
        }

        if (isWeb) {
            // Log.i("isweb=true");
            tv_update.setVisibility(View.VISIBLE);
            tv_update.setText("导入");
            tv_update.setOnClickListener(v -> {
                if (importClick != null)
                    importClick.onClick(dataId);
            });
            return;
        }


        String version = item.getString("version");
        if (version == null || version.equals("")) {
            return;
        }
        String name = item.getString("name");

        Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                JSONObject jsonObject1 = (JSONObject) msg.obj;
                int serverVer = Integer.parseInt(jsonObject1.getString("version"));
                int localVer = Integer.parseInt(version);

                if (serverVer > localVer) {
                    tv_update.setVisibility(View.VISIBLE);
                    String title = name + "[" + localVer + " → " + serverVer + "]";
                    String updateLog = jsonObject1.getString("log");
                    if (updateLog == null || updateLog.equals("")) updateLog = "无更新日志";
                    JSONObject js = new JSONObject();
                    js.put("title", title);
                    js.put("log", updateLog);
                    js.put("dataId", dataId);
                    js.put("name", jsonObject1.getString("name"));
                    tv_update.setOnClickListener(v -> {
                        if (updateClick != null){
                            updateClick.onClick(js);
                        }
                        //点完就隐藏
                        tv_update.setVisibility(View.GONE);

                    });


                }
            }
        };


        loadData(mContext, type, app, dataId, (LoadCloudApp) jsonObject -> HandlerUtil.send(mHandler, jsonObject, 0));


    }


    public void setOnMoreClick(MoreClick moreClick) {
        if (moreClick != null)
            this.moreClick = moreClick;
    }

    public void setOnUpdateClick(UpdateClick updateClick) {
        if (updateClick != null)
            this.updateClick = updateClick;
    }

    public void setOnImportClick(ImportClick importClick) {
        if (importClick != null)
            this.importClick = importClick;
    }

    public interface MoreClick {
        void onClick(Bundle item);
    }

    public interface UpdateClick {
        void onClick(JSONObject data);
    }

    public interface ImportClick {
        void onClick(String dataId);
    }

    private void loadData(Context context, String type, String app, String dataId, LoadCloudApp loadCloudApp) {
        AutoBillWeb.getList(context, new AutoBillWeb.WebCallback() {
            @Override
            public void onFailure() {

            }

            @Override
            public void onSuccessful(String data) {
                JSONObject jsonObject = null;

                try {
                    jsonObject = JSON.parseObject(data);
                } catch (Exception e) {
                    Log.d("解析JSON失败：" + data);
                    return;
                }


                if (!jsonObject.containsKey(type)) {
                    Log.d("JSON不包含数据：" + data);
                    return;
                }
                JSONObject jsonObject1 = jsonObject.getJSONObject(type);
                //Log.i(jsonObject1.toJSONString());
                if (app != null && !app.equals("")) {
                    JSONObject jsonObject3 = jsonObject1.getJSONObject(app);
                    if (jsonObject3 == null) {
                        Log.d("Js3 null：" + app);
                        return;
                    }
                    JSONObject jsonObject2 = jsonObject3.getJSONObject(dataId);
                    if (jsonObject2 == null) {
                        return;
                    }

                    loadCloudApp.onLoad(jsonObject2);
                }

            }
        });
    }

    public interface LoadCloudApp {
        void onLoad(JSONObject jsonObject);
    }
}
