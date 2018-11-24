package cn.aliyun.core.controller.seller;

import cn.aliyun.core.entity.PageResult;
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
    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody Seller seller){
        return sellerService.search(page,rows,seller);
    }

    @RequestMapping("findOne")
    //商家详情查询
    public Seller findOne(String id){
        return sellerService.findOne(id);
    }

    @RequestMapping("updateStatus")
    public Result updateStatus(String sellerId, String status){
        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"审核成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"审核失败");
        }
    }
}
