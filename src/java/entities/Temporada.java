package entities;

import entities.enums.NombreTemporada;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "temporadas")
public class Temporada extends ClaseBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_temporada")
    private int idTemporada;
    
    @NotNull(message = "El nombre de temporada es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "nombre")
    private NombreTemporada nombre;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;
    
    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;
    
    @NotNull(message = "El modificador de precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El modificador no puede ser negativo")
    @Digits(integer = 3, fraction = 2, message = "Formato porcentual: máximo 3 enteros y 2 decimales")
    @Column(name = "modificador_precio")
    private double modificadorPrecio;

    @AssertTrue(message = "La fecha fin debe ser después de la fecha inicio")
    public boolean isFechasValidas() {
        if (fechaInicio == null || fechaFin == null) {
            return true;
        }
        return fechaFin.isAfter(fechaInicio);
    }

    public int getIdTemporada() {
        return idTemporada;
    }

    public void setIdTemporada(int idTemporada) {
        this.idTemporada = idTemporada;
    }

    public NombreTemporada getNombre() {
        return nombre;
    }

    public void setNombre(NombreTemporada nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getModificadorPrecio() {
        return modificadorPrecio;
    }

    public void setModificadorPrecio(double modificadorPrecio) {
        this.modificadorPrecio = modificadorPrecio;
    }
}