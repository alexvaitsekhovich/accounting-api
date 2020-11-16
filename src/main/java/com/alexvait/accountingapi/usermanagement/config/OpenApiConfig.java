package com.alexvait.accountingapi.usermanagement.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "Accounting API",
                version = "v1",
                description = "REST API for accounting, with user management and JWT token authentication",
                contact = @Contact(
                        name = "Alex Vait",
                        email = "alex.vaitsekhovich@gmail.com",
                        url = "https://github.com/alexvaitsekhovich"
                )

        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local host"
                )
        }
)
@SecurityScheme(
        name = "JWT Token",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        bearerFormat = "jwt",
        flows = @OAuthFlows(
                implicit = @OAuthFlow(
                        tokenUrl = "http://localhost:8080/gettoken"
                )
        )
)
public class OpenApiConfig {
}
