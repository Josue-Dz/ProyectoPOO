package hn.unah.poo.proyecto.controllers;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hn.unah.poo.proyecto.dtos.DireccionesDTO;
import hn.unah.poo.proyecto.servicios.ClienteServicio;
import hn.unah.poo.proyecto.servicios.DireccionesServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionesController {

    @Autowired
    private ClienteServicio clienteServicio;
    
     @Autowired
    private DireccionesServicio direccionService;

      @Operation(summary = "Crear una nueva direccion al cliente", description = "Crea una direccion nueva al cliente")
    @PostMapping("/crear")
    public String crearDireccion(@RequestBody DireccionesDTO direccionDTO) {
        try {
            return this.direccionService.crearDireccion(direccionDTO);
        } catch (Exception e) {
            return "Error en crearDireccion: " + e.getMessage();

        }

    }

    @Operation(summary = "Mostrar direcciones", description = "Obtiene una lista de direcciones")
    @GetMapping("/")
    public List<DireccionesDTO> obtenerDirecciones() {
        try {
            return this.direccionService.obtenerDirecciones();
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }

    @Operation(summary = "Actualiza la direccion de un cliente por medio del idDireccion", description = "Actualiza la direccion de un cliente por medio del idDireccion")
    @PostMapping("actualizar/{idDireccion}")
    public String actualizarDireccion(@PathVariable int idDireccion, @RequestBody DireccionesDTO direccionDTO) {
        try {
            return this.direccionService.actualizarDireccion(idDireccion, direccionDTO);
        } catch (Exception e) {
            return "Error en el controlador: " + e.getMessage();
        }

    }

    @Operation(summary = "Elimina la direccion de un cliente por medio del idDireccion", description = "Elimina la direccion de un cliente por medio del idDireccion")
    @DeleteMapping("eliminar/{idDireccion}")
    public String el(@PathVariable int idDireccion) {
        try {
            return this.direccionService.eliminarDireccion(idDireccion);
        } catch (Exception e) {
            return "Error en el controlador: " + e.getMessage();
        }

    }

     @Operation(summary = "Agregar una direccion a un cliente", description = "Permite agregar una nueva dirección al cliente identificado por su DNI." +
    " Debe recibir un JSON de direccionesDTO")
    @PostMapping("/agregar/direccion/{dni}")
    public String agregarDireccionCliente(@PathVariable(name = "dni") String dni,
            @RequestBody DireccionesDTO direccionDTO) {
        return clienteServicio.agregarDireccion(dni, direccionDTO);
    }

}
