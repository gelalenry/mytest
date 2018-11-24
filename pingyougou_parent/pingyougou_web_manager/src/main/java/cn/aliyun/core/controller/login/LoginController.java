package cn.aliyun.core.controller.login;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("showName")
    public Map<String,String> showName(){
        Map<String,String> map = new HashMap<>();
        //从springsecurity获取登录用户信息
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);
        return map;
    }
}
