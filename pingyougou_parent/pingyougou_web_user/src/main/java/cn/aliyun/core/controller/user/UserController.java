package cn.aliyun.core.controller.user;

import cn.aliyun.core.entity.Result;
import cn.aliyun.core.pojo.user.User;
import cn.aliyun.core.service.user.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.PhoneFormatCheckUtils;

@RestController
@RequestMapping("user")
public class UserController {
    @Reference
    private UserService userService;
    @RequestMapping("add")
    public Result add(@RequestBody User user,String smscode){
        if (!userService.checkSmsCode(user.getPhone(),smscode)){
            return new Result(false,"验证码错误");
        }
        try {
            userService.add(user);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    @RequestMapping("sendCode")
    public Result sendCode(String phone){
        //判断手机号格式
        if(!PhoneFormatCheckUtils.isPhoneLegal(phone)){
            return new Result(false,"手机号格式有误");
        }
        try {
            userService.sendCode(phone);
            return new Result(true,"发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"发送失败");
        }
    }
}
