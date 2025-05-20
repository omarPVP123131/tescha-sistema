package tescha.inventario.dto;

import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Base64;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import javax.imageio.ImageIO;

public class EquipoDTO extends RecursiveTreeObject<EquipoDTO> {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty categoria = new SimpleStringProperty();
    private final StringProperty subcategoria = new SimpleStringProperty();
    private final IntegerProperty cantidad = new SimpleIntegerProperty();
    private final IntegerProperty cantidadMinima = new SimpleIntegerProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty ubicacion = new SimpleStringProperty();
    private final StringProperty numeroSerie = new SimpleStringProperty();
    private final StringProperty marca = new SimpleStringProperty();
    private final StringProperty modelo = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> fechaAdquisicion = new SimpleObjectProperty<>();
    private final DoubleProperty costoAdquisicion = new SimpleDoubleProperty();
    private final StringProperty proveedor = new SimpleStringProperty();
    private final StringProperty garantia = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> vencimientoGarantia = new SimpleObjectProperty<>();
    private final StringProperty mantenimientoProgramado = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> ultimoMantenimiento = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> proximoMantenimiento = new SimpleObjectProperty<>();
    private final StringProperty notas = new SimpleStringProperty();
    private final StringProperty imagen = new SimpleStringProperty();
    private final ListProperty<String> imagenes = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final StringProperty qrcode = new SimpleStringProperty();

    // Properties
    public IntegerProperty idProperty() { return id; }
    public StringProperty nombreProperty() { return nombre; }
    public StringProperty categoriaProperty() { return categoria; }
    public StringProperty subcategoriaProperty() { return subcategoria; }
    public IntegerProperty cantidadProperty() { return cantidad; }
    public IntegerProperty cantidadMinimaProperty() { return cantidadMinima; }
    public StringProperty statusProperty() { return status; }
    public StringProperty ubicacionProperty() { return ubicacion; }
    public StringProperty numeroSerieProperty() { return numeroSerie; }
    public StringProperty marcaProperty() { return marca; }
    public StringProperty modeloProperty() { return modelo; }
    public ObjectProperty<LocalDate> fechaAdquisicionProperty() { return fechaAdquisicion; }
    public DoubleProperty costoAdquisicionProperty() { return costoAdquisicion; }
    public StringProperty proveedorProperty() { return proveedor; }
    public StringProperty garantiaProperty() { return garantia; }
    public ObjectProperty<LocalDate> vencimientoGarantiaProperty() { return vencimientoGarantia; }
    public StringProperty mantenimientoProgramadoProperty() { return mantenimientoProgramado; }
    public ObjectProperty<LocalDate> ultimoMantenimientoProperty() { return ultimoMantenimiento; }
    public ObjectProperty<LocalDate> proximoMantenimientoProperty() { return proximoMantenimiento; }
    public StringProperty notasProperty() { return notas; }
    public StringProperty imagenProperty() { return imagen; }
    public ListProperty<String> imagenesProperty() { return imagenes; }
    public StringProperty qrcodeProperty() { return qrcode; }

