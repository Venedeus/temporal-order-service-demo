package dev.shvetsov.infrastructure.temporal;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface OrderProcessingWorkflow {

  @WorkflowMethod
  void processOrder(String orderId);

  @SignalMethod
  void paymentCompleted();

  @SignalMethod
  void inventoryReserved();
}
