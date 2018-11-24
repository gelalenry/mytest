package cn.aliyun.core.service.content;


import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.ad.Content;

import java.util.List;

public interface ContentService {

	public List<Content> findAll();

	public PageResult findPage(Content content, Integer pageNum, Integer pageSize);

	public void add(Content content);

	public void edit(Content content);

	public Content findOne(Long id);

	public void delAll(Long[] ids);

	//查询该分类下的广告列表
	public List<Content> findByCategoryId(Long categoryId);

}
