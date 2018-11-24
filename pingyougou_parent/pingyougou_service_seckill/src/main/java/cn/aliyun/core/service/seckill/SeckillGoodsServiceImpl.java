package cn.aliyun.core.service.seckill;

import cn.aliyun.core.dao.seckill.SeckillGoodsDao;
import cn.aliyun.core.pojo.seckill.SeckillGoods;
import cn.aliyun.core.pojo.seckill.SeckillGoodsQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Autowired
    private RedisTemplate redisTemplate;
    //查询当前正在参加秒杀的商品
    @Override
    public List<SeckillGoods> findList() {
        //查询缓存中的数据
        List<SeckillGoods> seckillGoodsList =redisTemplate.boundHashOps("seckillGoods").values();
        if(seckillGoodsList==null||seckillGoodsList.size()==0){
            System.out.println("从数据库中取出商品记录");
            //查询秒杀商品列表
            SeckillGoodsQuery query = new SeckillGoodsQuery();
            SeckillGoodsQuery.Criteria criteria = query.createCriteria();
            criteria.andNumGreaterThan(0);//商品库存大于零
            criteria.andStatusEqualTo("1");//商品状态为已审核
            criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前日期
            criteria.andEndTimeGreaterThan(new Date());//截止日期大于当前日期
            seckillGoodsList= seckillGoodsDao.selectByExample(query);
            //将秒杀商品列表放入缓存
            for (SeckillGoods seckillGoods : seckillGoodsList) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(),seckillGoods);
            }
        }
        return seckillGoodsList;
    }

    //根据id从缓存中获取实体数据
    @Override
    public SeckillGoods findOneFromRedis(Long id) {
        return (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
    }
}
