package dev.shvetsov.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "dev.shvetsov")
@EntityScan(basePackages = "dev.shvetsov.infrastructure.entity")
@EnableJpaRepositories(basePackages = "dev.shvetsov.infrastructure.repository")
public class TemporalOrderServiceDemoApplication {

	static void main(String[] args) {
		SpringApplication.run(TemporalOrderServiceDemoApplication.class, args);
	}

}
