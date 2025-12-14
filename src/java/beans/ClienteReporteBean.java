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
import utils.MessageUtil;

@Named
@ViewScoped
public class ClienteReporteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== FILTROS =====
    private Estado estadoFiltro;
    private Integer idTipoClienteFiltro;   // 🔹 YA NO ES TipoCliente
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    // ===== RESULTADOS =====
    private List<Cliente> listaReporte = new ArrayList<>();
    private List<TipoCliente> listaTipos = new ArrayList<>();

    // ===== DAOS =====
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    // ===== INIT =====
    public void init() {
        listaTipos = tipoClienteDAO.listar();
        filtrarReporte();
    }

    // ===== FILTRAR =====
    public void filtrarReporte() {
        listaReporte = clienteDAO.listarReporte(
            estadoFiltro,
            idTipoClienteFiltro,   // 🔹 SE PASA EL ID
            fechaDesde,
            fechaHasta
        );

        if (listaReporte == null || listaReporte.isEmpty()) {
            MessageUtil.info("No se encontraron resultados con los filtros aplicados");
        }
    }

    // ===== EXPORTAR =====
    public void exportarExcel() {
        if (listaReporte == null || listaReporte.isEmpty()) {
            MessageUtil.warn("No hay datos para exportar");
            return;
        }

        // exportación real si luego la haces manual
    }

    // ===== ENUMS PARA LA VISTA =====
    public Estado[] getEstados() {
        return Estado.values();
    }

    // ===== GETTERS / SETTERS =====
    public List<Cliente> getListaReporte() {
        return listaReporte;
    }

    public List<TipoCliente> getListaTipos() {
        return listaTipos;
    }

    public Estado getEstadoFiltro() {
        return estadoFiltro;
    }

    public void setEstadoFiltro(Estado estadoFiltro) {
        this.estadoFiltro = estadoFiltro;
    }

    public Integer getIdTipoClienteFiltro() {
        return idTipoClienteFiltro;
    }

    public void setIdTipoClienteFiltro(Integer idTipoClienteFiltro) {
        this.idTipoClienteFiltro = idTipoClienteFiltro;
    }

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
}
