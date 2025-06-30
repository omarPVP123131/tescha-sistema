package tescha.departamento.dto;

import java.time.LocalDateTime;

public class DepartamentoHistorialDTO {
    private int id;
    private int departamentoId;
    private String nombre;
    private String descripcion;
    private String estado;
    private LocalDateTime fechaCambio;
    private String usuario;
    private String accion;

    public DepartamentoHistorialDTO() {}

    public DepartamentoHistorialDTO(int id, int departamentoId, String nombre,
                                    String descripcion, String estado,
                                    LocalDateTime fechaCambio, String usuario,
                                    String accion) {
        this.id = id;
        this.departamentoId = departamentoId;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaCambio = fechaCambio;
        this.usuario = usuario;
        this.accion = accion;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(int departamentoId) { this.departamentoId = departamentoId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getAccion() { return accion; }
    public void setAccion(String accion) { this.accion = accion; }
}
