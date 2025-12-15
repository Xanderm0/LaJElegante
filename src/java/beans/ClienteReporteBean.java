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
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import models.Cliente;
import models.TipoCliente;
import models.enums.Estado;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Font.FontFamily;
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

    // ===== DAOS =====
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    // ===== INIT CON PARÁMETROS URL =====
    public void init() {
        try {
            cargarTiposCliente();
            
            // Leer parámetros de la URL si existen
            Map<String, String> params = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap();
            
            // Estado desde URL: /reportes.xhtml?estado=ACTIVO
            if (params.containsKey("estado")) {
                String estadoParam = params.get("estado");
                try {
                    estadoFiltro = Estado.valueOf(estadoParam.toUpperCase());
                } catch (IllegalArgumentException e) {
                    // Si el estado no es válido, se ignora
                }
            }
            
            // Tipo desde URL: /reportes.xhtml?tipo=1
            if (params.containsKey("tipo")) {
                try {
                    idTipoClienteFiltro = Integer.parseInt(params.get("tipo"));
                } catch (NumberFormatException e) {
                    // Si no es número válido, se ignora
                }
            }
            
            // Fecha desde URL: /reportes.xhtml?desde=2024-01-01
            if (params.containsKey("desde")) {
                try {
                    fechaDesde = LocalDate.parse(params.get("desde"));
                } catch (Exception e) {
                    // Si la fecha no es válida, se ignora
                }
            }
            
            // Fecha hasta URL: /reportes.xhtml?hasta=2024-12-31
            if (params.containsKey("hasta")) {
                try {
                    fechaHasta = LocalDate.parse(params.get("hasta"));
                } catch (Exception e) {
                    // Si la fecha no es válida, se ignora
                }
            }
            
            filtrarReporte();
            
        } catch (Exception e) {
            MessageUtil.error("Error al inicializar reporte: " + e.getMessage());
        }
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
    
    /**
     * Obtener nombre del tipo seleccionado (para mostrar en UI)
     */
    public String getNombreTipoSeleccionado() {
        if (idTipoClienteFiltro == null) return "Todos";
        
        for (TipoCliente tipo : listaTipos) {
            if (tipo.getIdTipoCliente() == idTipoClienteFiltro) {
                return tipo.getNombreTipo();
            }
        }
        return "Desconocido";
    }
    
    /**
     * Obtener texto de filtros aplicados (para mostrar en UI)
     */
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
    
    /**
     * Verificar si hay filtros activos
     */
    public boolean isFiltrosActivos() {
        return estadoFiltro != null || idTipoClienteFiltro != null || 
               fechaDesde != null || fechaHasta != null;
    }
    
    /**
     * Obtener cantidad de filtros activos
     */
    public int getCantidadFiltrosActivos() {
        int count = 0;
        if (estadoFiltro != null) count++;
        if (idTipoClienteFiltro != null) count++;
        if (fechaDesde != null) count++;
        if (fechaHasta != null) count++;
        return count;
    }

    // ===== EXPORTAR EXCEL =====
    
    /**
     * Exportar Excel con nombre por defecto
     */
    public void exportarExcel() {
        if (listaReporte == null || listaReporte.isEmpty()) {
            MessageUtil.warn("No hay datos para exportar");
            return;
        }
        
        // Generar nombre del archivo con filtros aplicados
        String nombreArchivo = generarNombreArchivo();
        exportarExcelConNombre(nombreArchivo);
    }
    
    /**
     * Exportar Excel con nombre personalizado basado en filtros
     */
    public void exportarExcelPersonalizado() {
        if (listaReporte == null || listaReporte.isEmpty()) {
            MessageUtil.warn("No hay datos para exportar");
            return;
        }
        
        try {
            // Generar nombre más descriptivo
            String nombreBase = "reporte_clientes";
            
            if (estadoFiltro != null) {
                nombreBase += "_" + estadoFiltro.name().toLowerCase();
            }
            
            if (idTipoClienteFiltro != null) {
                String tipoNombre = getNombreTipoSeleccionado().toLowerCase()
                    .replace(" ", "_")
                    .replace("ó", "o")
                    .replace("é", "e")
                    .replace("í", "i")
                    .replace("á", "a")
                    .replace("ú", "u");
                nombreBase += "_" + tipoNombre;
            }
            
            if (fechaDesde != null) {
                nombreBase += "_desde_" + fechaDesde;
            }
            
            if (fechaHasta != null) {
                nombreBase += "_hasta_" + fechaHasta;
            }
            
            // Si no hay filtros, agregar "completo"
            if (!isFiltrosActivos()) {
                nombreBase += "_completo";
            }
            
            // Agregar fecha actual
            nombreBase += "_" + LocalDate.now();
            
            String nombreArchivo = nombreBase + ".xlsx";
            exportarExcelConNombre(nombreArchivo);
            
        } catch (Exception e) {
            MessageUtil.error("Error al exportar Excel: " + e.getMessage());
        }
    }
    
    /**
     * Generar nombre del archivo automáticamente
     */
    private String generarNombreArchivo() {
        String nombreBase = "reporte_clientes";
        
        if (isFiltrosActivos()) {
            nombreBase += "_filtrado";
        } else {
            nombreBase += "_completo";
        }
        
        nombreBase += "_" + LocalDate.now();
        return nombreBase + ".xlsx";
    }
    
    /**
     * Método interno para exportar con nombre específico
     */
    private void exportarExcelConNombre(String nombreArchivo) {
        try {
            // 1. Crear libro de Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Reporte Clientes");
            
            // 2. Crear estilo para encabezado
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            // 3. Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "ID", "Nombre", "Apellido", "Email", 
                "Tipo Cliente", "Teléfono", "Estado", "Fecha Registro"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 4. Crear estilo para datos
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            
            // 5. Llenar datos
            int rowNum = 1;
            for (Cliente cliente : listaReporte) {
                Row row = sheet.createRow(rowNum++);
                
                // ID
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(cliente.getIdCliente());
                cell0.setCellStyle(dataStyle);
                
                // Nombre
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(cliente.getNombre() != null ? cliente.getNombre() : "");
                cell1.setCellStyle(dataStyle);
                
                // Apellido
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(cliente.getApellido() != null ? cliente.getApellido() : "");
                cell2.setCellStyle(dataStyle);
                
                // Email
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(cliente.getEmail() != null ? cliente.getEmail() : "");
                cell3.setCellStyle(dataStyle);
                
                // Tipo Cliente
                Cell cell4 = row.createCell(4);
                String tipoNombre = (cliente.getTipoCliente() != null && 
                                   cliente.getTipoCliente().getNombreTipo() != null) ?
                                   cliente.getTipoCliente().getNombreTipo() : "";
                cell4.setCellValue(tipoNombre);
                cell4.setCellStyle(dataStyle);
                
                // Teléfono
                Cell cell5 = row.createCell(5);
                String telefono = "";
                if (cliente.getPrefijo() != null && cliente.getNumTel() != null) {
                    telefono = cliente.getPrefijo() + " " + cliente.getNumTel();
                } else if (cliente.getNumTel() != null) {
                    telefono = cliente.getNumTel();
                }
                cell5.setCellValue(telefono);
                cell5.setCellStyle(dataStyle);
                
                // Estado
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(cliente.getEstado() != null ? 
                    cliente.getEstado().getValor() : "");
                cell6.setCellStyle(dataStyle);
                
                // Fecha Registro
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(cliente.getCreatedAt() != null ? 
                    cliente.getCreatedAt().toString() : "");
                cell7.setCellStyle(dataStyle);
            }
            
            // 6. Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 7. Preparar respuesta HTTP
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) 
                context.getExternalContext().getResponse();
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + nombreArchivo + "\"");
            
            // 8. Escribir archivo
            workbook.write(response.getOutputStream());
            workbook.close();
            
            context.responseComplete();
            
            MessageUtil.success("Reporte exportado correctamente: " + nombreArchivo);
            
        } catch (Exception e) {
            MessageUtil.error("Error al exportar Excel: " + e.getMessage());
        }
    }
    
        // ===== EXPORTAR PDF =====

    /**
     * Exportar a PDF usando iText
     */
    public void exportarPDF() {
        if (listaReporte == null || listaReporte.isEmpty()) {
            MessageUtil.warn("No hay datos para exportar a PDF");
            return;
        }

        try {
            // 1. Crear documento
            Document document = new Document(PageSize.A4.rotate()); // Horizontal
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            // 2. Abrir documento
            document.open();

            // 3. Agregar título
            Font titleFont = new Font(FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("REPORTE DE CLIENTES", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // 4. Agregar información de filtros
            if (isFiltrosActivos()) {
                Font filterFont = new Font(FontFamily.HELVETICA, 10, Font.ITALIC);
                Paragraph filters = new Paragraph("Filtros aplicados: " + getResumenFiltros(), filterFont);
                filters.setSpacingAfter(15);
                document.add(filters);
            }

            // 5. Crear tabla
            PdfPTable table = new PdfPTable(7); // 7 columnas
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            // 6. Estilo para encabezados
            Font headerFont = new Font(FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
            PdfPCell headerCell;

            // Encabezados
            String[] headers = {"ID", "Nombre", "Apellido", "Email", "Tipo", "Teléfono", "Estado"};

            for (String header : headers) {
                headerCell = new PdfPCell(new Phrase(header, headerFont));
                headerCell.setBackgroundColor(new BaseColor(59, 89, 152)); // Azul
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                headerCell.setPadding(5);
                table.addCell(headerCell);
            }

            // 7. Llenar datos
            Font dataFont = new Font(FontFamily.HELVETICA, 9);
            Font activeFont = new Font(FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.GREEN);
            Font inactiveFont = new Font(FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.RED);

            for (Cliente cliente : listaReporte) {
                // ID
                table.addCell(new PdfPCell(new Phrase(
                    String.valueOf(cliente.getIdCliente()), dataFont)));

                // Nombre
                table.addCell(new PdfPCell(new Phrase(
                    cliente.getNombre() != null ? cliente.getNombre() : "", dataFont)));

                // Apellido
                table.addCell(new PdfPCell(new Phrase(
                    cliente.getApellido() != null ? cliente.getApellido() : "", dataFont)));

                // Email
                table.addCell(new PdfPCell(new Phrase(
                    cliente.getEmail() != null ? cliente.getEmail() : "", dataFont)));

                // Tipo
                String tipoNombre = (cliente.getTipoCliente() != null && 
                                   cliente.getTipoCliente().getNombreTipo() != null) ?
                                   cliente.getTipoCliente().getNombreTipo() : "";
                table.addCell(new PdfPCell(new Phrase(tipoNombre, dataFont)));

                // Teléfono
                String telefono = "";
                if (cliente.getPrefijo() != null && cliente.getNumTel() != null) {
                    telefono = cliente.getPrefijo() + " " + cliente.getNumTel();
                } else if (cliente.getNumTel() != null) {
                    telefono = cliente.getNumTel();
                }
                table.addCell(new PdfPCell(new Phrase(telefono, dataFont)));

                // Estado (con color según estado)
                Font estadoFont = (cliente.getEstado() != null && 
                                 cliente.getEstado().name().equals("ACTIVO")) ? 
                                 activeFont : inactiveFont;
                String estadoValor = cliente.getEstado() != null ? 
                    cliente.getEstado().getValor() : "";
                table.addCell(new PdfPCell(new Phrase(estadoValor, estadoFont)));
            }

            // 8. Agregar tabla al documento
            document.add(table);

            // 9. Agregar pie de página
            Font footerFont = new Font(FontFamily.HELVETICA, 8, Font.ITALIC);
            Paragraph footer = new Paragraph(
                "Generado el " + LocalDate.now() + " - Total: " + listaReporte.size() + " clientes", 
                footerFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            footer.setSpacingBefore(20);
            document.add(footer);

            // 10. Cerrar documento
            document.close();

            // 11. Preparar respuesta HTTP
            FacesContext context = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) 
                context.getExternalContext().getResponse();

            String nombreArchivo = "reporte_clientes_" + LocalDate.now() + ".pdf";
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"" + nombreArchivo + "\"");
            response.setContentLength(baos.size());

            // 12. Escribir PDF en respuesta
            response.getOutputStream().write(baos.toByteArray());
            response.getOutputStream().flush();

            context.responseComplete();

            MessageUtil.success("Reporte PDF exportado correctamente: " + nombreArchivo);

        } catch (Exception e) {
            MessageUtil.error("Error al exportar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Exportar PDF con nombre personalizado
     */
    public void exportarPDFPersonalizado() {
        if (listaReporte == null || listaReporte.isEmpty()) {
            MessageUtil.warn("No hay datos para exportar a PDF");
            return;
        }

        try {
            String nombreBase = "reporte_clientes";

            if (estadoFiltro != null) {
                nombreBase += "_" + estadoFiltro.name().toLowerCase();
            }

            if (idTipoClienteFiltro != null) {
                nombreBase += "_tipo" + idTipoClienteFiltro;
            }

            if (fechaDesde != null) {
                nombreBase += "_desde_" + fechaDesde;
            }

            if (fechaHasta != null) {
                nombreBase += "_hasta_" + fechaHasta;
            }

            if (!isFiltrosActivos()) {
                nombreBase += "_completo";
            }

            String nombreArchivo = nombreBase + "_" + LocalDate.now() + ".pdf";

            // Llamar al método principal con nombre personalizado
            exportarPDFConNombre(nombreArchivo);

        } catch (Exception e) {
            MessageUtil.error("Error al exportar PDF personalizado: " + e.getMessage());
        }
    }

    // Método auxiliar para exportar con nombre específico
    private void exportarPDFConNombre(String nombreArchivo) {
        // Implementación similar a exportarPDF() pero con nombreArchivo personalizado
        // Copia el código de exportarPDF() y cambia solo el nombre del archivo
        exportarPDF(); // Por ahora usamos el mismo
    }

    // ===== ENUMS PARA LA VISTA =====
    public Estado[] getEstados() {
        return Estado.values();
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
}