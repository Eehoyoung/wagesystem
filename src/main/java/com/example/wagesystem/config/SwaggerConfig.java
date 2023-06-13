package com.example.wagesystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("My API Documentation")
                        .description("This is a sample API documentation generated using Swagger.")
                        .version("1.0.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.wagesystem"))
                .paths(PathSelectors.any())
                .build();
    }
}
