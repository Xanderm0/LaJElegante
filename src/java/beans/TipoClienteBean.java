package beans;

import dao.TipoClienteDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import models.TipoCliente;
import utils.MessageUtil;

@ManagedBean
@ApplicationScoped
public class TipoClienteBean implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador de versión para serialización (objetos a bytes)
    
    public TipoCliente tipoCliente = new TipoCliente();
    public List<TipoCliente> listaTipoClientes = new ArrayList<>();
    public List<TipoCliente> listaFiltrada = new ArrayList<>();
    public List<TipoCliente> listaPapelera = new ArrayList<>();
    
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();
    
    public void listar() {
        try {
            listaTipoClientes = tipoClienteDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar tipos de cliente: " + e.getMessage());
        }
    }
    
    public void buscar(int id) {
        tipoCliente = tipoClienteDAO.buscar(id);
        if (tipoCliente == null) {
            MessageUtil.notFoundError("Tipo de cliente", String.valueOf(id));
            tipoCliente = new TipoCliente();
        }
    }
    
    public void guardar() {
        if (!validarTipoCliente()) return;
        
        if (nombreTipoExiste(tipoCliente.getNombreTipo(), 0)) {
            MessageUtil.duplicateError("tipo de cliente", tipoCliente.getNombreTipo());
            return;
        }
        
        try {
            tipoClienteDAO.crear(tipoCliente);
            MessageUtil.createSuccess("Tipo de cliente");
            listar();
            tipoCliente = new TipoCliente(); // Limpiar formulario
        } catch (Exception e) {
            MessageUtil.createError("tipo de cliente");
        }
    }
    
    public void actualizar() {
        if (tipoCliente.getIdTipoCliente() == 0) {
            MessageUtil.validationError("Seleccione un tipo de cliente primero");
            return;
        }
        
        if (!validarTipoCliente()) return;
        
        if (nombreTipoExiste(tipoCliente.getNombreTipo(), tipoCliente.getIdTipoCliente())) {
            MessageUtil.duplicateError("tipo de cliente", tipoCliente.getNombreTipo());
            return;
        }
        
        try {
            tipoClienteDAO.actualizar(tipoCliente);
            MessageUtil.updateSuccess("Tipo de cliente");
            listar();
        } catch (Exception e) {
            MessageUtil.updateError("tipo de cliente");
        }
    }
    
    public void eliminar(int id) {
        try {
            tipoClienteDAO.eliminar(id);
            MessageUtil.deleteSuccess("Tipo de cliente");
            listar();
        } catch (Exception e) {
            MessageUtil.deleteError("tipo de cliente");
        }
    }
    
    public void listarPapelera() {
        try {
            listaPapelera = tipoClienteDAO.listarEliminados();
        } catch (Exception e) {
            MessageUtil.error("Error al listar papelera: " + e.getMessage());
        }
    }
    
    public void restaurar(int id) {
        try {
            tipoClienteDAO.restaurar(id);
            MessageUtil.success("Tipo de cliente restaurado correctamente");
            listarPapelera();
            listar();
        } catch (Exception e) {
            MessageUtil.error("Error al restaurar tipo de cliente: " + e.getMessage());
        }
    }
    
    public void nuevo() {
        tipoCliente = new TipoCliente();
    }
    
    public void obtenerTiposActivos() {
        // Ya está implementado en listar() - este método puede ser útil para filtros
        listar();
    }
    
    private boolean validarTipoCliente() {
        if (tipoCliente.getNombreTipo() == null || tipoCliente.getNombreTipo().trim().isEmpty()) {
            MessageUtil.validationError("El nombre del tipo de cliente es obligatorio");
            return false;
        }
        
        if (tipoCliente.getNombreTipo().length() < 3) {
            MessageUtil.validationError("El nombre debe tener al menos 3 caracteres");
            return false;
        }
        
        if (tipoCliente.getNombreTipo().length() > 50) {
            MessageUtil.validationError("El nombre no puede exceder 50 caracteres");
            return false;
        }
        
        // Validar caracteres válidos
        if (!tipoCliente.getNombreTipo().matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            MessageUtil.validationError("El nombre solo puede contener letras y espacios");
            return false;
        }
        
        return true;
    }
    
    private boolean nombreTipoExiste(String nombre, int idExcluir) {
        // Buscar en la lista actual de tipos
        for (TipoCliente tc : listaTipoClientes) {
            if (tc.getNombreTipo().equalsIgnoreCase(nombre) && 
                tc.getIdTipoCliente() != idExcluir) {
                return true;
            }
        }
        return false;
    }
    
    // Método auxiliar para obtener tipos de cliente comunes
    public List<String> getTiposPredefinidos() {
        List<String> tipos = new ArrayList<>();
        tipos.add("REGULAR");
        tipos.add("FRECUENTE");
        tipos.add("VIP");
        tipos.add("CORPORATIVO");
        tipos.add("ESTUDIANTE");
        tipos.add("ADULTO MAYOR");
        tipos.add("NO REGISTRADO");
        return tipos;
    }
    
    public void cargarTipoPredefinido(String tipo) {
        tipoCliente = new TipoCliente();
        tipoCliente.setNombreTipo(tipo);
        MessageUtil.success("Tipo predefinido cargado: " + tipo);
    }
    
    // Getters y Setters
    public TipoCliente getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(TipoCliente tipoCliente) { this.tipoCliente = tipoCliente; }
    
    public List<TipoCliente> getListaTipoClientes() { return listaTipoClientes; }
    public void setListaTipoClientes(List<TipoCliente> listaTipoClientes) { 
        this.listaTipoClientes = listaTipoClientes; 
    }
    
    public List<TipoCliente> getListaFiltrada() { return listaFiltrada; }
    public void setListaFiltrada(List<TipoCliente> listaFiltrada) { 
        this.listaFiltrada = listaFiltrada; 
    }
    
    public List<TipoCliente> getListaPapelera() { return listaPapelera; }
    public void setListaPapelera(List<TipoCliente> listaPapelera) { 
        this.listaPapelera = listaPapelera; 
    }
}