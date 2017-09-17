package com.example.accountmodule.base;

/**
 * Created by yingjiawei on 2017/9/16.
 */

public interface IChecker <RESULT,PARAM>{
    /**
     * 检查参数
     * @param param
     * @return
     */
    //String check(String param);
    RESULT check(PARAM param);
}
