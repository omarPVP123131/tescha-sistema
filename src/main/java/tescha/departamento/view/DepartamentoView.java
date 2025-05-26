package tescha.departamento.view;

import tescha.departamento.controller.DepartamentoController;
import tescha.departamento.dto.DepartamentoDTO;
import tescha.Components.AlertUtils;

import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.glyphfont.Glyph;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

// Importar AnimateFX
import animatefx.animation.*;

import java.sql.SQLException;

public class DepartamentoView {
    private DepartamentoController controller;
    private BorderPane view;
    private NotificationPane notificationPane;

    private TableView<DepartamentoDTO> table;
    private ObservableList<DepartamentoDTO> departamentos;

    private CustomTextField nombreField;
    private JFXTextArea descripcionField;
    private JFXButton agregarBtn;
    private JFXButton actualizarBtn;
    private JFXButton eliminarBtn;
    private JFXButton limpiarBtn;

    // Contenedores para animaciones
    private VBox leftPanel;
    private VBox rightPanel;
    private GridPane form;
    private HBox buttonBox;

    public DepartamentoView(DepartamentoController controller) {
        this.controller = controller;
        initializeUI();
        loadData();
        setupListeners();
        setupAnimations();
    }

    private void initializeUI() {
        view = new BorderPane();

        // Crear NotificationPane para mensajes elegantes
        notificationPane = new NotificationPane();

        // Fondo degradado espectacular
        setupGradientBackground();

        // Crear tabla con efectos
        createEnhancedTable();

        // Crear formulario con animaciones
        createEnhancedForm();

        // Crear botones con efectos hover
        createEnhancedButtons();

        // Diseño principal con efectos de profundidad
        setupMainLayout();
    }

