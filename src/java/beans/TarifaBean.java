package beans;

import dao.TarifaDAO;
import dao.TipoHabitacionDAO;
import dao.TemporadaDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import models.Tarifa;
import models.TipoHabitacion;
import models.Temporada;
import models.enums.Estado;
import utils.MessageUtil;

@ManagedBean
@ApplicationScoped
public class TarifaBean implements Serializable {
    private static final long serialVersionUID = 1L; // UID para Serializable
    
    public Tarifa tarifa = new Tarifa();
    public List<Tarifa> listaTarifas = new ArrayList<>();
    public List<Tarifa> listaFiltrada = new ArrayList<>();
    public List<Tarifa> listaPapelera = new ArrayList<>();
    
    public List<TipoHabitacion> listaTipoHabitaciones = new ArrayList<>();
    public List<Temporada> listaTemporadas = new ArrayList<>();
    
    private final TarifaDAO tarifaDAO = new TarifaDAO();
    private final TipoHabitacionDAO tipoHabitacionDAO = new TipoHabitacionDAO();
    private final TemporadaDAO temporadaDAO = new TemporadaDAO();
    
    // Para cálculos
    private int tipoHabitacionCalculo;
    private Date fechaCalculo;
    private Date fechaInicioCalculo;
    private Date fechaFinCalculo;
    private double precioCalculado;
    private double precioTotalReserva;
    
    public void listar() {
        try {
            listaTarifas = tarifaDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar tarifas: " + e.getMessage());
        }
    }
    
    public void buscar(int id) {
        tarifa = tarifaDAO.buscar(id);
        if (tarifa == null) {
            MessageUtil.notFoundError("Tarifa", String.valueOf(id));
            tarifa = new Tarifa();
        }
    }
    
    public void guardar() {
        if (!validarTarifa()) return;
        
        calcularPrecioFinal();
        
        try {
            tarifaDAO.crear(tarifa);
            MessageUtil.createSuccess("Tarifa");
            listar();
            tarifa = new Tarifa(); // Limpiar formulario
        } catch (Exception e) {
            MessageUtil.createError("tarifa");
        }
    }
    
    public void actualizar() {
        if (tarifa.getIdTarifa() == 0) {
            MessageUtil.validationError("Seleccione una tarifa primero");
            return;
        }
        
        if (!validarTarifa()) return;
        
        calcularPrecioFinal();
        
        try {
            tarifaDAO.actualizar(tarifa);
            MessageUtil.updateSuccess("Tarifa");
            listar();
        } catch (Exception e) {
            MessageUtil.updateError("tarifa");
        }
    }
    
    public void eliminar(int id) {
        try {
            tarifaDAO.eliminar(id);
            MessageUtil.deleteSuccess("Tarifa");
            listar();
        } catch (Exception e) {
            MessageUtil.deleteError("tarifa");
        }
    }
    
    public void listarPapelera() {
        try {
            listaPapelera = tarifaDAO.listarEliminados();
        } catch (Exception e) {
            MessageUtil.error("Error al listar papelera: " + e.getMessage());
        }
    }
    
    public void restaurar(int id) {
        try {
            tarifaDAO.restaurar(id);
            MessageUtil.success("Tarifa restaurada correctamente");
            listarPapelera();
            listar();
        } catch (Exception e) {
            MessageUtil.error("Error al restaurar tarifa: " + e.getMessage());
        }
    }
    
    public void cargarTipoHabitaciones() {
        try {
            listaTipoHabitaciones = tipoHabitacionDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al cargar tipos de habitación: " + e.getMessage());
        }
    }
    
    public void cargarTemporadas() {
        try {
            listaTemporadas = temporadaDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al cargar temporadas: " + e.getMessage());
        }
    }
    
    public void calcularPrecioPorFecha() {
        if (tipoHabitacionCalculo == 0 || fechaCalculo == null) {
            MessageUtil.validationError("Seleccione tipo de habitación y fecha");
            return;
        }
        
        try {
            precioCalculado = tarifaDAO.obtenerPrecioPorFecha(tipoHabitacionCalculo, fechaCalculo);
            if (precioCalculado > 0) {
                MessageUtil.success("Precio calculado: $" + String.format("%.2f", precioCalculado));
            } else {
                MessageUtil.warn("No hay tarifa configurada para esa fecha");
            }
        } catch (Exception e) {
            MessageUtil.error("Error al calcular precio: " + e.getMessage());
        }
    }
    
    public void calcularPrecioTotalReserva() {
        if (tipoHabitacionCalculo == 0 || fechaInicioCalculo == null || fechaFinCalculo == null) {
            MessageUtil.validationError("Complete todos los campos para el cálculo");
            return;
        }
        
        if (fechaInicioCalculo.after(fechaFinCalculo)) {
            MessageUtil.validationError("La fecha de inicio debe ser anterior a la fecha de fin");
            return;
        }
        
        try {
            precioTotalReserva = tarifaDAO.calcularPrecioTotalReserva(
                tipoHabitacionCalculo, fechaInicioCalculo, fechaFinCalculo);
            
            if (precioTotalReserva > 0) {
                long dias = (fechaFinCalculo.getTime() - fechaInicioCalculo.getTime()) / (1000 * 60 * 60 * 24);
                dias = Math.max(1, dias); // Mínimo 1 día
                
                MessageUtil.success(String.format(
                    "Precio total para %d días: $%.2f (Promedio por noche: $%.2f)",
                    dias, precioTotalReserva, precioTotalReserva / dias
                ));
            } else {
                MessageUtil.warn("No se pudo calcular el precio total");
            }
        } catch (Exception e) {
            MessageUtil.error("Error al calcular precio total: " + e.getMessage());
        }
    }
    
