package cn.aliyun.core.sms;

import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

public class MyMessageListener implements MessageListener{
    @Override
    public void onMessage(Message message) {
        MapMessage mapMessage=(MapMessage)message;
        try {
            String mobile = mapMessage.getString("mobile");//手机号
            String smscode = mapMessage.getString("smscode");//验证码
            System.out.println("收到"+mobile+"..."+smscode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
