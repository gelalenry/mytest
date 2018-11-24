package cn.aliyun.core.controller.cart;

import cn.aliyun.core.entity.LoginResult;
import cn.aliyun.core.pojo.cart.Cart;
import cn.aliyun.core.service.cart.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {
    @Reference
    private CartService cartService;

    @RequestMapping("addGoodsToCartList")
    public LoginResult addGoodsToCartList(@RequestBody List<Cart> cartList, Long itemId, Integer num) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录名
        System.out.println("当前登录用户" + username);
        if ("anonymousUser".equals(username)) {
            username = "";
        }
        try {

            if (!"".equals(username)) {//如果登录
                List<Cart> cartListFromRedis = cartService.findCartListFromRedis(username);
                List<Cart> carts = cartService.addGoodsToCartList(cartListFromRedis, itemId, num);
                cartService.saveCartListToRedis(username, carts);
                return new LoginResult(true, username, carts);
            }else{
                List<Cart> carts = cartService.addGoodsToCartList(cartList, itemId, num);
                return new LoginResult(true, username, carts);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResult(false, username, "添加失败");
        }

    }


    //查询购物车
    @RequestMapping("findCartList")
    public LoginResult findCartList(@RequestBody List<Cart> cartList) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();//获取当前登录名
        System.out.println("当前登录用户" + username);
        if ("anonymousUser".equals(username)) {//如果未登录
            return new LoginResult(true, "", cartList);
        } else {
            List<Cart> cartList_redis = cartService.findCartListFromRedis(username);//从redis中提取购物车
                //合并购物车
            if (cartList.size() > 0) {
                cartList_redis = cartService.mergeCartList(cartList, cartList_redis);
                cartService.saveCartListToRedis(username, cartList_redis);
            }
            return new LoginResult(true, username, cartList_redis);
        }
    }
}
