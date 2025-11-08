package com.solpyra.domain.wallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solpyra.constant.Constant;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PayoutCycleDTO {
  private BigInteger id;
  @JsonFormat(pattern = Constant.DATE_TIME_FORMAT, timezone = JsonFormat.DEFAULT_TIMEZONE)
  private ZonedDateTime period;
  private BigInteger userId;
  private BigDecimal amount;
  private String paidImage;
  private String status;
  @JsonFormat(pattern = Constant.DATE_TIME_FORMAT, timezone = JsonFormat.DEFAULT_TIMEZONE)
  private ZonedDateTime createAt;
}
