package hn.unah.poo.proyecto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hn.unah.poo.proyecto.models.Prestamos;

@Repository
public interface PrestamosRepositorio  extends JpaRepository <Prestamos, Integer> {
    
}
