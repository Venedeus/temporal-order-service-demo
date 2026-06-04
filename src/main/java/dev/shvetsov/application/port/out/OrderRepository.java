package dev.shvetsov.application.port.out;

import dev.shvetsov.domain.Order;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

  void save(Order order);

  Optional<Order> findById(UUID id);
}
