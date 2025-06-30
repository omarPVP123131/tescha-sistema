package tescha.prestamos.dto;

import javafx.beans.property.*;

public class PrestamoDetalleDTO {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty prestamoId = new SimpleIntegerProperty();
    private final IntegerProperty idEquipo = new SimpleIntegerProperty();
    private final IntegerProperty cantidad = new SimpleIntegerProperty();

    // Getters para las propiedades
    public IntegerProperty idProperty() {
        return id;
    }

    public IntegerProperty prestamoIdProperty() {
        return prestamoId;
    }

    public IntegerProperty idEquipoProperty() {
        return idEquipo;
    }

    public IntegerProperty cantidadProperty() {
        return cantidad;
    }

    // Getters y Setters normales
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getPrestamoId() {
        return prestamoId.get();
    }

    public void setPrestamoId(int prestamoId) {
        this.prestamoId.set(prestamoId);
    }

    public int getIdEquipo() {
        return idEquipo.get();
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo.set(idEquipo);
    }

    public int getCantidad() {
        return cantidad.get();
    }

    public void setCantidad(int cantidad) {
        this.cantidad.set(cantidad);
    }
}