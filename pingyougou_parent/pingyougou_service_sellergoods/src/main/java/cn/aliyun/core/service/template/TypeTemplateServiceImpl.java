package cn.aliyun.core.service.template;
import cn.aliyun.core.dao.specification.SpecificationOptionDao;
import cn.aliyun.core.dao.template.TypeTemplateDao;
import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.specification.SpecificationOption;
import cn.aliyun.core.pojo.specification.SpecificationOptionQuery;
import cn.aliyun.core.pojo.template.TypeTemplate;
import cn.aliyun.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {
    @Resource
    private TypeTemplateDao typeTemplateDao;
    @Resource
    private SpecificationOptionDao specificationOptionDao;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {
        //将模板中的品牌以及规格结果集放入到缓存中
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        if(typeTemplates!=null&&typeTemplates.size()>0){
            for (TypeTemplate template : typeTemplates) {
                //品牌结果集
                String brandIds = template.getBrandIds();
                List<Map> brandList = JSON.parseArray(brandIds, Map.class);
                //使用Redis的散列(hash)数据类型
                redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
                //规格选项结果集
                List<Map> specList = findBySpecList(template.getId());
                redisTemplate.boundHashOps("specList").put(template.getId(),specList);
            }
        }
        //设置分页
        PageHelper.startPage(page,rows);
        //设置查询条件
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        if (typeTemplate.getName()!=null&&!"".equals(typeTemplate.getBrandIds().trim())){
            typeTemplateQuery.createCriteria().andNameLike("%"+typeTemplate.getBrandIds().trim()+"%");
        }
        //根据id降序
            typeTemplateQuery.setOrderByClause("id desc");
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    //模板保存
    @Transactional
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    //商品添加之三级联动
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    //确定模板后加载的规格以及规格选项列表
    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        //将json串转换成对象
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        //根据规格获取规格选项
        for(Map map : maps){
            Long parseLong = Long.parseLong(map.get("id").toString());
            SpecificationOptionQuery optionQuery = new SpecificationOptionQuery();
            optionQuery.createCriteria().andSpecIdEqualTo(parseLong);
            List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(optionQuery);
            map.put("options",specificationOptions);
        }
        return maps;
    }
}
