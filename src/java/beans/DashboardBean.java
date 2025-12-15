package beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.BarChartModel;
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

    // ===== CONTADORES =====
    private int clientesCount;
    private int usuariosCount;
    private int habitacionesCount;
    private int reservasActivas;

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
        clienteDAO = new ClienteDAO();
        empleadoDAO = new EmpleadoDAO();
        habitacionDAO = new HabitacionDAO();
        reservaHabitacionDAO = new ReservaHabitacionDAO();

        clientesCount = clienteDAO.contarClientes();
        usuariosCount = empleadoDAO.contarEmpleados();
        habitacionesCount = habitacionDAO.contarHabitaciones();
        reservasActivas = reservaHabitacionDAO.contarReservasActivas();

        crearBarChart();
        crearDonutChart();
    }

    // ===== GRAFICA BARRAS =====
    private void crearBarChart() {
        reservasBarModel = new BarChartModel();
        ChartSeries reservas = new ChartSeries();
        reservas.setLabel("Reservas");

        List<Object[]> datos = reservaHabitacionDAO.reservasPorMes();

        for (Object[] fila : datos) {
            int mes = ((Number) fila[0]).intValue();
            int total = ((Number) fila[1]).intValue();
            reservas.set(nombreMes(mes), total);
        }

        reservasBarModel.addSeries(reservas);
        reservasBarModel.setTitle("Reservas por Mes");
        reservasBarModel.setLegendPosition("ne");
        reservasBarModel.setAnimate(true);
    }

    // ===== GRAFICA DONUT =====
    private void crearDonutChart() {
        ocupacionModel = new DonutChartModel();

        int ocupadas = habitacionDAO.contarHabitacionesOcupadas();
        int disponibles = habitacionesCount - ocupadas;

        Map<String, Number> ring = new LinkedHashMap<>();
        ring.put("Ocupadas", ocupadas);
        ring.put("Disponibles", disponibles);

        List<Map<String, Number>> data = new ArrayList<>();
        data.add(ring);

        ocupacionModel.setData(data);
        ocupacionModel.setTitle("Ocupación de Habitaciones");
        ocupacionModel.setLegendPosition("e");
    }

    private String nombreMes(int mes) {
        String[] meses = {
            "Ene","Feb","Mar","Abr","May","Jun",
            "Jul","Ago","Sep","Oct","Nov","Dic"
        };
        return meses[mes - 1];
    }

    // ===== GETTERS =====
    public int getClientesCount() { return clientesCount; }
    public int getUsuariosCount() { return usuariosCount; }
    public int getHabitacionesCount() { return habitacionesCount; }
    public int getReservasActivas() { return reservasActivas; }
    public BarChartModel getReservasBarModel() { return reservasBarModel; }
    public DonutChartModel getOcupacionModel() { return ocupacionModel; }
}
