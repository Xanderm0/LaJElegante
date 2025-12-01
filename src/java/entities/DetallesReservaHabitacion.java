package entities;

import java.util.Date;

public class DetallesReservaHabitacion extends ClaseBase {
    private int idDetalleReservaHab;
    
    private Habitacion habitacion;
    
    private int cantidadPersonas;
    
    private int cantidadNoches;
    
    private double precioNoche;
    
    private double descuentoAplicado = 0.0;
    
    private double recargoAplicado = 0.0;
    
    private double precioReserva;
    
    private String observacion;
    
    private Date fechaInicio;
    
    private Date fechaFin;
    
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