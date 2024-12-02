package hn.unah.poo.proyecto.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TablaAmortizacionDTO {
 /**
     * Id de la tabla Embebida; Creada para generar un primary key compuesto de
     * (numerocuota y idPrestamo)
     */
    private TablaAmortizacionId id;
    /**
     * Interes pagado por el clienteDTO en tablaAmortizacionDTO
     */
    private double interes;
    /**
     * Capital pagado por el clienteDTO en tablaAmortizacionDTO
     */
    private double capital;
    /**
     * Saldo a pagar por el clienteDTO en tablaAmortizacionDTO
     */
    private double saldo;
    /**
     * Estado de la cuota en tablaAmortizacionDTO
     */
    private char estado;
    /**
     * Fecha a vencer para pagar la cuota en tablaAmortizacionDTO
     */
    private LocalDate fechaVencimiento;


}
