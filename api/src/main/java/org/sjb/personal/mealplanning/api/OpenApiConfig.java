package org.sjb.personal.mealplanning.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mealPlanningOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Meal Planning API")
                        .description("API for managing meals and planning")
                        .version("v1.0.0"));
    }
}