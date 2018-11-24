package cn.aliyun.core.service.goods;

import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.good.Goods;
import cn.aliyun.core.vo.GoodsVo;

public interface GoodsService {
    //保存商品
    public void add(GoodsVo goodsVo);

    //查询当前商家的商品列表
    public PageResult search(Integer page, Integer rows, Goods goods);

    //商品回显
    public GoodsVo findOne(Long id);

    //更新商品
    public void update(GoodsVo goodsVo);

    //待审核商品列表查询
    public PageResult searchForManager(Integer page,Integer rows,Goods goods);

    //商品审核
    public void updateStatus(Long[] ids,String status);

    //商品删除
    public void delete(Long[] ids);
}
