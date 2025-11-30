package entities;

import entities.enums.Estado;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tarifas")
public class Tarifa extends ClaseBase{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarifa")
    private int idTarifa;
    
    @NotNull(message = "La tarifa fija es obligatoria")
    @DecimalMin(value = "0.0", message = "La tarifa no puede ser negativa")
    @Digits(integer = 8, fraction = 2, message = "Máximo 8 enteros y 2 decimales")
    @Column(name = "tarifa_fija")
    private BigDecimal tarifaFija;
    
    @DecimalMin(value = "0.0", message = "El precio final no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "Máximo 8 enteros y 2 decimales")
    @Column(name = "precio_final")
    private BigDecimal precioFinal = BigDecimal.ZERO;
    
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private Estado estado = Estado.VIGENTE;
    
    @NotNull(message = "El tipo de habitación es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_tipo_habitacion")
    private TipoHabitacion tipoHabitacion;
    
    @NotNull(message = "La temporada es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_temporada")
    private Temporada temporada;

    public int getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(int idTarifa) {
        this.idTarifa = idTarifa;
    }

    public BigDecimal getTarifaFija() {
        return tarifaFija;
    }

    public void setTarifaFija(BigDecimal tarifaFija) {
        this.tarifaFija = tarifaFija;
    }

    public BigDecimal getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(BigDecimal precioFinal) {
        this.precioFinal = precioFinal;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public Temporada getTemporada() {
        return temporada;
    }

    public void setTemporada(Temporada temporada) {
        this.temporada = temporada;
    }
}