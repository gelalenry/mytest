//购物车服务层
app.service('cartService',function($http){
    //存购物车
    this.setCartList=function (cartList) {
        localStorage.setItem('cartList',JSON.stringify(cartList))
    };


    //清空购物车
    this.removeCartList=function () {
        localStorage.removeItem('cartList');
    };

//返回购物车列表
	this.getCartList=function () {
		var cartList=localStorage.getItem('cartList');
		if(cartList==null){
			return [];
		}else {
			return JSON.parse(cartList)
		}
    };


    //添加商品到购物车
	this.addGoodsToCartList=function (cartList,itemId,num) {
		return $http.post("cart/addGoodsToCartList.do?itemId="+itemId+"&num="+num,cartList);
    };


    //购物车列表
    this.findCartList=function (cartList) {
        return $http.post("cart/findCartList.do",cartList);
    }

    //合计数显示
	this.sum=function (cartList) {
		var totalValue={totalNum:0,totalMoney:0};
		for(var i=0;i<cartList.length;i++){
			var cart=cartList[i];
			for(var j=0;j<cart.orderItemList.length;j++){
				var orderItem=cart.orderItemList[j];
				totalValue.totalNum+=orderItem.num;//数量叠加
				totalValue.totalMoney+=orderItem.totalFee;//金额叠加
			}
		}
		return totalValue;
    }
});