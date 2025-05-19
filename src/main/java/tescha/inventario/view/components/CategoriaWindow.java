package tescha.inventario.view.components;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import tescha.inventario.controller.InventarioController;
import tescha.inventario.dto.CategoriaDTO;

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

public class CategoriaWindow extends Stage {
    private final InventarioController controller;
    private final TableView<CategoriaDTO> tablaCategorias = new TableView<>();
    private final TextField campoNombre = new TextField();
    private final TextField campoDescripcion = new TextField();
    private final TextField campoColor = new TextField();
    private final ColorPicker colorPicker = new ColorPicker(Color.web("#2196F3"));
    private final ValidationSupport validationSupport = new ValidationSupport();
    private CategoriaDTO categoriaSeleccionada;

    // Panel de iconos
    private final FlowPane iconosPanel = new FlowPane();
    private String iconoSeleccionado = "DESKTOP";

    // Vista previa
    private final Label nombrePreview = new Label();
    private final FontAwesomeIconView iconoPreview = new FontAwesomeIconView(FontAwesomeIcon.DESKTOP);
    private final HBox previewContent = new HBox();
    private final Pane previewPane = new Pane();

    // Lista de íconos de FontAwesome más comunes
    private static final String[] ICONOS_COMUNES = {
            "DESKTOP", "LAPTOP", "MOBILE", "TABLET", "SERVER", "DATABASE",
            "WIFI", "PRINT", "CAMERA", "VIDEO_CAMERA", "HEADPHONES", "MICROPHONE",
            "KEYBOARD", "MOUSE", "USB", "HDD", "SSD", "MEMORY", "CPU", "TV",
            "PROJECTOR", "ROUTER", "SWITCH", "PHONE", "FAX", "SCANNER",
            "FOLDER", "FOLDER_OPEN", "ARCHIVE", "FILE", "FILE_TEXT", "CLOUD",
            "CLOUD_DOWNLOAD", "CLOUD_UPLOAD", "USERS", "USER", "COG", "COGS",
            "WRENCH", "TOOLS", "DOLLAR", "CREDIT_CARD", "MONEY", "BUILDING",
            "HOME", "BOOK", "BOOKMARK", "CALENDAR", "CLOCK", "BELL"
    };

    public CategoriaWindow(InventarioController controller, Stage ownerStage) {
        this.controller = controller;

        // Configurar ventana
        initOwner(ownerStage);
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.DECORATED);
        setTitle("Gestión de Categorías");
        setMinWidth(600);
        setMinHeight(300);

        // Crear escena principal
        Scene scene = new Scene(crearContenidoPrincipal());

        // Solución alternativa para CSS
        try {
            URL cssUrl = getClass().getResource("/styles/tacos.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Error cargando CSS: " + e.getMessage());
        }

        setScene(scene);

        // Inicializar componentes
        configurarTabla();
        configurarValidaciones();
        configurarIconosPanel();
        configurarVistaPrevia();
        cargarCategorias();

