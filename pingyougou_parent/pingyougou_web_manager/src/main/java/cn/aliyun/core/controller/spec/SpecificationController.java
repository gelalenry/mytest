package cn.aliyun.core.controller.spec;
import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.entity.Result;
import cn.aliyun.core.pojo.specification.Specification;
import cn.aliyun.core.service.spec.SpecificationService;
import cn.aliyun.core.vo.SpecificationVo;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/specification")
public class SpecificationController {
    @Reference
    private SpecificationService specificationService;
    //规格查询
    @RequestMapping("search")
    public PageResult search(Integer page, Integer rows,@RequestBody Specification specification){
    return specificationService.search(page,rows,specification);
    }

    //规格添加
    @RequestMapping("add")
    public Result add(@RequestBody SpecificationVo specificationVo) {
        try {
            specificationService.add(specificationVo);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败哦");
        }
    }

    //回显
    @RequestMapping("findOne")
    public SpecificationVo findOne(Long id){
        SpecificationVo specificationVo = specificationService.findOne(id);
        return specificationVo;
    }

    //更新
    @RequestMapping("update")
    public Result update(@RequestBody SpecificationVo specificationVo){
        try{
            specificationService.update(specificationVo);
            return new Result(true,"更新成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }

    //删除规格
    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try{
            specificationService.delete(ids);
            return new Result(true,"删除成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //模板需要的规格结果集
    @RequestMapping("selectOptionList")
    public List<Map<String,String>> selectOptionList(){
        return specificationService.selectOptionList();
    }
}
