package com.dituhui.xnyq.service;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * @author 常洪旭
 */
public interface Api002Service {

    /**
     * 地址逆解析
     * @return
     */
    public Map<String,Object> contraryAnalysis(HttpServletRequest request) throws IOException;
}
