package com.coderxi.service.pay.config;

import com.alipay.api.CertAlipayRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

/**
 * 适配支付宝公钥证书Request
 */
@Component
@ConfigurationProperties(prefix = "pay.ali")
public class AliPayConfigProperties extends CertAlipayRequest {

  @Getter
  @Setter
  private String notifyUrl;

  @Override
  public void setCertPath(String certPath) {
    super.setCertPath(absolutePath(certPath));
  }

  @Override
  public void setAlipayPublicCertPath(String alipayPublicCertPath) {
    super.setAlipayPublicCertPath(absolutePath(alipayPublicCertPath));
  }

  @Override
  public void setRootCertPath(String rootCertPath) {
    super.setRootCertPath(absolutePath(rootCertPath));
  }

  /**
   * 将resourceLocation改为absolutePath
   */
  @SneakyThrows
  private String absolutePath(String resourceLocation) {
    return ResourceUtils.getFile(resourceLocation).getAbsolutePath();
  }


}
