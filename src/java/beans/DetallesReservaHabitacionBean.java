package beans;

import dao.DetallesReservaHabitacionDAO;
import dao.HabitacionDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import models.DetallesReservaHabitacion;
import models.Habitacion;
import utils.MessageUtil;

@ManagedBean
@ApplicationScoped
public class DetallesReservaHabitacionBean implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador de versión para serialización (objetos a bytes)
    
    public DetallesReservaHabitacion detalle = new DetallesReservaHabitacion();
    public List<DetallesReservaHabitacion> lstDetalles = new ArrayList<>();
    public List<DetallesReservaHabitacion> lstDetallesFiltrado = new ArrayList<>();
    public List<Habitacion> lstHabitaciones = new ArrayList<>();
    
    private final DetallesReservaHabitacionDAO detalleDAO = new DetallesReservaHabitacionDAO();
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    
    public void listarDetalles() {
        try {
            lstDetalles = detalleDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar detalles de reserva: " + e.getMessage());
        }
    }
    
    public void buscar(int id) {
        detalle = detalleDAO.buscar(id);
        if (detalle == null) {
            MessageUtil.notFoundError("Detalle de reserva", String.valueOf(id));
            detalle = new DetallesReservaHabitacion();
        }
    }
    
    public void actualizar() {
        if (detalle.getIdDetalleReservaHab() == 0) {
            MessageUtil.validationError("Seleccione un detalle primero");
            return;
        }
        
        if (!validarDetalle()) {
            return;
        }
        
        calcularPrecioTotal();
        
        try {
            detalleDAO.actualizar(detalle);
            MessageUtil.updateSuccess("Detalle de reserva");
            listarDetalles();
        } catch (Exception e) {
            MessageUtil.updateError("detalle de reserva");
        }
    }
    
    public void eliminar(int id) {
        try {
            detalleDAO.eliminar(id);
            MessageUtil.deleteSuccess("Detalle de reserva");
            listarDetalles();
        } catch (Exception e) {
            MessageUtil.deleteError("detalle de reserva");
        }
    }
    
    public void cargarHabitaciones() {
        try {
            lstHabitaciones = habitacionDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al cargar habitaciones: " + e.getMessage());
        }
    }
    
    public void guardar() {
        if (!validarDetalle()) {
            return;
        }
        
        calcularPrecioTotal();
        
        try {
            detalleDAO.crear(detalle);
            MessageUtil.createSuccess("Detalle de reserva");
            listarDetalles();
            detalle = new DetallesReservaHabitacion(); // Limpiar formulario
        } catch (Exception e) {
            MessageUtil.createError("detalle de reserva");
        }
    }
    
    public void listarPapelera() {
        try {
            lstDetallesFiltrado = detalleDAO.listarEliminados();
        } catch (Exception e) {
            MessageUtil.error("Error al listar papelera: " + e.getMessage());
        }
    }
    
    public void restaurar(int id) {
        try {
            detalleDAO.restaurar(id);
            MessageUtil.success("Detalle de reserva restaurado correctamente");
            listarPapelera();
            listarDetalles();
        } catch (Exception e) {
            MessageUtil.error("Error al restaurar detalle: " + e.getMessage());
        }
    }
    
    public void nuevo() {
        detalle = new DetallesReservaHabitacion();
    }
    
    public void listarPorReserva(int idReserva) {
        try {
            lstDetalles = detalleDAO.listarPorReserva(idReserva);
        } catch (Exception e) {
            MessageUtil.error("Error al listar reservas por ID: " + e.getMessage());
        }
    }
    
    private void calcularPrecioTotal() {
        if (detalle.getPrecioNoche() > 0 && detalle.getCantidadNoches() > 0) {
            double total = (detalle.getPrecioNoche() * detalle.getCantidadNoches());
            total -= detalle.getDescuentoAplicado();
            total += detalle.getRecargoAplicado();
            if (total < 0) total = 0;
            detalle.setPrecioReserva(total);
        }
    }
    
    private boolean validarDetalle() {
        if (detalle.getHabitacion() == null || detalle.getHabitacion().getIdHabitacion() == 0) {
            MessageUtil.validationError("Seleccione una habitación");
            return false;
        }
        
        if (detalle.getFechaInicio() == null) {
            MessageUtil.validationError("La fecha de inicio es requerida");
            return false;
        }
        
        if (detalle.getFechaFin() == null) {
            MessageUtil.validationError("La fecha de fin es requerida");
            return false;
        }
        
        if (detalle.getCantidadNoches() <= 0) {
            MessageUtil.validationError("La cantidad de noches debe ser mayor a 0");
            return false;
        }
        
        if (detalle.getCantidadPersonas() <= 0) {
            MessageUtil.validationError("La cantidad de personas debe ser mayor a 0");
            return false;
        }
        
        if (detalle.getPrecioNoche() <= 0) {
            MessageUtil.validationError("El precio por noche debe ser mayor a 0");
            return false;
        }
        
        // Validar que fecha fin sea mayor a fecha inicio
        if (detalle.getFechaFin().before(detalle.getFechaInicio())) {
            MessageUtil.validationError("La fecha de fin debe ser posterior a la fecha de inicio");
            return false;
        }
        
        return true;
    }
    
    // GETTERS Y SETTERS
    public DetallesReservaHabitacion getDetalle() { return detalle; }
    public void setDetalle(DetallesReservaHabitacion detalle) { this.detalle = detalle; }
    
    public List<DetallesReservaHabitacion> getLstDetalles() { return lstDetalles; }
    public void setLstDetalles(List<DetallesReservaHabitacion> lstDetalles) { 
        this.lstDetalles = lstDetalles; 
    }
    
    public List<Habitacion> getLstHabitaciones() { return lstHabitaciones; }
    public void setLstHabitaciones(List<Habitacion> lstHabitaciones) { 
        this.lstHabitaciones = lstHabitaciones; 
    }
    
    public List<DetallesReservaHabitacion> getLstDetallesFiltrado() { 
        return lstDetallesFiltrado; 
    }
    public void setLstDetallesFiltrado(List<DetallesReservaHabitacion> lstDetallesFiltrado) { 
        this.lstDetallesFiltrado = lstDetallesFiltrado; 
    }
}