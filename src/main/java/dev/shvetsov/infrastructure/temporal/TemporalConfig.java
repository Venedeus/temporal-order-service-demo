package dev.shvetsov.infrastructure.temporal;

import io.temporal.client.WorkflowClient;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.WorkerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TemporalConfig {
  @Bean
  public WorkflowServiceStubs workflowServiceStubs() {
    return WorkflowServiceStubs.newInstance(
        WorkflowServiceStubsOptions.newBuilder()
            .setTarget("127.0.0.1:7233")
            .build()
    );
  }

  @Bean
  public WorkflowClient workflowClient(WorkflowServiceStubs workflowServiceStubs) {
    return WorkflowClient.newInstance(workflowServiceStubs);
  }

  @Bean
  public WorkerFactory workerFactory(WorkflowClient workflowClient) {
    return WorkerFactory.newInstance(workflowClient);
  }
}
