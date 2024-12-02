package hn.unah.poo.proyecto.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="direcciones")
public class Direcciones {
  /**
 * Id de la tabla Direccion
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name="iddireccion")
    private int idDireccion;

   /**
 * Pais del cliente
     */
    private String pais;
    /**
     * Departamento del cliente
     */
    private String departamento;
    /**
     * Ciudad del cliente
     */
    private String ciudad;
    /**
 * Colonia del cliente
     */
    private String colonia;

    /**
     * Referencia de donde vive el cliente
     */
    private String referencia;
  /**
     * Objeto cliente que contiene la informacion del mismo en la Entidad Direccion.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="dni",  referencedColumnName = "dni")
    @JsonIgnore
    private Cliente cliente;

}
