package tescha.prestamos.view;

import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import animatefx.animation.*;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import tescha.prestamos.controller.PrestamoController;
import tescha.prestamos.dto.PrestamoDTO;
import tescha.prestamos.service.PrestamoService;
import tescha.Components.AlertUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Vista principal del módulo de préstamos con diseño minimalista y elegante
 */
public class PrestamoView {

    private final PrestamoController controller;

    // Sistema de colores minimalista y elegante
    private final String PRIMARY_COLOR = "#2196F3";      // Azul Material
    private final String SECONDARY_COLOR = "#FF4081";    // Rosa Accent
    private final String SUCCESS_COLOR = "#4CAF50";      // Verde Success
    private final String WARNING_COLOR = "#FF9800";      // Naranja Warning
    private final String ERROR_COLOR = "#F44336";        // Rojo Error
    private final String INFO_COLOR = "#00BCD4";         // Cyan Info
    private final String BACKGROUND_COLOR = "#FAFAFA";   // Gris muy claro
    private final String CARD_COLOR = "#FFFFFF";         // Blanco puro
    private final String TEXT_PRIMARY = "#212121";       // Gris oscuro
    private final String TEXT_SECONDARY = "#757575";     // Gris medio
    private final String SURFACE_COLOR = "#F5F5F5";      // Gris superficie
    private final String ACCENT_COLOR = "#E3F2FD";       // Azul muy claro

    // Componentes principales
    private BorderPane mainContainer;
    private VBox contentContainer;
    private TableView<PrestamoDTO> prestamosTable;
    private ObservableList<PrestamoDTO> prestamosData;
    private JFXTextField searchField;
    private JFXComboBox<String> filterComboBox;
    private Label totalPrestamosLabel;
    private Label prestamosActivosLabel;
    private Label prestamosVencidosLabel;
    private Label prestamosDevueltosLabel;

    // Botones de acción
    private JFXButton nuevoPrestamoButton;
    private JFXButton editarPrestamoButton;
    private JFXButton eliminarPrestamoButton;
    private JFXButton procesarDevolucionButton;
    private JFXButton verDetallesButton;
    private JFXButton actualizarButton;

    public PrestamoView(PrestamoController controller) {
        this.controller = controller;
        this.prestamosData = FXCollections.observableArrayList();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadData();
        setupAutoRefresh();
    }

    private void initializeComponents() {
        mainContainer = new BorderPane();
        mainContainer.getStyleClass().add("prestamos-container");
        mainContainer.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(25));

        // Inicializar tabla
        prestamosTable = createPrestamosTable();

        // Inicializar controles
        searchField = new JFXTextField();
        filterComboBox = new JFXComboBox<>();

        // Inicializar labels de estadísticas
        totalPrestamosLabel = new Label("0");
        prestamosActivosLabel = new Label("0");
        prestamosVencidosLabel = new Label("0");
        prestamosDevueltosLabel = new Label("0");

