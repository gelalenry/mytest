package cn.aliyun.core.service;

import cn.aliyun.core.dao.address.AddressDao;
import cn.aliyun.core.pojo.address.Address;
import cn.aliyun.core.pojo.address.AddressQuery;
import cn.aliyun.core.service.user.AddressService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressDao addressDao;
    @Override
    public List<Address> findListByUserId(String userId) {
        AddressQuery query = new AddressQuery();
        AddressQuery.Criteria criteria = query.createCriteria();
        criteria.andUserIdEqualTo(userId);
        return addressDao.selectByExample(query);
    }
}
