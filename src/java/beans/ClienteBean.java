package beans;

import dao.ClienteDAO;
import dao.TipoClienteDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import modelo.Cliente;
import models.enums.Estado;
import models.enums.Saludo;
import models.TipoCliente;

@Named
@ViewScoped
public class ClienteBean implements Serializable {

    private Cliente cliente = new Cliente();
    private List<Cliente> listaClientes = new ArrayList<>();
    private List<TipoCliente> listaTipos = new ArrayList<>();
    private List<Cliente> listaFiltro = new ArrayList<>();
    
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    
    public ClienteBean() {
        listar();
        listarTiposCliente();
    }

    public void listar() {
        listaClientes = clienteDAO.listar();
        cliente = new Cliente();
    }

    public void buscar(int id) {
        cliente = clienteDAO.buscar(id);
    }

    public void guardar() {
        if (!esValido(cliente)) return;

        cliente.prePersist();
        clienteDAO.guardar(cliente);
        System.out.println("Cliente guardado correctamente.");
        listar();
    }

    public void actualizar() {
        if (!esValido(cliente)) return;

        cliente.preUpdate();
        clienteDAO.actualizar(cliente);
        System.out.println("Cliente actualizado correctamente.");
        listar();
    }

    public void marcarEliminado(int id) {
        Cliente cli = clienteDAO.buscar(id);
        if (cli != null) {
            cli.softDelete();
            clienteDAO.actualizar(cli);
            System.out.println("Cliente marcado como eliminado.");
            listar();
        }
    }
    
    public void restaurar(int id) {
        Cliente cli = clienteDAO.buscar(id);
        if (cli != null) {
            cli.restore();
            clienteDAO.actualizar(cli);
            System.out.println("Cliente restaurado.");
            listar();
        }
    }

    public void listarTiposCliente() {
        listaTipos = tipoClienteDAO.listar();
    }
    
    private boolean esValido(Cliente c) {
        var errores = validator.validate(c);
        
        if (!errores.isEmpty()) {
            for (ConstraintViolation<?> error : errores) {
                System.out.println("VALIDACIÓN ERROR: " + error.getMessage());
            }
            return false;
        }
        return true;
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

    public Saludo[] getSaludos() {
        return Saludo.values(); // Ahora apunta al enum correcto
    }

    public Estado[] getEstados() {
        return Estado.values();
    }
}
