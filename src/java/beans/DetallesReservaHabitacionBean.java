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

@ManagedBean
@ApplicationScoped
public class DetallesReservaHabitacionBean implements Serializable {
    
    public DetallesReservaHabitacion detalle = new DetallesReservaHabitacion();
    public List<DetallesReservaHabitacion> lstDetalles = new ArrayList<>();
    public List<DetallesReservaHabitacion> lstDetallesFiltrado;
    public List<Habitacion> lstHabitaciones = new ArrayList<>();
    
    private final DetallesReservaHabitacionDAO detalleDAO = new DetallesReservaHabitacionDAO();
    
    public DetallesReservaHabitacionBean() {
    }
    
    public void listarDetalles() {
        detalle = new DetallesReservaHabitacion();
        lstDetalles = detalleDAO.listar();
    }
    
    public void buscar(int id) {
        detalle = detalleDAO.buscar(id);
    }
    
    public void actualizar() {
        
        if (detalle.getIdDetalleReservaHab() == 0) {
            System.out.println("Error: Seleccione un detalle primero");
            return;
        }
        
        calcularPrecioTotal();
        detalleDAO.actualizar(detalle);
        listarDetalles();
    }
    
    public void eliminar(int id) {
        detalleDAO.eliminar(id);
        listarDetalles();
    }
    
    public void cargarHabitaciones() {
        HabitacionDAO habitacionDAO = new HabitacionDAO();
        lstHabitaciones = habitacionDAO.listar();
    }
    
    public void guardar() {

        if (detalle.getHabitacion() == null || detalle.getHabitacion().getIdHabitacion() == 0) {
            System.out.println("Error: Seleccione una habitación");
            return;
        }
        
        calcularPrecioTotal();
        detalleDAO.crear(detalle);
        listarDetalles();
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
    
    // GETTERS Y SETTERS (igual que ProductoBean)
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