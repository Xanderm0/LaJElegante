package entities;

import entities.enums.Estado;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "mesas")
public class Mesa extends ClaseBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mesa")
    private int idMesa;
    
    @Min(value = 1, message = "El número de mesa debe ser mayor a 0")
    @Max(value = 127, message = "El número de mesa no puede exceder 127")
    @Column(name = "numero_mesa")
    private int numeroMesa;
    
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    @Max(value = 12, message = "La capacidad no puede exceder 12")
    @Column(name = "capacidad")
    private int capacidad;
    
    @NotBlank(message = "La zona es obligatoria")
    @Size(max = 255, message = "La zona no puede exceder 255 caracteres")
    @Column(name = "zona")
    private String zona;
    
    @NotBlank(message = "La ubicación es obligatoria")
    @Size(max = 255, message = "La ubicación no puede exceder 255 caracteres")
    @Column(name = "ubicacion_detalle")
    private String ubicacionDetalle;
    
    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "activo")
    private Estado estado = Estado.ACTIVO;

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getUbicacionDetalle() {
        return ubicacionDetalle;
    }

    public void setUbicacionDetalle(String ubicacionDetalle) {
        this.ubicacionDetalle = ubicacionDetalle;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
    
}
