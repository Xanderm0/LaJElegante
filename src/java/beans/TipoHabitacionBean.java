package beans;

import dao.TipoHabitacionDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import models.TipoHabitacion;
import models.enums.NombreTipoHabitacion;
import utils.MessageUtil;

@ManagedBean
@ApplicationScoped
public class TipoHabitacionBean implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador de versión para serialización (objetos a bytes)
    
    public TipoHabitacion tipoHabitacion = new TipoHabitacion();
    public List<TipoHabitacion> listaTipoHabitaciones = new ArrayList<>();
    public List<TipoHabitacion> listaFiltrada = new ArrayList<>();
    public List<TipoHabitacion> listaPapelera = new ArrayList<>();
    
    // Para búsquedas
    private int capacidadMinimaBusqueda;
    
    private final TipoHabitacionDAO tipoHabitacionDAO = new TipoHabitacionDAO();
    
    public void listar() {
        try {
            listaTipoHabitaciones = tipoHabitacionDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar tipos de habitación: " + e.getMessage());
        }
    }
    
    public void buscar(int id) {
        tipoHabitacion = tipoHabitacionDAO.buscar(id);
        if (tipoHabitacion == null) {
            MessageUtil.notFoundError("Tipo de habitación", String.valueOf(id));
            tipoHabitacion = new TipoHabitacion();
        }
    }
    
    public void guardar() {
        if (!validarTipoHabitacion()) return;
        
        // Verificar si el nombre ya existe (case-insensitive)
        if (nombreTipoExiste(tipoHabitacion.getNombreTipo(), 0)) {
            MessageUtil.duplicateError("tipo de habitación", tipoHabitacion.getNombreTipo().name());
            return;
        }
        
        try {
            tipoHabitacionDAO.crear(tipoHabitacion);
            MessageUtil.createSuccess("Tipo de habitación");
            listar();
            tipoHabitacion = new TipoHabitacion(); // Limpiar formulario
        } catch (Exception e) {
            MessageUtil.createError("tipo de habitación");
        }
    }
    
    public void actualizar() {
        if (tipoHabitacion.getIdTipoHabitacion() == 0) {
            MessageUtil.validationError("Seleccione un tipo de habitación primero");
            return;
        }
        
        if (!validarTipoHabitacion()) return;
        
        // Verificar si el nombre ya existe excluyendo el actual
        if (nombreTipoExiste(tipoHabitacion.getNombreTipo(), tipoHabitacion.getIdTipoHabitacion())) {
            MessageUtil.duplicateError("tipo de habitación", tipoHabitacion.getNombreTipo().name());
            return;
        }
        
        try {
            tipoHabitacionDAO.actualizar(tipoHabitacion);
            MessageUtil.updateSuccess("Tipo de habitación");
            listar();
        } catch (Exception e) {
            MessageUtil.updateError("tipo de habitación");
        }
    }
    
    public void eliminar(int id) {
        // Verificar si el tipo está en uso antes de eliminar
        if (tipoHabitacionDAO.estaEnUso(id)) {
            MessageUtil.error("No se puede eliminar: Este tipo de habitación está asignado a habitaciones existentes");
            return;
        }
        
        try {
            tipoHabitacionDAO.eliminar(id);
            MessageUtil.deleteSuccess("Tipo de habitación");
            listar();
        } catch (Exception e) {
            MessageUtil.deleteError("tipo de habitación");
        }
    }
    
    public void listarPapelera() {
        try {
            listaPapelera = tipoHabitacionDAO.listarEliminados();
        } catch (Exception e) {
            MessageUtil.error("Error al listar papelera: " + e.getMessage());
        }
    }
    
    public void restaurar(int id) {
        try {
            tipoHabitacionDAO.restaurar(id);
            MessageUtil.success("Tipo de habitación restaurado correctamente");
            listarPapelera();
            listar();
        } catch (Exception e) {
            MessageUtil.error("Error al restaurar tipo de habitación: " + e.getMessage());
        }
    }
    
    public void obtenerPorCapacidadMinima() {
        if (capacidadMinimaBusqueda <= 0) {
            MessageUtil.validationError("La capacidad mínima debe ser mayor a 0");
            return;
        }
        
        try {
            listaTipoHabitaciones = tipoHabitacionDAO.obtenerPorCapacidadMinima(capacidadMinimaBusqueda);
            if (listaTipoHabitaciones.isEmpty()) {
                MessageUtil.info("No hay tipos de habitación con capacidad mínima de " + capacidadMinimaBusqueda + " personas");
            } else {
                MessageUtil.success("Encontrados " + listaTipoHabitaciones.size() + " tipos de habitación");
            }
        } catch (Exception e) {
            MessageUtil.error("Error al buscar por capacidad: " + e.getMessage());
        }
    }
    
    public void verificarUso(int id) {
        try {
            boolean enUso = tipoHabitacionDAO.estaEnUso(id);
            if (enUso) {
                MessageUtil.warn("Este tipo de habitación está siendo utilizado por habitaciones existentes");
            } else {
                MessageUtil.success("Este tipo de habitación no está en uso");
            }
        } catch (Exception e) {
            MessageUtil.error("Error al verificar uso: " + e.getMessage());
        }
    }
    
    public void nuevo() {
        tipoHabitacion = new TipoHabitacion();
        capacidadMinimaBusqueda = 0;
    }
    
    private boolean validarTipoHabitacion() {
        if (tipoHabitacion.getNombreTipo() == null) {
            MessageUtil.validationError("El nombre del tipo de habitación es obligatorio");
            return false;
        }
        
        if (tipoHabitacion.getDescripcion() == null || tipoHabitacion.getDescripcion().trim().isEmpty()) {
            MessageUtil.validationError("La descripción es obligatoria");
            return false;
        }
        
        if (tipoHabitacion.getDescripcion().length() < 10) {
            MessageUtil.validationError("La descripción debe tener al menos 10 caracteres");
            return false;
        }
        
        if (tipoHabitacion.getCapacidadMaxima() <= 0) {
            MessageUtil.validationError("La capacidad máxima debe ser mayor a 0");
            return false;
        }
        
        if (tipoHabitacion.getCapacidadMaxima() > 10) {
            MessageUtil.validationError("La capacidad máxima no puede exceder 10 personas");
            return false;
        }
        
        return true;
    }
    
    private boolean nombreTipoExiste(NombreTipoHabitacion nombre, int idExcluir) {
        // Buscar en la lista actual de tipos
        for (TipoHabitacion th : listaTipoHabitaciones) {
            if (th.getNombreTipo() == nombre && th.getIdTipoHabitacion() != idExcluir) {
                return true;
            }
        }
        return false;
    }
    
    // Métodos auxiliares para vistas
    public List<TipoHabitacion> getTiposParaReserva(int personas) {
        List<TipoHabitacion> adecuados = new ArrayList<>();
        for (TipoHabitacion th : listaTipoHabitaciones) {
            if (th.getCapacidadMaxima() >= personas) {
                adecuados.add(th);
            }
        }
        return adecuados;
    }
    
    public List<String> getDescripcionesCortas() {
        List<String> descripciones = new ArrayList<>();
        for (TipoHabitacion th : listaTipoHabitaciones) {
            String descCorta = th.getDescripcion().length() > 50 
                ? th.getDescripcion().substring(0, 50) + "..." 
                : th.getDescripcion();
            descripciones.add(descCorta);
        }
        return descripciones;
    }
    
    public NombreTipoHabitacion[] getNombresDisponibles() {
        return NombreTipoHabitacion.values();
    }
    
    // Getters y Setters
    public TipoHabitacion getTipoHabitacion() { return tipoHabitacion; }
    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) { this.tipoHabitacion = tipoHabitacion; }
    
    public List<TipoHabitacion> getListaTipoHabitaciones() { return listaTipoHabitaciones; }
    public void setListaTipoHabitaciones(List<TipoHabitacion> listaTipoHabitaciones) { 
        this.listaTipoHabitaciones = listaTipoHabitaciones; 
    }
    
    public List<TipoHabitacion> getListaFiltrada() { return listaFiltrada; }
    public void setListaFiltrada(List<TipoHabitacion> listaFiltrada) { 
        this.listaFiltrada = listaFiltrada; 
    }
    
    public List<TipoHabitacion> getListaPapelera() { return listaPapelera; }
    public void setListaPapelera(List<TipoHabitacion> listaPapelera) { 
        this.listaPapelera = listaPapelera; 
    }
    
    public int getCapacidadMinimaBusqueda() { return capacidadMinimaBusqueda; }
    public void setCapacidadMinimaBusqueda(int capacidadMinimaBusqueda) { 
        this.capacidadMinimaBusqueda = capacidadMinimaBusqueda; 
    }
}