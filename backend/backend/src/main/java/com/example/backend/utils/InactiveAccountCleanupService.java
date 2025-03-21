package com.example.backend.utils;

import com.example.backend.models.User;
import com.example.backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class InactiveAccountCleanupService implements SchedulingConfigurer {
    @Autowired
    private UserRepository userRepository;

    @Value("${cleanup.cutoff-days:30}")
    private int cutoffDays;

    @Value("${cleanup.cron:0 59 23 L * ?}")
    private String cronExpression;

    @Value("${cleanup.timezone:Europe/Belgrade}")
    private String timeZone;

    private static final Logger logger = LoggerFactory.getLogger(InactiveAccountCleanupService.class);

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(
                this::deleteUnverifiedAccounts,
                triggerContext -> {
                    CronTrigger trigger = new CronTrigger(cronExpression, ZoneId.of(timeZone));
                    return trigger.nextExecution(triggerContext);
                }
        );
    }
    @Transactional
    public void deleteUnverifiedAccounts() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(timeZone));
        LocalDateTime cutoffDate = now.minusDays(cutoffDays);


        List<User> toDelete = userRepository.findByEnabledFalseAndCreatedAtBefore(cutoffDate);

        if (!toDelete.isEmpty()) {
            userRepository.deleteAll(toDelete);
            logger.info("✅ Deleted {} unactivated user(s) registered before {}.", toDelete.size(), cutoffDate);
        } else {
            logger.info("ℹ️ No unverified users found for deletion.");
        }
    }
}
