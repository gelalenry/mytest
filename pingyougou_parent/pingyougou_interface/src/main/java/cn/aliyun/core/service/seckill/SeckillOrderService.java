package cn.aliyun.core.service.seckill;

import cn.aliyun.core.pojo.seckill.SeckillOrder;

public interface SeckillOrderService {
    //提交订单
    public void sumbitOrder(Long seckillId,String userId);

    //从缓存中提取订单
    public SeckillOrder searchOrderFromRedis(String userId);

    //从redis中提取订单并保存到数据库
    public void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId);
}
