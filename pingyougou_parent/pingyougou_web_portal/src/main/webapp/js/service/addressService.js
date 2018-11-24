//地址服务层
app.service('addressService',function ($http) {
    //获取收货列表
    this.findListByLoginUser=function () {
        return $http.get("address/findListByLoginUser.do")
    }
});