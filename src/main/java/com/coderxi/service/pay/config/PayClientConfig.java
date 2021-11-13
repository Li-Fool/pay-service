package com.coderxi.service.pay.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PayClientConfig {

  @Bean
  WXPay wxPay(WXPayConfig config) {
    return new WXPay(config);
  }

  @Bean
  AlipayClient alipayClient(CertAlipayRequest certAlipayRequest) throws AlipayApiException {
    return new DefaultAlipayClient(certAlipayRequest);
  }

}
