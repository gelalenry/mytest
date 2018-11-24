package cn.aliyun.core.service.user;

import cn.aliyun.core.pojo.address.Address;

import java.util.List;

public interface AddressService {
    //收货地址
    public List<Address> findListByUserId(String userId);
}
