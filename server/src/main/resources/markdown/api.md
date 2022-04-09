# 登录接口以及使用

登录接口的地址为 `/user/login`

可在登录接口调试的AfterScript写入下面的代码,实现登录完成的token自动填充至其他接口

``` javascript
//从header获取token
var token = ke.response.headers.authorization;
if(token !== null){
    //判断,如果服务端响应不为空
    //1、如何参数是Header，则设置当前逻辑分组下的全局Header
    ke.global.setHeader("Authorization", token);
}
```