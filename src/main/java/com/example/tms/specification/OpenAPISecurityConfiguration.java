package com.example.tms.specification;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info =@Info(
                title = "Task Management API",
                version = "1.0 SNAPSHOT",
                contact = @Contact(
                        name = "Pavel Gurin", email = "paho3998@mail.ru", url = "https://github.com/malebranchee?tab=repositories"
                ),
                license = @License(
                        name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"
                ),
                description = "${api.description}"
        ),
        servers = @Server(
                url = "${api.server.url}",
                description = "Production"
        )
)
public class OpenAPISecurityConfiguration {

}
