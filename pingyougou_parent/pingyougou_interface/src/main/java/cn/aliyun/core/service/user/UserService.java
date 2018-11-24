package cn.aliyun.core.service.user;

import cn.aliyun.core.pojo.user.User;

public interface UserService {
    public void add(User user);
    //生成短信验证码
    void sendCode(String phone);
    //判断验证码
    public boolean checkSmsCode(String phone, String smscode);
}
