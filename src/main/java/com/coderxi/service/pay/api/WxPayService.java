package com.coderxi.service.pay.api;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * 微信支付接口
 * @author 汐涌及岸
 */
@RequestMapping("pay/wx")
public interface WxPayService extends PayService {

  /**
   * 微信支付结果通知
   * 接收返回xml
   */
  @Override
  @RequestMapping(value = "notify", consumes = {"text/xml", "application/xml"}, produces = {"text/xml", "application/xml"})
  Map<String, String> notify(@RequestBody Map<String, String> params);

}
