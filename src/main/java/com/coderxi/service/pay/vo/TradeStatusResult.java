package com.coderxi.service.pay.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TradeStatusResult {

  private String outTradeNo;
  private String tradeNo;
  private String status;
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date payTime;
  private Integer totalAmount;
  //实际支付
  private Integer cashAmount;

}
