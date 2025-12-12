package beans;

import dao.TarifaDAO;
import dao.TipoHabitacionDAO;
import dao.TemporadaDAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import models.Tarifa;
import models.TipoHabitacion;
import models.Temporada;

@ManagedBean
@ViewScoped
public class TarifaBean implements Serializable {

    private Tarifa tarifa = new Tarifa();
    private List<Tarifa> listaTarifas = new ArrayList<>();
    private List<Tarifa> listaFiltrada;

    private List<TipoHabitacion> listaTipoHabitaciones = new ArrayList<>();
    private List<Temporada> listaTemporadas = new ArrayList<>();

    private final TarifaDAO tarifaDAO = new TarifaDAO();
    private final TipoHabitacionDAO tipoHabitacionDAO = new TipoHabitacionDAO();
    private final TemporadaDAO temporadaDAO = new TemporadaDAO();

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


    public TarifaBean() {
        listar();
        cargarTipoHabitaciones();
        cargarTemporadas();
    }

   
    public void listar() {
        listaTarifas = tarifaDAO.listar();
        tarifa = new Tarifa();
    }

    public void guardar() {
        if (!validar(tarifa)) return;

        tarifa.prePersist();
        tarifaDAO.guardar(tarifa);
        listar();
    }

    public void actualizar() {
        if (!validar(tarifa)) return;

        tarifa.preUpdate();
        tarifaDAO.actualizar(tarifa);
        listar();
    }

    public void eliminar(int id) {
        Tarifa t = tarifaDAO.buscar(id);
        t.softDelete();
        tarifaDAO.actualizar(t);
        listar();
    }

    public void restaurar(int id) {
        Tarifa t = tarifaDAO.buscar(id);
        t.restore();
        tarifaDAO.actualizar(t);
        listar();
    }

    public void buscar(int id) {
        tarifa = tarifaDAO.buscar(id);
    }

   
    private boolean validar(Tarifa t) {
        var errores = validator.validate(t);

        if (!errores.isEmpty()) {
            for (ConstraintViolation<?> er : errores) {
                System.out.println("VALIDACIÓN ERROR: " + er.getMessage());
            }
            return false;
        }

        if (t.getTarifaFija() <= 0) {
            System.out.println("VALIDACIÓN ERROR: La tarifa fija debe ser mayor a 0");
            return false;
        }

        if (t.getPrecioFinal() <= 0) {
            System.out.println("VALIDACIÓN ERROR: El precio final debe ser mayor a 0");
            return false;
        }

        if (t.getTipoHabitacion() == null) {
            System.out.println("VALIDACIÓN ERROR: Debe seleccionar el tipo de habitación");
            return false;
        }

        if (t.getTemporada() == null) {
            System.out.println("VALIDACIÓN ERROR: Debe seleccionar una temporada");
            return false;
        }

        return true;
    }

  
    public void cargarTipoHabitaciones() {
        listaTipoHabitaciones = tipoHabitacionDAO.listar();
    }

    public void cargarTemporadas() {
        listaTemporadas = temporadaDAO.listar();
    }

  
    public Tarifa getTarifa() {
        return tarifa;
    }

    public void setTarifa(Tarifa tarifa) {
        this.tarifa = tarifa;
    }

    public List<Tarifa> getListaTarifas() {
        return listaTarifas;
    }

    public void setListaTarifas(List<Tarifa> listaTarifas) {
        this.listaTarifas = listaTarifas;
    }

    public List<Tarifa> getListaFiltrada() {
        return listaFiltrada;
    }

    public void setListaFiltrada(List<Tarifa> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
    }

    public List<TipoHabitacion> getListaTipoHabitaciones() {
        return listaTipoHabitaciones;
    }

    public void setListaTipoHabitaciones(List<TipoHabitacion> listaTipoHabitaciones) {
        this.listaTipoHabitaciones = listaTipoHabitaciones;
    }

    public List<Temporada> getListaTemporadas() {
        return listaTemporadas;
    }

    public void setListaTemporadas(List<Temporada> listaTemporadas) {
        this.listaTemporadas = listaTemporadas;
    }
}