package dev.shvetsov.infrastructure.temporal;

import dev.shvetsov.application.port.out.OrderRepository;
import dev.shvetsov.application.service.MetricsService;
import dev.shvetsov.domain.OrderStatus;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderActivitiesImpl implements OrderActivities {

  private final OrderRepository orderRepository;
  private final MetricsService metricsService;

  public OrderActivitiesImpl(
      OrderRepository orderRepository,
      MetricsService metricsService) {
    this.orderRepository = orderRepository;
    this.metricsService = metricsService;
  }

  @Override
  public void validateOrder(String orderId) {
    metricsService.recordActivityExecution("validate", () -> {
      log.info("Validating order: {}", orderId);
      UUID id = UUID.fromString(orderId);
      var order = orderRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Order not found"));
      if (order.getStatus() != OrderStatus.PENDING) {
        throw new RuntimeException("Order is not in pending state");
      }
      orderRepository.save(order);
      log.info("Order validated: {}", orderId);
    });
  }

  @Override
  public void approveOrder(String orderId) {
    metricsService.recordActivityExecution("approve", () -> {
      log.info("Approving order: {}", orderId);
      UUID id = UUID.fromString(orderId);
      var order = orderRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Order not found"));
      order.approve();
      orderRepository.save(order);
      metricsService.recordOrderApproved();
      log.info("Order approved: {}", orderId);
    });
  }

  @Override
  public void shipOrder(String orderId) {
    metricsService.recordActivityExecution("ship", () -> {
      log.info("Shipping order: {}", orderId);
      UUID id = UUID.fromString(orderId);
      var order = orderRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
      order.ship();
      orderRepository.save(order);
      metricsService.recordOrderShipped();
      metricsService.recordWorkflowCompleted();
      log.info("Order {} shipped successfully!", orderId);
    });
  }

  @Override
  public void cancelOrder(String orderId, String reason) {
    metricsService.recordActivityExecution("cancel", () -> {
      log.info("Cancelling order: {}, reason: {}", orderId, reason);
      UUID id = UUID.fromString(orderId);
      var order = orderRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
      order.fail();
      orderRepository.save(order);
      metricsService.recordOrderCancelled();
      metricsService.recordWorkflowFailed();
      log.info("Order cancelled: {}", orderId);
    });
  }
}
