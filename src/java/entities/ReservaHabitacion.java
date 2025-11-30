package entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "reserva_habitacion")
public class ReservaHabitacion extends ClaseBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva_habitacion")
    private int idReservaHabitacion;
    
    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    
    @NotNull(message = "El detalle de reserva es obligatorio")
    @OneToOne
    @JoinColumn(name = "id_detalle_reserva_hab")
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