        setOnShown(e -> Platform.runLater(campoNombre::requestFocus));
    }

    private BorderPane crearContenidoPrincipal() {
        BorderPane root = new BorderPane();

        // Título en el encabezado
        HBox header = crearEncabezado("Gestión de Categorías");
        header.getStyleClass().add("header-panel");
        header.setPadding(new Insets(15));

        // Panel de contenido principal
        VBox contenido = new VBox(20);
        contenido.setPadding(new Insets(20));
        contenido.getChildren().addAll(
                crearFormulario(),
                crearTablaContenedor()
        );

        // Botones en el pie
        HBox footer = crearBotones();
        footer.setPadding(new Insets(15));
        footer.getStyleClass().add("footer-panel");

        root.setTop(header);
        root.setCenter(contenido);
        root.setBottom(footer);

        return root;
    }

    private HBox crearEncabezado(String titulo) {
        Label tituloLabel = new Label(titulo);
        tituloLabel.getStyleClass().add("window-title");

        FontAwesomeIconView icono = new FontAwesomeIconView(FontAwesomeIcon.TAGS);
        icono.setGlyphSize(30);
        icono.setFill(Color.valueOf("#2196F3"));

        HBox header = new HBox(15, icono, tituloLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private VBox crearFormulario() {
        // Configurar campos de texto
        configurarCampos();

        // Panel para el formulario con estilo elevado
        VBox formularioContainer = new VBox(20);
        formularioContainer.getStyleClass().add("form-panel");
        formularioContainer.setPadding(new Insets(20));
        formularioContainer.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Título del formulario
        Label tituloForm = new Label("Datos de la Categoría");
        tituloForm.getStyleClass().add("section-title");

        // Línea 1: Nombre y Descripción
        HBox fila1 = new HBox(20,
                new VBox(5, new Label("Nombre"), campoNombre),
                new VBox(5, new Label("Descripción"), campoDescripcion)
        );
        fila1.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(campoNombre, Priority.ALWAYS);
        HBox.setHgrow(campoDescripcion, Priority.ALWAYS);

        // Línea 2: Color
        HBox colorBox = new HBox(10,
                new VBox(5, new Label("Color"), campoColor),
                colorPicker
        );

        // Panel de iconos con título
        VBox iconosContainer = new VBox(10);
        Label iconosLabel = new Label("Seleccione un ícono");
        ScrollPane iconosScroll = new ScrollPane(iconosPanel);
        iconosScroll.setFitToWidth(true);
        iconosScroll.setPrefHeight(150);
        iconosScroll.setStyle("-fx-background-color: white;");
        iconosContainer.getChildren().addAll(iconosLabel, iconosScroll);

        // Vista previa
        VBox previewContainer = crearVistaPrevia();

        // Agregar todos los elementos al formulario
        formularioContainer.getChildren().addAll(
                tituloForm,
                fila1,
                colorBox,
                iconosContainer,
                previewContainer
        );

        return formularioContainer;
    }

    private void configurarCampos() {
        // Configuración de campos
        campoNombre.setPromptText("Nombre de categoría");
        campoNombre.setPrefWidth(300);

        campoDescripcion.setPromptText("Descripción");

        campoColor.setPromptText("#RRGGBB");
        campoColor.setPrefWidth(120);

        // Selector de colores
        colorPicker.setOnAction(e -> {
            String hexColor = convertirColorAHex(colorPicker.getValue());
            campoColor.setText(hexColor);
            actualizarVistaPrevia(campoNombre.getText(), iconoSeleccionado, hexColor);
        });

        // Vincular campos a la vista previa
        campoNombre.textProperty().addListener((obs, oldVal, newVal) ->
                actualizarVistaPrevia(newVal, iconoSeleccionado, campoColor.getText())
        );

        campoColor.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (newVal.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$")) {
                    colorPicker.setValue(Color.web(newVal));
                    actualizarVistaPrevia(campoNombre.getText(), iconoSeleccionado, newVal);
                }
            } catch (Exception e) {
                // Ignorar colores inválidos
            }
        });
    }

    private void configurarIconosPanel() {
        iconosPanel.setHgap(10);
        iconosPanel.setVgap(10);
        iconosPanel.setPadding(new Insets(10));

        for (String iconName : ICONOS_COMUNES) {
            try {
                FontAwesomeIcon icon = FontAwesomeIcon.valueOf(iconName);
                FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
                iconView.setGlyphSize(24);

                StackPane iconContainer = new StackPane(iconView);
                iconContainer.setPadding(new Insets(8));
                iconContainer.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 4;");
                iconContainer.setCursor(javafx.scene.Cursor.HAND);

                // Tooltip con nombre del icono
                Tooltip tooltip = new Tooltip(iconName);
                Tooltip.install(iconContainer, tooltip);

                // Selección de icono
                iconContainer.setOnMouseClicked(e -> {
                    iconoSeleccionado = iconName;
                    resaltarIconoSeleccionado();
                    actualizarVistaPrevia(campoNombre.getText(), iconName, campoColor.getText());
                });

                // Añadir al panel de iconos
                iconosPanel.getChildren().add(iconContainer);

                // Si es el icono seleccionado por defecto, resaltarlo
                if (iconName.equals(iconoSeleccionado)) {
                    iconContainer.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #2196F3; -fx-border-radius: 4;");
                }
            } catch (Exception e) {
                // Ignorar iconos no válidos
            }
        }
    }

    private void resaltarIconoSeleccionado() {
        // Resetear estilo de todos los iconos
        for (int i = 0; i < iconosPanel.getChildren().size(); i++) {
            StackPane container = (StackPane) iconosPanel.getChildren().get(i);
            container.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 4;");

            // Aplicar estilo al seleccionado
            if (i < ICONOS_COMUNES.length && ICONOS_COMUNES[i].equals(iconoSeleccionado)) {
                container.setStyle("-fx-background-color: #e3f2fd; -fx-border-color: #2196F3; -fx-border-radius: 4;");
            }
        }
    }

    private VBox crearVistaPrevia() {
        Label previewLabel = new Label("Vista previa");
        previewLabel.getStyleClass().add("section-subtitle");

        // Contenedor para la vista previa
        previewPane.setPrefHeight(50);
        previewPane.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 4;");

        // Elementos de la vista previa
        nombrePreview.setText("Nombre de categoría");
        iconoPreview.setGlyphSize(18);

        // Configurar el contenido de la vista previa
        previewContent.setAlignment(Pos.CENTER_LEFT);
        previewContent.setSpacing(15);
        previewContent.setPadding(new Insets(15));
        previewContent.getChildren().addAll(iconoPreview, nombrePreview);

        // Actualizar la vista previa inicial
        actualizarVistaPrevia("Nombre de categoría", iconoSeleccionado, "#2196F3");

        StackPane previewStack = new StackPane(previewPane, previewContent);
        previewStack.setPadding(new Insets(10, 0, 0, 0));

        // Contenedor para la etiqueta y la vista previa
        VBox previewContainer = new VBox(5);
        previewContainer.getChildren().addAll(previewLabel, previewStack);

        return previewContainer;
    }

    private void configurarVistaPrevia() {
        // Configurar el comportamiento de la vista previa
        nombrePreview.setStyle("-fx-font-size: 14px;");
        iconoPreview.setGlyphSize(18);
    }

    private void actualizarVistaPrevia(String nombre, String icono, String color) {
        // Actualizar texto
        nombrePreview.setText(nombre.isEmpty() ? "Nombre de categoría" : nombre);

        // Actualizar icono
        try {
            FontAwesomeIcon icon = FontAwesomeIcon.valueOf(icono);
            iconoPreview.setIcon(icon);
        } catch (Exception e) {
            iconoPreview.setIcon(FontAwesomeIcon.QUESTION);
        }

        // Actualizar color
        try {
            String colorStyle = "-fx-text-fill: " + color + ";";
            nombrePreview.setStyle(colorStyle + "-fx-font-size: 14px;");
            iconoPreview.setStyle(colorStyle);
        } catch (Exception e) {
            // Usar color por defecto si no es válido
            nombrePreview.setStyle("-fx-text-fill: #2196F3; -fx-font-size: 14px;");
            iconoPreview.setStyle("-fx-text-fill: #2196F3;");
        }
    }



    private VBox crearTablaContenedor() {
        Label tituloTabla = new Label("Categorías existentes");
        tituloTabla.getStyleClass().add("section-title");

        // Panel para la tabla con estilo elevado
        VBox tablaContainer = new VBox(10);
        tablaContainer.getStyleClass().add("table-panel");
        tablaContainer.setPadding(new Insets(20));
        tablaContainer.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        VBox.setVgrow(tablaCategorias, Priority.ALWAYS);
        tablaCategorias.setPrefHeight(200);

        tablaContainer.getChildren().addAll(tituloTabla, tablaCategorias);

        return tablaContainer;
    }

    private void configurarTabla() {
        // Configurar tabla con estilo material
        tablaCategorias.getStyleClass().add("table-view");
        tablaCategorias.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Columna para el ícono
        TableColumn<CategoriaDTO, String> colIcono = new TableColumn<>("");
        colIcono.setCellValueFactory(new PropertyValueFactory<>("icono"));
        colIcono.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String icono, boolean empty) {
                super.updateItem(icono, empty);
                if (empty || icono == null) {
                    setGraphic(null);
                } else {
                    try {
                        FontAwesomeIconView iconView = new FontAwesomeIconView();
                        iconView.setIcon(FontAwesomeIcon.valueOf(icono));
                        iconView.setGlyphSize(16);
                        String colorHex = getTableRow().getItem() != null ?
                                getTableRow().getItem().getColor() : "#000000";
                        iconView.setFill(Color.web(colorHex));
                        setGraphic(iconView);
                    } catch (Exception e) {
                        setGraphic(new FontAwesomeIconView(FontAwesomeIcon.QUESTION));
                    }
                }
            }
        });
        colIcono.setPrefWidth(40);
        colIcono.setResizable(false);

        // Columna para el nombre
        TableColumn<CategoriaDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNombre()));

        // Columna para la descripción
        TableColumn<CategoriaDTO, String> colDesc = new TableColumn<>("Descripción");
        colDesc.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDescripcion()));

        // Columna para el color
        TableColumn<CategoriaDTO, String> colColor = new TableColumn<>("Color");
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colColor.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String color, boolean empty) {
                super.updateItem(color, empty);
                if (empty || color == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(color);

                    // Mostrar un rectángulo con el color
                    Pane colorSquare = new Pane();
                    colorSquare.setPrefSize(20, 20);
                    colorSquare.setStyle("-fx-background-color: " + color + "; -fx-border-color: #e0e0e0;");

                    HBox container = new HBox(10, colorSquare, new Label(color));
                    container.setAlignment(Pos.CENTER_LEFT);

                    setGraphic(container);
                }
            }
        });

        // Agregar columnas a la tabla
        tablaCategorias.getColumns().addAll(colIcono, colNombre, colDesc, colColor);

        // Manejar selección
        tablaCategorias.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> cargarDatosCategoria(newSelection));
    }

    private HBox crearBotones() {
        // Botón agregar
        Button btnAgregar = new Button("Agregar");
        btnAgregar.getStyleClass().add("primary-button");
        btnAgregar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.PLUS));
        btnAgregar.setOnAction(e -> agregarCategoria());

        // Botón actualizar
        Button btnActualizar = new Button("Actualizar");
        btnActualizar.getStyleClass().add("accent-button");
        btnActualizar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.REFRESH));
        btnActualizar.setOnAction(e -> actualizarCategoria());
        btnActualizar.disableProperty().bind(
                tablaCategorias.getSelectionModel().selectedItemProperty().isNull());

        // Botón eliminar
        Button btnEliminar = new Button("Eliminar");
        btnEliminar.getStyleClass().add("danger-button");
        btnEliminar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TRASH));
        btnEliminar.setOnAction(e -> confirmarEliminacion());
        btnEliminar.disableProperty().bind(
                tablaCategorias.getSelectionModel().selectedItemProperty().isNull());

        // Botón limpiar
        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.ERASER));
        btnLimpiar.setOnAction(e -> limpiarCampos());

        // Botón cerrar
        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOSE));
        btnCerrar.setOnAction(e -> close());

        HBox botonesBox = new HBox(10, btnAgregar, btnActualizar, btnEliminar, btnLimpiar, btnCerrar);
        botonesBox.setAlignment(Pos.CENTER_RIGHT);
        return botonesBox;
    }

    private void configurarValidaciones() {
        validationSupport.registerValidator(campoNombre,
                Validator.createEmptyValidator("El nombre es obligatorio", Severity.ERROR));

        validationSupport.registerValidator(campoColor,
                Validator.createRegexValidator("Color hexadecimal inválido",
                        Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"), Severity.ERROR));
    }

    private void cargarCategorias() {
        List<CategoriaDTO> categorias = controller.listarCategorias();
        tablaCategorias.getItems().setAll(categorias);
    }

    private void cargarDatosCategoria(CategoriaDTO categoria) {
        categoriaSeleccionada = categoria;

        if (categoria != null) {
            campoNombre.setText(categoria.getNombre());
            campoDescripcion.setText(categoria.getDescripcion());
            campoColor.setText(categoria.getColor());
            iconoSeleccionado = categoria.getIcono();

            // Actualizar vista previa
            actualizarVistaPrevia(categoria.getNombre(), categoria.getIcono(), categoria.getColor());

            // Actualizar selección en el panel de iconos
            resaltarIconoSeleccionado();

            try {
                colorPicker.setValue(Color.web(categoria.getColor()));
            } catch (Exception e) {
                colorPicker.setValue(Color.BLACK);
            }
        } else {
            limpiarCampos();
        }
    }

    private void limpiarCampos() {
        campoNombre.clear();
        campoDescripcion.clear();
        campoColor.setText("#2196F3");
        iconoSeleccionado = "DESKTOP";
        colorPicker.setValue(Color.web("#2196F3"));
        categoriaSeleccionada = null;
        tablaCategorias.getSelectionModel().clearSelection();

        // Actualizar vista previa
        actualizarVistaPrevia("", iconoSeleccionado, "#2196F3");

        // Actualizar selección en el panel de iconos
        resaltarIconoSeleccionado();
    }

    private void agregarCategoria() {
        if (validationSupport.isInvalid()) {
            mostrarNotificacion("Corrija los campos con errores", NotificationType.WARNING);
            return;
        }

        CategoriaDTO nuevaCategoria = new CategoriaDTO(
                0,
                campoNombre.getText().trim(),
                campoDescripcion.getText().trim(),
                campoColor.getText().trim(),
                iconoSeleccionado
        );

        if (controller.agregarCategoria(nuevaCategoria)) {
            cargarCategorias();
            limpiarCampos();
            mostrarNotificacion("Categoría agregada correctamente", NotificationType.SUCCESS);
        } else {
            mostrarNotificacion("No se pudo agregar la categoría", NotificationType.ERROR);
        }
    }

    private void actualizarCategoria() {
        if (validationSupport.isInvalid()) {
            mostrarNotificacion("Corrija los campos con errores", NotificationType.WARNING);
            return;
        }

        if (categoriaSeleccionada == null) {
            mostrarNotificacion("Seleccione una categoría para actualizar", NotificationType.WARNING);
            return;
        }

        CategoriaDTO categoriaActualizada = new CategoriaDTO(
                categoriaSeleccionada.getId(),
                campoNombre.getText().trim(),
                campoDescripcion.getText().trim(),
                campoColor.getText().trim(),
                iconoSeleccionado
        );

        if (controller.actualizarCategoria(categoriaActualizada)) {
            cargarCategorias();
            mostrarNotificacion("Categoría actualizada correctamente", NotificationType.SUCCESS);
        } else {
            mostrarNotificacion("No se pudo actualizar la categoría", NotificationType.ERROR);
        }
    }

    private void confirmarEliminacion() {
        if (categoriaSeleccionada == null) {
            mostrarNotificacion("Seleccione una categoría para eliminar", NotificationType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar la categoría?");
        alert.setContentText("Categoría: " + categoriaSeleccionada.getNombre());

        ButtonType btnConfirmar = new ButtonType("Eliminar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(btnConfirmar, btnCancelar);

        alert.showAndWait().ifPresent(response -> {
            if (response == btnConfirmar) {
                eliminarCategoria();
            }
        });
    }

    private void eliminarCategoria() {
        if (controller.eliminarCategoria(categoriaSeleccionada.getId())) {
            cargarCategorias();
            limpiarCampos();
            mostrarNotificacion("Categoría eliminada correctamente", NotificationType.SUCCESS);
        } else {
            mostrarNotificacion("No se pudo eliminar la categoría", NotificationType.ERROR);
        }
    }

    public enum NotificationType {
        SUCCESS,
        WARNING,
        ERROR,
        INFO
    }

    private void mostrarNotificacion(String mensaje, NotificationType tipo) {
        Notifications notificacion = Notifications.create()
                .title("Gestión de Categorías")
                .text(mensaje)
                .hideAfter(Duration.seconds(3))
                .position(Pos.BOTTOM_RIGHT);

        switch (tipo) {
            case SUCCESS:
                notificacion.graphic(new FontAwesomeIconView(FontAwesomeIcon.CHECK_CIRCLE));
                notificacion.showConfirm();
                break;
            case WARNING:
                notificacion.graphic(new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_TRIANGLE));
                notificacion.showWarning();
                break;
            case ERROR:
                notificacion.graphic(new FontAwesomeIconView(FontAwesomeIcon.TIMES_CIRCLE));
                notificacion.showError();
                break;
            default:
                notificacion.graphic(new FontAwesomeIconView(FontAwesomeIcon.INFO_CIRCLE));
                notificacion.showInformation();
        }
    }

    private String convertirColorAHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    /**
     * Muestra la ventana de gestión de categorías.
     */
    public void mostrar() {
        cargarCategorias();
        show();
    }
}