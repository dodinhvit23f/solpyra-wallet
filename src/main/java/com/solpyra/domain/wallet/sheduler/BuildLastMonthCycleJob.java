package com.solpyra.domain.wallet.sheduler;

import com.solpyra.domain.wallet.services.PayoutCycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BuildLastMonthCycleJob implements Job {

    private final PayoutCycleService payoutCycleService;

    @Override
    public void execute(JobExecutionContext context) {
        payoutCycleService.createLastMonthCycle();
    }
}