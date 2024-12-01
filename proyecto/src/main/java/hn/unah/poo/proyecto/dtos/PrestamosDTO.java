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
    
    private int idPrestamo;

    private double monto;

    private int plazo;

    private double tasaInteres;

    private double cuota;

    private char estado;

    private String tipoPrestamo;

    private List<TablaAmortizacionDTO> tablaAmortizacionDTO;

    private Set<ClienteDTO> clientesDTO;

}
