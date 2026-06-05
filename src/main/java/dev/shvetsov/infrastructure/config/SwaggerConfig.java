package dev.shvetsov.infrastructure.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Order Service API")
            .version("1.0.0")
            .description("""
                API for managing orders with Temporal workflow orchestration.
                
                ## Features
                - Create orders and start async processing
                - Send payment and inventory signals
                - Monitor order status and metrics
                - Track workflow progress
                
                ## Workflow Process
                1. Create order (returns orderId)
                2. System validates and approves order
                3. Send payment confirmation
                4. Send inventory reservation
                5. Order is shipped automatically
                """)
            .contact(new Contact()
                .name("Support Team")
                .email("support@example.com")
                .url("https://example.com"))
            .license(new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0")))
        .servers(List.of(
            new Server()
                .url("http://localhost:8081")
                .description("Development Server"),
            new Server()
                .url("https://api.example.com")
                .description("Production Server"))
        )
        .externalDocs(new ExternalDocumentation()
            .description("Temporal Workflow Documentation")
            .url("https://docs.temporal.io"));
  }
}