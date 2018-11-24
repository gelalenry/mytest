package cn.aliyun.core.service.goods;

import cn.aliyun.core.dao.good.BrandDao;
import cn.aliyun.core.dao.good.GoodsDao;
import cn.aliyun.core.dao.good.GoodsDescDao;
import cn.aliyun.core.dao.item.ItemCatDao;
import cn.aliyun.core.dao.item.ItemDao;
import cn.aliyun.core.dao.seller.SellerDao;
import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.good.Goods;
import cn.aliyun.core.pojo.good.GoodsDesc;
import cn.aliyun.core.pojo.good.GoodsQuery;
import cn.aliyun.core.pojo.item.Item;
import cn.aliyun.core.pojo.item.ItemQuery;
import cn.aliyun.core.vo.GoodsVo;


import com.alibaba.dubbo.config.annotation.Service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Resource
    private GoodsDao goodsDao;
    @Resource
    private GoodsDescDao goodsDescDao;
    @Resource
    private ItemDao itemDao;
    @Resource
    private ItemCatDao itemCatDao;
    @Resource
    private SellerDao sellerDao;
    @Resource
    private BrandDao brandDao;
    @Resource
    private SolrTemplate solrTemplate;
    @Transactional
    @Override
    public void add(GoodsVo goodsVo) {
        //保存商品
        Goods goods = goodsVo.getGoods();
        //默认商品待审核的状态
        goods.setAuditStatus("0");
        goodsDao.insertSelective(goods);
        //保存明细
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getId());
        goodsDescDao.insertSelective(goodsDesc);
        //保存商品对应的库存信息
        //判断是否启用规格
        if("1".equals(goods.getIsEnableSpec())){
            //启用规格,一个商品对应多个库存信息
            List<Item> itemList = goodsVo.getItemList();
            if(itemList!=null&&itemList.size()>0){
                for (Item item : itemList) {
                    //商品标题:spu名称+spu副标题+规格名称
                    String title=goods.getGoodsName()+""+ goods.getCaption();
                    //规格数据
                    String spec = item.getSpec();
                    Map<String,String> map = JSON.parseObject(spec, Map.class);
                    Set<Map.Entry<String,String>> entries= map.entrySet();
                    for(Map.Entry<String, String> entry : entries){
                        title += " " + entry.getValue();
                    }
                    item.setTitle(title);
                    setAttributeForItem(goods, goodsDesc, item); // 设置库存表的属性
                    itemDao.insertSelective(item);
                }
            }
        }else{
            //不启用规格:一个商品对应一个库存信息
            Item item = new Item();
            item.setTitle(goods.getGoodsName()+""+goods.getCaption());
            item.setPrice(goods.getPrice());
            item.setNum(9999);
            item.setIsDefault("1");
            item.setSpec("{}");
            //保存库存表的属性
            setAttributeForItem(goods, goodsDesc, item);
            itemDao.insertSelective(item);
        }
    }

    //设置库存属性
    private void setAttributeForItem(Goods goods, GoodsDesc goodsDesc, Item item) {
        //商品图片
        String itemImages = goodsDesc.getItemImages();
        List<Map> maps = JSON.parseArray(itemImages, Map.class);
        if (maps!=null&&maps.size()>0){
            //在检索的过程中用于显示
            String s = maps.get(0).get("url").toString();
            item.setImage(s);
        }
        item.setCategoryid(goods.getCategory3Id()); // 三级分类的id
        item.setStatus("1"); // 商品状态
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setGoodsId(goods.getId());
        item.setSellerId(goods.getSellerId());
        item.setCategory(itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName()); // 分类名称
        item.setBrand(brandDao.selectByPrimaryKey(goods.getBrandId()).getName());    // 品牌名称
        item.setSeller(sellerDao.selectByPrimaryKey(goods.getSellerId()).getNickName());   // 商家店铺名称
    }

    //查询当前商家的商品列表
    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        //设置分页条件
        PageHelper.startPage(page,rows);
        //设置查询条件,根据商家id查询
        GoodsQuery goodsQuery = new GoodsQuery();
        if(goods.getSellerId()!=null&&!"".equals(goods.getSellerId().trim())){
            goodsQuery.createCriteria().andSellerIdEqualTo(goods.getSellerId().trim());
        }
        goodsQuery.setOrderByClause("id desc");
        //查询
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    //商品回显
    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo goodsVo = new GoodsVo();
        //商品信息
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goodsVo.setGoods(goods);
        //商品明细
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goodsVo.setGoodsDesc(goodsDesc);
        //库存信息
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(itemQuery);
        goodsVo.setItemList(items);
        return goodsVo;
    }

    //商品更新
    @Transactional
    @Override
    public void update(GoodsVo goodsVo) {
    //商品审核未通过,需要重新修改,重新设置审核状态
        Goods goods = goodsVo.getGoods();
        goods.setAuditStatus("0");
        goodsDao.updateByPrimaryKeySelective(goods);
        //更新商品明细
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDescDao.updateByPrimaryKeySelective(goodsDesc);
        //更新库存,先删后加
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(goods.getId());
        itemDao.deleteByExample(itemQuery);
        //判断是否启用规格
        if("1".equals(goods.getIsEnableSpec())){
            //启用规格,一个商品对应多个库存
            List<Item> itemList = goodsVo.getItemList();
            if(itemList!=null&&itemList.size()>0) {
                for (Item item : itemList) {
                    //SPU名称
                    String title = goods.getGoodsName();
                    Map<String, String> map = JSON.parseObject(item.getSpec(), Map.class);
                    Set<Map.Entry<String, String>> entries = map.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        title += "" + entry.getValue();
                    }
                    item.setTitle(title);
                    //设置库存表的属性
                    setAttributeForItem(goods, goodsDesc, item);
                    itemDao.insertSelective(item);
                }
            }
        }else {
            //不启用规格,一个商品对应一个库存信息
            Item item = new Item();
            item.setTitle(goods.getGoodsName()+""+goods.getCaption());
            item.setPrice(goods.getPrice());
            item.setNum(999);
            item.setSpec("{}");
            item.setIsDefault("1");
            //设置库存表的属性
            setAttributeForItem(goods,goodsDesc,item);
            itemDao.insertSelective(item);
        }
    }

    //待审核商品列表查询
    @Override
    public PageResult searchForManager(Integer page, Integer rows, Goods goods) {
        //设置分页条件
        PageHelper.startPage(page,rows);
        //设置查询条件
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();

        if(goods.getAuditStatus()!=null&&!"".equals(goods.getAuditStatus().trim())){
            criteria.andAuditStatusEqualTo(goods.getAuditStatus().trim());
        }
        //查询未删除的商品
        criteria.andIsDeleteIsNull();
        //根据id降序
        goodsQuery.setOrderByClause("id desc");
        //查询
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    //商品审核
    @Transactional
    @Override
    public void updateStatus(Long[] ids, String status) {
        if(ids!=null&&ids.length>0){
            Goods goods = new Goods();
            goods.setAuditStatus(status);
            for (Long id : ids) {
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
                if ("1".equals(status)){
                    //审核通过
                    //TODO:将商品保存到索引库
                    //合理业务:将审核通过后的商品数据保存到索引库
                    //为了明天的搜索,将全部数据保存到索引库
                    dataImportItemToSolr();
                    //TODO:生成商品详情的静态页
                }
            }
        }
    }

    //将数据库中的数据保存到索引库
    private void dataImportItemToSolr() {
        //查询所有SKU
        List<Item> items = itemDao.selectByExample(null);
        if(items!=null&&items.size()>0){
            for (Item item : items) {
                //处理动态字段
                String spec = item.getSpec();
                Map map = JSON.parseObject(spec, Map.class);
                item.setSpecMap(map);
            }
            solrTemplate.saveBeans(items);
            solrTemplate.commit();
        }
    }

    //商品删除
    @Transactional
    @Override
    public void delete(Long[] ids) {
        if(ids!=null&&ids.length>0){
            Goods goods = new Goods();
            goods.setIsDelete("1");
            for (Long id : ids) {
                goods.setId(id);
                goodsDao.updateByPrimaryKeySelective(goods);
                //TODO:更新索引库
                //TODO:删除商品详情的静态页
            }
        }
    }
}
