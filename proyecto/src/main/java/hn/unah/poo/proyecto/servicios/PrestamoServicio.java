package hn.unah.poo.proyecto.servicios;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hn.unah.poo.proyecto.dtos.PrestamosDTO;
import hn.unah.poo.proyecto.repositories.ClienteRepositorio;
import hn.unah.poo.proyecto.repositories.PrestamosRepositorio;
import hn.unah.poo.singleton.SingletonModelMapper;
import hn.unah.poo.proyecto.models.Cliente;
import hn.unah.poo.proyecto.models.Prestamos;

@Service
public class PrestamoServicio {

    @Autowired
    private PrestamosRepositorio prestamosRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    //
    public String crearPrestamo(String dni, PrestamosDTO nvoPrestamosDTO){
        if(!this.clienteRepositorio.existsById(dni)){
            return "No es posible crear el prestamo ya que la persona con DNI: " + dni + " no existe";
        }

        Prestamos prestamoBD = SingletonModelMapper.getModelMapperInstance().map(nvoPrestamosDTO, Prestamos.class);

        if(asociarPrestamos(dni, prestamoBD)){
            return "Prestamo agregado exitosamente";
        }
        return "";
    }

    //Asocia un prestamo a un cliente
    private boolean asociarPrestamos(String dni, Prestamos prestamoBD){

        Cliente cliente = this.clienteRepositorio.findById(dni).get();

        prestamoBD.getClientes().add(cliente);

        cliente.getPrestamos().add(prestamoBD);
        this.clienteRepositorio.save(cliente);

        this.prestamosRepositorio.save(prestamoBD);

        return true;
    }
}
