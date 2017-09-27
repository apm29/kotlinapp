package com.example.accountmodule.base;

/**
 * Created by yingjiawei on 2017/9/16.
 */

public interface IAccountManager {
    enum AccountBehavior{
        /**
         * 用户行为：登陆 找回密码 登出 修改密码 注册
         */
        LOGIN,RETRIEVE_PASSWORD,LOGOUT,CHANGE_PASSWORD,REGISTER
    }

    /**
     * 设置请求的网络client
     * @param client
     */
    IAccountManager setClient(IHttpClient client);

    /**
     * 设置结果类型
     * @param responseType
     * @return
     */

    /**
     * 设置行为
     * @param behavior
     * @return
     */
    IAccountManager setAccountBehavior(AccountBehavior behavior);


    /**
     * 相当于构建完成方法,进行开始account行为
     */
    <T> T setup(Class<T> tClass);

}
