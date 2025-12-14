package beans;

import dao.EmpleadoDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import models.Empleado;
import models.enums.Rol;
import utils.MessageUtil;

@ManagedBean
@ApplicationScoped
public class EmpleadoBean implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador de versión para serialización (objetos a bytes)
    
    public Empleado empleado = new Empleado();
    public List<Empleado> listaEmpleados = new ArrayList<>();
    public List<Empleado> listaFiltrada = new ArrayList<>();
    public List<Empleado> listaPapelera = new ArrayList<>();
    
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    
    public void listar() {
        try {
            listaEmpleados = empleadoDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar empleados: " + e.getMessage());
        }
    }
    
    public void buscar(int id) {
        empleado = empleadoDAO.buscar(id);
        if (empleado == null) {
            MessageUtil.notFoundError("Empleado", String.valueOf(id));
            empleado = new Empleado();
        }
    }
    
    public void guardar() {
        if (!validarEmpleado()) return;
        
        if (empleadoDAO.existeEmail(empleado.getEmail(), 0)) {
            MessageUtil.duplicateError("email", empleado.getEmail());
            return;
        }
        
        try {
            empleadoDAO.crear(empleado);
            MessageUtil.createSuccess("Empleado");
            listar();
            empleado = new Empleado(); // Limpiar formulario
        } catch (Exception e) {
            MessageUtil.createError("empleado");
        }
    }
    
    public void actualizar() {
        if (empleado.getId() == 0) {
            MessageUtil.validationError("Seleccione un empleado primero");
            return;
        }
        
        if (!validarEmpleado()) return;
        
        if (empleadoDAO.existeEmail(empleado.getEmail(), empleado.getId())) {
            MessageUtil.duplicateError("email", empleado.getEmail());
            return;
        }
        
        try {
            empleadoDAO.actualizar(empleado);
            MessageUtil.updateSuccess("Empleado");
            listar();
        } catch (Exception e) {
            MessageUtil.updateError("empleado");
        }
    }
    
    public void eliminar(int id) {
        try {
            empleadoDAO.eliminar(id);
            MessageUtil.deleteSuccess("Empleado");
            listar();
        } catch (Exception e) {
            MessageUtil.deleteError("empleado");
        }
    }
    
    public void listarPapelera() {
        try {
            listaPapelera = empleadoDAO.listarEliminados();
        } catch (Exception e) {
            MessageUtil.error("Error al listar papelera: " + e.getMessage());
        }
    }
    
    public void restaurar(int id) {
        try {
            empleadoDAO.restaurar(id);
            MessageUtil.success("Empleado restaurado correctamente");
            listarPapelera();
            listar();
        } catch (Exception e) {
            MessageUtil.error("Error al restaurar empleado: " + e.getMessage());
        }
    }
    
    public void nuevo() {
        empleado = new Empleado();
    }
    
    public void buscarPorEmail(String email) {
        Empleado emp = empleadoDAO.buscarPorEmail(email);
        if (emp != null) {
            empleado = emp;
        } else {
            MessageUtil.notFoundError("Empleado con email", email);
        }
    }
    
    public List<Empleado> listarPorRol(Rol rol) {
        try {
            return empleadoDAO.listarPorRol(rol);
        } catch (Exception e) {
            MessageUtil.error("Error al listar por rol: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public void cambiarRol(int id, Rol nuevoRol) {
        Empleado emp = empleadoDAO.buscar(id);
        if (emp != null) {
            emp.setRol(nuevoRol);
            empleadoDAO.actualizar(emp);
            MessageUtil.success("Rol cambiado a " + nuevoRol.getValor());
            listar();
        }
    }
    
    private boolean validarEmpleado() {
        if (empleado.getName() == null || empleado.getName().trim().isEmpty()) {
            MessageUtil.validationError("El nombre es requerido");
            return false;
        }
        
        if (empleado.getEmail() == null || empleado.getEmail().trim().isEmpty()) {
            MessageUtil.validationError("El email es requerido");
            return false;
        }
        
        if (!empleado.getEmail().contains("@")) {
            MessageUtil.validationError("Email inválido");
            return false;
        }
        
        if (empleado.getPassword() == null || empleado.getPassword().trim().isEmpty()) {
            MessageUtil.validationError("La contraseña es requerida");
            return false;
        }
        
        if (empleado.getPassword().length() < 6) {
            MessageUtil.validationError("La contraseña debe tener al menos 6 caracteres");
            return false;
        }
        
        if (empleado.getRol() == null) {
            MessageUtil.validationError("Seleccione un rol");
            return false;
        }
        
        return true;
    }
    
    public Rol[] getRolesDisponibles() {
        return Rol.values();
    }
    
    // Getters y Setters
    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }
    
    public List<Empleado> getListaEmpleados() { return listaEmpleados; }
    public void setListaEmpleados(List<Empleado> listaEmpleados) { 
        this.listaEmpleados = listaEmpleados; 
    }
    
    public List<Empleado> getListaFiltrada() { return listaFiltrada; }
    public void setListaFiltrada(List<Empleado> listaFiltrada) { 
        this.listaFiltrada = listaFiltrada; 
    }
    
    public List<Empleado> getListaPapelera() { return listaPapelera; }
    public void setListaPapelera(List<Empleado> listaPapelera) { 
        this.listaPapelera = listaPapelera; 
    }
}