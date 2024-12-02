package hn.unah.poo.proyecto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(info = @Info(title = "Proyecto POO (Prestamo)", version = "1.0.0", description = """
            API para gestionar préstamos.
            Este proyecto fue desarrollado como parte de una colaboración grupal para
            la asignatura de POO impartida por el ingeniero Harold Coello.

            **Equipo de Desarrollo:**
            - José Daniel Nuñez (20221001249)
            - Eduardo Gabriel Martínez Zelaya (20121010326)
            - Edgar David Vasquez Sanchez (20221004825)
            - Ronny Josué Posadas Díaz (20161002484)
        """, license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"), contact = @Contact(name = "Soporte del Microservicio", email = "emartinez@unah.hn", url = "http://api.whatsapp.com/send?phone=50431512355")), servers = {
        @Server(description = "Ambiente Local", url = "http://localhost:8080/")
})
@SpringBootApplication
public class ProyectoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoApplication.class, args);
    }

}
