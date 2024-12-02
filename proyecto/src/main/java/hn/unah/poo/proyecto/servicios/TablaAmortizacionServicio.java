package hn.unah.poo.proyecto.servicios;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hn.unah.poo.proyecto.dtos.TablaAmortizacionId;
import hn.unah.poo.proyecto.models.Prestamos;
import hn.unah.poo.proyecto.models.TablaAmortizacion;
import hn.unah.poo.proyecto.repositories.TablaAmortizacionRepositorio;

@Service
public class TablaAmortizacionServicio {

    @Autowired
    private TablaAmortizacionRepositorio tablaAmortizacionRepositorio;

    protected void crearTablaAmortizacion(Prestamos prestamo) {
        // Se inicializan las variables
        double saldo = prestamo.getMonto();
        double cuota = prestamo.getCuota();
        double tasaDeInteresMensual = prestamo.getTasaInteres() / 12;
        LocalDate fechaVencimiento = LocalDate.now();

        // Crear el registro inicial (cuota 0)
        crearRegistroAmortizacion(prestamo, 0, 0, 0, saldo, 'A', fechaVencimiento);

        // Generar las cuotas restantes
        int numeroDeCuotas = prestamo.getPlazo() * 12;

        for (int i = 1; i <= numeroDeCuotas; i++) {
            // Calcular los valores de la cuota
            double interesMensual = saldo * tasaDeInteresMensual;
            double capitalMensual = cuota - interesMensual;
            saldo -= capitalMensual;

            // Crear registro de amortización para la cuota actual
            crearRegistroAmortizacion(prestamo, i, interesMensual, capitalMensual, saldo, 'P', fechaVencimiento);

            // Incrementar la fecha de vencimiento al siguiente mes
            fechaVencimiento = fechaVencimiento.plusMonths(1);
        }
    }

    // Método que guardar un registro en la tabla de amortización
    private void crearRegistroAmortizacion(Prestamos prestamo, int numeroCuota, double interes,
            double capital, double saldo, char estado, LocalDate fechaVencimiento) {
        // Configurar la clave primaria compuesta
        TablaAmortizacionId id = new TablaAmortizacionId();
        id.setIdPrestamo(prestamo.getIdPrestamo());
        id.setNumeroCuota(numeroCuota);

        // Configurar el registro de amortización
        TablaAmortizacion tablaAmortizacion = new TablaAmortizacion();
        tablaAmortizacion.setId(id);
        tablaAmortizacion.setPrestamos(prestamo);
        tablaAmortizacion.setInteres(interes);
        tablaAmortizacion.setCapital(capital);
        tablaAmortizacion.setSaldo(saldo);
        tablaAmortizacion.setEstado(estado);
        tablaAmortizacion.setFechaVencimiento(fechaVencimiento);

        // Guardar en la base de datos
        tablaAmortizacionRepositorio.save(tablaAmortizacion);
    }

}
