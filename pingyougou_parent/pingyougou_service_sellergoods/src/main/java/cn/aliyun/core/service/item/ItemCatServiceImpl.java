package cn.aliyun.core.service.item;
import cn.aliyun.core.dao.item.ItemCatDao;
import cn.aliyun.core.pojo.item.ItemCat;
import cn.aliyun.core.pojo.item.ItemCatQuery;

import cn.aliyun.core.service.itemcat.ItemCatService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;
@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Resource
    private ItemCatDao itemCatDao;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    //商品分类列表查询
    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        //将商品分类的全部数据放入到缓存中
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        if(itemCats!=null&&itemCats.size()>0){
        for (ItemCat itemCat : itemCats) {
            //使用redis的hash(散列)数据类型
            redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
             }
        }
        //设置查询条件
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    //商品添加之三级联动
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    //查询所有分类
    @Override
    public List<ItemCat> findAll(){
        return itemCatDao.selectByExample(null);
    }
}
