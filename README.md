# pay-service
Spring整合的微信、支付宝 支付模块
- [DEMO](http://81.68.233.56:11451)

## 运行

1. 修改`application.yml`的`notify-url`为自己服务器的地址，微信/支付宝会在支付完毕后通知此地址
2. 如果是本地运行，请记得使用内外映射/穿透工具
3. **配置的微信商家号是随便找的，没法退款，用1分测试测试就行了**
4. 支付宝使用的[沙箱环境](https://openhome.alipay.com/platform/appDaily.htm)，公钥证书模式，yml中配置的是本人的方便测试，如需配置，记得修改yml中的三个`cert-path`和对应的文件
5. 切换到正式环境，只需要修改网关接口`pay.ali.server-url`
6. 启动`PayApplication`，访问`你的服务器地址:端口号`

## 接口

|接口|路径|
|--|--|
|创建订单|POST `/pay/ali/trade`|
|查看订单|GET `/pay/ali/trade`|
|关闭订单|POST `/pay/ali/trade/_close`|
|订单退款|POST `/pay/ali/trade/_refund`|
|WebSocket|`/pay/ali/websocket`|

- 将`ali`替换为`wx`可直接切换成微信支付
- 具体传参请自行查看`com.coderxi.service.pay.api`包
- 传递的金额，单位是**分**
