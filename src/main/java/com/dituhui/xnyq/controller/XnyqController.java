package com.dituhui.xnyq.controller;


import com.dituhui.xnyq.model.Urlconfig;
import com.dituhui.xnyq.service.Api002Service;
import com.dituhui.xnyq.service.Api003Service;
import com.dituhui.xnyq.service.Api004Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author 常洪旭
 */
@RestController
public class XnyqController {

    @Autowired
    private Api002Service api002Service;
    @Autowired
    private Api003Service api003Service;
    @Autowired
    private Api004Service api004Service;


    /**
     * 逆地址解析
     * 根据提供的坐标，返回该坐标最近的兴趣点
     * @return
     */
    @RequestMapping(value = Urlconfig.API002,method = RequestMethod.GET)

    public Map<String, Object> api002(HttpServletRequest request) throws IOException {
        return api002Service.contraryAnalysis(request);
    }

    /**
     * 路径分析
     * 根据传入的起始点坐标，返回两点间路径情况
     * @return
     */
    @RequestMapping(value = Urlconfig.API003,method = RequestMethod.GET)
    public Map<String, Object> api003(HttpServletRequest request){
        return api003Service.pathAnalysis(request);
    }

    /**
     * POI 搜索
     * 传入关键词，返回包含该关键词的 POI 相关信息
     * @return
     */
    @RequestMapping(value = Urlconfig.API004,method = RequestMethod.GET)
    public Map<String, Object> api004(HttpServletRequest request){
        return api004Service.poiSearch(request);
    }
}
