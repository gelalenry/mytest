package cn.aliyun.core.vo;

import cn.aliyun.core.pojo.good.Goods;
import cn.aliyun.core.pojo.good.GoodsDesc;
import cn.aliyun.core.pojo.item.Item;


import java.io.Serializable;
import java.util.List;

public class GoodsVo implements Serializable {
    //商品信息
    private Goods goods;
    //商品明细
    private GoodsDesc goodsDesc;
    //库存信息
    private List<Item> itemList;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(GoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
