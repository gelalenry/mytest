package cn.aliyun.core.controller.goods;

import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.entity.Result;
import cn.aliyun.core.pojo.good.Goods;
import cn.aliyun.core.service.goods.GoodsService;
import cn.aliyun.core.vo.GoodsVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("goods")
public class GoodsController {
    @Reference
    private GoodsService goodService;
    @RequestMapping("add")
    public Result add(@RequestBody GoodsVo goodsVo){
        try {
            //设置商家id
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsVo.getGoods().setSellerId(name);
            goodService.add(goodsVo);
            return new Result(true,"保存成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }


    //查询当前商家的商品列表
    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){
        //设置商家id
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(id);
        return goodService.search(page,rows,goods);
    }


    //商品回显
    @RequestMapping("findOne")
    public GoodsVo findOne(Long id){
        return goodService.findOne(id);
    }


    //商品更新
    @RequestMapping("update")
    public Result update(@RequestBody GoodsVo goodsVo){
        try {
            goodService.update(goodsVo);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }
}
