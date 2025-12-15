package beans;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DonutChartModel;

import dao.ClienteDAO;
import dao.EmpleadoDAO;
import dao.HabitacionDAO;
import dao.ReservaHabitacionDAO;

@ManagedBean(name = "dashboardBean")
@ViewScoped
public class DashboardBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===== CONTADORES PRINCIPALES =====
    private int clientesCount;
    private int usuariosCount;
    private int habitacionesCount;
    private int reservasActivas;
    
    // ===== NUEVAS MÉTRICAS =====
    private int reservasHoy;
    private int clientesNuevos30Dias;
    private double ocupacionPorcentaje;
    private double ingresosHoy;
    private String fechaActual;
    
    // ===== DATOS PARA GRÁFICAS =====
    private List<String> mesesLabels;
    private List<Integer> reservasPorMes;

    // ===== MODELOS =====
    private BarChartModel reservasBarModel;
    private DonutChartModel ocupacionModel;

    // ===== DAOs =====
    private ClienteDAO clienteDAO;
    private EmpleadoDAO empleadoDAO;
    private HabitacionDAO habitacionDAO;
    private ReservaHabitacionDAO reservaHabitacionDAO;

    @PostConstruct
    public void init() {
        // Inicializar DAOs
        clienteDAO = new ClienteDAO();
        empleadoDAO = new EmpleadoDAO();
        habitacionDAO = new HabitacionDAO();
        reservaHabitacionDAO = new ReservaHabitacionDAO();

        // Cargar todos los datos
        cargarTodosLosDatos();
        
        // Crear gráficas
        crearBarChart();
        crearDonutChart();
    }

    private void cargarTodosLosDatos() {
        // Obtener fecha actual formateada
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        fechaActual = sdf.format(new Date());

        // Cargar contadores principales
        cargarContadoresPrincipales();
        
        // Cargar nuevas métricas
        cargarMetricasAdicionales();
        
        // Preparar datos para gráficas
        prepararDatosGraficas();
    }

    private void cargarContadoresPrincipales() {
        clientesCount = clienteDAO.contarClientes();
        usuariosCount = empleadoDAO.contarEmpleados();
        habitacionesCount = habitacionDAO.contarHabitaciones();
        reservasActivas = reservaHabitacionDAO.contarReservasActivas();
    }

    private void cargarMetricasAdicionales() {
        reservasHoy = reservaHabitacionDAO.contarReservasHoy();
        clientesNuevos30Dias = clienteDAO.contarClientesNuevosUltimos30Dias();
        
        // Calcula ocupación como porcentaje
        int totalHabitaciones = habitacionesCount;
        int habitacionesOcupadas = habitacionDAO.contarHabitacionesOcupadas();
        
        if (totalHabitaciones > 0) {
            // Redondear a 1 decimal
            ocupacionPorcentaje = Math.round((habitacionesOcupadas * 100.0 / totalHabitaciones) * 10.0) / 10.0;
        } else {
            ocupacionPorcentaje = 0.0;
        }
        
        // Ingresos hoy - temporal (0.0) hasta que implementes el DAO correspondiente
        ingresosHoy = 0.0;
    }

    private void prepararDatosGraficas() {
        // Preparar etiquetas de meses
        mesesLabels = new ArrayList<>();
        String[] meses = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
        for (String mes : meses) {
            mesesLabels.add(mes);
        }
        
        // Inicializar array de reservas por mes
        reservasPorMes = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            reservasPorMes.add(0);
        }
        
        // Obtener datos reales de reservas por mes
        try {
            List<Object[]> datos = reservaHabitacionDAO.reservasPorMesAnioActual();
            for (Object[] fila : datos) {
                if (fila != null && fila.length >= 2) {
                    int mes = ((Number) fila[0]).intValue();
                    int total = ((Number) fila[1]).intValue();
                    if (mes >= 1 && mes <= 12) {
                        reservasPorMes.set(mes - 1, total);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos para gráficas: " + e.getMessage());
        }
    }

    // ===== GRAFICA BARRAS =====
    private void crearBarChart() {
        reservasBarModel = new BarChartModel();
        ChartSeries reservas = new ChartSeries();
        reservas.setLabel("Reservas");

        // Usar los datos preparados
        if (mesesLabels != null && reservasPorMes != null) {
            for (int i = 0; i < mesesLabels.size(); i++) {
                reservas.set(mesesLabels.get(i), reservasPorMes.get(i));
            }
        }

        reservasBarModel.addSeries(reservas);
        reservasBarModel.setTitle("Reservas por Mes - " + new SimpleDateFormat("yyyy").format(new Date()));
        reservasBarModel.setLegendPosition("ne");
        reservasBarModel.setAnimate(true);
        reservasBarModel.setShadow(true);
        reservasBarModel.setShowPointLabels(true);
        
        // Configurar ejes
        CategoryAxis xAxis = new CategoryAxis("Meses");
        reservasBarModel.getAxes().put(AxisType.X, xAxis);
        
        Axis yAxis = reservasBarModel.getAxis(AxisType.Y);
        yAxis.setLabel("Cantidad de Reservas");
        yAxis.setMin(0);
        
        // Configuración adicional para mejor visualización
        reservasBarModel.setSeriesColors("3a7bd5");
        reservasBarModel.setDatatipFormat("<span style=\"display:none;\">%s</span><span>%s</span>");
    }

    // ===== GRAFICA DONUT =====
 private void crearDonutChart() {
    ocupacionModel = new DonutChartModel();

    int ocupadas = habitacionDAO.contarHabitacionesOcupadas();
    int disponibles = habitacionDAO.contarHabitacionesDisponibles();

    Map<String, Number> ring = new LinkedHashMap<>();
    ring.put("Ocupadas (" + ocupadas + ")", ocupadas);
    ring.put("Disponibles (" + disponibles + ")", disponibles);

    List<Map<String, Number>> data = new ArrayList<>();
    data.add(ring);

    ocupacionModel.setData(data);
    ocupacionModel.setTitle("Ocupación de Habitaciones");
    ocupacionModel.setLegendPosition("e");
    ocupacionModel.setShowDataLabels(true);
    ocupacionModel.setSliceMargin(2);
    ocupacionModel.setShadow(false);
    
    // Personalizar colores (rojo para ocupadas, verde para disponibles)
    ocupacionModel.setSeriesColors("e74c3c, 2ecc71");
    
    // Configurar formato de etiquetas
    ocupacionModel.setDataFormat("value");
    ocupacionModel.setDataLabelFormatString("%d");
}

    // ===== GETTERS =====
    public int getClientesCount() { 
        return clientesCount; 
    }
    
    public int getUsuariosCount() { 
        return usuariosCount; 
    }
    
    public int getHabitacionesCount() { 
        return habitacionesCount; 
    }
    
    public int getReservasActivas() { 
        return reservasActivas; 
    }
    
    public int getReservasHoy() { 
        return reservasHoy; 
    }
    
    public int getClientesNuevos() { 
        return clientesNuevos30Dias; 
    }
    
    public double getOcupacionPorcentaje() { 
        return ocupacionPorcentaje; 
    }
    
    public double getIngresosHoy() { 
        return ingresosHoy; 
    }
    
    public String getFechaActual() { 
        return fechaActual; 
    }
    
    public List<String> getMesesLabels() { 
        return mesesLabels; 
    }
    
    public List<Integer> getReservasPorMes() { 
        return reservasPorMes; 
    }
    
    public BarChartModel getReservasBarModel() { 
        return reservasBarModel; 
    }
    
    public DonutChartModel getOcupacionModel() { 
        return ocupacionModel; 
    }
    
    // ===== MÉTODOS UTILITARIOS =====
    public String getFormattedIngresosHoy() {
        return String.format("$%,.2f", ingresosHoy);
    }
    
    public String getFormattedOcupacionPorcentaje() {
        return String.format("%.1f%%", ocupacionPorcentaje);
    }
    
    public String getFormattedIngresosConSimbolo() {
        if (ingresosHoy > 0) {
            return String.format("$%,.2f", ingresosHoy);
        }
        return "$0.00";
    }
    
    public String getReservasHoyFormatted() {
        if (reservasHoy > 0) {
            return String.valueOf(reservasHoy);
        }
        return "0";
    }
    
    public String getClientesNuevosFormatted() {
        if (clientesNuevos30Dias > 0) {
            return String.valueOf(clientesNuevos30Dias);
        }
        return "0";
    }
    
    // Método para obtener el color según el porcentaje de ocupación
    public String getOcupacionColorClass() {
        if (ocupacionPorcentaje >= 80) {
            return "text-danger"; // Rojo para alta ocupación
        } else if (ocupacionPorcentaje >= 50) {
            return "text-warning"; // Amarillo para ocupación media
        } else {
            return "text-success"; // Verde para baja ocupación
        }
    }
    
    // Método para obtener el icono según el porcentaje de ocupación
    public String getOcupacionIcon() {
        if (ocupacionPorcentaje >= 80) {
            return "fa-exclamation-circle"; // Alerta
        } else if (ocupacionPorcentaje >= 50) {
            return "fa-chart-line"; // Tendencia
        } else {
            return "fa-check-circle"; // OK
        }
    }
    
    // Método para verificar si hay datos para mostrar
    public boolean tieneDatos() {
        return clientesCount > 0 || usuariosCount > 0 || habitacionesCount > 0;
    }
    
    // Método para recargar datos (útil para actualizar el dashboard)
    public void recargarDatos() {
        cargarTodosLosDatos();
    }
}