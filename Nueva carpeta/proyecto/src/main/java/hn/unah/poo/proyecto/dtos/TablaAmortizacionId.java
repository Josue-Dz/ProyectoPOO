package hn.unah.poo.proyecto.dtos;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TablaAmortizacionId implements Serializable{

    @Column(name = "idprestamo", insertable = false, updatable = false)
    private int idPrestamo;

    @Column(name = "numerocuota")
    private int numeroCuota;

    
}
