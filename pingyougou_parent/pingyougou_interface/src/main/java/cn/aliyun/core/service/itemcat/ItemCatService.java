package cn.aliyun.core.service.itemcat;

import cn.aliyun.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    public List<ItemCat> findByParentId(Long parentId);
    //商品添加之三级联动
    public ItemCat findOne(Long id);

    //查询所有商品分类
    public List<ItemCat> findAll();
}
