package hn.unah.poo.proyecto.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hn.unah.poo.proyecto.dtos.DireccionesDTO;
import hn.unah.poo.proyecto.servicios.ClienteServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionesController {

    @Autowired
    private ClienteServicio clienteServicio;

     @Operation(summary = "Agregar una direccion a un cliente", description = "Permite agregar una nueva dirección al cliente identificado por su DNI." +
    " Debe recibir un JSON de direccionesDTO")
    @PostMapping("/agregar/direccion/{dni}")
    public String agregarDireccionCliente(@PathVariable(name = "dni") String dni,
            @RequestBody DireccionesDTO direccionDTO) {
        return clienteServicio.agregarDireccion(dni, direccionDTO);
    }

}
