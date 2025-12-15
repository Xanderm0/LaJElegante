            // Marcar enlace activo en sidebar
            document.addEventListener('DOMContentLoaded', function() {
                // Marcar cliente como activo en sidebar
                const clienteLink = document.querySelector('a[href*="clientes"]');
                if (clienteLink) {
                    clienteLink.classList.add('active');
                }
                
                // Agregar clases a la tabla de PrimeFaces
                const table = document.querySelector('.ui-datatable');
                if (table) {
                    table.classList.add('pagination-custom');
                }
            });