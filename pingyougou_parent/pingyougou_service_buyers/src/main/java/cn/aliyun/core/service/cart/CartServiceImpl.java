package cn.aliyun.core.service.cart;

import cn.aliyun.core.dao.item.ItemDao;
import cn.aliyun.core.pojo.item.Item;
import cn.aliyun.core.pojo.cart.Cart;
import cn.aliyun.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品SKU  ID查询商品SKU信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        if(item==null){
            throw new RuntimeException("该商品不存在");
        }
        if(!"1".equals(item.getStatus())){
            throw new RuntimeException("该商品无效");
        }
        //2.获取商家ID
        String sellerId = item.getSellerId();
        //3.根据商家ID判断购物车列表是否存在该商家购物车信息
        Cart cart=searchCartBySellerId(cartList,sellerId);
        //4.如果购物车列表不存在该商家购物车
        if(cart==null){
            //4.1.创建新购物车
            cart=new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            //4.2.将该购物车信息添加到购物车列表
            OrderItem orderItem=createOrderItem(item,num);
            List<OrderItem> list = new ArrayList<>();
            list.add(orderItem);
            cart.setOrderItemList(list);
            cartList.add(cart);
        }else {
            //5.如果购物车列表存在该商家购物车
            OrderItem orderItem=searchOrderItemByItemId(cart.getOrderItemList(),itemId);
            //查询购物车明细列表是否存在该商品
            if(orderItem==null) {
                //5.1.如果没有,新增购物车明细
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            }else{
            //5.2.如果有,修改商品数量及商品总价
            orderItem.setNum(orderItem.getNum()+num);
            orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue()*orderItem.getNum()));
            //如果该明细的数量小于等于0,则从购物车明细列表移除
            if(orderItem.getNum()<=0){
            cart.getOrderItemList().remove(orderItem);
            }
            //当购物车对象的明细列表记录数为0，则移除该购物车对象
            if(cart.getOrderItemList().size()==0){
                cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    //从redis中取购物车
    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车数据"+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if (cartList==null){
            return new ArrayList<>();
        }
        return cartList;
    }

    //向redis中存购物车
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis中存购物车数据"+username);
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    //合并购物车
    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车");
        //实现思路:循环一个购物车,根据根据购物车中商品的数量及ID,添加到另一个购物车
        for (Cart cart : cartList2) {
             for (OrderItem orderItem:cart.getOrderItemList()){
                 cartList1=addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
             }
        }
        return cartList1;
    }


    //根据商品id(sku)查询购物车明细列表中的购物车明细对象
    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItemList, Long itemId) {
        for (OrderItem orderItem : orderItemList) {
            if(orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    //创建订单明细
    private OrderItem createOrderItem(Item item, Integer num) {
        if(num<=0){
            throw new RuntimeException("数量非法");
        }
        OrderItem orderItem = new OrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;
    }


    //判断购物车列表是否存在该商家购物车信息
    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
}
