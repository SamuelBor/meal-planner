package org.sjb.personal.mealplanning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "org.sjb.personal.mealplanning")
@EntityScan(basePackages = "org.sjb.personal.mealplanning.core.domain")
@EnableJpaRepositories(basePackages = "org.sjb.personal.mealplanning.core.repository")
public class MealPlanningApplication {

    private static final Logger log = LoggerFactory.getLogger(MealPlanningApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MealPlanningApplication.class, args);
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        
        log.info("\n----------------------------------------------------------\n\t" +
                "Application is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:{}{}\n\t" +
                "Swagger UI: \thttp://localhost:{}{}/swagger-ui.html\n\t" +
                "OpenAPI Docs: \thttp://localhost:{}{}/v3/api-docs\n" +
                "----------------------------------------------------------",
                port, contextPath, port, contextPath, port, contextPath);
    }
}