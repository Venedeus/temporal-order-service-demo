package dev.shvetsov.infrastructure.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerGroupsConfig {

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public")
        .pathsToMatch("/api/**")
        .packagesToScan("dev.shvetsov.infrastructure.web")
        .build();
  }

  @Bean
  public GroupedOpenApi metricsApi() {
    return GroupedOpenApi.builder()
        .group("metrics")
        .pathsToMatch("/api/metrics/**")
        .build();
  }

  @Bean
  public GroupedOpenApi actuatorApi() {
    return GroupedOpenApi.builder()
        .group("actuator")
        .pathsToMatch("/actuator/**")
        .build();
  }
}
