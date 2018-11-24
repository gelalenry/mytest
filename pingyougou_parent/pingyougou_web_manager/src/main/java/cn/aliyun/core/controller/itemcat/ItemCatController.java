package cn.aliyun.core.controller.itemcat;

import cn.aliyun.core.pojo.item.ItemCat;

import cn.aliyun.core.service.itemcat.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("itemCat")
public class ItemCatController {
    @Reference
    private ItemCatService itemCatService;
    @RequestMapping("findByParentId")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
    }

    //查询所有商品分类
    @RequestMapping("findAll")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }
}
