package com.dituhui.xnyq.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dituhui.xnyq.model.Urlconfig;
import com.dituhui.xnyq.service.Api003Service;
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
import java.util.List;
import java.util.Map;

/**
 * @author 常洪旭
 */
@Service
@PropertySource(value = {"file:${config.path}"},encoding = "utf-8")
public class Api003ServiceImpl implements Api003Service {

    @Value("${key}")
    private String key;

    private String url = Urlconfig.DIRVING;

    @Override
    public Map<String, Object> pathAnalysis(HttpServletRequest request) {
        String pathAnalystParameters = request.getParameter("pathAnalystParameters");
        JSONArray parameters = JSONArray.fromObject(pathAnalystParameters);
        String param = parameters.getString(0);
        JSONObject jsonObject = JSON.parseObject(param);

        //校验必填项
        String startPoint = jsonObject.getString("startPoint");
        if (StringUtils.isEmpty(startPoint)){
            return ResultInfoUtil.fail("startPoint必填");
        }

        //拼接调用接口参数
        JSONObject jsonStart = JSON.parseObject(startPoint);
        String sx = jsonStart.getString("x");
        String sy = jsonStart.getString("y");
        url = url + "?key=" + key + "&origin=" + sx + "," +sy;

        String endPoint = jsonObject.getString("endPoint");
        if (StringUtils.isEmpty(endPoint)){
            return ResultInfoUtil.fail("endPoint必填");
        }
        JSONObject jsonEnd = JSON.parseObject(endPoint);
        String ex = jsonEnd.getString("x");
        String ey = jsonEnd.getString("y");
        url = url + "&destination=" + ex + "," +ey;


        String passPoints = jsonObject.getString("passPoints");
        if (StringUtils.isNotEmpty(passPoints)) {
            JSONArray points = JSONArray.fromObject(passPoints);
            String pass = points.getString(0);
            JSONObject jsonPass = JSON.parseObject(pass);
            String px = jsonPass.getString("x");
            String py = jsonPass.getString("y");
            url = url + "&waypoints=" + px + "," + py;
        }

        String routeType = jsonObject.getString("routeType");
        String strategy = null;
        if (StringUtils.isNotEmpty(routeType)){
            switch (routeType) {
                case "MINLENGTH":
                    strategy = "2";
                    break;
                case "NOHIGHWAY":
                    strategy = "7";
                    break;
                case "RECOMMEND":
                    strategy = "0";
                    break;
                default:
                    break;
            }
            if (StringUtils.isNotEmpty(strategy)) {
                url = url + "&strategy=" + strategy;
            }
        }
        //调用外部接口
        String data = HttpRequestUtil.sendGet(url);
        Map<String, Object> requestData = requestData(data);

        return requestData;
    }

    //数据包装
    private Map<String, Object> requestData(String data){

        List pointList = new ArrayList<>();
        List infoList = new ArrayList<>();
        JSONObject jsonObject = JSON.parseObject(data);
        String route = jsonObject.getString("route");
        String status = jsonObject.getString("status");
        String info = jsonObject.getString("info");
        JSONObject routeJson = JSON.parseObject(route);
        String paths = routeJson.getString("paths");
        JSONArray parameter = JSONArray.fromObject(paths);
        String param = parameter.getString(0);

        JSONObject paramJson = JSON.parseObject(param);
        String pathLength = paramJson.getString("toll_distance");
        String steps = paramJson.getString("steps");
        JSONArray stepsJson = JSONArray.fromObject(steps);

        //包装pathPoint
        for (int i=0; i<stepsJson.size()-1; i++){
            String step = stepsJson.getString(i);
            JSONObject stepJson = JSON.parseObject(step);
            String polyline = stepJson.getString("polyline");
            String[] split = polyline.split(";");
            for (int j=0; j<split.length-1; j++ ){
                String[] xy = split[j].split(",");
                HashMap<String, Object> map = new HashMap<>();
                map.put("x",xy[0]);
                map.put("y",xy[1]);
                pointList.add(map);
            }
            String tmcs = stepJson.getString("tmcs");
            JSONArray tmcsJson = JSONArray.fromObject(tmcs);
            //包装pathInfos
            for (int j=0; j<tmcsJson.size()-1; j++){
                String tmc = tmcsJson.getString(j);
                JSONObject tmcJson = JSON.parseObject(tmc);
                String polyline1 = tmcJson.getString("polyline");
                String[] split1 = polyline1.split(";");
                HashMap<String, Object> tmcsMap = new HashMap<>();
                tmcsMap.put("dirToSwerve",j);
                tmcsMap.put("length",tmcJson.getString("distance"));
                for (int l=0; l<split1.length-1; l++ ){
                    String[] xy = split1[l].split(",");
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("x",xy[0]);
                    map.put("y",xy[1]);
                    tmcsMap.put("junction",map);
                }
                infoList.add(tmcsMap);
            }
        }
        Map<String, Object> object = ResultInfoUtil.succsee(status, info);
        object.put("pathPoints",pointList);
        object.put("pathInfos",infoList);
        object.put("pathLength",pathLength);
        return object;
    }


}
