package com.coderxi.service.pay.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TradeCreateResult {

  private String outTradeNo;
  private String codeUrl;
  private Integer totalAmount;

}
