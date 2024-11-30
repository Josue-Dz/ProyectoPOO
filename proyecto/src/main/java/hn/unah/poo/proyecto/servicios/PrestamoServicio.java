package hn.unah.poo.proyecto.servicios;


import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import hn.unah.poo.proyecto.dtos.PrestamosDTO;
import hn.unah.poo.proyecto.dtos.TablaAmortizacionId;
import hn.unah.poo.proyecto.enumeration.TipoPrestamo;
import hn.unah.poo.proyecto.repositories.ClienteRepositorio;
import hn.unah.poo.proyecto.repositories.PrestamosRepositorio;
import hn.unah.poo.proyecto.repositories.TablaAmortizacionRepositorio;
import hn.unah.poo.proyecto.singleton.SingletonModelMapper;
import jakarta.transaction.Transactional;
import hn.unah.poo.proyecto.models.Cliente;
import hn.unah.poo.proyecto.models.Prestamos;
import hn.unah.poo.proyecto.models.TablaAmortizacion;

@Service
public class PrestamoServicio {

    @Autowired
    private PrestamosRepositorio prestamosRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private TablaAmortizacionRepositorio tablaAmortizacionRepositorio;

    @Value("${prestamo.tasa.personal}")
    private double tasaPersonal;

    @Value("${prestamo.tasa.hipotecario}")
    private double tasaHipotecario;

    @Value("${prestamo.tasa.vehicular}")
    private double tasaVehicular;


    @Transactional
    public String crearPrestamo(String dni, PrestamosDTO nvoPrestamosDTO){
        if(!this.clienteRepositorio.existsById(dni)){
            return "No es posible crear el prestamo ya que el cliente con DNI: " + dni + " no existe";
        }

        if (nvoPrestamosDTO.getPlazo() < 1) {
            return "El plazo mínimo para un préstamo es de 1 año.";
        }

        // Obtener y establecer la tasa de interés y la cuota
        double tasaDeInteres = obtenerTasaInteres(nvoPrestamosDTO.getTipoPrestamo());
        double cuota = obtenerCuota(nvoPrestamosDTO.getMonto(), tasaDeInteres, nvoPrestamosDTO.getPlazo());
        nvoPrestamosDTO.setTasaInteres(tasaDeInteres);
        nvoPrestamosDTO.setCuota(cuota);
        nvoPrestamosDTO.setEstado('P');

        Prestamos prestamoBD = SingletonModelMapper.getModelMapperInstance().map(nvoPrestamosDTO, Prestamos.class);
        

        return asociarPrestamoCliente(dni, prestamoBD);
    }


    
    private String asociarPrestamoCliente(String dni, Prestamos prestamoBD){

        Cliente cliente = this.clienteRepositorio.findById(dni).get();

        // Calcular el nivel de endeudamiento
        double totalEgresos = obtenerTotalDeEgresos(cliente);
        double sueldo = cliente.getSueldo();
        double nivelEndeudamiento = totalEgresos / sueldo;

        if (nivelEndeudamiento > 0.40) {
            return "El nivel de endeudamiento del cliente con DNI " + dni + " es superior al 40%. No se puede crear el préstamo.";
        }

        //prestamoBD.setEstado('P');
        // Crear la tabla de amortización para el préstamo
        prestamoBD.getClientes().add(cliente);
        this.prestamosRepositorio.save(prestamoBD);

        cliente.getPrestamos().add(prestamoBD);
        crearTablaAmortizacion(prestamoBD, prestamoBD.getCuota(), prestamoBD.getTasaInteres());
        this.clienteRepositorio.save(cliente);

        return "El cliente con DNI: " + cliente.getDni() + " ha adquirido un prestamo exitosamente!";
    }

     /**
     * Obtiene la cuota
     *
     * @param monto
     * @param tasaDeInteres
     * @param plazo
     * @return cuota
     */
    private double obtenerCuota(double monto, double tasaDeInteres, int plazo) {
        // P es el monto del préstamo
        double p = monto;

        // r es la tasa de interés mensual
        double r = (tasaDeInteres / 12);
        // n es el número total de pagos
        int n = plazo * 12;  // Plazo en años convertido a meses
        // Calculamos la cuota usando la fórmula
        double cuota = (p * r * Math.pow((1 + r), n)) / (Math.pow((1 + r), n) - 1);

        return cuota;
    }


    // Método para obtener la tasa según el tipo de préstamo
    private double obtenerTasaInteres(TipoPrestamo tipoPrestamo) {
        switch (tipoPrestamo) {
            case V -> {
                return tasaVehicular;
            }
            case P -> {
                return tasaPersonal;
            }
            case H -> {
                return tasaHipotecario;
            }
            default ->
                throw new IllegalArgumentException("Tasa de interés no disponible para el tipo de préstamo " + tipoPrestamo);
        }
    }

