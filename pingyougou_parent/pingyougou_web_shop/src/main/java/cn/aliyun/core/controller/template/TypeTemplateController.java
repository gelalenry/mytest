package cn.aliyun.core.controller.template;

import cn.aliyun.core.pojo.template.TypeTemplate;
import cn.aliyun.core.service.template.TypeTemplateService;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("typeTemplate")
public class TypeTemplateController {
    @Reference
    private TypeTemplateService typeTemplateService;
    //商品添加之三级联动
    @RequestMapping("findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    //确定模板后加载的规格以及规格选项列表
    @RequestMapping("findBySpecList")
    public List<Map> findBySpecList(Long id){
        return typeTemplateService.findBySpecList(id);
    }
}
