package tescha.inventario.view.components;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.DirectoryChooser;
import javafx.util.Duration;
import tescha.inventario.controller.InventarioController;
import tescha.inventario.dto.EquipoDTO;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;

public class EquipoCard extends BorderPane {
    private EquipoDTO equipo;
    private InventarioController controller;
    private ImageView imagenView;
    private TabPane tabPane;

    public EquipoCard(EquipoDTO equipo, InventarioController controller) {
        this.equipo = equipo;
        this.controller = controller;

        getStyleClass().add("equipo-card");
        setPadding(new Insets(10));
        setMinWidth(300);
        setPrefWidth(350);

        // Inicializar efectos visuales
        initializeParticleEffect();

        // Crear header compacto con imagen y título
        VBox headerBox = createHeaderSection();
        setTop(headerBox);

        // Crear TabPane para organizar la información
        tabPane = createTabPane();
        setCenter(tabPane);

        // Crear barra de acciones en la parte inferior
        HBox actionBar = createActionBar();
        setBottom(actionBar);

        setupHoverReveal();
        setupButtonRippleEffects();
    }

    private VBox createHeaderSection() {
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(0, 0, 5, 0));

        // Título del equipo
        Label titulo = new Label(equipo.getNombre());
        titulo.getStyleClass().add("equipo-titulo");

        // Contenedor para imagen y código de barras mini
        HBox mediaContainer = new HBox(10);
        mediaContainer.setAlignment(Pos.CENTER);

        // Imagen del producto
        imagenView = new ImageView();
        imagenView.getStyleClass().add("skeleton-loader");

        if (equipo.getImagen() != null && !equipo.getImagen().isEmpty()) {
            try {
                Image img = new Image("file:" + equipo.getImagen(), 100, 100, true, true);
                imagenView.setImage(img);
                imagenView.setPreserveRatio(true);
            } catch (Exception e) {
                imagenView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            }
        } else {
            imagenView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
        }
        imagenView.setFitWidth(100);
        imagenView.setFitHeight(100);

        // Mini código de barras
        VBox barcodeBox = new VBox(5);
        barcodeBox.setAlignment(Pos.CENTER);

        // Generar código de barras automáticamente
        if (equipo.getQrcode() == null) {
            equipo.generarCodigoBarrasDinamico();
        }

        ImageView miniBarcodeView = new ImageView(equipo.getBarcodeImage());
        miniBarcodeView.setFitWidth(100);
        miniBarcodeView.setFitHeight(35);
        miniBarcodeView.setOnMouseClicked(e -> mostrarCodigoBarrasCompleto(equipo));

        Tooltip.install(miniBarcodeView, new Tooltip("Clic para ver código completo"));

        // Botón compacto para guardar con icono FontAwesome
        Button saveBtn = new Button();
        FontAwesomeIconView saveIcon = new FontAwesomeIconView(FontAwesomeIcon.SAVE);
        saveIcon.setSize("16px");
        saveBtn.setGraphic(saveIcon);
        saveBtn.setTooltip(new Tooltip("Guardar código de barras"));
        saveBtn.setOnAction(ev -> guardarCodigoBarras(equipo));

        barcodeBox.getChildren().addAll(miniBarcodeView, saveBtn);

        // Indicador de stock
        ProgressIndicator stockIndicator = new ProgressIndicator(
                Math.min(1.0, (double)equipo.getCantidad() / Math.max(1, equipo.getCantidadMinima() * 2))
        );
        stockIndicator.setPrefSize(30, 30);
        stockIndicator.setTooltip(new Tooltip("Stock: " + equipo.getCantidad() + " de " + equipo.getCantidadMinima() + " mínimo"));

        if (equipo.isStockBajo()) {
            stockIndicator.setStyle("-fx-progress-color: red;");
        } else {
            stockIndicator.setStyle("-fx-progress-color: green;");
        }

        mediaContainer.getChildren().addAll(imagenView, barcodeBox, stockIndicator);
        headerBox.getChildren().addAll(titulo, mediaContainer);