    private double obtenerTotalDeEgresos(Cliente cliente) {
        // Inicializar la variable totalEgresos en 0
        double totalEgresos = 0;

        // Iterar sobre todos los préstamos del cliente
        for (Prestamos prestamo : cliente.getPrestamos()) {
            // Verificar si el préstamo está pendiente (estado 'P')
            if (prestamo.getEstado() == 'P') {
                // Sumar la cuota del préstamo al total de egresos
                totalEgresos += prestamo.getCuota();
            }
        }
        return totalEgresos;
    }

    private void crearTablaAmortizacion(Prestamos prestamoBD, double cuota, double tasaDeInteres) {
        double saldo = prestamoBD.getMonto(); // Saldo inicial es el monto del préstamo

        LocalDate fechaVencimiento = LocalDate.now(); // Obtener la fecha actual del sistema

        // Crear y agregar el registro de la cuota a la tabla de amortización
        TablaAmortizacion tablaAmortizacion = new TablaAmortizacion();

        // Asignar correctamente el número de cuota en la clave primaria compuesta
        TablaAmortizacionId idTablaAmortizacion = new TablaAmortizacionId();

        // Asociar el préstamo
        tablaAmortizacion.setPrestamos(prestamoBD);

        idTablaAmortizacion.setIdPrestamo(prestamoBD.getIdPrestamo()); // Asignar el ID del préstamo
        idTablaAmortizacion.setNumeroCuota(0); // Las cuotas empiezan desde 1, no 0

        tablaAmortizacion.setId(idTablaAmortizacion); // Asignar la clave primaria compuesta

        tablaAmortizacion.setInteres(0);
        tablaAmortizacion.setCapital(0);
        tablaAmortizacion.setSaldo(saldo);
        tablaAmortizacion.setEstado('A');
        tablaAmortizacion.setFechaVencimiento(fechaVencimiento);

        // Guardar cada cuota en la base de datos (presumiblemente con un repositorio)
        this.tablaAmortizacionRepositorio.save(tablaAmortizacion);

        for (int i = 0; i < prestamoBD.getPlazo() * 12; i++) {
            // Calcular el interés para la cuota actual
            double interes = (tasaDeInteres/ 12 )* saldo;

            // Calcular el capital de la cuota
            double capital = cuota - interes;

            // Calcular el saldo después de la cuota
            saldo = saldo-capital;

            // Crear y agregar el registro de la cuota a la tabla de amortización
            tablaAmortizacion = new TablaAmortizacion();

            // Asociar el préstamo
            tablaAmortizacion.setPrestamos(prestamoBD);

            // Asignar correctamente el número de cuota en la clave primaria compuesta
            idTablaAmortizacion = new TablaAmortizacionId();
            idTablaAmortizacion.setIdPrestamo(prestamoBD.getIdPrestamo()); // Asignar el ID del préstamo
            idTablaAmortizacion.setNumeroCuota(i + 1); // Las cuotas empiezan desde 1, no 0

            tablaAmortizacion.setId(idTablaAmortizacion); // Asignar la clave primaria compuesta

            tablaAmortizacion.setInteres(interes);
            tablaAmortizacion.setCapital(capital);
            tablaAmortizacion.setSaldo(saldo);
            tablaAmortizacion.setEstado('P');
            tablaAmortizacion.setFechaVencimiento(fechaVencimiento);

            // Guardar cada cuota en la base de datos (presumiblemente con un repositorio)
            this.tablaAmortizacionRepositorio.save(tablaAmortizacion);

            // Incrementar la fecha para la siguiente cuota (un mes)
            fechaVencimiento = fechaVencimiento.plusMonths(1);
        }
    }

    public String obtenerSaldoPendiente(String dni, int idprestamo){
        return "";
    }
    public String pagarCuota(String dni, int idprestamo){
        if (!this.clienteRepositorio.existsById(dni)){
            return "El cliente con DNI: " + dni + " no existe!.";
        }

        Cliente cliente = this.clienteRepositorio.findById(dni).get();
        if (!this.prestamosRepositorio.existsById(idprestamo)){
            return "El cliente con DNI: " + dni + " no cuenta con dicho prestamo!.";
        }

        Prestamos prestamoBD = this.prestamosRepositorio.findById(idprestamo).get();
       // TablaAmortizacion cuotaAntiguaPendiente = prestamoBD.getTablaAmortizacion().get(0);

        for(TablaAmortizacion cuota : prestamoBD.getTablaAmortizacion()){
            if(cuota.getEstado() == 'P'){
                cuota.setEstado('A');
                prestamoBD.getTablaAmortizacion().set(cuota.getId().getNumeroCuota(), cuota);
                cuota.setPrestamos(prestamoBD);
                break;
            }

        }

        this.prestamosRepositorio.save(prestamoBD);

        return "Se ha realizado el pago exitosamente!";
    }
}
