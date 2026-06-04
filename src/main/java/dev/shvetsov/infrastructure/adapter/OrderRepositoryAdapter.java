package dev.shvetsov.infrastructure.adapter;

import dev.shvetsov.application.port.out.OrderRepository;
import dev.shvetsov.domain.Order;
import dev.shvetsov.infrastructure.entity.OrderEntity;
import dev.shvetsov.infrastructure.mapper.OrderMapper;
import dev.shvetsov.infrastructure.repository.OrderJpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class OrderRepositoryAdapter implements OrderRepository {
  private final OrderJpaRepository jpaRepository;
  private final OrderMapper mapper;

  public OrderRepositoryAdapter(OrderJpaRepository jpaRepository, OrderMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public void save(Order order) {
    OrderEntity entity = mapper.toEntity(order);
    jpaRepository.save(entity);
  }

  @Override
  public Optional<Order> findById(UUID id) {
    return jpaRepository.findById(id)
        .map(mapper::toDomain);
  }
}
