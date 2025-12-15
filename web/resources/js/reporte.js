// /resources/js/reporte.js
function inicializarTooltips() {
    if (typeof bootstrap !== 'undefined' && bootstrap.Tooltip) {
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }
}

function confirmarExportacion(numRegistros, formato) {
    return confirm('¿Exportar ' + numRegistros + ' registro(s) a ' + formato + '?');
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', inicializarTooltips);