package models;

import models.enums.NombreTipoHabitacion;

public class TipoHabitacion extends ClaseBase {
    private int idTipoHabitacion;
    
    private NombreTipoHabitacion nombreTipo;
    
    private String descripcion;
    
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