package beans;

import dao.ReservaHabitacionDAO;
import dao.ClienteDAO;
import dao.DetallesReservaHabitacionDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import models.ReservaHabitacion;
import models.Cliente;
import models.DetallesReservaHabitacion;

@ManagedBean
@ViewScoped
public class ReservaHabitacionBean implements Serializable {

    private ReservaHabitacion reserva = new ReservaHabitacion();
    private List<ReservaHabitacion> listaReservas = new ArrayList<>();
    private List<ReservaHabitacion> listaFiltrada;

    private List<Cliente> listaClientes = new ArrayList<>();
    private List<DetallesReservaHabitacion> listaDetalles = new ArrayList<>();

    private final ReservaHabitacionDAO reservaDAO = new ReservaHabitacionDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DetallesReservaHabitacionDAO detallesDAO = new DetallesReservaHabitacionDAO();

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    public ReservaHabitacionBean() {
        listar();
        cargarClientes();
        cargarDetalles();
    }

   
    public void listar() {
        listaReservas = reservaDAO.listar();
        reserva = new ReservaHabitacion();
    }

    public void guardar() {
        if (!validar(reserva)) return;

        reserva.prePersist();
        reservaDAO.guardar(reserva);
        listar();
    }

    public void actualizar() {
        if (!validar(reserva)) return;

        reserva.preUpdate();
        reservaDAO.actualizar(reserva);
        listar();
    }

    public void eliminar(int id) {
        ReservaHabitacion r = reservaDAO.buscar(id);
        r.softDelete();
        reservaDAO.actualizar(r);
        listar();
    }

    public void restaurar(int id) {
        ReservaHabitacion r = reservaDAO.buscar(id);
        r.restore();
        reservaDAO.actualizar(r);
        listar();
    }

    public void buscar(int id) {
        reserva = reservaDAO.buscar(id);
    }

  
    private boolean validar(ReservaHabitacion r) {
        var errores = validator.validate(r);
        if (!errores.isEmpty()) {
            for (ConstraintViolation<?> er : errores) {
                System.out.println("VALIDACIÓN ERROR: " + er.getMessage());
            }
            return false;
        }

        if (r.getCliente() == null) {
            System.out.println("VALIDACIÓN ERROR: Debe seleccionar un cliente");
            return false;
        }

        if (r.getDetalleReservaHabitacion() == null) {
            System.out.println("VALIDACIÓN ERROR: Debe seleccionar un detalle de reserva");
            return false;
        }

        return true;
    }

    
    public void cargarClientes() {
        listaClientes = clienteDAO.listar();
    }

    public void cargarDetalles() {
        listaDetalles = detallesDAO.listar();
    }


    
    public ReservaHabitacion getReserva() {
        return reserva;
    }

    public void setReserva(ReservaHabitacion reserva) {
        this.reserva = reserva;
    }

    public List<ReservaHabitacion> getListaReservas() {
        return listaReservas;
    }

    public void setListaReservas(List<ReservaHabitacion> listaReservas) {
        this.listaReservas = listaReservas;
    }

    public List<ReservaHabitacion> getListaFiltrada() {
        return listaFiltrada;
    }

    public void setListaFiltrada(List<ReservaHabitacion> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
    }

    public List<Cliente> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public List<DetallesReservaHabitacion> getListaDetalles() {
        return listaDetalles;
    }

    public void setListaDetalles(List<DetallesReservaHabitacion> listaDetalles) {
        this.listaDetalles = listaDetalles;
    }
}
