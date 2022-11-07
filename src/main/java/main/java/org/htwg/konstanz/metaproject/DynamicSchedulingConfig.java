package main.java.org.htwg.konstanz.metaproject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import main.java.org.htwg.konstanz.metaproject.configuration.Constants;
import main.java.org.htwg.konstanz.metaproject.entities.FailedINdigitTransfer;
import main.java.org.htwg.konstanz.metaproject.persistance.FailedINdigitTransferDAO;
import main.java.org.htwg.konstanz.metaproject.persistance.SystemVariableDAO;
import main.java.org.htwg.konstanz.metaproject.rest.ProjectEndpoint;
import main.java.org.htwg.konstanz.metaproject.services.INdigitApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class DynamicSchedulingConfig implements SchedulingConfigurer {

    private final static Logger log = LoggerFactory.getLogger(ProjectEndpoint.class);

    private final FailedINdigitTransferDAO failedINdigitTransferDAO;

    private final SystemVariableDAO systemVariableDAO;

    public DynamicSchedulingConfig(FailedINdigitTransferDAO failedINdigitTransferDAO, SystemVariableDAO systemVariableDAO) {
        this.failedINdigitTransferDAO = failedINdigitTransferDAO;
        this.systemVariableDAO = systemVariableDAO;
    }

    @Bean
    public Executor taskExecutor() {
        return Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());

        // add cron-job to the registrar that triggers the retry of transfer to INdigit
        // dynamic -> the time between those triggers can change while the system is still running
        taskRegistrar.addTriggerTask(
                () -> {
                    List<FailedINdigitTransfer> failedTransfers = failedINdigitTransferDAO.findAll();
                    try {
                        retryTransfers(failedTransfers);
                    } catch (INdigitApiService.HttpStatusCodeException | IOException e) {
                        throw new RuntimeException(e);
                    }
                },
                context -> { //Trigger
                    Optional<Date> lastCompletionTime = Optional.ofNullable(context.lastCompletionTime());
                    int milliSecondsUntilNextAttempt = Integer.parseInt(systemVariableDAO.findByKey(Constants.FAILED_INDIGIT_TRANSFER_INTERVAL).getValue());
                    Instant nextExecutionTime = lastCompletionTime.orElseGet(Date::new).toInstant().plusMillis(milliSecondsUntilNextAttempt);
                    return Date.from(nextExecutionTime);
                }
        );
    }

    private void retryTransfers(List<FailedINdigitTransfer> failedTransfers) throws INdigitApiService.HttpStatusCodeException, IOException {
        for (FailedINdigitTransfer failedINdigitTransfer : failedTransfers) {
            failedINdigitTransferDAO.retryTransfer(failedINdigitTransfer);
        }
    }

}
