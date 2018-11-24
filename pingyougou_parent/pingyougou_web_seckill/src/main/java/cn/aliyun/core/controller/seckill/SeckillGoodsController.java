package cn.aliyun.core.controller.seckill;

import cn.aliyun.core.pojo.seckill.SeckillGoods;
import cn.aliyun.core.service.seckill.SeckillGoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("seckillGoods")
public class SeckillGoodsController {
    @Reference(timeout = 10000)
    private SeckillGoodsService seckillGoodsService;
    //当前秒杀的商品
    @RequestMapping("findList")
    public List<SeckillGoods> findList(){
        return seckillGoodsService.findList();
    }

    //根据id从缓存中获取实体数据
    @RequestMapping("findOne")
    public SeckillGoods findOne(Long id){
        return seckillGoodsService.findOneFromRedis(id);
    }

}
