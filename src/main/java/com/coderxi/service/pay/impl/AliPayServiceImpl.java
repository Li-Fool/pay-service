package com.coderxi.service.pay.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.coderxi.service.pay.api.AliPayService;
import com.coderxi.service.pay.config.AliPayConfigProperties;
import com.coderxi.service.pay.vo.TradeCloseResult;
import com.coderxi.service.pay.vo.TradeCreateResult;
import com.coderxi.service.pay.vo.TradeRefundResult;
import com.coderxi.service.pay.vo.TradeStatusResult;
import com.coderxi.service.pay.websocket.PayWebSocket;
import com.coderxi.util.MapBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付宝支付接口实现
 *
 * @author 汐涌及岸
 */
@Slf4j
@RestController
public class AliPayServiceImpl implements AliPayService {

  @Autowired
  private AlipayClient alipay;
  @Autowired
  private AliPayConfigProperties properties;

  @Override
  @SneakyThrows(AlipayApiException.class)
  public TradeCreateResult createTrade(String outTradeNo, Integer total) {
    BigDecimal totalWithYuan = BigDecimal.valueOf(total).divide(BigDecimal.valueOf(100));
    //请求接口 https://opendocs.alipay.com/apis/api_1/alipay.trade.create
    AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
    //可以选择使用request.setBizModel(AlipayTradePrecreateModel)
    request.setBizContent(MapBuilder
      .pro("subject", "支付宝付款测试")          //订单描述
      .put("out_trade_no", outTradeNo)             //商户订单号
      .put("total_amount", totalWithYuan)          //交易金额
      .put("timeout_express", "30m")            //最晚付款时间
      .put("body", "附加信息")                  //附加信息
      .json());
    request.setNotifyUrl(properties.getNotifyUrl());  //回调地址
    //接口响应
    AlipayTradePrecreateResponse response = alipay.certificateExecute(request);
    if (!response.isSuccess()) {
      throw new RuntimeException("调用失败：" + response.getMsg() + "，" + response.getSubMsg());
    }
    //返回结果
    return TradeCreateResult.builder()
      .outTradeNo(response.getOutTradeNo())
      .totalAmount(total)
      .codeUrl(response.getQrCode())
      .build();
  }

  @Override
  @SneakyThrows(AlipayApiException.class)
  public TradeStatusResult showTrade(String outTradeNo) {
    //请求接口
    AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
    request.setBizContent(MapBuilder
      .pre("out_trade_no", outTradeNo)
      .json());
    //接口响应
    AlipayTradeQueryResponse response = alipay.certificateExecute(request);
    //返回结果
    return response.isSuccess()
      //成功
      ? TradeStatusResult.builder()
      .outTradeNo(response.getOutTradeNo())
      .tradeNo(response.getTradeNo())
      .status(response.getTradeStatus())
      .payTime(response.getSendPayDate())
      .totalAmount(Integer.parseInt(response.getReceiptAmount().replace(".", "")))
      .cashAmount(Integer.parseInt(response.getBuyerPayAmount().replace(".", "")))
      .build()
      //失败
      : TradeStatusResult.builder()
      .outTradeNo(response.getOutTradeNo())
      .status(response.getSubCode())
      .build();
  }

  @Override
  @SneakyThrows(AlipayApiException.class)
  public String notify(Map<String, String> params) {
    log.info("支付宝回调通知：{}", params);
    if (!AlipaySignature.rsaCertCheckV1(params, properties.getAlipayPublicCertPath(), properties.getCharset(), properties.getSignType())) {
      throw new RuntimeException("验证失败！");
    }

    /*
      业务逻辑
      具体参数参考 https://opendocs.alipay.com/apis/api_1/alipay.trade.create#%E8%A7%A6%E5%8F%91%E9%80%9A%E7%9F%A5%E7%A4%BA%E4%BE%8B
     */

    //关闭ws
    PayWebSocket.close("ali", params.get("out_trade_no"));
    return "SUCCESS";
  }

  @Override
  @SneakyThrows(AlipayApiException.class)
  public TradeCloseResult closeTrade(String outTradeNo) {
    //请求接口
    AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
    request.setBizContent(MapBuilder
      .pre("out_trade_no", outTradeNo)
      .json());
    //接口响应
    AlipayTradeCloseResponse response = alipay.certificateExecute(request);
    //重新获取状态
    String status = response.isSuccess()
      ? showTrade(outTradeNo).getStatus()
      : response.getSubCode();
    //返回结果
    return TradeCloseResult.builder()
      .outTradeNo(response.getOutTradeNo())
      .tradeNo(response.getTradeNo())
      .status(status)
      .build();
  }

  @Override
  @SneakyThrows(AlipayApiException.class)
  public TradeRefundResult refundTrade(String outTradeNo, Integer refund, String outRequestNo) {
    BigDecimal refundWithYuan = BigDecimal.valueOf(refund).divide(BigDecimal.valueOf(100));
    //请求接口
    AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
    request.setBizContent(MapBuilder
      .pro("out_trade_no", outTradeNo)
      .put("refund_amount", refundWithYuan)
      .put("out_request_no", outRequestNo)
      .json());
    //接口响应
    AlipayTradeRefundResponse response = alipay.certificateExecute(request);
    //返回结果
    return TradeRefundResult.builder()
      .outTradeNo(response.getOutTradeNo())
      .outRequestNo(outRequestNo)
      .refundAmount(Integer.parseInt(response.getRefundFee().replace(".", "")))
      .sendBackAmount(Integer.parseInt(response.getSendBackFee().replace(".", "")))
      .change("Y".equals(response.getFundChange()))
      .build();
  }
}
