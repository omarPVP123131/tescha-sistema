package tescha.inventario.view.components;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.geometry.*;
import tescha.inventario.controller.InventarioController;
import tescha.inventario.dto.CategoriaDTO;
import tescha.inventario.dto.EquipoDTO;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

public class EquipoForm extends Dialog<EquipoDTO> {
    private final InventarioController controller;
    private final EquipoDTO equipoOriginal;
    private final SimpleBooleanProperty formValid = new SimpleBooleanProperty(false);

    // Campos del formulario
    private TextField nombreField;
    private ComboBox<CategoriaDTO> categoriaCombo;
    private Spinner<Integer> cantidadSpinner, minimoSpinner;
    private ComboBox<String> statusCombo;
    private TextField ubicacionField;
    private TextField serieField, marcaField, modeloField, proveedorField;
    private TextField garantiaField, mantenimientoField, notasField;
    private DatePicker fechaAdqPicker, vencGarantiaPicker, ultimoMantPicker, proximoMantPicker;
    private Spinner<Double> costoSpinner;

    // Imagen
    private String rutaImagen = null;
    private ImageView previewImageView;
    private Label imagenLabel;

    // UI components
    private TabPane tabPane;
    private Button btnGuardar;

    public EquipoForm(InventarioController controller, EquipoDTO equipoExistente) {
        this.controller = controller;
        this.equipoOriginal = equipoExistente;

        configurarDialog();
        crearInterfaz();

        if (equipoOriginal != null) {
            cargarDatosEquipo();
            setTitle("Editar Equipo");
        } else {
            setTitle("Nuevo Equipo");
        }

        validarFormulario();
    }

    private void configurarDialog() {
        setHeaderText(null); // Eliminar header text para un diseño más limpio
        getDialogPane().setPrefWidth(800);
        getDialogPane().setPrefHeight(600);

        getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/inventario/styles.css")
                        .toExternalForm()
        );

        getDialogPane().getStyleClass().add("equipo-form");

        // Botones
        ButtonType guardarButtonType = new ButtonType(equipoOriginal == null ? "Agregar" : "Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelarButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(guardarButtonType, cancelarButtonType);

        btnGuardar = (Button) getDialogPane().lookupButton(guardarButtonType);
        btnGuardar.getStyleClass().add("btn-primary");
        btnGuardar.disableProperty().bind(formValid.not());

        Button btnCancelar = (Button) getDialogPane().lookupButton(cancelarButtonType);
        btnCancelar.getStyleClass().add("btn-secondary");

        setResultConverter(dialogButton -> {
            if (dialogButton == guardarButtonType) {
                EquipoDTO equipoGuardado = guardarEquipo();
                if (equipoGuardado != null) {
                    if (equipoOriginal == null) {
                        controller.agregarEquipo(equipoGuardado);
                    } else {
                        controller.actualizarEquipo(equipoGuardado);
                    }
                }
                return equipoGuardado;
            }
            return null;
        });
    }


    private void crearInterfaz() {
        // Tabs para organizar la información
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab infoBasicaTab = new Tab("Información General");
        Tab infoTecnicaTab = new Tab("Detalles Técnicos");
        Tab mantenimientoTab = new Tab("Mantenimiento");

        tabPane.getTabs().addAll(infoBasicaTab, infoTecnicaTab, mantenimientoTab);

        // Primera pestaña: Información General
        infoBasicaTab.setContent(crearSeccionInfoBasica());

        // Segunda pestaña: Información Técnica
        infoTecnicaTab.setContent(crearSeccionInfoTecnica());

        // Tercera pestaña: Mantenimiento
        mantenimientoTab.setContent(crearSeccionMantenimiento());

        getDialogPane().setContent(tabPane);
    }