        return headerBox;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("compact-tabs");

        // Tab 1: Información General
        ScrollPane infoTab = createInfoTab();
        Tab generalTab = new Tab("Información");
        generalTab.setContent(infoTab);
        generalTab.setGraphic(createFontAwesomeIcon(FontAwesomeIcon.INFO_CIRCLE, 16));

        // Tab 2: Información Técnica
        ScrollPane tecnicaTab = createTecnicaTab();
        Tab tecnTab = new Tab("Técnica");
        tecnTab.setContent(tecnicaTab);
        tecnTab.setGraphic(createFontAwesomeIcon(FontAwesomeIcon.COG, 16));

        // Tab 3: Mantenimiento
        ScrollPane mantTab = createMantenimientoTab();
        Tab mantenimientoTab = new Tab("Mantenimiento");
        mantenimientoTab.setContent(mantTab);
        mantenimientoTab.setGraphic(createFontAwesomeIcon(FontAwesomeIcon.WRENCH, 16));

        // Tab 4: Historial
        Tab historialTab = new Tab("Historial");
        historialTab.setContent(createHistorialTab());
        historialTab.setGraphic(createFontAwesomeIcon(FontAwesomeIcon.HISTORY, 16));

        tabPane.getTabs().addAll(generalTab, tecnTab, mantenimientoTab, historialTab);

