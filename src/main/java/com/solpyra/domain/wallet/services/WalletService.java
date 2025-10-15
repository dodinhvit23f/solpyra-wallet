package com.solpyra.domain.wallet.services;

import com.solpyra.domain.wallet.dto.CommissionMessage;
import org.springframework.transaction.annotation.Transactional;

public interface WalletService {

  @Transactional
  void addCommission(CommissionMessage message);
}
