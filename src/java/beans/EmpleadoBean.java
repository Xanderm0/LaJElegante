package beans;

import dao.EmpleadoDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import models.Empleado;
import models.enums.Rol;

@Named
@ViewScoped
public class EmpleadoBean implements Serializable {

    private Empleado empleado = new Empleado();
    private List<Empleado> listaEmpleados = new ArrayList<>();
    private List<Empleado> listaFiltrada = new ArrayList<>();

    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public EmpleadoBean() {
        listar();
    }

    public void listar() {
        listaEmpleados = empleadoDAO.listar();
        empleado = new Empleado();
    }

    public void guardar() {
        if (!validar(empleado)) return;

        empleado.prePersist();
        empleadoDAO.guardar(empleado);
        System.out.println("Empleado guardado correctamente.");
        listar();
    }

    public void actualizar() {
        if (!validar(empleado)) return;

        empleado.preUpdate();
        empleadoDAO.actualizar(empleado);
        System.out.println("Empleado actualizado correctamente.");
        listar();
    }

    public void eliminar(int id) {
        Empleado emp = empleadoDAO.buscar(id);
        if (emp != null) {
            emp.softDelete();
            empleadoDAO.actualizar(emp);
            System.out.println("Empleado marcado como eliminado.");
            listar();
        }
    }

    public void restaurar(int id) {
        Empleado emp = empleadoDAO.buscar(id);
        if (emp != null) {
            emp.restore();
            empleadoDAO.actualizar(emp);
            System.out.println("Empleado restaurado.");
            listar();
        }
    }

    public void buscar(int id) {
        empleado = empleadoDAO.buscar(id);
    }

    private boolean validar(Empleado e) {
        var errores = validator.validate(e);
        if (!errores.isEmpty()) {
            for (ConstraintViolation<?> er : errores) {
                System.out.println("VALIDACIÓN ERROR: " + er.getMessage());
            }
            return false;
        }
        return true;
    }

    public Rol[] getRolesDisponibles() {
        return Rol.values();
    }

    // Getters y Setters
    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public List<Empleado> getListaEmpleados() {
        return listaEmpleados;
    }

    public void setListaEmpleados(List<Empleado> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }

    public List<Empleado> getListaFiltrada() {
        return listaFiltrada;
    }

    public void setListaFiltrada(List<Empleado> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
    }
}