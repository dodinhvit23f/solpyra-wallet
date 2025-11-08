package com.solpyra.domain.wallet.services.impl;

import com.solpyra.domain.wallet.dto.CashBackOverall;
import com.solpyra.domain.wallet.dto.WalletBalance;
import com.solpyra.domain.wallet.repositories.WalletRepository;
import com.solpyra.domain.wallet.repositories.WalletTransactionRepository;
import com.solpyra.domain.wallet.services.WalletDashBoardService;
import com.solpyra.util.Utils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletDashBoardServiceImpl  implements WalletDashBoardService {

  final WalletTransactionRepository walletTransactionRepository;
  final WalletRepository walletRepository;
  final StringEncryptor encryptorBean;

  @Override
  public CashBackOverall getCashBackOverall(String id) {
    BigInteger userId = new BigInteger(encryptorBean.decrypt(id));
    BigDecimal currentMonth = BigDecimal.ZERO;
    BigDecimal previousMonth = BigDecimal.ZERO;

    ZonedDateTime now = ZonedDateTime.now();

    ZonedDateTime firstDay = Utils.firstMomentOfMonth(now.getYear(), now.getMonthValue());
    ZonedDateTime lastDay = Utils.firstMomentOfMonth(now.getYear(), now.getMonthValue());

    currentMonth = walletTransactionRepository.getCommissionByTime(userId,firstDay, lastDay);
    previousMonth = walletTransactionRepository.getCommissionByTime(userId,firstDay.minusMonths(1), lastDay.minusMonths(1));

    return CashBackOverall.builder()
        .currentMonth(currentMonth)
        .previousMonth(previousMonth)
        .build();
  }

  @Override
  public WalletBalance getWalletBalance(String id){
    BigInteger userId = new BigInteger(encryptorBean.decrypt(id));
    BigDecimal balance = walletRepository.getBalanceByCustomerId(userId);

    if(Objects.isNull(balance)) {
      balance = BigDecimal.ZERO;
    }

    return  WalletBalance.builder()
        .balance(balance)
        .build();
  }
}
