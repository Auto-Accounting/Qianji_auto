package cn.dreamn.qianji_auto.utils.file;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/*设置数据的本地存储*/
public class Storage {

    static private JSONObject jsonObject;//设置数据的存储对象
    static private String file;
    //声明存储类型，如Set、Map、Learn等
    public static final String Set="Set";
    public static final String AliMap="AliMap";
    public static final String WeMap="WeMap";
    public static final String Map="Map";
    public static final String Learn="Learn";
    public static final String Bill="Bill";
    public static final String Log="Log";
    public static boolean isExist(String Type){
        return myFile.get(Type + ".json").equals("");
    }
    //传入分类表示类型
    public static Storage type(String Type){
        Storage s=new Storage();
        file=Type+".json";
        String fileContent=myFile.get(file);
        try{
            jsonObject=JSONObject.parseObject(fileContent);
            if(jsonObject==null)
                jsonObject=new JSONObject();
        }catch (Exception ignored){
            jsonObject=new JSONObject();
        }
        return s;
    }

    public String get(String key, String def){
        String ret;
        try{
            ret=jsonObject.getString(key);
            if(ret.equals(""))
                ret=def;
        }catch (Exception e){
            ret=def;
        }
        return ret;
    }
    public Boolean getBoolean(String key, boolean def){
        boolean ret;
        try{
            ret=jsonObject.getBoolean(key);

        }catch (Exception e){
            ret=def;
        }
        return ret;
    }
    public void set(String key, boolean val){
        if(jsonObject==null)jsonObject=new JSONObject();
        jsonObject.put(key,val);
        save();
    }
    public void set(String key, JSONArray val){
        if(jsonObject==null)jsonObject=new JSONObject();
        jsonObject.put(key,val);
        save();
    }
    public void set(String key, String val){
        if(jsonObject==null)jsonObject=new JSONObject();
        jsonObject.put(key,val);
        save();
    }
    public void del(String key){
        if(jsonObject==null)jsonObject=new JSONObject();
        jsonObject.remove(key);
        save();
    }
    public JSONObject getAll(){
        return jsonObject;
    }
    //对象销毁时写入存储
    private void save()
    {
        myFile.write(file,JSONObject.toJSONString(jsonObject),true);
    }

    public JSONArray getJSONArray(String log) {
        JSONArray ret;
        try{
            ret=jsonObject.getJSONArray(log);
        }catch (Exception e){
            ret=new JSONArray();
        }
        if(ret==null)ret=new JSONArray();
        return ret;
    }
}
