package hn.unah.poo.proyecto.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hn.unah.poo.proyecto.repositories.DireccionesRepositorio;

@Service
public class DireccionesServicio {

    @Autowired
    private DireccionesRepositorio direccionesRepositorio;

    
}
