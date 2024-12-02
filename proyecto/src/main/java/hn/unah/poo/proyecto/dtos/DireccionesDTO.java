package hn.unah.poo.proyecto.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DireccionesDTO {
  
    private int idDirecciones;

    private String pais;
    
    private String departamento;
   
    private String ciudad;
   
    private String colonia;
    
    private String referencia;
    /**
     * Objeto clienteDTO que contiene la informacion del mismo en la clase
     * DireccionDTO.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ClienteDTO clienteDTO;
}
