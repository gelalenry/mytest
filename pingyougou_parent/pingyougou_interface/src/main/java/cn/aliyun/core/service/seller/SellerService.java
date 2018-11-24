package cn.aliyun.core.service.seller;

import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.seller.Seller;

public interface SellerService {
    //商家入驻
    public void add(Seller seller);
    //待审核商家的列表查询
    public PageResult search(Integer page,Integer rows,Seller seller);
    //商家详情审核
    public Seller findOne(String sellerId);
    //审核商家
    public void updateStatus(String sellerId,String status);
}
