package com.dituhui.xnyq.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dituhui.xnyq.model.Urlconfig;
import com.dituhui.xnyq.service.Api004Service;
import com.dituhui.xnyq.util.HttpRequestUtil;
import com.dituhui.xnyq.util.ResultInfoUtil;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 常洪旭
 */
@Service
@PropertySource(value = {"file:${config.path}"},encoding = "utf-8")
public class Api004ServiceImpl implements Api004Service {

    @Value("${useKey}")
    private String usekey;

    @Value("${key}")
    private String key;

    private String url = Urlconfig.TEXT;
    @Override
    public Map<String, Object> poiSearch(HttpServletRequest request) {
        String urlKey = request.getParameter("key");
        String param = request.getParameter("param");
        if (StringUtils.isEmpty(urlKey)){
            return ResultInfoUtil.fail("key必填");
        }
        if (!usekey.equals(urlKey)){
            return ResultInfoUtil.fail("key值不对");
        }
        JSONObject jsonObject = JSON.parseObject(param);
        String filter = jsonObject.getString("filter");
        String pageNo = jsonObject.getString("pageNo");
        String pageSize = jsonObject.getString("pageSize");
        String returnFields = jsonObject.getString("returnFields");

        String keywords = "酒店";
        String city = "110106";
        String cityLimit = "true";
        url = url + "?key=" + key + "&keywords=" + keywords + "&city=" + city +"&citylimit=" + cityLimit + "&offset=" + pageSize + "&page=" + pageNo;
        String data = HttpRequestUtil.sendGet(url);

        JSONObject dataJson = JSON.parseObject(data);
        String totalCount = dataJson.getString("count");
        String pois = dataJson.getString("pois");
        String status = dataJson.getString("status");
        String info = dataJson.getString("info");
        JSONArray poisJson = JSONArray.fromObject(pois);
        int currentCount = poisJson.size();
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("totalCount",totalCount);
        resultMap.put("page",pageNo);
        resultMap.put("currentCount",currentCount);

        ArrayList<Object> list = new ArrayList<>();
        for (int i=0; i<=poisJson.size()-1; i++){
            HashMap<Object, Object> poisMap = new HashMap<>();
            String poisString = poisJson.getString(i);
            JSONObject object = JSON.parseObject(poisString);
            String location = object.getString("location");
            String[] split = location.split(",");
            poisMap.put("poi_id",object.getString("id"));
            poisMap.put("x",split[0]);
            poisMap.put("y",split[1]);
            poisMap.put("name",object.getString("name"));
            poisMap.put("admincode",object.getString("typecode"));
            poisMap.put("address",object.getString("address"));
            poisMap.put("province",object.getString("pname"));
            poisMap.put("city",object.getString("cityname"));
            poisMap.put("county",object.getString("adname"));
            poisMap.put("py",null);
            list.add(poisMap);
        }
        resultMap.put("pois",list);
        Map<String, Object> object = ResultInfoUtil.succsee(status, info);
        object.put("result",resultMap);
        return  object;
    }



}
