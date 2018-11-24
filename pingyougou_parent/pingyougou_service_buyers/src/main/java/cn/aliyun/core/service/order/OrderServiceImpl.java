package cn.aliyun.core.service.order;

import cn.aliyun.core.dao.log.PayLogDao;
import cn.aliyun.core.dao.order.OrderDao;
import cn.aliyun.core.dao.order.OrderItemDao;
import cn.aliyun.core.pojo.cart.Cart;
import cn.aliyun.core.pojo.log.PayLog;
import cn.aliyun.core.pojo.order.Order;
import cn.aliyun.core.pojo.order.OrderItem;
import cn.aliyun.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private PayLogDao payLogDao;
    //生成订单
    @Override
    public void add(Order order) {
        //1.从redis读取购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        if(cartList==null){
            return;
        }
        //2.保存订单和订单明细
        String outTradeNo=idWorker.nextId()+"";//支付订单号
        long total_money=0;
        for (Cart cart : cartList) {
            Order tbOrder=new Order();
            long orderId = idWorker.nextId();//订单ID
            tbOrder.setOrderId(orderId);
            tbOrder.setStatus("1");//订单状态为未支付
            tbOrder.setPaymentType(order.getPaymentType());//支付方式
            tbOrder.setCreateTime(new Date());//订单创建日期
            tbOrder.setUserId(order.getUserId());//用户id
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());//收货地址
            tbOrder.setReceiverMobile(order.getReceiverMobile());//联系电话
            tbOrder.setReceiver(order.getReceiver());//收件人
            tbOrder.setSourceType(order.getSourceType());//订单来源
            tbOrder.setSellerId(cart.getSellerId());//商家ID
            tbOrder.setOutTradeNo(outTradeNo);//支付订单号

            double money=0;//金额
            for(OrderItem orderItem:cart.getOrderItemList()){
                orderItem.setOrderId(orderId);//订单id
                orderItem.setId(orderId);//订单明细表id
                orderItemDao.insert(orderItem);
                money+=orderItem.getTotalFee().doubleValue();//金额累加
            }
            total_money+=(long)(money*100);
            tbOrder.setPayment(new BigDecimal(money));//支付金额
            orderDao.insert(tbOrder);
        }
        //3.若为微信支付,在支付日志中添加记录
            if(order.getPaymentType().equals("1")){
                PayLog payLog = new PayLog();//支付日志=支付订单
                payLog.setOutTradeNo(outTradeNo);//订单号
                payLog.setCreateTime(new Date());//创建日期
                payLog.setUserId(order.getUserId());//支付账户
                payLog.setTotalFee(total_money);//支付总金额
                payLog.setTradeState("0");//未支付
                payLogDao.insert(payLog);
                redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
            }
        //4.清除购物车记录
        redisTemplate.boundHashOps("cartList").delete(order.getUserId());
    }

    @Override
    public PayLog searchPayLogFromRedis(String userId) {
        return (PayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }


    //修改订单状态
    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
        //1.修改支付日志的状态及相关字段
        PayLog payLog = payLogDao.selectByPrimaryKey(out_trade_no);
        payLog.setPayTime(new Date());//支付日期
        payLog.setTransactionId(transaction_id);//支付订单号
        payLog.setTradeState("1");//支付状态0未支付1已支付
        payLogDao.updateByPrimaryKey(payLog);
        //2.修改订单状态
        OrderQuery query = new OrderQuery();
        OrderQuery.Criteria criteria = query.createCriteria();
        criteria.andOutTradeNoEqualTo(out_trade_no);
        Order order = new Order();
        order.setStatus("2");
        order.setPaymentTime(new Date());
        orderDao.updateByExampleSelective(order, query);
        //3.清除redis的支付日志对象
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
