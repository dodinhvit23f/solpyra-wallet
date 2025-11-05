package com.solpyra.domain.wallet.services;

import org.springframework.transaction.annotation.Transactional;

public interface PayoutCycleService {

  @Transactional
  void createLastMonthCycle();

}
