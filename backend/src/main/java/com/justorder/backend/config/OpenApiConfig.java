package com.justorder.backend.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
            .components(new Components().addSecuritySchemes(securitySchemeName,
                new SecurityScheme()
                    .name("Authorization")
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            ))
            .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
            .info(new Info()
                .title("JustOrder API")
                .description("API documentation for the JustOrder backend")
                .version("v1")
            );
    }
}