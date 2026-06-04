package dev.shvetsov.infrastructure.repository;

import dev.shvetsov.infrastructure.entity.OrderEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

}
