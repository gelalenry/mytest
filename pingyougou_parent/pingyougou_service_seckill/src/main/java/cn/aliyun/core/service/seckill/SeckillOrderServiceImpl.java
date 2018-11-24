package cn.aliyun.core.service.seckill;
import cn.aliyun.core.dao.seckill.SeckillGoodsDao;
import cn.aliyun.core.dao.seckill.SeckillOrderDao;
import cn.aliyun.core.pojo.seckill.SeckillGoods;
import cn.aliyun.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;
import java.util.Date;
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Autowired
    private SeckillOrderDao seckillOrderDao;
    @Override
    @Transactional
    public void sumbitOrder(Long seckillId, String userId) {
        //限制一个用户在同一时间内只能下单一次
        if(redisTemplate.boundHashOps("seckillOrder").get(userId)!=null){
            throw new RuntimeException("请完成上一个订单");
        }
        //1.从缓存中提取商品数据
        SeckillGoods seckillGoods= (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
        if(seckillGoods==null||seckillGoods.getNum()<=0){
            throw new RuntimeException("活动已结束");
        }
        //2.扣减库存
        seckillGoods.setNum(seckillGoods.getNum()-1);
        seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
        redisTemplate.boundHashOps("seckillGoods").put(seckillId,seckillGoods);
        //2.1如果商品被抢购空,同步到数据库
        if(seckillGoods.getNum()==0){
            redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
            seckillGoodsDao.updateByPrimaryKey(seckillGoods);
        }
        //3.将订单数据添加到缓存
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(seckillId);//商家ID
        seckillOrder.setUserId(userId);//用户id
        seckillOrder.setCreateTime(new Date());//创建日期
        seckillOrder.setStatus("0");//商品状态
        seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
        redisTemplate.boundHashOps("seckillOrder").put(userId,seckillGoods);

    }

    //从redis中提取订单
    @Override
    public SeckillOrder searchOrderFromRedis(String userId) {
        return (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }

    //从redis读取订单并保存到数据库
    @Override
    @Transactional
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
    //从redis中查询订单
        SeckillOrder seckillOrder = searchOrderFromRedis(userId);
        if(seckillOrder==null){
            throw  new RuntimeException("该订单不存在");
        }
        if(seckillOrder.getId().longValue()!=orderId.longValue()){
            throw new RuntimeException("订单号不一致");
        }
        //修改订单状态
        seckillOrder.setPayTime(new Date());//支付日期
        seckillOrder.setStatus("1");//订单状态
        seckillOrder.setTransactionId(transactionId);//支付账单流水号
        //保存到数据库
        seckillOrderDao.insert(seckillOrder);
        //清除redis中的数据
        redisTemplate.boundHashOps("seckillOrder").delete(userId);
    }
}
