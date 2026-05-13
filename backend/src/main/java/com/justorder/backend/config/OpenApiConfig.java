package com.justorder.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("JustOrder API")
                .description("API documentation for the JustOrder backend")
                .version("v1")
            )
            .addTagsItem(new Tag().name("Admin").description("Administrator-only endpoints for managing admin operations."))
            .addTagsItem(new Tag().name("Allergens").description("Endpoints for retrieving and managing allergen data."))
            .addTagsItem(new Tag().name("Auth").description("Authentication endpoints for admin users and JWT token issuance."))
            .addTagsItem(new Tag().name("Customers").description("Endpoints for customer registration, orders, and dashboard analytics."))
            .addTagsItem(new Tag().name("Dishes").description("Endpoints for restaurant dish management, including creation, updates, and deletion."))
            .addTagsItem(new Tag().name("General").description("Basic API health check and general test endpoints."))
            .addTagsItem(new Tag().name("Orders").description("Endpoints to create and manage orders through checkout operations."))
            .addTagsItem(new Tag().name("Restaurants").description("API endpoints for restaurant registration, profile management, menus, and orders."))
            .addTagsItem(new Tag().name("Riders").description("Endpoints for rider registration, order assignment, and delivery workflows."))
            .addTagsItem(new Tag().name("Sessions").description("User, rider, and restaurant session login/logout endpoints."));
    }
}