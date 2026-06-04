package dev.shvetsov.infrastructure.temporal;

import io.temporal.client.WorkflowClient;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TemporalWorkerStarter {
  @Autowired
  private WorkflowClient workflowClient;

  @Autowired
  private OrderActivities orderActivities;

  @PostConstruct
  public void startWorker() {
    WorkerFactory factory = WorkerFactory.newInstance(workflowClient);
    Worker worker = factory.newWorker("order-queue");
    worker.registerWorkflowImplementationTypes(OrderProcessingWorkflowImpl.class);
    worker.registerActivitiesImplementations(orderActivities);
    factory.start();
    log.info("Temporal Worker started on task queue: order-queue");
  }
}
