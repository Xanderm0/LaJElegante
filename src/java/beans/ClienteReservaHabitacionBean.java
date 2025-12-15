package beans;

import dao.HabitacionDAO;
import dao.ReservaHabitacionDAO;
import models.Cliente;
import models.Habitacion;
import utils.MessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
public class ClienteReservaHabitacionBean implements Serializable {

    private Cliente cliente;

    private int idHabitacionSeleccionada;
    private Date fechaInicio;
    private Date fechaFin;
    private int cantidadPersonas = 1;
    private String observacion;

    private List<Habitacion> habitacionesDisponibles;

    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final ReservaHabitacionDAO reservaDAO = new ReservaHabitacionDAO();

    @PostConstruct
    public void init() {
        obtenerClienteSesion();
    }

    private void obtenerClienteSesion() {
        Map<String, Object> session =
                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap();

        Object usuario = session.get("usuario");

        if (usuario instanceof Cliente) {
            cliente = (Cliente) usuario;
        } else {
            MessageUtil.error("Debe iniciar sesión como cliente");
        }
    }

    // =========================
    // BUSCAR HABITACIONES
    // =========================
    public void buscarHabitaciones() {
        if (fechaInicio == null || fechaFin == null) {
            MessageUtil.requiredFieldsWarning();
            return;
        }

        if (!fechaFin.after(fechaInicio)) {
            MessageUtil.error("La fecha de salida debe ser posterior a la de entrada");
            return;
        }

        habitacionesDisponibles =
                habitacionDAO.obtenerDisponibles(fechaInicio, fechaFin, cantidadPersonas);

        if (habitacionesDisponibles.isEmpty()) {
            MessageUtil.warn("No hay habitaciones disponibles para esas fechas");
        }
    }

    // =========================
    // CREAR RESERVA
    // =========================
    public void reservar() {

        if (cliente == null) {
            MessageUtil.error("Sesión inválida");
            return;
        }

        if (idHabitacionSeleccionada <= 0 ||
            fechaInicio == null ||
            fechaFin == null) {

            MessageUtil.requiredFieldsWarning();
            return;
        }

        // Verificar conflictos
        boolean conflicto = reservaDAO.existeConflictoReserva(
                idHabitacionSeleccionada,
                fechaInicio,
                fechaFin
        );

        if (conflicto) {
            MessageUtil.error("La habitación ya está reservada en ese rango de fechas");
            return;
        }

        int idReserva = reservaDAO.crearReservaInvitado(
                cliente.getIdCliente(),
                idHabitacionSeleccionada,
                fechaInicio,
                fechaFin,
                cantidadPersonas,
                observacion
        );

        if (idReserva > 0) {
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        idHabitacionSeleccionada = 0;
        fechaInicio = null;
        fechaFin = null;
        cantidadPersonas = 1;
        observacion = null;
        habitacionesDisponibles = null;
    }

    // =========================
    // GETTERS & SETTERS
    // =========================
    public Cliente getCliente() {
        return cliente;
    }

    public int getIdHabitacionSeleccionada() {
        return idHabitacionSeleccionada;
    }

    public void setIdHabitacionSeleccionada(int idHabitacionSeleccionada) {
        this.idHabitacionSeleccionada = idHabitacionSeleccionada;
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

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public List<Habitacion> getHabitacionesDisponibles() {
        return habitacionesDisponibles;
    }
}
