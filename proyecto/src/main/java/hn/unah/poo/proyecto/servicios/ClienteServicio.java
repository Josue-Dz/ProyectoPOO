package hn.unah.poo.proyecto.servicios;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hn.unah.poo.proyecto.dtos.ClienteDTO;
import hn.unah.poo.proyecto.dtos.DireccionesDTO;
import hn.unah.poo.proyecto.dtos.PrestamosDTO;
import hn.unah.poo.proyecto.dtos.TablaAmortizacionDTO;
import hn.unah.poo.proyecto.models.Cliente;
import hn.unah.poo.proyecto.models.Direcciones;
import hn.unah.poo.proyecto.models.Prestamos;
import hn.unah.poo.proyecto.models.TablaAmortizacion;
import hn.unah.poo.proyecto.repositories.ClienteRepositorio;
import hn.unah.poo.proyecto.repositories.DireccionesRepositorio;
import hn.unah.poo.proyecto.singleton.SingletonModelMapper;

@Service
public class ClienteServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private DireccionesRepositorio direccionesRepositorio;

    /**
    * Crea un nuevo cliente en la base de datos.
     * Este método realiza las siguientes operaciones:
     * - Verifica si el cliente ya existe en la base de datos mediante su DNI.
     * - Valida que el cliente tenga al menos una dirección y no más de dos.
     * - Asocia cada dirección al cliente antes de guardar los datos.
     * - Almacena el cliente y sus direcciones en la base de datos.
     * @param nvoClienteDTO
     * @return Un mensaje indicando si el cliente fue creado exitosamente, o una descripción del error
     *         en caso de que no se haya podido realizar la operación.
     */
    
    public String crearCliente(ClienteDTO nvoClienteDTO){
        try {
            if (this.clienteRepositorio.existsById(nvoClienteDTO.getDni())){
                return "El cliente con DNI: " + nvoClienteDTO.getDni() + " ya existe. Por lo que no se puede agregar!";
            }
    
            Cliente nvoCliente = SingletonModelMapper.getModelMapperInstance().map(nvoClienteDTO, Cliente.class);
            
    
            if(nvoCliente.getDirecciones() == null){
                return "Información incompleta. Para poder agregar este cliente necesita agregar al menos una dirección";
            }else if(nvoCliente.getDirecciones().size()>2){
                return "El cliente a agregar no puede tener más de dos direcciones. Por favor intentelo nuevamente.";
            }
            
    
            for (Direcciones direccion : nvoCliente.getDirecciones()) {
                direccion.setCliente(nvoCliente);
                this.direccionesRepositorio.save(direccion);
            }
    
            
            this.clienteRepositorio.save(nvoCliente);
            return "El cliente " + nvoClienteDTO.getNombre() + " ha sido agregado exitosamente.";
        } catch (Exception e) {
            return "No ha sido posible completar la acción " + e;
        }
        
    }


    /**
     * * Agrega una nueva dirección a un cliente existente en la base de datos.
     * Este método verifica que:
     * - El cliente existe en la base de datos.
     * - El cliente no tiene más de dos direcciones asignadas.
     * 
     * @param dni
     * @param direccionDTO
     * @return Mensaje indicando si la direccion fue agregada al cliente existente.
     */

    public String agregarDireccion(String dni, DireccionesDTO direccionDTO){
        try {
            if(direccionDTO == null){
                return "No se han llenado los campos de la direccion";
            }
    
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
        } catch (Exception e) {
            return "No ha sido posible completar la acción " + e;
        }
        
    }


    /**
     * Busca un cliente por su DNI y retorna toda su información en formato DTO.
     * 
     * Este método realiza las siguientes operaciones:
     * - Verifica si el cliente existe en la base de datos.
     * - Si existe, convierte la entidad `Cliente` a un objeto `ClienteDTO`.
     * - Mapea las direcciones asociadas al cliente en una lista de `DireccionesDTO`.
     * - Mapea los préstamos asociados al cliente en un conjunto de `PrestamosDTO`,
     *   incluyendo las tablas de amortización correspondientes.
     * @param dni
     * @return Un objeto `ClienteDTO` con toda la información del cliente, o `null` si el cliente no existe.
     */

     public Optional<ClienteDTO> buscarClientePorId(String dni) {
        try {

            // Obtener el cliente desde el repositorio
            Cliente cliente = this.clienteRepositorio.findById(dni).get();
            ClienteDTO clienteDTO = SingletonModelMapper.getModelMapperInstance().map(cliente, ClienteDTO.class);
    
            // Mapear las direcciones
            clienteDTO.setDireccionesDTO(new ArrayList<>());
            if (cliente.getDirecciones() != null) {
                for (Direcciones direccion : cliente.getDirecciones()) {
                    DireccionesDTO direccionDTO = SingletonModelMapper.getModelMapperInstance().map(direccion, DireccionesDTO.class);
                    clienteDTO.getDireccionesDTO().add(direccionDTO);
                }
            }
    
            // Mapear los préstamos y sus tablas de amortización
            clienteDTO.setPrestamosDTO(new HashSet<>());
            if (cliente.getPrestamos() != null) {
                for (Prestamos prestamo : cliente.getPrestamos()) {
                    PrestamosDTO prestamoDTO = SingletonModelMapper.getModelMapperInstance().map(prestamo, PrestamosDTO.class);
                    prestamoDTO.setTablaAmortizacionDTO(new ArrayList<>());
    
                    for (TablaAmortizacion cuota : prestamo.getTablaAmortizacion()) {
                        TablaAmortizacionDTO cuotaDTO = SingletonModelMapper.getModelMapperInstance().map(cuota, TablaAmortizacionDTO.class);
                        prestamoDTO.getTablaAmortizacionDTO().add(cuotaDTO);
                    }
                    clienteDTO.getPrestamosDTO().add(prestamoDTO);

                    return Optional.of(clienteDTO);
                }
            }
    
            // Retornar el cliente mapeado dentro de un Optional
            return Optional.of(clienteDTO);
        } catch (Exception e) {
           
            return Optional.empty();
        }
    }

    /**
     * Recupera la lista completa de clientes registrados en la base de datos.
     * Este método realiza las siguientes operaciones:
     * - Consulta todos los clientes almacenados en la base de datos.
     * - Convierte cada entidad `Cliente` a un objeto `ClienteDTO`.
     * - Mapea las direcciones asociadas al cliente en una lista de `DireccionesDTO`.
     * - Mapea los préstamos asociados al cliente en un conjunto de `PrestamosDTO`.
     * @return Una lista de objetos `ClienteDTO`, donde cada elemento contiene la información completa
     *         de un cliente, incluyendo direcciones y préstamos asociados.
     */

    public List<ClienteDTO> obtenerTodos() {

        List<Cliente> listaClientes = clienteRepositorio.findAll();
        List<ClienteDTO> listaClientesDTO = new ArrayList<>();

        for (Cliente cliente : listaClientes) {
            ClienteDTO clienteDTO = SingletonModelMapper.getModelMapperInstance().map(cliente, ClienteDTO.class);
            clienteDTO.setDireccionesDTO(new ArrayList<>());

            if (cliente.getDirecciones() != null) {
                for (Direcciones direccion : cliente.getDirecciones()) {
                    DireccionesDTO direccionDTO = SingletonModelMapper.getModelMapperInstance().map(direccion, DireccionesDTO.class);
                    clienteDTO.getDireccionesDTO().add(direccionDTO);
                }
            }

            clienteDTO.setPrestamosDTO(new HashSet<>());
            if (cliente.getPrestamos() != null) {
                for (Prestamos prestamo : cliente.getPrestamos()) {
                    PrestamosDTO prestamosDTO = SingletonModelMapper.getModelMapperInstance().map(prestamo, PrestamosDTO.class);
                    clienteDTO.getPrestamosDTO().add(prestamosDTO);
                }
            }

            listaClientesDTO.add(clienteDTO);
        }

        return listaClientesDTO;
    }

