package beans;

import dao.HabitacionDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import models.Habitacion;
import models.enums.EstadoHabitacion;

@ManagedBean
@ViewScoped
public class HabitacionBean implements Serializable {

    private Habitacion habitacion = new Habitacion();
    private List<Habitacion> listaHabitaciones = new ArrayList<>();
    private List<Habitacion> listaFiltrada;

    private final HabitacionDAO habitacionDAO = new HabitacionDAO();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public HabitacionBean() {
        listar();
    }

   
    public void listar() {
        listaHabitaciones = habitacionDAO.listar();
        habitacion = new Habitacion();
    }

    public void guardar() {
        if (!validar(habitacion)) return;

        habitacion.prePersist();
        habitacionDAO.guardar(habitacion);
        listar();
    }

    public void actualizar() {
        if (!validar(habitacion)) return;

        habitacion.preUpdate();
        habitacionDAO.actualizar(habitacion);
        listar();
    }

    public void eliminar(int id) {
        Habitacion hab = habitacionDAO.buscar(id);
        hab.softDelete();
        habitacionDAO.actualizar(hab);
        listar();
    }

    public void restaurar(int id) {
        Habitacion hab = habitacionDAO.buscar(id);
        hab.restore();
        habitacionDAO.actualizar(hab);
        listar();
    }

    public void buscar(int id) {
        habitacion = habitacionDAO.buscar(id);
    }

   

    private boolean validar(Habitacion h) {
        var errores = validator.validate(h);
        if (!errores.isEmpty()) {
            for (ConstraintViolation<?> er : errores) {
                System.out.println("VALIDACIÓN ERROR: " + er.getMessage());
            }
            return false;
        }
        return true;
    }

   
  

    public EstadoHabitacion[] getEstadosDisponibles() {
        return EstadoHabitacion.values();
    }

   

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public List<Habitacion> getListaHabitaciones() {
        return listaHabitaciones;
    }

    public void setListaHabitaciones(List<Habitacion> listaHabitaciones) {
        this.listaHabitaciones = listaHabitaciones;
    }

    public List<Habitacion> getListaFiltrada() {
        return listaFiltrada;
    }

    public void setListaFiltrada(List<Habitacion> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
    }
}
