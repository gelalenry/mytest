app.controller('payController' ,function($scope ,$location,payService){
	
	
	$scope.createNative=function(){
		payService.createNative().success(
			function(response){
				
				//显示订单号和金额
				$scope.money= (response.total_fee/100).toFixed(2);
				$scope.out_trade_no=response.out_trade_no;
				
				//生成二维码
				 var qr=new QRious({
					    element:document.getElementById('qrious'),
						size:250,
						value:response.code_url,
						level:'H'
			     });
				 
				 queryPayStatus();//调用查询
				
			}	
		);	
	}
	
	//调用查询
	queryPayStatus=function(){
		payService.queryPayStatus($scope.out_trade_no).success(
			function(response){

                //alert( JSON.stringify(response) )

				if(response.flag){
					//实际中应该是收获地址选择页面(学员实现)
					location.href="paysuccess.html#?money="+$scope.money;
				}else{
					if(response.message=='二维码超时'){
						alert("超时");
                        location.href="payfail.html";
					}else{

						location.href="payfail.html";
					}
				}				
			}		
		);		
	}
	
	//获取金额
	$scope.getMoney=function(){
		return $location.search()['money'];
	}
	
});