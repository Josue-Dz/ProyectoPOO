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

    /*@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idPrestamo;
        result = prime * result + numeroCuota;
        return result;
    }
        
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TablaAmortizacionId other = (TablaAmortizacionId) obj;
        if (idPrestamo != other.idPrestamo)
            return false;
        return numeroCuota == other.numeroCuota;
    }
    **/

    @Column(name = "idprestamo", insertable = false, updatable = false)
    private int idPrestamo;

    @Column(name = "numerocuota")
    private int numeroCuota;

    
}