    private void setupGradientBackground() {
        // Fondo degradado dinámico
        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#667eea")),
                new Stop(0.5, Color.web("#764ba2")),
                new Stop(1, Color.web("#f093fb"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, stops);

        Rectangle background = new Rectangle();
        background.setFill(gradient);
        background.widthProperty().bind(view.widthProperty());
        background.heightProperty().bind(view.heightProperty());

        // Animación de gradiente
        Timeline colorAnimation = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(background.fillProperty(), gradient)
                ),
                new KeyFrame(Duration.seconds(5),
                        new KeyValue(background.fillProperty(),
                                new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                                        new Stop(0, Color.web("#f093fb")),
                                        new Stop(0.5, Color.web("#f5576c")),
                                        new Stop(1, Color.web("#4facfe"))
                                )
                        )
                )
        );
        colorAnimation.setCycleCount(Timeline.INDEFINITE);
        colorAnimation.setAutoReverse(true);
        colorAnimation.play();

        view.getChildren().add(background);
        background.toBack();
    }

    private void createEnhancedTable() {
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Estilo glassmorphism para la tabla
        table.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-background-radius: 15;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 15;"
        );

        // Efecto de cristal
        BoxBlur blur = new BoxBlur(3, 3, 1);
        table.setEffect(blur);

        // Columnas con iconos
        TableColumn<DepartamentoDTO, Integer> idCol = new TableColumn<>();
        Label idHeader = new Label("ID");
        idHeader.setGraphic(new FontIcon(FontAwesomeSolid.HASHTAG));
        idHeader.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        idCol.setGraphic(idHeader);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<DepartamentoDTO, String> nombreCol = new TableColumn<>();
        Label nombreHeader = new Label("Nombre");
        nombreHeader.setGraphic(new FontIcon(FontAwesomeSolid.BUILDING));
        nombreHeader.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        nombreCol.setGraphic(nombreHeader);
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<DepartamentoDTO, String> descCol = new TableColumn<>();
        Label descHeader = new Label("Descripción");
        descHeader.setGraphic(new FontIcon(FontAwesomeSolid.INFO_CIRCLE));
        descHeader.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        descCol.setGraphic(descHeader);
        descCol.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        table.getColumns().addAll(idCol, nombreCol, descCol);

        // Efecto de hover en filas
        table.setRowFactory(tv -> {
            TableRow<DepartamentoDTO> row = new TableRow<>();
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    new Pulse(row).play();
                    row.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);");
                }
            });
            row.setOnMouseExited(e -> {
                row.setStyle("");
            });
            return row;
        });
    }

    private void createEnhancedForm() {
        form = new GridPane();
        form.setVgap(20);
        form.setHgap(20);
        form.setPadding(new Insets(30));

        // Fondo glassmorphism para el formulario
        form.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.15);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.3);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 20;"
        );

        // Efecto de profundidad
        JFXDepthManager.setDepth(form, 4);

        // Campo nombre con icono
        nombreField = new CustomTextField();
        nombreField.setPromptText("Nombre del departamento");
        nombreField.setLeft(new FontIcon(FontAwesomeSolid.BUILDING));
        nombreField.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-font-size: 14;" +
                        "-fx-padding: 15;"
        );

        // Animación de focus
        nombreField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                new RubberBand(nombreField).play();
                nombreField.setStyle(nombreField.getStyle() + "-fx-border-color: #667eea; -fx-border-width: 2;");
            } else {
                nombreField.setStyle(nombreField.getStyle().replace("-fx-border-color: #667eea; -fx-border-width: 2;", ""));
            }
        });

        descripcionField = new JFXTextArea();
        descripcionField.setPromptText("Descripción del departamento");
        descripcionField.setLabelFloat(true);
        descripcionField.setWrapText(true);
        descripcionField.setPrefRowCount(4);
        descripcionField.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-font-size: 14;"
        );

        // Labels con iconos y efectos
        Label nombreLabel = new Label("Nombre del Departamento");
        nombreLabel.setGraphic(new FontIcon(FontAwesomeSolid.BUILDING));
        nombreLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");

        Label descLabel = new Label("Descripción");
        descLabel.setGraphic(new FontIcon(FontAwesomeSolid.ALIGN_LEFT));
        descLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16;");

        form.add(nombreLabel, 0, 0);
        form.add(nombreField, 0, 1);
        form.add(descLabel, 0, 2);
        form.add(descripcionField, 0, 3);
    }

    private void createEnhancedButtons() {
        buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        // Botón Agregar - Verde vibrante
        agregarBtn = createStylizedButton("Agregar", FontAwesomeSolid.PLUS, "#00E676");

        // Botón Actualizar - Azul brillante
        actualizarBtn = createStylizedButton("Actualizar", FontAwesomeSolid.EDIT, "#2196F3");
        actualizarBtn.setDisable(true);

        // Botón Eliminar - Rojo vibrante
        eliminarBtn = createStylizedButton("Eliminar", FontAwesomeSolid.TRASH_ALT, "#FF5252");
        eliminarBtn.setDisable(true);

        // Botón Limpiar - Púrpura elegante
        limpiarBtn = createStylizedButton("Limpiar", FontAwesomeSolid.BROOM, "#9C27B0");

        buttonBox.getChildren().addAll(agregarBtn, actualizarBtn, eliminarBtn, limpiarBtn);
        form.add(buttonBox, 0, 4);
    }

    private JFXButton createStylizedButton(String text, FontAwesomeSolid icon, String color) {
        JFXButton button = new JFXButton(text);
        FontIcon fontIcon = new FontIcon(icon);
        fontIcon.setIconSize(18);
        fontIcon.setIconColor(Color.WHITE);
        button.setGraphic(fontIcon);

        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14;" +
                        "-fx-background-radius: 25;" +
                        "-fx-padding: 12 24 12 24;" +
                        "-fx-cursor: hand;"
        );

        // Efectos hover espectaculares
        button.setOnMouseEntered(e -> {
            new Pulse(button).play();
            button.setStyle(button.getStyle() +
                    "-fx-effect: dropshadow(gaussian, " + color + ", 15, 0.5, 0, 0);" +
                    "-fx-scale-x: 1.1; -fx-scale-y: 1.1;"
            );
        });

        button.setOnMouseExited(e -> {
            button.setStyle(button.getStyle().replaceAll("-fx-effect: [^;]+;", "").replaceAll("-fx-scale-[xy]: [^;]+;", ""));
        });

        // Animación de click
        button.setOnMousePressed(e -> new Bounce(button).play());

        return button;
    }

    private void setupMainLayout() {
        // Panel izquierdo - Lista
        Label titleLeft = new Label("📋 Departamentos Registrados");
        titleLeft.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 20;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 0 0 15 0;"
        );

        leftPanel = new VBox(20);
        leftPanel.getChildren().addAll(titleLeft, table);
        leftPanel.setPrefWidth(450);
        leftPanel.setPadding(new Insets(20));

        // Efecto glassmorphism
        leftPanel.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 20;"
        );

        // Panel derecho - Formulario
        Label titleRight = new Label("⚙️ Gestión de Departamentos");
        titleRight.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 20;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 0 0 20 0;"
        );

        rightPanel = new VBox(20);
        rightPanel.getChildren().addAll(titleRight, form);
        rightPanel.setPrefWidth(400);
        rightPanel.setPadding(new Insets(20));

        rightPanel.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 20;"
        );

        HBox mainContent = new HBox(30);
        mainContent.getChildren().addAll(leftPanel, rightPanel);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(40));

        notificationPane.setContent(mainContent);
        view.setCenter(notificationPane);
    }

    private void setupAnimations() {
        // Animación de entrada para paneles
        new SlideInLeft(leftPanel).setDelay(Duration.millis(200)).play();
        new SlideInRight(rightPanel).setDelay(Duration.millis(400)).play();

        // Animación flotante continua para botones
        Timeline floatingAnimation = new Timeline();
        for (int i = 0; i < buttonBox.getChildren().size(); i++) {
            final int index = i;
            floatingAnimation.getKeyFrames().addAll(
                    new KeyFrame(Duration.seconds(2 + index * 0.2),
                            new KeyValue(buttonBox.getChildren().get(index).translateYProperty(), -5)
                    ),
                    new KeyFrame(Duration.seconds(4 + index * 0.2),
                            new KeyValue(buttonBox.getChildren().get(index).translateYProperty(), 0)
                    )
            );
        }
        floatingAnimation.setCycleCount(Timeline.INDEFINITE);
        floatingAnimation.play();
    }

    private void showNotification(String message, String type) {
        // Crear el ícono según el tipo
        FontIcon icon;
        if ("success".equals(type)) {
            icon = new FontIcon(FontAwesomeSolid.CHECK_CIRCLE);
        } else if ("error".equals(type)) {
            icon = new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
        } else {
            icon = new FontIcon(FontAwesomeSolid.INFO_CIRCLE);
        }
        icon.setIconSize(24);

        // Construir y mostrar la notificación
        Notifications notification = Notifications.create()
                .title(type.equals("success") ? "✔️ Éxito" : "⚠️ Aviso")
                .text(message)
                .graphic(icon)                        // <-- aquí tu .graphic(icon)
                .hideAfter(Duration.seconds(3))
                .position(Pos.TOP_RIGHT);

        if ("success".equals(type)) {
            notification.showInformation();
        } else if ("error".equals(type)) {
            notification.showError();
        } else {
            notification.showWarning();
        }
    }

    private void loadData() {
        try {
            departamentos = FXCollections.observableArrayList(controller.obtenerTodosLosDepartamentos());
            table.setItems(departamentos);

            // Animación de carga
            new FadeIn(table).play();

        } catch (SQLException e) {
            showNotification("Error al cargar departamentos", "error");
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        // Selección en la tabla con animaciones
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                // Animación de formulario
                new FlipInX(form).play();

                nombreField.setText(newSelection.getNombre());
                descripcionField.setText(newSelection.getDescripcion());
                actualizarBtn.setDisable(false);
                eliminarBtn.setDisable(false);
                agregarBtn.setDisable(true);

                // Highlight de botones activos
                new Tada(actualizarBtn).play();
                new Tada(eliminarBtn).play();
            }
        });

        // Botón limpiar con animación
        limpiarBtn.setOnAction(e -> {
            // 1) creas la animación
            RotateOut rotOut = new RotateOut(form);
            // 2) le asignas el callback
            rotOut.setOnFinished(evt -> {
                table.getSelectionModel().clearSelection();
                clearForm();
                new RotateIn(form).play();   // aquí RotateIn sí puede encadenar play()
            });
            // 3) la arrancas
            rotOut.play();
        });


        // Botón agregar con efectos
        agregarBtn.setOnAction(e -> {
            try {
                DepartamentoDTO nuevo = new DepartamentoDTO();
                nuevo.setNombre(nombreField.getText());
                nuevo.setDescripcion(descripcionField.getText());

                controller.agregarDepartamento(nuevo);

                // Animaciones de éxito
                new Flash(agregarBtn).play();
                loadData();
                clearForm();
                showNotification("¡Departamento agregado exitosamente! 🎉", "success");

            } catch (IllegalArgumentException ex) {
                showNotification("Error: " + ex.getMessage(), "error");
                new Shake(form).play();
            } catch (SQLException ex) {
                showNotification("Error al agregar departamento", "error");
                new Shake(form).play();
                ex.printStackTrace();
            }
        });

        // Botón actualizar con efectos
        actualizarBtn.setOnAction(e -> {
            DepartamentoDTO seleccionado = table.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                try {
                    seleccionado.setNombre(nombreField.getText());
                    seleccionado.setDescripcion(descripcionField.getText());

                    controller.actualizarDepartamento(seleccionado);

                    new Flash(actualizarBtn).play();
                    loadData();
                    clearForm();
                    showNotification("¡Departamento actualizado! ✨", "success");

                } catch (IllegalArgumentException ex) {
                    showNotification("Error: " + ex.getMessage(), "error");
                    new Shake(form).play();
                } catch (SQLException ex) {
                    showNotification("Error al actualizar departamento", "error");
                    new Shake(form).play();
                    ex.printStackTrace();
                }
            }
        });

        // Botón eliminar con confirmación animada
        eliminarBtn.setOnAction(e -> {
            DepartamentoDTO seleccionado = table.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                // Animación de alerta
                new Wobble(eliminarBtn).play();

                if (AlertUtils.showConfirmation("🗑️ Confirmar Eliminación",
                        "¿Está seguro de eliminar el departamento '" + seleccionado.getNombre() + "'?\n\nEsta acción no se puede deshacer.")) {

                    try {
                        controller.eliminarDepartamento(seleccionado.getId());

                        Hinge hinge = new Hinge(/* el nodo o condición que pasabas */);
                        hinge.setOnFinished(evt -> {
                            loadData();
                            clearForm();
                            showNotification("Departamento eliminado correctamente 🗑️", "success");
                        });
                        hinge.play();
                    } catch (SQLException ex) {
                        showNotification("Error al eliminar departamento", "error");
                        new Shake(eliminarBtn).play();
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void clearForm() {
        nombreField.clear();
        descripcionField.clear();
        agregarBtn.setDisable(false);
        actualizarBtn.setDisable(true);
        eliminarBtn.setDisable(true);

        FadeOut fadeOut = new FadeOut(form);
        fadeOut.setOnFinished(e -> {
            // cuando termine el fade-out, haces el fade-in
            new FadeIn(form).play();
        });
        fadeOut.play();    }


    public BorderPane getView() {
        return view;
    }
}