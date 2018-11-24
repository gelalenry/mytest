package cn.aliyun.core.service.page;


import cn.aliyun.core.dao.good.GoodsDao;
import cn.aliyun.core.dao.good.GoodsDescDao;
import cn.aliyun.core.dao.item.ItemCatDao;
import cn.aliyun.core.dao.item.ItemDao;
import cn.aliyun.core.pojo.good.Goods;
import cn.aliyun.core.pojo.good.GoodsDesc;
import cn.aliyun.core.pojo.item.Item;
import cn.aliyun.core.pojo.item.ItemCat;
import cn.aliyun.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private ItemDao itemDao;
    @Value("${pagedir}")
    private String pagedir;
    @Override
    public boolean genHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfig.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");
            //定义数据模型
            Map map = new HashMap();
            Goods goods = goodsDao.selectByPrimaryKey(goodsId);
            map.put("goods",goods);
            GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goodsId);
            map.put("goodsDesc",goodsDesc);
            //商品分类
            ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());//一级分类
            ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());//二级分类
            ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());//三级分类
            map.put("itemCat1",itemCat1.getName());
            map.put("itemCat2",itemCat2.getName());
            map.put("itemCat3",itemCat3.getName());
            //SKU列表
            ItemQuery itemQuery = new ItemQuery();
            ItemQuery.Criteria criteria = itemQuery.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            itemQuery.setOrderByClause("is_default desc");
            List<Item> items = itemDao.selectByExample(itemQuery);
            map.put("itemList",items);
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(pagedir+goodsId+".html"),"utf-8");
                template.process(map,writer);
            writer.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



}
