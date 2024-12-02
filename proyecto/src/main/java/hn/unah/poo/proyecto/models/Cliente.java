package hn.unah.poo.proyecto.models;

import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {
    /**
     * DNI del cliente
     */
    @Id
    private String dni;
    /**
     * Nombre del Cliente
     */
    private String nombre;
    /**
     * Apellido del Cliente
     */
    private String apellido;
    /**
     * Telefono del cliente
     */
    private String telefono;
    /**
     * Correo del cliente
     */
    private String correo;
    /**
     * Sueldo del cliente
     */
    @Column(columnDefinition = "DECIMAL(14,2)")
    private double sueldo;

     /**
 * Lista de Direcciones del cliente
     */
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Direcciones> direcciones;
  /**
     * Set de Prestamos del cliente
     */
    @ManyToMany
    @JoinTable(name = "cliente_prestamos", joinColumns = @JoinColumn(name = "dni"), inverseJoinColumns = @JoinColumn(name = "idprestamo"))
    private Set<Prestamos> prestamos;

}
