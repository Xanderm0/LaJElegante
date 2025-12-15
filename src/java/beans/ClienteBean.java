package beans;

import dao.ClienteDAO;
import dao.TipoClienteDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import models.Cliente;
import models.TipoCliente;
import models.enums.Estado;
import models.enums.Saludo;
import utils.MessageUtil;

@ManagedBean
@ViewScoped
public class ClienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== MODELO =====
    private Cliente cliente = new Cliente();

    // ===== AUXILIAR PARA EDIT =====
    private Integer idClienteSeleccionado;
    private boolean cargado = false;

    // ===== AUX PARA TIPO CLIENTE (SIN CONVERTER) =====
    private Integer idTipoClienteSeleccionado;

    // ===== LISTAS =====
    private List<Cliente> listaClientes = new ArrayList<>();
    private List<TipoCliente> listaTipos = new ArrayList<>();
    private List<Cliente> listaFiltro = new ArrayList<>();
    private List<Cliente> listaPapelera = new ArrayList<>();

    // ===== DAOS =====
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    // ===== NAVEGACIÓN =====
    public String crearCliente() {
        cliente = new Cliente();
        idTipoClienteSeleccionado = null;
        cargado = false;
        return "formCliente?faces-redirect=true";
    }
    
    public String editarCliente(int id) {
        this.idClienteSeleccionado = id;
        return "editarCliente?faces-redirect=true";
    }
    
    public String irALista() {
        return "listaClientes?faces-redirect=true";
    }

    // ===== INIT EDITAR =====
    public void initEditar() {
        if (!cargado && idClienteSeleccionado != null) {
            cliente = clienteDAO.buscar(idClienteSeleccionado);

            if (cliente == null) {
                MessageUtil.notFoundError("Cliente", String.valueOf(idClienteSeleccionado));
                cliente = new Cliente();
            } else {
                if (cliente.getTipoCliente() != null) {
                    idTipoClienteSeleccionado = cliente.getTipoCliente().getIdTipoCliente();
                }
            }

            cargarTiposCliente();
            cargado = true;
        }
    }

    // ===== CRUD =====
    public void listar() {
        try {
            listaClientes = clienteDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al listar clientes: " + e.getMessage());
        }
    }

    public String guardar() {
        if (!validarCliente()) return null;

        if (clienteDAO.existeEmail(cliente.getEmail(), 0)) {
            MessageUtil.duplicateError("email", cliente.getEmail());
            return null;
        }

        TipoCliente tipo = tipoClienteDAO.buscar(idTipoClienteSeleccionado);
        cliente.setTipoCliente(tipo);
        
        // Estado por defecto si no está definido
        if (cliente.getEstado() == null) {
            cliente.setEstado(Estado.ACTIVO);
        }

        clienteDAO.crear(cliente);
        MessageUtil.createSuccess("Cliente");
        
        // Redirigir a lista
        return "listaClientes?faces-redirect=true";
    }

    public String actualizar() {
        if (cliente.getIdCliente() == 0) {
            MessageUtil.validationError("Seleccione un cliente primero");
            return null;
        }

        if (!validarCliente()) return null;

        if (clienteDAO.existeEmail(cliente.getEmail(), cliente.getIdCliente())) {
            MessageUtil.duplicateError("email", cliente.getEmail());
            return null;
        }

        // Manejar nueva contraseña si se proporcionó
        if (cliente.getNuevaContraseña() != null && 
            !cliente.getNuevaContraseña().trim().isEmpty()) {
            cliente.setContraseña(cliente.getNuevaContraseña());
        }

        TipoCliente tipo = tipoClienteDAO.buscar(idTipoClienteSeleccionado);
        cliente.setTipoCliente(tipo);

        clienteDAO.actualizar(cliente);
        MessageUtil.updateSuccess("Cliente");
        
        return "listaClientes?faces-redirect=true";
    }

    public void eliminar(int id) {
        try {
            clienteDAO.eliminar(id);
            MessageUtil.deleteSuccess("Cliente");
            listar(); // Actualizar lista
        } catch (Exception e) {
            MessageUtil.error("Error al eliminar cliente: " + e.getMessage());
        }
    }

    // ===== PAPELERA =====
    public void listarPapelera() {
        try {
            listaPapelera = clienteDAO.listarEliminados();
        } catch (Exception e) {
            MessageUtil.error("Error al listar papelera: " + e.getMessage());
        }
    }

    public void restaurar(int id) {
        try {
            clienteDAO.restaurar(id);
            MessageUtil.success("Cliente restaurado correctamente");
            listarPapelera();
            listar();
        } catch (Exception e) {
            MessageUtil.error("Error al restaurar cliente: " + e.getMessage());
        }
    }

    // ===== AUX =====
    public void cargarTiposCliente() {
        try {
            listaTipos = tipoClienteDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al cargar tipos de cliente: " + e.getMessage());
        }
    }

    private boolean validarCliente() {
        boolean valido = true;
        
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            MessageUtil.validationError("El nombre es requerido");
            valido = false;
        }
        
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            MessageUtil.validationError("El email es requerido");
            valido = false;
        } else if (!cliente.getEmail().contains("@")) {
            MessageUtil.validationError("Email inválido");
            valido = false;
        }
        
        if (idTipoClienteSeleccionado == null) {
            MessageUtil.validationError("Seleccione un tipo de cliente");
            valido = false;
        }
        
        // Validar contraseña solo para nuevo cliente
        if (cliente.getIdCliente() == 0 && 
            (cliente.getContraseña() == null || cliente.getContraseña().trim().isEmpty())) {
            MessageUtil.validationError("La contraseña es requerida para nuevo cliente");
            valido = false;
        }
        
        return valido;
    }

    // ===== GETTERS / SETTERS =====
    public Cliente getCliente() {
        return cliente;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Integer getIdClienteSeleccionado() {
        return idClienteSeleccionado;
    }

    public void setIdClienteSeleccionado(Integer id) {
        this.idClienteSeleccionado = id;
    }

    public Integer getIdTipoClienteSeleccionado() {
        return idTipoClienteSeleccionado;
    }

    public void setIdTipoClienteSeleccionado(Integer idTipoClienteSeleccionado) {
        this.idTipoClienteSeleccionado = idTipoClienteSeleccionado;
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
    
    public void setListaPapelera(List<Cliente> listaPapelera) {
        this.listaPapelera = listaPapelera;
    }

    public Saludo[] getSaludos() {
        return Saludo.values();
    }

    public Estado[] getEstados() {
        return Estado.values();
    }
}