package dev.shvetsov.infrastructure.temporal;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.failure.ActivityFailure;
import io.temporal.workflow.Workflow;
import java.time.Duration;

public class OrderProcessingWorkflowImpl implements OrderProcessingWorkflow {

  private boolean paymentDone = false;
  private boolean inventoryDone = false;
  private final OrderActivities activities = Workflow.newActivityStub(
      OrderActivities.class,
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(1000))
          .setRetryOptions(RetryOptions.newBuilder()
              .setMaximumAttempts(3)
              .build())
          .build()
  );

  @Override
  public void processOrder(String orderId) {
    try {
      activities.validateOrder(orderId);
      activities.approveOrder(orderId);
      Workflow.await(() -> paymentDone && inventoryDone);
      activities.shipOrder(orderId);
    } catch (ActivityFailure e) {
      activities.cancelOrder(orderId, e.getMessage());
      throw e;
    }
  }

  @Override
  public void paymentCompleted() {
    this.paymentDone = true;
  }

  @Override
  public void inventoryReserved() {
    this.inventoryDone = true;
  }
}
