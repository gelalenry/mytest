package cn.aliyun.core.service.cart;

import cn.aliyun.core.pojo.cart.Cart;

import java.util.List;

public interface CartService {
    //添加商品到购物车
    public List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);

    //从redis中提取购物车
    public List<Cart> findCartListFromRedis(String username);

    //向redis中存购物车
    public void saveCartListToRedis(String username, List<Cart> cartList);

    //合并购物车
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2);
}
