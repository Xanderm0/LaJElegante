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
        listaClientes = clienteDAO.listar();
    }

    public void guardar() {
        if (!validarCliente()) return;

        if (clienteDAO.existeEmail(cliente.getEmail(), 0)) {
            MessageUtil.duplicateError("email", cliente.getEmail());
            return;
        }

        TipoCliente tipo = tipoClienteDAO.buscar(idTipoClienteSeleccionado);
        cliente.setTipoCliente(tipo);

        clienteDAO.crear(cliente);
        MessageUtil.createSuccess("Cliente");
        listar();

        cliente = new Cliente();
        idTipoClienteSeleccionado = null;
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

        if (cliente.getNuevaContraseña() != null &&
            !cliente.getNuevaContraseña().trim().isEmpty()) {
            cliente.setContraseña(cliente.getNuevaContraseña());
        }

        TipoCliente tipo = tipoClienteDAO.buscar(idTipoClienteSeleccionado);
        cliente.setTipoCliente(tipo);

        clienteDAO.actualizar(cliente);
        MessageUtil.updateSuccess("Cliente");
        listar();
    }

    public void eliminar(int id) {
        clienteDAO.eliminar(id);
        MessageUtil.deleteSuccess("Cliente");
        listar();
    }

    // ===== PAPELERA =====
    public void listarPapelera() {
        listaPapelera = clienteDAO.listarEliminados();
    }

    public void restaurar(int id) {
        clienteDAO.restaurar(id);
        MessageUtil.success("Cliente restaurado correctamente");
        listarPapelera();
    }

    // ===== AUX =====
    public void cargarTiposCliente() {
        listaTipos = tipoClienteDAO.listar();
    }

    private boolean validarCliente() {
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            MessageUtil.validationError("El nombre es requerido");
            return false;
        }
        if (cliente.getEmail() == null || !cliente.getEmail().contains("@")) {
            MessageUtil.validationError("Email inválido");
            return false;
        }
        if (idTipoClienteSeleccionado == null) {
            MessageUtil.validationError("Seleccione un tipo de cliente");
            return false;
        }
        return true;
    }

    // ===== GETTERS / SETTERS =====
    public Cliente getCliente() {
        return cliente;
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

    public List<TipoCliente> getListaTipos() {
        return listaTipos;
    }

    public List<Cliente> getListaFiltro() {
        return listaFiltro;
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
