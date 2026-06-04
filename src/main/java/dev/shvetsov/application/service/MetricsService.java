package dev.shvetsov.application.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

  private final MeterRegistry meterRegistry;
  private final Counter ordersCreatedCounter;
  private final Counter ordersApprovedCounter;
  private final Counter ordersShippedCounter;
  private final Counter ordersFailedCounter;
  private final Counter ordersCancelledCounter;
  private final Counter workflowStartedCounter;
  private final Counter workflowCompletedCounter;
  private final Counter workflowFailedCounter;
  private final Counter signalsReceivedCounter;
  private final Timer orderProcessingTimer;
  private final Timer activityExecutionTimer;
  private final AtomicInteger activeWorkflows = new AtomicInteger(0);
  private final AtomicInteger pendingOrders = new AtomicInteger(0);

  public MetricsService(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;

    this.ordersCreatedCounter = Counter.builder("orders.created.total")
        .description("Total number of orders created")
        .register(meterRegistry);

    this.ordersApprovedCounter = Counter.builder("orders.approved.total")
        .description("Total number of orders approved")
        .register(meterRegistry);

    this.ordersShippedCounter = Counter.builder("orders.shipped.total")
        .description("Total number of orders shipped")
        .register(meterRegistry);

    this.ordersFailedCounter = Counter.builder("orders.failed.total")
        .description("Total number of orders failed")
        .register(meterRegistry);

    this.ordersCancelledCounter = Counter.builder("orders.cancelled.total")
        .description("Total number of orders cancelled")
        .register(meterRegistry);

    this.workflowStartedCounter = Counter.builder("temporal.workflow.started.total")
        .description("Total number of workflows started")
        .register(meterRegistry);

    this.workflowCompletedCounter = Counter.builder("temporal.workflow.completed.total")
        .description("Total number of workflows completed")
        .register(meterRegistry);

    this.workflowFailedCounter = Counter.builder("temporal.workflow.failed.total")
        .description("Total number of workflows failed")
        .register(meterRegistry);

    this.signalsReceivedCounter = Counter.builder("temporal.signals.received.total")
        .description("Total number of signals received")
        .tag("type", "payment_and_inventory")
        .register(meterRegistry);

    this.orderProcessingTimer = Timer.builder("order.processing.duration")
        .description("Time taken to process an order from creation to completion")
        .register(meterRegistry);

    this.activityExecutionTimer = Timer.builder("temporal.activity.duration")
        .description("Duration of activity execution")
        .register(meterRegistry);
  }

  public void recordOrderCreated() {
    ordersCreatedCounter.increment();
    pendingOrders.incrementAndGet();
  }

  public void recordOrderApproved() {
    ordersApprovedCounter.increment();
  }

  public void recordOrderShipped() {
    ordersShippedCounter.increment();
    pendingOrders.decrementAndGet();
  }

  public void recordOrderFailed() {
    ordersFailedCounter.increment();
    pendingOrders.decrementAndGet();
  }

  public void recordOrderCancelled() {
    ordersCancelledCounter.increment();
    pendingOrders.decrementAndGet();
  }

  public void recordWorkflowStarted() {
    workflowStartedCounter.increment();
    activeWorkflows.incrementAndGet();
  }

  public void recordWorkflowCompleted() {
    workflowCompletedCounter.increment();
    activeWorkflows.decrementAndGet();
  }

  public void recordWorkflowFailed() {
    workflowFailedCounter.increment();
    activeWorkflows.decrementAndGet();
  }

  public void recordSignalReceived() {
    signalsReceivedCounter.increment();
  }

  public <T> T recordOrderProcessing(Runnable task) {
    return orderProcessingTimer.record(() -> {
          task.run();
          return null;
        }
    );
  }

  public void recordActivityExecution(String activityName, Runnable activity) {
    Timer.Sample sample = Timer.start(meterRegistry);
    try {
      activity.run();
    } finally {
      sample.stop(Timer.builder("temporal.activity." + activityName + ".duration")
          .description("Duration of " + activityName + " activity")
          .register(meterRegistry));
    }
  }

  public Timer.Sample startTimer() {
    return Timer.start(meterRegistry);
  }

  public void stopTimer(Timer.Sample sample, String name, String... tags) {
    Timer timer = Timer.builder(name)
        .tags(tags)
        .register(meterRegistry);
    sample.stop(timer);
  }
}
