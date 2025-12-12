package beans;

import dao.MesaDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import models.Mesa;
import models.enums.Estado;

@ManagedBean
@ViewScoped
public class MesaBean implements Serializable {

    private Mesa mesa = new Mesa();
    private List<Mesa> listaMesas = new ArrayList<>();
    private List<Mesa> listaFiltrada;

    private final MesaDAO mesaDAO = new MesaDAO();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public MesaBean() {
        listar();
    }

   
    public void listar() {
        listaMesas = mesaDAO.listar();
        mesa = new Mesa();
    }

    public void guardar() {
        if (!validar(mesa)) return;

        mesa.prePersist();
        mesaDAO.guardar(mesa);
        listar();
    }

    public void actualizar() {
        if (!validar(mesa)) return;

        mesa.preUpdate();
        mesaDAO.actualizar(mesa);
        listar();
    }

    public void eliminar(int id) {
        Mesa mes = mesaDAO.buscar(id);
        mes.softDelete();
        mesaDAO.actualizar(mes);
        listar();
    }

    public void restaurar(int id) {
        Mesa mes = mesaDAO.buscar(id);
        mes.restore();
        mesaDAO.actualizar(mes);
        listar();
    }

    public void buscar(int id) {
        mesa = mesaDAO.buscar(id);
    }

   
    private boolean validar(Mesa m) {
        var errores = validator.validate(m);
        if (!errores.isEmpty()) {
            for (ConstraintViolation<?> er : errores) {
                System.out.println("VALIDACIÓN ERROR: " + er.getMessage());
            }
            return false;
        }
        return true;
    }

   
    public Estado[] getEstadosDisponibles() {
        return Estado.values();
    }

   

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public List<Mesa> getListaMesas() {
        return listaMesas;
    }

    public void setListaMesas(List<Mesa> listaMesas) {
        this.listaMesas = listaMesas;
    }

    public List<Mesa> getListaFiltrada() {
        return listaMesas;
    }

    public void setListaFiltrada(List<Mesa> listaFiltrada) {
        this.listaFiltrada = listaFiltrada;
    }
}