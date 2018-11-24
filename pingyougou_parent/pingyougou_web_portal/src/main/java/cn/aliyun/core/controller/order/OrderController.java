package cn.aliyun.core.controller.order;

import cn.aliyun.core.entity.Result;
import cn.aliyun.core.pojo.order.Order;
import cn.aliyun.core.service.order.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {
    @Reference
    private OrderService orderService;
    @RequestMapping("add")
    public Result add(@RequestBody Order order){
        //获取当前登录人信息
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        order.setUserId(username);
        try {
            orderService.add(order);
            return new Result(true,"订单提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"订单提交失败");
        }
    }
}