    // Getters y Setters
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }

    public String getCategoria() { return categoria.get(); }
    public void setCategoria(String categoria) { this.categoria.set(categoria); }

    public String getSubcategoria() { return subcategoria.get(); }
    public void setSubcategoria(String subcategoria) { this.subcategoria.set(subcategoria); }

    public int getCantidad() { return cantidad.get(); }
    public void setCantidad(int cantidad) { this.cantidad.set(cantidad); }

    public int getCantidadMinima() { return cantidadMinima.get(); }
    public void setCantidadMinima(int cantidadMinima) { this.cantidadMinima.set(cantidadMinima); }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    public String getUbicacion() { return ubicacion.get(); }
    public void setUbicacion(String ubicacion) { this.ubicacion.set(ubicacion); }

    public String getNumeroSerie() { return numeroSerie.get(); }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie.set(numeroSerie); }

    public String getMarca() { return marca.get(); }
    public void setMarca(String marca) { this.marca.set(marca); }

    public String getModelo() { return modelo.get(); }
    public void setModelo(String modelo) { this.modelo.set(modelo); }

    public LocalDate getFechaAdquisicion() { return fechaAdquisicion.get(); }
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) { this.fechaAdquisicion.set(fechaAdquisicion); }

    public double getCostoAdquisicion() { return costoAdquisicion.get(); }
    public void setCostoAdquisicion(double costoAdquisicion) { this.costoAdquisicion.set(costoAdquisicion); }

    public String getProveedor() { return proveedor.get(); }
    public void setProveedor(String proveedor) { this.proveedor.set(proveedor); }

    public String getGarantia() { return garantia.get(); }
    public void setGarantia(String garantia) { this.garantia.set(garantia); }

    public LocalDate getVencimientoGarantia() { return vencimientoGarantia.get(); }
    public void setVencimientoGarantia(LocalDate vencimientoGarantia) { this.vencimientoGarantia.set(vencimientoGarantia); }

    public String getMantenimientoProgramado() { return mantenimientoProgramado.get(); }
    public void setMantenimientoProgramado(String mantenimientoProgramado) { this.mantenimientoProgramado.set(mantenimientoProgramado); }

    public LocalDate getUltimoMantenimiento() { return ultimoMantenimiento.get(); }
    public void setUltimoMantenimiento(LocalDate ultimoMantenimiento) { this.ultimoMantenimiento.set(ultimoMantenimiento); }

    public LocalDate getProximoMantenimiento() { return proximoMantenimiento.get(); }
    public void setProximoMantenimiento(LocalDate proximoMantenimiento) { this.proximoMantenimiento.set(proximoMantenimiento); }

    public String getNotas() { return notas.get(); }
    public void setNotas(String notas) { this.notas.set(notas); }

    public String getImagen() { return imagen.get(); }
    public void setImagen(String imagen) { this.imagen.set(imagen); }

    public List<String> getImagenes() { return imagenes.get(); }
    public void setImagenes(List<String> imagenes) { this.imagenes.setAll(imagenes); }

    public String getQrcode() { return qrcode.get(); }
    public void setQrcode(String qrcode) { this.qrcode.set(qrcode); }

    /**
     * Genera un código de barras con los datos clave del producto en formato legible por pistola
     * Estructura: ID|NOMBRE|CATEGORIA|SERIE (optimizado para lectura y procesamiento)
     */
    public void generarCodigoBarrasDinamico() {
        String qrData = String.format("%d|%s|%s|%s",
                getId(),
                getNombre().substring(0, Math.min(15, getNombre().length())),
                getCategoria(),
                getNumeroSerie()
        );

        try {
            QRCodeWriter qrWriter = new QRCodeWriter();

            // Parámetros de configuración
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M); // Nivel medio de corrección
            hints.put(EncodeHintType.MARGIN, 1); // Márgenes mínimos

            // Tamaño fijo para un QR cuadrado (por ejemplo 300x300px)
            int size = 300;
            BitMatrix bitMatrix = qrWriter.encode(qrData, BarcodeFormat.QR_CODE, size, size, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            String base64Qr = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            setQrcode(base64Qr);

        } catch (Exception e) {
            e.printStackTrace();
            setQrcode(null);
        }
    }

    /**
     * Devuelve un Image de JavaFX con el código de barras, generándolo
     * si aún no existe.
     */
    public Image getBarcodeImage() {
        if (getQrcode() == null || getQrcode().isEmpty()) {
            generarCodigoBarrasDinamico();
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(getQrcode());
            ByteArrayInputStream is = new ByteArrayInputStream(decoded);
            return new Image(is);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new Image(
                    getClass().getResourceAsStream("/images/placeholder.png")
            );
        }
    }


    public boolean isStockBajo() {
        return getCantidad() <= getCantidadMinima();
    }
}