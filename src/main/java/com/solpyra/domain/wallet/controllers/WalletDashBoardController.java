package com.solpyra.domain.wallet.controllers;

import com.solpyra.common.dto.response.Response;
import com.solpyra.constant.Constant;
import com.solpyra.domain.wallet.dto.CashBackOverall;
import com.solpyra.domain.wallet.dto.WalletBalance;
import com.solpyra.domain.wallet.services.WalletDashBoardService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("wallet/v1/dashboard")
public class WalletDashBoardController {

    final WalletDashBoardService walletDashBoardService;

    @GetMapping("/cashback")
    public ResponseEntity<Response<CashBackOverall>> getCashback(Principal principal) {

      return ResponseEntity.ok(Response.<CashBackOverall>builder()
          .data(walletDashBoardService.getCashBackOverall(principal))
          .traceId(MDC.get(Constant.TRACE_ID))
          .build());
    }


  @GetMapping("/balance")
  public ResponseEntity<Response<WalletBalance>> getBalance(Principal principal) {
    return ResponseEntity.ok(Response.<WalletBalance>builder()
        .data(walletDashBoardService.getWalletBalance(principal))
        .traceId(MDC.get(Constant.TRACE_ID))
        .build());
  }

}
