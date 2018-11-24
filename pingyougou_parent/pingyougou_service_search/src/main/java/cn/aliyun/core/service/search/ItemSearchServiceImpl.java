package cn.aliyun.core.service.search;
import cn.aliyun.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import javax.annotation.Resource;

import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Resource
    private SolrTemplate solrTemplate;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    //前台系统检索
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //根据关键字检索并且分页
        //Map<String, Object> resultMap= new HashMap<>();
        //Map<String,Object> map=searchForPage(searchMap);
        //根据关键字检索并分页且关键字高亮
        Map<String, Object> resultMap= new HashMap<>();
        //处理输入关键字中包含的空格
        String keywords = searchMap.get("keywords");
        if(keywords!=null&&!"".equals(keywords)){
            keywords = keywords.replace(" ", "");
            searchMap.put("keywords",keywords);
        }
        //商品结果集
        Map<String,Object> map=searchForHighLightPage(searchMap);
        resultMap.putAll(map);

        //商品分类列表categoryList
        List<String> categoryList=searchForGroupPage(searchMap);
        //默认加载第一个分类下的品牌以及规格
        if(categoryList!=null&&categoryList.size()>0){
            Map<String, Object> stringObjectMap = searchBrandListAndSpecListForCatagroy1(categoryList.get(0));
            resultMap.putAll(stringObjectMap);
            resultMap.put("categoryList",categoryList);
        }
        return resultMap;
    }

    //默认加载第一个分类下的品牌以及规格
    private Map<String, Object> searchBrandListAndSpecListForCatagroy1(String name) {
    //根据分类模板获取id
        Object typeId =redisTemplate.boundHashOps("itemCat").get(name);
        //获取品牌结果集
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        //获取规格结果集
        List<Map> specList= (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        //封装结果
        Map<String, Object> map = new HashMap<>();
        map.put("brandList",brandList);
        map.put("specList",specList);
        return map;
    }

    //查询商品分类
    private List<String> searchForGroupPage(Map<String, String> searchMap) {
    //封装检索条件
        String keywords = searchMap.get("keywords");
        Criteria criteria = new Criteria("item_keywords");
        if(keywords!=null&&!"".equals(keywords)){
            criteria.is(keywords);//模糊查询
        }
        SimpleQuery query = new SimpleQuery(criteria);
        //设置分组条件
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");//设置分组字段
        query.setGroupOptions(groupOptions);
        //根据条件查询
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(query, Item.class);
        //处理结果集
        List<String> categoryList = new ArrayList<>();
        GroupResult<Item> category = groupPage.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = category.getGroupEntries();
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue();
            //将分类的名称放入集合
            categoryList.add(groupValue);
        }
        return categoryList;
    }

    //根据关键字检索并分页且关键字高亮
    private Map<String, Object> searchForHighLightPage(Map<String, String> searchMap) {
        String keywords = searchMap.get("keywords");
        //设置检索条件
        //设置检索关键字
        Criteria criteria = new Criteria("item_keywords");
        //封装检索的条件
        if(keywords!=null&&!"".equals(keywords)){
            criteria.is(keywords);//is模糊查询
        }
        SimpleHighlightQuery query = new SimpleHighlightQuery(criteria);
        //设置分页
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer offset=(pageNo-1)*pageSize;
        query.setOffset(offset);//起始行
        query.setRows(pageSize);//每页显示的条数
        //设置关键字高亮,对检索的内容添加HTML标签
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title");//需要对哪个字段高亮
        highlightOptions.setSimplePrefix("<font color='red'>");//高亮起始
        highlightOptions.setSimplePostfix("</font>");//高亮结束===

        query.setHighlightOptions(highlightOptions);//设置高亮操作

        //添加过滤条件
        //商品分类过滤
        String category = searchMap.get("category");
        if(category!=null&&!"".equals(category)){
            Criteria cri = new Criteria("item_category");
            cri.is(category);//模糊查询
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
            query.addFilterQuery(filterQuery);
        }
//商品品牌过滤
        String brand = searchMap.get("brand");
        if(brand!=null&&!"".equals(brand)){
            Criteria crit = new Criteria("item_brand");
            crit.is(brand);
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(crit);
            query.addFilterQuery(simpleFilterQuery);
        }
// 商品规格过滤:{"网络":"4G","机身内存":"32G"}
        String spec = searchMap.get("spec");
        if(spec!=null&&!"".equals(spec)){
            Map<String,String> map = JSON.parseObject(spec, Map.class);
            Set<Map.Entry<String, String>> entries = map.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                Criteria criteria1 = new Criteria("item_spec_" + entry.getKey());
                criteria1.is(entry.getValue());
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(criteria1);
                query.addFilterQuery(simpleFilterQuery);
            }
        }
//商品价格过滤
        String price = searchMap.get("price");
        if(price!=null&&!"".equals(price)){
            String[] split = price.split("-");
            Criteria price1 = new Criteria("item_price");
            price1.between(split[0],split[1],true,true);
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(price1);
            query.addFilterQuery(simpleFilterQuery);
        }

        // 结果排序：新品、价格
        // 根据新品排序：sortField，排序字段   sort：排序规则
        String s1 = searchMap.get("sort");
        if(s1!=null&&!"".equals(s1)){
            if("ASC".equals(s1)){
                Sort sort = new Sort(Sort.Direction.ASC, "item_"+searchMap.get("sortField"));
                query.addSort(sort);
            }else {
                Sort sort = new Sort(Sort.Direction.DESC, "item_"+searchMap.get("sortField"));
                query.addSort(sort);
            }
        }

        //根据条件查询
        HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(query, Item.class);
        //处理高亮的结果
        List<HighlightEntry<Item>> highlighted = highlightPage.getHighlighted();
        if(highlighted!=null&&highlighted.size()>0){
            for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
                Item entity = itemHighlightEntry.getEntity();//普通结果集
                List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();//高亮结果集
                if(highlights!=null&&highlights.size()>0){
                    for (HighlightEntry.Highlight highlight : highlights) {
                        String s = highlight.getSnipplets().get(0);//高亮的结果集
                        entity.setTitle(s);
                    }
                }
            }
        }
        //处理封装结果集
        Map<String, Object> map = new HashMap<>();
        map.put("totalPages",highlightPage.getTotalPages());//总页数
        map.put("total",highlightPage.getTotalElements());//总条数
        map.put("rows",highlightPage.getContent());//结果集
        return map;
    }


    //关键字检索并分页
    private Map<String, Object> searchForPage(Map<String, String> searchMap) {
        String keywords = searchMap.get("keywords");
        //设置检索条件
        //设置检索关键字
        Criteria criteria = new Criteria("item_keywords");
        //封装检索的条件
        if(keywords!=null&&!"".equals(keywords)){
            criteria.is(keywords);//is模糊查询
        }
        SimpleQuery query = new SimpleQuery(criteria);
        //设置分页
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer offset=(pageNo-1)*pageSize;
        query.setOffset(offset);//起始行
        query.setRows(pageSize);//每页显示的条数
        //根据条件查询
        ScoredPage<Item> items = solrTemplate.queryForPage(query, Item.class);
        //处理封装结果集
        Map<String, Object> map = new HashMap<>();
        map.put("totalPages",items.getTotalPages());//总页数
        map.put("total",items.getTotalElements());//总条数
        map.put("rows",items.getContent());//结果集
        return map;
    }
}
