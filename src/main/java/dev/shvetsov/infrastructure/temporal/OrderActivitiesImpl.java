package dev.shvetsov.infrastructure.temporal;

import dev.shvetsov.application.port.out.OrderRepository;
import dev.shvetsov.domain.OrderStatus;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderActivitiesImpl implements OrderActivities {

  private final OrderRepository orderRepository;

  public OrderActivitiesImpl(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @Override
  public void validateOrder(String orderId) {
    log.info("Validating order: {}", orderId);
    UUID id = UUID.fromString(orderId);
    var order = orderRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Order not found"));
    if(order.getStatus() != OrderStatus.PENDING) {
      throw new RuntimeException("Order is not in pending state");
    }
    orderRepository.save(order);
    log.info("Order validated: {}", orderId);
  }

  @Override
  public void approveOrder(String orderId) {
    log.info("Approving order: {}", orderId);
    UUID id = UUID.fromString(orderId);
    var order = orderRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Order not found"));
    order.approve();
    orderRepository.save(order);
    log.info("Order approved: {}", orderId);
  }

  @Override
  public void shipOrder(String orderId) {
    log.info("Shipping order: {}", orderId);
    UUID id = UUID.fromString(orderId);
    var order = orderRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    order.ship();
    orderRepository.save(order);
    log.info("Order {} shipped successfully!", orderId);
  }

  @Override
  public void cancelOrder(String orderId, String reason) {
    log.info("Cancelling order: {}, reason: {}", orderId, reason);
    UUID id = UUID.fromString(orderId);
    var order = orderRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
    order.fail();
    orderRepository.save(order);
    log.info("Order cancelled: {}", orderId);
  }
}
