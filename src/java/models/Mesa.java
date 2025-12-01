package models;

import models.enums.Estado;

public class Mesa extends ClaseBase {
    private int idMesa;

    private int numeroMesa;

    private int capacidad;
    
    private String zona;

    private String ubicacionDetalle;

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
