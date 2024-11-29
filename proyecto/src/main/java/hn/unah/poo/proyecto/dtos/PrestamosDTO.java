package hn.unah.poo.proyecto.dtos;

import hn.unah.poo.proyecto.enumeration.TipoPrestamo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrestamosDTO {
    
    private int idPrestamo;

    private double monto;

    private int plazo;

    private double tasaInteres;

    private double cuota;

    private char estado;

    private TipoPrestamo tipoPrestamo;

}
