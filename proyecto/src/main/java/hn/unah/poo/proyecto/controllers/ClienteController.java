package hn.unah.poo.proyecto.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hn.unah.poo.proyecto.dtos.ClienteDTO;
import hn.unah.poo.proyecto.dtos.DireccionesDTO;
import hn.unah.poo.proyecto.servicios.ClienteServicio;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



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

    @PostMapping("/agregar/direccion/{dni}")
    public String agregarDireccionCliente(@PathVariable(name = "dni") String dni,@RequestBody DireccionesDTO direccionDTO){   
        return clienteServicio.agregarDireccion(dni, direccionDTO);
    }

    @GetMapping("/obtener/cliente/{dni}")
    public ClienteDTO obtenerClientePorId(@PathVariable(name = "dni") String dni) {
        return clienteServicio.buscarClientePorId(dni);
    }
    
    @GetMapping("/obtener/todos")
    public List<ClienteDTO> obtenerTodos() {
        return clienteServicio.obtenerTodos();
    }
    
    
}
