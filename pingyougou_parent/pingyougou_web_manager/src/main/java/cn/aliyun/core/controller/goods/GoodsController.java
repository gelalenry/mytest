package cn.aliyun.core.controller.goods;

import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.entity.Result;
import cn.aliyun.core.pojo.good.Goods;
import cn.aliyun.core.service.goods.GoodsService;
import cn.aliyun.core.service.page.ItemPageService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;
    @Reference
    private ItemPageService itemPageService;
    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){
        return goodsService.searchForManager(page,rows,goods);
    }

    //审核商品
    @RequestMapping("updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            goodsService.updateStatus(ids,status);
            for (Long id : ids) {
                itemPageService.genHtml(id);
            }
            return new Result(true,"审核通过");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"审核失败哦");
        }
    }

    //删除商品
    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try {
            goodsService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("genHtml")
    public void genHtml(Long goodsId){
        itemPageService.genHtml(goodsId);
    }
}
