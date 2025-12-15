package beans;

import dao.ClienteDAO;
import dao.TipoClienteDAO;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import models.Cliente;
import models.TipoCliente;
import models.enums.Estado;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;
import utils.MessageUtil;

@ManagedBean
@ViewScoped
public class ClienteReporteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== FILTROS =====
    private Estado estadoFiltro;
    private Integer idTipoClienteFiltro;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    // ===== RESULTADOS =====
    private List<Cliente> listaReporte = new ArrayList<>();
    private List<TipoCliente> listaTipos = new ArrayList<>();

    // ===== OPCIONES DE EXPORTACIÓN =====
    private ExcelOptions excelOpt;
    private PDFOptions pdfOpt;

    // ===== DAOS =====
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    // ===== INIT =====
    public void init() {
        try {
            cargarTiposCliente();
            
            // Leer parámetros de la URL
            Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
            
            if (params.containsKey("estado")) {
                String estadoParam = params.get("estado");
                try {
                    estadoFiltro = Estado.valueOf(estadoParam.toUpperCase());
                } catch (IllegalArgumentException e) {}
            }
            
            if (params.containsKey("tipo")) {
                try {
                    idTipoClienteFiltro = Integer.parseInt(params.get("tipo"));
                } catch (NumberFormatException e) {}
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
            
            // Inicializar opciones de exportación
            initExportOptions();
            
        } catch (Exception e) {
            MessageUtil.error("Error al inicializar reporte: " + e.getMessage());
        }
    }
    
    // ===== INICIALIZAR OPCIONES DE EXPORTACIÓN =====
    public void initExportOptions() {
        // Opciones para Excel
        excelOpt = new ExcelOptions();
        excelOpt.setFacetBgColor("#F8F9FA");
        excelOpt.setFacetFontSize("12");
        excelOpt.setFacetFontStyle("BOLD");
        excelOpt.setCellFontSize("11");
        
        // Opciones para PDF
        pdfOpt = new PDFOptions();
        pdfOpt.setFacetBgColor("#6c757d");
        pdfOpt.setFacetFontColor("#FFFFFF");
        pdfOpt.setFacetFontStyle("BOLD");
        pdfOpt.setCellFontSize("10");
    }
    
    // ===== PRE-PROCESADORES PARA EXPORTACIÓN =====
    
    /**
     * Pre-procesar Excel (agregar cabecera personalizada)
     */
    public void preProcessExcel(Object document) {
        // Puedes personalizar el documento Excel aquí si necesitas
        // Por ejemplo, agregar título, fecha, etc.
    }
    
    /**
     * Pre-procesar PDF (agregar cabecera personalizada)
     */
    public void preProcessPDF(Object document) {
        // Puedes personalizar el documento PDF aquí si necesitas
        // Por ejemplo, agregar título, fecha, etc.
    }
    
    // ===== CARGAR TIPOS =====
    public void cargarTiposCliente() {
        try {
            listaTipos = tipoClienteDAO.listar();
        } catch (Exception e) {
            MessageUtil.error("Error al cargar tipos de cliente: " + e.getMessage());
        }
    }

    // ===== FILTRAR =====
    public void filtrarReporte() {
        try {
            listaReporte = clienteDAO.listarReporte(
                estadoFiltro,
                idTipoClienteFiltro,
                fechaDesde,
                fechaHasta
            );

            if (listaReporte == null || listaReporte.isEmpty()) {
                MessageUtil.info("No se encontraron resultados con los filtros aplicados");
            } else {
                MessageUtil.info("Se encontraron " + listaReporte.size() + " cliente(s)");
            }
        } catch (Exception e) {
            MessageUtil.error("Error al filtrar reporte: " + e.getMessage());
            listaReporte = new ArrayList<>();
        }
    }
    
    // ===== LIMPIAR FILTROS =====
    public void limpiarFiltros() {
        estadoFiltro = null;
        idTipoClienteFiltro = null;
        fechaDesde = null;
        fechaHasta = null;
        filtrarReporte();
        MessageUtil.info("Filtros limpiados");
    }
    
    // ===== MÉTODOS PARA UI =====
    
    public String getNombreTipoSeleccionado() {
        if (idTipoClienteFiltro == null) return "Todos";
        
        for (TipoCliente tipo : listaTipos) {
            if (tipo.getIdTipoCliente() == idTipoClienteFiltro) {
                return tipo.getNombreTipo();
            }
        }
        return "Desconocido";
    }
    
    public String getResumenFiltros() {
        StringBuilder sb = new StringBuilder();
        
        if (estadoFiltro != null) {
            sb.append("Estado: ").append(estadoFiltro.getValor()).append("; ");
        }
        
        if (idTipoClienteFiltro != null) {
            sb.append("Tipo: ").append(getNombreTipoSeleccionado()).append("; ");
        }
        
        if (fechaDesde != null) {
            sb.append("Desde: ").append(fechaDesde).append("; ");
        }
        
        if (fechaHasta != null) {
            sb.append("Hasta: ").append(fechaHasta).append("; ");
        }
        
        return sb.length() > 0 ? sb.toString() : "Sin filtros";
    }
    
    public boolean isFiltrosActivos() {
        return estadoFiltro != null || idTipoClienteFiltro != null || 
               fechaDesde != null || fechaHasta != null;
    }

    // ===== GETTERS / SETTERS =====
    public List<Cliente> getListaReporte() {
        return listaReporte;
    }
    
    public void setListaReporte(List<Cliente> listaReporte) {
        this.listaReporte = listaReporte;
    }

    public List<TipoCliente> getListaTipos() {
        return listaTipos;
    }
    
    public void setListaTipos(List<TipoCliente> listaTipos) {
        this.listaTipos = listaTipos;
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
    
    public ExcelOptions getExcelOpt() {
        return excelOpt;
    }
    
    public PDFOptions getPdfOpt() {
        return pdfOpt;
    }
    
    public Estado[] getEstados() {
        return Estado.values();
    }
}