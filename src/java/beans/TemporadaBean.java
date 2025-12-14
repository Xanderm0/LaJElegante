package beans;

import dao.TemporadaDAO;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import models.Temporada;
import models.enums.NombreTemporada;
import utils.MessageUtil;

@ManagedBean
@ApplicationScoped
public class TemporadaBean implements Serializable {
    private static final long serialVersionUID = 1L; // UID para Serializable
    
    public Temporada temporada = new Temporada();
    public List<Temporada> listaTemporadas = new ArrayList<>();
    public List<Temporada> listaFiltrada = new ArrayList<>();
    public List<Temporada> listaPapelera = new ArrayList<>();
    
    // Para búsquedas
    private Date fechaBusqueda;
    private Temporada temporadaActual;
    private Temporada temporadaPorFecha;
    
    private final TemporadaDAO temporadaDAO = new TemporadaDAO();
    
    public void listar() {
        try {
            listaTemporadas = temporadaDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar temporadas: " + e.getMessage());
        }
    }
    
    public void buscar(int id) {
        temporada = temporadaDAO.buscar(id);
        if (temporada == null) {
            MessageUtil.notFoundError("Temporada", String.valueOf(id));
            temporada = new Temporada();
        }
    }
    
    public void guardar() {
        if (!validarTemporada()) return;
        
        // Verificar solapamiento de fechas
        if (existeSolapamiento(0)) {
            MessageUtil.warn("Ya existe una temporada que se solapa con las fechas seleccionadas");
            return;
        }
        
        try {
            temporadaDAO.crear(temporada);
            MessageUtil.createSuccess("Temporada");
            listar();
            temporada = new Temporada(); // Limpiar formulario
        } catch (Exception e) {
            MessageUtil.createError("temporada");
        }
    }
    
    public void actualizar() {
        if (temporada.getIdTemporada() == 0) {
            MessageUtil.validationError("Seleccione una temporada primero");
            return;
        }
        
        if (!validarTemporada()) return;
        
        // Verificar solapamiento excluyendo la actual
        if (existeSolapamiento(temporada.getIdTemporada())) {
            MessageUtil.warn("Ya existe otra temporada que se solapa con las fechas seleccionadas");
            return;
        }
        
        try {
            temporadaDAO.actualizar(temporada);
            MessageUtil.updateSuccess("Temporada");
            listar();
        } catch (Exception e) {
            MessageUtil.updateError("temporada");
        }
    }
    
    public void eliminar(int id) {
        try {
            temporadaDAO.eliminar(id);
            MessageUtil.deleteSuccess("Temporada");
            listar();
        } catch (Exception e) {
            MessageUtil.deleteError("temporada");
        }
    }
    
    public void listarPapelera() {
        try {
            listaPapelera = temporadaDAO.listarEliminados();
        } catch (Exception e) {
            MessageUtil.error("Error al listar papelera: " + e.getMessage());
        }
    }
    
    public void restaurar(int id) {
        try {
            temporadaDAO.restaurar(id);
            MessageUtil.success("Temporada restaurada correctamente");
            listarPapelera();
            listar();
        } catch (Exception e) {
            MessageUtil.error("Error al restaurar temporada: " + e.getMessage());
        }
    }
    
    public void obtenerTemporadaActual() {
        try {
            temporadaActual = temporadaDAO.getTemporadaActual();
            if (temporadaActual != null) {
                MessageUtil.success("Temporada actual: " + temporadaActual.getNombre());
            } else {
                MessageUtil.info("No hay temporada especial en este momento (temporada base)");
            }
        } catch (Exception e) {
            MessageUtil.error("Error al obtener temporada actual: " + e.getMessage());
        }
    }
    
    public void obtenerTemporadaPorFecha() {
        if (fechaBusqueda == null) {
            MessageUtil.validationError("Seleccione una fecha primero");
            return;
        }
        
        try {
            temporadaPorFecha = temporadaDAO.obtenerTemporadaPorFecha(fechaBusqueda);
            if (temporadaPorFecha != null) {
                MessageUtil.success("Fecha " + fechaBusqueda + 
                                   " corresponde a temporada: " + temporadaPorFecha.getNombre() +
                                   " (Modificador: " + temporadaPorFecha.getModificadorPrecio() + "x)");
            } else {
                MessageUtil.info("La fecha seleccionada no corresponde a ninguna temporada especial");
            }
        } catch (Exception e) {
            MessageUtil.error("Error al obtener temporada por fecha: " + e.getMessage());
        }
    }
    
