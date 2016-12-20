package toast.s07150813.gdmec.edu.cn.myweather;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/20.
 */
public class SaxHandler extends DefaultHandler{
    //key保存省份,value保存城市
    private Map<String,List<String>> cityMap = new HashMap<>();
    //临时缓存
    String cityName = "";
    String provinceName = "";
    public Map<String,List<String>> getCityMap(){
        return cityMap;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if("Province".equals(qName)){
            //读取省份信息
            provinceName = attributes.getValue("name");
            //每读取到一个省份信息，就将城市名称保存起来
            cityMap.put(provinceName,new ArrayList<String>());
        }else if("City".equals(qName)){
            //读取城信息
            cityName = attributes.getValue("name");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if("City".equals(qName)){
          cityMap.get(provinceName).add(cityName);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
