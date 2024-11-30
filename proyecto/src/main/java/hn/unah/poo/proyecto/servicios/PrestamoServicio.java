 package hn.unah.poo.proyecto.servicios;


import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import hn.unah.poo.proyecto.dtos.PrestamosDTO;
import hn.unah.poo.proyecto.dtos.TablaAmortizacionId;
import hn.unah.poo.proyecto.enumeration.TipoPrestamo;
import hn.unah.poo.proyecto.models.Cliente;
import hn.unah.poo.proyecto.models.Prestamos;
import hn.unah.poo.proyecto.models.TablaAmortizacion;
import hn.unah.poo.proyecto.repositories.ClienteRepositorio;
import hn.unah.poo.proyecto.repositories.PrestamosRepositorio;
import hn.unah.poo.proyecto.repositories.TablaAmortizacionRepositorio;
import hn.unah.poo.proyecto.singleton.SingletonModelMapper;

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

    //
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

    //Asocia un prestamo a un cliente
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
        crearTablaAmortizacion(prestamoBD, prestamoBD.getCuota(), prestamoBD.getTasaInteres());
        prestamoBD.getClientes().add(cliente);

        cliente.getPrestamos().add(prestamoBD);
        this.clienteRepositorio.save(cliente);

        this.prestamosRepositorio.save(prestamoBD);

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
        double r = (tasaDeInteres / 12) / 100; // Dividir entre 100 para convertir a porcentaje mensual
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
            double interes = (tasaDeInteres / 100) / 12 * saldo;

            // Calcular el capital de la cuota
            double capital = cuota - interes;

            // Calcular el saldo después de la cuota
            saldo = saldo - capital;

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
            tablaAmortizacionRepositorio.save(tablaAmortizacion);

            // Incrementar la fecha para la siguiente cuota (un mes)
            fechaVencimiento = fechaVencimiento.plusMonths(1);
        }
    }

//    public String obtenerSaldoPendiente(String dni, int idprestamo){

  //      try {

            // Buscar si existe un cliente con el DNI proporcionado
    //    Optional<Cliente> cliente = clienteRepositorio.findById(dni);
      //  if (cliente == null) {
        //    throw new NotFoundException();
        //}

           // Buscar el préstamo asociado al cliente y verificar que pertenece al cliente
          // Prestamos prestamo = prestamosRepositorio.findById(idprestamo)
           //.orElseThrow(() -> new NotFoundException());

   //if (!prestamo.getClientes().getDni().equals(dni)) {
     //  throw new IllegalAccessException("El préstamo con ID " + idprestamo + " no pertenece al cliente con DNI " + dni + ".");
   //}

   // Obtener el saldo pendiente de la tabla de amortización
   //Double saldoPendiente = tablaAmortizacionRepositorio.obtenerSaldoPendiente(idprestamo);

   //if (saldoPendiente == null || saldoPendiente == 0) {
     //  return "El préstamo con ID " + idprestamo + " no tiene cuotas pendientes.";
   //}

   // Retornar el saldo pendiente formateado
   //return "El saldo pendiente para el préstamo con ID " + idprestamo + " es de: " + saldoPendiente + " LPS.";

//} catch (NotFoundException | IllegalAccessException e) {
//}
//return "Error: " + e.getMessage();

        
    public String pagarCuota(String dni, int idprestamo){
        return "";
    }

    

    }