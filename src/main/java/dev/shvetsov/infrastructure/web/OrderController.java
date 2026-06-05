package dev.shvetsov.infrastructure.web;

import dev.shvetsov.application.port.in.ProcessOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Endpoints for managing orders and workflow orchestration")
public class OrderController {

  private final ProcessOrderUseCase orderProcessing;

  public OrderController(ProcessOrderUseCase orderProcessing) {
    this.orderProcessing = orderProcessing;
  }

  @PostMapping
  @Operation(
      summary = "Create a new order",
      description = "Creates a new order and starts the Temporal workflow. The order will be processed asynchronously."
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Order created successfully",
              content = @Content(schema = @Schema(implementation = OrderCreatedResponse.class))),
          @ApiResponse(responseCode = "500", description = "Internal server error",
              content = @Content)
      }
  )
  public ResponseEntity<OrderCreatedResponse> createOrder() {
    UUID orderId = UUID.randomUUID();
    orderProcessing.startOrderProcessing(orderId);
    return ResponseEntity.ok(new OrderCreatedResponse(orderId.toString()));
  }

  @PostMapping("/{orderId}/payment")
  @Operation(
      summary = "Notify payment completion",
      description = "Sends a signal to the workflow that payment has been completed for the order."
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Payment notification sent"),
          @ApiResponse(responseCode = "404", description = "Order not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  public ResponseEntity<Void> paymentCompleted(
      @Parameter(description = "Order ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable UUID orderId) {
    orderProcessing.notifyPaymentCompleted(orderId);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{orderId}/inventory")
  @Operation(
      summary = "Notify inventory reservation",
      description = "Sends a signal to the workflow that inventory has been reserved for the order."
  )
  @ApiResponses(
      value = {
          @ApiResponse(responseCode = "200", description = "Inventory notification sent"),
          @ApiResponse(responseCode = "404", description = "Order not found"),
          @ApiResponse(responseCode = "500", description = "Internal server error")
      }
  )
  public void inventoryReserved(
      @Parameter(description = "Order ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
      @PathVariable UUID orderId) {
    orderProcessing.notifyInventoryReserved(orderId);
  }

  @Schema(description = "Response when creating a new order")
  public static class OrderCreatedResponse {

    @Schema(description = "Unique identifier of the created order", example = "550e8400-e29b-41d4-a716-446655440000")
    private final String orderId;

    public OrderCreatedResponse(String orderId) {
      this.orderId = orderId;
    }

    public String getOrderId() {
      return orderId;
    }
  }
}
