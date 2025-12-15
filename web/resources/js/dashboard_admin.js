        function toggleSidebar() {
            const sidebar = document.querySelector('.sidebar');
            sidebar.classList.toggle('show');
            
            // Agregar backdrop
            if (sidebar.classList.contains('show')) {
                const backdrop = document.createElement('div');
                backdrop.className = 'offcanvas-backdrop fade show';
                backdrop.style.zIndex = '1040';
                backdrop.onclick = toggleSidebar;
                document.body.appendChild(backdrop);
                document.body.style.overflow = 'hidden';
            } else {
                const backdrop = document.querySelector('.offcanvas-backdrop');
                if (backdrop) {
                    backdrop.remove();
                    document.body.style.overflow = '';
                }
            }
        }
        
        // Cerrar sidebar al hacer clic en un enlace en móvil
        document.addEventListener('DOMContentLoaded', function() {
            const mobileLinks = document.querySelectorAll('.sidebar .nav-link');
            mobileLinks.forEach(link => {
                link.addEventListener('click', function() {
                    if (window.innerWidth < 992) {
                        toggleSidebar();
                    }
                });
            });
            
            // Ajustar altura de gráficas
            window.addEventListener('resize', function() {
                const charts = document.querySelectorAll('canvas');
                charts.forEach(chart => {
                    if (chart.parentElement) {
                        chart.style.height = '300px';
                    }
                });
            });
        });