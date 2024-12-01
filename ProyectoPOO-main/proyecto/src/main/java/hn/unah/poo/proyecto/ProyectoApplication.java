package hn.unah.poo.proyecto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Microservicio Prestamo",
        version = "1.0.0",
        description = "API para gestionar préstamos",
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0"
        ),
        contact = @Contact(
            name = "Soporte de Microservicio",
            email = "soporte@poo.hn",
            url = "http://apirestdocumentation.hn"
        )
    ),
    servers = {
        @Server(description = "Ambiente Local", url = "http://localhost:8080/"),
        @Server(description = "Ambiente Dev expuesto por Apigateway", url = "http://dev.api.poo.hn"),
        @Server(description = "Ambiente QA expuesto por Apigateway", url = "http://qa.api.poo.hn"),
        @Server(description = "Ambiente Prod expuesto por Apigateway", url = "http://api.poo.hn")
    }
)
@SpringBootApplication
public class ProyectoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoApplication.class, args);
	}

}
