package com.solpyra.domain.wallet.services.impl;

import com.solpyra.constant.PayoutCycleStatus;
import com.solpyra.domain.wallet.repositories.PayoutCycleRepository;
import com.solpyra.domain.wallet.repositories.WalletRepository;
import com.solpyra.domain.wallet.repositories.WalletTransactionRepository;
import com.solpyra.domain.wallet.services.PayoutCycleService;
import com.solpyra.entities.PayoutCycle;
import com.solpyra.entities.Wallet;
import com.solpyra.util.Utils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
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
  final WalletTransactionRepository walletTransactionRepository;
  final WalletRepository walletRepository;

  /**
   * Create (or return existing) payout cycle for a given year & month.
   * Idempotent: if exists -> return existing without creating a new one.
   */
  public Optional<PayoutCycle> createIfAbsent(Wallet wallet, int year, int month) {
    return Optional.ofNullable(payoutCycleRepository.findByWalletIdAndPeriodYearAndPeriodMonth(wallet.getId(), year, month)
        .orElseGet(() -> {
          ZonedDateTime startAt = Utils.firstMomentOfMonth(year, month);
          ZonedDateTime endAt = Utils.lastMomentOfMonth(year, month);

          BigDecimal amount = walletTransactionRepository.getCommissionByTime(wallet.getCustomerId(), startAt, endAt);

          if(Objects.isNull(amount)) {
            return null;
          }

          PayoutCycle cycle = PayoutCycle.builder()
              .wallet(wallet)
              .amount(amount)
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
        }));
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
      createIfAbsent(wallet, year, month).ifPresent(payoutCycle ->
        log.info("Ensure payout cycle exists for {}/{} (id={})",
            payoutCycle.getPeriodMonth(), payoutCycle.getPeriodYear(), wallet.getId())
      );
    });

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
