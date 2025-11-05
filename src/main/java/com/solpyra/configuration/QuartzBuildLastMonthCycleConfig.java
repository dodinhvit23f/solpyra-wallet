package com.solpyra.configuration;

import com.solpyra.domain.wallet.sheduler.BuildLastMonthCycleJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class QuartzBuildLastMonthCycleConfig {

    @Bean
    public JobDetail buildLastMonthCycleJobDetail() {
        return JobBuilder.newJob(BuildLastMonthCycleJob.class)
                .withIdentity("buildLastMonthCycleJob")
                .storeDurably() // important for replacement & clustering
                .build();
    }

    @Bean
    public Trigger buildLastMonthCycleTrigger(JobDetail buildLastMonthCycleJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(buildLastMonthCycleJobDetail)
                .withIdentity("buildLastMonthCycleTrigger")
                /*.withSchedule(
                        CronScheduleBuilder
                                .cronSchedule("0 0 1 2 * ?")  
                                // sec min hour day month dayOfWeek
                                // 01:00:00, 2nd day of every month
                                .inTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"))
                )*/
                .build();
    }
}
