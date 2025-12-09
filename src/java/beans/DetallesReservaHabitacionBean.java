package beans;

import dao.DetallesReservaHabitacionDAO;
import dao.HabitacionDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import models.DetallesReservaHabitacion;
import models.Habitacion;

@Named
@ViewScoped
public class DetallesReservaHabitacionBean implements Serializable {

    private DetallesReservaHabitacion detalle = new DetallesReservaHabitacion();
    private List<DetallesReservaHabitacion> listaDetalles = new ArrayList<>();
    private List<DetallesReservaHabitacion> listaFiltrados = new ArrayList<>();
    private List<Habitacion> listaHabitaciones = new ArrayList<>();

    private final DetallesReservaHabitacionDAO detalleDAO = new DetallesReservaHabitacionDAO();
    private final HabitacionDAO habitacionDAO = new HabitacionDAO();

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public DetallesReservaHabitacionBean() {
        listar();
        cargarHabitaciones();
    }

    public void listar() {
        listaDetalles = detalleDAO.listar();
        detalle = new DetallesReservaHabitacion();
    }

    public void buscar(int id) {
        detalle = detalleDAO.buscar(id);
    }

    public void guardar() {
        calcularPrecioTotal();
        if (!validar(detalle)) return;

        detalle.prePersist();
        detalleDAO.guardar(detalle);
        System.out.println("Detalle de reserva guardado correctamente.");
        listar();
    }

    public void actualizar() {
        calcularPrecioTotal();
        if (!validar(detalle)) return;

        detalle.preUpdate();
        detalleDAO.actualizar(detalle);
        System.out.println("Detalle de reserva actualizado correctamente.");
        listar();
    }

    public void eliminar(int id) {
        DetallesReservaHabitacion d = detalleDAO.buscar(id);
        if (d != null) {
            d.softDelete();
            detalleDAO.actualizar(d);
            System.out.println("Detalle de reserva marcado como eliminado.");
            listar();
        }
    }

    public void restaurar(int id) {
        DetallesReservaHabitacion d = detalleDAO.buscar(id);
        if (d != null) {
            d.restore();
            detalleDAO.actualizar(d);
            System.out.println("Detalle de reserva restaurado.");
            listar();
        }
    }

    private void calcularPrecioTotal() {
        double total = (detalle.getPrecioNoche() * detalle.getCantidadNoches());
        total -= detalle.getDescuentoAplicado();
        total += detalle.getRecargoAplicado();
        detalle.setPrecioReserva(total);
    }

    private boolean validar(DetallesReservaHabitacion det) {
        var errores = validator.validate(det);
        if (!errores.isEmpty()) {
            for (ConstraintViolation<?> e : errores) {
                System.out.println("ERROR VALIDACIÓN: " + e.getMessage());
            }
            return false;
        }
        return true;
    }

    public void cargarHabitaciones() {
        listaHabitaciones = habitacionDAO.listar();
    }

    // Getters y Setters
    public DetallesReservaHabitacion getDetalle() {
        return detalle;
    }

    public void setDetalle(DetallesReservaHabitacion detalle) {
        this.detalle = detalle;
    }

    public List<DetallesReservaHabitacion> getListaDetalles() {
        return listaDetalles;
    }

    public void setListaDetalles(List<DetallesReservaHabitacion> listaDetalles) {
        this.listaDetalles = listaDetalles;
    }

    public List<DetallesReservaHabitacion> getListaFiltrados() {
        return listaFiltrados;
    }

    public void setListaFiltrados(List<DetallesReservaHabitacion> listaFiltrados) {
        this.listaFiltrados = listaFiltrados;
    }

    public List<Habitacion> getListaHabitaciones() {
        return listaHabitaciones;
    }

    public void setListaHabitaciones(List<Habitacion> listaHabitaciones) {
        this.listaHabitaciones = listaHabitaciones;
    }
}
