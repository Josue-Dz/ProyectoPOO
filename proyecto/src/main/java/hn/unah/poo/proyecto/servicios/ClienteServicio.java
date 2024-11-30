package hn.unah.poo.proyecto.servicios;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
     * 
     * @param nvoClienteDTO
     * @return
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
     * 
     * @param dni
     * @param direccionDTO
     * @return
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
     * 
     * @param dni
     * @return
     */
    public ClienteDTO buscarClientePorId(String dni){
       
        if(!this.clienteRepositorio.existsById(dni)){
            return null;
        }

        Cliente cliente = this.clienteRepositorio.findById(dni).get();
        ClienteDTO clienteDTO = SingletonModelMapper.getModelMapperInstance().map(cliente, ClienteDTO.class);

        clienteDTO.setDireccionesDTO(new ArrayList<>());
        if (cliente.getDirecciones() != null){
            for (Direcciones direccion : cliente.getDirecciones()) {
                DireccionesDTO direccionDTO = SingletonModelMapper.getModelMapperInstance().map(direccion, DireccionesDTO.class);
                clienteDTO.getDireccionesDTO().add(direccionDTO);
            }
        }

        clienteDTO.setPrestamosDTO(new HashSet<>());
        if (cliente.getPrestamos() != null){
            for(Prestamos prestamo : cliente.getPrestamos()){
                PrestamosDTO prestamoDTO = SingletonModelMapper.getModelMapperInstance().map(prestamo, PrestamosDTO.class);
                prestamoDTO.setTablaAmortizacionDTO(new ArrayList<>());

                for(TablaAmortizacion cuota : prestamo.getTablaAmortizacion()){
                    TablaAmortizacionDTO cuotaDTO = SingletonModelMapper.getModelMapperInstance().map(cuota, TablaAmortizacionDTO.class);
                    prestamoDTO.getTablaAmortizacionDTO().add(cuotaDTO);
                }
                clienteDTO.getPrestamosDTO().add(prestamoDTO);
            }
        }
        

        return clienteDTO;
    }


    /**
     * 
     * @return
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
    }}
