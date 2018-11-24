package cn.aliyun.core.service.pay;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService{
    @Value("${appid}")//微信公众号或开发平台app唯一标识
    private String appid;
    @Value("${partner}")//财付通平台的商户账号
    private String partner;
    @Value("${partnerkey}")//财付通平台的商户密钥
    private String partnerkey;
    @Override
    public HashMap createNative(String out_trade_no, String total_fee) {
        //1.创建参数
        HashMap map = new HashMap();
        map.put("appid",appid);//公众号 ID
        map.put("mch_id",partner);//商户号
        map.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        map.put("body","品优购");
        map.put("out_trade_no",out_trade_no);//支付订单号
        map.put("total_fee",total_fee);//支付金额（分）
        map.put("spbill_create_ip","127.0.0.1");//随便
        map.put("notify_url","http://pay.itcast.cn");//回调地址(随便)
        map.put("trade_type","NATIVE");//本地支付（扫码支付）
        try {
            String signedXml = WXPayUtil.generateSignedXml(map, partnerkey);//请求参数
            System.out.println("请求参数:"+signedXml);
        //2.发送请求
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        httpClient.setHttps(true);
        httpClient.setXmlParam(signedXml);
        httpClient.post();
        //3.获得结果
            String content = httpClient.getContent();
            System.out.println("返回结果"+content);
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(content);
            HashMap m=new HashMap();
            if( "SUCCESS".equals(xmlToMap.get("return_code")) && "SUCCESS".equals(xmlToMap.get("result_code")) ){
                m.put("code_url",xmlToMap.get("code_url"));//支付地址
                m.put("out_trade_no",out_trade_no);//订单号
                m.put("total_fee",total_fee);//支付总金额
            }else{
                System.out.println("出错");
            }
            return m;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //检查订单状态
    @Override
    public HashMap queryPayStatus(String out_trade_no) {
        //1.构建参数
        HashMap hashMap = new HashMap();
        hashMap.put("appid",appid);//公众号 ID
        hashMap.put("mch_id",partner);//商户号
        hashMap.put("nonce_str", WXPayUtil.generateNonceStr());//随机字符串
        hashMap.put("out_trade_no",out_trade_no);//支付订单号
        try {
            String xml = WXPayUtil.generateSignedXml(hashMap, partnerkey);
            System.out.println("查询订单支付状态请求参数:"+xml);
            //2.发送请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            client.setHttps(true);
            client.setXmlParam(xml);
            client.post();
            //3.获得结果
            String content = client.getContent();
            System.out.println("查询订单状态获取结果:"+content);
            HashMap<String, String> map = (HashMap) WXPayUtil.xmlToMap(content);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //循环查询订单状态
    @Override
    public HashMap queryPayStatusWhile(String out_trade_no) {
        int x=0;
        HashMap map=null;
        while (true){
            if (x>100){
                break;//跳出
            }
            map=queryPayStatus(out_trade_no);
            if(map==null){
                break;//跳出
            }
            if("SUCCESS".equals(map.get("trade_state"))){//支付成功
                break;//跳出
            }
            //间隔3秒
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
}
