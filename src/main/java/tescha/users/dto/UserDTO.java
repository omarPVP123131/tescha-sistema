package tescha.users.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class UserDTO {
    private Integer id;
    private String username;
    private String password;
    private String nombre;
    private String telefono;
    private String departamento;
    private String rol;
    private boolean activo;
    private String ultimoAcceso;
    private String imagen;
    private String fechaRegistro;

    public UserDTO() {
        this.rol = "usuario";
        this.activo = true;
        this.fechaRegistro = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public UserDTO(Integer id, String username, String password, String nombre,
                   String telefono, String departamento, String rol, boolean activo,
                   String ultimoAcceso, String imagen, String fechaRegistro) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.telefono = telefono;
        this.departamento = departamento;
        this.rol = rol != null ? rol : "usuario";
        this.activo = activo;
        this.ultimoAcceso = ultimoAcceso;
        this.imagen = imagen;
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro :
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getRol() { return rol; }
    public void setRol(String rol) {
        if (rol == null || (!rol.equals("admin") && !rol.equals("usuario"))) {
            this.rol = "usuario";
        } else {
            this.rol = rol;
        }
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(String ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public String getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(String fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    @Override
    public String toString() {
        return nombre + " (" + username + ")";
    }
}