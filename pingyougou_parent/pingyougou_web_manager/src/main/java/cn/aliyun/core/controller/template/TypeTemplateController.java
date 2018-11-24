package cn.aliyun.core.controller.template;

import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.entity.Result;
import cn.aliyun.core.pojo.template.TypeTemplate;
import cn.aliyun.core.service.template.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    @Reference
    private TypeTemplateService typeTemplateService;
    //模板结果集
    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows, @RequestBody TypeTemplate typeTemplate){
        return typeTemplateService.search(page,rows,typeTemplate);
    }

    //保存模板
    @RequestMapping("add")
    public Result add(@RequestBody TypeTemplate typeTemplate){
        try{
            typeTemplateService.add(typeTemplate);
            return new Result(true,"保存成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }
}