    private VBox crearSeccionInfoBasica() {
        VBox seccion = new VBox(15);
        seccion.setPadding(new Insets(20));

        // Título de sección
        Text titulo = new Text("Datos Generales del Equipo");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        seccion.getChildren().add(titulo);

        // Panel de imagen a la derecha
        BorderPane panelSuperior = new BorderPane();

        // Panel izquierdo con campos principales
        GridPane gridIzquierdo = new GridPane();
        gridIzquierdo.setVgap(12);
        gridIzquierdo.setHgap(10);

        // Configuración de campos
        nombreField = new TextField();
        nombreField.setPromptText("Nombre del equipo");
        nombreField.getStyleClass().add("required-field");

        categoriaCombo = new ComboBox<>();
        categoriaCombo.setPromptText("Seleccione categoría");
        categoriaCombo.setItems(FXCollections.observableArrayList(controller.listarCategorias()));
        categoriaCombo.getSelectionModel().selectFirst(); // Selección por defecto

        categoriaCombo.setCellFactory(lv -> new ListCell<CategoriaDTO>() {
            @Override
            protected void updateItem(CategoriaDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });

        categoriaCombo.setButtonCell(new ListCell<CategoriaDTO>() {
            @Override
            protected void updateItem(CategoriaDTO item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getNombre());
            }
        });


        statusCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Disponible", "En uso", "Mantenimiento", "Dañado", "Obsoleto"));
        statusCombo.setValue("Disponible");

        cantidadSpinner = crearSpinner(1, 0, 9999);
        minimoSpinner = crearSpinner(1, 0, 9999);

        ubicacionField = new TextField();
        ubicacionField.setPromptText("Ubicación física");

        // Añadir campos al grid
        int row = 0;
        gridIzquierdo.add(new Label("Nombre*:"), 0, row);
        gridIzquierdo.add(nombreField, 1, row++);

        gridIzquierdo.add(new Label("Categoría:"), 0, row);
        gridIzquierdo.add(categoriaCombo, 1, row++);

        gridIzquierdo.add(new Label("Estado:"), 0, row);
        gridIzquierdo.add(statusCombo, 1, row++);

        gridIzquierdo.add(new Label("Cantidad*:"), 0, row);
        gridIzquierdo.add(cantidadSpinner, 1, row++);

        gridIzquierdo.add(new Label("Cantidad mínima*:"), 0, row);
        gridIzquierdo.add(minimoSpinner, 1, row++);

        gridIzquierdo.add(new Label("Ubicación:"), 0, row);
        gridIzquierdo.add(ubicacionField, 1, row++);

        // Configurar columnas
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        col1.setMinWidth(100);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setFillWidth(true);

        gridIzquierdo.getColumnConstraints().addAll(col1, col2);

        // Panel derecho para la imagen
        VBox panelImagen = new VBox(10);
        panelImagen.setAlignment(Pos.CENTER);
        panelImagen.setPadding(new Insets(5));
        panelImagen.setMinWidth(200);
        panelImagen.setMaxWidth(200);
        panelImagen.getStyleClass().add("image-panel");

        imagenLabel = new Label("Imagen del equipo");

        previewImageView = new ImageView();
        previewImageView.setFitHeight(150);
        previewImageView.setFitWidth(150);
        previewImageView.setPreserveRatio(true);
        previewImageView.setSmooth(true);
        previewImageView.getStyleClass().add("image-preview");

        // Imagen por defecto
        try {
            previewImageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
        } catch (Exception e) {
            // Ignorar si no existe la imagen por defecto
        }

        Button btnSeleccionarImagen = new Button("Seleccionar imagen");
        btnSeleccionarImagen.getStyleClass().add("btn-image");
        btnSeleccionarImagen.setOnAction(e -> seleccionarImagen());

        panelImagen.getChildren().addAll(imagenLabel, previewImageView, btnSeleccionarImagen);

        // Configurar layout
        panelSuperior.setLeft(gridIzquierdo);
        panelSuperior.setRight(panelImagen);

        // Notas - área de texto grande en la parte inferior
        VBox notasBox = new VBox(5);
        Label notasLabel = new Label("Notas adicionales:");

        TextArea notasArea = new TextArea();
        notasArea.setPromptText("Información adicional sobre el equipo...");
        notasArea.setPrefRowCount(4);
        notasField = new TextField(); // Mantenemos el TextField original como hidden para compatibilidad
        notasField.visibleProperty().set(false);

        // Binding bidireccional
        notasArea.textProperty().bindBidirectional(notasField.textProperty());

        notasBox.getChildren().addAll(notasLabel, notasArea);
        VBox.setVgrow(notasArea, Priority.ALWAYS);

        // Añadir todo a la sección
        seccion.getChildren().addAll(panelSuperior, new Separator(), notasBox);

        return seccion;
    }

