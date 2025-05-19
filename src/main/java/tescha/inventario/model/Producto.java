package tescha.inventario.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Producto {
    private final IntegerProperty idProducto;
    private final StringProperty codigo;
    private final StringProperty nombre;
    private final StringProperty descripcion;
    private final IntegerProperty idProveedor;
    private final StringProperty nombreProveedor;
    private final IntegerProperty stock;
    private final IntegerProperty stockMinimo;
    private final DoubleProperty precioCompra;
    private final DoubleProperty precioVenta;
    private final StringProperty categoria;
    private final StringProperty unidadMedida;
    private final DoubleProperty iva;
    private final DoubleProperty ieps;
    private final StringProperty fechaCreacion;
    private final StringProperty fechaActualizacion;
    private final IntegerProperty activo;

    public Producto() {
        this.idProducto = new SimpleIntegerProperty(0);
        this.codigo = new SimpleStringProperty("");
        this.nombre = new SimpleStringProperty("");
        this.descripcion = new SimpleStringProperty("");
        this.idProveedor = new SimpleIntegerProperty(0);
        this.nombreProveedor = new SimpleStringProperty("");
        this.stock = new SimpleIntegerProperty(0);
        this.stockMinimo = new SimpleIntegerProperty(5);
        this.precioCompra = new SimpleDoubleProperty(0.0);
        this.precioVenta = new SimpleDoubleProperty(0.0);
        this.categoria = new SimpleStringProperty("");
        this.unidadMedida = new SimpleStringProperty("PIEZA");
        this.iva = new SimpleDoubleProperty(0.16);
        this.ieps = new SimpleDoubleProperty(0.0);
        this.fechaCreacion = new SimpleStringProperty("");
        this.fechaActualizacion = new SimpleStringProperty("");
        this.activo = new SimpleIntegerProperty(1);
    }

    public Producto(int idProducto, String codigo, String nombre, String descripcion,
                    int idProveedor, String nombreProveedor, int stock, int stockMinimo,
                    double precioCompra, double precioVenta, String categoria,
                    String unidadMedida, double iva, double ieps,
                    String fechaCreacion, String fechaActualizacion, int activo) {
        this.idProducto = new SimpleIntegerProperty(idProducto);
        this.codigo = new SimpleStringProperty(codigo);
        this.nombre = new SimpleStringProperty(nombre);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.idProveedor = new SimpleIntegerProperty(idProveedor);
        this.nombreProveedor = new SimpleStringProperty(nombreProveedor);
        this.stock = new SimpleIntegerProperty(stock);
        this.stockMinimo = new SimpleIntegerProperty(stockMinimo);
        this.precioCompra = new SimpleDoubleProperty(precioCompra);
        this.precioVenta = new SimpleDoubleProperty(precioVenta);
        this.categoria = new SimpleStringProperty(categoria);
        this.unidadMedida = new SimpleStringProperty(unidadMedida);
        this.iva = new SimpleDoubleProperty(iva);
        this.ieps = new SimpleDoubleProperty(ieps);
        this.fechaCreacion = new SimpleStringProperty(fechaCreacion);
        this.fechaActualizacion = new SimpleStringProperty(fechaActualizacion);
        this.activo = new SimpleIntegerProperty(activo);
    }

    // Getters y setters para todas las propiedades
    public int getIdProducto() {
        return idProducto.get();
    }

    public IntegerProperty idProductoProperty() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto.set(idProducto);
    }

    public String getCodigo() {
        return codigo.get();
    }

    public StringProperty codigoProperty() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo.set(codigo);
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    public int getIdProveedor() {
        return idProveedor.get();
    }

    public IntegerProperty idProveedorProperty() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor.set(idProveedor);
    }

    public String getNombreProveedor() {
        return nombreProveedor.get();
    }

    public StringProperty nombreProveedorProperty() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor.set(nombreProveedor);
    }

    public int getStock() {
        return stock.get();
    }

    public IntegerProperty stockProperty() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock.set(stock);
    }

    public int getStockMinimo() {
        return stockMinimo.get();
    }

    public IntegerProperty stockMinimoProperty() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo.set(stockMinimo);
    }

    public double getPrecioCompra() {
        return precioCompra.get();
    }

    public DoubleProperty precioCompraProperty() {
        return precioCompra;
    }

    public void setPrecioCompra(double precioCompra) {
        this.precioCompra.set(precioCompra);
    }

    public double getPrecioVenta() {
        return precioVenta.get();
    }

    public DoubleProperty precioVentaProperty() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta.set(precioVenta);
    }

    public String getCategoria() {
        return categoria.get();
    }

    public StringProperty categoriaProperty() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria.set(categoria);
    }

    public String getUnidadMedida() {
        return unidadMedida.get();
    }

    public StringProperty unidadMedidaProperty() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida.set(unidadMedida);
    }

    public double getIva() {
        return iva.get();
    }

    public DoubleProperty ivaProperty() {
        return iva;
    }

    public void setIva(double iva) {
        this.iva.set(iva);
    }

    public double getIeps() {
        return ieps.get();
    }

    public DoubleProperty iepsProperty() {
        return ieps;
    }

    public void setIeps(double ieps) {
        this.ieps.set(ieps);
    }

    public String getFechaCreacion() {
        return fechaCreacion.get();
    }

    public StringProperty fechaCreacionProperty() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion.set(fechaCreacion);
    }

    public String getFechaActualizacion() {
        return fechaActualizacion.get();
    }

    public StringProperty fechaActualizacionProperty() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(String fechaActualizacion) {
        this.fechaActualizacion.set(fechaActualizacion);
    }

    public int getActivo() {
        return activo.get();
    }

    public IntegerProperty activoProperty() {
        return activo;
    }

    public void setActivo(int activo) {
        this.activo.set(activo);
    }

    @Override
    public String toString() {
        return nombre.get();
    }
}