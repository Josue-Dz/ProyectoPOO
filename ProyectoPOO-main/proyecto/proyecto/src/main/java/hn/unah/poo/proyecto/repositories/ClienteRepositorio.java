package hn.unah.poo.proyecto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hn.unah.poo.proyecto.models.Cliente;

@Repository
public interface ClienteRepositorio  extends JpaRepository<Cliente, String>{
    
}
