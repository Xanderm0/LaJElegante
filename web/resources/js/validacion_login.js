var dominiosPermitidosLogin = ['gmail.com', 'lje.com', 'hotmail.com', 'outlook.com', 'yahoo.com'];

function validarEmailLoginTiempoReal(input) {
    var email = input.value.trim();
    var feedback = document.getElementById('emailFeedback');
    
    if (!email) {
        if (feedback) {
            feedback.style.display = 'none';
        }
        return { valid: false, message: '' };
    }
    
    var partes = email.split('@');
    var isValid = false;
    var message = '';
    
    if (partes.length !== 2) {
        message = '✗ Formato de email inválido';
    } else {
        var dominio = partes[1].toLowerCase();
        
        if (!dominiosPermitidosLogin.includes(dominio)) {
            message = '✗ Usa un email con dominio permitido';
        } else {
            isValid = true;
            message = '✓ Email válido';
        }
    }
    
    if (feedback) {
        feedback.innerHTML = message;
        feedback.className = 'feedback-message ' + (isValid ? 'feedback-success' : 'feedback-error');
        feedback.style.display = 'block';
    }
    
    return { valid: isValid, message: message };
}

function validarPasswordLoginTiempoReal(input) {
    var password = input.value;
    var feedback = document.getElementById('passwordFeedback');
    
    if (!password) {
        if (feedback) {
            feedback.style.display = 'none';
        }
        return { valid: false };
    }
    
    var isValid = password.length >= 6;
    
    if (feedback) {
        feedback.innerHTML = isValid ? '✓ Contraseña válida' : '✗ Mínimo 6 caracteres';
        feedback.className = 'feedback-message ' + (isValid ? 'feedback-success' : 'feedback-error');
        feedback.style.display = 'block';
    }
    
    return { valid: isValid };
}

function validarLoginFormulario() {
    var emailValid = validarEmailLoginTiempoReal(document.getElementById('loginForm:email'));
    var passwordValid = validarPasswordLoginTiempoReal(document.getElementById('loginForm:password'));
    
    var allValid = emailValid.valid && passwordValid.valid;
    
    if (!allValid) {
        var mensaje = 'Por favor corrige los siguientes errores:\n\n';
        
        if (!emailValid.valid) {
            mensaje += '- ' + emailValid.message.replace('✗ ', '') + '\n';
        }
        if (!passwordValid.valid) {
            mensaje += '- La contraseña debe tener al menos 6 caracteres\n';
        }
        
        if (typeof PF !== 'undefined') {
            PF('growl').show([
                {
                    summary: 'Error de validación',
                    detail: 'Por favor completa todos los campos correctamente.',
                    severity: 'error'
                }
            ]);
        } else {
            alert(mensaje);
        }
        
        if (!emailValid.valid) {
            document.getElementById('loginForm:email').focus();
        } else if (!passwordValid.valid) {
            document.getElementById('loginForm:password').focus();
        }
        
        return false;
    }
    
    mostrarCargaLogin();
    
    return true;
}

function mostrarCargaLogin() {
    var boton = document.querySelector('#loginForm button[type="submit"]');
    if (boton) {
        var textoOriginal = boton.innerHTML;
        boton.innerHTML = '<i class="pi pi-spin pi-spinner"></i> Procesando...';
        boton.disabled = true;
        
        setTimeout(function() {
            boton.innerHTML = textoOriginal;
            boton.disabled = false;
        }, 5000);
    }
}

function limpiarFeedbackLogin() {
    var feedbacks = document.querySelectorAll('.feedback-message');
    feedbacks.forEach(function(feedback) {
        feedback.style.display = 'none';
    });
}

function inicializarValidacionLogin() {
    var emailInput = document.getElementById('loginForm:email');
    var passwordInput = document.getElementById('loginForm:password');
    
    if (emailInput) {
        emailInput.addEventListener('input', function() {
            validarEmailLoginTiempoReal(this);
        });
        
        emailInput.addEventListener('blur', function() {
            validarEmailLoginTiempoReal(this);
        });
        
        emailInput.addEventListener('focus', function() {
            limpiarFeedbackLogin();
        });
    }
    
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            validarPasswordLoginTiempoReal(this);
        });
        
        passwordInput.addEventListener('blur', function() {
            validarPasswordLoginTiempoReal(this);
        });
        
        passwordInput.addEventListener('focus', function() {
            limpiarFeedbackLogin();
        });
    }
    
    if (emailInput) {
        emailInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                if (validarLoginFormulario()) {
                    // Simular clic en el botón de submit
                    var submitBtn = document.querySelector('#loginForm button[type="submit"]');
                    if (submitBtn) submitBtn.click();
                }
            }
        });
    }
    
    if (passwordInput) {
        passwordInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                if (validarLoginFormulario()) {
                    var submitBtn = document.querySelector('#loginForm button[type="submit"]');
                    if (submitBtn) submitBtn.click();
                }
            }
        });
    }
    
    console.log('Validación de login inicializada - Hotel LJE');
}

function configurarEfectosVisualesLogin() {
    var inputs = document.querySelectorAll('#loginForm .login-input');
    
    inputs.forEach(function(input) {
        input.addEventListener('focus', function() {
            this.parentElement.classList.add('focused');
        });
        
        input.addEventListener('blur', function() {
            if (!this.value) {
                this.parentElement.classList.remove('focused');
            }
        });
        
        if (input.value) {
            input.parentElement.classList.add('focused');
        }
    });
}

function inicializarLogin() {
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function() {
            inicializarValidacionLogin();
            configurarEfectosVisualesLogin();
        });
    } else {
        inicializarValidacionLogin();
        configurarEfectosVisualesLogin();
    }
}

function resetearFormularioLogin() {
    document.getElementById('loginForm:email').value = '';
    document.getElementById('loginForm:password').value = '';
    limpiarFeedbackLogin();
    
    var inputs = document.querySelectorAll('#loginForm .login-input');
    inputs.forEach(function(input) {
        input.parentElement.classList.remove('focused');
    });
    
    document.getElementById('loginForm:email').focus();
    
    console.log('Formulario de login reseteado');
}

inicializarLogin();

if (typeof window !== 'undefined') {
    window.validarLoginFormulario = validarLoginFormulario;
    window.resetearFormularioLogin = resetearFormularioLogin;
    window.inicializarLogin = inicializarLogin;
}