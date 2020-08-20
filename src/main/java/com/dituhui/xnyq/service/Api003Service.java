package com.dituhui.xnyq.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author 常洪旭
 */
public interface Api003Service {

    /**
     * 地址分析
     */
    public Map<String, Object> pathAnalysis(HttpServletRequest request);

}
