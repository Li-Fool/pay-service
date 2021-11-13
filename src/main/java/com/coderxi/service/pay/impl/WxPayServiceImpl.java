package com.coderxi.service.pay.impl;

import com.coderxi.service.pay.api.WxPayService;
import com.coderxi.service.pay.config.WxPayConfigProperties;
import com.coderxi.service.pay.vo.TradeCloseResult;
import com.coderxi.service.pay.vo.TradeCreateResult;
import com.coderxi.service.pay.vo.TradeRefundResult;
import com.coderxi.service.pay.vo.TradeStatusResult;
import com.coderxi.service.pay.websocket.PayWebSocket;
import com.coderxi.util.MapBuilder;
import com.github.wxpay.sdk.WXPay;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * 微信支付接口实现
 *
 * @author 汐涌及岸
 */
@Slf4j
@RestController
public class WxPayServiceImpl implements WxPayService {

  @Autowired
  private WXPay wxpay;

  @Autowired
  private WxPayConfigProperties properties;

  @Override
  @SneakyThrows
  public TradeCreateResult createTrade(String outTradeNo, Integer total) {
    //请求接口 https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_1
    Map<String, String> params = MapBuilder
      .pre("body", "微信付款测试")                     //订单描述
      .put("out_trade_no", outTradeNo)        //商户订单号
      .put("total_fee", total.toString())                //交易金额
      .put("fee_type", "CNY")                         //交易金额单位
      .put("spbill_create_ip", "127.0.0.1")           //终端IP
      .put("trade_type", "NATIVE")                    //交易类型:扫码支付
      .put("attach", "附加信息")                       //附加信息
      .put("notify_url", properties.getNotifyUrl())       //回调地址
      .end();
    //接口响应
    Map<String, String> response = wxpay.unifiedOrder(params);
    if ("FAIL".equals(response.get("return_code"))) {
      throw new RuntimeException(response.get("return_msg"));
    }
    //返回结果
    return TradeCreateResult.builder()
      .outTradeNo(response.get("out_trade_no"))
      .totalAmount(Integer.parseInt(response.get("total_fee")))
      .codeUrl(response.get("code_url"))
      .build();
  }

  @Override
  @SneakyThrows
  public TradeStatusResult showTrade(String outTradeNo) {
    //请求接口
    Map<String, String> params = MapBuilder
      .pre("out_trade_no", outTradeNo)
      .end();
    //接口响应
    Map<String, String> response = wxpay.orderQuery(params);
    //返回结果
    return "SUCCESS".equals(response.get("result_code"))
      //成功
      ? TradeStatusResult.builder()
      .outTradeNo(outTradeNo)
      .tradeNo(response.get("transaction_id"))
      .status(response.get("trade_state"))
      .payTime(response.containsKey("time_end") ? new SimpleDateFormat("yyyyMMddHHmmss").parse(response.get("time_end")) : null)
      .totalAmount(response.containsKey("total_fee") ? Integer.parseInt(response.get("total_fee")) : null)
      .cashAmount(response.containsKey("cash_fee") ? Integer.parseInt(response.get("cash_fee")) : null)
      .build()
      //失败
      : TradeStatusResult.builder()
      .outTradeNo(response.get("out_trade_no"))
      .status(response.get("err_code"))
      .build();
  }

  @Override
  @SneakyThrows
  public Map<String, String> notify(Map<String, String> params) {
    log.info("微信回调通知：{}", params);
    if (!wxpay.isPayResultNotifySignatureValid(params)) {
      throw new RuntimeException("验证失败！");
    }

    /*
      业务逻辑
      具体参数请参考 https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_7&index=8
     */

    //关闭ws
    PayWebSocket.close("wx", params.get("out_trade_no"));

    return MapBuilder
      .pre("return_code", "SUCCESS")
      .put("return_msg", "OK")
      .end();
  }

  @Override
  @SneakyThrows
  public TradeCloseResult closeTrade(String outTradeNo) {
    //请求接口 https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_3
    Map<String, String> params = MapBuilder
      .pre("out_trade_no", outTradeNo)
      .end();
    //接口响应
    Map<String, String> response = wxpay.closeOrder(params);
    //重新获取状态
    String status = "SUCCESS".equals(response.get("result_code"))
      ? showTrade(outTradeNo).getStatus()
      : response.get("result_code");
    //返回结果
    return TradeCloseResult.builder()
      .outTradeNo(response.get("out_trade_no"))
      .status(status)
      .build();
  }

  @Override
  @SneakyThrows
  public TradeRefundResult refundTrade(String outTradeNo, Integer refund, String outRequestNo) {
    TradeStatusResult tradeStatus = showTrade(outTradeNo);
    if (!"SUCCESS".equals(tradeStatus.getStatus()))
      throw new RuntimeException("订单还未完成！");
    //请求接口 https://pay.weixin.qq.com/wiki/doc/api/native.php?chapter=9_4
    //使用此接口，需要配置pay.wx.cert-path
    //我没有证书文件，不能进行退款测试
    //此接口可能不能正常使用
    Map<String, String> params = MapBuilder
      .pre("out_trade_no", outTradeNo)
      .put("out_refund_no", outRequestNo)
      .put("refund_fee", refund.toString())
      .put("total_fee", tradeStatus.getTotalAmount().toString())
      .end();
    //接口响应
    Map<String, String> response = wxpay.refund(params);
    //返回结果
    return TradeRefundResult.builder()
      .outTradeNo(response.get("out_trade_no"))
      .outRequestNo(response.get("out_request_no"))
      .refundAmount(Integer.parseInt(response.get("refund_fee")))
      .sendBackAmount(Integer.parseInt(response.get("cash_refund_fee")))
      .change("SUCCESS".equals(response.get("return_code")))
      .build();
  }
}
