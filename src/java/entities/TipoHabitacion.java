package entities;
import entities.enums.NombreTipoHabitacion;
import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "tipo_habitacion")
public class TipoHabitacion extends ClaseBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_habitacion")
    private int idTipoHabitacion;
    
    @NotNull(message = "El nombre del tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "nombre_tipo")
    private NombreTipoHabitacion nombreTipo;
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(name = "descripcion")
    private String descripcion;
    
    @Min(value = 1, message = "La capacidad máxima debe ser mayor a 0")
    @Max(value = 8, message = "La capacidad máxima no puede exceder 8")
    @Column(name = "capacidad_maxima")
    private int capacidadMaxima;

    public int getIdTipoHabitacion() {
        return idTipoHabitacion;
    }

    public void setIdTipoHabitacion(int idTipoHabitacion) {
        this.idTipoHabitacion = idTipoHabitacion;
    }

    public NombreTipoHabitacion getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(NombreTipoHabitacion nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }
}