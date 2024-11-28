package hn.unah.poo.proyecto.servicios;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hn.unah.poo.proyecto.dtos.ClienteDTO;
import hn.unah.poo.proyecto.dtos.DireccionesDTO;
import hn.unah.poo.proyecto.models.Cliente;
import hn.unah.poo.proyecto.models.Direcciones;
import hn.unah.poo.proyecto.repositories.ClienteRepositorio;
import hn.unah.poo.singleton.SingletonModelMapper;

@Service
public class ClienteServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    public String crearCliente(ClienteDTO nvoClienteDTO){
        if (this.clienteRepositorio.existsById(nvoClienteDTO.getDni())){
            return "El cliente con DNI: " + nvoClienteDTO.getDni() + " ya existe. Por lo que no se puede agregar!";
        }

        Cliente nvoCliente = SingletonModelMapper.getModelMapperInstance().map(nvoClienteDTO, Cliente.class);

        if(nvoCliente.getDirecciones().isEmpty()){
            return "Información incompleta. Para poder agregar este cliente necesita agregar al menos una dirección";
        }

        List<DireccionesDTO> direccionesDTO = nvoClienteDTO.getDireccionesDTO();
        List<Direcciones> direcciones;
        
        for (DireccionesDTO direccionDTO : direccionesDTO) {
            
            
        }
        return "El cliente " + nvoClienteDTO.getNombre() + " ha sido agregado exitosamente.";
    }


}
