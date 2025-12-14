package beans;

import dao.ReservaRestauranteDAO;
import dao.ClienteDAO;
import dao.MesaDAO;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import models.ReservaRestaurante;
import models.Cliente;
import models.Mesa;
import models.enums.Estado;
import models.enums.EstadoReserva;
import utils.MessageUtil;

@ManagedBean
@ApplicationScoped
public class ReservaRestauranteBean implements Serializable {
    private static final long serialVersionUID = 1L; // UID para Serializable
    
    public ReservaRestaurante reserva = new ReservaRestaurante();
    public List<ReservaRestaurante> listaReservas = new ArrayList<>();
    public List<ReservaRestaurante> listaFiltrada = new ArrayList<>();
    public List<ReservaRestaurante> listaPapelera = new ArrayList<>();
    
    public List<Cliente> listaClientes = new ArrayList<>();
    public List<Mesa> listaMesas = new ArrayList<>();
    
    private final ReservaRestauranteDAO reservaDAO = new ReservaRestauranteDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MesaDAO mesaDAO = new MesaDAO();
    
    // Para filtros
    private EstadoReserva estadoFiltro;
    private LocalDate fechaFiltro;
    private boolean mostrarTodas = false;
    
    public void listar() {
        try {
            listaReservas = reservaDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar reservas de restaurante: " + e.getMessage());
        }
    }
    
    public void listarConFiltros() {
        try {
            listaReservas = reservaDAO.listarConFiltros(estadoFiltro, fechaFiltro, mostrarTodas);
        } catch (Exception e) {
            MessageUtil.error("Error al listar con filtros: " + e.getMessage());
        }
    }
    
    public void buscar(int id) {
        reserva = reservaDAO.buscar(id);
        if (reserva == null) {
            MessageUtil.notFoundError("Reserva de restaurante", String.valueOf(id));
            reserva = new ReservaRestaurante();
        }
    }
    
    public void guardar() {
        if (!validarReserva()) return;
        
        try {
            int idReserva = reservaDAO.crearReservaConVerificacion(reserva);
            if (idReserva > 0) {
                MessageUtil.createSuccess("Reserva de restaurante");
                listar();
                reserva = new ReservaRestaurante(); // Limpiar formulario
            }
        } catch (Exception e) {
            MessageUtil.createError("reserva de restaurante");
        }
    }
    
    public void actualizar() {
        if (reserva.getIdReserva() == 0) {
            MessageUtil.validationError("Seleccione una reserva primero");
            return;
        }
        
        if (!validarReserva()) return;
        
        try {
            reservaDAO.actualizar(reserva);
            MessageUtil.updateSuccess("Reserva de restaurante");
            listar();
        } catch (Exception e) {
            MessageUtil.updateError("reserva de restaurante");
        }
    }
    
