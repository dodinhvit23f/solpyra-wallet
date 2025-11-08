package com.solpyra.domain.wallet.controllers;

import com.solpyra.common.dto.response.PageObject;
import com.solpyra.common.dto.response.Response;
import com.solpyra.constant.Constant;
import com.solpyra.domain.wallet.dto.request.PayoutEvidence;
import com.solpyra.domain.wallet.services.PayoutCycleService;
import com.solpyra.exception.NotFoundException;
import java.math.BigInteger;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("payout/v1")
public class PayoutCycleController {

  final PayoutCycleService payoutCycleService;

  @GetMapping("/list")
  public ResponseEntity<Response<PageObject>> getPayoutCycle(
      @RequestParam(required = false) List<Long> status,
      Pageable pageable) {
    return ResponseEntity.ok(Response.<PageObject>builder()
        .data(payoutCycleService.findAllPayout(status, pageable))
        .traceId(MDC.get(Constant.TRACE_ID))
        .build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Response<Object>> updatePayoutCycle(Principal principal, @RequestBody
  PayoutEvidence evidence, @PathVariable Long id) throws NotFoundException {
    evidence.setId(BigInteger.valueOf(id));
    evidence.setUpdatedUserName(principal.getName());

    payoutCycleService.markPaidWithEvidence(evidence);
    return ResponseEntity.ok(Response.<Object>builder()
        .traceId(MDC.get(Constant.TRACE_ID))
        .build());
  }

}
