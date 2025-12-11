package utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MessageUtil {
    // MÉTODOS POLIMÓRFICOS BASE
    public static void show(FacesMessage.Severity severity, String titulo, String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(severity, titulo, mensaje));
    }
    
    public static void show(FacesMessage.Severity severity, String mensaje) {
        String titulo = getDefaultTitle(severity);
        show(severity, titulo, mensaje);
    }
    
    // MÉTODOS CONVENCIONALES
    // ERROR - Rojo
    public static void error(String titulo, String mensaje) {
        show(FacesMessage.SEVERITY_ERROR, titulo, mensaje);
    }
    
    public static void error(String mensaje) {
        show(FacesMessage.SEVERITY_ERROR, mensaje);
    }
    
    // INFO - Azul/Verde
    public static void info(String titulo, String mensaje) {
        show(FacesMessage.SEVERITY_INFO, titulo, mensaje);
    }
    
    public static void info(String mensaje) {
        show(FacesMessage.SEVERITY_INFO, mensaje);
    }
    
    // WARN - Amarillo/Naranja
    public static void warn(String titulo, String mensaje) {
        show(FacesMessage.SEVERITY_WARN, titulo, mensaje);
    }
    
    public static void warn(String mensaje) {
        show(FacesMessage.SEVERITY_WARN, mensaje);
    }
    
    // FATAL - Rojo oscuro
    public static void fatal(String titulo, String mensaje) {
        show(FacesMessage.SEVERITY_FATAL, titulo, mensaje);
    }
    
    public static void fatal(String mensaje) {
        show(FacesMessage.SEVERITY_FATAL, mensaje);
    }

    // MÉTODOS DE DOMINIO ESPECÍFICO
    public static void success(String mensaje) {
        info("Éxito", mensaje);
    }
    
    public static void validationError(String mensaje) {
        error("Error de validación", mensaje);
    }
    
    public static void loginError() {
        error("Error de autenticación", 
            "Correo y/o contraseña incorrectos. Verifica tus credenciales.");
    }
    
    public static void loginSuccess(String nombre) {
        success("¡Bienvenido " + nombre + "!");
    }
    
    public static void logoutSuccess() {
        info("Sesión cerrada", "Has cerrado sesión correctamente.");
    }
    
    public static void duplicateError(String campo, String valor) {
        error("Registro duplicado", 
            "El " + campo + " '" + valor + "' ya existe en el sistema.");
    }
    
    public static void notFoundError(String elemento, String id) {
        error("No encontrado", 
            elemento + " con ID " + id + " no existe.");
    }
    
    public static void deleteWarning(String elemento) {
        warn("Confirmar eliminación", 
            "¿Estás seguro de eliminar " + elemento + "? Esta acción no se puede deshacer.");
    }

    public static void requiredFieldsWarning() {
        warn("Campos obligatorios", 
            "Por favor, completa todos los campos marcados como requeridos (*).");
    }
    
    public static void createSuccess(String elemento) {
        success(elemento + " creado correctamente.");
    }
    
    public static void updateSuccess(String elemento) {
        success(elemento + " actualizado correctamente.");
    }
    
    public static void deleteSuccess(String elemento) {
        success(elemento + " eliminado correctamente.");
    }
    
    public static void saveSuccess(String elemento) {
        success(elemento + " guardado correctamente.");
    }
    
    // Error al crear/actualizar/eliminar
    public static void createError(String elemento) {
        error("Error al crear", 
            "No se pudo crear " + elemento + ". Intenta nuevamente.");
    }
    
    public static void updateError(String elemento) {
        error("Error al actualizar", 
            "No se pudo actualizar " + elemento + ". Intenta nuevamente.");
    }
    
    public static void deleteError(String elemento) {
        error("Error al eliminar", 
            "No se pudo eliminar " + elemento + ". Intenta nuevamente.");
    }
    
    // MÉTODO PRIVADO AUXILIAR
    // Obtiene título por defecto según la severidad
    private static String getDefaultTitle(FacesMessage.Severity severity) {
        if (severity.equals(FacesMessage.SEVERITY_ERROR)) {
            return "Error";
        } else if (severity.equals(FacesMessage.SEVERITY_INFO)) {
            return "Información";
        } else if (severity.equals(FacesMessage.SEVERITY_WARN)) {
            return "Advertencia";
        } else if (severity.equals(FacesMessage.SEVERITY_FATAL)) {
            return "Error Crítico";
        }
        return "Mensaje";
    }
}