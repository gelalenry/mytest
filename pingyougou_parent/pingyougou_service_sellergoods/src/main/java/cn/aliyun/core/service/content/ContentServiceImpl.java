package cn.aliyun.core.service.content;

import cn.aliyun.core.dao.ad.ContentDao;
import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.ad.Content;
import cn.aliyun.core.pojo.ad.ContentQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private ContentDao contentDao;
	@Resource
	private RedisTemplate redisTemplate;

	@Override
	public List<Content> findAll() {
		List<Content> list = contentDao.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Content> page = (Page<Content>)contentDao.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override

	public void add(Content content) {
		//清除缓存   新增广告时更新缓存
		clearCache(content.getCategoryId());
		contentDao.insertSelective(content);
	}

	//清除缓存的方法
	private void clearCache(Long categoryId) {
		redisTemplate.boundHashOps("content").delete(categoryId);
	}

	@Override
	public void edit(Content content) {
		// 清除缓存    修改广告时更新缓存
		// 判断广告的分类是否发生改变，不改变：删除  改变：本次和之前的数据都需要清空
		Long newCategoryId = content.getCategoryId();
		//在更新之前查出来
		Long oldCategoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();
		if(newCategoryId!=oldCategoryId){
			//分类发生改变,全部清空
			clearCache(newCategoryId);
			clearCache(oldCategoryId);
		}else {
			clearCache(oldCategoryId);
		}
		contentDao.updateByPrimaryKeySelective(content);
	}

	@Override
	public Content findOne(Long id) {
		Content content = contentDao.selectByPrimaryKey(id);
		return content;
	}

	@Override
	public void delAll(Long[] ids) {
		if(ids != null){
			for(Long id : ids){
				//清除缓存    删除广告时更新缓存
				Content content = contentDao.selectByPrimaryKey(id);
				clearCache(content.getCategoryId());
				contentDao.deleteByPrimaryKey(id);
			}
		}
	}

	//查询该分类下的广告列表
	@Override
	public List<Content> findByCategoryId(Long categoryId) {
		//从缓存中获取数据
		List<Content> list= (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
		//判断缓存中是否存在:不存在
		if(list==null){
			synchronized (this){
				list=(List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
		if(list==null){
			//从数据库中查询
			ContentQuery contentQuery = new ContentQuery();
			contentQuery.createCriteria().andCategoryIdEqualTo(categoryId);
			list = contentDao.selectByExample(contentQuery);
			//放入缓存库
			redisTemplate.boundHashOps("content").put(categoryId,list);
				}
			}
		}
		return list;
	}
}
