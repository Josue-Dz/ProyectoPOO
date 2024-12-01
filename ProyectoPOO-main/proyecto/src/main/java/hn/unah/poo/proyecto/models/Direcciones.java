package hn.unah.poo.proyecto.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name="iddireccion")
    private int idDireccion;

    private String pais;
    
    private String departamento;

    private String ciudad;

    private String colonia;

    private String referencia;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="dni",  referencedColumnName = "dni")
    @JsonIgnore
    private Cliente cliente;

}
