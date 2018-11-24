package cn.aliyun.core.service.brand;
import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.good.Brand;
import java.util.List;
import java.util.Map;


public interface BrandService {
    //查询全部
    public List<Brand> findAll();
    //分页查询
    public PageResult findPage(Integer pageNum, Integer pageSize);
    //条件查询
    public PageResult search(Integer pageNum,Integer pageSize,Brand brand);
    //添加品牌
    public void add(Brand brand);
    //修改品牌
    public Brand findOne(Long id);
    //更新
    public void update(Brand brand);
    //删除
    public void del(Long[] ids);
    //模板品牌结果集
    public List<Map<String,String>> selectOptionList();
}
