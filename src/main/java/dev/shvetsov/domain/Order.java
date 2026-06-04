package dev.shvetsov.domain;

import java.time.Instant;
import java.util.UUID;

public class Order {

  private final UUID id;
  private OrderStatus status;
  private final Instant createdAt;

  public Order(UUID id) {
    this.id = id;
    this.status = OrderStatus.PENDING;
    this.createdAt = Instant.now();
  }

  public UUID getId() {
    return id;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void approve() {
    if (status != OrderStatus.PENDING) {
      throw new IllegalStateException("Only validated orders can be approved");
    }
    this.status = OrderStatus.APPROVED;
  }

  public void ship() {
    if (status != OrderStatus.APPROVED) {
      throw new IllegalStateException("Only approved orders can be shipped");
    }
    this.status = OrderStatus.SHIPPED;
  }

  public void fail() {
    this.status = OrderStatus.FAILED;
  }
}
