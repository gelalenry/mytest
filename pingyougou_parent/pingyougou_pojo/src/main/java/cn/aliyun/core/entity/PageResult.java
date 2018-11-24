package cn.aliyun.core.entity;
import java.io.Serializable;
import java.util.List;

//需要实现序列化接口(1.网络传输;2.ORM框架缓存    好处:将数据写入磁盘,数据备份--防止容灾/灾备;二进制数据可以共享)
//分页对象,封装页面需要的数据
public class PageResult implements Serializable {
    private long total;//总条数
    private List rows;//结果集

    public PageResult(long total, List rows) {
        this.total = total;
        this.rows = rows;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List getRows() {
        return rows;
    }

    public void setRows(List rows) {
        this.rows = rows;
    }
}
