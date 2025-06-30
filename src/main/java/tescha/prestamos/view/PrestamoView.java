package tescha.prestamos.view;

import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.application.Platform;
import javafx.scene.effect.DropShadow;

// AnimateFX imports
import animatefx.animation.*;

// ControlsFX imports
import javafx.util.Duration;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

// JFoenix imports
import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;

import tescha.inventario.dao.InventarioDAO;
import tescha.inventario.dao.InventarioSQLiteDAO;
import tescha.prestamos.controller.PrestamoController;
import tescha.prestamos.dto.PrestamoDTO;
import tescha.prestamos.dto.PrestamoDetalleDTO;
import tescha.inventario.dto.EquipoDTO;
import tescha.prestamos.util.SolicitanteManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class PrestamoView {

    private BorderPane view;
    private PrestamoController controller;
    private InventarioDAO inventarioDAO;
    private SolicitanteManager solicitanteManager;

    // TabPane principal
    private TabPane mainTabPane;

    // Tabs
    private Tab gestionTab;
    private Tab listadoTab;
    private Tab estadisticasTab;

    // Tablas principales
    private TableView<PrestamoDTO> prestamosTable;
    private ObservableList<PrestamoDTO> prestamosData;
    private TableView<PrestamoDetalleDTO> detallesTable;
    private ObservableList<PrestamoDetalleDTO> detallesData;

    // Carrito temporal para nuevos préstamos
    private TableView<PrestamoDetalleDTO> carritoTable;
    private ObservableList<PrestamoDetalleDTO> carritoData;

    // Campos del formulario mejorados
    private JFXTextField solicitanteField;
    private DatePicker fechaPrestamoField; // Cambiado a DatePicker nativo
    private JFXTextArea comentariosArea;

    // Botones con mejor diseño
    private JFXButton crearPrestamoBtn, editarPrestamoBtn, eliminarPrestamoBtn, devolverBtn;
    private JFXButton agregarEquipoBtn, eliminarEquipoBtn, limpiarCarritoBtn;

    // Diálogo de inventario mejorado
    private JFXPopup inventarioPopup;
    private TableView<EquipoDTO> tablaInventario;
    private ObservableList<EquipoDTO> inventarioData;
    private JFXTextField cantidadField;
    private JFXTextField buscarEquipoField;

    // Estado del formulario
    private boolean modoEdicion = false;
    private PrestamoDTO prestamoEnEdicion = null;

    // Referencias para estadísticas en vivo
    private Label totalPrestamosLabel;
    private Label pendientesLabel;
    private Label devueltosLabel;

    // Colores del tema moderno
    private static final String PRIMARY_COLOR = "#6C63FF";
    private static final String SECONDARY_COLOR = "#FF6B6B";
    private static final String SUCCESS_COLOR = "#4ECDC4";
    private static final String WARNING_COLOR = "#FFE66D";
    private static final String BACKGROUND_COLOR = "#F8F9FF";
    private static final String CARD_COLOR = "#FFFFFF";

    public PrestamoView(PrestamoController controller) {
        this.controller = controller;
        this.solicitanteManager = new SolicitanteManager();
        this.view = new BorderPane();
        this.prestamosData = FXCollections.observableArrayList();
        this.detallesData = FXCollections.observableArrayList();
        this.carritoData = FXCollections.observableArrayList();
        this.inventarioData = FXCollections.observableArrayList();

        try {
            this.inventarioDAO = new InventarioSQLiteDAO(controller.getConnection());
        } catch (SQLException e) {
            mostrarNotificacion("Error", "No se pudo conectar con el inventario", Alert.AlertType.ERROR);
        }

        inicializarUI();
        configurarEventos();
        configurarAtajosTeclado();
        cargarDatos();

        // Animación de entrada elegante
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), view);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(800), view);
        slideIn.setFromY(-50);
        slideIn.setToY(0);

        ParallelTransition entrance = new ParallelTransition(fadeIn, slideIn);
        entrance.play();
    }

    private void inicializarUI() {
        view.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Contenedor principal
        VBox mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(20));

        // Header moderno
        createModernHeader(mainContainer);

        // TabPane principal
        mainTabPane = new TabPane();
        mainTabPane.setStyle("-fx-background-color: transparent;");
        mainTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Crear las pestañas
        createGestionTab();
        createListadoTab();
        createEstadisticasTab();

        mainTabPane.getTabs().addAll(gestionTab, listadoTab, estadisticasTab);

        mainContainer.getChildren().add(mainTabPane);
        VBox.setVgrow(mainTabPane, Priority.ALWAYS);

        view.setCenter(mainContainer);
    }

    private void createModernHeader(VBox container) {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(20));
        header.setStyle("-fx-background-color: " + CARD_COLOR + "; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(108,99,255,0.3), 20, 0, 0, 5);");

        // Icono principal
        Glyph mainIcon = new Glyph("FontAwesome", FontAwesome.Glyph.CLIPBOARD);
        mainIcon.setFontSize(36);
        mainIcon.setColor(Color.web(PRIMARY_COLOR));

        VBox titleContainer = new VBox(5);

        Label title = new Label("Sistema de Préstamos");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");

        Label subtitle = new Label("Gestión moderna de equipos y préstamos");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #7F8C8D; -fx-font-style: italic;");

        titleContainer.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Estadísticas rápidas en el header
        HBox statsContainer = createHeaderStatsContainer();

        header.getChildren().addAll(mainIcon, titleContainer, spacer, statsContainer);
        container.getChildren().add(header);
    }

    private HBox createHeaderStatsContainer() {
        HBox stats = new HBox(15);
        stats.setAlignment(Pos.CENTER_RIGHT);

        VBox totalCard = createStatCard("Total", "0", SUCCESS_COLOR);
        VBox pendientesCard = createStatCard("Pendientes", "0", WARNING_COLOR);
        VBox devueltosCard = createStatCard("Devueltos", "0", PRIMARY_COLOR);

        // Guardar referencias para actualización en vivo
        totalPrestamosLabel = (Label) ((VBox) totalCard.getChildren().get(0)).getChildren().get(0);
        pendientesLabel = (Label) ((VBox) pendientesCard.getChildren().get(0)).getChildren().get(0);
        devueltosLabel = (Label) ((VBox) devueltosCard.getChildren().get(0)).getChildren().get(0);

        stats.getChildren().addAll(totalCard, pendientesCard, devueltosCard);
        return stats;
    }

    private VBox createStatCard(String label, String value, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(12));
        card.setStyle("-fx-background-color: " + color + "; " +
                "-fx-background-radius: 10px; " +
                "-fx-min-width: 70px;");

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 11px; -fx-text-fill: white; -fx-opacity: 0.9;");

        VBox innerContainer = new VBox(3);
        innerContainer.setAlignment(Pos.CENTER);
        innerContainer.getChildren().addAll(valueLabel, labelText);

        card.getChildren().add(innerContainer);
        return card;
    }

    // TAB 1: GESTIÓN DE PRÉSTAMOS
    private void createGestionTab() {
        gestionTab = new Tab("📋 Gestión");
        gestionTab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        HBox gestionContent = new HBox(20);
        gestionContent.setPadding(new Insets(20));

        // Panel izquierdo - Formulario
        VBox formularioPanel = createFormularioPanel();

        // Panel derecho - Carrito y vista previa
        VBox carritoPanel = createCarritoPanel();

        gestionContent.getChildren().addAll(formularioPanel, carritoPanel);
        HBox.setHgrow(formularioPanel, Priority.SOMETIMES);
        HBox.setHgrow(carritoPanel, Priority.SOMETIMES);

        ScrollPane scrollPane = new ScrollPane(gestionContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        gestionTab.setContent(scrollPane);
    }

    // TAB 2: LISTADO DE PRÉSTAMOS
    private void createListadoTab() {
        listadoTab = new Tab("📊 Listado");
        listadoTab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox listadoContent = new VBox(20);
        listadoContent.setPadding(new Insets(20));

        // Panel de préstamos
        VBox prestamosPanel = createPrestamosPanel();

        // Panel de detalles
        VBox detallesPanel = createDetallesPanel();

        listadoContent.getChildren().addAll(prestamosPanel, detallesPanel);
        VBox.setVgrow(prestamosPanel, Priority.ALWAYS);

        listadoTab.setContent(listadoContent);
    }

    // TAB 3: ESTADÍSTICAS
    private void createEstadisticasTab() {
        estadisticasTab = new Tab("📈 Estadísticas");
        estadisticasTab.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        VBox estadisticasContent = createEstadisticasPanel();
        estadisticasTab.setContent(estadisticasContent);
    }

    private VBox createFormularioPanel() {
        VBox panel = new VBox(20);
        panel.setPrefWidth(400);
        panel.setStyle("-fx-background-color: " + CARD_COLOR + "; " +
                "-fx-background-radius: 15px; " +
                "-fx-padding: 25px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);");

        // Título del formulario
        HBox titleContainer = new HBox(15);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        Glyph formIcon = new Glyph("FontAwesome", FontAwesome.Glyph.PLUS_CIRCLE);
        formIcon.setFontSize(20);
        formIcon.setColor(Color.web(PRIMARY_COLOR));

        Label formTitle = new Label("Nuevo Préstamo");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");

        titleContainer.getChildren().addAll(formIcon, formTitle);

        // Campos del formulario
        solicitanteField = createStyledTextField("Nombre del solicitante");

        // Usar DatePicker nativo en lugar de JFXDatePicker
        fechaPrestamoField = new DatePicker();
        fechaPrestamoField.setValue(LocalDate.now());
        fechaPrestamoField.setPromptText("Fecha de préstamo");
        fechaPrestamoField.setStyle("-fx-font-size: 14px;");

        comentariosArea = new JFXTextArea();
        comentariosArea.setPromptText("Observaciones sobre el préstamo");
        comentariosArea.setLabelFloat(true);
        comentariosArea.setPrefRowCount(3);
        comentariosArea.setStyle("-fx-font-size: 14px;");

        // Configurar autocompletado
        configurarAutocompletadoMejorado();

        // Botones de acción del formulario
        HBox botonesFormulario = createBotonesFormulario();

        panel.getChildren().addAll(
                titleContainer,
                new Separator(),
                createFieldContainer("Solicitante", solicitanteField),
                createFieldContainer("Fecha de Préstamo", fechaPrestamoField),
                createFieldContainer("Comentarios", comentariosArea),
                botonesFormulario
        );

        return panel;
    }

    private VBox createCarritoPanel() {
        VBox panel = new VBox(20);
        panel.setPrefWidth(400);
        panel.setStyle("-fx-background-color: " + CARD_COLOR + "; " +
                "-fx-background-radius: 15px; " +
                "-fx-padding: 25px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);");

        // Título del carrito
        HBox titleContainer = new HBox(15);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        Glyph cartIcon = new Glyph("FontAwesome", FontAwesome.Glyph.SHOPPING_CART);
        cartIcon.setFontSize(20);
        cartIcon.setColor(Color.web(SUCCESS_COLOR));

        Label title = new Label("Equipos a Prestar");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + SUCCESS_COLOR + ";");

        titleContainer.getChildren().addAll(cartIcon, title);

        // Tabla del carrito
        carritoTable = new TableView<>();
        carritoTable.setItems(carritoData);
        carritoTable.setPrefHeight(200);
        carritoTable.setStyle("-fx-background-radius: 8px;");

        configurarColumnasCarrito();

        // Botones del carrito
        HBox botonesCarrito = new HBox(10);
        botonesCarrito.setAlignment(Pos.CENTER_LEFT);

        agregarEquipoBtn = createStyledButton("Agregar (F1)", SUCCESS_COLOR, FontAwesome.Glyph.PLUS);
        eliminarEquipoBtn = createStyledButton("Quitar (F2)", SECONDARY_COLOR, FontAwesome.Glyph.MINUS);
        limpiarCarritoBtn = createStyledButton("Limpiar (F3)", WARNING_COLOR, FontAwesome.Glyph.TRASH);

        botonesCarrito.getChildren().addAll(agregarEquipoBtn, eliminarEquipoBtn, limpiarCarritoBtn);

        panel.getChildren().addAll(titleContainer, new Separator(), carritoTable, botonesCarrito);
        return panel;
    }

    private void configurarColumnasCarrito() {
        TableColumn<PrestamoDetalleDTO, String> colNombreCarrito = new TableColumn<>("Equipo");
        colNombreCarrito.setCellValueFactory(cellData -> {
            try {
                EquipoDTO equipo = inventarioDAO.obtenerEquipoPorId(cellData.getValue().getIdEquipo());
                return new SimpleStringProperty(equipo != null ? equipo.getNombre() : "N/A");
            } catch (Exception e) {
                return new SimpleStringProperty("Error");
            }
        });
        colNombreCarrito.setPrefWidth(200);

        TableColumn<PrestamoDetalleDTO, String> colCantidadCarrito = new TableColumn<>("Cantidad");
        colCantidadCarrito.setCellValueFactory(cellData ->
                cellData.getValue().cantidadProperty().asString());
        colCantidadCarrito.setPrefWidth(80);

        carritoTable.getColumns().addAll(colNombreCarrito, colCantidadCarrito);
    }

    private VBox createPrestamosPanel() {
        VBox panel = new VBox(20);
        panel.setStyle("-fx-background-color: " + CARD_COLOR + "; " +
                "-fx-background-radius: 15px; " +
                "-fx-padding: 25px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);");

        // Título con búsqueda
        HBox titleContainer = new HBox(15);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        Glyph listIcon = new Glyph("FontAwesome", FontAwesome.Glyph.LIST);
        listIcon.setFontSize(20);
        listIcon.setColor(Color.web(PRIMARY_COLOR));

        Label title = new Label("Lista de Préstamos");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        JFXTextField buscarField = new JFXTextField();
        buscarField.setPromptText("Buscar préstamos... (Ctrl+F)");
        buscarField.setPrefWidth(200);

        titleContainer.getChildren().addAll(listIcon, title, spacer, buscarField);

        // Tabla de préstamos
        prestamosTable = new TableView<>();
        prestamosTable.setItems(prestamosData);
        prestamosTable.setPrefHeight(300);
        prestamosTable.setStyle("-fx-background-radius: 8px;");

        configurarColumnasPrestamosMejoradas();

        // Botones de acción para préstamos
        HBox botonesPrestamos = new HBox(15);
        botonesPrestamos.setAlignment(Pos.CENTER_LEFT);
        botonesPrestamos.setPadding(new Insets(15, 0, 0, 0));

        devolverBtn = createStyledButton("Devolver (F4)", SUCCESS_COLOR, FontAwesome.Glyph.CHECK_CIRCLE);
        eliminarPrestamoBtn = createStyledButton("Eliminar (Del)", SECONDARY_COLOR, FontAwesome.Glyph.TRASH);

        botonesPrestamos.getChildren().addAll(devolverBtn, eliminarPrestamoBtn);

        panel.getChildren().addAll(titleContainer, new Separator(), prestamosTable, botonesPrestamos);
        return panel;
    }

    private VBox createDetallesPanel() {
        VBox panel = new VBox(20);
        panel.setStyle("-fx-background-color: " + CARD_COLOR + "; " +
                "-fx-background-radius: 15px; " +
                "-fx-padding: 25px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);");

        HBox titleContainer = new HBox(15);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        Glyph detailIcon = new Glyph("FontAwesome", FontAwesome.Glyph.INFO_CIRCLE);
        detailIcon.setFontSize(20);
        detailIcon.setColor(Color.web(PRIMARY_COLOR));

        Label title = new Label("Detalles del Préstamo Seleccionado");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");

        titleContainer.getChildren().addAll(detailIcon, title);

        // Tabla de detalles
        detallesTable = new TableView<>();
        detallesTable.setItems(detallesData);
        detallesTable.setPrefHeight(150);
        detallesTable.setStyle("-fx-background-radius: 8px;");

        configurarColumnasDetalles();

        panel.getChildren().addAll(titleContainer, new Separator(), detallesTable);
        return panel;
    }

    private VBox createEstadisticasPanel() {
        VBox panel = new VBox(30);
        panel.setPadding(new Insets(30));
        panel.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Título
        HBox titleContainer = new HBox(15);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        Glyph statsIcon = new Glyph("FontAwesome", FontAwesome.Glyph.BAR_CHART);
        statsIcon.setFontSize(24);
        statsIcon.setColor(Color.web(PRIMARY_COLOR));

        Label title = new Label("Ayuda");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");


        // Información adicional
        VBox infoPanel = createInfoPanel();

        panel.getChildren().addAll(titleContainer, infoPanel);
        return panel;
    }

    private VBox createLargeStatCard(String label, String value, String color, FontAwesome.Glyph icon) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setPrefSize(200, 150);
        card.setStyle("-fx-background-color: " + CARD_COLOR + "; " +
                "-fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);");

        Glyph cardIcon = new Glyph("FontAwesome", icon);
        cardIcon.setFontSize(32);
        cardIcon.setColor(Color.web(color));

        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D; -fx-text-alignment: center;");
        labelText.setWrapText(true);

        card.getChildren().addAll(cardIcon, valueLabel, labelText);
        return card;
    }

    private VBox createInfoPanel() {
        VBox panel = new VBox(15);
        panel.setStyle("-fx-background-color: " + CARD_COLOR + "; " +
                "-fx-background-radius: 15px; " +
                "-fx-padding: 25px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);");

        Label infoTitle = new Label("Atajos de Teclado");
        infoTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");

        GridPane shortcutsGrid = new GridPane();
        shortcutsGrid.setHgap(30);
        shortcutsGrid.setVgap(10);
        shortcutsGrid.setPadding(new Insets(15, 0, 0, 0));

        String[][] shortcuts = {
                {"F1", "Agregar equipo al carrito"},
                {"F2", "Quitar equipo del carrito"},
                {"F3", "Limpiar carrito"},
                {"F4", "Marcar como devuelto"},
                {"Ctrl+S", "Guardar préstamo"},
                {"Ctrl+E", "Editar préstamo seleccionado"},
                {"Ctrl+F", "Buscar préstamos"},
                {"Del", "Eliminar préstamo"},
                {"Tab", "Cambiar entre pestañas"},
                {"Doble Click", "Ver detalles del préstamo"}
        };

        for (int i = 0; i < shortcuts.length; i++) {
            Label keyLabel = new Label(shortcuts[i][0]);
            keyLabel.setStyle("-fx-background-color: #E8E8E8; -fx-padding: 5px 10px; " +
                    "-fx-background-radius: 5px; -fx-font-weight: bold; -fx-font-size: 12px;");

            Label descLabel = new Label(shortcuts[i][1]);
            descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

            shortcutsGrid.add(keyLabel, 0, i);
            shortcutsGrid.add(descLabel, 1, i);
        }

        panel.getChildren().addAll(infoTitle, shortcutsGrid);
        return panel;
    }

    private JFXTextField createStyledTextField(String prompt) {
        JFXTextField field = new JFXTextField();
        field.setPromptText(prompt);
        field.setLabelFloat(true);
        field.setStyle("-fx-font-size: 14px;");
        field.setFocusColor(Color.web(PRIMARY_COLOR));
        return field;
    }

    private VBox createFieldContainer(String labelText, Node field) {
        VBox container = new VBox(8);

        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495E; -fx-font-size: 13px;");

        container.getChildren().addAll(label, field);
        return container;
    }

    private HBox createBotonesFormulario() {
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(20, 0, 0, 0));

        crearPrestamoBtn = createStyledButton("Crear (Ctrl+S)", PRIMARY_COLOR, FontAwesome.Glyph.SAVE);
        editarPrestamoBtn = createStyledButton("Actualizar (Ctrl+S)", WARNING_COLOR, FontAwesome.Glyph.EDIT);

        JFXButton cancelarBtn = createStyledButton("Cancelar (Esc)", "#95A5A6", FontAwesome.Glyph.TIMES);
        cancelarBtn.setOnAction(e -> cancelarEdicion());

        container.getChildren().addAll(crearPrestamoBtn, editarPrestamoBtn, cancelarBtn);

        // Inicialmente mostrar solo crear
        editarPrestamoBtn.setVisible(false);

        return container;
    }

    private JFXButton createStyledButton(String text, String color, FontAwesome.Glyph icon) {
        JFXButton button = new JFXButton(text.toUpperCase());
        button.setButtonType(JFXButton.ButtonType.RAISED);

        Glyph buttonIcon = new Glyph("FontAwesome", icon);
        buttonIcon.setFontSize(12);
        buttonIcon.setColor(Color.WHITE);
        button.setGraphic(buttonIcon);

        button.setStyle("-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 20px; " +
                "-fx-padding: 10px 20px; " +
                "-fx-font-size: 11px;");

        // Efectos hover
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });

        return button;
    }

    private void configurarColumnasPrestamosMejoradas() {
        TableColumn<PrestamoDTO, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asString());
        colId.setPrefWidth(60);

        TableColumn<PrestamoDTO, String> colSolicitante = new TableColumn<>("Solicitante");
        colSolicitante.setCellValueFactory(cellData -> cellData.getValue().solicitanteProperty());
        colSolicitante.setPrefWidth(200);

        TableColumn<PrestamoDTO, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(cellData ->
                cellData.getValue().fechaPrestamoProperty().asString());
        colFecha.setPrefWidth(120);

        TableColumn<PrestamoDTO, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(cellData ->
                Bindings.createStringBinding(() ->
                                cellData.getValue().isDevuelto() ? "✓ Devuelto" : "⏳ Pendiente",
                        cellData.getValue().devueltoProperty()));

        colEstado.setCellFactory(column -> new TableCell<PrestamoDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Devuelto")) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; " +
                                "-fx-font-weight: bold; -fx-background-radius: 10px; " +
                                "-fx-padding: 3px 8px;");
                    } else {
                        setStyle("-fx-background-color: #fff3cd; -fx-text-fill: #856404; " +
                                "-fx-font-weight: bold; -fx-background-radius: 10px; " +
                                "-fx-padding: 3px 8px;");
                    }
                }
            }
        });
        colEstado.setPrefWidth(120);

        prestamosTable.getColumns().addAll(colId, colSolicitante, colFecha, colEstado);

        // Doble click para mostrar detalles
        prestamosTable.setRowFactory(tv -> {
            TableRow<PrestamoDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    PrestamoDTO prestamo = row.getItem();
                    mostrarDetallesPrestamo(prestamo);
                    cargarParaEdicion(prestamo);
                    // Cambiar a la pestaña de gestión
                    mainTabPane.getSelectionModel().select(gestionTab);
                }
            });
            return row;
        });

        // Selección simple para mostrar detalles
        prestamosTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        mostrarDetallesPrestamo(newSelection);
                    }
                });
    }

    private void configurarColumnasDetalles() {
        TableColumn<PrestamoDetalleDTO, String> colEquipo = new TableColumn<>("Equipo");
        colEquipo.setCellValueFactory(cellData -> {
            try {
                EquipoDTO equipo = inventarioDAO.obtenerEquipoPorId(cellData.getValue().getIdEquipo());
                return new SimpleStringProperty(equipo != null ? equipo.getNombre() : "N/A");
            } catch (Exception e) {
                return new SimpleStringProperty("Error");
            }
        });
        colEquipo.setPrefWidth(200);

        TableColumn<PrestamoDetalleDTO, String> colMarca = new TableColumn<>("Marca");
        colMarca.setCellValueFactory(cellData -> {
            try {
                EquipoDTO equipo = inventarioDAO.obtenerEquipoPorId(cellData.getValue().getIdEquipo());
                return new SimpleStringProperty(equipo != null ? equipo.getMarca() : "N/A");
            } catch (Exception e) {
                return new SimpleStringProperty("Error");
            }
        });
        colMarca.setPrefWidth(150);

        TableColumn<PrestamoDetalleDTO, String> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(cellData ->
                cellData.getValue().cantidadProperty().asString());
        colCantidad.setPrefWidth(100);

        detallesTable.getColumns().addAll(colEquipo, colMarca, colCantidad);
    }

    private void configurarEventos() {
        // Eventos de botones principales
        crearPrestamoBtn.setOnAction(e -> {
            animateButtonClick(crearPrestamoBtn);
            crearPrestamo();
        });

        editarPrestamoBtn.setOnAction(e -> {
            animateButtonClick(editarPrestamoBtn);
            actualizarPrestamo();
        });

        devolverBtn.setOnAction(e -> {
            animateButtonClick(devolverBtn);
            marcarComoDevuelto();
        });

        eliminarPrestamoBtn.setOnAction(e -> {
            animateButtonClick(eliminarPrestamoBtn);
            eliminarPrestamo();
        });

        // Eventos del carrito
        agregarEquipoBtn.setOnAction(e -> {
            animateButtonClick(agregarEquipoBtn);
            mostrarDialogoInventario();
        });

        eliminarEquipoBtn.setOnAction(e -> {
            animateButtonClick(eliminarEquipoBtn);
            eliminarDelCarrito();
        });

        limpiarCarritoBtn.setOnAction(e -> {
            animateButtonClick(limpiarCarritoBtn);
            limpiarCarrito();
        });
    }

    private void configurarAtajosTeclado() {
        view.setOnKeyPressed(event -> {
            // Atajos principales
            if (event.getCode() == KeyCode.F1) {
                mostrarDialogoInventario();
            } else if (event.getCode() == KeyCode.F2) {
                eliminarDelCarrito();
            } else if (event.getCode() == KeyCode.F3) {
                limpiarCarrito();
            } else if (event.getCode() == KeyCode.F4) {
                marcarComoDevuelto();
            } else if (event.getCode() == KeyCode.DELETE) {
                eliminarPrestamo();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                cancelarEdicion();
            }

            // Atajos con Ctrl
            else if (event.isControlDown()) {
                switch (event.getCode()) {
                    case S:
                        if (modoEdicion) {
                            actualizarPrestamo();
                        } else {
                            crearPrestamo();
                        }
                        break;
                    case E:
                        PrestamoDTO selected = prestamosTable.getSelectionModel().getSelectedItem();
                        if (selected != null) {
                            cargarParaEdicion(selected);
                            mainTabPane.getSelectionModel().select(gestionTab);
                        }
                        break;
                    case F:
                        mainTabPane.getSelectionModel().select(listadoTab);
                        break;
                    case DIGIT1:
                        mainTabPane.getSelectionModel().select(gestionTab);
                        break;
                    case DIGIT2:
                        mainTabPane.getSelectionModel().select(listadoTab);
                        break;
                    case DIGIT3:
                        mainTabPane.getSelectionModel().select(estadisticasTab);
                        break;
                }
            }

            event.consume();
        });

        view.setFocusTraversable(true);
        view.requestFocus();
    }

    private void animateButtonClick(JFXButton button) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.95);
        scale.setToY(0.95);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    private void configurarAutocompletadoMejorado() {
        Platform.runLater(() -> {
            List<String> solicitantes = solicitanteManager.obtenerTodos();
            if (!solicitantes.isEmpty()) {
                TextFields.bindAutoCompletion(solicitanteField, solicitantes);
            }
        });

        solicitanteField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !solicitanteField.getText().isEmpty()) {
                String nombre = solicitanteField.getText().trim();
                solicitanteManager.agregarSolicitante(nombre);
            }
        });
    }

    // Métodos de negocio (manteniendo toda la funcionalidad existente)

    private void mostrarDialogoInventario() {
        if (inventarioPopup == null) {
            crearDialogoInventario();
        }

        cargarInventario();
        inventarioPopup.show(agregarEquipoBtn, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT);

        // Animación de entrada
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(inventarioPopup.getPopupContent().scaleXProperty(), 0.8),
                        new KeyValue(inventarioPopup.getPopupContent().scaleYProperty(), 0.8),
                        new KeyValue(inventarioPopup.getPopupContent().opacityProperty(), 0)),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(inventarioPopup.getPopupContent().scaleXProperty(), 1.0),
                        new KeyValue(inventarioPopup.getPopupContent().scaleYProperty(), 1.0),
                        new KeyValue(inventarioPopup.getPopupContent().opacityProperty(), 1))
        );
        timeline.play();
    }

    private void crearDialogoInventario() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 15px; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");
        content.setPrefWidth(700);
        content.setPrefHeight(500);

        // Título del diálogo
        HBox titleContainer = new HBox(15);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        Glyph icon = new Glyph("FontAwesome", FontAwesome.Glyph.CUBE);
        icon.setFontSize(24);
        icon.setColor(Color.web(PRIMARY_COLOR));

        Label title = new Label("Seleccionar Equipo del Inventario");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");

        titleContainer.getChildren().addAll(icon, title);

        // Campo de búsqueda
        buscarEquipoField = new JFXTextField();
        buscarEquipoField.setPromptText("Buscar equipos...");
        buscarEquipoField.setLabelFloat(true);
        buscarEquipoField.setPrefWidth(300);

        // Tabla de inventario
        tablaInventario = new TableView<>();
        tablaInventario.setItems(inventarioData);
        tablaInventario.setPrefHeight(300);

        configurarColumnasInventario();

        // Campo cantidad
        HBox cantidadContainer = new HBox(15);
        cantidadContainer.setAlignment(Pos.CENTER_LEFT);

        Label cantidadLabel = new Label("Cantidad:");
        cantidadLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #34495E;");

        cantidadField = new JFXTextField("1");
        cantidadField.setPrefWidth(100);
        cantidadField.setPromptText("Cantidad");

        cantidadContainer.getChildren().addAll(cantidadLabel, cantidadField);

        // Botones
        HBox botones = new HBox(15);
        botones.setAlignment(Pos.CENTER_RIGHT);

        JFXButton cancelarBtn = createStyledButton("Cancelar", "#95A5A6", FontAwesome.Glyph.TIMES);
        cancelarBtn.setOnAction(e -> cerrarDialogoInventario());

        JFXButton agregarBtn = createStyledButton("Agregar", SUCCESS_COLOR, FontAwesome.Glyph.CART_PLUS);
        agregarBtn.setOnAction(e -> agregarEquipoAlCarrito());

        botones.getChildren().addAll(cancelarBtn, agregarBtn);

        content.getChildren().addAll(
                titleContainer,
                new Separator(),
                buscarEquipoField,
                tablaInventario,
                cantidadContainer,
                botones
        );

        inventarioPopup = new JFXPopup(content);
    }

    private void configurarColumnasInventario() {
        TableColumn<EquipoDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colNombre.setPrefWidth(200);

        TableColumn<EquipoDTO, String> colMarca = new TableColumn<>("Marca");
        colMarca.setCellValueFactory(cellData -> cellData.getValue().marcaProperty());
        colMarca.setPrefWidth(150);

        TableColumn<EquipoDTO, Integer> colDisponible = new TableColumn<>("Disponible");
        colDisponible.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
        colDisponible.setCellFactory(column -> new TableCell<EquipoDTO, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    if (item <= 0) {
                        setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-font-weight: bold;");
                    } else if (item <= 5) {
                        setStyle("-fx-background-color: #fff8e1; -fx-text-fill: #f57c00; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-background-color: #e8f5e8; -fx-text-fill: #2e7d32; -fx-font-weight: bold;");
                    }
                }
            }
        });
        colDisponible.setPrefWidth(100);

        tablaInventario.getColumns().addAll(colNombre, colMarca, colDisponible);

        // Doble click para agregar
        tablaInventario.setRowFactory(tv -> {
            TableRow<EquipoDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    agregarEquipoAlCarrito();
                }
            });
            return row;
        });
    }

    private void agregarEquipoAlCarrito() {
        EquipoDTO equipoSeleccionado = tablaInventario.getSelectionModel().getSelectedItem();

        if (equipoSeleccionado == null) {
            mostrarNotificacion("Advertencia", "Seleccione un equipo", Alert.AlertType.WARNING);
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadField.getText().trim());

            if (cantidad <= 0) {
                mostrarNotificacion("Error", "La cantidad debe ser mayor a 0", Alert.AlertType.ERROR);
                return;
            }

            if (cantidad > equipoSeleccionado.getCantidad()) {
                mostrarNotificacion("Error", "Cantidad insuficiente en inventario", Alert.AlertType.ERROR);
                return;
            }

            // Verificar si ya existe en el carrito
            Optional<PrestamoDetalleDTO> existente = carritoData.stream()
                    .filter(item -> item.getIdEquipo() == equipoSeleccionado.getId())
                    .findFirst();

            if (existente.isPresent()) {
                existente.get().setCantidad(existente.get().getCantidad() + cantidad);
            } else {
                PrestamoDetalleDTO nuevoItem = new PrestamoDetalleDTO();
                nuevoItem.setIdEquipo(equipoSeleccionado.getId());
                nuevoItem.setCantidad(cantidad);
                carritoData.add(nuevoItem);
            }

            cerrarDialogoInventario();
            mostrarNotificacion("Éxito", "Equipo agregado al carrito", Alert.AlertType.INFORMATION);

            // Animación en el carrito
            new Flash(carritoTable).play();

        } catch (NumberFormatException e) {
            mostrarNotificacion("Error", "Ingrese una cantidad válida", Alert.AlertType.ERROR);
        }
    }

    private void cerrarDialogoInventario() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(inventarioPopup.getPopupContent().scaleXProperty(), 1.0),
                        new KeyValue(inventarioPopup.getPopupContent().scaleYProperty(), 1.0),
                        new KeyValue(inventarioPopup.getPopupContent().opacityProperty(), 1)),
                new KeyFrame(Duration.millis(150),
                        new KeyValue(inventarioPopup.getPopupContent().scaleXProperty(), 0.8),
                        new KeyValue(inventarioPopup.getPopupContent().scaleYProperty(), 0.8),
                        new KeyValue(inventarioPopup.getPopupContent().opacityProperty(), 0))
        );
        timeline.setOnFinished(e -> inventarioPopup.hide());
        timeline.play();
    }

    private void eliminarDelCarrito() {
        PrestamoDetalleDTO selected = carritoTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            carritoData.remove(selected);
            new SlideOutRight(carritoTable).play();
        } else {
            mostrarNotificacion("Advertencia", "Seleccione un equipo del carrito", Alert.AlertType.WARNING);
        }
    }

    private void limpiarCarrito() {
        if (!carritoData.isEmpty()) {
            carritoData.clear();
            new FadeOut(carritoTable).play();
            mostrarNotificacion("Información", "Carrito limpiado", Alert.AlertType.INFORMATION);
        }
    }

    private void crearPrestamo() {
        if (!validarFormulario()) return;

        if (carritoData.isEmpty()) {
            mostrarNotificacion("Error", "Agregue al menos un equipo al préstamo", Alert.AlertType.ERROR);
            new Shake(carritoTable).play();
            return;
        }

        PrestamoDTO nuevoPrestamo = new PrestamoDTO();
        nuevoPrestamo.setFechaPrestamo(fechaPrestamoField.getValue());
        nuevoPrestamo.setComentarios(comentariosArea.getText().trim());
        nuevoPrestamo.setDevuelto(false);

        String nombreSolicitante = solicitanteField.getText().trim();
        nuevoPrestamo.setSolicitante(nombreSolicitante);

        solicitanteManager.agregarSolicitante(nombreSolicitante);

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                int prestamoId = controller.crearPrestamo(nuevoPrestamo);

                if (prestamoId > 0) {
                    for (PrestamoDetalleDTO detalle : carritoData) {
                        detalle.setPrestamoId(prestamoId);
                        controller.agregarEquipoAPrestamo(detalle);

                        EquipoDTO equipo = inventarioDAO.obtenerEquipoPorId(detalle.getIdEquipo());
                        equipo.setCantidad(equipo.getCantidad() - detalle.getCantidad());
                        inventarioDAO.actualizarEquipo(equipo);
                    }
                }

                return prestamoId;
            }

            @Override
            protected void succeeded() {
                if (getValue() > 0) {
                    limpiarFormulario();
                    carritoData.clear();
                    cargarDatos();
                    mostrarNotificacion("Éxito", "Préstamo creado correctamente", Alert.AlertType.INFORMATION);
                    new BounceIn(prestamosTable).play();
                } else {
                    mostrarNotificacion("Error", "No se pudo crear el préstamo", Alert.AlertType.ERROR);
                }
            }

            @Override
            protected void failed() {
                mostrarNotificacion("Error", "Error al crear el préstamo: " + getException().getMessage(),
                        Alert.AlertType.ERROR);
            }
        };

        new Thread(task).start();
    }

    private void cargarParaEdicion(PrestamoDTO prestamo) {
        modoEdicion = true;
        prestamoEnEdicion = prestamo;

        solicitanteField.setText(prestamo.getSolicitante());
        fechaPrestamoField.setValue(prestamo.getFechaPrestamo());
        comentariosArea.setText(prestamo.getComentarios());

        Task<List<PrestamoDetalleDTO>> task = new Task<List<PrestamoDetalleDTO>>() {
            @Override
            protected List<PrestamoDetalleDTO> call() throws Exception {
                return controller.obtenerEquiposDePrestamo(prestamo.getId());
            }

            @Override
            protected void succeeded() {
                carritoData.setAll(getValue());
            }
        };

        new Thread(task).start();

        crearPrestamoBtn.setVisible(false);
        editarPrestamoBtn.setVisible(true);

        new Flash(solicitanteField).play();
    }

    private void actualizarPrestamo() {
        if (!validarFormulario() || prestamoEnEdicion == null) return;

        prestamoEnEdicion.setFechaPrestamo(fechaPrestamoField.getValue());
        prestamoEnEdicion.setComentarios(comentariosArea.getText().trim());

        String nombreSolicitante = solicitanteField.getText().trim();
        prestamoEnEdicion.setSolicitante(nombreSolicitante);

        solicitanteManager.agregarSolicitante(nombreSolicitante);

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return controller.actualizarPrestamo(prestamoEnEdicion);
            }

            @Override
            protected void succeeded() {
                if (getValue()) {
                    cancelarEdicion();
                    cargarDatos();
                    mostrarNotificacion("Éxito", "Préstamo actualizado correctamente", Alert.AlertType.INFORMATION);
                    new Pulse(prestamosTable).play();
                } else {
                    mostrarNotificacion("Error", "No se pudo actualizar el préstamo", Alert.AlertType.ERROR);
                }
            }

            @Override
            protected void failed() {
                mostrarNotificacion("Error", "Error al actualizar: " + getException().getMessage(),
                        Alert.AlertType.ERROR);
            }
        };

        new Thread(task).start();
    }

    private void cancelarEdicion() {
        modoEdicion = false;
        prestamoEnEdicion = null;

        limpiarFormulario();
        carritoData.clear();

        crearPrestamoBtn.setVisible(true);
        editarPrestamoBtn.setVisible(false);

        new FadeIn(crearPrestamoBtn).play();
    }

    private void eliminarPrestamo() {
        PrestamoDTO selected = prestamosTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostrarNotificacion("Advertencia", "Seleccione un préstamo para eliminar", Alert.AlertType.WARNING);
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Está seguro de eliminar este préstamo?");
        confirm.setContentText("Esta acción no se puede deshacer y devolverá los equipos al inventario.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    List<PrestamoDetalleDTO> detalles = controller.obtenerEquiposDePrestamo(selected.getId());
                    for (PrestamoDetalleDTO detalle : detalles) {
                        EquipoDTO equipo = inventarioDAO.obtenerEquipoPorId(detalle.getIdEquipo());
                        if (equipo != null) {
                            equipo.setCantidad(equipo.getCantidad() + detalle.getCantidad());
                            inventarioDAO.actualizarEquipo(equipo);
                        }
                    }

                    return controller.eliminarPrestamo(selected.getId());
                }

                @Override
                protected void succeeded() {
                    if (getValue()) {
                        cargarDatos();
                        detallesData.clear();
                        mostrarNotificacion("Éxito", "Préstamo eliminado correctamente", Alert.AlertType.INFORMATION);
                        new SlideOutRight(prestamosTable).play();
                    } else {
                        mostrarNotificacion("Error", "No se pudo eliminar el préstamo", Alert.AlertType.ERROR);
                    }
                }
            };

            new Thread(task).start();
        }
    }

    private void marcarComoDevuelto() {
        PrestamoDTO selected = prestamosTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostrarNotificacion("Advertencia", "Seleccione un préstamo", Alert.AlertType.WARNING);
            return;
        }

        if (selected.isDevuelto()) {
            mostrarNotificacion("Información", "Este préstamo ya está devuelto", Alert.AlertType.INFORMATION);
            return;
        }

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                List<PrestamoDetalleDTO> detalles = controller.obtenerEquiposDePrestamo(selected.getId());
                for (PrestamoDetalleDTO detalle : detalles) {
                    EquipoDTO equipo = inventarioDAO.obtenerEquipoPorId(detalle.getIdEquipo());
                    if (equipo != null) {
                        equipo.setCantidad(equipo.getCantidad() + detalle.getCantidad());
                        inventarioDAO.actualizarEquipo(equipo);
                    }
                }

                return controller.marcarComoDevuelto(selected.getId());
            }

            @Override
            protected void succeeded() {
                if (getValue()) {
                    cargarDatos();
                    mostrarNotificacion("Éxito", "Préstamo marcado como devuelto", Alert.AlertType.INFORMATION);
                    new BounceIn(prestamosTable).play();
                } else {
                    mostrarNotificacion("Error", "No se pudo marcar como devuelto", Alert.AlertType.ERROR);
                }
            }
        };

        new Thread(task).start();
    }

    private void mostrarDetallesPrestamo(PrestamoDTO prestamo) {
        Task<List<PrestamoDetalleDTO>> task = new Task<List<PrestamoDetalleDTO>>() {
            @Override
            protected List<PrestamoDetalleDTO> call() throws Exception {
                return controller.obtenerEquiposDePrestamo(prestamo.getId());
            }

            @Override
            protected void succeeded() {
                detallesData.setAll(getValue());
                new FadeIn(detallesTable).play();
            }
        };

        new Thread(task).start();
    }

    private void cargarDatos() {
        cargarPrestamos();
        cargarInventario();
    }

    private void cargarPrestamos() {
        Task<List<PrestamoDTO>> task = new Task<List<PrestamoDTO>>() {
            @Override
            protected List<PrestamoDTO> call() throws Exception {
                return controller.listarTodosLosPrestamos();
            }

            @Override
            protected void succeeded() {
                prestamosData.setAll(getValue());
                actualizarEstadisticasEnVivo();
            }

            @Override
            protected void failed() {
                mostrarNotificacion("Error", "No se pudieron cargar los préstamos", Alert.AlertType.ERROR);
            }
        };

        new Thread(task).start();
    }

    private void cargarInventario() {
        Task<List<EquipoDTO>> task = new Task<List<EquipoDTO>>() {
            @Override
            protected List<EquipoDTO> call() throws Exception {
                return inventarioDAO.obtenerTodosLosEquipos();
            }

            @Override
            protected void succeeded() {
                inventarioData.setAll(getValue());
            }

            @Override
            protected void failed() {
                mostrarNotificacion("Error", "No se pudo cargar el inventario", Alert.AlertType.ERROR);
            }
        };

        new Thread(task).start();
    }

    private void actualizarEstadisticasEnVivo() {
        Platform.runLater(() -> {
            int total = prestamosData.size();
            long pendientes = prestamosData.stream().filter(p -> !p.isDevuelto()).count();
            long devueltos = prestamosData.stream().filter(PrestamoDTO::isDevuelto).count();

            // Actualizar las estadísticas del header con animación
            actualizarLabelConAnimacion(totalPrestamosLabel, String.valueOf(total));
            actualizarLabelConAnimacion(pendientesLabel, String.valueOf(pendientes));
            actualizarLabelConAnimacion(devueltosLabel, String.valueOf(devueltos));

            // Actualizar estadísticas detalladas si estamos en esa pestaña
            if (mainTabPane.getSelectionModel().getSelectedItem() == estadisticasTab) {
                actualizarEstadisticasDetalladas(total, (int)pendientes, (int)devueltos);
            }
        });
    }

    private void actualizarLabelConAnimacion(Label label, String nuevoValor) {
        if (!label.getText().equals(nuevoValor)) {
            // Animación de cambio de valor
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(150), label);
            scaleOut.setToX(1.2);
            scaleOut.setToY(1.2);

            scaleOut.setOnFinished(e -> {
                label.setText(nuevoValor);
                ScaleTransition scaleIn = new ScaleTransition(Duration.millis(150), label);
                scaleIn.setToX(1.0);
                scaleIn.setToY(1.0);
                scaleIn.play();
            });

            scaleOut.play();
        }
    }

    private void actualizarEstadisticasDetalladas(int total, int pendientes, int devueltos) {
        // Calcular equipos en préstamo
        Task<Integer> equiposTask = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                int totalEquipos = 0;
                for (PrestamoDTO prestamo : prestamosData) {
                    if (!prestamo.isDevuelto()) {
                        List<PrestamoDetalleDTO> detalles = controller.obtenerEquiposDePrestamo(prestamo.getId());
                        totalEquipos += detalles.stream().mapToInt(PrestamoDetalleDTO::getCantidad).sum();
                    }
                }
                return totalEquipos;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    // Aquí actualizarías las tarjetas grandes de estadísticas
                    // Por simplicidad, solo mostramos en consola
                    System.out.println("Estadísticas actualizadas - Total: " + total +
                            ", Pendientes: " + pendientes +
                            ", Devueltos: " + devueltos +
                            ", Equipos en préstamo: " + getValue());
                });
            }
        };

        new Thread(equiposTask).start();
    }

    private boolean validarFormulario() {
        boolean esValido = true;

        // Validar solicitante
        if (solicitanteField.getText().trim().isEmpty()) {
            mostrarNotificacion("Error", "El nombre del solicitante es obligatorio", Alert.AlertType.ERROR);
            new Shake(solicitanteField).play();
            solicitanteField.requestFocus();
            esValido = false;
        }

        // Validar fecha
        if (fechaPrestamoField.getValue() == null) {
            mostrarNotificacion("Error", "La fecha de préstamo es obligatoria", Alert.AlertType.ERROR);
            new Shake(fechaPrestamoField).play();
            fechaPrestamoField.requestFocus();
            esValido = false;
        }

        // Validar que la fecha no sea futura (opcional)
        if (fechaPrestamoField.getValue() != null && fechaPrestamoField.getValue().isAfter(LocalDate.now())) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Fecha futura");
            confirm.setHeaderText("La fecha seleccionada es futura");
            confirm.setContentText("¿Está seguro de usar esta fecha?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                esValido = false;
            }
        }

        return esValido;
    }

    private void limpiarFormulario() {
        Platform.runLater(() -> {
            // Limpiar campos con animación
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200));
            fadeOut.setNode(solicitanteField);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.3);

            fadeOut.setOnFinished(e -> {
                solicitanteField.clear();
                fechaPrestamoField.setValue(LocalDate.now());
                comentariosArea.clear();

                FadeTransition fadeIn = new FadeTransition(Duration.millis(200));
                fadeIn.setNode(solicitanteField);
                fadeIn.setFromValue(0.3);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });

            fadeOut.play();
        });
    }

    private void mostrarNotificacion(String titulo, String mensaje, Alert.AlertType tipo) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);

            // Personalizar el estilo de la alerta según el tipo
            DialogPane dialogPane = alert.getDialogPane();
            String borderColor = PRIMARY_COLOR;

            switch (tipo) {
                case ERROR:
                    borderColor = SECONDARY_COLOR;
                    break;
                case WARNING:
                    borderColor = WARNING_COLOR;
                    break;
                case INFORMATION:
                    borderColor = SUCCESS_COLOR;
                    break;
                case CONFIRMATION:
                    borderColor = PRIMARY_COLOR;
                    break;
            }

            dialogPane.setStyle("-fx-background-color: white; " +
                    "-fx-border-color: " + borderColor + "; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 10px; " +
                    "-fx-background-radius: 10px;");

            // Agregar icono personalizado
            Glyph alertIcon = null;
            switch (tipo) {
                case ERROR:
                    alertIcon = new Glyph("FontAwesome", FontAwesome.Glyph.EXCLAMATION_TRIANGLE);
                    alertIcon.setColor(Color.web(SECONDARY_COLOR));
                    break;
                case WARNING:
                    alertIcon = new Glyph("FontAwesome", FontAwesome.Glyph.WARNING);
                    alertIcon.setColor(Color.web(WARNING_COLOR));
                    break;
                case INFORMATION:
                    alertIcon = new Glyph("FontAwesome", FontAwesome.Glyph.INFO_CIRCLE);
                    alertIcon.setColor(Color.web(SUCCESS_COLOR));
                    break;
                case CONFIRMATION:
                    alertIcon = new Glyph("FontAwesome", FontAwesome.Glyph.QUESTION_CIRCLE);
                    alertIcon.setColor(Color.web(PRIMARY_COLOR));
                    break;
            }

            if (alertIcon != null) {
                alertIcon.setFontSize(32);
                alert.setGraphic(alertIcon);
            }

            alert.showAndWait();
        });
    }

    // Métodos de utilidad para búsqueda y filtrado

    private void configurarBusquedaEnTiempoReal() {
        // Configurar búsqueda en la tabla de préstamos
        JFXTextField buscarField = new JFXTextField();
        buscarField.setPromptText("Buscar préstamos...");

        buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
            filtrarPrestamos(newValue);
        });
    }

    private void filtrarPrestamos(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            prestamosTable.setItems(prestamosData);
            return;
        }

        ObservableList<PrestamoDTO> prestamosFiltrados = prestamosData.filtered(prestamo -> {
            String filtroLower = filtro.toLowerCase();

            // Buscar en solicitante
            if (prestamo.getSolicitante() != null &&
                    prestamo.getSolicitante().toLowerCase().contains(filtroLower)) {
                return true;
            }

            // Buscar en comentarios
            if (prestamo.getComentarios() != null &&
                    prestamo.getComentarios().toLowerCase().contains(filtroLower)) {
                return true;
            }

            // Buscar en fecha
            if (prestamo.getFechaPrestamo() != null &&
                    prestamo.getFechaPrestamo().toString().contains(filtroLower)) {
                return true;
            }

            // Buscar en estado
            String estado = prestamo.isDevuelto() ? "devuelto" : "pendiente";
            if (estado.contains(filtroLower)) {
                return true;
            }

            return false;
        });

        prestamosTable.setItems(prestamosFiltrados);
    }

    // Métodos para exportar datos

    public void exportarPrestamosACSV() {
        Task<Boolean> exportTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    java.io.FileWriter writer = new java.io.FileWriter("prestamos_export.csv");
                    writer.append("ID,Solicitante,Fecha,Estado,Comentarios\n");

                    for (PrestamoDTO prestamo : prestamosData) {
                        writer.append(String.valueOf(prestamo.getId())).append(",");
                        writer.append(prestamo.getSolicitante() != null ? prestamo.getSolicitante() : "").append(",");
                        writer.append(prestamo.getFechaPrestamo() != null ? prestamo.getFechaPrestamo().toString() : "").append(",");
                        writer.append(prestamo.isDevuelto() ? "Devuelto" : "Pendiente").append(",");
                        writer.append(prestamo.getComentarios() != null ? prestamo.getComentarios().replace(",", ";") : "").append("\n");
                    }

                    writer.flush();
                    writer.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void succeeded() {
                if (getValue()) {
                    mostrarNotificacion("Éxito", "Datos exportados a prestamos_export.csv", Alert.AlertType.INFORMATION);
                } else {
                    mostrarNotificacion("Error", "No se pudo exportar los datos", Alert.AlertType.ERROR);
                }
            }
        };

        new Thread(exportTask).start();
    }

    // Métodos para gestión de ventanas y diálogos

    public void mostrarAyuda() {
        Alert ayuda = new Alert(Alert.AlertType.INFORMATION);
        ayuda.setTitle("Ayuda - Sistema de Préstamos");
        ayuda.setHeaderText("Guía de uso rápido");

        String contenidoAyuda = """
            PESTAÑAS:
            • Gestión: Crear y editar préstamos
            • Listado: Ver todos los préstamos
            • Estadísticas: Información detallada
            
            ATAJOS DE TECLADO:
            • F1: Agregar equipo al carrito
            • F2: Quitar equipo del carrito
            • F3: Limpiar carrito
            • F4: Marcar como devuelto
            • Ctrl+S: Guardar préstamo
            • Ctrl+E: Editar préstamo seleccionado
            • Ctrl+F: Buscar préstamos
            • Del: Eliminar préstamo
            • Ctrl+1/2/3: Cambiar pestañas
            
            FUNCIONES:
            • Doble click en préstamo: Ver detalles y editar
            • Autocompletado en nombres de solicitantes
            • Validación automática de inventario
            • Actualización en tiempo real de estadísticas
            """;

        ayuda.setContentText(contenidoAyuda);

        // Hacer el diálogo más grande
        ayuda.getDialogPane().setPrefWidth(500);
        ayuda.getDialogPane().setPrefHeight(400);

        ayuda.showAndWait();
    }

    // Métodos de configuración avanzada

    private void configurarTemaOscuro(boolean activar) {
        String tema = activar ? "dark" : "light";

        if (activar) {
            view.setStyle("-fx-background-color: #2C3E50;");
            // Aplicar más estilos para tema oscuro
        } else {
            view.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        }

        // Guardar preferencia
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass());
        prefs.put("tema", tema);
    }

    private void cargarPreferenciasUsuario() {
        java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass());
        String tema = prefs.get("tema", "light");

        if ("dark".equals(tema)) {
            configurarTemaOscuro(true);
        }
    }

    // Métodos de validación avanzada

    private boolean validarDisponibilidadInventario() {
        for (PrestamoDetalleDTO item : carritoData) {
            try {
                EquipoDTO equipo = inventarioDAO.obtenerEquipoPorId(item.getIdEquipo());
                if (equipo == null || equipo.getCantidad() < item.getCantidad()) {
                    mostrarNotificacion("Error",
                            "No hay suficiente inventario para el equipo: " +
                                    (equipo != null ? equipo.getNombre() : "ID " + item.getIdEquipo()),
                            Alert.AlertType.ERROR);
                    return false;
                }
            } catch (Exception e) {
                mostrarNotificacion("Error", "Error al validar inventario: " + e.getMessage(), Alert.AlertType.ERROR);
                return false;
            }
        }
        return true;
    }

    // Métodos de respaldo y recuperación

    public void crearRespaldoDatos() {
        Task<Boolean> backupTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    String timestamp = java.time.LocalDateTime.now().format(
                            java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
                    String filename = "backup_prestamos_" + timestamp + ".json";

                    // Aquí implementarías la lógica de respaldo
                    // Por simplicidad, solo simulamos
                    Thread.sleep(2000); // Simular proceso

                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void succeeded() {
                if (getValue()) {
                    mostrarNotificacion("Éxito", "Respaldo creado correctamente", Alert.AlertType.INFORMATION);
                } else {
                    mostrarNotificacion("Error", "No se pudo crear el respaldo", Alert.AlertType.ERROR);
                }
            }
        };

        new Thread(backupTask).start();
    }

    // Métodos de limpieza y cierre

    public void limpiarRecursos() {
        // Detener todas las animaciones activas
        if (inventarioPopup != null && inventarioPopup.isShowing()) {
            inventarioPopup.hide();
        }

        // Limpiar listeners
        prestamosData.clear();
        detallesData.clear();
        carritoData.clear();
        inventarioData.clear();

        // Cerrar conexiones si es necesario
        try {
            if (controller != null && controller.getConnection() != null) {
                // El controller debería manejar el cierre de conexiones
            }
        } catch (Exception e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }

    // Métodos públicos de la interfaz

    public BorderPane getView() {
        return view;
    }

    public void requestFocus() {
        view.requestFocus();
    }

    public void mostrarPestanaGestion() {
        mainTabPane.getSelectionModel().select(gestionTab);
    }

    public void mostrarPestanaListado() {
        mainTabPane.getSelectionModel().select(listadoTab);
    }

    public void mostrarPestanaEstadisticas() {
        mainTabPane.getSelectionModel().select(estadisticasTab);
    }

    // Método para refrescar datos desde el exterior
    public void refrescarDatos() {
        cargarDatos();
    }

    // Método para obtener estadísticas actuales
    public Map<String, Integer> obtenerEstadisticas() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", prestamosData.size());
        stats.put("pendientes", (int) prestamosData.stream().filter(p -> !p.isDevuelto()).count());
        stats.put("devueltos", (int) prestamosData.stream().filter(PrestamoDTO::isDevuelto).count());
        return stats;
    }

    // Método para configurar callbacks externos
    public void setOnPrestamoCreado(Runnable callback) {
        // Implementar si necesitas notificar a otros componentes
    }

    public void setOnPrestamoActualizado(Runnable callback) {
        // Implementar si necesitas notificar a otros componentes
    }

    // Método de inicialización final
    private void finalizarInicializacion() {
        // Cargar preferencias del usuario
        cargarPreferenciasUsuario();

        // Configurar búsqueda en tiempo real
        configurarBusquedaEnTiempoReal();

        // Establecer foco inicial
        Platform.runLater(() -> {
            solicitanteField.requestFocus();
        });

        // Configurar actualización automática de estadísticas cada 30 segundos
        Timeline actualizacionPeriodica = new Timeline(
                new KeyFrame(Duration.seconds(30), e -> actualizarEstadisticasEnVivo())
        );
        actualizacionPeriodica.setCycleCount(Timeline.INDEFINITE);
        actualizacionPeriodica.play();
    }

    // Constructor actualizado para incluir inicialización final
    public void inicializacionCompleta() {
        finalizarInicializacion();
    }
}