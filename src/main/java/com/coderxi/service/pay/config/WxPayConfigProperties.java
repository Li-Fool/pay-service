package com.coderxi.service.pay.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.InputStream;

/**
 * 适配WXPayConfig
 */
@Data
@Component
@ConfigurationProperties(prefix = "pay.wx")
public class WxPayConfigProperties implements WXPayConfig {

  private String appID;
  private String mchID;
  private int httpConnectTimeoutMs = 5000;
  private int httpReadTimeoutMs = 5000;
  private String key;

  private String certPath;
  private String notifyUrl;

  @SneakyThrows
  public InputStream getCertStream() {
    return certPath == null ? null : ResourceUtils.getURL(certPath).openStream();
  }

}