        // Inicializar botones
        initializeButtons();
    }

    private void initializeButtons() {
        nuevoPrestamoButton = createElegantButton("Nuevo Préstamo", FontAwesomeSolid.PLUS, PRIMARY_COLOR);
        editarPrestamoButton = createElegantButton("Editar", FontAwesomeSolid.EDIT, INFO_COLOR);
        eliminarPrestamoButton = createElegantButton("Eliminar", FontAwesomeSolid.TRASH, ERROR_COLOR);
        procesarDevolucionButton = createElegantButton("Procesar Devolución", FontAwesomeSolid.UNDO, SUCCESS_COLOR);
        verDetallesButton = createElegantButton("Ver Detalles", FontAwesomeSolid.EYE, TEXT_SECONDARY);
        actualizarButton = createIconButton(FontAwesomeSolid.SYNC_ALT, PRIMARY_COLOR, "Actualizar");

        // Deshabilitar botones que requieren selección
        editarPrestamoButton.setDisable(true);
        eliminarPrestamoButton.setDisable(true);
        procesarDevolucionButton.setDisable(true);
        verDetallesButton.setDisable(true);
    }

    private JFXButton createElegantButton(String text, FontAwesomeSolid icon, String color) {
        FontIcon iconGraphic = new FontIcon(icon);
        iconGraphic.setIconSize(14);
        iconGraphic.setIconColor(Color.WHITE);

        JFXButton button = new JFXButton(text, iconGraphic);
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: 500;" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-radius: 6px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        );
        button.setPadding(new Insets(12, 20, 12, 20));
        button.setRipplerFill(Color.WHITE.deriveColor(0, 1, 1, 0.3));

        // Efectos hover elegantes
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: derive(" + color + ", -8%);" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: 500;" +
                            "-fx-font-size: 13px;" +
                            "-fx-background-radius: 6px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);"
            );
            new Pulse(button).setSpeed(3).play();
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: " + color + ";" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: 500;" +
                            "-fx-font-size: 13px;" +
                            "-fx-background-radius: 6px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
            );
        });

        return button;
    }

    private JFXButton createIconButton(FontAwesomeSolid icon, String color, String tooltip) {
        FontIcon iconGraphic = new FontIcon(icon);
        iconGraphic.setIconSize(16);
        iconGraphic.setIconColor(Color.web(color));

        JFXButton button = new JFXButton("", iconGraphic);
        button.setButtonType(JFXButton.ButtonType.RAISED);
        button.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                        "-fx-background-radius: 50%;" +
                        "-fx-min-width: 40px;" +
                        "-fx-min-height: 40px;" +
                        "-fx-max-width: 40px;" +
                        "-fx-max-height: 40px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
        );
        button.setRipplerFill(Color.web(color, 0.2));

        // Tooltip
        Tooltip.install(button, new Tooltip(tooltip));

        // Efectos hover
        button.setOnMouseEntered(e -> {
            button.setStyle(
                    "-fx-background-color: " + ACCENT_COLOR + ";" +
                            "-fx-background-radius: 50%;" +
                            "-fx-min-width: 40px;" +
                            "-fx-min-height: 40px;" +
                            "-fx-max-width: 40px;" +
                            "-fx-max-height: 40px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);"
            );
            new Pulse(button).setSpeed(2).play();
        });

        button.setOnMouseExited(e -> {
            button.setStyle(
                    "-fx-background-color: " + CARD_COLOR + ";" +
                            "-fx-background-radius: 50%;" +
                            "-fx-min-width: 40px;" +
                            "-fx-min-height: 40px;" +
                            "-fx-max-width: 40px;" +
                            "-fx-max-height: 40px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);"
            );
        });

        return button;
    }

    private void setupLayout() {
        // Header con título y estadísticas
        VBox header = createHeader();

        // Barra de herramientas
        HBox toolbar = createToolbar();

        // Contenedor de la tabla
        VBox tableContainer = createTableContainer();

        // Barra de acciones
        HBox actionBar = createActionBar();

        contentContainer.getChildren().addAll(header, toolbar, tableContainer, actionBar);

        // ScrollPane personalizado (usando ScrollPane estándar)
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("prestamos-scroll");
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        mainContainer.setCenter(scrollPane);
    }

    private VBox createHeader() {
        VBox header = new VBox(25);
        header.setPadding(new Insets(0, 0, 20, 0));

        // Título principal con diseño minimalista
        HBox titleBox = new HBox(20);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Contenedor de icono con fondo circular
        StackPane iconContainer = new StackPane();
        FontIcon titleIcon = new FontIcon(FontAwesomeSolid.HAND_HOLDING);
        titleIcon.setIconSize(24);
        titleIcon.setIconColor(Color.WHITE);
        iconContainer.getChildren().add(titleIcon);
        iconContainer.setStyle(
                "-fx-background-color: " + PRIMARY_COLOR + ";" +
                        "-fx-background-radius: 50%;" +
                        "-fx-min-width: 50px;" +
                        "-fx-min-height: 50px;" +
                        "-fx-max-width: 50px;" +
                        "-fx-max-height: 50px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 4);"
        );

        VBox titleTextBox = new VBox(2);
        Label titleLabel = new Label("Gestión de Préstamos");
        titleLabel.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: 300;" +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );

        Label subtitleLabel = new Label("Administra y controla todos los préstamos de equipos");
        subtitleLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";"
        );

        titleTextBox.getChildren().addAll(titleLabel, subtitleLabel);
        titleBox.getChildren().addAll(iconContainer, titleTextBox);

        // Tarjetas de estadísticas
        HBox statsCards = createStatsCards();

        header.getChildren().addAll(titleBox, statsCards);
        return header;
    }

    private HBox createStatsCards() {
        HBox statsContainer = new HBox(20);
        statsContainer.setAlignment(Pos.CENTER_LEFT);

        VBox totalCard = createMinimalStatCard("Total de Préstamos", totalPrestamosLabel, PRIMARY_COLOR, FontAwesomeSolid.LIST);
        VBox activosCard = createMinimalStatCard("Préstamos Activos", prestamosActivosLabel, SUCCESS_COLOR, FontAwesomeSolid.PLAY_CIRCLE);
        VBox vencidosCard = createMinimalStatCard("Préstamos Vencidos", prestamosVencidosLabel, ERROR_COLOR, FontAwesomeSolid.EXCLAMATION_TRIANGLE);
        VBox devueltosCard = createMinimalStatCard("Préstamos Devueltos", prestamosDevueltosLabel, INFO_COLOR, FontAwesomeSolid.CHECK_CIRCLE);

        statsContainer.getChildren().addAll(totalCard, activosCard, vencidosCard, devueltosCard);
        return statsContainer;
    }

    private VBox createMinimalStatCard(String title, Label valueLabel, String color, FontAwesomeSolid iconType) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(25, 20, 25, 20));
        card.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                        "-fx-background-radius: 12px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 4);" +
                        "-fx-border-color: " + SURFACE_COLOR + ";" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 12px;"
        );
        card.setAlignment(Pos.TOP_LEFT);
        card.setPrefWidth(220);
        card.setMaxWidth(220);

        // Header del card
        HBox cardHeader = new HBox(15);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(18);
        icon.setIconColor(Color.web(color));

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                        "-fx-font-weight: 500;"
        );

        cardHeader.getChildren().addAll(icon, titleLabel);

        // Valor principal
        valueLabel.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-font-weight: 300;" +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );

        card.getChildren().addAll(cardHeader, valueLabel);

        // Efectos hover minimalistas
        card.setOnMouseEntered(e -> {
            card.setStyle(
                    "-fx-background-color: " + CARD_COLOR + ";" +
                            "-fx-background-radius: 12px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 6);" +
                            "-fx-border-color: " + color + ";" +
                            "-fx-border-width: 1px;" +
                            "-fx-border-radius: 12px;"
            );
            new Pulse(card).setSpeed(3).play();
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                    "-fx-background-color: " + CARD_COLOR + ";" +
                            "-fx-background-radius: 12px;" +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 4);" +
                            "-fx-border-color: " + SURFACE_COLOR + ";" +
                            "-fx-border-width: 1px;" +
                            "-fx-border-radius: 12px;"
            );
        });

        return card;
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(20);
        toolbar.setPadding(new Insets(20, 25, 20, 25));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                        "-fx-background-radius: 12px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 4);" +
                        "-fx-border-color: " + SURFACE_COLOR + ";" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 12px;"
        );

        // Campo de búsqueda elegante
        HBox searchContainer = new HBox(10);
        searchContainer.setAlignment(Pos.CENTER_LEFT);
        searchContainer.setStyle(
                "-fx-background-color: " + SURFACE_COLOR + ";" +
                        "-fx-background-radius: 8px;" +
                        "-fx-padding: 10 15;"
        );

        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconColor(Color.web(TEXT_SECONDARY));
        searchIcon.setIconSize(14);

        searchField.setPromptText("Buscar préstamos...");
        searchField.setPrefWidth(300);
        searchField.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                        "-fx-border-color: transparent;" +
                        "-fx-focus-color: transparent;" +
                        "-fx-faint-focus-color: transparent;"
        );

        searchContainer.getChildren().addAll(searchIcon, searchField);

        // ComboBox de filtros elegante
        filterComboBox.setPromptText("Filtrar por estado");
        filterComboBox.setPrefWidth(180);
        filterComboBox.setStyle(
                "-fx-background-color: " + SURFACE_COLOR + ";" +
                        "-fx-background-radius: 8px;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );
        filterComboBox.getItems().addAll("Todos", "Activos", "Vencidos", "Devueltos", "Vencen Hoy");
        filterComboBox.setValue("Todos");

        // Espaciador
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(searchContainer, filterComboBox, spacer, actualizarButton);
        return toolbar;
    }

    private VBox createTableContainer() {
        VBox container = new VBox(20);
        container.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                        "-fx-background-radius: 12px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 4);" +
                        "-fx-border-color: " + SURFACE_COLOR + ";" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 12px;"
        );
        container.setPadding(new Insets(25));

        Label tableTitle = new Label("Lista de Préstamos");
        tableTitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: 500;" +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";"
        );

        container.getChildren().addAll(tableTitle, prestamosTable);
        VBox.setVgrow(prestamosTable, Priority.ALWAYS);

        return container;
    }

    private TableView<PrestamoDTO> createPrestamosTable() {
        TableView<PrestamoDTO> table = new TableView<>();
        table.setItems(prestamosData);
        table.getStyleClass().add("prestamos-table");
        table.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-table-cell-border-color: " + SURFACE_COLOR + ";" +
                        "-fx-selection-bar: " + ACCENT_COLOR + ";" +
                        "-fx-selection-bar-non-focused: " + SURFACE_COLOR + ";"
        );

        // Personalizar filas
        table.setRowFactory(tv -> {
            TableRow<PrestamoDTO> row = new TableRow<PrestamoDTO>() {
                @Override
                protected void updateItem(PrestamoDTO item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                    } else {
                        String statusColor = item.getStatusColor();
                        setStyle(
                                "-fx-border-color: transparent transparent transparent " + statusColor + ";" +
                                        "-fx-border-width: 0 0 0 3px;" +
                                        "-fx-background-color: transparent;" +
                                        "-fx-padding: 8px 0;"
                        );
                    }
                }
            };

            // Efectos hover en filas
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle(row.getStyle() + "-fx-background-color: " + ACCENT_COLOR + ";");
                }
            });

            row.setOnMouseExited(e -> {
                if (!row.isEmpty()) {
                    PrestamoDTO item = row.getItem();
                    if (item != null) {
                        String statusColor = item.getStatusColor();
                        row.setStyle(
                                "-fx-border-color: transparent transparent transparent " + statusColor + ";" +
                                        "-fx-border-width: 0 0 0 3px;" +
                                        "-fx-background-color: transparent;" +
                                        "-fx-padding: 8px 0;"
                        );
                    }
                }
            });

            return row;
        });

        // Configurar columnas con estilo minimalista
        TableColumn<PrestamoDTO, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(70);
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PrestamoDTO, String> fechaCol = new TableColumn<>("Fecha");
        fechaCol.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFecha();
            return new javafx.beans.property.SimpleStringProperty(
                    fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
            );
        });
        fechaCol.setPrefWidth(100);
        fechaCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PrestamoDTO, String> solicitanteCol = new TableColumn<>("Solicitante");
        solicitanteCol.setCellValueFactory(new PropertyValueFactory<>("solicitanteNombre"));
        solicitanteCol.setPrefWidth(160);

        TableColumn<PrestamoDTO, String> equipoCol = new TableColumn<>("Equipo");
        equipoCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEquipoCompleto())
        );
        equipoCol.setPrefWidth(220);

        TableColumn<PrestamoDTO, Integer> cantidadCol = new TableColumn<>("Cantidad");
        cantidadCol.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        cantidadCol.setPrefWidth(80);
        cantidadCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PrestamoDTO, String> fechaDevolucionCol = new TableColumn<>("Fecha Devolución");
        fechaDevolucionCol.setCellValueFactory(cellData -> {
            LocalDate fecha = cellData.getValue().getFechaDevolucion();
            return new javafx.beans.property.SimpleStringProperty(
                    fecha != null ? fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : ""
            );
        });
        fechaDevolucionCol.setPrefWidth(130);
        fechaDevolucionCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<PrestamoDTO, String> statusCol = new TableColumn<>("Estado");
        statusCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatusText())
        );
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(column -> new TableCell<PrestamoDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    PrestamoDTO prestamo = getTableView().getItems().get(getIndex());
                    String color = prestamo.getStatusColor();
                    setStyle(
                            "-fx-text-fill: " + color + ";" +
                                    "-fx-font-weight: 500;" +
                                    "-fx-alignment: CENTER;" +
                                    "-fx-background-color: " + color + "22;" +
                                    "-fx-background-radius: 4px;" +
                                    "-fx-padding: 4px 8px;"
                    );
                }
            }
        });

        table.getColumns().addAll(idCol, fechaCol, solicitanteCol, equipoCol, cantidadCol, fechaDevolucionCol, statusCol);

        // Configurar selección
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editarPrestamoButton.setDisable(!hasSelection);
            eliminarPrestamoButton.setDisable(!hasSelection);
            verDetallesButton.setDisable(!hasSelection);

            // Solo habilitar devolución si el préstamo está activo o vencido
            boolean canReturn = hasSelection && !newSelection.isDevuelto();
            procesarDevolucionButton.setDisable(!canReturn);
        });

        return table;
    }

    private HBox createActionBar() {
        HBox actionBar = new HBox(15);
        actionBar.setPadding(new Insets(20, 25, 20, 25));
        actionBar.setAlignment(Pos.CENTER_LEFT);
        actionBar.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                        "-fx-background-radius: 12px;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 4);" +
                        "-fx-border-color: " + SURFACE_COLOR + ";" +
                        "-fx-border-width: 1px;" +
                        "-fx-border-radius: 12px;"
        );

        // Separadores elegantes
        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: " + SURFACE_COLOR + ";");

        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: " + SURFACE_COLOR + ";");

        actionBar.getChildren().addAll(
                nuevoPrestamoButton,
                sep1,
                editarPrestamoButton,
                eliminarPrestamoButton,
                sep2,
                procesarDevolucionButton,
                verDetallesButton
        );

        return actionBar;
    }

    private void setupEventHandlers() {
        // Búsqueda en tiempo real con debounce
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                loadData();
            } else {
                buscarPrestamos(newText.trim());
            }
        });

        // Filtro por estado
        filterComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                filtrarPorEstado(newValue);
            }
        });

        // Botones de acción
        nuevoPrestamoButton.setOnAction(e -> {
            new FadeInUp(nuevoPrestamoButton).play();
            mostrarFormularioNuevoPrestamo();
        });

        editarPrestamoButton.setOnAction(e -> {
            new FadeInUp(editarPrestamoButton).play();
            editarPrestamoSeleccionado();
        });

        eliminarPrestamoButton.setOnAction(e -> {
            new FadeInUp(eliminarPrestamoButton).play();
            eliminarPrestamoSeleccionado();
        });

        procesarDevolucionButton.setOnAction(e -> {
            new FadeInUp(procesarDevolucionButton).play();
            mostrarFormularioDevolucion();
        });

        verDetallesButton.setOnAction(e -> {
            new FadeInUp(verDetallesButton).play();
            mostrarDetallesPrestamo();
        });

        actualizarButton.setOnAction(e -> {
            new RotateIn(actualizarButton).play();
            loadData();
        });

        // Doble clic en tabla para ver detalles
        prestamosTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2 && prestamosTable.getSelectionModel().getSelectedItem() != null) {
                new FadeInUp(prestamosTable).play();
                mostrarDetallesPrestamo();
            }
        });
    }

    private void loadData() {
        // Animación de carga
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), prestamosTable);
        fadeOut.setToValue(0.5);
        fadeOut.setOnFinished(e -> {
            controller.obtenerTodosLosPrestamos(
                    prestamos -> {
                        prestamosData.clear();
                        prestamosData.addAll(prestamos);
                        updateStatistics();

                        // Animación de entrada
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), prestamosTable);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();

                        new FadeInUp(prestamosTable).play();
                    },
                    error -> {
                        AlertUtils.showError("Error", error);
                        // Restaurar opacidad en caso de error
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), prestamosTable);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();
                    }
            );
        });
        fadeOut.play();
    }

    private void updateStatistics() {
        controller.obtenerEstadisticas(
                stats -> {
                    // Animaciones para los números
                    animateNumberChange(totalPrestamosLabel, stats.getTotal());
                    animateNumberChange(prestamosActivosLabel, stats.getActivos());
                    animateNumberChange(prestamosVencidosLabel, stats.getVencidos());
                    animateNumberChange(prestamosDevueltosLabel, stats.getDevueltos());
                },
                error -> System.err.println("Error al actualizar estadísticas: " + error)
        );
    }

    private void animateNumberChange(Label label, int newValue) {
        try {
            int currentValue = Integer.parseInt(label.getText());
            if (currentValue != newValue) {
                Timeline timeline = new Timeline();
                final int difference = newValue - currentValue;
                final int steps = Math.min(Math.abs(difference), 30);

                for (int i = 0; i <= steps; i++) {
                    final int stepValue = currentValue + (difference * i / steps);
                    KeyFrame keyFrame = new KeyFrame(
                            Duration.millis(i * 20),
                            e -> label.setText(String.valueOf(stepValue))
                    );
                    timeline.getKeyFrames().add(keyFrame);
                }
                timeline.play();
            }
        } catch (NumberFormatException e) {
            label.setText(String.valueOf(newValue));
        }
    }

    private void buscarPrestamos(String termino) {
        // Animación de búsqueda
        new Pulse(searchField.getParent()).setSpeed(2).play();

        controller.buscarPrestamos(termino,
                prestamos -> {
                    prestamosData.clear();
                    prestamosData.addAll(prestamos);
                    new FadeInLeft(prestamosTable).play();
                },
                error -> AlertUtils.showError("Error de Búsqueda", error)
        );
    }

    private void filtrarPorEstado(String estado) {
        String estadoFiltro = estado.equals("Todos") ? "" : estado.toUpperCase().replace(" ", "_");

        // Animación de filtro
        new Pulse(filterComboBox).setSpeed(2).play();

        controller.filtrarPorEstado(estadoFiltro,
                prestamos -> {
                    prestamosData.clear();
                    prestamosData.addAll(prestamos);
                    new FadeInRight(prestamosTable).play();
                },
                error -> AlertUtils.showError("Error de Filtro", error)
        );
    }

    private void mostrarFormularioNuevoPrestamo() {
        Platform.runLater(() -> {
            try {
                PrestamoFormDialog dialog = new PrestamoFormDialog(null);
                dialog.showAndWait().ifPresent(prestamo -> {
                    controller.crearPrestamo(prestamo,
                            id -> {
                                AlertUtils.showSuccess("Éxito", "Préstamo creado correctamente");
                                loadData();
                                new BounceIn(prestamosTable).play();
                            },
                            error -> AlertUtils.showError("Error", error)
                    );
                });
            } catch (Exception e) {
                AlertUtils.showError("Error", "No se pudo abrir el formulario de préstamo");
            }
        });
    }

    private void editarPrestamoSeleccionado() {
        PrestamoDTO selected = prestamosTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                PrestamoFormDialog dialog = new PrestamoFormDialog(selected);
                dialog.showAndWait().ifPresent(prestamo -> {
                    controller.actualizarPrestamo(prestamo,
                            () -> {
                                AlertUtils.showSuccess("Éxito", "Préstamo actualizado correctamente");
                                loadData();
                                new BounceIn(prestamosTable).play();
                            },
                            error -> AlertUtils.showError("Error", error)
                    );
                });
            } catch (Exception e) {
                AlertUtils.showError("Error", "No se pudo abrir el formulario de edición");
            }
        }
    }

    private void eliminarPrestamoSeleccionado() {
        PrestamoDTO selected = prestamosTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            // Corregir el método showConfirmation - usar solo 2 parámetros
            boolean confirmed = AlertUtils.showConfirmation(
                    "Confirmar Eliminación",
                    "¿Está seguro de que desea eliminar este préstamo?\n\nEsta acción no se puede deshacer."
            );

            if (confirmed) {
                controller.eliminarPrestamo(selected.getId(),
                        () -> {
                            AlertUtils.showSuccess("Éxito", "Préstamo eliminado correctamente");
                            loadData();
                            new FadeOutUp(prestamosTable).play();
                        },
                        error -> AlertUtils.showError("Error", error)
                );
            }
        }
    }

    private void mostrarFormularioDevolucion() {
        PrestamoDTO selected = prestamosTable.getSelectionModel().getSelectedItem();
        if (selected != null && !selected.isDevuelto()) {
            try {
                DevolucionDialog dialog = new DevolucionDialog(selected);
                dialog.showAndWait().ifPresent(devolucionData -> {
                    controller.procesarDevolucion(
                            selected.getId(),
                            devolucionData.getDevueltoPor(),
                            devolucionData.getRecibidoPor(),
                            devolucionData.getEstadoDevuelto(),
                            () -> {
                                AlertUtils.showSuccess("Éxito", "Devolución procesada correctamente");
                                loadData();
                                new BounceIn(prestamosTable).play();
                            },
                            error -> AlertUtils.showError("Error", error)
                    );
                });
            } catch (Exception e) {
                AlertUtils.showError("Error", "No se pudo abrir el formulario de devolución");
            }
        }
    }

    private void mostrarDetallesPrestamo() {
        PrestamoDTO selected = prestamosTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                PrestamoDetallesDialog dialog = new PrestamoDetallesDialog(selected);
                dialog.showAndWait();
            } catch (Exception e) {
                AlertUtils.showError("Error", "No se pudo abrir los detalles del préstamo");
            }
        }
    }

    private void setupAutoRefresh() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(5), e -> {
            updateStatistics();
            // Actualización silenciosa de datos si no hay búsqueda activa
            if (searchField.getText() == null || searchField.getText().trim().isEmpty()) {
                controller.obtenerTodosLosPrestamos(
                        prestamos -> {
                            // Solo actualizar si los datos han cambiado
                            if (prestamos.size() != prestamosData.size()) {
                                prestamosData.clear();
                                prestamosData.addAll(prestamos);
                            }
                        },
                        error -> {
                            // Silenciar errores de actualización automática
                            System.err.println("Error en actualización automática: " + error);
                        }
                );
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Método para aplicar tema personalizado
     */
    public void applyCustomTheme() {
        String customCSS =
                ".prestamos-container {" +
                        "-fx-font-family: 'Segoe UI', 'San Francisco', 'Helvetica Neue', Arial, sans-serif;" +
                        "}" +

                        ".prestamos-table {" +
                        "-fx-background-color: transparent;" +
                        "-fx-border-color: transparent;" +
                        "}" +

                        ".prestamos-table .column-header {" +
                        "-fx-background-color: " + SURFACE_COLOR + ";" +
                        "-fx-font-weight: 500;" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + TEXT_SECONDARY + ";" +
                        "-fx-border-color: transparent;" +
                        "}" +

                        ".prestamos-table .table-cell {" +
                        "-fx-font-size: 13px;" +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                        "-fx-border-color: transparent;" +
                        "-fx-padding: 12px 8px;" +
                        "}" +

                        ".prestamos-scroll .scroll-bar {" +
                        "-fx-background-color: transparent;" +
                        "}" +

                        ".prestamos-scroll .scroll-bar .thumb {" +
                        "-fx-background-color: " + TEXT_SECONDARY + "44;" +
                        "-fx-background-radius: 4px;" +
                        "}" +

                        ".prestamos-scroll .scroll-bar .thumb:hover {" +
                        "-fx-background-color: " + TEXT_SECONDARY + "88;" +
                        "}";

        mainContainer.setStyle(mainContainer.getStyle() + customCSS);
    }

    /**
     * Método para obtener el nodo principal de la vista
     */
    public Node getView() {
        return mainContainer;
    }

    /**
     * Método para limpiar recursos cuando se cierra la vista
     */
    public void cleanup() {
        // Detener animaciones y timelines
        prestamosTable.getItems().clear();
        // Limpiar listeners si es necesario
    }

    /**
     * Método para exportar datos (funcionalidad adicional)
     */
    public void exportarDatos() {
        // Implementar exportación de datos si es necesario
        AlertUtils.showInfo("Información", "Funcionalidad de exportación en desarrollo");
    }

    /**
     * Método para configurar atajos de teclado
     */
    private void setupKeyboardShortcuts() {
        mainContainer.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case F5:
                    loadData();
                    break;
                case N:
                    if (e.isControlDown()) {
                        mostrarFormularioNuevoPrestamo();
                    }
                    break;
                case F:
                    if (e.isControlDown()) {
                        searchField.requestFocus();
                    }
                    break;
                case DELETE:
                    if (prestamosTable.getSelectionModel().getSelectedItem() != null) {
                        eliminarPrestamoSeleccionado();
                    }
                    break;
                case ENTER:
                    if (prestamosTable.getSelectionModel().getSelectedItem() != null) {
                        mostrarDetallesPrestamo();
                    }
                    break;
            }
        });
    }
}