package cn.aliyun.core.controller.pay;

import cn.aliyun.core.entity.Result;
import cn.aliyun.core.pojo.log.PayLog;
import cn.aliyun.core.service.order.OrderService;
import cn.aliyun.core.service.pay.WeixinPayService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.jboss.netty.util.Timeout;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.HashMap;

@RestController
@RequestMapping("pay")
public class PayController {
    @Reference(timeout = 1000*60*6)
    private WeixinPayService weixinPayService;
    @Reference
    private OrderService orderService;
    @RequestMapping("createNative")
    public HashMap createNative(){
         String name = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录用户
         PayLog payLog=orderService.searchPayLogFromRedis(name);//查询支付日志
         if(payLog!=null){
            return weixinPayService.createNative(payLog.getOutTradeNo(),payLog.getTotalFee()+"");
         }else {
             return new HashMap();
         }
        // IdWorker idWorker = new IdWorker(0, 0);
        // return weixinPayService.createNative(idWorker.nextId()+"","1");
    }

    //检测支付状态
    @RequestMapping("queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        HashMap map = weixinPayService.queryPayStatusWhile(out_trade_no);
        if(map==null){
            return new Result(false,"二维码过期,请重试");
        }else{
            if("SUCCESS".equals(map.get("trade_state"))){//如果支付成功
                orderService.updateOrderStatus(out_trade_no,(String)map.get("transaction_id"));
                return new Result(true,"支付成功");
            }else {
                return new Result(false,"支付失败");
            }
        }
    }
}
