package com.solpyra.domain.wallet.services.impl;

import com.solpyra.constant.PayoutCycleStatus;
import com.solpyra.domain.wallet.repositories.PayoutCycleRepository;
import com.solpyra.domain.wallet.repositories.WalletRepository;
import com.solpyra.domain.wallet.services.PayoutCycleService;
import com.solpyra.entities.PayoutCycle;
import com.solpyra.entities.Wallet;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class PayoutCycleServiceImpl implements PayoutCycleService {
  final PayoutCycleRepository payoutCycleRepository;
  final WalletRepository walletRepository;

  /**
   * Create (or return existing) payout cycle for a given year & month.
   * Idempotent: if exists -> return existing without creating a new one.
   */
  public PayoutCycle createIfAbsent(Wallet wallet, int year, int month) {
    return payoutCycleRepository.findByWalletIdAndPeriodYearAndPeriodMonth(wallet.getId(), year, month)
        .orElseGet(() -> {
          ZonedDateTime startAt = firstMomentOfMonth(year, month);
          ZonedDateTime endAt = lastMomentOfMonth(year, month);

          PayoutCycle cycle = PayoutCycle.builder()
              .wallet(wallet)
              .periodYear(year)
              .periodMonth(month)
              .startAt(startAt)
              .endAt(endAt)
              .status(PayoutCycleStatus.OPEN)
              .createdAt(ZonedDateTime.now())
              .build();

          PayoutCycle saved = payoutCycleRepository.save(cycle);
          log.info("Created payout cycle wallet={} {}/{} id={}", wallet, month, year, saved.getId());
          return saved;
        });
  }

  /**
   * Convenience: create last month's cycle relative to now (in Asia/Ho_Chi_Minh). Example: run on
   * 2nd day monthly via Quartz.
   */
  @Override
  public void createLastMonthCycle() {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime lastMonth = now.minusMonths(1);
    int year = lastMonth.getYear();
    int month = lastMonth.getMonthValue();

    walletRepository.findAll().forEach(wallet -> {
      PayoutCycle payoutCycle = createIfAbsent(wallet, year, month);
      log.info("Ensure payout cycle exists for {}/{} (id={})",
          payoutCycle.getPeriodMonth(), payoutCycle.getPeriodYear(), wallet.getId());
    });

  }

  private ZonedDateTime firstMomentOfMonth(int year, int month) {
    return LocalDate.of(year, month, 1)
        .atStartOfDay(ZoneOffset.systemDefault())
        .withSecond(0).withNano(0);
  }

  private ZonedDateTime lastMomentOfMonth(int year, int month) {
    ZonedDateTime firstNextMonth = LocalDate.of(year, month, 1)
        .plusMonths(1)
        .atStartOfDay(ZoneOffset.systemDefault())
        .withSecond(0).withNano(0);
    // one nanosecond before next month
    return firstNextMonth.minusNanos(1);
  }

  /**
   * Optional helper: attach evidence (paid_image) and mark PAID/CLOSED when hoàn tất.
   */
  @Transactional
  public PayoutCycle markPaidWithEvidence(long cycleId, String paidImageUrl) {
    PayoutCycle cycle = payoutCycleRepository.findById(BigInteger.valueOf(cycleId))
        .orElseThrow(() -> new IllegalArgumentException("Payout cycle not found: " + cycleId));

    cycle.setPaidImage(paidImageUrl);
    cycle.setStatus(PayoutCycleStatus.PAID);
    cycle.setClosedAt(ZonedDateTime.now(ZoneOffset.systemDefault()));
    return payoutCycleRepository.save(cycle);
  }
}