    public void obtenerTarifaVigente() {
        if (tipoHabitacionCalculo == 0 || fechaCalculo == null) {
            MessageUtil.validationError("Seleccione tipo de habitación y fecha");
            return;
        }
        
        try {
            Tarifa tarifaVigente = tarifaDAO.obtenerTarifaVigente(tipoHabitacionCalculo, fechaCalculo);
            if (tarifaVigente != null) {
                tarifa = tarifaVigente;
                MessageUtil.success("Tarifa vigente cargada");
            } else {
                MessageUtil.warn("No hay tarifa vigente para esa fecha");
                tarifa = new Tarifa();
            }
        } catch (Exception e) {
            MessageUtil.error("Error al obtener tarifa vigente: " + e.getMessage());
        }
    }
    
    public void cambiarEstado(int id, Estado nuevoEstado) {
        Tarifa t = tarifaDAO.buscar(id);
        if (t != null) {
            t.setEstado(nuevoEstado);
            tarifaDAO.actualizar(t);
            MessageUtil.success("Estado cambiado a " + nuevoEstado.getValor());
            listar();
        }
    }
    
    public void nuevo() {
        tarifa = new Tarifa();
    }
    
    private void calcularPrecioFinal() {
        if (tarifa.getTarifaFija() > 0 && tarifa.getTemporada() != null) {
            // Aplicar modificador de temporada
            double modificador = tarifa.getTemporada().getModificadorPrecio();
            double precioFinal = tarifa.getTarifaFija() * modificador;
            tarifa.setPrecioFinal((float) precioFinal);
        } else {
            // Sin temporada, precio final = tarifa fija
            tarifa.setPrecioFinal(tarifa.getTarifaFija());
        }
    }
    
    private boolean validarTarifa() {
        if (tarifa.getTarifaFija() <= 0) {
            MessageUtil.validationError("La tarifa fija debe ser mayor a 0");
            return false;
        }
        
        if (tarifa.getTipoHabitacion() == null || tarifa.getTipoHabitacion().getIdTipoHabitacion() == 0) {
            MessageUtil.validationError("Seleccione el tipo de habitación");
            return false;
        }
        
        // Temporada puede ser null (tarifa base)
        if (tarifa.getEstado() == null) {
            MessageUtil.validationError("Seleccione un estado para la tarifa");
            return false;
        }
        
        // Validar rango razonable de tarifa
        if (tarifa.getTarifaFija() > 10000) {
            MessageUtil.validationError("La tarifa fija no puede exceder $10,000");
            return false;
        }
        
        return true;
    }
    
    public Estado[] getEstadosDisponibles() {
        return Estado.values();
    }
    
    // Getters y Setters
    public Tarifa getTarifa() { return tarifa; }
    public void setTarifa(Tarifa tarifa) { this.tarifa = tarifa; }
    
    public List<Tarifa> getListaTarifas() { return listaTarifas; }
    public void setListaTarifas(List<Tarifa> listaTarifas) { 
        this.listaTarifas = listaTarifas; 
    }
    
    public List<Tarifa> getListaFiltrada() { return listaFiltrada; }
    public void setListaFiltrada(List<Tarifa> listaFiltrada) { 
        this.listaFiltrada = listaFiltrada; 
    }
    
    public List<Tarifa> getListaPapelera() { return listaPapelera; }
    public void setListaPapelera(List<Tarifa> listaPapelera) { 
        this.listaPapelera = listaPapelera; 
    }
    
    public List<TipoHabitacion> getListaTipoHabitaciones() { return listaTipoHabitaciones; }
    public void setListaTipoHabitaciones(List<TipoHabitacion> listaTipoHabitaciones) { 
        this.listaTipoHabitaciones = listaTipoHabitaciones; 
    }
    
    public List<Temporada> getListaTemporadas() { return listaTemporadas; }
    public void setListaTemporadas(List<Temporada> listaTemporadas) { 
        this.listaTemporadas = listaTemporadas; 
    }
    
    // Getters y Setters para cálculos
    public int getTipoHabitacionCalculo() { return tipoHabitacionCalculo; }
    public void setTipoHabitacionCalculo(int tipoHabitacionCalculo) { 
        this.tipoHabitacionCalculo = tipoHabitacionCalculo; 
    }
    
    public Date getFechaCalculo() { return fechaCalculo; }
    public void setFechaCalculo(Date fechaCalculo) { 
        this.fechaCalculo = fechaCalculo; 
    }
    
    public Date getFechaInicioCalculo() { return fechaInicioCalculo; }
    public void setFechaInicioCalculo(Date fechaInicioCalculo) { 
        this.fechaInicioCalculo = fechaInicioCalculo; 
    }
    
    public Date getFechaFinCalculo() { return fechaFinCalculo; }
    public void setFechaFinCalculo(Date fechaFinCalculo) { 
        this.fechaFinCalculo = fechaFinCalculo; 
    }
    
    public double getPrecioCalculado() { return precioCalculado; }
    public void setPrecioCalculado(double precioCalculado) { 
        this.precioCalculado = precioCalculado; 
    }
    
    public double getPrecioTotalReserva() { return precioTotalReserva; }
    public void setPrecioTotalReserva(double precioTotalReserva) { 
        this.precioTotalReserva = precioTotalReserva; 
    }
}