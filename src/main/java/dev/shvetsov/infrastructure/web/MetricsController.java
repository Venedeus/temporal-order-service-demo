package dev.shvetsov.infrastructure.web;

import io.micrometer.core.instrument.MeterRegistry;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
  private final MeterRegistry meterRegistry;

  public MetricsController(MeterRegistry meterRegistry) {
    this.meterRegistry = meterRegistry;
  }

  @GetMapping("/summary")
  public Map<String, Object> getMetricsSummary() {
    Map<String, Object> metrics = new HashMap<>();
    metrics.put("orders_created", getMetricValue("orders.created.total"));
    metrics.put("orders_approved", getMetricValue("orders.approved.total"));
    metrics.put("orders_shipped", getMetricValue("orders.shipped.total"));
    metrics.put("orders_failed", getMetricValue("orders.failed.total"));
    metrics.put("orders_cancelled", getMetricValue("orders.cancelled.total"));
    metrics.put("pending_orders", getMetricValue("orders.pending.current"));
    metrics.put("workflows_started", getMetricValue("temporal.workflow.started.total"));
    metrics.put("workflows_completed", getMetricValue("temporal.workflow.completed.total"));
    metrics.put("workflows_failed", getMetricValue("temporal.workflow.failed.total"));
    metrics.put("active_workflows", getMetricValue("workflow.active.current"));
    metrics.put("signals_received", getMetricValue("temporal.signals.received.total"));
    return metrics;
  }

  private double getMetricValue(String metricName) {
    var meter = meterRegistry.find(metricName).meter();
    if(meter != null) {
      return meter.measure().iterator().next().getValue();
    }
    return 0.0;
  }
}
