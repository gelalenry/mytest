package cn.aliyun.core.service.seller;
import cn.aliyun.core.dao.seller.SellerDao;
import cn.aliyun.core.entity.PageResult;
import cn.aliyun.core.pojo.seller.Seller;
import cn.aliyun.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.Date;

@Service
public class SellerServiceImpl implements SellerService {

    @Resource
    private SellerDao sellerDao;
    //商家入驻
    @Override
    public void add(Seller seller) {
    //设置商家审核状态
        seller.setStatus("0");//未审核
        //提交日期
        seller.setCreateTime(new Date());
        //商家密码加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(seller.getPassword());
        seller.setPassword(encode);
        sellerDao.insertSelective(seller);
    }

    //待审核商家的列表查询
    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        //设置分页
        PageHelper.startPage(page,rows);
        //查询待审核商家
        SellerQuery sellerQuery = new SellerQuery();
        if(seller.getStatus()!=null&&!"".equals(seller.getStatus().trim())){
            sellerQuery.createCriteria().andStatusEqualTo(seller.getStatus().trim());
        }
        Page<Seller> p= (Page<Seller>) sellerDao.selectByExample(sellerQuery);
        return new PageResult(p.getTotal(),p.getResult());
    }

    //商家详情审核
    @Override
    public Seller findOne(String sellerId) {
        return sellerDao.selectByPrimaryKey(sellerId);
    }

    //审核商家
    @Transactional
    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }
}
