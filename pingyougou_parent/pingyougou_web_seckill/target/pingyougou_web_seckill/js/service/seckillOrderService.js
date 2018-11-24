//服务层
app.service('seckillOrderService',function($http){
    //读取列表数据绑定到表单中
    this.submitOrder=function(seckillId){
        return $http.get('seckillOrder/submitOrder.do?seckillId='+seckillId);
    }
});