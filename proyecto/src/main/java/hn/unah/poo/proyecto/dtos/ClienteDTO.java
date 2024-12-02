package hn.unah.poo.proyecto.dtos;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClienteDTO {
  /**
     * DNI del clienteDTO
     */
    @NotBlank(message = "El DNI es obligatorio.")
private String dni;

 /**
     * Nombre del clienteDTO
     */
    @NotBlank(message = "El nombre es obligatorio.")
private String nombre;

/**
     * Apellido del clienteDTO
     */
    @NotBlank(message = "El apellido es obligatorio.")
    private String apellido;

    /**
     * Teléfono del clienteDTO
     */
    @Size(max = 20, message = "El teléfono no debe exceder 20 caracteres.")
    private String telefono;

    /**
     * Correo del clienteDTO
     */
    @Size(max = 100, message = "El correo no debe exceder 100 caracteres.")
    private String correo;

    /**
     * Sueldo del clienteDTO
     */
    private double sueldo;

private List<DireccionesDTO> direccionesDTO;

private Set<PrestamosDTO> prestamosDTO;
}
