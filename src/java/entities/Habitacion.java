package entities;

import javax.persistence.*;
import javax.validation.constraints.*;
import entities.enums.EstadoHabitacion;

@Entity
@Table(name = "habitaciones")
public class Habitacion extends ClaseBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private int idHabitacion;
    
    @NotNull(message = "El tipo de habitacion es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_tipo_habitacion")
    private TipoHabitacion tipoHabitacion; 
   
    @NotNull(message = "El número de la habitacion es obligatorio")
    @Column(name = "numero_habitacion" , unique = true)
    private int numeroHabitacion;
    
    @NotNull(message = "El estado de la habitación es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_habitacion")
    private EstadoHabitacion estadoHabitacion = EstadoHabitacion.EN_SERVICIO;

    public int getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public int getNumeroHabitacion() {
        return numeroHabitacion;
    }

    public void setNumeroHabitacion(int numeroHabitacion) {
        this.numeroHabitacion = numeroHabitacion;
    }

    public EstadoHabitacion getEstadoHabitacion() {
        return estadoHabitacion;
    }

    public void setEstadoHabitacion(EstadoHabitacion estadoHabitacion) {
        this.estadoHabitacion = estadoHabitacion;
    }   
}
