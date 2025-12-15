package beans;

import dao.TipoClienteDAO;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import models.TipoCliente;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;
import utils.MessageUtil;

@ManagedBean
@ViewScoped
public class TipoClienteReporteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== FILTROS =====
    private String nombreFiltro;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    // ===== RESULTADOS =====
    private List<TipoCliente> listaReporte = new ArrayList<>();

    // ===== OPCIONES DE EXPORTACIÓN =====
    private ExcelOptions excelOpt;
    private PDFOptions pdfOpt;

    // ===== DAO =====
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    // ===== INIT =====
    public void init() {
        try {
            // Leer parámetros de la URL
            Map<String, String> params = FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestParameterMap();

            if (params.containsKey("nombre")) {
                nombreFiltro = params.get("nombre");
            }
            if (params.containsKey("desde")) {
                try {
                    fechaDesde = LocalDate.parse(params.get("desde"));
                } catch (Exception e) {}
            }
            if (params.containsKey("hasta")) {
                try {
                    fechaHasta = LocalDate.parse(params.get("hasta"));
                } catch (Exception e) {}
            }

            filtrarReporte();
            initExportOptions();

        } catch (Exception e) {
            MessageUtil.error("Error al inicializar reporte: " + e.getMessage());
        }
    }

    // ===== EXPORTACIÓN =====
    public void initExportOptions() {
        excelOpt = new ExcelOptions();
        excelOpt.setFacetBgColor("#F8F9FA");
        excelOpt.setFacetFontSize("12");
        excelOpt.setFacetFontStyle("BOLD");
        excelOpt.setCellFontSize("11");

        pdfOpt = new PDFOptions();
        pdfOpt.setFacetBgColor("#6c757d");
        pdfOpt.setFacetFontColor("#FFFFFF");
        pdfOpt.setFacetFontStyle("BOLD");
        pdfOpt.setCellFontSize("10");
    }

    public void preProcessExcel(Object document) {}
    public void preProcessPDF(Object document) {}

    // ===== FILTRO =====
    public void filtrarReporte() {
        try {
            listaReporte = tipoClienteDAO.listar(); // Traemos todos

            // Filtrar por nombre
            if (nombreFiltro != null && !nombreFiltro.trim().isEmpty()) {
                String filtro = nombreFiltro.trim().toLowerCase();
                listaReporte.removeIf(tc -> !tc.getNombreTipo().toLowerCase().contains(filtro));
            }

            // Filtrar por fecha
            if (fechaDesde != null) {
                listaReporte.removeIf(tc -> tc.getCreatedAt().toLocalDate().isBefore(fechaDesde));
            }
            if (fechaHasta != null) {
                listaReporte.removeIf(tc -> tc.getCreatedAt().toLocalDate().isAfter(fechaHasta));
            }

            if (listaReporte.isEmpty()) {
                MessageUtil.info("No se encontraron resultados con los filtros aplicados");
            } else {
                MessageUtil.info("Se encontraron " + listaReporte.size() + " tipo(s) de cliente");
            }

        } catch (Exception e) {
            MessageUtil.error("Error al filtrar reporte: " + e.getMessage());
            listaReporte = new ArrayList<>();
        }
    }

    public void limpiarFiltros() {
        nombreFiltro = null;
        fechaDesde = null;
        fechaHasta = null;
        filtrarReporte();
        MessageUtil.info("Filtros limpiados");
    }

    public String getResumenFiltros() {
        StringBuilder sb = new StringBuilder();
        if (nombreFiltro != null && !nombreFiltro.isEmpty()) sb.append("Nombre: ").append(nombreFiltro).append("; ");
        if (fechaDesde != null) sb.append("Desde: ").append(fechaDesde).append("; ");
        if (fechaHasta != null) sb.append("Hasta: ").append(fechaHasta).append("; ");
        return sb.length() > 0 ? sb.toString() : "Sin filtros";
    }

    // ===== GETTERS / SETTERS =====
    public List<TipoCliente> getListaReporte() { return listaReporte; }
    public void setListaReporte(List<TipoCliente> listaReporte) { this.listaReporte = listaReporte; }

    public String getNombreFiltro() { return nombreFiltro; }
    public void setNombreFiltro(String nombreFiltro) { this.nombreFiltro = nombreFiltro; }

    public LocalDate getFechaDesde() { return fechaDesde; }
    public void setFechaDesde(LocalDate fechaDesde) { this.fechaDesde = fechaDesde; }

    public LocalDate getFechaHasta() { return fechaHasta; }
    public void setFechaHasta(LocalDate fechaHasta) { this.fechaHasta = fechaHasta; }

    public ExcelOptions getExcelOpt() { return excelOpt; }
    public PDFOptions getPdfOpt() { return pdfOpt; }
}
