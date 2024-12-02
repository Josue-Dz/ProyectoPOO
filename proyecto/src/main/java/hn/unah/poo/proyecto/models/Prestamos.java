package hn.unah.poo.proyecto.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import hn.unah.poo.proyecto.enumeration.TipoPrestamo;
import hn.unah.poo.proyecto.enumeration.TipoPrestamoConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

    @Column(columnDefinition = "DECIMAL(14,2)")
    private double monto;
   
    private int plazo;
    
    @Column(columnDefinition = "DECIMAL(14,2)")
    private double tasaInteres;
    
    @Column(columnDefinition = "DECIMAL(14,2)")
    private double cuota;
   
    private char estado;
    
    @Convert(converter = TipoPrestamoConverter.class)
    @Column(name = "tipo_prestamo")
    private TipoPrestamo tipoPrestamo;

    @ManyToMany(mappedBy= "prestamos", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Cliente> clientes = new HashSet<>();

    @OneToMany(mappedBy= "prestamos", cascade = CascadeType.ALL)
    private List<TablaAmortizacion> tablaAmortizacion;

    public void setTipoPrestamo(String tipoPrestamo){
        this.tipoPrestamo = TipoPrestamo.fromCode(tipoPrestamo.charAt(0));
    }
}
