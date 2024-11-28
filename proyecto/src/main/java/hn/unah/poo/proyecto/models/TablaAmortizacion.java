package hn.unah.poo.proyecto.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import hn.unah.poo.proyecto.dtos.TablaAmortizacionId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="amortizacion")
public class TablaAmortizacion {

    @EmbeddedId
    private TablaAmortizacionId id;

    private double interes;

    private double capital;

    private double saldo;

    private String estado;

    @Column(name = "fechavencimiento")
    private  LocalDate fechaVencimiento;

    @ManyToOne
    @JoinColumn(name = "idprestamo", referencedColumnName= "idprestamo")
    @JsonIgnore
    private Prestamo prestamos;
    
}
