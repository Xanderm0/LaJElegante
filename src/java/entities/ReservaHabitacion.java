package entities;

public class ReservaHabitacion extends ClaseBase {
    private int idReservaHabitacion;
    
    private Cliente cliente;
    
    private DetallesReservaHabitacion detalleReservaHabitacion;

    public int getIdReservaHabitacion() {
        return idReservaHabitacion;
    }

    public void setIdReservaHabitacion(int idReservaHabitacion) {
        this.idReservaHabitacion = idReservaHabitacion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public DetallesReservaHabitacion getDetalleReservaHabitacion() {
        return detalleReservaHabitacion;
    }

    public void setDetalleReservaHabitacion(DetallesReservaHabitacion detalleReservaHabitacion) {
        this.detalleReservaHabitacion = detalleReservaHabitacion;
    }
}