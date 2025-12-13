package beans;

import dao.ReservaHabitacionDAO;
import dao.ClienteDAO;
import dao.DetallesReservaHabitacionDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import models.ReservaHabitacion;
import models.Cliente;
import models.DetallesReservaHabitacion;
import utils.MessageUtil;

@ManagedBean
@ApplicationScoped
public class ReservaHabitacionBean implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador de versión para serialización (objetos a bytes)
    
    public ReservaHabitacion reserva = new ReservaHabitacion();
    public List<ReservaHabitacion> listaReservas = new ArrayList<>();
    public List<ReservaHabitacion> listaFiltrada = new ArrayList<>();
    public List<ReservaHabitacion> listaPapelera = new ArrayList<>();
    
    public List<Cliente> listaClientes = new ArrayList<>();
    public List<DetallesReservaHabitacion> listaDetalles = new ArrayList<>();
    
    private final ReservaHabitacionDAO reservaDAO = new ReservaHabitacionDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DetallesReservaHabitacionDAO detallesDAO = new DetallesReservaHabitacionDAO();
    
    public void listar() {
        try {
            listaReservas = reservaDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar reservas de habitación: " + e.getMessage());
        }
    }
    
    public void buscar(int id) {
        reserva = reservaDAO.buscar(id);
        if (reserva == null) {
            MessageUtil.notFoundError("Reserva de habitación", String.valueOf(id));
            reserva = new ReservaHabitacion();
        }
    }
    
    public void guardar() {
        if (!validarReserva()) return;
        
        try {
            reservaDAO.crear(reserva);
            MessageUtil.createSuccess("Reserva de habitación");
            listar();
            reserva = new ReservaHabitacion(); // Limpiar formulario
        } catch (Exception e) {
            MessageUtil.createError("reserva de habitación");
        }
    }
    
    public void actualizar() {
        if (reserva.getIdReservaHabitacion() == 0) {
            MessageUtil.validationError("Seleccione una reserva primero");
            return;
        }
        
        if (!validarReserva()) return;
        
        try {
            reservaDAO.actualizar(reserva);
            MessageUtil.updateSuccess("Reserva de habitación");
            listar();
        } catch (Exception e) {
            MessageUtil.updateError("reserva de habitación");
        }
    }
    
    public void eliminar(int id) {
        try {
            reservaDAO.eliminar(id);
            MessageUtil.deleteSuccess("Reserva de habitación");
            listar();
        } catch (Exception e) {
            MessageUtil.deleteError("reserva de habitación");
        }
    }
    
    public void listarPapelera() {
        try {
            listaPapelera = reservaDAO.listarEliminados();
        } catch (Exception e) {
            MessageUtil.error("Error al listar papelera: " + e.getMessage());
        }
    }
    
    public void restaurar(int id) {
        try {
            reservaDAO.restaurar(id);
            MessageUtil.success("Reserva de habitación restaurada correctamente");
            listarPapelera();
            listar();
        } catch (Exception e) {
            MessageUtil.error("Error al restaurar reserva: " + e.getMessage());
        }
    }
    
    public void cargarClientes() {
        try {
            listaClientes = clienteDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al cargar clientes: " + e.getMessage());
        }
    }
    
    public void cargarDetalles() {
        try {
            listaDetalles = detallesDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al cargar detalles de reserva: " + e.getMessage());
        }
    }
    
    public int crearReservaInvitado(int idCliente, int idHabitacion, Date fechaInicio, 
                                   Date fechaFin, int personas, String observacion) {
        try {
            int idReserva = reservaDAO.crearReservaInvitado(idCliente, idHabitacion, 
                                                          fechaInicio, fechaFin, 
                                                          personas, observacion);
            if (idReserva > 0) {
                MessageUtil.createSuccess("Reserva para invitado creada");
                listar();
                return idReserva;
            }
        } catch (Exception e) {
            MessageUtil.createError("reserva para invitado");
        }
        return -1;
    }
    
    public void verificarConflicto(int idHabitacion, Date fechaInicio, Date fechaFin) {
        try {
            boolean conflicto = reservaDAO.existeConflictoReserva(idHabitacion, fechaInicio, fechaFin);
            if (conflicto) {
                MessageUtil.warn("Ya existe una reserva para esa habitación en las fechas seleccionadas");
            } else {
                MessageUtil.success("La habitación está disponible para esas fechas");
            }
        } catch (Exception e) {
            MessageUtil.error("Error al verificar conflicto: " + e.getMessage());
        }
    }
    
    public void nuevo() {
        reserva = new ReservaHabitacion();
    }
    
    public void listarReservasPorCliente(int idCliente) {
        try {
            // Necesitarías un método en el DAO para esto
            // listaReservas = reservaDAO.listarPorCliente(idCliente);
            
            // Por ahora filtramos de la lista existente
            List<ReservaHabitacion> filtradas = new ArrayList<>();
            for (ReservaHabitacion r : listaReservas) {
                if (r.getCliente() != null && r.getCliente().getIdCliente() == idCliente) {
                    filtradas.add(r);
                }
            }
            listaReservas = filtradas;
        } catch (Exception e) {
            MessageUtil.error("Error al filtrar por cliente: " + e.getMessage());
        }
    }
    
    private boolean validarReserva() {
        if (reserva.getCliente() == null || reserva.getCliente().getIdCliente() == 0) {
            MessageUtil.validationError("Seleccione un cliente");
            return false;
        }
        
        if (reserva.getDetalleReservaHabitacion() == null || 
            reserva.getDetalleReservaHabitacion().getIdDetalleReservaHab() == 0) {
            MessageUtil.validationError("Seleccione un detalle de reserva");
            return false;
        }
        
        return true;
    }
    
    // Getters y Setters
    public ReservaHabitacion getReserva() { return reserva; }
    public void setReserva(ReservaHabitacion reserva) { this.reserva = reserva; }
    
    public List<ReservaHabitacion> getListaReservas() { return listaReservas; }
    public void setListaReservas(List<ReservaHabitacion> listaReservas) { 
        this.listaReservas = listaReservas; 
    }
    
    public List<ReservaHabitacion> getListaFiltrada() { return listaFiltrada; }
    public void setListaFiltrada(List<ReservaHabitacion> listaFiltrada) { 
        this.listaFiltrada = listaFiltrada; 
    }
    
    public List<ReservaHabitacion> getListaPapelera() { return listaPapelera; }
    public void setListaPapelera(List<ReservaHabitacion> listaPapelera) { 
        this.listaPapelera = listaPapelera; 
    }
    
    public List<Cliente> getListaClientes() { return listaClientes; }
    public void setListaClientes(List<Cliente> listaClientes) { 
        this.listaClientes = listaClientes; 
    }
    
    public List<DetallesReservaHabitacion> getListaDetalles() { return listaDetalles; }
    public void setListaDetalles(List<DetallesReservaHabitacion> listaDetalles) { 
        this.listaDetalles = listaDetalles; 
    }
}