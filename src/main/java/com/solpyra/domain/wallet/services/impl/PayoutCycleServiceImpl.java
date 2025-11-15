package com.solpyra.domain.wallet.services.impl;

import com.solpyra.common.dto.response.PageObject;
import com.solpyra.constant.ApplicationMessage.ErrorMessage;
import com.solpyra.constant.PayoutCycleStatus;
import com.solpyra.domain.wallet.dto.PayoutCycleDTO;
import com.solpyra.domain.wallet.dto.request.PayoutEvidence;
import com.solpyra.domain.wallet.repositories.PayoutCycleRepository;
import com.solpyra.domain.wallet.repositories.WalletRepository;
import com.solpyra.domain.wallet.repositories.WalletTransactionRepository;
import com.solpyra.domain.wallet.services.PayoutCycleService;
import com.solpyra.entities.PayoutCycle;
import com.solpyra.entities.QPayoutCycle;
import com.solpyra.entities.Wallet;
import com.solpyra.exception.NotFoundException;
import com.solpyra.util.Utils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  final StringEncryptor encryptorBean;

  /**
   * Create (or return existing) payout cycle for a given year & month. Idempotent: if exists ->
   * return existing without creating a new one.
   */
  public Optional<PayoutCycle> createIfAbsent(Wallet wallet, int year, int month) {
    return Optional.ofNullable(
        payoutCycleRepository.findByWalletIdAndPeriodYearAndPeriodMonth(wallet.getId(), year, month)
            .orElseGet(() -> {
              ZonedDateTime startAt = Utils.firstMomentOfMonth(year, month);
              ZonedDateTime endAt = Utils.lastMomentOfMonth(year, month);

              BigDecimal amount = walletTransactionRepository.getCommissionByTime(
                  wallet.getCustomerId(), startAt, endAt);

              if (Objects.isNull(amount)) {
                return null;
              }

              PayoutCycle cycle = PayoutCycle.builder()
                  .wallet(wallet)
                  .amount(amount.setScale(0, RoundingMode.DOWN))
                  .periodYear(year)
                  .periodMonth(month)
                  .startAt(startAt)
                  .endAt(endAt)
                  .status(PayoutCycleStatus.OPEN)
                  .createdAt(ZonedDateTime.now())
                  .build();

              PayoutCycle saved = payoutCycleRepository.save(cycle);
              log.info("Created payout cycle wallet={} {}/{} id={}", wallet, month, year,
                  saved.getId());
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

  @Override
  public PageObject findAllPayout(List<Long> status, Pageable pageable) {
    Page<PayoutCycle> page = payoutCycleRepository.findAll(pageable);

    return PageObject.builder()
        .page(page.getNumber())
        .pageSize(page.getSize())
        .list(page.getContent().stream()
            .map(payoutCycle -> {

              ZonedDateTime zonedDateTime = ZonedDateTime.of(payoutCycle.getPeriodYear(),
                  payoutCycle.getPeriodMonth(), 1, 0, 0, 0,
                  0, ZoneId.systemDefault());

              return PayoutCycleDTO.builder()
                  .id(payoutCycle.getId())
                  .paidImage(payoutCycle.getPaidImage())
                  .amount(payoutCycle.getAmount())
                  .userId(payoutCycle.getWallet().getCustomerId())
                  .period(zonedDateTime)
                  .status(payoutCycle.getStatus().toString())
                  .createAt(payoutCycle.getCreatedAt())
                  .build();
            })
            .toList())
        .totalPage(page.getTotalPages())
        .build();
  }

  /**
   * Optional helper: attach evidence (paid_image) and mark PAID/CLOSED when hoàn tất.
   */
  @Transactional
  @Override
  public void markPaidWithEvidence(PayoutEvidence evidence) throws NotFoundException {
    PayoutCycle cycle = payoutCycleRepository.findById(evidence.getId())
        .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FUND_PAYOUT));

    if(!cycle.getStatus().equals(PayoutCycleStatus.OPEN)) {
      throw new NotFoundException(ErrorMessage.NOT_FUND_PAYOUT);
    }

    cycle.setPaidImage(evidence.getPaidImage());
    cycle.setStatus(PayoutCycleStatus.PAID);
    cycle.setClosedAt(ZonedDateTime.now(ZoneOffset.systemDefault()));

    Wallet wallet = cycle.getWallet();
    wallet.setCommissionedBalance(wallet.getCommissionedBalance().add(cycle.getAmount()));
    wallet.setBalance(wallet.getBalance().subtract(cycle.getAmount()));

    payoutCycleRepository.save(cycle);
  }

  @Override
  public PageObject findUserPayout(String userId, List<Long> status, Pageable pageable) {
    QPayoutCycle qPayoutCycle = QPayoutCycle.payoutCycle;
    BigInteger id = new BigInteger(encryptorBean.decrypt(userId));

    Page<PayoutCycle> page = payoutCycleRepository.findAll(qPayoutCycle.wallet.customerId.eq(id),pageable);

    return PageObject.builder()
        .page(page.getNumber())
        .pageSize(page.getSize())
        .list(page.getContent().stream()
            .map(payoutCycle -> {

              ZonedDateTime zonedDateTime = ZonedDateTime.of(payoutCycle.getPeriodYear(),
                  payoutCycle.getPeriodMonth(), 1, 0, 0, 0,
                  0, ZoneId.systemDefault());

              return PayoutCycleDTO.builder()
                  .id(payoutCycle.getId())
                  .paidImage(payoutCycle.getPaidImage())
                  .amount(payoutCycle.getAmount())
                  .userId(payoutCycle.getWallet().getCustomerId())
                  .period(zonedDateTime)
                  .status(payoutCycle.getStatus().toString())
                  .createAt(payoutCycle.getCreatedAt())
                  .build();
            })
            .toList())
        .totalPage(page.getTotalPages())
        .build();
  }


}
