package cn.aliyun.core.controller.pay;

import cn.aliyun.core.entity.Result;
import cn.aliyun.core.pojo.seckill.SeckillOrder;
import cn.aliyun.core.service.pay.WeixinPayService;
import cn.aliyun.core.service.seckill.SeckillOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("pay")
public class PayController {
    @Reference
    private WeixinPayService weixinPayService;
    @Reference
    private SeckillOrderService seckillOrderService;
    @RequestMapping("createNative")
    public HashMap createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前用户
        SeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedis(name);//在redis中查询秒杀订单
        if(seckillOrder!=null){
        long money_fee=(long)(seckillOrder.getMoney().doubleValue()*100);
        HashMap map=weixinPayService.createNative(seckillOrder.getId()+"",money_fee+"");
                return map;
        }else {
            return new HashMap();
        }
    }

    //从redis中读取订单数据并保存到数据库
    @RequestMapping("queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前用户
        HashMap map = weixinPayService.queryPayStatusWhile(out_trade_no);
        if(map==null){
        return new Result(false,"二维码超时");
        }else {
            if("SUCCESS".equals(map.get("trade_state"))){//如果支付成功
                try {
                    seckillOrderService.saveOrderFromRedisToDb(name,Long.valueOf(out_trade_no), (String)map.get("transaction_id"));
                    return new Result(true,"支付成功");
                } catch (RuntimeException ex){
                    return new Result(false,ex.getMessage());
                }catch (Exception e) {
                    e.printStackTrace();
                    return new Result(false,"支付失败");
                }
            }else{
                return new Result(false,"支付失败");
            }
        }
    }
}
