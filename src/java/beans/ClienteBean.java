package beans;

import dao.ClienteDAO;
import dao.TipoClienteDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;
import javax.faces.context.FacesContext;
import models.Cliente;
import models.enums.Estado;
import models.enums.Saludo;
import models.TipoCliente;

@ManagedBean
@ApplicationScoped
public class ClienteBean implements Serializable {

    public Cliente cliente = new Cliente();
    public List<Cliente> listaClientes = new ArrayList<>();
    public List<TipoCliente> listaTipos = new ArrayList<>();
    public List<Cliente> listaFiltro = new ArrayList<>();
    public List<Cliente> listaPapelera = new ArrayList<>();
    
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();
    
    public ClienteBean() {
    }

    public void listar() {
        listaClientes = clienteDAO.listar();
        cliente = new Cliente();
    }

    public void buscar(int id) {
        cliente = clienteDAO.buscar(id);
        if (cliente == null) {
            addMessage("Error", "Cliente no encontrado");
            cliente = new Cliente();
        }
    }

    public void guardar() {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            addMessage("Error", "El nombre es requerido");
            return;
        }
        
        if (clienteDAO.existeEmail(cliente.getEmail(), 0)) {
            addMessage("Error", "El email ya está registrado");
            return;
        }
        
        clienteDAO.crear(cliente);
        addMessage("Éxito", "Cliente creado correctamente");
        listar();
    }

    public void actualizar() {
        if (cliente.getIdCliente() == 0) {
            addMessage("Error", "Seleccione un cliente primero");
            return;
        }
        
        if (clienteDAO.existeEmail(cliente.getEmail(), cliente.getIdCliente())) {
            addMessage("Error", "El email ya está registrado");
            return;
        }

        clienteDAO.actualizar(cliente);
        addMessage("Éxito", "Cliente actualizado");
        listar();
    }

    public void eliminar(int id) {
        clienteDAO.eliminar(id);
        addMessage("Éxito", "Cliente eliminado");
        listar();
    }
    
    public void listarPapelera() {
        listaPapelera = clienteDAO.listarEliminados();
    }
    
    public void restaurar(int id) {
        clienteDAO.restaurar(id); 
        addMessage("Éxito", "Cliente restaurado");
        listarPapelera();
    }
    
    public void cambiarEstado(int id, Estado nuevoEstado) {
        Cliente cli = clienteDAO.buscar(id);
        if (cli != null) {
            cli.setEstado(nuevoEstado);
            clienteDAO.actualizar(cli);
            addMessage("Éxito", "Estado cambiado a " + nuevoEstado);
            listar();
        }
    }

    public void listarTiposCliente() {
        listaTipos = tipoClienteDAO.listar();
    }

    private void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    // Getters y Setters
    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<Cliente> getListaClientes() {
        return listaClientes;
    }

    public void setListaClientes(List<Cliente> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public List<TipoCliente> getListaTipos() {
        return listaTipos;
    }

    public void setListaTipos(List<TipoCliente> listaTipos) {
        this.listaTipos = listaTipos;
    }

    public List<Cliente> getListaFiltro() {
        return listaFiltro;
    }

    public void setListaFiltro(List<Cliente> listaFiltro) {
        this.listaFiltro = listaFiltro;
    }
    
    public List<Cliente> getListaPapelera() {
        return listaPapelera;
    }

    public Saludo[] getSaludos() {
        return Saludo.values();
    }

    public Estado[] getEstados() {
        return Estado.values();
    }
}