package toast.s07150813.gdmec.edu.cn.myweather;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MainActivity extends AppCompatActivity {
           private MainActivity left;
    private Map<String,List<String>> cityMap;
    private Spinner province_spinner;
    private Spinner city_spinner;
    private AlertDialog choose_dialog;
    private LinearLayout choose_layout;
    private ImageButton settingBtn;
    private ImageButton refreshBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        left = this;
        //初始化
        initProvince();
        //初始化城市窗口
        initChooseDialog();
        settingBtn = (ImageButton) findViewById(R.id.setting);
        refreshBtn = (ImageButton) findViewById(R.id.refresh);
        //为天气设置事件监听器
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_dialog.show();
            }
        });
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获得当前城市
                String cityName = ((TextView)left.findViewById(R.id.city)).getText().toString();
                //请求天气信息
                new GetWeatherInfoTask(left).execute(cityName);
            }
        });
        new GetWeatherInfoTask(this).execute("广州");
    }
    //初始化城市选择窗口
    private void initChooseDialog(){
        //创建一个警告对话框
        choose_dialog = new AlertDialog.Builder(left).setTitle("选择城市").setPositiveButton("确定",
                new chooseCityLister()).setNegativeButton("取消",null).create();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        //初始化布局
        choose_layout = (LinearLayout) layoutInflater.inflate(R.layout.choose,null);
        //初始化省份spinner
        province_spinner = (Spinner) choose_layout.findViewById(R.id.spinner1);
        city_spinner = (Spinner) choose_layout.findViewById(R.id.spinner2);
        //将所有省份取出放进spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.simple_list_item,new ArrayList<>(cityMap.keySet()));
        province_spinner.setAdapter(arrayAdapter);
        province_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //获得选中省份名称
                String province_name = province_spinner.getSelectedItem().toString();
                //获取省份下的城市，放进spinner
                ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(left,R.layout.simple_list_item,
                        cityMap.get(province_name));
                city_spinner.setAdapter(arrayAdapter1);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //设置窗口内容
        choose_dialog.setView(choose_layout);
    }
    //选择城市的对话框
    private  class chooseCityLister implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialog, int which) {
            //获得选中的城市
            String city_name = city_spinner.getSelectedItem().toString();
            TextView cityName = (TextView) left.findViewById(R.id.city);
            //设置当前城市名称
            cityName.setText(city_name);
            //连接网路中选中的城市
            new GetWeatherInfoTask(left).execute(city_name);
        }
    }
    //解释XML文件，初始化省份城市
    private void initProvince(){
        AssetManager assetManager = getAssets();
        SaxHandler saxHandler = new SaxHandler();
        InputStream inputStream = null;
        try{
            //打开省份，城市文件
            inputStream = assetManager.open("City.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputStream,saxHandler);
            //解释XML得到省，市列表；
            cityMap = saxHandler.getCityMap();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
             if(inputStream!=null){
                 try {
                     inputStream.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
        }
    }
}
