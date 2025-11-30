package entities;

import entities.enums.Rol;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

@Entity
@Table(name = "empleados")
public class Empleado extends ClaseBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    private String name;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Column(unique = true)
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    private Rol rol;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}