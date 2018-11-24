package cn.aliyun.core.controller.search;

import cn.aliyun.core.service.search.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("itemsearch")
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;
    //前台系统检索
    @RequestMapping("search")
    public Map<String,Object> search(@RequestBody Map<String,String>searchMap){
        return itemSearchService.search(searchMap);
    }
}
