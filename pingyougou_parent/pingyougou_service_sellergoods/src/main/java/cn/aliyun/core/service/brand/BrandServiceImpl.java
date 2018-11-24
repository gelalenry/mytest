package cn.aliyun.core.service.brand;
import cn.aliyun.core.dao.good.BrandDao;
import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.good.Brand;
import cn.aliyun.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
@Service
public class BrandServiceImpl implements BrandService {
    @Resource
    private BrandDao brandDao;
    //查询所有商品
    @Override
    public List<Brand> findAll(){
        return brandDao.selectByExample(null);
    }

    //分页查询
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        //设置分页参数
        PageHelper.startPage(pageNum,pageSize);
        //查询结果集
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);
        //创建pageResult并填充数据
        return new PageResult(page.getTotal(),page.getResult());
    }

    //条件查询
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Brand brand) {
        //设置分页参数
        PageHelper.startPage(pageNum,pageSize);
        //设置查询条件
        BrandQuery brandQuery=new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        //封装品牌的名称
        if(brand.getName()!=null&&!"".equals(brand.getName().trim())){
            //条件封装,拼接sql语句,没有%
            criteria.andNameLike("%"+brand.getName().trim()+"%");
        }
        if(brand.getFirstChar()!=null&&!"".equals(brand.getFirstChar().trim())){
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
        }
        //根据ID降序
        brandQuery.setOrderByClause("id desc");
        //根据条件查询
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //保存品牌
    @Transactional
    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    //修改品牌数据
    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    @Transactional
    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Transactional
    @Override
    public void del(Long[] ids) {
        if(ids!=null&&ids.length>0){
//            for (Long id : ids) {
//                brandDao.deleteByPrimaryKey(id);//缺点:频繁连接数据库提交事物,效率低
//            }
            brandDao.deleteByPrimaryKeys(ids);
        }
    }

    //模板需要的品牌结果集
    @Override
    public List<Map<String, String>> selectOptionList() {
        return brandDao.selectOptionList();
    }


}
