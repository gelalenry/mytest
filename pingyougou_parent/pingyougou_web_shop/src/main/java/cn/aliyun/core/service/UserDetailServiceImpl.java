package cn.aliyun.core.service;

import cn.aliyun.core.pojo.seller.Seller;
import cn.aliyun.core.service.seller.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

//自定义认证类
public class UserDetailServiceImpl implements UserDetailsService {
    //手动注入
    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    //判断该用户是否存在
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Seller seller = sellerService.findOne(username);
        if(seller != null && "1".equals(seller.getStatus())){ // 必须是审核通过后的商家
            Set<GrantedAuthority> authorities = new HashSet<>();
            //添加访问权限
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_SELLER");
            authorities.add(simpleGrantedAuthority);
            // 用户名称、密码、该用户的访问权限
            User user = new User(username, seller.getPassword(), authorities);
            return user;
        }
        return null;
    }
}
