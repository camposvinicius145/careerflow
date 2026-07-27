package com.careerflow.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI careerFlowOpenApi() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("CareerFlow API")
                                .description(
                                        "API REST para organizar candidaturas e acompanhar o funil de contratação.")
                                .version("v1")
                                .contact(
                                        new Contact()
                                                .name("Vinicius Campos")
                                                .url("https://github.com/camposvinicius145/careerflow"))
                                .license(
                                        new License()
                                                .name("MIT")
                                                .url("https://opensource.org/licenses/MIT")));
    }
}
