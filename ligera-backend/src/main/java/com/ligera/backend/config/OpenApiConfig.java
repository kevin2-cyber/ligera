package com.ligera.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * OpenAPI documentation configuration
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.api.version:1.0.0}")
    private String apiVersion;

    @Value("${app.api.title:Ligera Clothing Marketplace API}")
    private String apiTitle;

    @Value("${app.api.description:REST API for Ligera Clothing Marketplace}")
    private String apiDescription;

    @Value("${app.api.server.url:http://localhost:8080}")
    private String serverUrl;

    @Value("${app.api.server.description:Development Server}")
    private String serverDescription;

    /**
     * Configure OpenAPI documentation
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .externalDocs(externalDocs())
                .servers(servers())
                .components(securityComponents())
                .addSecurityItem(securityRequirement());
    }

    /**
     * Configure API info
     */
    private Info apiInfo() {
        return new Info()
                .title(apiTitle)
                .description(apiDescription)
                .version(apiVersion)
                .contact(new Contact()
                        .name("Ligera Development Team")
                        .email("dev@ligera.com")
                        .url("https://www.ligera.com"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"))
                .termsOfService("https://www.ligera.com/terms");
    }

    /**
     * Configure external documentation
     */
    private ExternalDocumentation externalDocs() {
        return new ExternalDocumentation()
                .description("Ligera Clothing Marketplace Documentation")
                .url("https://docs.ligera.com");
    }

    /**
     * Configure server information
     */
    private List<Server> servers() {
        return Arrays.asList(
                new Server()
                        .url(serverUrl)
                        .description(serverDescription),
                new Server()
                        .url("https://api.ligera.com")
                        .description("Production Server")
        );
    }

    /**
     * Configure security components
     */
    private Components securityComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", 
                        new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authorization header using the Bearer scheme. Example: 'Bearer {token}'"));
    }

    /**
     * Configure security requirement
     */
    private SecurityRequirement securityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }
}


