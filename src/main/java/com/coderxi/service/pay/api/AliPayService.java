package com.coderxi.service.pay.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 支付宝支付接口
 *
 * @author 汐涌及岸
 */
@RequestMapping("pay/ali")
public interface AliPayService extends PayService {

  /**
   * 支付宝支付结果通知
   */
  @Override
  String notify(@RequestParam Map<String, String> params);

}