/**
 * Elimina un cliente de la base de datos si cumple con las condiciones necesarias.
 * 
 * Este método realiza las siguientes operaciones:
 * - Verifica si el cliente existe en la base de datos.
 * - Comprueba si el cliente tiene préstamos con saldo pendiente.
 * - Si el cliente no tiene préstamos con saldo pendiente, procede a eliminarlo.
 * 
 * @param dni
 * @return Un mensaje indicando si la eliminación fue exitosa o enviando una excepcion.
 */

public String eliminarCliente(String dni) {
    try {
        if (!this.clienteRepositorio.existsById(dni)) {
            return "El cliente con DNI: " + dni + " no existe. No se puede eliminar.";
        }

        Cliente cliente = this.clienteRepositorio.findById(dni).get();

        // Verificar si el cliente tiene préstamos con saldo pendiente
        for (Prestamos prestamo : cliente.getPrestamos()) {
            if(prestamo.getEstado() == 'P'){
                return "El cliente con DNI: " + dni + " no puede ser eliminado, posee prestamo(s) con saldo pendiente!";
            }
        }

        // Si no hay saldo pendiente, proceder a eliminar el cliente
        this.clienteRepositorio.delete(cliente);
        return "El cliente con DNI: " + dni + " ha sido eliminado exitosamente.";
    } catch (Exception e) {
        return "No ha sido posible completar la acción. Verifique los datos!\n" + e;
    }
}

