package com.coderxi.service.pay.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TradeRefundResult {

  private String outTradeNo;
  private String outRequestNo;
  private boolean change;
  //对应的交易的订单金额
  // private Integer totalAmount;
  //已退款金额
  private Integer refundAmount;
  //退回金额
  private Integer sendBackAmount;

}
