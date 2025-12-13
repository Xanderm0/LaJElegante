package beans;

import dao.HabitacionDAO;
import dao.TipoHabitacionDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import models.Habitacion;
import models.TipoHabitacion;
import models.enums.EstadoHabitacion;
import utils.MessageUtil;

@ManagedBean
@ApplicationScoped
public class HabitacionBean implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador de versión para serialización (objetos a bytes)
    
    public Habitacion habitacion = new Habitacion();
    public List<Habitacion> listaHabitaciones = new ArrayList<>();
    public List<Habitacion> listaFiltrada = new ArrayList<>();
    public List<Habitacion> listaPapelera = new ArrayList<>();
    public List<TipoHabitacion> listaTipos = new ArrayList<>();
    
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final TipoHabitacionDAO tipoHabitacionDAO = new TipoHabitacionDAO();
    
    public void listar() {
        try {
            listaHabitaciones = habitacionDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar habitaciones: " + e.getMessage());
        }
    }
    
    public void buscar(int id) {
        habitacion = habitacionDAO.buscar(id);
        if (habitacion == null) {
            MessageUtil.notFoundError("Habitación", String.valueOf(id));
            habitacion = new Habitacion();
        }
    }
    
    public void guardar() {
        if (!validarHabitacion()) return;
        
        if (numeroHabitacionExiste(habitacion.getNumeroHabitacion(), 0)) {
            MessageUtil.duplicateError("número de habitación", 
                                      String.valueOf(habitacion.getNumeroHabitacion()));
            return;
        }
        
        try {
            habitacionDAO.crear(habitacion);
            MessageUtil.createSuccess("Habitación");
            listar();
            habitacion = new Habitacion(); // Limpiar formulario
        } catch (Exception e) {
            MessageUtil.createError("habitación");
        }
    }
    
    public void actualizar() {
        if (habitacion.getIdHabitacion() == 0) {
            MessageUtil.validationError("Seleccione una habitación primero");
            return;
        }
        
        if (!validarHabitacion()) return;
        
        if (numeroHabitacionExiste(habitacion.getNumeroHabitacion(), 
                                  habitacion.getIdHabitacion())) {
            MessageUtil.duplicateError("número de habitación", 
                                      String.valueOf(habitacion.getNumeroHabitacion()));
            return;
        }
        
        try {
            habitacionDAO.actualizar(habitacion);
            MessageUtil.updateSuccess("Habitación");
            listar();
        } catch (Exception e) {
            MessageUtil.updateError("habitación");
        }
    }
    
    public void eliminar(int id) {
        try {
            habitacionDAO.eliminar(id);
            MessageUtil.deleteSuccess("Habitación");
            listar();
        } catch (Exception e) {
            MessageUtil.deleteError("habitación");
        }
    }
    
    public void listarPapelera() {
        try {
            listaPapelera = habitacionDAO.listarEliminados();
        } catch (Exception e) {
            MessageUtil.error("Error al listar papelera: " + e.getMessage());
        }
    }
    
    public void restaurar(int id) {
        try {
            habitacionDAO.restaurar(id);
            MessageUtil.success("Habitación restaurada correctamente");
            listarPapelera();
            listar();
        } catch (Exception e) {
            MessageUtil.error("Error al restaurar habitación: " + e.getMessage());
        }
    }
    
    public void listarTiposHabitacion() {
        try {
            listaTipos = tipoHabitacionDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar tipos de habitación: " + e.getMessage());
        }
    }
    
    public void obtenerDisponibles(Date fechaInicio, Date fechaFin, int personas) {
        try {
            listaHabitaciones = habitacionDAO.obtenerDisponibles(fechaInicio, fechaFin, personas);
        } catch (Exception e) {
            MessageUtil.error("Error al buscar habitaciones disponibles: " + e.getMessage());
        }
    }
    
    public void obtenerPorPiso(int piso) {
        try {
            listaHabitaciones = habitacionDAO.obtenerPorPiso(piso);
        } catch (Exception e) {
            MessageUtil.error("Error al buscar habitaciones por piso: " + e.getMessage());
        }
    }
    
    public void cambiarEstado(int id, EstadoHabitacion nuevoEstado) {
        Habitacion hab = habitacionDAO.buscar(id);
        if (hab != null) {
            hab.setEstadoHabitacion(nuevoEstado);
            habitacionDAO.actualizar(hab);
            MessageUtil.success("Estado cambiado a " + nuevoEstado.name());
            listar();
        }
    }
    
    public void verificarDisponibilidad(int idHabitacion, Date fechaInicio, 
                                        Date fechaFin, int personas) {
        try {
            boolean disponible = habitacionDAO.estaDisponible(
                idHabitacion, fechaInicio, fechaFin, personas);
            
            if (disponible) {
                MessageUtil.success("La habitación está disponible para esas fechas");
            } else {
                MessageUtil.warn("La habitación NO está disponible para esas fechas");
            }
        } catch (Exception e) {
            MessageUtil.error("Error al verificar disponibilidad: " + e.getMessage());
        }
    }
    
    public void nuevo() {
        habitacion = new Habitacion();
    }
    
    private boolean validarHabitacion() {
        if (habitacion.getTipoHabitacion() == null || 
            habitacion.getTipoHabitacion().getIdTipoHabitacion() == 0) {
            MessageUtil.validationError("Seleccione un tipo de habitación");
            return false;
        }
        
        if (habitacion.getNumeroHabitacion() <= 0) {
            MessageUtil.validationError("El número de habitación debe ser mayor a 0");
            return false;
        }
        
        if (habitacion.getEstadoHabitacion() == null) {
            MessageUtil.validationError("Seleccione un estado para la habitación");
            return false;
        }
        
        // Validación: número de habitación entre 100 y 999
        if (habitacion.getNumeroHabitacion() < 100 || habitacion.getNumeroHabitacion() > 999) {
            MessageUtil.validationError("El número de habitación debe estar entre 100 y 999");
            return false;
        }
        
        return true;
    }
    
    private boolean numeroHabitacionExiste(int numero, int idExcluir) {
        // Buscar en la lista actual de habitaciones
        for (Habitacion hab : listaHabitaciones) {
            if (hab.getNumeroHabitacion() == numero && 
                hab.getIdHabitacion() != idExcluir) {
                return true;
            }
        }
        return false;
    }
    
    public EstadoHabitacion[] getEstadosDisponibles() {
        return EstadoHabitacion.values();
    }
    
    // Getters y Setters
    public Habitacion getHabitacion() { return habitacion; }
    public void setHabitacion(Habitacion habitacion) { this.habitacion = habitacion; }
    
    public List<Habitacion> getListaHabitaciones() { return listaHabitaciones; }
    public void setListaHabitaciones(List<Habitacion> listaHabitaciones) { 
        this.listaHabitaciones = listaHabitaciones; 
    }
    
    public List<Habitacion> getListaFiltrada() { return listaFiltrada; }
    public void setListaFiltrada(List<Habitacion> listaFiltrada) { 
        this.listaFiltrada = listaFiltrada; 
    }
    
    public List<Habitacion> getListaPapelera() { return listaPapelera; }
    public void setListaPapelera(List<Habitacion> listaPapelera) { 
        this.listaPapelera = listaPapelera; 
    }
    
    public List<TipoHabitacion> getListaTipos() { return listaTipos; }
    public void setListaTipos(List<TipoHabitacion> listaTipos) { 
        this.listaTipos = listaTipos; 
    }
}