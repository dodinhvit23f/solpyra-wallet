package com.solpyra.domain.wallet.services.impl;

import com.solpyra.domain.wallet.dto.CashBackOverall;
import com.solpyra.domain.wallet.dto.WalletBalance;
import com.solpyra.domain.wallet.repositories.WalletRepository;
import com.solpyra.domain.wallet.repositories.WalletTransactionRepository;
import com.solpyra.domain.wallet.services.WalletDashBoardService;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletDashBoardServiceImpl  implements WalletDashBoardService {

  final WalletTransactionRepository walletTransactionRepository;
  final WalletRepository walletRepository;

  @Override
  public CashBackOverall getCashBackOverall(Principal principal) {
    BigInteger userId = new BigInteger(principal.getName());
    BigDecimal currentMonth = BigDecimal.ZERO;
    BigDecimal previousMonth = BigDecimal.ZERO;

    ZonedDateTime now = ZonedDateTime.now();

    ZonedDateTime firstDay = now
        .with(TemporalAdjusters.firstDayOfMonth())
        .toLocalDate()
        .atStartOfDay(now.getZone());

    ZonedDateTime lastDay = now
        .with(TemporalAdjusters.lastDayOfMonth())
        .toLocalDate()
        .atTime(23, 59, 59, 999_999_999)
        .atZone(now.getZone());

    currentMonth = walletTransactionRepository.getCommissionByTime(userId,firstDay, lastDay);
    previousMonth = walletTransactionRepository.getCommissionByTime(userId,firstDay.minusMonths(1), lastDay.minusMonths(1));


    return CashBackOverall.builder()
        .currentMonth(currentMonth)
        .previousMonth(previousMonth)
        .build();
  }

  @Override
  public WalletBalance getWalletBalance(Principal principal){
    BigInteger userId = new BigInteger(principal.getName());
    BigDecimal balance = walletRepository.getBalanceByCustomerId(userId);

    if(Objects.isNull(balance)) {
      balance = BigDecimal.ZERO;
    }

    return  WalletBalance.builder()
        .balance(balance)
        .build();
  }
}
