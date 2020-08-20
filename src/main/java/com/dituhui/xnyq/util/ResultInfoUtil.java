package com.dituhui.xnyq.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 常洪旭
 */
public class ResultInfoUtil {
    /**
     * 返回失败信息
     * @return
     */
    public static Map<String, Object> fail(String info){
        Map<String,Object> jsonRequest = new HashMap<>();
        jsonRequest.put("success", false);
        jsonRequest.put("info",info);
        return jsonRequest;
    }

    /**
     * 成功返回参数
     * @param status
     * @param info
     * @return
     */
    public static Map<String, Object> succsee(String status, String info){
        if ("0".equals(status)){
            return fail(info);
        }  else {
            Map<String,Object> object = new HashMap<>();
            object.put("success",true);
            return object;
        }
    }
}
