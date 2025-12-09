package beans;

import dao.TemporadaDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import models.Temporada;
import models.enums.NombreTemporada;

@ManagedBean
@ViewScoped
public class TemporadaBean implements Serializable {

    private Temporada temporada = new Temporada();
    private List<Temporada> listaTemporadas = new ArrayList<>();
    private List<Temporada> listaFiltrada;

    private final TemporadaDAO temporadaDAO = new TemporadaDAO();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public TemporadaBean() {
        listar();
    }

    public void listar() {
        listaTemporadas = temporadaDAO.listar();
        temporada = new Temporada();
    }

    public void guardar() {
        if (!validar(temporada)) return;

        temporada.prePersist();
        temporadaDAO.guardar(temporada);
        listar();
    }

    public void actualizar() {
        if (!validar(temporada)) return;

        temporada.preUpdate();
        temporadaDAO.actualizar(temporada);
        listar();
    }

    public void eliminar(int id) {
        Temporada t = temporadaDAO.buscar(id);
        t.softDelete();
        temporadaDAO.actualizar(t);
        listar();
    }

    public void restaurar(int id) {
        Temporada t = temporadaDAO.buscar(id);
        t.restore();
        temporadaDAO.actualizar(t);
        listar();
    }

    public void buscar(int id) {
        temporada = temporadaDAO.buscar(id);
    }

    

    private boolean validar(Temporada t) {
        var errores = validator.validate(t);

        if (!errores.isEmpty()) {
            for (ConstraintViolation<?> er : errores) {
                System.out.println("VALIDACIÓN ERROR: " + er.getMessage());
            }
            return false;
        }

        if (t.getNombre() == null) {
            System.out.println("VALIDACIÓN ERROR: Debe seleccionar un nombre de temporada");
            return false;
        }

        if (t.getFechaInicio() == null || t.getFechaFin() == null) {
            System.out.println("VALIDACIÓN ERROR: Las fechas son obligatorias");
            return false;
        }

        if (t.getFechaFin().isBefore(t.getFechaInicio())) {
            System.out.println("VALIDACIÓN ERROR: La fecha fin no puede ser anterior a la fecha inicio");
            return false;
        }

        if (t.getModificadorPrecio() <= 0) {
            System.out.println("VALIDACIÓN ERROR: El modificador debe ser mayor a 0");
            return false;
        }

        return true;
    }

    
    public NombreTemporada[] getNombresTemporadasDisponibles() {
        return NombreTemporada.values();
    }

    

    public Temporada getTemporada() {
        return temporada;
    }

    public void setTemporada(Temporada temporada) {
        this.temporada = temporada;
    }

    public List<Temporada> getListaTemporadas() {
        return listaTemporadas;
    }

    public void setListaTemporadas(List<Temporada> listaTemporadas) {
        this.listaTemporadas = listaTemporadas;
    }

    public List<Temporada> getListaFiltrada() {
        return listaFiltrada;
    }

    public void setListaFiltrada(List<Temporada> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
    }
}
