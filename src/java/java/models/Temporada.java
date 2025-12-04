package models;

import models.enums.NombreTemporada;
import java.time.LocalDate;

public class Temporada extends ClaseBase {
    private int idTemporada;
    
    private NombreTemporada nombre;
    
    private LocalDate fechaInicio;
    
    private LocalDate fechaFin;
    
    private double modificadorPrecio;

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