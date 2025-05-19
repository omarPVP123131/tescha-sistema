package tescha.configuracion.dto;

import javafx.beans.property.*;

public class UsuarioDTO {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty telefono = new SimpleStringProperty();
    private final StringProperty departamento = new SimpleStringProperty();
    private final StringProperty rol = new SimpleStringProperty();
    private final BooleanProperty activo = new SimpleBooleanProperty();
    private final StringProperty ultimoAcceso = new SimpleStringProperty();

    // Property getters
    public IntegerProperty idProperty() { return id; }
    public StringProperty usernameProperty() { return username; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty telefonoProperty() { return telefono; }
    public StringProperty departamentoProperty() { return departamento; }
    public StringProperty rolProperty() { return rol; }
    public BooleanProperty activoProperty() { return activo; }
    public StringProperty ultimoAccesoProperty() { return ultimoAcceso; }

    // Getters y setters normales
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }

    public String getPassword() { return password.get(); }
    public void setPassword(String password) { this.password.set(password); }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }

    public String getTelefono() { return telefono.get(); }
    public void setTelefono(String telefono) { this.telefono.set(telefono); }

    public String getDepartamento() { return departamento.get(); }
    public void setDepartamento(String departamento) { this.departamento.set(departamento); }

    public String getRol() { return rol.get(); }
    public void setRol(String rol) { this.rol.set(rol); }

    public boolean isActivo() { return activo.get(); }
    public void setActivo(boolean activo) { this.activo.set(activo); }

    public String getUltimoAcceso() { return ultimoAcceso.get(); }
    public void setUltimoAcceso(String ultimoAcceso) { this.ultimoAcceso.set(ultimoAcceso); }
}