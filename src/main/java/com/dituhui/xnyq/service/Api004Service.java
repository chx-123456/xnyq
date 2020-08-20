package com.dituhui.xnyq.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 常洪旭
 */
public interface Api004Service {

    /**
     * POI搜索
     * @param request
     */
    public Map<String, Object> poiSearch(HttpServletRequest request);

}
