package converters;

import dao.TipoClienteDAO;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import models.TipoCliente;

@FacesConverter("tipoClienteConverter")
public class TipoClienteConverter implements Converter {

    private final TipoClienteDAO tipoClienteDAO = new TipoClienteDAO();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return tipoClienteDAO.buscar(Integer.parseInt(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        TipoCliente tipo = (TipoCliente) value;
        return String.valueOf(tipo.getIdTipoCliente());
    }
}
