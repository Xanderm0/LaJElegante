package models;

import models.enums.Estado;

public class Tarifa extends ClaseBase{
    private int idTarifa;
    
    private float tarifaFija;
    
    private float precioFinal;
    
    private Estado estado = Estado.VIGENTE;
    
    private TipoHabitacion tipoHabitacion;
    
    private Temporada temporada;

    public int getIdTarifa() {
        return idTarifa;
    }

    public void setIdTarifa(int idTarifa) {
        this.idTarifa = idTarifa;
    }

    public float getTarifaFija() {
        return tarifaFija;
    }

    public void setTarifaFija(float tarifaFija) {
        this.tarifaFija = tarifaFija;
    }

    public float getPrecioFinal() {
        return precioFinal;
    }

    public void setPrecioFinal(float precioFinal) {
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