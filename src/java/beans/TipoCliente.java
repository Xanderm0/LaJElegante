package beans;

import dao.TipoClienteDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import models.TipoCliente;

@ManagedBean
@ViewScoped
public class TipoClienteBean implements Serializable {

    private TipoCliente tipoCliente = new TipoCliente();
    private List<TipoCliente> listaTipoClientes = new ArrayList<>();
    private List<TipoCliente> listaFiltrada;

    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public TipoClienteBean() {
        listar();
    }

  
    public void listar() {
        listaTipoClientes = tipoClienteDAO.listar();
        tipoCliente = new TipoCliente();
    }

    public void guardar() {
        if (!validar(tipoCliente)) return;

        tipoCliente.prePersist();
        tipoClienteDAO.guardar(tipoCliente);
        listar();
    }

    public void actualizar() {
        if (!validar(tipoCliente)) return;

        tipoCliente.preUpdate();
        tipoClienteDAO.actualizar(tipoCliente);
        listar();
    }

    public void eliminar(int id) {
        TipoCliente tc = tipoClienteDAO.buscar(id);
        tc.softDelete();
        tipoClienteDAO.actualizar(tc);
        listar();
    }

    public void restaurar(int id) {
        TipoCliente tc = tipoClienteDAO.buscar(id);
        tc.restore();
        tipoClienteDAO.actualizar(tc);
        listar();
    }

    public void buscar(int id) {
        tipoCliente = tipoClienteDAO.buscar(id);
    }

    
    private boolean validar(TipoCliente t) {
        var errores = validator.validate(t);

        if (!errores.isEmpty()) {
            for (ConstraintViolation<?> er : errores) {
                System.out.println("VALIDACIÓN ERROR: " + er.getMessage());
            }
            return false;
        }

        if (t.getNombreTipo() == null || t.getNombreTipo().trim().isEmpty()) {
            System.out.println("VALIDACIÓN ERROR: El nombre del tipo de cliente es obligatorio");
            return false;
        }

        if (t.getNombreTipo().length() < 3) {
            System.out.println("VALIDACIÓN ERROR: El nombre debe tener al menos 3 caracteres");
            return false;
        }

        return true;
    }

   

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public List<TipoCliente> getListaTipoClientes() {
        return listaTipoClientes;
    }

    public void setListaTipoClientes(List<TipoCliente> listaTipoClientes) {
        this.listaTipoClientes = listaTipoClientes;
    }

    public List<TipoCliente> getListaFiltrada() {
        return listaFiltrada;
    }

    public void setListaFiltrada(List<TipoCliente> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
    }
}