    public void verificarFechaEnTemporada() {
        if (temporada.getFechaInicio() == null || temporada.getFechaFin() == null) {
            MessageUtil.validationError("Complete las fechas primero");
            return;
        }
        
        LocalDate hoy = LocalDate.now();
        if ((hoy.isEqual(temporada.getFechaInicio()) || hoy.isAfter(temporada.getFechaInicio())) &&
            (hoy.isEqual(temporada.getFechaFin()) || hoy.isBefore(temporada.getFechaFin()))) {
            MessageUtil.warn("¡ATENCIÓN! Las fechas seleccionadas incluyen HOY");
        } else if (hoy.isBefore(temporada.getFechaInicio())) {
            MessageUtil.info("Las fechas seleccionadas son futuras");
        } else {
            MessageUtil.info("Las fechas seleccionadas son pasadas");
        }
    }
    
    public void nuevo() {
        temporada = new Temporada();
        fechaBusqueda = null;
        temporadaActual = null;
        temporadaPorFecha = null;
    }
    
    private boolean validarTemporada() {
        if (temporada.getNombre() == null) {
            MessageUtil.validationError("Seleccione un nombre de temporada");
            return false;
        }
        
        if (temporada.getFechaInicio() == null) {
            MessageUtil.validationError("La fecha de inicio es requerida");
            return false;
        }
        
        if (temporada.getFechaFin() == null) {
            MessageUtil.validationError("La fecha de fin es requerida");
            return false;
        }
        
        if (temporada.getFechaFin().isBefore(temporada.getFechaInicio())) {
            MessageUtil.validationError("La fecha fin no puede ser anterior a la fecha inicio");
            return false;
        }
        
        if (temporada.getModificadorPrecio() <= 0) {
            MessageUtil.validationError("El modificador debe ser mayor a 0");
            return false;
        }
        
        // Validar rango razonable de modificador
        if (temporada.getModificadorPrecio() < 0.5 || temporada.getModificadorPrecio() > 3.0) {
            MessageUtil.validationError("El modificador debe estar entre 0.5x y 3.0x");
            return false;
        }
        
        // Validar duración mínima (al menos 1 día)
        if (temporada.getFechaInicio().equals(temporada.getFechaFin())) {
            MessageUtil.validationError("La temporada debe durar al menos 1 día");
            return false;
        }
        
        return true;
    }
    
    private boolean existeSolapamiento(int idExcluir) {
        // Verificar en la lista actual si hay solapamiento
        for (Temporada t : listaTemporadas) {
            if (t.getIdTemporada() != idExcluir) {
                // Verificar solapamiento: (StartA <= EndB) and (EndA >= StartB)
                boolean seSolapa = !temporada.getFechaFin().isBefore(t.getFechaInicio()) &&
                                  !temporada.getFechaInicio().isAfter(t.getFechaFin());
                
                if (seSolapa) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public List<Temporada> getTemporadasActivas() {
        List<Temporada> activas = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        
        for (Temporada t : listaTemporadas) {
            if ((hoy.isEqual(t.getFechaInicio()) || hoy.isAfter(t.getFechaInicio())) &&
                (hoy.isEqual(t.getFechaFin()) || hoy.isBefore(t.getFechaFin()))) {
                activas.add(t);
            }
        }
        return activas;
    }
    
    public List<Temporada> getTemporadasFuturas() {
        List<Temporada> futuras = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        
        for (Temporada t : listaTemporadas) {
            if (t.getFechaInicio().isAfter(hoy)) {
                futuras.add(t);
            }
        }
        return futuras;
    }
    
    public NombreTemporada[] getNombresTemporadasDisponibles() {
        return NombreTemporada.values();
    }
    
    // Getters y Setters
    public Temporada getTemporada() { return temporada; }
    public void setTemporada(Temporada temporada) { this.temporada = temporada; }
    
    public List<Temporada> getListaTemporadas() { return listaTemporadas; }
    public void setListaTemporadas(List<Temporada> listaTemporadas) { 
        this.listaTemporadas = listaTemporadas; 
    }
    
    public List<Temporada> getListaFiltrada() { return listaFiltrada; }
    public void setListaFiltrada(List<Temporada> listaFiltrada) { 
        this.listaFiltrada = listaFiltrada; 
    }
    
    public List<Temporada> getListaPapelera() { return listaPapelera; }
    public void setListaPapelera(List<Temporada> listaPapelera) { 
        this.listaPapelera = listaPapelera; 
    }
    
    public Date getFechaBusqueda() { return fechaBusqueda; }
    public void setFechaBusqueda(Date fechaBusqueda) { 
        this.fechaBusqueda = fechaBusqueda; 
    }
    
    public Temporada getTemporadaActual() { return temporadaActual; }
    public void setTemporadaActual(Temporada temporadaActual) { 
        this.temporadaActual = temporadaActual; 
    }
    
    public Temporada getTemporadaPorFecha() { return temporadaPorFecha; }
    public void setTemporadaPorFecha(Temporada temporadaPorFecha) { 
        this.temporadaPorFecha = temporadaPorFecha; 
    }
}