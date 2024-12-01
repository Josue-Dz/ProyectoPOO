package hn.unah.poo.proyecto.controllers;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hn.unah.poo.proyecto.dtos.PrestamosDTO;
import hn.unah.poo.proyecto.servicios.PrestamoServicio;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamosController {

    @Autowired
    private PrestamoServicio prestamosServicio;


@Operation(summary = "Permite crear un nuevo préstamo para un cliente", 
description = """
 Crea un nuevo prestamo para el cliente identificado por su DNI. 
 \nRecibe un objeto JSON que contiene la informacion del prestamo
 \nEl préstamo será asociado al cliente y se calculará la cuota mensual.""")
    @PostMapping("/crear/{dni}")
    public String crearPrestamo(@PathVariable String dni, @RequestBody PrestamosDTO nvoPrestamosDTO) {
        return prestamosServicio.crearPrestamo(dni, nvoPrestamosDTO);
    }  

    @Operation(summary = "Obtiene la información de un préstamo por su ID",
    description = """
                  Permite obtener los detalles de un prestamo especifico mediante su ID. 
                  \n Retorna un objeto `PrestamosDTO` que contiene la informacion del prestamo solicitado o `Optional.empty()` 
                  si no se encuentra el prestamo con el ID proporcionado.""")
    @GetMapping("/buscar/prestamo/{idPrestamo}")
    public Optional<PrestamosDTO> buscarPrestamoPorId(@PathVariable(name ="idPrestamo") int idPrestamo) {
        return prestamosServicio.buscarPrestamoPorId(idPrestamo);
    }
    
    @Operation(summary = "Obtiene los préstamos asociados a un cliente mediante su DNI",
    description = """
    Permite obtener todos los prestamos asociados al cliente identificado por su DNI.  
    \n Retorna un conjunto de objetos `PrestamosDTO` que contienen la informacion de cada prestamo asociado al cliente.""")
    @GetMapping("/buscar/{dni}")
    public Optional<Set<PrestamosDTO>> buscarPrestamosPorDni(@PathVariable(name ="dni") String dni) {
        return prestamosServicio.buscarPrestamoPorDni(dni);
    }

    @Operation(summary = "Asocia un préstamo a un cliente mediante su DNI",
     description = """
                   Permite asociar un prestamo a un cliente identificado por su DNI.  
                   \nRecibe un parametro de tipo `String` que contiene el DNI del cliente. 
                   \nEl prestamo sera asociado al cliente y se actualizara su informacion en la base de datos.""")
    @GetMapping("/asociar/prestamo/{dni}/{idPrestamo}")
    public String asociarPrestamoACliente(@PathVariable (name = "dni") String dni, @PathVariable (name = "idPrestamo") int idPrestamo) {
        return prestamosServicio.asociarPrestamoACliente(dni, idPrestamo);
    }

  
    @Operation(summary = "Consulta el saldo pendiente de un préstamo asociado a un cliente",
    description = """
                  Permite obtener el saldo pendiente de un prestamo especifico asociado a un cliente, identificado por su DNI y el ID del prestamo. 
                  El resultado incluye el saldo pendiente y detalles sobre el estado de las cuotas (cuotas pagadas y cuotas pendientes).""")
    @GetMapping("/obtener/saldo/{dni}/{idPrestamo}")
    public String obtenerSaldoPendiente(@PathVariable(name = "dni") String dni, @PathVariable(name = "idPrestamo") int idPrestamo) {
        return prestamosServicio.obtenerSaldoPendiente(dni, idPrestamo);
    }

    @Operation(summary = "Permite pagar una cuota de un préstamo asociado a un cliente",
    description = """
                  Permite al cliente pagar una cuota especifica de un prestamo. El cliente es identificado mediante su DNI y el ID del prestamo. 
                  \nProcesa el pago de la cuota pendiente y actualiza el estado de la misma.""")
    @GetMapping("/pagar/cuota/{dni}/{idprestamo}")
    public String pagarCuotaPrestamo(@PathVariable(name="dni") String dni, @PathVariable (name="idprestamo") int idPrestamo) {
        return prestamosServicio.pagarCuota(dni, idPrestamo);
    }

}
