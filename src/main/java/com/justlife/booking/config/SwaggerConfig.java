package com.justlife.booking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI justlifeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Justlife Booking API")
                        .description("Booking Management API including vehicle assignment, professional allocation, and availability checks")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Justlife Support")
                                .email("support@justlife.com")
                                .url("https://justlife.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
