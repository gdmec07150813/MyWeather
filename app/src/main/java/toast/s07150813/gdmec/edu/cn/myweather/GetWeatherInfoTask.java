package toast.s07150813.gdmec.edu.cn.myweather;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.EntityIterator;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/20.
 */
public class GetWeatherInfoTask extends AsyncTask<String,Void,List<Map<String,Object>>> {
    private Activity context;
    private ProgressDialog progressDialog;
    private ListView weather_info;
    private String errorMsg = "网络错误！";
    private static String BASE_URL = "http://v.juhe.cn/weather/index?format=2&cityname=";
    private static String key = "&key=a675315273732bf6501967763cc1d3e0";
    //网络访问时的进度对话框
    public GetWeatherInfoTask(Activity context){
         this.context = context;
         //获取天气的提示框
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在获取天气信息，请稍等....");
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(List<Map<String, Object>> maps) {
        super.onPostExecute(maps);
        progressDialog.dismiss();
        if(maps.size()>0){
          weather_info = (ListView) context.findViewById(R.id.weather_info);
            //更新天气列表
            SimpleAdapter simpleAdapter = new SimpleAdapter(context,maps,
                    R.layout.weather_item,
                    new String[]{"temperature","weather","date","week","weather_icon"},
                    new int[]{R.id.temperature,R.id.weather,R.id.date,R.id.weather,R.id.weather_icon});
            weather_info.setAdapter(simpleAdapter);
        }else{
            Toast.makeText(context,errorMsg,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected List<Map<String, Object>> doInBackground(String... params) {
        List<Map<String,Object>> list = new ArrayList<>();
        try{
            //连接网络
            HttpClient httpClient = new DefaultHttpClient();
            //创建访问url，并转码
            String url = BASE_URL+ URLEncoder.encode(params[0],"UTF-8")+key;
            //创建httpGet对象
            HttpGet httpGet = new HttpGet(url);
            //httpClient 执行 httpGet,获取response
            HttpResponse response = httpClient.execute(httpGet);
            if(response.getStatusLine().getStatusCode()==200){
            //获取gson字符串
                String jsonString = EntityUtils.toString(response.getEntity(),"UTF-8");
                JSONObject jsondata = new JSONObject(jsonString);
                if(jsondata.getInt("resultcode")==200){
                  JSONObject result = jsondata.getJSONObject("result");
                    JSONArray weatherList = result.getJSONArray("future");
                    for(int i=0;i<7;i++){
                      Map<String,Object> item = new HashMap<>();
                        JSONObject weatObject = weatherList.getJSONObject(i);
                        item.put("temperature",weatObject.getString("temperature"));
                        item.put("weather",weatObject.getString("weather"));
                        item.put("date",weatObject.getString("date"));
                        item.put("wind",weatObject.getString("wind"));
                        //获取天气图标
                        JSONObject wid = weatObject.getJSONObject("weather_id");
                        int weather_icon = wid.getInt("fa");
                        //获取对应的天气图标
                        item.put("weather_icon",WeathIcon.weather_icons[weather_icon]);
                        list.add(item);
                    }
                }else{
                      errorMsg = "非常抱歉，本应用暂不支持你所在的城市";
                }
            }else{
                errorMsg = "网络错误";
            }


        }catch (Exception E){
            E.printStackTrace();
        }
        return list;
    }
}
