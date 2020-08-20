package com.dituhui.xnyq.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dituhui.xnyq.util.HttpRequestUtil;
import com.dituhui.xnyq.model.Urlconfig;
import com.dituhui.xnyq.service.Api002Service;
import com.dituhui.xnyq.util.ResultInfoUtil;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 常洪旭
 */
@PropertySource(value = {"file:${config.path}"},encoding = "utf-8")
@Service
public class Api002ServiceImpl implements Api002Service {


    @Value("${useKey}")
    private String usekey;

    @Value("${key}")
    private String key;

    private String url = Urlconfig.REGEO;


    /**
     * 逆地址解析
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    public Map<String,Object> contraryAnalysis(HttpServletRequest request) throws IOException {

        String urlkey = request.getParameter("key");
        String t = request.getParameter("t");
        String callbacks = request.getParameter("callbacks");
        String param = request.getParameter("param");

        //校验必填项
        if (StringUtils.isEmpty(urlkey)){
            return ResultInfoUtil.fail("key必填");
        }
        if (!usekey.equals(urlkey)){
            return ResultInfoUtil.fail("key值不对");
        }

        //校验必填项
        JSONObject json = JSON.parseObject(param);
        String pointsStr = json.getString("points");
        if (StringUtils.isEmpty(pointsStr)){
            return ResultInfoUtil.fail("参数必填");
        }
        String type = json.getString("type");
        String from = json.getString("from");

        //获取url中的参数
        JSONArray jsonPointsStr = JSONArray.fromObject(pointsStr);
        String points = jsonPointsStr.getString(0);
        JSONObject jsonPoints = JSON.parseObject(points);
        String code = jsonPoints.getString("code");
        String point = jsonPoints.getString("point");

        JSONObject jsonPoint = JSON.parseObject(point);
        String x = jsonPoint.getString("x");
        String y = jsonPoint.getString("y");

        //摩托卡转经纬度
        double vx = Double.parseDouble(x);
        double vy = Double.parseDouble(y);
        double xx = vx / 20037508.34 * 180;
        double yy = vy / 20037508.34 * 180;
        yy = 180 / Math.PI * (2 * Math.atan(Math.exp(yy * Math.PI / 180)) - Math.PI / 2);
        String longitude  = location(xx);
        String latitude  = location(yy);
        url = url + "?key=" + key + "&location=" + longitude+","+latitude;

        //调用接口返回数据
        String data = HttpRequestUtil.sendGet(url);
        JSONObject jsondata = JSON.parseObject(data);
        String regeocode = jsondata.getString("regeocode");
        String status = jsondata.getString("status");
        String info = jsondata.getString("info");

        //数据拆分
        JSONObject jsonRegeocode= JSON.parseObject(regeocode);
        String addressComponent = jsonRegeocode.getString("addressComponent");
        JSONObject jsonGet = JSON.parseObject(addressComponent);
        String streetNumber = jsonGet.getString("streetNumber");
        JSONObject jsonStreet = JSON.parseObject(streetNumber);

        //封装要返回的数据
        Map<String,Object> mapObject = new HashMap<>();
        HashMap<String, Object> map = new HashMap<>();
        map.put("x",xx);
        map.put("y",yy);
        mapObject.put("point",map);
        mapObject.put("code",code);
        mapObject.put("country",jsonGet.getString("country"));
        mapObject.put("province",jsonGet.getString("province"));
        mapObject.put("city",jsonGet.getString("city"));
        mapObject.put("district",jsonGet.getString("district"));
        mapObject.put("street",jsonStreet.getString("street"));
        mapObject.put("address",jsonRegeocode.getString("formatted_address"));
        List list = new ArrayList<>();
        list.add(mapObject);
        Map<String, Object> succsee = ResultInfoUtil.succsee(status, info);
        succsee.put("result",list);
        return succsee;
    }


    private String location(Double d){
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留六位小数
        nf.setMaximumFractionDigits(6);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.UP);
        return nf.format(d);
    }


}
