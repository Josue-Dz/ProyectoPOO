package hn.unah.poo.proyecto.dtos;

import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {
 
private String dni;

private String nombre;

private String apellido;

private String telefono;

private String correo;

private int sueldo;

private List<DireccionesDTO> direccionesDTO;

private Set<PrestamosDTO> prestamosDTO;
}
