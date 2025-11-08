package com.solpyra.domain.wallet.dto.request;

import java.math.BigInteger;
import lombok.Data;

@Data
public class PayoutEvidence {
  private BigInteger id;
  private String paidImage;
  private String updatedUserName;
}
