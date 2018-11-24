package cn.aliyun.core.service.order;

import cn.aliyun.core.pojo.log.PayLog;
import cn.aliyun.core.pojo.order.Order;

public interface OrderService {
    //添加订单
    public void add(Order order);

    PayLog searchPayLogFromRedis(String userId);

    //修改订单状态
    public void updateOrderStatus(String out_trade_no,String transaction_id);
}
