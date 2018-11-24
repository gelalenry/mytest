package cn.aliyun.core.service.pay;

import java.util.HashMap;

public interface WeixinPayService {
    //统一下单API(生成订单二维码)
    public HashMap createNative(String out_trade_no,String total_fee);

    //查询订单状态
    public HashMap queryPayStatus(String out_trade_no);

    //循环查询订单状态
    public HashMap queryPayStatusWhile(String out_trade_no);
}
