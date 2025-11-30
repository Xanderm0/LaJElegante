package entities;

import entities.enums.Estado;
import entities.enums.EstadoReserva;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "reservas_restaurante")
public class ReservaRestaurante extends ClaseBase{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private int idReserva;
    
    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    
    @NotNull(message = "La mesa es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;
    
    @NotNull(message = "La fecha de reserva es obligatoria")
    @Future(message = "La fecha de reserva debe ser futura")
    @Column(name = "fecha_reserva")
    private LocalDate fechaReserva;
    
    @NotNull(message = "La hora de reserva es obligatoria")
    @Column(name = "hora_reserva")
    private LocalTime horaReserva;
    
    @Min(value = 1, message = "Mínimo 1 persona")
    @Max(value = 8, message = "Máximo 8 personas")
    @Column(name = "numero_personas")
    private int numeroPersonas;
    
    @NotNull(message = "El estado de reserva es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_reserva")
    private EstadoReserva estadoReserva = EstadoReserva.PENDIENTE;
    
    @NotNull(message = "El estado activo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "activo")
    private Estado estado = Estado.ACTIVO;

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalTime getHoraReserva() {
        return horaReserva;
    }

    public void setHoraReserva(LocalTime horaReserva) {
        this.horaReserva = horaReserva;
    }

    public int getNumeroPersonas() {
        return numeroPersonas;
    }

    public void setNumeroPersonas(int numeroPersonas) {
        this.numeroPersonas = numeroPersonas;
    }

    public EstadoReserva getEstadoReserva() {
        return estadoReserva;
    }

    public void setEstadoReserva(EstadoReserva estadoReserva) {
        this.estadoReserva = estadoReserva;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}