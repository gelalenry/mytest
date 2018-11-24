package cn.aliyun.core.service.template;

import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate);

    //模板保存
    public void add(TypeTemplate typeTemplate);

    //商品添加之三级联动
    TypeTemplate findOne(Long id);

    //商品规格数据初始化
    public List<Map> findBySpecList(Long id);
}
