package hn.unah.poo.proyecto.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hn.unah.poo.proyecto.dtos.ClienteDTO;
import hn.unah.poo.proyecto.dtos.DireccionesDTO;
import hn.unah.poo.proyecto.models.Cliente;
import hn.unah.poo.proyecto.models.Direcciones;
import hn.unah.poo.proyecto.repositories.ClienteRepositorio;
import hn.unah.poo.proyecto.repositories.DireccionesRepositorio;
import hn.unah.poo.singleton.SingletonModelMapper;

@Service
public class ClienteServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private DireccionesRepositorio direccionesRepositorio;

    public String crearCliente(ClienteDTO nvoClienteDTO){
        if (this.clienteRepositorio.existsById(nvoClienteDTO.getDni())){
            return "El cliente con DNI: " + nvoClienteDTO.getDni() + " ya existe. Por lo que no se puede agregar!";
        }

        Cliente nvoCliente = SingletonModelMapper.getModelMapperInstance().map(nvoClienteDTO, Cliente.class);
        

        if(nvoCliente.getDirecciones() == null){
            return "Información incompleta. Para poder agregar este cliente necesita agregar al menos una dirección";
        }else if(nvoCliente.getDirecciones().size()>=2){
            return "El cliente a agregar no puede tener más de dos direcciones. Por favor intentelo nuevamente.";
        }

        for (Direcciones direccion : nvoCliente.getDirecciones()) {
            direccion.setCliente(nvoCliente);
            this.direccionesRepositorio.save(direccion);
        }

        this.clienteRepositorio.save(nvoCliente);
        return "El cliente " + nvoClienteDTO.getNombre() + " ha sido agregado exitosamente.";
    }


    public String agregarDireccion(String dni, DireccionesDTO direccionDTO){

        if (this.clienteRepositorio.existsById(dni) && !(this.clienteRepositorio.findById(dni).get().getDirecciones().size()<2)){
            return "Este cliente ya tiene dos direcciones asignadas, no puede agregar más!";
        }

        if(!this.clienteRepositorio.existsById(dni)){
            return "El cliente con DNI: " + dni + " no exite!. Por lo que no es posible agregar esta direccion.";
        }

        Cliente cliente = this.clienteRepositorio.findById(dni).get();

        Direcciones direccion = SingletonModelMapper.getModelMapperInstance().map(direccionDTO, Direcciones.class);

        direccion.setCliente(cliente);
        cliente.getDirecciones().add(direccion);

        this.clienteRepositorio.save(cliente);

        return "Se agrego una nueva direccion al cliente con DNI: " + dni + "!";
    }


    public ClienteDTO buscarClientePorId(String dni){
        if(!this.clienteRepositorio.existsById(dni)){
            return null;
        }

        Cliente cliente = this.clienteRepositorio.findById(dni).get();
        ClienteDTO clienteDTO = SingletonModelMapper.getModelMapperInstance().map(cliente, ClienteDTO.class);

        for (Direcciones direccion : cliente.getDirecciones()) {
            DireccionesDTO direccionDTO = SingletonModelMapper.getModelMapperInstance().map(direccion, DireccionesDTO.class);
            clienteDTO.getDireccionesDTO().add(direccionDTO);
        }
        
        return clienteDTO;
    }
    

}
