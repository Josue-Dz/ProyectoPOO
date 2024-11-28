package hn.unah.poo.proyecto.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hn.unah.poo.proyecto.dtos.PrestamosDTO;
import hn.unah.poo.proyecto.servicios.PrestamoServicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/prestamos")
public class PrestamosController {

    @Autowired
    private PrestamoServicio prestamosServicio;

    @PostMapping("/crear/prestamo/{dni}")
    public String crearPrestamo(@PathVariable String dni, @RequestBody PrestamosDTO nvoPrestamosDTO) {
        return prestamosServicio.crearPrestamo(dni, nvoPrestamosDTO);
    }   

}
