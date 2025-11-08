package com.solpyra.domain.wallet.controllers;

import com.solpyra.common.dto.response.Response;
import com.solpyra.constant.Constant;
import com.solpyra.domain.authentication.services.JwtService;
import com.solpyra.domain.wallet.dto.CashBackOverall;
import com.solpyra.domain.wallet.dto.WalletBalance;
import com.solpyra.domain.wallet.services.WalletDashBoardService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("wallet/v1/dashboard")
public class WalletDashBoardController {

    final WalletDashBoardService walletDashBoardService;
    final JwtService jwtService;

    @GetMapping("/cashback")
    public ResponseEntity<Response<CashBackOverall>> getCashback(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {

      Claims claims = jwtService.getClaims(header);

      return ResponseEntity.ok(Response.<CashBackOverall>builder()
          .data(walletDashBoardService.getCashBackOverall(claims.get(Constant.SALT).toString()))
          .traceId(MDC.get(Constant.TRACE_ID))
          .build());
    }


  @GetMapping("/balance")
  public ResponseEntity<Response<WalletBalance>> getBalance(@RequestHeader(HttpHeaders.AUTHORIZATION) String header) {

    Claims claims = jwtService.getClaims(header);

    return ResponseEntity.ok(Response.<WalletBalance>builder()
        .data(walletDashBoardService.getWalletBalance(claims.get(Constant.SALT).toString()))
        .traceId(MDC.get(Constant.TRACE_ID))
        .build());
  }

}
