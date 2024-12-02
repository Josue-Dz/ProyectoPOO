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
    /**
     * Id de la clase DireccionDTO
     */
    private int idDirecciones;

    /**
     * Pais del clienteDTO
     */
    private String pais;
    /**
     * Departamento del clienteDTO
     */
    private String departamento;
    /**
     * Ciudad del clienteDTO
     */
    private String ciudad;
    /**
     * Colonia del clienteDTO
     */
    private String colonia;
    /**
     * Referencia de donde vive el clienteDTO
     */
    private String referencia;
    /**
     * Objeto clienteDTO que contiene la informacion del mismo en la clase
     * DireccionDTO.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ClienteDTO clienteDTO;
}