/**
 *Actualiza la información de un cliente existente en la base de datos.
 * 
 * Este método realiza las siguientes operaciones:
 * - Verifica si el cliente con el DNI proporcionado existe en la base de datos.
 * - Actualiza los campos modificables del cliente, como correo, sueldo y teléfono.
 * - Actualiza las direcciones asociadas al cliente, reemplazando las existentes con las nuevas proporcionadas.
 * - Almacena los cambios en la base de datos.
 * @param dni
 * @param clienteDTO
 * @return Un mensaje indicando si la actualización fue exitosa o enviando una excepcion.
 */
 
 public String actualizarCliente(String dni, ClienteDTO clienteDTO) {

    try {
        if (!this.clienteRepositorio.existsById(dni)) {
            return "El cliente con DNI: " + dni + " no existe. No se puede actualizar.";
            
        }

        Cliente clienteExistente = this.clienteRepositorio.findById(dni).get();

        // Actualizar campos permitidos
        if (clienteDTO.getCorreo() == null || clienteDTO.getSueldo() == 0 || clienteDTO.getTelefono() == null) {
            return "No es posible actualizar la información mientras sueldo,"
                   + " telefono o el correo se encuentren vacíos!";
        }

        clienteExistente.setCorreo(clienteDTO.getCorreo());
        clienteExistente.setTelefono(clienteDTO.getTelefono());
        clienteExistente.setSueldo(clienteDTO.getSueldo());

        if (clienteDTO.getPrestamosDTO() != null){
            return "No es posible actualizar la información sobre los prestamos del cliente,"
            + " solamente direcciones y algunos datos personales!.";
        }

        // Actualizar direcciones con límite de 2 direcciones
        if (clienteDTO.getDireccionesDTO() != null) {
            if (clienteDTO.getDireccionesDTO().size() > 2) {
                return "El cliente no puede tener más de 2 direcciones. Intente nuevamente.";
            }

            int i = 0;
            if(clienteDTO.getDireccionesDTO().size() <= 2){
                List<Direcciones> listaDirecciones = new ArrayList<>();
                List<DireccionesDTO> listaDireccionesDTO = clienteDTO.getDireccionesDTO();

                for (DireccionesDTO direccionDTO : clienteDTO.getDireccionesDTO()){
                    Direcciones direccion = SingletonModelMapper.getModelMapperInstance().map(direccionDTO, Direcciones.class);
                    listaDirecciones.add(direccion);
                }

                
                    clienteExistente.getDirecciones().get(i).setPais(listaDirecciones.get(i).getPais());
                    clienteExistente.getDirecciones().get(i).setDepartamento(listaDirecciones.get(i).getDepartamento());
                    clienteExistente.getDirecciones().get(i).setCiudad(listaDirecciones.get(i).getCiudad());
                    clienteExistente.getDirecciones().get(i).setColonia(listaDirecciones.get(i).getColonia());
                    clienteExistente.getDirecciones().get(i).setReferencia(listaDirecciones.get(i).getReferencia());

                    if(clienteDTO.getDireccionesDTO().size() > clienteExistente.getDirecciones().size()){
                        agregarDireccion(dni, listaDireccionesDTO.get(i+1));
                    }
                clienteDTO.getDireccionesDTO().clear();
                
            }
            
        }

        // Guardar cambios en el repositorio
        this.clienteRepositorio.save(clienteExistente);
        return "El cliente con DNI: " + dni + " ha sido actualizado exitosamente.";
    } catch (Exception e) {
        return "No ha sido posible completar la acción. Error: " + e.getMessage();
    }
}



}
