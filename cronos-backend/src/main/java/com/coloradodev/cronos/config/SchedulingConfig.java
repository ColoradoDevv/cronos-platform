package com.coloradodev.cronos.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Scheduling configuration for periodic/cron tasks.
 * Used for appointment reminders, cleanup jobs, etc.
 */
@Configuration
@EnableScheduling
@Slf4j
public class SchedulingConfig {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("Scheduled-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.setErrorHandler(t -> log.error("Error in scheduled task: {}", t.getMessage(), t));
        scheduler.initialize();

        log.info("Task scheduler initialized with pool size: {}", scheduler.getPoolSize());

        return scheduler;
    }
}
