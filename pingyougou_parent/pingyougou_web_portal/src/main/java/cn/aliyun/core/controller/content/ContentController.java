package cn.aliyun.core.controller.content;

import cn.aliyun.core.pojo.ad.Content;
import cn.aliyun.core.service.content.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("content")
public class ContentController {
    @Reference
    private ContentService contentService;
    //首页大广告轮播
    @RequestMapping("findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId){
        return contentService.findByCategoryId(categoryId);
    }
}
