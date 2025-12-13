package beans;

import dao.MesaDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import models.Mesa;
import models.enums.Estado;
import utils.MessageUtil;

@ManagedBean
@ApplicationScoped
public class MesaBean implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador de versión para serialización (objetos a bytes)
    
    public Mesa mesa = new Mesa();
    public List<Mesa> listaMesas = new ArrayList<>();
    public List<Mesa> listaFiltrada = new ArrayList<>();
    public List<Mesa> listaPapelera = new ArrayList<>();
    
    private final MesaDAO mesaDAO = new MesaDAO();
    
    public void listar() {
        try {
            listaMesas = mesaDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar mesas: " + e.getMessage());
        }
    }
    
    public void buscar(int id) {
        mesa = mesaDAO.buscar(id);
        if (mesa == null) {
            MessageUtil.notFoundError("Mesa", String.valueOf(id));
            mesa = new Mesa();
        }
    }
    
    public void guardar() {
        if (!validarMesa()) return;
        
        if (numeroMesaExiste(mesa.getNumeroMesa(), 0)) {
            MessageUtil.duplicateError("número de mesa", String.valueOf(mesa.getNumeroMesa()));
            return;
        }
        
        try {
            mesaDAO.crear(mesa);
            MessageUtil.createSuccess("Mesa");
            listar();
            mesa = new Mesa(); // Limpiar formulario
        } catch (Exception e) {
            MessageUtil.createError("mesa");
        }
    }
    
    public void actualizar() {
        if (mesa.getIdMesa() == 0) {
            MessageUtil.validationError("Seleccione una mesa primero");
            return;
        }
        
        if (!validarMesa()) return;
        
        if (numeroMesaExiste(mesa.getNumeroMesa(), mesa.getIdMesa())) {
            MessageUtil.duplicateError("número de mesa", String.valueOf(mesa.getNumeroMesa()));
            return;
        }
        
        try {
            mesaDAO.actualizar(mesa);
            MessageUtil.updateSuccess("Mesa");
            listar();
        } catch (Exception e) {
            MessageUtil.updateError("mesa");
        }
    }
    
    public void eliminar(int id) {
        try {
            mesaDAO.eliminar(id);
            MessageUtil.deleteSuccess("Mesa");
            listar();
        } catch (Exception e) {
            MessageUtil.deleteError("mesa");
        }
    }
    
    public void listarPapelera() {
        try {
            listaPapelera = mesaDAO.listarEliminados();
        } catch (Exception e) {
            MessageUtil.error("Error al listar papelera: " + e.getMessage());
        }
    }
    
    public void restaurar(int id) {
        try {
            mesaDAO.restaurar(id);
            MessageUtil.success("Mesa restaurada correctamente");
            listarPapelera();
            listar();
        } catch (Exception e) {
            MessageUtil.error("Error al restaurar mesa: " + e.getMessage());
        }
    }
    
    public void obtenerMesasDisponibles(Date fecha, String turno, int capacidadMinima) {
        try {
            listaMesas = mesaDAO.obtenerMesasDisponibles(fecha, turno, capacidadMinima);
        } catch (Exception e) {
            MessageUtil.error("Error al buscar mesas disponibles: " + e.getMessage());
        }
    }
    
    public void obtenerMesasPorZona(String zona) {
        try {
            listaMesas = mesaDAO.obtenerMesasPorZona(zona);
        } catch (Exception e) {
            MessageUtil.error("Error al buscar mesas por zona: " + e.getMessage());
        }
    }
    
    public void obtenerMesasPorCapacidad(int capacidadMin, int capacidadMax) {
        try {
            listaMesas = mesaDAO.obtenerMesasPorCapacidad(capacidadMin, capacidadMax);
        } catch (Exception e) {
            MessageUtil.error("Error al buscar mesas por capacidad: " + e.getMessage());
        }
    }
    
    public void cambiarEstado(int id, Estado nuevoEstado) {
        try {
            mesaDAO.cambiarEstadoMesa(id, nuevoEstado);
            MessageUtil.success("Estado cambiado a " + nuevoEstado.getValor());
            listar();
        } catch (Exception e) {
            MessageUtil.error("Error al cambiar estado: " + e.getMessage());
        }
    }
    
    public void verificarDisponibilidad(int idMesa, Date fecha, String turno) {
        try {
            boolean disponible = mesaDAO.estaDisponible(idMesa, fecha, turno);
            if (disponible) {
                MessageUtil.success("La mesa está disponible para esa fecha y turno");
            } else {
                MessageUtil.warn("La mesa NO está disponible para esa fecha y turno");
            }
        } catch (Exception e) {
            MessageUtil.error("Error al verificar disponibilidad: " + e.getMessage());
        }
    }
    
    public void nuevo() {
        mesa = new Mesa();
    }
    
    private boolean validarMesa() {
        if (mesa.getNumeroMesa() <= 0) {
            MessageUtil.validationError("El número de mesa debe ser mayor a 0");
            return false;
        }
        
        if (mesa.getCapacidad() <= 0) {
            MessageUtil.validationError("La capacidad debe ser mayor a 0");
            return false;
        }
        
        if (mesa.getZona() == null || mesa.getZona().trim().isEmpty()) {
            MessageUtil.validationError("La zona es requerida");
            return false;
        }
        
        if (mesa.getEstado() == null) {
            MessageUtil.validationError("Seleccione un estado para la mesa");
            return false;
        }
        
        // Validación de capacidad máxima razonable
        if (mesa.getCapacidad() > 20) {
            MessageUtil.validationError("La capacidad máxima por mesa es 20 personas");
            return false;
        }
        
        return true;
    }
    
    private boolean numeroMesaExiste(int numero, int idExcluir) {
        // Buscar en la lista actual de mesas
        for (Mesa m : listaMesas) {
            if (m.getNumeroMesa() == numero && m.getIdMesa() != idExcluir) {
                return true;
            }
        }
        return false;
    }
    
    public Estado[] getEstadosDisponibles() {
        return Estado.values();
    }
    
    // Métodos auxiliares para zonas comunes
    public List<String> getZonasDisponibles() {
        List<String> zonas = new ArrayList<>();
        zonas.add("SALÓN PRINCIPAL");
        zonas.add("TERRAZA");
        zonas.add("SALÓN VIP");
        zonas.add("JARDÍN");
        zonas.add("PISCINA");
        return zonas;
    }
    
    public List<String> getTurnosDisponibles() {
        List<String> turnos = new ArrayList<>();
        turnos.add("DESAYUNO");
        turnos.add("ALMUERZO");
        turnos.add("CENA");
        return turnos;
    }
    
    // Getters y Setters
    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }
    
    public List<Mesa> getListaMesas() { return listaMesas; }
    public void setListaMesas(List<Mesa> listaMesas) { 
        this.listaMesas = listaMesas; 
    }
    
    public List<Mesa> getListaFiltrada() { return listaFiltrada; }
    public void setListaFiltrada(List<Mesa> listaFiltrada) { 
        this.listaFiltrada = listaFiltrada; 
    }
    
    public List<Mesa> getListaPapelera() { return listaPapelera; }
    public void setListaPapelera(List<Mesa> listaPapelera) { 
        this.listaPapelera = listaPapelera; 
    }
}