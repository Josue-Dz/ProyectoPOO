package hn.unah.poo.proyecto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import hn.unah.poo.proyecto.dtos.TablaAmortizacionId;
import hn.unah.poo.proyecto.models.TablaAmortizacion;

@Repository
public interface TablaAmortizacionRepositorio extends JpaRepository<TablaAmortizacion, TablaAmortizacionId>{

}
