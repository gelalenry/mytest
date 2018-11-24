//购物车控制层
app.controller('cartController',function($scope,$location,cartService,addressService,orderService){
    $scope.loginname='';//当前登录名
//返回购物车列表
    //初始化
    $scope.init=function () {
        $scope.cartList = cartService.getCartList();//获取购物车列表
        var itemId = $location.search()['itemId'];
        var num = $location.search()['num'];
        //$location.search()['num']
        if (itemId != null && num != null) {
            $scope.addGoodsToCartList(itemId, num)//添加商品到购物车
        } else {//查询
            $scope.findCartList()//查询购物车
        }
        // cartService.findCartList($scope.cartList,itemId,num).success(function (response) {
        //     if (response.loginname==""){//用户未登录
        //         cartService.setCartList(response.data)//保存购物车到本地
        //     }
        //     $scope.cartList=response.data;
        // })
    };

    //查询购物车
    $scope.findCartList=function () {
        $scope.cartList=cartService.getCartList();//取出本地购物车
        cartService.findCartList($scope.cartList).success(function (response) {
            $scope.cartList=response.data;
            //如果用户登录,清除本地购物车
            if (response.loginname!=''){
                cartService.removeCartList()
            }
            $scope.loginname=response.loginname
        })
    };

    //添加商品到购物车
	$scope.addGoodsToCartList=function (itemId,num) {
		cartService.addGoodsToCartList($scope.cartList,itemId,num).success(function (response) {
			if(response.success){
			    //如果用户登录,清除本地购物车
                $scope.cartList=response.data;
			    if (response.loginname!=''){//用户未登录

                    $scope.findCartList()//查询购物车,目的是合并
                }else{
			        cartService.setCartList(response.data);//保存购物车
                }
                $scope.loginname=response.loginname
			}
        })
    };

    //合计数显示
	$scope.$watch("cartList",function (newValue,oldValue) {
		$scope.totalValue=cartService.sum($scope.cartList)
    });

    //获取收货列表
    $scope.findAddressList=function () {
        addressService.findListByLoginUser().success(function (response) {
            $scope.addressList=response;
            //默认地址选择
            for(var i=0;i<$scope.addressList.length;i++){
                if($scope.addressList[i].isDefault=='1'){
                        $scope.address=$scope.addressList[i];
                    break;
                }
            }
        })
    };

    //选择地址
    $scope.selectAddress=function (address) {
        $scope.address=address;
    };

    //判断某地址对象是不是当前的地址
    $scope.isSelectedAddress=function (address) {
        if ($scope.address==address){
            return true;
        }else{
            return false;
        }
    };

    //选择支付方式
    $scope.order={paymentType:'1'};
    $scope.selectPaymentType=function (type) {
        $scope.order.paymentType=type;
    };
    
    //收件人信息
    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;//收件人地址
        $scope.order.receiverMobile=$scope.address.mobile;//电话
        $scope.order.receiver=$scope.address.contact;//收件人
        orderService.submitOrder($scope.order).success(function (response) {
            if(response.flag){//如果成功
            if($scope.order.paymentType=='1') {//微信支付
                location.href="pay.html";
            }else{
                location.href="paysuccess.html";
            }
            }else {
                alert(response.message)
            }
        })
    }
});