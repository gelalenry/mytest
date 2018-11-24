package cn.aliyun.core.task;
import cn.aliyun.core.dao.item.ItemCatDao;
import cn.aliyun.core.dao.specification.SpecificationOptionDao;
import cn.aliyun.core.dao.template.TypeTemplateDao;
import cn.aliyun.core.pojo.item.ItemCat;
import cn.aliyun.core.pojo.specification.SpecificationOption;
import cn.aliyun.core.pojo.specification.SpecificationOptionQuery;
import cn.aliyun.core.pojo.template.TypeTemplate;
import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class RedisTask {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    //商品分类的数据同步到缓存中
    //定义任务:cron=程序执行的时间   秒分时日月年
    @Scheduled(cron="00 18 16 * * ?")
    public void autoItemCatsToRedis(){
    //将商品分类的全部数据放入到缓存中
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        if(itemCats!=null&&itemCats.size()>0){
            for (ItemCat itemCat : itemCats) {
                //使用redis的散列数据类型
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
            }
            System.out.println("将商品分类同步到了缓存中");
        }
    }


    // 商品模板的数据同步到缓存中
    // 定义任务
    @Scheduled(cron="00 18 16 * * ?")
    public void autoTypeTemplatesToRedis() {
        //将模板中的品牌以及规格结果集放入到缓存中
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        if (typeTemplates != null && typeTemplates.size() > 0) {
            for (TypeTemplate template : typeTemplates) {
                //品牌结果集
                String brandIds = template.getBrandIds();
                List<Map> brandList = JSON.parseArray(brandIds, Map.class);
                //使用Redis的散列(hash)数据类型
                redisTemplate.boundHashOps("brandList").put(template.getId(), brandList);
                //规格选项结果集
                List<Map> specList = findBySpecList(template.getId());
                redisTemplate.boundHashOps("specList").put(template.getId(), specList);
            }
            System.out.println("将商品模板缓存到了redis中");
        }
    }

    public List<Map> findBySpecList(Long id){
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        //将JSON串转换成对象
        List<Map> list = JSON.parseArray(specIds,Map.class);
        //根据规格获取到规格选项
        for (Map map : list) {
            long aLong = Long.parseLong(map.get("id").toString());//规格id
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            query.createCriteria().andSpecIdEqualTo(aLong);
            List<SpecificationOption> options = specificationOptionDao.selectByExample(query);
            map.put("options",options);
        }
        return list;
    }
}
