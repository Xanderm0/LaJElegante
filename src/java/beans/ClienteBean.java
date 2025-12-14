package beans;

import dao.ClienteDAO;
import dao.TipoClienteDAO;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import models.Cliente;
import models.TipoCliente;
import models.enums.Estado;
import models.enums.Saludo;
import utils.MessageUtil;

@Named
@ViewScoped
public class ClienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== MODELO =====
    private Cliente cliente = new Cliente();

    // ===== AUXILIAR PARA EDIT =====
    private Integer idClienteSeleccionado;
    private boolean cargado = false;

    // ===== LISTAS =====
    private List<Cliente> listaClientes = new ArrayList<>();
    private List<TipoCliente> listaTipos = new ArrayList<>();
    private List<Cliente> listaFiltro = new ArrayList<>();
    private List<Cliente> listaPapelera = new ArrayList<>();

    // ===== REPORTE =====
    private Estado estadoFiltro;
    private TipoCliente tipoClienteFiltro;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private List<Cliente> listaReporte = new ArrayList<>();

    // ===== DAOS =====
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    // ===== INIT =====
    public void initEditar() {
        if (!cargado && idClienteSeleccionado != null) {
            cliente = clienteDAO.buscar(idClienteSeleccionado);

            if (cliente == null) {
                MessageUtil.notFoundError("Cliente", String.valueOf(idClienteSeleccionado));
                cliente = new Cliente();
            }

            cargarTiposCliente();
            cargado = true;
        }
    }

    // ===== INIT REPORTE =====
    public void initReporte() {
        cargarTiposCliente();
        filtrarReporte();
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

        if (cliente.getNuevaContraseña() != null &&
            !cliente.getNuevaContraseña().trim().isEmpty()) {
            cliente.setContraseña(cliente.getNuevaContraseña());
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

    // ===== REPORTE =====
    public void filtrarReporte() {
        listaReporte = clienteDAO.listarReporte(
                estadoFiltro,
                tipoClienteFiltro,
                fechaDesde,
                fechaHasta
        );
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
        if (cliente.getTipoCliente() == null) {
            MessageUtil.validationError("Seleccione un tipo de cliente");
            return false;
        }
        return true;
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

    // ===== GETTERS / SETTERS =====
    public Cliente getCliente() { return cliente; }
    public Integer getIdClienteSeleccionado() { return idClienteSeleccionado; }
    public void setIdClienteSeleccionado(Integer id) { this.idClienteSeleccionado = id; }

    public List<Cliente> getListaClientes() { return listaClientes; }
    public List<TipoCliente> getListaTipos() { return listaTipos; }
    public List<Cliente> getListaFiltro() { return listaFiltro; }
    public List<Cliente> getListaPapelera() { return listaPapelera; }
    public List<Cliente> getListaReporte() { return listaReporte; }

    public Estado getEstadoFiltro() { return estadoFiltro; }
    public void setEstadoFiltro(Estado estadoFiltro) { this.estadoFiltro = estadoFiltro; }

    public TipoCliente getTipoClienteFiltro() { return tipoClienteFiltro; }
    public void setTipoClienteFiltro(TipoCliente tipoClienteFiltro) {
        this.tipoClienteFiltro = tipoClienteFiltro;
    }

    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }

    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }

    public Saludo[] getSaludos() { return Saludo.values(); }
    public Estado[] getEstados() { return Estado.values(); }
}
