package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig{

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI(
        ).info(new Info().title("Example API").description("这是一个员工接口文档").version("1.0.0")
                .contact(new Contact().name("gali").email("gali@qq.com"))
                .license(new License().name("apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html"))
        );
    }

    @Bean
    public GroupedOpenApi groupedOpenApi(){
        return GroupedOpenApi.builder().group("user").pathsToMatch("/**").build();
    }

}
