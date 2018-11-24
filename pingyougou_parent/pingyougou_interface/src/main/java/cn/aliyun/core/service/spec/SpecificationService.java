package cn.aliyun.core.service.spec;

import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.specification.Specification;
import cn.aliyun.core.vo.SpecificationVo;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    //规格列表查询
    public PageResult search(Integer page,Integer rows,Specification specification);

    //规格添加
    public void add(SpecificationVo specificationVo);

    //规格回显
    public SpecificationVo findOne(Long id);

    //更新
    public void update(SpecificationVo specificationVo);

    //删除规格
    public void delete(Long[] ids);

    //模板需要的规格结果集
    List<Map<String,String>> selectOptionList();
}
