 package hn.unah.poo.proyecto.servicios;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import hn.unah.poo.proyecto.dtos.ClienteDTO;
import hn.unah.poo.proyecto.dtos.DireccionesDTO;
import hn.unah.poo.proyecto.dtos.PrestamosDTO;
import hn.unah.poo.proyecto.dtos.TablaAmortizacionDTO;
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
     * Crea un nuevo préstamo para un cliente existente en la base de datos. 
     * Este método realiza las siguientes operaciones:
     * - Verifica que el cliente con el DNI proporcionado exista en la base de datos.
     * - Valida que el plazo del préstamo no exceda el límite permitido (1 año).
     * - Calcula la tasa de interés y la cuota del préstamo según el tipo y monto.
     * - Establece el estado inicial del préstamo como pendiente ('P').
     * - Asocia el préstamo al cliente y lo almacena en la base de datos.
     * @param dni
     * @param nvoPrestamoDTO
     * @return Un mensaje indicando si el préstamo fue creado exitosamente o una descripción del error
     *         en caso de que no se haya podido realizar la operación.
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
 *  Busca los préstamos asociados a un cliente dado su DNI.
 * 
 * Este método realiza las siguientes operaciones:
 * - Verifica si el cliente con el DNI proporcionado existe en la base de datos.
 * - Si el cliente existe, mapea todos sus préstamos a objetos `PrestamosDTO`.
 * - Para cada préstamo, también mapea la tabla de amortización asociada (`TablaAmortizacionDTO`).
 * - Mapea las direcciones del cliente a `DireccionesDTO` y los agrega al `ClienteDTO`.
 * @param dni
 * @return Un `Optional` que contiene un conjunto de objetos `PrestamosDTO` si el cliente tiene préstamos asociados.
 *         Si el cliente no existe o no tiene préstamos, se devuelve un conjunto vacío.
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
     * Busca un préstamo por su ID y devuelve la información asociada en formato DTO.
     * 
     * Este método realiza las siguientes operaciones:
     * - Verifica si el préstamo con el ID proporcionado existe en la base de datos.
     * - Si existe, mapea la entidad `Prestamos` a un objeto `PrestamosDTO`.
     * - Mapea los clientes asociados al préstamo en un conjunto de `ClienteDTO`.
     * - Mapea la tabla de amortización asociada al préstamo en una lista de `TablaAmortizacionDTO`.
     * 
     * @param idPrestamo
     * @return Un `Optional` que contiene un objeto `PrestamosDTO` con toda la información del préstamo, o `Optional.empty()`
     *         si el préstamo no existe o si ocurre un error durante la operación.
     */
    public Optional<PrestamosDTO> buscarPrestamoPorId(int idPrestamo) {
        try {
            // Verificar si el préstamo existe
            if (!this.prestamosRepositorio.existsById(idPrestamo)) {
                return Optional.empty();
            }
    
            // Obtener el objeto Prestamo desde el repositorio
            Prestamos prestamo = this.prestamosRepositorio.findById(idPrestamo).orElseThrow();
    
            // Mapear el objeto Prestamo a PrestamosDTO
            PrestamosDTO prestamoDTO = SingletonModelMapper.getModelMapperInstance().map(prestamo, PrestamosDTO.class);
    
            // Mapear los clientes asociados al préstamo (si existen)
            if (prestamo.getClientes() != null) {
                Set<ClienteDTO> clientesDTO = prestamo.getClientes().stream()
                        .map(cliente -> SingletonModelMapper.getModelMapperInstance().map(cliente, ClienteDTO.class))
                        .collect(Collectors.toSet());
                prestamoDTO.setClientesDTO(clientesDTO);
            }
    
            // Mapear la tabla de amortización asociada al préstamo (si existe)
            if (prestamo.getTablaAmortizacion() != null) {
                List<TablaAmortizacionDTO> tablaAmortizacionDTO = prestamo.getTablaAmortizacion().stream()
                        .map(amortizacion -> SingletonModelMapper.getModelMapperInstance().map(amortizacion, TablaAmortizacionDTO.class))
                        .collect(Collectors.toList());
                prestamoDTO.setTablaAmortizacionDTO(tablaAmortizacionDTO);
            }
    
            // Retornar el DTO con todos los datos
            return Optional.of(prestamoDTO);
    
        } catch (Exception e) {
            System.err.println("Error al buscar el préstamo por ID: " + e.getMessage());
            return Optional.empty();
        }
    }


    /**
     * Asocia un préstamo a un cliente y realiza las validaciones necesarias.
     * 
     * Este método realiza las siguientes operaciones:
     * - Verifica si el nivel de endeudamiento del cliente es superior al 40%.
     * - Si el nivel de endeudamiento es mayor al límite, no se crea el préstamo.
     * - Si el nivel de endeudamiento es aceptable, asocia el préstamo al cliente.
     * - Calcula la tabla de amortización del préstamo y la guarda.
     * - Almacena el préstamo y el cliente en la base de datos.
     * 
     * @param dni
     * @param prestamoBD
     * @return Un mensaje indicando si el préstamo fue asociado exitosamente o una descripción del error,
     *         en caso de que el nivel de endeudamiento sea superior al 40% o de otro error durante el proceso.
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

    /*
     
    /**
     * Obtiene el saldo Pendiente del prestamo buscado por el id
 * @param idPrestamo
 * @return El saldo pendiente del prestamo.
 public String obtenerSaldo(int idPrestamo) {
    // Verificar si el préstamo existe
    if (!this.prestamosRepositorio.existsById(idPrestamo)) {
        return "El id del Prestamo: " + idPrestamo + " no existe en el sistema";
    }
    
        // Obtener el objeto Prestamo desde el repositorio
        Prestamos prestamo = this.prestamosRepositorio.findById(idPrestamo).get();
        
        // Inicializar el saldo pendiente
        double saldoPendiente = 0;
        
        // Verificar si el préstamo tiene tabla de amortización
        if (prestamo.getTablaAmortizacion() != null) {
            // Iterar sobre la tabla de amortización
            for (TablaAmortizacion amortizacion : prestamo.getTablaAmortizacion()) {
                // Si el estado es 'P' (pendiente), sumar el saldo
                if (amortizacion.getEstado() == 'P') { // 'P' representa pendiente
                saldoPendiente += amortizacion.getSaldo();
            }
            }
        }
        // Formatear el saldo pendiente con 14 enteros y 2 decimales
        DecimalFormat df = new DecimalFormat("###,###,###,###.00"); // 14 enteros y 2 decimales
        String saldoFormateado = df.format(saldoPendiente);
        
        // Devolver el saldo pendiente
        return "El saldo pendiente del préstamo con ID " + idPrestamo + " es de: " + saldoFormateado;
    }
    
    */
    

     /**
     * Calcula la cuota mensual de un préstamo utilizando la fórmula de amortización.
     * @param monto
     * @param tasaDeInteres
     * @param plazo
     * @return el valor de la cuota mensual calculada.
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
     * Obtiene la tasa de interés correspondiente para el tipo de préstamo especificado.
     * @param tipoPrestamo
     * @return La tasa de interés correspondiente al tipo de préstamo.
     * @throws IllegalArgumentException Si el tipo de préstamo no es válido.
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
     * Calcula el total de los egresos mensuales de un cliente basado en sus préstamos pendientes.

     * Este método recorre todos los préstamos asociados al cliente y suma la cuota de cada préstamo cuyo
     * estado sea "P" (pendiente). El total de estas cuotas representa los egresos mensuales del cliente
     * debido a los préstamos activos.
     * @param cliente
     * @return El total de los egresos del cliente basado en las cuotas de sus préstamos pendientes.
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
     * Crea la tabla de amortización para un préstamo dado, calculando las cuotas, el interés y el capital de cada una.
     * 
     * Este método realiza las siguientes operaciones:
     * - Inicializa el saldo del préstamo con el monto total.
     * - Calcula y guarda las cuotas del préstamo, junto con los intereses y el capital correspondiente.
     * - Establece las fechas de vencimiento de las cuotas, comenzando desde la fecha actual y aumentando mensualmente.
     * - Para cada cuota, calcula el interés correspondiente y la parte de capital, y guarda cada cuota en la base de datos.
     * @param prestamoBD
     * @param cuota
     * @param tasaDeInteres
     * @return Un mensaje indicando el éxito o el error en el proceso de creación de la tabla de amortización.
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
     * Obtiene el saldo pendiente de un préstamo asociado a un cliente, así como el número de cuotas pagadas y pendientes.
     * Este método realiza las siguientes operaciones:
     * - Verifica si el cliente con el DNI proporcionado existe en la base de datos.
     * - Verifica si el préstamo con el ID proporcionado está asociado al cliente.
     * - Calcula el saldo pendiente del préstamo sumando los saldos de las cuotas pendientes.
     * - Calcula cuántas cuotas del préstamo han sido pagadas y cuántas están pendientes.
     * @param dni
     * @param idPrestamo
     * @return Un mensaje que incluye el número de cuotas pagadas, cuotas pendientes y el saldo pendiente del préstamo,
     *         o un mensaje de error si el cliente o el préstamo no existen o no están asociados.
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
    
            return String.format("""
                                 Pagado: %d cuota(s) 
                                 Pendiente: %d cuota(s)
                                 Saldo Pendiete: %.2f""", cuotasPagadas, cuotasPendientes, saldoPendiente);
            
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
                    return String.format("""
                                         Ya no existen cuotas a pagar para este prestamo!
                                         El saldo del prestamo es de: %.2f""", saldoPendiente);
                }
            }
    
            this.prestamosRepositorio.save(prestamoBD);
    
            return String.format("""
                                 Se ha realizado con exito el pago de la cuota #%d!.
                                  
                                 El saldo del prestamos es de: %.2f""", 
                               numeroCuota, saldoPendiente);

        } catch (Exception e) {
            return "No se logró completar la acción!" + e;
        }
        
    }

    }
