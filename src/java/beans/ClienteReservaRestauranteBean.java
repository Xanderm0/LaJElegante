package beans;

import dao.MesaDAO;
import dao.ReservaRestauranteDAO;
import models.Cliente;
import models.Mesa;
import models.ReservaRestaurante;
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
public class ClienteReservaRestauranteBean implements Serializable {

    private Cliente cliente;

    private Date fechaReserva;
    private String turno;
    private int cantidadPersonas = 1;

    private int idMesaSeleccionada;
    private List<Mesa> mesasDisponibles;

    private String observacion;

    private final MesaDAO mesaDAO = new MesaDAO();
    private final ReservaRestauranteDAO reservaDAO = new ReservaRestauranteDAO();

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
            MessageUtil.error("Debe iniciar sesión para realizar una reserva");
        }
    }

    public void buscarMesasDisponibles() {

        if (fechaReserva == null || turno == null || turno.isEmpty()) {
            MessageUtil.requiredFieldsWarning();
            return;
        }

        mesasDisponibles = mesaDAO.obtenerMesasDisponibles(
                fechaReserva,
                turno,
                cantidadPersonas
        );

        if (mesasDisponibles == null || mesasDisponibles.isEmpty()) {
            MessageUtil.warn("No hay mesas disponibles para ese turno");
        }
    }

    public void reservar() {

    if (cliente == null) {
        MessageUtil.error("Sesión inválida");
        return;
    }

    if (idMesaSeleccionada <= 0 || fechaReserva == null) {
        MessageUtil.requiredFieldsWarning();
        return;
    }

    ReservaRestaurante reserva = new ReservaRestaurante();

    reserva.setCliente(cliente);

    Mesa mesa = new Mesa();
    mesa.setIdMesa(idMesaSeleccionada);
    reserva.setMesa(mesa);

    // Conversión Date -> LocalDate (CORRECTO)
    reserva.setFechaReserva(
        fechaReserva.toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
    );

    // ⚠️ NO SE SETEAN:
    // turno
    // cantidadPersonas
    // observacion
    // porque NO existen en el modelo ReservaRestaurante

    int idReserva = reservaDAO.crearReservaConVerificacion(reserva);

    if (idReserva > 0) {
        limpiarFormulario();
        MessageUtil.createSuccess("reserva de restaurante");
    }
}


    private void limpiarFormulario() {
        fechaReserva = null;
        turno = null;
        cantidadPersonas = 1;
        idMesaSeleccionada = 0;
        observacion = null;
        mesasDisponibles = null;
    }

    public Date getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(Date fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public void setCantidadPersonas(int cantidadPersonas) {
        this.cantidadPersonas = cantidadPersonas;
    }

    public int getIdMesaSeleccionada() {
        return idMesaSeleccionada;
    }

    public void setIdMesaSeleccionada(int idMesaSeleccionada) {
        this.idMesaSeleccionada = idMesaSeleccionada;
    }

    public List<Mesa> getMesasDisponibles() {
        return mesasDisponibles;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
