package com.wc.config.swagger;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

    @Autowired
    private SwaggerProperties swaggerProperties;

    @Bean
    public Docket docket( ){
        Docket build = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
                .paths(PathSelectors.any())
                .build();
        build.securitySchemes(securityScheme())
                .securityContexts(securityContext());
        return  build;
    }
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder().contact(
                new Contact(swaggerProperties.getName(),
                        swaggerProperties.getUrl(),
                        swaggerProperties.getEmail()))
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion())
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl()).build();
    }
    private List<SecurityContext> securityContext(){
        return Arrays.asList(
                new SecurityContext(
                        Arrays.asList(new SecurityReference("Authorization",
                                new AuthorizationScope[]{
                                        new AuthorizationScope("global","accessResource")
                                }
                            )
                        ),
                        PathSelectors.any()
                )
        );
    }
    private List<SecurityScheme> securityScheme(){
        return Arrays.asList(
                new ApiKey("Authorization","Authorization","Authorization")
        );
    }
}
