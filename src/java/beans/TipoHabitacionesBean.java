package beans;

import dao.TipoHabitacionDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import models.TipoHabitacion;
import models.enums.NombreTipoHabitacion;

@ManagedBean
@ViewScoped
public class TipoHabitacionBean implements Serializable {

    private TipoHabitacion tipoHabitacion = new TipoHabitacion();
    private List<TipoHabitacion> listaTipoHabitaciones = new ArrayList<>();
    private List<TipoHabitacion> listaFiltrada;

    private final TipoHabitacionDAO tipoHabitacionDAO = new TipoHabitacionDAO();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public TipoHabitacionBean() {
        listar();
    }

   
    public void listar() {
        listaTipoHabitaciones = tipoHabitacionDAO.listar();
        tipoHabitacion = new TipoHabitacion();
    }

    public void guardar() {
        if (!validar(tipoHabitacion)) return;

        tipoHabitacion.prePersist();
        tipoHabitacionDAO.guardar(tipoHabitacion);
        listar();
    }

    public void actualizar() {
        if (!validar(tipoHabitacion)) return;

        tipoHabitacion.preUpdate();
        tipoHabitacionDAO.actualizar(tipoHabitacion);
        listar();
    }

    public void eliminar(int id) {
        TipoHabitacion th = tipoHabitacionDAO.buscar(id);
        th.softDelete();
        tipoHabitacionDAO.actualizar(th);
        listar();
    }

    public void restaurar(int id) {
        TipoHabitacion th = tipoHabitacionDAO.buscar(id);
        th.restore();
        tipoHabitacionDAO.actualizar(th);
        listar();
    }

    public void buscar(int id) {
        tipoHabitacion = tipoHabitacionDAO.buscar(id);
    }

   
    private boolean validar(TipoHabitacion t) {
        var errores = validator.validate(t);

        if (!errores.isEmpty()) {
            for (ConstraintViolation<?> er : errores) {
                System.out.println("VALIDACIÓN ERROR: " + er.getMessage());
            }
            return false;
        }

        if (t.getNombreTipo() == null) {
            System.out.println("VALIDACIÓN ERROR: El nombre del tipo de habitación es obligatorio");
            return false;
        }

        if (t.getDescripcion() == null || t.getDescripcion().trim().isEmpty()) {
            System.out.println("VALIDACIÓN ERROR: La descripción es obligatoria");
            return false;
        }

        if (t.getCapacidadMaxima() <= 0) {
            System.out.println("VALIDACIÓN ERROR: La capacidad máxima debe ser mayor a 0");
            return false;
        }

        return true;
    }

   
    public TipoHabitacion getTipoHabitacion() {
        return tipoHabitacion;
    }

    public void setTipoHabitacion(TipoHabitacion tipoHabitacion) {
        this.tipoHabitacion = tipoHabitacion;
    }

    public List<TipoHabitacion> getListaTipoHabitaciones() {
        return listaTipoHabitaciones;
    }

    public void setListaTipoHabitaciones(List<TipoHabitacion> listaTipoHabitaciones) {
        this.listaTipoHabitaciones = listaTipoHabitaciones;
    }

    public List<TipoHabitacion> getListaFiltrada() {
        return listaFiltrada;
    }

    public void setListaFiltrada(List<TipoHabitacion> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
    }

    
    public NombreTipoHabitacion[] getNombresDisponibles() {
        return NombreTipoHabitacion.values();
    }
}