package cn.aliyun.core.controller.seckill;
import cn.aliyun.core.entity.Result;
import cn.aliyun.core.service.seckill.SeckillOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("seckillOrder")
public class SeckillOrderController {
    @Reference
    private SeckillOrderService seckillOrderService;
    @RequestMapping("sumbitOrder")
    public Result sumbitOrder(Long seckillId){
        String username= SecurityContextHolder.getContext().getAuthentication().getName();//获取登录用户名
        if("anonymousUser".equals(username)){//用户未登录
        return new Result(false,"用户未登录");
        }
        try {
            seckillOrderService.sumbitOrder(seckillId,username);
            return new Result(true,"提交成功");
        }catch (RuntimeException ex){
            ex.printStackTrace();
            return new Result(false,ex.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(false,"提交失败");
    }
}
