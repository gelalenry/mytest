package cn.aliyun.core.service.spec;
import cn.aliyun.core.dao.specification.SpecificationDao;
import cn.aliyun.core.dao.specification.SpecificationOptionDao;
import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.specification.Specification;
import cn.aliyun.core.pojo.specification.SpecificationOption;
import cn.aliyun.core.pojo.specification.SpecificationOptionQuery;
import cn.aliyun.core.pojo.specification.SpecificationQuery;

import cn.aliyun.core.vo.SpecificationVo;
import com.alibaba.dubbo.config.annotation.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationServiceImpl implements SpecificationService {
    @Resource
    private SpecificationDao specificationDao;
    @Resource
    private SpecificationOptionDao specificationOptionDao;
    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        //设置分页条件
        PageHelper.startPage(page,rows);
        //设置查询条件
        SpecificationQuery specificationQuery = new SpecificationQuery();
        if(specification.getSpecName()!=null&&!"".equals(specification.getSpecName().trim())){
            specificationQuery.createCriteria().andSpecNameLike("%"+specification.getSpecName().trim()+"%");
        }
        specificationQuery.setOrderByClause("id desc");
        //查询
        Page<Specification> p= (Page<Specification>) specificationDao.selectByExample(specificationQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    //保存规格
    @Transactional
    @Override
    public void add(SpecificationVo specificationVo) {
        Specification specification = specificationVo.getSpecification();
        specificationDao.insertSelective(specification);//返回自增主键的id
        //保存规格选项
        List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
        if(specificationOptionList!=null&&specificationOptionList.size()>0){
            for (SpecificationOption specificationOption : specificationOptionList) {
                //设置外键
                specificationOption.setSpecId(specification.getId());
                //插入
                //specificationOptionDao.insertSelective(specificationOption);
            }
            //批量插入
            specificationOptionDao.insertSelectives(specificationOptionList);
        }
    }

    @Override
    public SpecificationVo findOne(Long id) {
        SpecificationVo specificationVo = new SpecificationVo();
        //查询选项
        Specification specification = specificationDao.selectByPrimaryKey(id);
        //查询规格选项
        SpecificationOptionQuery optionQuery = new SpecificationOptionQuery();
        optionQuery.createCriteria().andIdEqualTo(id);
        List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(optionQuery);
        //封装数据
        specificationVo.setSpecification(specification);
        specificationVo.setSpecificationOptionList(specificationOptions);
        return specificationVo;
    }

    //更新商品数据
    @Transactional
    @Override
    public void update(SpecificationVo specificationVo) {
        //更新规格
        Specification specification = specificationVo.getSpecification();
        specificationDao.updateByPrimaryKeySelective(specification);
        //更新规格选项
        //先删除
        SpecificationOptionQuery optionQuery = new SpecificationOptionQuery();
        optionQuery.createCriteria().andIdEqualTo(specification.getId());
        specificationOptionDao.deleteByExample(optionQuery);
        //后插入
        List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
        if(specificationOptionList!=null&&specificationOptionList.size()>0){
            for (SpecificationOption specificationOption : specificationOptionList) {
                specificationOption.setSpecId(specification.getId());//外设主键
            }
            specificationOptionDao.insertSelectives(specificationOptionList);
        }
    }

    @Transactional
    @Override
    public void delete(Long[] ids) {
    if(ids!=null&&ids.length>0){
        for (long id : ids) {

            //删除规格选项
            SpecificationOptionQuery optionQuery = new SpecificationOptionQuery();
            optionQuery.createCriteria().andIdEqualTo(id);
            specificationOptionDao.deleteByExample(optionQuery);
            //删除规格
            specificationDao.deleteByPrimaryKey(id);
        }
    }
    }

    @Override
    public List<Map<String, String>> selectOptionList() {
        return specificationDao.selectOptionList();
    }
}
