package hn.unah.poo.proyecto.dtos;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PrestamosDTO {
    /**
     * Id del prestamoDTO
     */
    private int idPrestamo;

    /**
     * Monto del prestamoDTO
     */
    private double monto;
    /**
     * Plazo del prestamoDTO
     */
    private int plazo;
    /**
     * Tasa de Interes del prestamoDTO
     */
    private double tasaInteres;
    /**
     * Cuota del prestamoDTO
     */
    private double cuota;
    /**
     * Estado del prestamoDTO
     */
    private char estado;
    /**
     * Tipo de prestamoDTO
     */
    private String tipoPrestamo;
    /**
     * Lista que contiene la tabla de amortizacionDTO del prestamoDTO
     */
    private List<TablaAmortizacionDTO> tablaAmortizacionDTO;
    /**
     * Set de clientesDTO del prestamoDTO
     */
    private Set<ClienteDTO> clientesDTO;

}
