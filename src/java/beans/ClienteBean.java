package beans;

import dao.ClienteDAO;
import dao.TipoClienteDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import models.Cliente;
import models.enums.Estado;
import models.enums.Saludo;
import models.TipoCliente;
import utils.MessageUtil;

@ManagedBean
@ApplicationScoped
public class ClienteBean implements Serializable {
    private static final long serialVersionUID = 1L; // Identificador de versión para serialización (objetos a bytes)
    
    public Cliente cliente = new Cliente();
    public List<Cliente> listaClientes = new ArrayList<>();
    public List<TipoCliente> listaTipos = new ArrayList<>();
    public List<Cliente> listaFiltro = new ArrayList<>();
    public List<Cliente> listaPapelera = new ArrayList<>();
    
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();
    
    public void listar() {
        try {
            listaClientes = clienteDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar clientes: " + e.getMessage());
        }
    }
    
    public void buscar(int id) {
        cliente = clienteDAO.buscar(id);
        if (cliente == null) {
            MessageUtil.notFoundError("Cliente", String.valueOf(id));
            cliente = new Cliente();
        }
    }
    
    public void guardar() {
        if (!validarCliente()) return;
        
        if (clienteDAO.existeEmail(cliente.getEmail(), 0)) {
            MessageUtil.duplicateError("email", cliente.getEmail());
            return;
        }
        
        clienteDAO.crear(cliente);
        MessageUtil.createSuccess("Cliente");
        listar();
        cliente = new Cliente();
    }
    
    public void actualizar() {
        if (cliente.getIdCliente() == 0) {
            MessageUtil.validationError("Seleccione un cliente primero");
            return;
        }
        
        if (!validarCliente()) return;
        
        if (clienteDAO.existeEmail(cliente.getEmail(), cliente.getIdCliente())) {
            MessageUtil.duplicateError("email", cliente.getEmail());
            return;
        }
        
        clienteDAO.actualizar(cliente);
        MessageUtil.updateSuccess("Cliente");
        listar();
    }
    
    public void eliminar(int id) {
        clienteDAO.eliminar(id);
        MessageUtil.deleteSuccess("Cliente");
        listar();
    }
    
    public void listarPapelera() {
        try {
            listaPapelera = clienteDAO.listarEliminados();
        } catch (Exception e) {
            MessageUtil.error("Error al listar papelera: " + e.getMessage());
        }
    }
    
    public void restaurar(int id) {
        clienteDAO.restaurar(id);
        MessageUtil.success("Cliente restaurado correctamente");
        listarPapelera();
        listar();
    }
    
    public void cambiarEstado(int id, Estado nuevoEstado) {
        Cliente cli = clienteDAO.buscar(id);
        if (cli != null) {
            cli.setEstado(nuevoEstado);
            clienteDAO.actualizar(cli);
            MessageUtil.success("Estado cambiado a " + nuevoEstado.getValor());
            listar();
        }
    }
    
    public void listarTiposCliente() {
        try {
            listaTipos = tipoClienteDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar tipos de cliente: " + e.getMessage());
        }
    }
    
    private boolean validarCliente() {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            MessageUtil.validationError("El nombre es requerido");
            return false;
        }
        
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            MessageUtil.validationError("El email es requerido");
            return false;
        }
        
        if (cliente.getTipoCliente() == null || cliente.getTipoCliente().getIdTipoCliente() == 0) {
            MessageUtil.validationError("Seleccione un tipo de cliente");
            return false;
        }
        
        if (!cliente.getEmail().contains("@")) {
            MessageUtil.validationError("Email inválido");
            return false;
        }
        
        return true;
    }

    public void cargarTiposCliente() {
        try {
            listaTipos = tipoClienteDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al cargar tipos de cliente: " + e.getMessage());
        }
    }
    
    // Getters y Setters
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    
    public List<Cliente> getListaClientes() { return listaClientes; }
    public void setListaClientes(List<Cliente> listaClientes) { this.listaClientes = listaClientes; }
    
    public List<TipoCliente> getListaTipos() { return listaTipos; }
    public void setListaTipos(List<TipoCliente> listaTipos) { this.listaTipos = listaTipos; }
    
    public List<Cliente> getListaFiltro() { return listaFiltro; }
    public void setListaFiltro(List<Cliente> listaFiltro) { this.listaFiltro = listaFiltro; }
    
    public List<Cliente> getListaPapelera() { return listaPapelera; }
    public void setListaPapelera(List<Cliente> listaPapelera) { this.listaPapelera = listaPapelera; }
    
    public Saludo[] getSaludos() { return Saludo.values(); }
    public Estado[] getEstados() { return Estado.values(); }
}