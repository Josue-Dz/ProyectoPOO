package hn.unah.poo.proyecto.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hn.unah.poo.proyecto.enumeration.TipoPrestamo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name="prestamos")
public class Prestamos {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idprestamo")
    private int idPrestamo;

    private double monto;

    private int plazo;

    @Column(name = "tasa_interes")
    private double tasaInteres;

    private double cuota;

    private char estado;

   @Column(name = "tipo_prestamo")
    private TipoPrestamo tipoPrestamo;

    @ManyToMany(mappedBy= "prestamos", cascade = CascadeType.ALL)
    private Set<Cliente> clientes = new HashSet<>();

    @OneToMany(mappedBy= "prestamos", cascade = CascadeType.ALL)
    private List<TablaAmortizacion> tablaAmortizacion;

}
