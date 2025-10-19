package com.solpyra.domain.wallet.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WalletBalance {
  private BigDecimal balance;
}
