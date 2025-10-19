package com.solpyra.domain.wallet.services;

import com.solpyra.domain.wallet.dto.CashBackOverall;
import com.solpyra.domain.wallet.dto.WalletBalance;
import java.security.Principal;

public interface WalletDashBoardService {

  CashBackOverall getCashBackOverall(Principal principal);

  WalletBalance getWalletBalance(Principal principal);
}
