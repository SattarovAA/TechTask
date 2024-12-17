package ru.effective.tms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class OpenAPIConfig {
    @Value("${app.openapi.dev-url}")
    private String devUrl;

    @Value("${app.openapi.prod-url}")
    private String prodUrl;

    /**
     * Bean {@link OpenAPI} for swagger-ui configure.
     *
     * @return {@link OpenAPI} for swagger-ui configure.
     */
    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

//        Server prodServer = new Server();
//        prodServer.setUrl(prodUrl);
//        prodServer.setDescription("Server URL in Production environment");

        Contact contact = new Contact();
        contact.setEmail("aa.sattarov@gmail.com");
        contact.setName("Alexey Sattarov");
//        contact.setUrl("https://www.bezkoder.com");
//
//        License mitLicense = new License()
//                .name("MIT License")
//                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Task Management System API.")
                .version("1.0")
                .contact(contact)
                .description("Task Management System.");
//                .termsOfService("https://www.bezkoder.com/terms")
//                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
}
