package tescha.prestamos.dto;

import java.time.LocalDate;
import javafx.beans.property.*;

public class PrestamoDTO {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final ObjectProperty<LocalDate> fechaPrestamo = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> fechaDevolucion = new SimpleObjectProperty<>();
    private final StringProperty solicitante = new SimpleStringProperty();

    private final BooleanProperty devuelto = new SimpleBooleanProperty();
    private final ObjectProperty<LocalDate> fechaDevuelto = new SimpleObjectProperty<>();
    private final StringProperty comentarios = new SimpleStringProperty();

    // Getters para las propiedades
    public IntegerProperty idProperty() {
        return id;
    }

    public ObjectProperty<LocalDate> fechaPrestamoProperty() {
        return fechaPrestamo;
    }

    public ObjectProperty<LocalDate> fechaDevolucionProperty() {
        return fechaDevolucion;
    }

    public BooleanProperty devueltoProperty() {
        return devuelto;
    }

    // Getters y Setters normales
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo.get();
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo.set(fechaPrestamo);
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion.get();
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion.set(fechaDevolucion);
    }
    // Cambiar getters/setters
    public String getSolicitante() { return solicitante.get(); }
    public void setSolicitante(String solicitante) { this.solicitante.set(solicitante); }
    public StringProperty solicitanteProperty() { return solicitante; }


    public boolean isDevuelto() {
        return devuelto.get();
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto.set(devuelto);
    }

    public LocalDate getFechaDevuelto() {
        return fechaDevuelto.get();
    }

    public void setFechaDevuelto(LocalDate fechaDevuelto) {
        this.fechaDevuelto.set(fechaDevuelto);
    }

    public String getComentarios() {
        return comentarios.get();
    }

    public void setComentarios(String comentarios) {
        this.comentarios.set(comentarios);
    }
}