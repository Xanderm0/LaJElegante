package beans;

import dao.MesaDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import models.Mesa;
import models.enums.Estado;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;
import utils.MessageUtil;

@ManagedBean
@ViewScoped
public class MesaReporteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Filtros
    private Estado estadoFiltro;
    private String zonaFiltro;
    private Integer capacidadMin;
    private Integer capacidadMax;

    // Resultados
    private List<Mesa> listaReporte = new ArrayList<>();
    private List<String> listaZonas = new ArrayList<>();

    // Export
    private ExcelOptions excelOpt;
    private PDFOptions pdfOpt;

    private final MesaDAO mesaDAO = new MesaDAO();

    public void init() {
        cargarZonas();
        filtrarReporte();
        initExportOptions();
    }

    public void cargarZonas() {
        listaZonas.clear();
        listaZonas.add("SALÓN PRINCIPAL");
        listaZonas.add("TERRAZA");
        listaZonas.add("SALÓN VIP");
        listaZonas.add("JARDÍN");
        listaZonas.add("PISCINA");
    }

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

    public void filtrarReporte() {
        try {
            // Usar DAO según filtros
            if (zonaFiltro != null && !zonaFiltro.isEmpty() && capacidadMin != null && capacidadMax != null) {
                listaReporte = mesaDAO.obtenerMesasPorCapacidad(capacidadMin, capacidadMax);
                listaReporte.removeIf(m -> !m.getZona().equalsIgnoreCase(zonaFiltro));
            } else if (zonaFiltro != null && !zonaFiltro.isEmpty()) {
                listaReporte = mesaDAO.obtenerMesasPorZona(zonaFiltro);
            } else if (capacidadMin != null && capacidadMax != null) {
                listaReporte = mesaDAO.obtenerMesasPorCapacidad(capacidadMin, capacidadMax);
            } else {
                listaReporte = mesaDAO.listar();
            }

            if (estadoFiltro != null) {
                listaReporte.removeIf(m -> !m.getEstado().equals(estadoFiltro));
            }

            MessageUtil.info("Se encontraron " + listaReporte.size() + " mesa(s)");
        } catch (Exception e) {
            MessageUtil.error("Error al filtrar reporte: " + e.getMessage());
            listaReporte = new ArrayList<>();
        }
    }

    public void limpiarFiltros() {
        estadoFiltro = null;
        zonaFiltro = null;
        capacidadMin = null;
        capacidadMax = null;
        filtrarReporte();
        MessageUtil.info("Filtros limpiados");
    }

    // Getters y Setters
    public List<Mesa> getListaReporte() { return listaReporte; }
    public Estado getEstadoFiltro() { return estadoFiltro; }
    public void setEstadoFiltro(Estado estadoFiltro) { this.estadoFiltro = estadoFiltro; }
    public String getZonaFiltro() { return zonaFiltro; }
    public void setZonaFiltro(String zonaFiltro) { this.zonaFiltro = zonaFiltro; }
    public Integer getCapacidadMin() { return capacidadMin; }
    public void setCapacidadMin(Integer capacidadMin) { this.capacidadMin = capacidadMin; }
    public Integer getCapacidadMax() { return capacidadMax; }
    public void setCapacidadMax(Integer capacidadMax) { this.capacidadMax = capacidadMax; }
    public List<String> getListaZonas() { return listaZonas; }
    public ExcelOptions getExcelOpt() { return excelOpt; }
    public PDFOptions getPdfOpt() { return pdfOpt; }
    public Estado[] getEstados() { return Estado.values(); }
}
