package com.solpyra.domain.wallet.services;

import com.solpyra.common.dto.response.PageObject;
import com.solpyra.domain.wallet.dto.request.PayoutEvidence;
import com.solpyra.exception.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface PayoutCycleService {

  @Transactional
  void createLastMonthCycle();

  PageObject findAllPayout(List<Long> status, Pageable pageable);

  @Transactional
  void markPaidWithEvidence(PayoutEvidence evidence) throws NotFoundException;

  PageObject findUserPayout(String userId, List<Long> status, Pageable pageable);
}
