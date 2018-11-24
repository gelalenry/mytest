package cn.aliyun.core.controller.seller;

import cn.aliyun.core.entity.Result;
import cn.aliyun.core.pojo.seller.Seller;
import cn.aliyun.core.service.seller.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("seller")
public class SellerController {
    @Reference
    private SellerService sellerService;
    //商家入驻
    @RequestMapping("add")
    public Result add(@RequestBody Seller seller){
    try{
        sellerService.add(seller);
return new Result(true,"入驻成功");
    }catch (Exception e){
        e.printStackTrace();
        return new Result(false,"入驻失败");
    }
    }

}