    private VBox crearSeccionInfoTecnica() {
        VBox seccion = new VBox(15);
        seccion.setPadding(new Insets(20));


        // Título de sección
        Text titulo = new Text("Especificaciones Técnicas");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        seccion.getChildren().add(titulo);

        // Crear grid para los campos
        GridPane grid = new GridPane();
        grid.setVgap(12);
        grid.setHgap(15);

        // Primera columna
        marcaField = new TextField();
        marcaField.setPromptText("Marca/fabricante");

        modeloField = new TextField();
        modeloField.setPromptText("Modelo");

        serieField = new TextField("X");
        serieField.setPromptText("Número de serie");


        costoSpinner = new Spinner<>(0.0, Double.MAX_VALUE, 0.0, 50.0);
        costoSpinner.setEditable(true);
        costoSpinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                Double.parseDouble(newValue);
            } catch (NumberFormatException e) {
                Platform.runLater(() -> costoSpinner.getEditor().setText(oldValue));
            }
        });

        int row = 0;
        grid.add(new Label("Marca:"), 0, row);
        grid.add(marcaField, 1, row++);

        grid.add(new Label("Modelo:"), 0, row);
        grid.add(modeloField, 1, row++);

        grid.add(new Label("Número de serie:"), 0, row);
        grid.add(serieField, 1, row++);

        grid.add(new Label("Costo:"), 0, row);
        grid.add(costoSpinner, 1, row++);

        // Segunda columna
        proveedorField = new TextField();
        proveedorField.setPromptText("Proveedor");

        fechaAdqPicker = new DatePicker(LocalDate.now());
        fechaAdqPicker.setPromptText("Fecha de adquisición");

        garantiaField = new TextField();
        garantiaField.setPromptText("Datos de garantía");

        vencGarantiaPicker = new DatePicker();
        vencGarantiaPicker.setPromptText("Vencimiento de garantía");

        row = 0;
        grid.add(new Label("Proveedor:"), 2, row);
        grid.add(proveedorField, 3, row++);

        grid.add(new Label("Fecha adquisición:"), 2, row);
        grid.add(fechaAdqPicker, 3, row++);

        grid.add(new Label("Garantía:"), 2, row);
        grid.add(garantiaField, 3, row++);

        grid.add(new Label("Venc. garantía:"), 2, row);
        grid.add(vencGarantiaPicker, 3, row++);

        // Configurar columnas
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        col1.setMinWidth(120);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setFillWidth(true);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.NEVER);
        col3.setMinWidth(120);

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setHgrow(Priority.ALWAYS);
        col4.setFillWidth(true);

        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        seccion.getChildren().add(grid);

        return seccion;
    }

    private VBox crearSeccionMantenimiento() {
        VBox seccion = new VBox(15);
        seccion.setPadding(new Insets(20));

        // Título de sección
        Text titulo = new Text("Información de Mantenimiento");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 16));
        seccion.getChildren().add(titulo);

        // Crear grid para los campos
        GridPane grid = new GridPane();
        grid.setVgap(12);
        grid.setHgap(15);

        mantenimientoField = new TextField();
        mantenimientoField.setPromptText("Detalles del mantenimiento requerido");

        ultimoMantPicker = new DatePicker();
        ultimoMantPicker.setPromptText("Fecha del último mantenimiento");

        proximoMantPicker = new DatePicker();
        proximoMantPicker.setPromptText("Fecha del próximo mantenimiento");

        int row = 0;
        grid.add(new Label("Mantenimiento programado:"), 0, row);
        grid.add(mantenimientoField, 1, row++, 3, 1);

        grid.add(new Label("Último mantenimiento:"), 0, row);
        grid.add(ultimoMantPicker, 1, row);

        grid.add(new Label("Próximo mantenimiento:"), 2, row);
        grid.add(proximoMantPicker, 3, row++);

        // Configurar columnas
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.NEVER);
        col1.setMinWidth(150);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        col2.setFillWidth(true);

        ColumnConstraints col3 = new ColumnConstraints();
        col3.setHgrow(Priority.NEVER);
        col3.setMinWidth(150);

        ColumnConstraints col4 = new ColumnConstraints();
        col4.setHgrow(Priority.ALWAYS);
        col4.setFillWidth(true);

        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        // Sección de historial de mantenimiento (simulada)
        TitledPane historialPane = new TitledPane();
        historialPane.setText("Historial de Mantenimiento");

        ListView<String> historialList = new ListView<>();
        historialList.setPlaceholder(new Label("No hay registros de mantenimiento"));

        if (equipoOriginal != null && equipoOriginal.getUltimoMantenimiento() != null) {
            historialList.getItems().add(
                    String.format("%s - Mantenimiento regular realizado",
                            equipoOriginal.getUltimoMantenimiento().toString()));
        }

        historialPane.setContent(historialList);
        historialPane.setExpanded(false);

        seccion.getChildren().addAll(grid, historialPane);

        // Configurar cálculo automático de fecha de próximo mantenimiento
        Button btnCalcularProximo = new Button("Calcular próximo mantenimiento");
        btnCalcularProximo.getStyleClass().add("btn-secondary");
        btnCalcularProximo.setOnAction(e -> {
            if (ultimoMantPicker.getValue() != null) {
                // Asumimos que el mantenimiento es cada 3 meses, pero se podría hacer configurable
                proximoMantPicker.setValue(ultimoMantPicker.getValue().plusMonths(3));
                mostrarNotificacion("Fecha calculada en base al último mantenimiento", false);
            } else {
                mostrarNotificacion("Defina primero la fecha del último mantenimiento", true);
            }
        });

        seccion.getChildren().add(btnCalcularProximo);

        return seccion;
    }

    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del equipo");

        // Configurar filtros de extensión
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Archivos de imagen", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        // Mostrar el diálogo
        Stage stage = (Stage) getDialogPane().getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                // Guardar la ruta de la imagen seleccionada
                rutaImagen = file.getAbsolutePath();

                // Actualizar la vista previa
                Image imagen = new Image(file.toURI().toString());
                previewImageView.setImage(imagen);

                // Animación de aparición
                FadeTransition ft = new FadeTransition(Duration.millis(500), previewImageView);
                ft.setFromValue(0.3);
                ft.setToValue(1.0);
                ft.play();

                imagenLabel.setText("Imagen seleccionada");
            } catch (Exception e) {
                mostrarNotificacion("Error al cargar la imagen: " + e.getMessage(), true);
            }
        }
    }

    private <T> Spinner<T> crearSpinner(T initialValue, T min, T max) {
        SpinnerValueFactory<T> valueFactory;

        if (initialValue instanceof Integer) {
            valueFactory = (SpinnerValueFactory<T>) new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    (Integer) min, (Integer) max, (Integer) initialValue);
        } else if (initialValue instanceof Double) {
            valueFactory = (SpinnerValueFactory<T>) new SpinnerValueFactory.DoubleSpinnerValueFactory(
                    (Double) min, (Double) max, (Double) initialValue);
        } else {
            throw new IllegalArgumentException("Tipo no soportado para Spinner");
        }

        Spinner<T> spinner = new Spinner<>();
        spinner.setValueFactory(valueFactory);
        spinner.setEditable(true);
        spinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                if (initialValue instanceof Integer) {
                    Integer.parseInt(newValue);
                } else if (initialValue instanceof Double) {
                    Double.parseDouble(newValue);
                }
            } catch (NumberFormatException e) {
                Platform.runLater(() -> spinner.getEditor().setText(oldValue));
            }
        });

        return spinner;
    }

    private void cargarDatosEquipo() {
        nombreField.setText(equipoOriginal.getNombre());

        // Buscar categoría por nombre
        if (equipoOriginal.getCategoria() != null && !equipoOriginal.getCategoria().isEmpty()) {
            for (CategoriaDTO cat : categoriaCombo.getItems()) {
                if (cat.getNombre().equals(equipoOriginal.getCategoria())) {
                    categoriaCombo.setValue(cat);
                    break;
                }
            }
        }

        cantidadSpinner.getValueFactory().setValue(equipoOriginal.getCantidad());
        minimoSpinner.getValueFactory().setValue(equipoOriginal.getCantidadMinima());

        if (equipoOriginal.getStatus() != null) {
            statusCombo.setValue(equipoOriginal.getStatus());
        }

        ubicacionField.setText(equipoOriginal.getUbicacion());
        serieField.setText(equipoOriginal.getNumeroSerie());
        marcaField.setText(equipoOriginal.getMarca());
        modeloField.setText(equipoOriginal.getModelo());

        if (equipoOriginal.getFechaAdquisicion() != null) {
            fechaAdqPicker.setValue(equipoOriginal.getFechaAdquisicion());
        }

        costoSpinner.getValueFactory().setValue(equipoOriginal.getCostoAdquisicion());
        proveedorField.setText(equipoOriginal.getProveedor());
        garantiaField.setText(equipoOriginal.getGarantia());

        if (equipoOriginal.getVencimientoGarantia() != null) {
            vencGarantiaPicker.setValue(equipoOriginal.getVencimientoGarantia());
        }

        mantenimientoField.setText(equipoOriginal.getMantenimientoProgramado());

        if (equipoOriginal.getUltimoMantenimiento() != null) {
            ultimoMantPicker.setValue(equipoOriginal.getUltimoMantenimiento());
        }

        if (equipoOriginal.getProximoMantenimiento() != null) {
            proximoMantPicker.setValue(equipoOriginal.getProximoMantenimiento());
        }

        notasField.setText(equipoOriginal.getNotas());

        // Cargar imagen si existe
        if (equipoOriginal.getImagen() != null && !equipoOriginal.getImagen().isEmpty()) {
            try {
                File file = new File(equipoOriginal.getImagen());
                if (file.exists()) {
                    rutaImagen = equipoOriginal.getImagen();
                    previewImageView.setImage(new Image(file.toURI().toString()));
                    imagenLabel.setText("Imagen actual");
                }
            } catch (Exception e) {
                // Si falla la carga, dejamos la imagen por defecto
            }
        }
    }

    private void validarFormulario() {
        // Validar que los campos requeridos no estén vacíos
        nombreField.textProperty().addListener((observable, oldValue, newValue) -> validarCamposRequeridos());
        cantidadSpinner.valueProperty().addListener((observable, oldValue, newValue) -> validarCamposRequeridos());
        minimoSpinner.valueProperty().addListener((observable, oldValue, newValue) -> validarCamposRequeridos());

        // Validación inicial
        validarCamposRequeridos();
    }

    // Auxiliar para poner/quitar estilo de error
    private void applyErrorStyle(Control control, boolean error) {
        if (error) {
            control.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        } else {
            control.setStyle(null);
        }
    }

    private void validarCamposRequeridos() {
        boolean nombreValido    = !nombreField.getText().trim().isEmpty();
        boolean cantidadValida  = cantidadSpinner.getValue() != null && cantidadSpinner.getValue() >= 0;
        boolean minimoValido    = minimoSpinner.getValue()   != null && minimoSpinner.getValue()   >= 0;
        boolean relacionValida  = cantidadValida && minimoValido
                && cantidadSpinner.getValue() >= minimoSpinner.getValue();

        // Aplica o quita el estilo de error
        applyErrorStyle(nombreField,   !nombreValido);
        applyErrorStyle(cantidadSpinner.getEditor(),  !cantidadValida);
        applyErrorStyle(minimoSpinner.getEditor(),    !minimoValido || !relacionValida);

        // Habilita/deshabilita el flag general
        formValid.set(nombreValido && cantidadValida && minimoValido && relacionValida);

        // Mensaje si la relación falla
        if (cantidadValida && minimoValido && !relacionValida) {
            mostrarNotificacion("La cantidad actual debe ser mayor o igual a la cantidad mínima", true);
        }
    }


    private EquipoDTO guardarEquipo() {
        EquipoDTO equipo = equipoOriginal != null ? equipoOriginal : new EquipoDTO();

        // Guardar la imagen en un directorio de imágenes
        String rutaImagenFinal = rutaImagen;
        if (rutaImagen != null && !rutaImagen.equals(equipo.getImagen())) {
            try {
                // Definir directorio de destino
                Path directorioDestino = Paths.get(System.getProperty("user.dir"), "imagenes");
                if (!Files.exists(directorioDestino)) {
                    Files.createDirectories(directorioDestino);
                }

                // Generar nombre único para la imagen
                String extension = rutaImagen.substring(rutaImagen.lastIndexOf('.'));
                String nombreArchivo = "equipo_" + UUID.randomUUID().toString() + extension;
                Path rutaDestino = directorioDestino.resolve(nombreArchivo);

                // Copiar archivo
                Files.copy(Paths.get(rutaImagen), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
                rutaImagenFinal = rutaDestino.toString();
            } catch (Exception e) {
                mostrarNotificacion("Error al guardar la imagen: " + e.getMessage(), true);
                // En caso de error, usamos la ruta original
            }
        }

        // Configurar datos del equipo
        equipo.setNombre(nombreField.getText());
        equipo.setCategoria(categoriaCombo.getValue() != null ? categoriaCombo.getValue().getNombre() : null);
        equipo.setCantidad(cantidadSpinner.getValue());
        equipo.setCantidadMinima(minimoSpinner.getValue());
        equipo.setStatus(statusCombo.getValue());
        equipo.setUbicacion(ubicacionField.getText());
        equipo.setNumeroSerie(serieField.getText());
        equipo.setMarca(marcaField.getText());
        equipo.setModelo(modeloField.getText());
        equipo.setFechaAdquisicion(fechaAdqPicker.getValue());
        equipo.setCostoAdquisicion(costoSpinner.getValue());
        equipo.setProveedor(proveedorField.getText());
        equipo.setGarantia(garantiaField.getText());
        equipo.setVencimientoGarantia(vencGarantiaPicker.getValue());
        equipo.setMantenimientoProgramado(mantenimientoField.getText());
        equipo.setUltimoMantenimiento(ultimoMantPicker.getValue());
        equipo.setProximoMantenimiento(proximoMantPicker.getValue());
        equipo.setNotas(notasField.getText());
        equipo.setNotas(notasField.getText());
        equipo.setImagen(rutaImagenFinal);

        // Mostrar notificación de éxito
        mostrarNotificacion(equipoOriginal == null ?
                "Equipo agregado correctamente" : "Cambios guardados exitosamente", false);

        return equipo;
    }

    private void mostrarNotificacion(String mensaje, boolean esError) {
        Tooltip tooltip = new Tooltip(mensaje);
        tooltip.setAutoHide(true);
        tooltip.setStyle(esError ? "-fx-text-fill: #d9534f;" : "-fx-text-fill: #5cb85c;");

        // Mostrar tooltip cerca del botón de guardar
        tooltip.show(btnGuardar,
                btnGuardar.localToScreen(btnGuardar.getBoundsInLocal()).getMinX(),
                btnGuardar.localToScreen(btnGuardar.getBoundsInLocal()).getMinY() - 30);

        // Ocultar después de 3 segundos
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> tooltip.hide());
                    }
                },
                3000
        );
    }
}