    public void eliminar(int id) {
        try {
            reservaDAO.eliminar(id);
            MessageUtil.deleteSuccess("Reserva de restaurante");
            listar();
        } catch (Exception e) {
            MessageUtil.deleteError("reserva de restaurante");
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
            MessageUtil.success("Reserva de restaurante restaurada correctamente");
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
    
    public void cargarMesas() {
        try {
            listaMesas = mesaDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al cargar mesas: " + e.getMessage());
        }
    }
    
    public void cambiarEstadoReserva(int idReserva, EstadoReserva nuevoEstado) {
        try {
            if (reservaDAO.cambiarEstadoReserva(idReserva, nuevoEstado)) {
                MessageUtil.success("Estado de reserva cambiado a " + nuevoEstado.getValor());
                listar();
            }
        } catch (Exception e) {
            MessageUtil.error("Error al cambiar estado de reserva: " + e.getMessage());
        }
    }
    
    public void cambiarEstado(int idReserva, Estado nuevoEstado) {
        try {
            if (reservaDAO.cambiarEstado(idReserva, nuevoEstado)) {
                MessageUtil.success("Reserva " + nuevoEstado.getValor());
                listar();
            }
        } catch (Exception e) {
            MessageUtil.error("Error al cambiar estado: " + e.getMessage());
        }
    }
    
    public void verificarDisponibilidad(int idMesa, LocalDate fecha, LocalTime hora) {
        try {
            boolean disponible = reservaDAO.mesaDisponible(idMesa, fecha, hora);
            if (disponible) {
                MessageUtil.success("La mesa está disponible para esa fecha y hora");
            } else {
                MessageUtil.warn("La mesa NO está disponible para esa fecha y hora");
            }
        } catch (Exception e) {
            MessageUtil.error("Error al verificar disponibilidad: " + e.getMessage());
        }
    }
    
    public void filtrarPorEstadoReserva(EstadoReserva estado) {
        try {
            listaReservas = reservaDAO.filtrarPorEstadoReserva(estado);
        } catch (Exception e) {
            MessageUtil.error("Error al filtrar por estado: " + e.getMessage());
        }
    }
    
    public void filtrarPorFecha(LocalDate fecha) {
        try {
            listaReservas = reservaDAO.filtrarPorFecha(fecha);
        } catch (Exception e) {
            MessageUtil.error("Error al filtrar por fecha: " + e.getMessage());
        }
    }
    
    public void obtenerReservasPorCliente(int idCliente) {
        try {
            listaReservas = reservaDAO.obtenerReservasPorCliente(idCliente);
        } catch (Exception e) {
            MessageUtil.error("Error al obtener reservas del cliente: " + e.getMessage());
        }
    }
    
    public void obtenerReservasHoy() {
        try {
            listaReservas = reservaDAO.obtenerReservasHoy();
        } catch (Exception e) {
            MessageUtil.error("Error al obtener reservas de hoy: " + e.getMessage());
        }
    }
    
    public void nuevo() {
        reserva = new ReservaRestaurante();
    }
    
    public void limpiarFiltros() {
        estadoFiltro = null;
        fechaFiltro = null;
        mostrarTodas = false;
        listar();
    }
    
    private boolean validarReserva() {
        if (reserva.getCliente() == null || reserva.getCliente().getIdCliente() == 0) {
            MessageUtil.validationError("Seleccione un cliente");
            return false;
        }
        
        if (reserva.getMesa() == null || reserva.getMesa().getIdMesa() == 0) {
            MessageUtil.validationError("Seleccione una mesa");
            return false;
        }
        
        if (reserva.getFechaReserva() == null) {
            MessageUtil.validationError("Seleccione una fecha");
            return false;
        }
        
        if (reserva.getHoraReserva() == null) {
            MessageUtil.validationError("Seleccione una hora");
            return false;
        }
        
        if (reserva.getNumeroPersonas() <= 0) {
            MessageUtil.validationError("El número de personas debe ser mayor a 0");
            return false;
        }
        
        if (reserva.getEstadoReserva() == null) {
            MessageUtil.validationError("Seleccione un estado de reserva");
            return false;
        }
        
        if (reserva.getEstado() == null) {
            MessageUtil.validationError("Seleccione un estado general");
            return false;
        }
        
        // Validar capacidad de la mesa
        if (reserva.getMesa().getCapacidad() < reserva.getNumeroPersonas()) {
            MessageUtil.validationError("La mesa seleccionada no tiene capacidad para " + 
                                       reserva.getNumeroPersonas() + " personas. Capacidad máxima: " + 
                                       reserva.getMesa().getCapacidad());
            return false;
        }
        
        // Validar fecha no pasada
        if (reserva.getFechaReserva().isBefore(LocalDate.now())) {
            MessageUtil.validationError("No se pueden hacer reservas para fechas pasadas");
            return false;
        }
        
        // Validar hora razonable (entre 6am y 10pm)
        LocalTime hora = reserva.getHoraReserva();
        if (hora.isBefore(LocalTime.of(6, 0)) || hora.isAfter(LocalTime.of(22, 0))) {
            MessageUtil.validationError("El restaurante está abierto de 6:00 AM a 10:00 PM");
            return false;
        }
        
        return true;
    }
    
    // Métodos auxiliares para vistas
    public EstadoReserva[] getEstadosReservaDisponibles() {
        return EstadoReserva.values();
    }
    
    public Estado[] getEstadosDisponibles() {
        return Estado.values();
    }
    
    public List<LocalTime> getHorasDisponibles() {
        List<LocalTime> horas = new ArrayList<>();
        for (int h = 6; h <= 22; h++) {
            horas.add(LocalTime.of(h, 0));
            horas.add(LocalTime.of(h, 30));
        }
        return horas;
    }
    
    // Getters y Setters
    public ReservaRestaurante getReserva() { return reserva; }
    public void setReserva(ReservaRestaurante reserva) { this.reserva = reserva; }
    
    public List<ReservaRestaurante> getListaReservas() { return listaReservas; }
    public void setListaReservas(List<ReservaRestaurante> listaReservas) { 
        this.listaReservas = listaReservas; 
    }
    
    public List<ReservaRestaurante> getListaFiltrada() { return listaFiltrada; }
    public void setListaFiltrada(List<ReservaRestaurante> listaFiltrada) { 
        this.listaFiltrada = listaFiltrada; 
    }
    
    public List<ReservaRestaurante> getListaPapelera() { return listaPapelera; }
    public void setListaPapelera(List<ReservaRestaurante> listaPapelera) { 
        this.listaPapelera = listaPapelera; 
    }
    
    public List<Cliente> getListaClientes() { return listaClientes; }
    public void setListaClientes(List<Cliente> listaClientes) { 
        this.listaClientes = listaClientes; 
    }
    
    public List<Mesa> getListaMesas() { return listaMesas; }
    public void setListaMesas(List<Mesa> listaMesas) { 
        this.listaMesas = listaMesas; 
    }
    
    // Getters y Setters para filtros
    public EstadoReserva getEstadoFiltro() { return estadoFiltro; }
    public void setEstadoFiltro(EstadoReserva estadoFiltro) { this.estadoFiltro = estadoFiltro; }
    
    public LocalDate getFechaFiltro() { return fechaFiltro; }
    public void setFechaFiltro(LocalDate fechaFiltro) { this.fechaFiltro = fechaFiltro; }
    
    public boolean isMostrarTodas() { return mostrarTodas; }
    public void setMostrarTodas(boolean mostrarTodas) { this.mostrarTodas = mostrarTodas; }
}