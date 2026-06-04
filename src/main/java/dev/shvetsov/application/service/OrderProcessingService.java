package dev.shvetsov.application.service;

import dev.shvetsov.application.port.in.ProcessOrderUseCase;
import dev.shvetsov.application.port.out.OrderRepository;
import dev.shvetsov.domain.Order;
import dev.shvetsov.infrastructure.temporal.OrderProcessingWorkflow;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderProcessingService implements ProcessOrderUseCase {

  private final WorkflowClient workflowClient;
  private final OrderRepository orderRepository;

  public OrderProcessingService(WorkflowClient workflowClient, OrderRepository orderRepository) {
    this.workflowClient = workflowClient;
    this.orderRepository = orderRepository;
  }

  @Override
  public void startOrderProcessing(UUID orderId) {
    Order order = new Order(orderId);
    orderRepository.save(order);
    WorkflowOptions options = WorkflowOptions.newBuilder()
        .setTaskQueue("order-queue")
        .setWorkflowId("order-" + orderId)
        .build();
    OrderProcessingWorkflow workflow = workflowClient.newWorkflowStub(OrderProcessingWorkflow.class, options);
    WorkflowClient.start(workflow::processOrder, orderId.toString());
    log.info("Workflow started for order: {}", orderId);
  }

  @Override
  public void notifyPaymentCompleted(UUID orderId) {
    OrderProcessingWorkflow workflow = workflowClient.newWorkflowStub(
        OrderProcessingWorkflow.class,
        "order-" + orderId
    );
    workflow.paymentCompleted();
    log.info("Payment completed for order: {}", orderId);
  }

  @Override
  public void notifyInventoryReserved(UUID orderId) {
    OrderProcessingWorkflow workflow = workflowClient.newWorkflowStub(
        OrderProcessingWorkflow.class,
        "order-" + orderId
    );
    workflow.inventoryReserved();
    log.info("Inventory reserved for order: {}", orderId);
  }
}
