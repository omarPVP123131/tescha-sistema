package tescha.departamento.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DepartamentoDTO {
    private int id;
    private String nombre;
    private String descripcion;
    private String estado;
    private LocalDateTime fecha = LocalDateTime.now(); // Valor por defecto

    // Constructor vacío con valores por defecto
    public DepartamentoDTO() {
        this.fecha = LocalDateTime.now(); // Valor por defecto
    }


    // Constructor con parámetros
    public DepartamentoDTO(int id, String nombre, String descripcion, String estado, LocalDateTime fecha) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fecha = (fecha != null) ? fecha : LocalDateTime.now();
    }



    // Método adicional para manejo de fechas
    public LocalDate getFechaAsLocalDate() {
        return getFecha().toLocalDate();
    }

    public void setFechaFromLocalDate(LocalDate fecha) {
        this.fecha = (fecha != null) ? fecha.atStartOfDay() : LocalDateTime.now();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha() {
        return (fecha != null) ? fecha : LocalDateTime.now();
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = (fecha != null) ? fecha : LocalDateTime.now();
    }


    @Override
    public String toString() {
        return nombre;
    }
}
