package dev.shvetsov.infrastructure.web;

import dev.shvetsov.application.port.in.ProcessOrderUseCase;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
  private final ProcessOrderUseCase orderProcessing;

  public OrderController(ProcessOrderUseCase orderProcessing) {
    this.orderProcessing = orderProcessing;
  }

  @PostMapping
  public void createOrder() {
   UUID orderId = UUID.randomUUID();
   orderProcessing.startOrderProcessing(orderId);
  }

  @PostMapping("/{orderId}/payment")
  public void paymentCompleted(@PathVariable UUID orderId) {
    orderProcessing.notifyPaymentCompleted(orderId);
  }

  @PostMapping("/{orderId}/inventory")
  public void inventoryReserved(@PathVariable UUID orderId) {
    orderProcessing.notifyInventoryReserved(orderId);
  }
}
