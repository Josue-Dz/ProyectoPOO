 package hn.unah.poo.proyecto.servicios;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import jakarta.transaction.Transactional;

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

   /**
     * Método para asociar un prestamo a un cliente
     * @param dni
     * @param prestamoBD
     * @return
     */
    @Transactional
    public String crearPrestamo(String dni, PrestamosDTO nvoPrestamosDTO){
        if(!this.clienteRepositorio.existsById(dni)){
            return "No es posible crear el prestamo ya que el cliente con DNI: " + dni + " no existe";
        }

        if (nvoPrestamosDTO.getPlazo() != 1) {
            return "El plazo máximo para un préstamo es de 1 año.";
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

    
/**
 * Busca todos los prestamos que tiene el cliente con el DNI asociado
 * @param dni
 * @return Una Lista o Set de los Prestamos del cliente con el DNI
 */
    public Optional<Set<PrestamosDTO>> buscarPrestamoPorDni(String dni) {
        try {
            // Verificar si existe el cliente por DNI
            if (!this.clienteRepositorio.existsById(dni)) {
                return Optional.of(new HashSet<>());
            }
    
            // Obtener el cliente encontrado
            Cliente clienteEncontrado = this.clienteRepositorio.findById(dni).orElseThrow();
    
            // Mapear los préstamos a PrestamosDTO si existen
            if (clienteEncontrado.getPrestamos() != null) {
                Set<PrestamosDTO> prestamosDTO = clienteEncontrado.getPrestamos()
                        .stream()
                        .map(prestamo -> {
                            // Mapear Prestamo a PrestamosDTO
                            PrestamosDTO prestamoDTO = SingletonModelMapper.getModelMapperInstance().map(prestamo, PrestamosDTO.class);
    
                            // Mapear la lista de TablaAmortizacion a TablaAmortizacionDTO
                            List<TablaAmortizacionDTO> tablaAmortizacionDTO = prestamo.getTablaAmortizacion()
                                    .stream()
                                    .map(amortizacion -> SingletonModelMapper.getModelMapperInstance().map(amortizacion, TablaAmortizacionDTO.class))
                                    .collect(Collectors.toList());
    
                            // Establecer la lista mapeada en el DTO
                            prestamoDTO.setTablaAmortizacionDTO(tablaAmortizacionDTO);
    
                            // Mapear Cliente a ClienteDTO
                            ClienteDTO clienteDTO = SingletonModelMapper.getModelMapperInstance().map(clienteEncontrado, ClienteDTO.class);
    
                            // Mapear las direcciones a DireccionDTO
                            List<DireccionesDTO> direccionesDTO = clienteEncontrado.getDirecciones()                                    
                                    .stream()
                                    .map(direccion -> SingletonModelMapper.getModelMapperInstance().map(direccion, DireccionesDTO.class))
                                    .collect(Collectors.toList());
    
                            // Asignar direcciones al ClienteDTO
                            clienteDTO.setDireccionesDTO(direccionesDTO);
    
                            // Crear un Set<ClienteDTO> y agregar el ClienteDTO
                            Set<ClienteDTO> clientesDTOSet = new HashSet<>();
                            clientesDTOSet.add(clienteDTO);
    
                            // Asignar clientes al DTO del préstamo
                            prestamoDTO.setClientesDTO(clientesDTOSet);
    
                            return prestamoDTO; // Retornar el DTO completo
                        })
                        .collect(Collectors.toSet());
    
                return Optional.of(prestamosDTO); // Devolver los préstamos mapeados
            }
    
            // Si no hay préstamos, devolver un conjunto vacío
            return Optional.of(new HashSet<>());
    
        } catch (Exception e) {
            // Manejar la excepción con un registro (opcional) o personalización
            System.err.println("Error al buscar préstamos por DNI: " + e.getMessage());
            return Optional.of(new HashSet<>());
        }
    }


    /**
     * 
     * @param idPrestamo
     * @return
     */
    public PrestamosDTO buscarPrestamoPorId(int idPrestamo) {

        // Verificar si el préstamo existe en la base de datos
        if (!this.prestamosRepositorio.existsById(idPrestamo)) {
            return null; // Retornar null si el préstamo no existe
        }
    
        // Obtener el préstamo desde el repositorio
        Prestamos prestamo = this.prestamosRepositorio.findById(idPrestamo).get();
    
        // Mapear el préstamo a un DTO usando SingletonModelMapper
        PrestamosDTO prestamoDTO = SingletonModelMapper.getModelMapperInstance().map(prestamo, PrestamosDTO.class);
    
        // Retornar el préstamo en formato DTO
        return prestamoDTO;

    }


    /**
     * Método que permite asociar un prestamo a un cliente existente
     * @param dni
     * @param prestamoBD
     * @return una cadena de texto con la información referente a la correcta o incorrecta asociación del prestamo al cliente
     */
    public String asociarPrestamoCliente(String dni, Prestamos prestamoBD){

        try {
            Cliente cliente = this.clienteRepositorio.findById(dni).get();

            // Calcular el nivel de endeudamiento
            double totalEgresos = obtenerTotalDeEgresos(cliente);
            double sueldo = cliente.getSueldo();
            double nivelEndeudamiento = totalEgresos / sueldo;
    
            if (nivelEndeudamiento > 0.40) {
                return "El nivel de endeudamiento del cliente con DNI " + dni + " es superior al 40%. No se puede crear el préstamo.";
            }

            // Crear la tabla de amortización para el préstamo
            prestamoBD.getClientes().add(cliente);
            this.prestamosRepositorio.save(prestamoBD);
    
            cliente.getPrestamos().add(prestamoBD);
            crearTablaAmortizacion(prestamoBD, prestamoBD.getCuota(), prestamoBD.getTasaInteres());
            this.clienteRepositorio.save(cliente);
    
            return "El cliente con DNI: " + cliente.getDni() + " ha adquirido un prestamo exitosamente!";
        } catch (Exception e) {
            return "Ha ocurrido un error: " + e;
        }
       
    }

     /**
     * Método que permite obtener la cuota del prestamo a pagar
     * @param monto
     * @param tasaDeInteres
     * @param plazo
     * @return el valor de la cuota a pagar
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



    /**
     * Método para obtener la tasa de interés según el tipo de prestamo
     * @param tipoPrestamo
     * @return el valor de la tasa de interés
     */
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


    /**
     * 
     * @param cliente
     * @return
     */
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

    /**
     * 
     * @param prestamoBD
     * @param cuota
     * @param tasaDeInteres
     * @return
     */
    private String crearTablaAmortizacion(Prestamos prestamoBD, double cuota, double tasaDeInteres) {
        try {
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

            return "";
        } catch (Exception e) {
            return "No fue posible crear la tabla amortizaciones" + e;
        }
        
    }


    /**
     * 
     * @param dni
     * @param idPrestamo
     * @return
    */
    public String obtenerSaldoPendiente(String dni, int idPrestamo){

        try {
            if (!this.clienteRepositorio.existsById(dni)){
                return "El cliente con DNI: " + dni + " no existe!.";
            }
    
            Cliente cliente = this.clienteRepositorio.findById(dni).get();
            Prestamos prestamo = this.prestamosRepositorio.findById(idPrestamo).get();
            double saldoPendiente = 0.0;
            int cuotasPagadas = prestamo.getPlazo() * 12;
            int cuotasPendientes = 0;
            
            if (!cliente.getPrestamos().contains(prestamo)){
                return "El cliente con DNI:" + dni + " no cuenta con dicho prestamo!";
            }
    
            for (TablaAmortizacion cuota : prestamo.getTablaAmortizacion()) {
                if (cuota.getEstado() == 'P'){
                    saldoPendiente =+ cuota.getSaldo();
                    cuotasPagadas--;
                }   
            }
    
            cuotasPendientes = (prestamo.getPlazo()*12) - cuotasPagadas;
    
            return String.format("Pagado: %d cuota(s) \n"
                                +"Pendiente: %d cuota(s)\n"
                                +"Saldo Pendiete: %.2f", cuotasPagadas, cuotasPendientes, saldoPendiente);
            
        } catch (Exception e) {
            return "No se pudo completar la acción " + e;
        }

    }


    /**
     * Método que permite pagar una a una las cuotas pendientes del prestamo o los 
     * prestamos asociados a clientes
     * @param dni
     * @param idprestamo
     * @return String una cadena de texto indicando si fue o no posible pagar la cuota, también se
     * incluye el saldo pendiente
     */  
    public String pagarCuota(String dni, int idprestamo){

        try {

            if (!this.clienteRepositorio.existsById(dni)){
                return "El cliente con DNI: " + dni + " no existe!.";
            }
    
            Cliente cliente = this.clienteRepositorio.findById(dni).get();
    
            if (!cliente.getPrestamos().contains(this.prestamosRepositorio.findById(idprestamo).get())){
                return "El cliente con DNI: " + dni + " no cuenta con dicho prestamo!";
            }
    
            Prestamos prestamoBD = this.prestamosRepositorio.findById(idprestamo).get();
    
            int numeroCuota = 0;
            double saldoPendiente = 0;
            for(TablaAmortizacion cuota : prestamoBD.getTablaAmortizacion()){
                if(cuota.getEstado() == 'P'){
                    cuota.setEstado('A');
                    prestamoBD.getTablaAmortizacion().set(cuota.getId().getNumeroCuota(), cuota);
                    cuota.setPrestamos(prestamoBD);
                    numeroCuota = cuota.getId().getNumeroCuota();
                    saldoPendiente = cuota.getSaldo();
                    break;
                }
    
                if (cuota.getEstado() == 'A' && cuota.getId().getNumeroCuota() == prestamoBD.getPlazo()*12){
                    return String.format("Ya no existen cuotas a pagar para este prestamo!\n"
                                       + "El saldo del prestamo es de: %.2f", saldoPendiente);
                }
            }
    
            this.prestamosRepositorio.save(prestamoBD);
    
            return String.format("Se ha realizado con exito el pago de la cuota"
                               + " #%d!.\n \nEl saldo del prestamos es de: %.2f", 
                               numeroCuota, saldoPendiente);

        } catch (Exception e) {
            return "No se logró completar la acción!" + e;
        }
        
    }

    

    }
