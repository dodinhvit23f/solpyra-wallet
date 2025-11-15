package com.solpyra.domain.wallet.controllers;

import com.solpyra.common.dto.response.PageObject;
import com.solpyra.common.dto.response.Response;
import com.solpyra.constant.Constant;
import com.solpyra.domain.authentication.services.JwtService;
import com.solpyra.domain.wallet.dto.request.PayoutEvidence;
import com.solpyra.domain.wallet.services.PayoutCycleService;
import com.solpyra.exception.NotFoundException;
import io.jsonwebtoken.Claims;
import java.math.BigInteger;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user/payout/v1")
public class UserPayoutController {

  final PayoutCycleService payoutCycleService;
  final JwtService jwtService;

  @GetMapping("/list")
  public ResponseEntity<Response<PageObject>> getPayoutCycle(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String header,
      @RequestParam(required = false) List<Long> status,
      Pageable pageable) {

    Claims claims = jwtService.getClaims(header);
    String userId = claims.get(Constant.SALT).toString();

    return ResponseEntity.ok(Response.<PageObject>builder()
        .data(payoutCycleService.findUserPayout(userId, status, pageable))
        .traceId(MDC.get(Constant.TRACE_ID))
        .build());
  }


}
