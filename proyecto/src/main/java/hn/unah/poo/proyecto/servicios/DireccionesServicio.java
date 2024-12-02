package hn.unah.poo.proyecto.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hn.unah.poo.proyecto.dtos.ClienteDTO;
import hn.unah.poo.proyecto.dtos.DireccionesDTO;
import hn.unah.poo.proyecto.models.Cliente;
import hn.unah.poo.proyecto.models.Direcciones;
import hn.unah.poo.proyecto.repositories.ClienteRepositorio;
import hn.unah.poo.proyecto.repositories.DireccionesRepositorio;
import hn.unah.poo.proyecto.singleton.SingletonModelMapper;

@Service
public class DireccionesServicio {

    @Autowired
    private DireccionesRepositorio direccionRepositorio;
    @Autowired
    private ClienteRepositorio clienteRepositorio;

    public List<DireccionesDTO> obtenerDirecciones() {
        try {
            // Obtener todas las direcciones desde el repositorio
            List<Direcciones> direcciones = this.direccionRepositorio.findAll();

            // Convertir cada Dirección a DirecciónDTO y asignar el ClienteDTO si existe
            List<DireccionesDTO> direccionesDTO = direcciones.stream()
                    .map(direccion -> {
                        // Mapear la dirección a DirecciónDTO
                        DireccionesDTO direccionDTO = SingletonModelMapper.getModelMapperInstance().map(direccion,
                                DireccionesDTO.class);

                        // Si la dirección tiene un cliente asociado, mapearlo a ClienteDTO y asignarlo
                        if (direccion.getCliente() != null) {
                            ClienteDTO clienteDTO = SingletonModelMapper.getModelMapperInstance()
                                    .map(direccion.getCliente(), ClienteDTO.class);
                            direccionDTO.setClienteDTO(clienteDTO); // Establecer el clienteDTO
                        }

                        return direccionDTO;
                    })
                    .collect(Collectors.toList()); // Recoger los resultados en una lista

            // Devolver la lista de DireccionDTOs con el ClienteDTO incluido
            return direccionesDTO;
        } catch (Exception e) {
            // En caso de error, devolver una lista vacía
            return new ArrayList<>();
        }
    }

    /**
     * Metodo que crea las direcciones de un cliente
     *
     * @param direccionDTO
     * @return
     */
    public String crearDireccion(DireccionesDTO direccionDTO) {
        try {
            // Convertir el DTO a la entidad Direccion
            Direcciones direccion = SingletonModelMapper.getModelMapperInstance().map(direccionDTO, Direcciones.class);

            // Verificar si el cliente asociado a la dirección existe
            Cliente cliente = direccion.getCliente(); // Obtener el cliente asociado
            if (cliente == null || cliente.getDni() == null) {
                return "Error: El cliente asociado a la dirección no es válido.";
            }

            // Verificar si el cliente existe en la base de datos
            Optional<Cliente> clienteExistente = this.clienteRepositorio.findById(cliente.getDni());
            if (!clienteExistente.isPresent()) {
                return "Error: El cliente no existe en el sistema.";
            }

            // Verificar cuántas direcciones tiene el cliente
            List<Direcciones> direccionesCliente = clienteExistente.get().getDirecciones();
            if (direccionesCliente.size() >= 2) {
                return "Error: El cliente ya tiene dos direcciones registradas. No se puede agregar más.";
            }

            // Si el cliente existe y tiene menos de dos direcciones, guardar la nueva
            // dirección
            direccion.setCliente(clienteExistente.get()); // Asignar el cliente a la nueva dirección
            this.direccionRepositorio.save(direccion);

            return "Se ha guardado la dirección al usuario.";
        } catch (Exception e) {
            return "Ha ocurrido un error: " + e.getMessage();
        }
    }

    public String actualizarDireccion(int idDireccion, DireccionesDTO direccionDTO) {
        // Verificar si la direccion existe en el sistema
        if (!this.direccionRepositorio.existsById(idDireccion)) {
            return "El id de la direccion: " + idDireccion + " no existe en el sistema";
        }
        try {
            // Mapear la direccionDTO al modelo Direccion
            Direcciones direccion = SingletonModelMapper.getModelMapperInstance().map(direccionDTO, Direcciones.class);
            if (direccion.getIdDireccion() == 0) {
                direccion.setIdDireccion(idDireccion);
            }
            if (direccion.getIdDireccion() != idDireccion) {
                return "La direccion con id: " + direccion.getIdDireccion()
                        + " enviado en el JSON es diferente que el id: " + idDireccion
                        + " enviado por el usuario";
            }

            // Guardar la direccion actualizado en la base de datos
            this.direccionRepositorio.save(direccion);

            return "Se ha actualizado la direccion con id: " + idDireccion;
        } catch (Exception e) {
            // En caso de algún error, se captura la excepción
            return "Ocurrió un error al actualizar el cliente: " + e.getMessage();
        }
    }

    public String eliminarDireccion(int idDireccion) {
        if (!this.direccionRepositorio.existsById(idDireccion)) {
            return "No hay necesidad de eliminar la direccion con id: " + idDireccion
                    + " porque no existe en el sistema.";

        }
        this.direccionRepositorio.deleteById(idDireccion);
        return "La direccion con id: " + idDireccion + " se ha eliminado del sistema";
    }

}
