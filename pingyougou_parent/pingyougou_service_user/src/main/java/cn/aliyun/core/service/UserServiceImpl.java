package cn.aliyun.core.service;

import cn.aliyun.core.dao.user.UserDao;
import cn.aliyun.core.pojo.user.User;
import cn.aliyun.core.service.user.UserService;

import com.alibaba.dubbo.config.annotation.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;


import javax.jms.Destination;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private Destination smscodeDestination;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Override
    public void add(User user) {
        user.setCreated(new Date());//创建日期
        user.setUpdated(new Date());//修改日期
        String s = DigestUtils.md5Hex(user.getPassword());//对密码进行加密
        user.setPassword(s);
        userDao.insert(user);
    }

    @Override
    public void sendCode(String phone) {
    //生成6位随机数
        Long code=(long)(Math.random()*1000000);
        if(code<100000){
            code=code+100000;
        }
        System.out.println("验证码:"+code);
        //存入redis         set加入超时时间
        redisTemplate.boundValueOps("smscode"+phone).set(code+"",5, TimeUnit.MINUTES);
        //发送消息到队列
        Map map = new HashMap();
        map.put("mobile",phone);
        map.put("smscode",code+"");
        jmsTemplate.convertAndSend(smscodeDestination,map);
    }


    //判断验证码
    @Override
    public boolean checkSmsCode(String phone, String smscode) {
        String o = (String) redisTemplate.boundValueOps("smscode" + phone).get();
        if(smscode.equals(o)&&o!=null){
            return true;
        }else{
            return false;
        }

    }
}
