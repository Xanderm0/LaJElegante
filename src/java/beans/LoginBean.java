package beans;

import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import dao.ClienteDAO;
import dao.EmpleadoDAO;
import javax.faces.bean.SessionScoped;
import models.Cliente;
import models.Empleado;
import utils.Utils;
import utils.MessageUtil;

@ManagedBean
@SessionScoped
public class LoginBean implements Serializable {
    
    private String email;
    private String password;
    private Object usuarioLogueado; // Puede ser Cliente o Empleado
    
    private ClienteDAO clienteDAO = new ClienteDAO();
    private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    
    public String login() {
        if (email == null || email.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            MessageUtil.requiredFieldsWarning();
            return null;
        }

        try {
            Cliente cliente = clienteDAO.buscarPorEmail(email);
            if (cliente != null) {
                if (Utils.checkPassword(password, cliente.getContraseña())) {
                    usuarioLogueado = cliente;
                    guardarEnSesion(cliente, "CLIENTE");
                    MessageUtil.loginSuccess(cliente.getNombre());
                    return "/views/cliente/dashboard/index?faces-redirect=true";
                }
            }

            Empleado empleado = empleadoDAO.buscarPorEmail(email);
            if (empleado != null) {
                if (Utils.checkPassword(password, empleado.getPassword())) {
                    usuarioLogueado = empleado;
                    guardarEnSesion(empleado, "EMPLEADO");
                    MessageUtil.loginSuccess(empleado.getName());
                    limpiarFormulario();
                    
                    String rol = empleado.getRol().getValor();

                    switch(rol) {
                        case "administrador":
                            return "/views/empleado/admin/dashboard/index?faces-redirect=true";
                        case "gerente_general":
                            return "/views/empleado/gerente_general/dashboard/index?faces-redirect=true";
                        case "gerente_habitaciones":
                            return "/views/empleado/gerente_habitaciones/dashboard/index?faces-redirect=true";
                        case "gerente_alimentos":
                            return "/views/empleado/gerente_alimentos/dashboard/index?faces-redirect=true";
                        case "recepcionista":
                            return "/views/empleado/recepcionista/dashboard/index?faces-redirect=true";
                        case "asistente_administrativo":
                            return "/views/empleado/asistente/dashboard/index?faces-redirect=true";
                        default:
                            MessageUtil.warn("Rol no configurado", "Redirigiendo al lobby principal");
                            return "/views/hotel/lobby?faces-redirect=true";
                    }
                }
            }

            MessageUtil.loginError();
            return null;

        } catch (Exception e) {
            MessageUtil.error("Error del sistema: " + e.getMessage());
            return null;
        }
    }
    
    private void limpiarFormulario() {
        this.email = null;
        this.password = null;
    }
    
    private void guardarEnSesion(Object usuario, String tipo) {
        FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().put("usuario", usuario);
        FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().put("tipoUsuario", tipo);
        FacesContext.getCurrentInstance().getExternalContext()
            .getSessionMap().put("loginBean", this);
    }
    
    public String logout() {
        MessageUtil.logoutSuccess();limpiarFormulario();
        FacesContext.getCurrentInstance().getExternalContext()
            .invalidateSession();
        usuarioLogueado = null;
        return "/views/hotel/lobby?faces-redirect=true";
    }
    
    public boolean isCliente() {
        return usuarioLogueado instanceof Cliente;
    }
    
    public boolean isEmpleado() {
        return usuarioLogueado instanceof Empleado;
    }
    
    public boolean isAdmin() {
        if (usuarioLogueado instanceof Empleado) {
            return "administrador".equals(((Empleado)usuarioLogueado).getRol().getValor());
        }
        return false;
    }
    
    public String getNombreUsuario() {
        if (usuarioLogueado == null) return "";
        
        if (usuarioLogueado instanceof Cliente) {
            Cliente c = (Cliente) usuarioLogueado;
            return c.getNombre() + " " + c.getApellido();
        } else if (usuarioLogueado instanceof Empleado) {
            return ((Empleado)usuarioLogueado).getName();
        }
        return "";
    }
    
    public String getEmailUsuario() {
        if (usuarioLogueado == null) return "";
        
        if (usuarioLogueado instanceof Cliente) {
            return ((Cliente)usuarioLogueado).getEmail();
        } else if (usuarioLogueado instanceof Empleado) {
            return ((Empleado)usuarioLogueado).getEmail();
        }
        return "";
    }
    
    public boolean isLoggedIn() {
        return usuarioLogueado != null;
    }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public Object getUsuarioLogueado() { return usuarioLogueado; }
}