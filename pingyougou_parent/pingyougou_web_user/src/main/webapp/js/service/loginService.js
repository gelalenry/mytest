//服务层
app.service("loginService",function ($http) {
    //读取数据列表绑定到表单中
    this.showName=function () {
        return $http.get("../login/name.do")
    }
})