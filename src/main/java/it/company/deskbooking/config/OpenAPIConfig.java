package it.company.deskbooking.config;

import io.swagger.v3.oas.models.Components;
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

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI deskBookingOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:" + serverPort);
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setEmail("it@company.com");
        contact.setName("IT Team");

        License license = new License()
                .name("Proprietary")
                .url("https://company.it/license");

        Info info = new Info()
                .title("Desk Booking System API")
                .version("1.0.0")
                .description("API per la gestione delle prenotazioni di postazioni di lavoro e locker. " +
                        "Questo sistema permette di gestire piani dell'edificio, postazioni, dipartimenti, " +
                        "prenotazioni e assegnazioni locker.<br><br>" +
                        "<b>Autenticazione:</b> Usa /api/auth/login per ottenere il token JWT, " +
                        "poi clicca sul pulsante 'Authorize' e inserisci il token.")
                .contact(contact)
                .license(license);

        // Configurazione JWT Security Scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("Inserisci il token JWT ottenuto dal login (senza 'Bearer ')");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
