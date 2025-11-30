package entities;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "detalles_reserva_habitacion")
public class DetallesReservaHabitacion extends ClaseBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_reserva_hab")
    private int idDetalleReservaHab;
    
    @NotNull(message = "La habitación es obligatoria")
    @OneToOne
    @JoinColumn(name = "id_habitacion")
    private Habitacion habitacion;
    
    @Min(value = 1, message = "Mínimo 1 persona")
    @Max(value = 8, message = "Máximo 8 personas")
    @Column(name = "cantidad_personas")
    private int cantidadPersonas;
    
    @Min(value = 1, message = "Mínimo 1 noche")
    @Max(value = 30, message = "Máximo 30 noches")
    @Column(name = "noches")
    private int cantidadNoches;
    
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "Máximo 8 enteros y 2 decimales")
    @Column(name = "precio_noche")
    private double precioNoche;
    
    @DecimalMin(value = "0.0", message = "El descuento no puede ser negativo")
    @Digits(integer = 3, fraction = 2, message = "Máximo 3 enteros y 2 decimales")
    @Column(name = "descuento_aplicado")
    private double descuentoAplicado = 0.0;
    
    @DecimalMin(value = "0.0", message = "El recargo no puede ser negativo")
    @Digits(integer = 3, fraction = 2, message = "Máximo 3 enteros y 2 decimales")
    @Column(name = "recargo_aplicado")
    private double recargoAplicado = 0.0;
    
    @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
    @Digits(integer = 8, fraction = 2, message = "Máximo 8 enteros y 2 decimales")
    @Column(name = "precio_reserva")
    private double precioReserva;
    
    @Size(max = 65535, message = "Máximo 65535 caracteres")
    @Column(name = "observaciones")
    private String observacion;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    @Column(name = "fecha_inicio")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    @Column(name = "fecha_fin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    
    @AssertTrue(message = "La fecha fin debe ser después de la fecha inicio")
    public boolean isFechasValidas() {
        if (fechaInicio == null || fechaFin == null) {
            return true;
        }
        return fechaFin.after(fechaInicio);
    }
    
    public int getIdDetalleReservaHab() {
        return idDetalleReservaHab;
    }

    public void setIdDetalleReservaHab(int idDetalleReservaHab) {
        this.idDetalleReservaHab = idDetalleReservaHab;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }
    
    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public int getCantidadNoches() {
        return cantidadNoches;
    }

    public void setCantidadNoches(int cantidadNoches) {
        this.cantidadNoches = cantidadNoches;
    }

    public double getPrecioNoche() {
        return precioNoche;
    }

    public void setPrecioNoche(double precioNoche) {
        this.precioNoche = precioNoche;
    }

    public double getDescuentoAplicado() {
        return descuentoAplicado;
    }

    public void setDescuentoAplicado(double descuentoAplicado) {
        this.descuentoAplicado = descuentoAplicado;
    }

    public double getRecargoAplicado() {
        return recargoAplicado;
    }

    public void setRecargoAplicado(double recargoAplicado) {
        this.recargoAplicado = recargoAplicado;
    }

    public double getPrecioReserva() {
        return precioReserva;
    }

    public void setPrecioReserva(double precioReserva) {
        this.precioReserva = precioReserva;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
}