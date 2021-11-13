package com.coderxi.service.pay.api;

import com.coderxi.service.pay.vo.TradeCloseResult;
import com.coderxi.service.pay.vo.TradeCreateResult;
import com.coderxi.service.pay.vo.TradeRefundResult;
import com.coderxi.service.pay.vo.TradeStatusResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 支付服务父接口
 *
 * @author 汐涌及岸
 */
@RequestMapping("pay")
public interface PayService {

  /**
   * 创建交易
   *
   * @param outTradeNo 商户订单号
   * @param total      金额（单位:分）
   * @return 创建结果
   */
  @PostMapping("trade")
  TradeCreateResult createTrade(String outTradeNo, Integer total);

  /**
   * 查看交易状态
   *
   * @param outTradeNo 商户订单号
   * @return 交易状态
   */
  @GetMapping("trade")
  TradeStatusResult showTrade(String outTradeNo);

  /**
   * 支付结果通知
   */
  @RequestMapping("notify")
  Object notify(Map<String, String> params);

  /**
   * 关闭交易
   *
   * @param outTradeNo 商户订单号
   */
  @RequestMapping("trade/_close")
  TradeCloseResult closeTrade(String outTradeNo);

  /**
   * 退款
   *
   * @param outTradeNo 商户订单号
   */
  @RequestMapping("trade/_refund")
  TradeRefundResult refundTrade(String outTradeNo, Integer refund, String outRequestNo);

}
