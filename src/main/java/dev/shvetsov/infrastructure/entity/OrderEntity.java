package dev.shvetsov.infrastructure.entity;

import dev.shvetsov.domain.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {
  @Id
  private UUID id;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  private Instant createdAt;

  public OrderEntity() {

  }

  public OrderEntity(UUID id, OrderStatus status, Instant createdAt) {
    this.id = id;
    this.status = status;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
