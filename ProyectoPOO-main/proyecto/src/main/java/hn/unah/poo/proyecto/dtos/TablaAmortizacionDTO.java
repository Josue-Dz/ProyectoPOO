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

    private TablaAmortizacionId id;

    private double interes;

    private double capital;

    private double saldo;

    private char estado;

    private  LocalDate fechaVencimiento;

}
