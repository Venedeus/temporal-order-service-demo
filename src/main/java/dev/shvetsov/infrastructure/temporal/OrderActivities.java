package dev.shvetsov.infrastructure.temporal;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface OrderActivities {

  @ActivityMethod
  void validateOrder(String orderId);

  @ActivityMethod
  void approveOrder(String orderId);

  @ActivityMethod
  void shipOrder(String orderId);

  @ActivityMethod
  void cancelOrder(String orderId, String reason);
}
