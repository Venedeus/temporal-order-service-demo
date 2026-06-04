package dev.shvetsov.application.service;

import dev.shvetsov.application.port.in.ProcessOrderUseCase;
import dev.shvetsov.application.port.out.OrderRepository;
import dev.shvetsov.domain.Order;
import dev.shvetsov.infrastructure.temporal.OrderProcessingWorkflow;
import io.micrometer.core.instrument.Timer;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class OrderProcessingService implements ProcessOrderUseCase {

  private final WorkflowClient workflowClient;
  private final OrderRepository orderRepository;
  private final MetricsService metricsService;

  public OrderProcessingService(WorkflowClient workflowClient,
      OrderRepository orderRepository,
      MetricsService metricsService) {
    this.workflowClient = workflowClient;
    this.orderRepository = orderRepository;
    this.metricsService = metricsService;
  }

  @Override
  public void startOrderProcessing(UUID orderId) {
    Timer.Sample timer = metricsService.startTimer();
    try {
      Order order = new Order(orderId);
      orderRepository.save(order);
      metricsService.recordOrderCreated();
      WorkflowOptions options = WorkflowOptions.newBuilder()
          .setTaskQueue("order-queue")
          .setWorkflowId("order-" + orderId)
          .build();
      OrderProcessingWorkflow workflow = workflowClient.newWorkflowStub(
          OrderProcessingWorkflow.class,
          options);
      WorkflowClient.start(workflow::processOrder, orderId.toString());
      metricsService.recordWorkflowStarted();
      log.info("Workflow started for order: {}", orderId);
    } finally {
      metricsService.stopTimer(timer, "order.startup.duration", "orderId", orderId.toString());
    }
  }

  @Override
  public void notifyPaymentCompleted(UUID orderId) {
    OrderProcessingWorkflow workflow = workflowClient.newWorkflowStub(
        OrderProcessingWorkflow.class,
        "order-" + orderId
    );
    workflow.paymentCompleted();
    metricsService.recordSignalReceived();
    log.info("Payment completed for order: {}", orderId);
  }

  @Override
  public void notifyInventoryReserved(UUID orderId) {
    OrderProcessingWorkflow workflow = workflowClient.newWorkflowStub(
        OrderProcessingWorkflow.class,
        "order-" + orderId
    );
    workflow.inventoryReserved();
    metricsService.recordSignalReceived();
    log.info("Inventory reserved for order: {}", orderId);
  }
}
