package cn.aliyun.core.entity;

import java.io.Serializable;

/**
 * 登陆结果(需要同时返回登陆用户名和查询结果的时候调用)
 */
public class LoginResult implements Serializable {

    private boolean success;//是否成功
    private String loginname;//登陆名
    private Object data;//返回的数据

    public LoginResult(boolean success, String loginname, Object data) {
        this.success = success;
        this.loginname = loginname;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getLoginname() {
        return loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
