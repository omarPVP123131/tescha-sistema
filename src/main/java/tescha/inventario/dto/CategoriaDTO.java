package tescha.inventario.dto;

public class CategoriaDTO {
    private int id;
    private String nombre;
    private String descripcion;
    private String color;
    private String icono;

    public CategoriaDTO(int id, String nombre, String descripcion, String color, String icono) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
        this.icono = icono;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    @Override
    public String toString() {
        return nombre;
    }
}