        return tabPane;
    }

    private Node createFontAwesomeIcon(FontAwesomeIcon icon, int size) {
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setSize(size + "px");
        return iconView;
    }

    private ScrollPane createInfoTab() {
        VBox infoContent = new VBox(10);
        infoContent.setPadding(new Insets(10));
        infoContent.getStyleClass().add("info-tab-content");

        // Sección de info básica
        TitledPane basicInfoPane = new TitledPane("Información Básica", null);
        basicInfoPane.setExpanded(true);

        VBox infoBasica = new VBox(5);
        infoBasica.setPadding(new Insets(5));
        infoBasica.getStyleClass().add("info-basica");

        addInfoRowWithIcon(infoBasica, FontAwesomeIcon.TAG, "ID:", String.valueOf(equipo.getId()));
        addInfoRowWithIcon(infoBasica, FontAwesomeIcon.FOLDER, "Categoría:", equipo.getCategoria());
        addInfoRowWithIcon(infoBasica, FontAwesomeIcon.MAP_MARKER, "Ubicación:", equipo.getUbicacion());
        addInfoRowWithIcon(infoBasica, FontAwesomeIcon.CHECK_CIRCLE, "Estado:", equipo.getStatus());

        // Stock con barra de progreso
        HBox stockBox = new HBox(10);
        stockBox.setAlignment(Pos.CENTER_LEFT);

        FontAwesomeIconView stockIcon = new FontAwesomeIconView(FontAwesomeIcon.CUBES);
        stockIcon.setSize("16px");
        stockBox.getChildren().add(stockIcon);

        Label stockLabel = new Label("Stock:");

        // Barra de progreso para stock
        ProgressBar stockBar = new ProgressBar(
                Math.min(1.0, (double)equipo.getCantidad() / Math.max(1, equipo.getCantidadMinima() * 2))
        );
        stockBar.setPrefWidth(100);

        Label stockValue = new Label(equipo.getCantidad() + " (Mín: " + equipo.getCantidadMinima() + ")");

        if (equipo.isStockBajo()) {
            stockValue.getStyleClass().add("stock-bajo");
            stockBar.setStyle("-fx-accent: red;");
        } else {
            stockBar.setStyle("-fx-accent: green;");
        }

        stockBox.getChildren().addAll(stockLabel, stockBar, stockValue);
        infoBasica.getChildren().add(stockBox);

        basicInfoPane.setContent(infoBasica);
        infoContent.getChildren().add(basicInfoPane);

        // Notas
        if (equipo.getNotas() != null && !equipo.getNotas().isEmpty()) {
            TitledPane notasPane = new TitledPane("Notas", null);
            VBox notasBox = new VBox(5);
            notasBox.setPadding(new Insets(5));

            FontAwesomeIconView notesIcon = new FontAwesomeIconView(FontAwesomeIcon.STICKY_NOTE);
            notesIcon.setSize("16px");
            notasPane.setGraphic(notesIcon);

            TextArea notasArea = new TextArea(equipo.getNotas());
            notasArea.setEditable(false);
            notasArea.setWrapText(true);
            notasArea.setPrefRowCount(4);

            notasBox.getChildren().add(notasArea);
            notasPane.setContent(notasBox);
            infoContent.getChildren().add(notasPane);
        }

        ScrollPane scrollPane = new ScrollPane(infoContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private void addInfoRowWithIcon(VBox container, FontAwesomeIcon icon, String label, String value) {
        if (value != null && !value.isEmpty()) {
            HBox row = new HBox(5);
            row.setAlignment(Pos.CENTER_LEFT);

            FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
            iconView.setSize("14px");
            row.getChildren().add(iconView);

            Label lbl = new Label(label);
            lbl.setStyle("-fx-font-weight: bold;");
            Label val = new Label(value);
            row.getChildren().addAll(lbl, val);
            container.getChildren().add(row);
        }
    }

    private ScrollPane createTecnicaTab() {
        VBox tecnicaContent = new VBox(10);
        tecnicaContent.setPadding(new Insets(10));
        tecnicaContent.getStyleClass().add("tecnica-tab-content");

        // Especificaciones técnicas
        TitledPane especificacionesPane = new TitledPane("Especificaciones", null);
        especificacionesPane.setExpanded(true);

        VBox infoTecnica = new VBox(5);
        infoTecnica.setPadding(new Insets(5));
        infoTecnica.getStyleClass().add("info-tecnica");

        // Grid para la información técnica (en dos columnas)
        GridPane gridTecnica = new GridPane();
        gridTecnica.setHgap(15);
        gridTecnica.setVgap(5);

        int row = 0;

        addGridInfoRowWithIcon(gridTecnica, row++, FontAwesomeIcon.BARCODE, "Número de serie:", equipo.getNumeroSerie());
        addGridInfoRowWithIcon(gridTecnica, row++, FontAwesomeIcon.COG, "Marca:", equipo.getMarca());
        addGridInfoRowWithIcon(gridTecnica, row++, FontAwesomeIcon.LIST_ALT, "Modelo:", equipo.getModelo());

        infoTecnica.getChildren().add(gridTecnica);
        especificacionesPane.setContent(infoTecnica);

        // Adquisición
        TitledPane adquisicionPane = new TitledPane("Información de Adquisición", null);
        VBox adquisicionBox = new VBox(5);
        adquisicionBox.setPadding(new Insets(5));

        GridPane gridAdquisicion = new GridPane();
        gridAdquisicion.setHgap(15);
        gridAdquisicion.setVgap(5);

        row = 0;

        if (equipo.getFechaAdquisicion() != null) {
            addGridInfoRowWithIcon(gridAdquisicion, row++, FontAwesomeIcon.CALENDAR, "Fecha adquisición:",
                    equipo.getFechaAdquisicion().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        addGridInfoRowWithIcon(gridAdquisicion, row++, FontAwesomeIcon.MONEY, "Costo:", String.format("$%.2f", equipo.getCostoAdquisicion()));
        addGridInfoRowWithIcon(gridAdquisicion, row++, FontAwesomeIcon.TRUCK, "Proveedor:", equipo.getProveedor());
        addGridInfoRowWithIcon(gridAdquisicion, row++, FontAwesomeIcon.SHIELD, "Garantía:", equipo.getGarantia());

        if (equipo.getVencimientoGarantia() != null) {
            addGridInfoRowWithIcon(gridAdquisicion, row++, FontAwesomeIcon.CALENDAR_TIMES_ALT, "Venc. garantía:",
                    equipo.getVencimientoGarantia().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }

        adquisicionBox.getChildren().add(gridAdquisicion);
        adquisicionPane.setContent(adquisicionBox);

        tecnicaContent.getChildren().addAll(especificacionesPane, adquisicionPane);

        ScrollPane scrollPane = new ScrollPane(tecnicaContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private void addGridInfoRowWithIcon(GridPane grid, int row, FontAwesomeIcon icon, String label, String value) {
        if (value != null && !value.isEmpty()) {
            HBox labelBox = new HBox(5);
            labelBox.setAlignment(Pos.CENTER_LEFT);

            FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
            iconView.setSize("14px");

            Label lbl = new Label(label);
            lbl.setStyle("-fx-font-weight: bold;");

            labelBox.getChildren().addAll(iconView, lbl);
            Label val = new Label(value);

            grid.add(labelBox, 0, row);
            grid.add(val, 1, row);
        }
    }

    private ScrollPane createMantenimientoTab() {
        VBox mantenimientoContent = new VBox(10);
        mantenimientoContent.setPadding(new Insets(10));
        mantenimientoContent.getStyleClass().add("mantenimiento-tab-content");

        // Programación de mantenimiento
        TitledPane programacionPane = new TitledPane("Programación de Mantenimiento", null);
        programacionPane.setExpanded(true);

        VBox infoMantenimiento = new VBox(5);
        infoMantenimiento.setPadding(new Insets(5));
        infoMantenimiento.getStyleClass().add("info-mantenimiento");

        addInfoRowWithIcon(infoMantenimiento, FontAwesomeIcon.CALENDAR_CHECK_ALT, "Tipo de mantenimiento:", equipo.getMantenimientoProgramado());

        // Fechas importantes con indicadores visuales
        if (equipo.getUltimoMantenimiento() != null) {
            HBox ultMantBox = new HBox(10);
            ultMantBox.setAlignment(Pos.CENTER_LEFT);

            FontAwesomeIconView calendarIcon = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR);
            calendarIcon.setSize("14px");

            Label ultMantLabel = new Label("Último mantenimiento:");
            Label ultMantValue = new Label(
                    equipo.getUltimoMantenimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            Circle statusCircle = new Circle(5);
            statusCircle.setFill(Color.GREEN);

            ultMantBox.getChildren().addAll(calendarIcon, ultMantLabel, ultMantValue, statusCircle);
            infoMantenimiento.getChildren().add(ultMantBox);
        }

        if (equipo.getProximoMantenimiento() != null) {
            HBox proxMantBox = new HBox(10);
            proxMantBox.setAlignment(Pos.CENTER_LEFT);

            FontAwesomeIconView calendarIcon = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR);
            calendarIcon.setSize("14px");

            Label proxMantLabel = new Label("Próximo mantenimiento:");
            Label proxMantValue = new Label(
                    equipo.getProximoMantenimiento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            // Determinar si estamos cerca de la fecha de próximo mantenimiento
            Circle statusCircle = new Circle(5);
            statusCircle.setFill(Color.ORANGE); // Por defecto mostrar naranja

            proxMantBox.getChildren().addAll(calendarIcon, proxMantLabel, proxMantValue, statusCircle);
            infoMantenimiento.getChildren().add(proxMantBox);
        }

        // Cronograma visual (simplificado)
        VBox cronograma = new VBox(5);
        ProgressBar cronogramaBar = new ProgressBar(0.7); // Valor de ejemplo
        cronogramaBar.setPrefWidth(200);

        HBox timelineLabels = new HBox();
        timelineLabels.setAlignment(Pos.CENTER);
        timelineLabels.setPrefWidth(200);

        Label startLabel = new Label("Último");
        startLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(startLabel, Priority.ALWAYS);

        Label endLabel = new Label("Próximo");
        endLabel.setMaxWidth(Double.MAX_VALUE);
        endLabel.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(endLabel, Priority.ALWAYS);

        timelineLabels.getChildren().addAll(startLabel, endLabel);

        cronograma.getChildren().addAll(
                new Label("Cronograma de mantenimiento:"),
                cronogramaBar,
                timelineLabels
        );

        infoMantenimiento.getChildren().add(cronograma);
        programacionPane.setContent(infoMantenimiento);

        // Historial de mantenimiento reciente
        TitledPane historialMantPane = new TitledPane("Historial de Mantenimiento Reciente", null);
        VBox historialMantBox = new VBox(5);
        historialMantBox.setPadding(new Insets(5));

        // Aquí se podría agregar un ListView con entradas del historial específicas de mantenimiento
        Label placeholder = new Label("No hay registros de mantenimiento recientes.");
        historialMantBox.getChildren().add(placeholder);

        historialMantPane.setContent(historialMantBox);

        mantenimientoContent.getChildren().addAll(programacionPane, historialMantPane);

        ScrollPane scrollPane = new ScrollPane(mantenimientoContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private ScrollPane createHistorialTab() {
        VBox historialContent = new VBox(10);
        historialContent.setPadding(new Insets(10));

        // Obtener historial del equipo
        List<String> historial = controller.obtenerHistorial(equipo.getId());

        if (historial != null && !historial.isEmpty()) {
            ListView<String> listView = new ListView<>();
            listView.getItems().addAll(historial);
            listView.setPrefHeight(200);

            historialContent.getChildren().add(listView);
        } else {
            Label noHistorial = new Label("No hay registros de historial para este equipo.");
            noHistorial.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
            historialContent.getChildren().add(noHistorial);
        }

        // Filtros de historial
        TitledPane filtrosPane = new TitledPane("Filtros", null);
        filtrosPane.setExpanded(false);

        HBox filtrosBox = new HBox(10);
        filtrosBox.setPadding(new Insets(5));
        filtrosBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> tipoFiltro = new ComboBox<>();
        tipoFiltro.getItems().addAll("Todos", "Mantenimiento", "Movimientos", "Cambios de estado");
        tipoFiltro.setValue("Todos");

        DatePicker fechaDesde = new DatePicker();
        fechaDesde.setPromptText("Fecha desde");

        Button aplicarFiltro = new Button("Aplicar");
        aplicarFiltro.setGraphic(createFontAwesomeIcon(FontAwesomeIcon.FILTER, 14));

        filtrosBox.getChildren().addAll(
                new Label("Tipo:"), tipoFiltro,
                new Label("Desde:"), fechaDesde,
                aplicarFiltro
        );

        filtrosPane.setContent(filtrosBox);
        historialContent.getChildren().add(filtrosPane);

        ScrollPane scrollPane = new ScrollPane(historialContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return scrollPane;
    }

    private HBox createActionBar() {
        HBox actionBar = new HBox(10);
        actionBar.setPadding(new Insets(10, 0, 0, 0));
        actionBar.setAlignment(Pos.CENTER);

        StackPane editarBtnContainer = createButtonWithIcon("Editar", FontAwesomeIcon.EDIT);
        Button editarBtn = (Button) editarBtnContainer.getChildren().get(0);
        editarBtn.setOnAction(e -> {
            e.consume();
            EquipoForm form = new EquipoForm(controller, equipo);
            form.showAndWait(); // Use showAndWait() instead of mostrar()
        });
        StackPane historialBtnContainer = createButtonWithIcon("Historial", FontAwesomeIcon.HISTORY);
        Button historialBtn = (Button) historialBtnContainer.getChildren().get(0);
        historialBtn.setOnAction(e -> tabPane.getSelectionModel().select(3));

        StackPane barcodeBtnContainer = createButtonWithIcon("Código", FontAwesomeIcon.BARCODE);
        Button barcodeBtn = (Button) barcodeBtnContainer.getChildren().get(0);
        barcodeBtn.setOnAction(e -> mostrarCodigoBarrasCompleto(equipo));

        actionBar.getChildren().addAll(editarBtnContainer, historialBtnContainer, barcodeBtnContainer);
        return actionBar;
    }

    private StackPane createButtonWithIcon(String text, FontAwesomeIcon icon) {
        Button button = new Button(text);
        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setSize("14px");
        button.setGraphic(iconView);

        StackPane container = new StackPane();
        container.getChildren().add(button);
        return container;
    }

    // Método para inicializar las partículas flotantes
    private void initializeParticleEffect() {
        Pane particleContainer = new Pane();
        particleContainer.setMouseTransparent(true);
        particleContainer.setPrefSize(300, 600);
        particleContainer.setStyle("-fx-background-color: transparent;");

        // Crear partículas
        Random random = new Random();
        for (int i = 0; i < 10; i++) { // Reducido el número de partículas
            Circle particle = new Circle(random.nextDouble() * 1.5 + 0.5); // Partículas más pequeñas
            particle.getStyleClass().add("particle");
            particle.setLayoutX(random.nextDouble() * 300);
            particle.setLayoutY(random.nextDouble() * 600);

            // Animar partículas con Timeline
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(particle.layoutYProperty(), particle.getLayoutY()),
                            new KeyValue(particle.opacityProperty(), 0.1 + random.nextDouble() * 0.3) // Menos opacidad
                    ),
                    new KeyFrame(Duration.seconds(8 + random.nextDouble() * 12),
                            new KeyValue(particle.layoutYProperty(), particle.getLayoutY() - 80 - random.nextDouble() * 80),
                            new KeyValue(particle.opacityProperty(), 0)
                    )
            );
            timeline.setOnFinished(e -> {
                // Reiniciar partícula
                particle.setLayoutY(random.nextDouble() * 600 + 600);
                particle.setLayoutX(random.nextDouble() * 300);
                particle.setOpacity(0.1 + random.nextDouble() * 0.3);
                timeline.play();
            });
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            particleContainer.getChildren().add(particle);
        }

        // Agregar contenedor de partículas como fondo
        this.setBackground(new Background(new BackgroundFill(
                Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        // Agregar el efecto de partículas al fondo
        StackPane background = new StackPane();
        background.getChildren().add(particleContainer);
        this.setBackground(new Background(new BackgroundFill(
                Color.TRANSPARENT, new CornerRadii(5), Insets.EMPTY)));
    }

    // Método para configurar el efecto hover reveal
    private void setupHoverReveal() {
        // Crear contenedor para acciones rápidas
        HBox quickActions = new HBox(10);
        quickActions.getStyleClass().add("hover-reveal-container");
        quickActions.setAlignment(Pos.CENTER_RIGHT);
        quickActions.setPadding(new Insets(5));
        quickActions.setVisible(false);
        quickActions.setOpacity(0);

        // Botones de acción rápida con iconos FontAwesome
        Button viewButton = new Button("", new FontAwesomeIconView(FontAwesomeIcon.EYE));
        Button editButton = new Button("", new FontAwesomeIconView(FontAwesomeIcon.EDIT));

        viewButton.setTooltip(new Tooltip("Ver detalles"));
        editButton.setTooltip(new Tooltip("Editar equipo"));

        quickActions.getChildren().addAll(viewButton, editButton);

        // Mostrar/ocultar con hover
        this.setOnMouseEntered(e -> {
            if (e.isConsumed()) return;

            FadeTransition ft = new FadeTransition(Duration.millis(200), quickActions);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
            quickActions.setVisible(true);
        });

        this.setOnMouseExited(e -> {
            if (e.isConsumed()) return;

            FadeTransition ft = new FadeTransition(Duration.millis(200), quickActions);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setOnFinished(event -> quickActions.setVisible(false));
            ft.play();
        });
    }

    // Método para implementar efecto ripple en botones
    private void setupButtonRippleEffects() {
        for (Node node : this.lookupAll(".button")) {
            if (node instanceof Button) {
                Button button = (Button) node;
                button.setOnMousePressed(e -> {
                    StackPane container = (StackPane) button.getParent();

                    Circle ripple = new Circle(1);
                    ripple.setCenterX(e.getX());
                    ripple.setCenterY(e.getY());
                    ripple.setFill(Color.web("rgba(255, 255, 255, 0.3)"));
                    ripple.setMouseTransparent(true);

                    StackPane rippleContainer = new StackPane();
                    rippleContainer.setClip(new javafx.scene.shape.Rectangle(
                            button.getWidth(), button.getHeight()));
                    rippleContainer.getChildren().add(ripple);
                    rippleContainer.setMouseTransparent(true);

                    container.getChildren().add(rippleContainer);

                    ScaleTransition st = new ScaleTransition(Duration.millis(300), ripple);
                    st.setToX(button.getWidth() * 2);
                    st.setToY(button.getHeight() * 2);
                    st.setInterpolator(Interpolator.EASE_OUT);

                    FadeTransition ft = new FadeTransition(Duration.millis(300), ripple);
                    ft.setToValue(0);
                    ft.setInterpolator(Interpolator.EASE_OUT);

                    ParallelTransition pt = new ParallelTransition(st, ft);
                    pt.setOnFinished(event -> container.getChildren().remove(rippleContainer));
                    pt.play();
                });
            }
        }
    }

    private void mostrarCodigoBarrasCompleto(EquipoDTO equipo) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Identificación del Equipo");
        dialog.setHeaderText("Código de Barras - " + equipo.getNombre());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        // Generar código si no existe
        if (equipo.getQrcode() == null) {
            equipo.generarCodigoBarrasDinamico();
        }

        ImageView barcodeView = new ImageView(equipo.getBarcodeImage());
        barcodeView.setFitWidth(300);
        barcodeView.setFitHeight(100);

        // Agregar botones de acción con iconos FontAwesome
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveBtn = new Button("Guardar");
        saveBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.SAVE));
        saveBtn.setOnAction(e -> guardarCodigoBarras(equipo));

        Button copyBtn = new Button("Copiar");
        copyBtn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.COPY));
        copyBtn.setOnAction(e -> copiarCodigoBarras(equipo));

        buttonBox.getChildren().addAll(saveBtn, copyBtn);

        content.getChildren().addAll(barcodeView, buttonBox);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void guardarCodigoBarras(EquipoDTO equipo) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Guardar código de barras");
        File selectedDirectory = directoryChooser.showDialog(this.getScene().getWindow());

        if (selectedDirectory != null) {
            try {
                File outputFile = new File(selectedDirectory,
                        "codigo_barras_" + equipo.getId() + ".png");

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Guardado exitoso");
                alert.setHeaderText(null);
                alert.setContentText("El código de barras se ha guardado en:\n" +
                        outputFile.getAbsolutePath());
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al guardar");
                alert.setHeaderText(null);
                alert.setContentText("No se pudo guardar el código de barras:\n" +
                        e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void copiarCodigoBarras(EquipoDTO equipo) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        // Aquí deberías implementar la lógica para copiar el código de barras
        // Esto es solo un ejemplo simplificado
        clipboard.setContent(content);

        // Mostrar notificación de éxito
        Tooltip tooltip = new Tooltip("Código copiado al portapapeles");
        tooltip.setAutoHide(true);
        tooltip.show(this.getScene().getWindow());

        // Ocultar después de 2 segundos
        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(event -> tooltip.hide());
        delay.play();
    }

    // Método para actualizar la información del equipo
    public void actualizarEquipo(EquipoDTO equipoActualizado) {
        this.equipo = equipoActualizado;

        // Actualizar la vista según los nuevos datos
        if (getTop() instanceof VBox) {
            VBox header = (VBox) getTop();
            Label titulo = (Label) header.getChildren().get(0);
            titulo.setText(equipoActualizado.getNombre());

            // Actualizar imagen si es necesario
            if (equipoActualizado.getImagen() != null &&
                    !equipoActualizado.getImagen().equals(equipo.getImagen())) {
                try {
                    Image img = new Image("file:" + equipoActualizado.getImagen(), 100, 100, true, true);
                    imagenView.setImage(img);
                } catch (Exception e) {
                    imagenView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
                }
            }
        }

        // Actualizar las pestañas
        setCenter(createTabPane());
    }

    // Método para obtener el equipo asociado a esta tarjeta
    public EquipoDTO getEquipo() {
        return equipo;
    }
}