package dev.shvetsov.application.port.in;

import java.util.UUID;

public interface ProcessOrderUseCase {

  void startOrderProcessing(UUID orderId);

  void notifyPaymentCompleted(UUID orderId);

  void notifyInventoryReserved(UUID orderId);
}
