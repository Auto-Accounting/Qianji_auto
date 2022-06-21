package cn.dreamn.qianji_auto.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;

import java.util.Collection;

import cn.dreamn.qianji_auto.R;
import cn.dreamn.qianji_auto.setting.AppInfo;
import cn.dreamn.qianji_auto.ui.base.BaseAdapter;

public class AppListAdapter extends BaseAdapter {

    private final Context mContext;

    public AppListAdapter(Context context) {
        super(R.layout.adapter_data_item);
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(SmartViewHolder holder, Bundle item, int position) {
        ImageView imageView = holder.findViewById(R.id.icon_header);
        TextView textView = holder.findViewById(R.id.item_value);
        TextView packageName = holder.findViewById(R.id.item_package);
        ConstraintLayout rl = holder.findViewById(R.id.rl);
        String name = item.getString("name");
        String pkg = item.getString("pkg");
        imageView.setImageDrawable(AppInfo.getIcon(mContext, pkg));
        textView.setText(highLightKeyWord(name));
        packageName.setText(highLightKeyWord(pkg));
        if (item.getBoolean("checked")) {
            rl.setBackgroundColor(mContext.getColor(R.color.float_2));
        } else {
            rl.setBackgroundColor(mContext.getColor(R.color.background_white));
        }
    }

    private SpannableStringBuilder highLightKeyWord(String str) {
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        int startPos = str.indexOf(keyWord);
        if (!"".equals(keyWord) && startPos >= 0) {
            style.setSpan(new ForegroundColorSpan(Color.GREEN), startPos,
                    startPos + keyWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return style;
    }

    private String keyWord = "";
    public SmartRecyclerAdapter<Bundle> refresh(Collection<Bundle> collection, @NonNull String keyWord) {
        this.keyWord = keyWord;
        return super.refresh(collection);
    }
}
