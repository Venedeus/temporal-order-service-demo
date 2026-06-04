package dev.shvetsov.infrastructure.mapper;

import dev.shvetsov.domain.Order;
import dev.shvetsov.domain.OrderStatus;
import dev.shvetsov.infrastructure.entity.OrderEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
  public OrderEntity toEntity(Order order) {
    return new OrderEntity(
        order.getId(),
        OrderStatus.valueOf(order.getStatus().name()),
        order.getCreatedAt()
    );
  }

  public Order toDomain(OrderEntity entity) {
    Order order = new Order(entity.getId());
    switch (entity.getStatus()) {
      case APPROVED -> order.approve();
      case SHIPPED -> order.ship();
      case FAILED -> order.fail();
    }
    return order;
  }
}
