package cn.aliyun.core.service.seckill;

import cn.aliyun.core.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {
    //查询当前正在参加秒杀的商品
    public List<SeckillGoods> findList();

    //根据ID从缓存中获取实体数据
    public SeckillGoods findOneFromRedis(Long id);
}
