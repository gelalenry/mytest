package cn.aliyun.core.controller.brand;
import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.entity.Result;
import cn.aliyun.core.pojo.good.Brand;
import cn.aliyun.core.service.brand.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    //查询全部商品
    @RequestMapping("/findAll")
    public List<Brand> findAll(){
        return brandService.findAll();
    }

    //分页
    @RequestMapping("findPage")
    public PageResult findPage(Integer pageNum,Integer pageSize){
        return brandService.findPage(pageNum, pageSize);
    }

    //条件查询
    @RequestMapping("search")
    public PageResult search(Integer pageNum,Integer pageSize,@RequestBody Brand brand){
        return brandService.search(pageNum,pageSize,brand);
    }

    //添加商品
    @RequestMapping("add")
    public Result add(@RequestBody Brand brand){
        try{
        brandService.add(brand);
            return new Result(true,"保存成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"保存失败");
        }
    }

    //修改品牌数据
    @RequestMapping("findOne")
    public Brand findOne(Long id){
        return brandService.findOne(id);
    }

    //更新
    @RequestMapping("update")
    public Result update(@RequestBody Brand brand){
        try{
            brandService.update(brand);
            return new Result(true,"更新成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }

    //删除数据
    @RequestMapping("del")
    public Result del(Long[] ids){
        try{
            brandService.del(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //模板需要的品牌结果集
    @RequestMapping("selectOptionList")
    public List<Map<String,String>> selectOptionList(){
        return brandService.selectOptionList();
    }

}
