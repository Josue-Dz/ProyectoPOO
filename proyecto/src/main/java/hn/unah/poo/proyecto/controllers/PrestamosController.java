package hn.unah.poo.proyecto.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import hn.unah.poo.proyecto.dtos.PrestamosDTO;
import hn.unah.poo.proyecto.servicios.PrestamoServicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/api/prestamos")
public class PrestamosController {

    @Autowired
    private PrestamoServicio prestamosServicio;

    @PostMapping("/crear/{dni}")
    public String crearPrestamo(@PathVariable String dni, @RequestBody PrestamosDTO nvoPrestamosDTO) {
        return prestamosServicio.crearPrestamo(dni, nvoPrestamosDTO);
    }  

    @GetMapping("/buscar/prestamo/{idPrestamo}")
    public PrestamosDTO buscarPrestamoPorid(@PathVariable(name ="idPrestamo") int idPrestamo) {
        return prestamosServicio.buscarPrestamoPorId(idPrestamo);
    }
    
    @GetMapping("/buscar/{dni}")
    public List<PrestamosDTO> buscarPrestamosPordni(@PathVariable(name ="dni") String dni) {
        return prestamosServicio.buscarPrestamoPorDni(dni);
    }

    @GetMapping("/buscarPrestamoPorId/{idPrestamo}")
    public Optional<PrestamosDTO> buscarPrestamoPorId(@PathVariable int idPrestamo) {
        return prestamosServicio.buscarPrestamoPorId(idPrestamo);
    }
    

    @GetMapping("/asociar/prestamo/{dni}")
    public String asociarPrestamoACliente(@RequestParam String param) {
        return prestamosServicio.asociarPrestamoCliente(param, null);
    }

    /**
    @GetMapping("/obtener/saldo/{dni}/{idPrestamo}")
    public String obtenerSaldoPendiente(@PathVariable(name = "dni") String dni, @PathVariable(name = "idPrestamo") int idPrestamo) {
        return prestamosServicio.obtenerSaldoPendiente(dni, idPrestamo);
    }
   */
     @Operation(summary = "Obtiene el saldo del Prestamo buscado", description = "Obtiene el saldo Pendiente del prestamo con el id buscado")
    @GetMapping("/obtenerSaldo/{idPrestamo}")
    public String obtenerSaldo(@PathVariable int idPrestamo) {
        return prestamosServicio.obtenerSaldo(idPrestamo);
    }
    
    
    @GetMapping("/pagar/cuota/{dni}/{idprestamo}")
    public String pagarCuotaPrestamo(@PathVariable(name="dni") String dni, @PathVariable (name="idprestamo") int idPrestamo) {
        return prestamosServicio.pagarCuota(dni, idPrestamo);
    }

}
