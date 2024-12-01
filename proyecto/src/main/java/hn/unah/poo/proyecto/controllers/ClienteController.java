package hn.unah.poo.proyecto.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hn.unah.poo.proyecto.dtos.ClienteDTO;
import hn.unah.poo.proyecto.dtos.DireccionesDTO;
import hn.unah.poo.proyecto.servicios.ClienteServicio;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

     @Autowired
    private ClienteServicio clienteServicio;

    @Operation(summary = "Permite crear un nuevo registro para un cliente", description = "Crea un cliente puede recibir un JSON con la información completa")
    @PostMapping("/crear/nuevo")
    public String crearNuevoCliente(@RequestBody ClienteDTO nvoClienteDTO) {
        return clienteServicio.crearCliente(nvoClienteDTO);
    }


    @Operation(summary = "Agregar una direccion a un cliente", description = "Permite agregar una nueva dirección al cliente identificado por su DNI." +
    " Debe recibir un JSON de direccionesDTO")
    @PostMapping("/agregar/direccion/{dni}")
    public String agregarDireccionCliente(@PathVariable(name = "dni") String dni,@RequestBody DireccionesDTO direccionDTO){   
        return clienteServicio.agregarDireccion(dni, direccionDTO);
    }

    @Operation(summary = "Obtener información del cliente por DNI", description = "Permite recuperar la información completa de un cliente específico identificado por su DNI." +
    " Si el cliente no existe, se retornará un error.")
    @GetMapping("/obtener/cliente/{dni}")
    public Optional<ClienteDTO> obtenerClientePorId(@PathVariable(name = "dni") String dni) {
        return clienteServicio.buscarClientePorId(dni);
    }
    
    @Operation(summary = "Obtener todos los clientes registrados", description = "Permite recuperar la lista de todos los clientes registrados en la BD. "+
    " Cada cliente incluye datos personales, direcciones y prestamos.")
    @GetMapping("/obtener/todos")
    public List<ClienteDTO> obtenerTodos() {
        return clienteServicio.obtenerTodos();
    }


    @Operation(summary = "Elimina un cliente de la base de datos mediante su DNI",
    description = """
                  Elimina un cliente de la base de datos si cumple con las condiciones necesarias, como no tener pr\u00e9stamos con saldo pendiente. 
                  Si el cliente no existe o no puede ser eliminado, retorna un mensaje indicando la razon.""")
    @DeleteMapping("/eliminar/{dni}")
    public String eliminarClienteporDni(@PathVariable(name="dni") String dni){
        return this.clienteServicio.eliminarCliente(dni);

    }


    @Operation(summary = "Actualiza la información de un cliente existente en la base de datos",
    description = """
    Permite actualizar la informacion de un cliente identificado por su ID.
    Recibe un objeto `ClienteDTO` en el cuerpo de la solicitud con los datos actualizados.  
    Los campos permitidos para actualizar incluyen correo, sueldo, telefono y direcciones asociadas.  
    Retorna un mensaje indicando si la actualizacion fue exitosa o por qu\u00e9 no pudo realizarse.""")
    @PutMapping("/actualizar/{dni}")
    public String Actualizarcliente(@PathVariable(name="dni") String dni, 
                                @RequestBody ClienteDTO cliente) {
        return this.clienteServicio.actualizarCliente(dni, cliente);
    }
    
}
