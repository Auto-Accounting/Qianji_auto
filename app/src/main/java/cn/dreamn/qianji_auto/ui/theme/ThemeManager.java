package cn.dreamn.qianji_auto.ui.theme;

import android.content.Context;

import com.tencent.mmkv.MMKV;
import com.zhengsr.skinlib.ZSkin;
import com.zhengsr.skinlib.utils.ZUtils;

import java.io.File;

import cn.dreamn.qianji_auto.ui.views.IconView;
import cn.dreamn.qianji_auto.ui.views.IconViewDelegate;

public class ThemeManager {
    private Context mContext;
    private MMKV mmkv;
    public ThemeManager(Context context){
        mContext=context;
        mmkv=MMKV.defaultMMKV();
    }
    public static void init(){
        ZSkin.addDelegate(IconView.class,new IconViewDelegate());
    }
    public void setTheme(){
        replaceInApp(mmkv.getString("theme","default"));
    }

    private void replace(String skinName){
        if(skinName.equals("default"))return;
        String path =  mContext.getFilesDir().getAbsolutePath();
        String name = skinName+".skin";
        String assetName = "skin/"+skinName+".skin";
//直接改变了
        File file = new File(path,name);
        //如果不存在，则从 assets copy 过去
        if (!file.exists()) {
            ZUtils.copyAssetFileToStorage(mContext,assetName,path,name);
        }

        //立刻加载
        ZSkin.loadSkin(file.getAbsolutePath());
    }
    private void replaceInApp(String skinName){
        if(skinName.equals("default"))return;
        //立刻加载
        ZSkin.loadSkinByPrefix(skinName);
    }
}
