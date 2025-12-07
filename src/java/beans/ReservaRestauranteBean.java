package beans;

import dao.ReservaRestauranteDAO;
import dao.ClienteDAO;
import dao.MesaDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import models.ReservaRestaurante;
import models.Cliente;
import models.Mesa;

@ManagedBean
@ViewScoped
public class ReservaRestauranteBean implements Serializable {

    private ReservaRestaurante reserva = new ReservaRestaurante();
    private List<ReservaRestaurante> listaReservas = new ArrayList<>();
    private List<ReservaRestaurante> listaFiltrada;

    private List<Cliente> listaClientes = new ArrayList<>();
    private List<Mesa> listaMesas = new ArrayList<>();

    private final ReservaRestauranteDAO reservaDAO = new ReservaRestauranteDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MesaDAO mesaDAO = new MesaDAO();

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    public ReservaRestauranteBean() {
        listar();
        cargarClientes();
        cargarMesas();
    }

    

    public void listar() {
        listaReservas = reservaDAO.listar();
        reserva = new ReservaRestaurante();
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
        ReservaRestaurante r = reservaDAO.buscar(id);
        r.softDelete();
        reservaDAO.actualizar(r);
        listar();
    }

    public void restaurar(int id) {
        ReservaRestaurante r = reservaDAO.buscar(id);
        r.restore();
        reservaDAO.actualizar(r);
        listar();
    }

    public void buscar(int id) {
        reserva = reservaDAO.buscar(id);
    }


   

    private boolean validar(ReservaRestaurante r) {
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

        if (r.getMesa() == null) {
            System.out.println("VALIDACIÓN ERROR: Debe seleccionar una mesa");
            return false;
        }

        if (r.getFechaReserva() == null) {
            System.out.println("VALIDACIÓN ERROR: Debe seleccionar una fecha");
            return false;
        }

        if (r.getHoraReserva() == null) {
            System.out.println("VALIDACIÓN ERROR: Debe seleccionar una hora");
            return false;
        }

        if (r.getNumeroPersonas() <= 0) {
            System.out.println("VALIDACIÓN ERROR: Debe ingresar personas válidas");
            return false;
        }

        return true;
    }


    
    public void cargarClientes() {
        listaClientes = clienteDAO.listar();
    }

    public void cargarMesas() {
        listaMesas = mesaDAO.listar();
    }


    
    public ReservaRestaurante getReserva() {
        return reserva;
    }

    public void setReserva(ReservaRestaurante reserva) {
        this.reserva = reserva;
    }

    public List<ReservaRestaurante> getListaReservas() {
        return listaReservas;
    }

    public void setListaReservas(List<ReservaRestaurante> listaReservas) {
        this.listaReservas = listaReservas;
    }

    public List<ReservaRestaurante> getListaFiltrada() {
        return listaFiltrada;
    }

    public void setListaFiltrada(List<ReservaRestaurante> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
    }

    public List<Cliente> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public List<Mesa> getListaMesas() {
        return listaMesas;
    }

    public void setListaMesas(List<Mesa> listaMesas) {
        this.listaMesas = listaMesas;
    }
}
