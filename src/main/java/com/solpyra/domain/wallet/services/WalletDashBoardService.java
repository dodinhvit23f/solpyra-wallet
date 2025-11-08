package com.solpyra.domain.wallet.services;

import com.solpyra.domain.wallet.dto.CashBackOverall;
import com.solpyra.domain.wallet.dto.WalletBalance;

public interface WalletDashBoardService {

  CashBackOverall getCashBackOverall(String userId);

  WalletBalance getWalletBalance(String userId);
}
