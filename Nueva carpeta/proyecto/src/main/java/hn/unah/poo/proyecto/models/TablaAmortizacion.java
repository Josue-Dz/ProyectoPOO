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
  /**
     * Id de la tabla Embebida; Creada para generar un primary key compuesto de
     * (numerocuota y idPrestamo)
     */
    @EmbeddedId
    private TablaAmortizacionId id;
 /**
     * Interes pagado por el cliente en tablaAmortizacion
     */
    @Column(columnDefinition = "DECIMAL(14,2)")
    private double interes;
    /**
     * Capital pagado por el cliente en tablaAmortizacion
     */
    @Column(columnDefinition = "DECIMAL(14,2)")
    private double capital;
    /**
     * Saldo a pagar por el cliente en tablaAmortizacion
     */
    @Column(columnDefinition = "DECIMAL(14,2)")
    private double saldo;
    /**
     * Estado de la cuota en tablaAmortizacion
     */
    private char estado;
    /**
     * Fecha a vencer para pagar la cuota en tablaAmortizacion
     */
    @Column(name = "fechavencimiento")
    private LocalDate fechaVencimiento;


    // Usamos insertable=false, updatable=false para evitar la duplicación de la
    // columna idprestamo
    /**
     * Objeto que obtiene toda la informacion del prestamo
     */
    @ManyToOne
    @JoinColumn(name = "idprestamo", referencedColumnName= "idprestamo", insertable = false, updatable = false)
    @JsonIgnore
    private Prestamos prestamos;
    
}
