package tescha.departamento.view.components;

import tescha.departamento.dto.DepartamentoDTO;
import tescha.departamento.view.styles.DepartamentoViewStyles;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.controlsfx.control.textfield.CustomTextField;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class DepartamentoViewComponents {

    private final DepartamentoViewStyles styles;

    public DepartamentoViewComponents() {
        this.styles = new DepartamentoViewStyles();
    }

    // Creación de campos de texto
    public CustomTextField createStyledTextField(String promptText, FontAwesomeSolid icon) {
        CustomTextField field = new CustomTextField();
        field.setPromptText(promptText);
        field.setLeft(createIcon(icon, styles.GRAY_COLOR));
        applyTextFieldStyle(field);
        return field;
    }

    public CustomTextField createSearchField(String promptText) {
        CustomTextField field = new CustomTextField();
        field.setPromptText(promptText);
        field.setLeft(createIcon(FontAwesomeSolid.SEARCH, styles.GRAY_COLOR));
        field.setPrefWidth(300);
        applySearchFieldStyle(field);
        return field;
    }

    public JFXTextArea createStyledTextArea(String promptText, int rows) {
        JFXTextArea area = new JFXTextArea();
        area.setPromptText(promptText);
        area.setWrapText(true);
        area.setPrefRowCount(rows);
        applyTextAreaStyle(area);
        return area;
    }


    public JFXDatePicker createDatePicker(String promptText) {
        JFXDatePicker picker = new JFXDatePicker();
        picker.setPromptText(promptText);
        applyDatePickerStyle(picker);
        return picker;
    }

    public JFXComboBox<String> createComboBox(String promptText, List<String> items) {
        JFXComboBox<String> comboBox = new JFXComboBox<>();
        comboBox.setPromptText(promptText);
        comboBox.setItems(FXCollections.observableArrayList(items));
        applyComboBoxStyle(comboBox);
        return comboBox;
    }

    // Creación de botones
    public JFXButton createActionButton(String text, FontAwesomeSolid icon, String color, String shortcut) {
        JFXButton button = new JFXButton(text);
        button.setGraphic(createIcon(icon, "white"));
        applyActionButtonStyle(button, color);

        Tooltip tooltip = new Tooltip(text + " (" + shortcut + ")");
        button.setTooltip(tooltip);

        return button;
    }

    public JFXButton createToolbarButton(String text, FontAwesomeSolid icon, String shortcut) {
        JFXButton button = new JFXButton();
        button.setGraphic(createIcon(icon, styles.PRIMARY_COLOR));
        applyToolbarButtonStyle(button);

        Tooltip tooltip = new Tooltip(text + " (" + shortcut + ")");
        button.setTooltip(tooltip);

        return button;
    }

    public JFXButton createMiniButton(FontAwesomeSolid icon, String color, String tooltip) {
        JFXButton button = new JFXButton();
        button.setGraphic(createIcon(icon, "white"));
        applyMiniButtonStyle(button, color);

        if (tooltip != null) {
            button.setTooltip(new Tooltip(tooltip));
        }

        return button;
    }

    // Creación de indicadores
    public ProgressIndicator createLoadingIndicator() {
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setMaxSize(32, 32);
        indicator.setStyle("-fx-accent: " + styles.PRIMARY_COLOR + ";");
        indicator.setVisible(false);
        return indicator;
    }

    public Label createStatusLabel() {
        Label label = new Label("Listo");
        label.setStyle("-fx-text-fill: #4a5568; -fx-font-size: 11;");
        return label;
    }

    public Label createCountLabel() {
        Label label = new Label("Total: 0");
        label.setId("countLabel");
        label.setStyle("-fx-text-fill: #4a5568; -fx-font-size: 12; -fx-font-weight: 500;");
        return label;
    }

    public Label createStatsLabel() {
        Label label = new Label("Estadísticas");
        label.setStyle("-fx-text-fill: #4a5568; -fx-font-size: 11; -fx-font-weight: 500;");
        return label;
    }

    // Creación de paneles
    public HBox createToolbar(CustomTextField searchField, JFXButton... buttons) {
        HBox toolbar = new HBox(16);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(12, 24, 12, 24));
        applyToolbarStyle(toolbar);

        // Icono y título
        FontIcon titleIcon = createIcon(FontAwesomeSolid.BUILDING, styles.PRIMARY_COLOR);
        Label titleLabel = new Label("Gestión de Departamentos");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: 700; -fx-text-fill: #2d3748;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().add(titleIcon);
        toolbar.getChildren().add(titleLabel);
        toolbar.getChildren().add(spacer);
        toolbar.getChildren().add(searchField);

        for (JFXButton button : buttons) {
            toolbar.getChildren().add(button);
        }

        return toolbar;
    }

    public VBox createFormPanel(CustomTextField nombreField, JFXTextArea descripcionField,
                                JFXDatePicker fechaPicker, JFXComboBox<String> estadoCombo,
                                HBox buttonPanel) {

        // Título del formulario
        Label formTitle = new Label("Información del Departamento");
        formTitle.setStyle("-fx-text-fill: #1a202c; -fx-font-size: 18; -fx-font-weight: 700;");

        // Sección de campos básicos
        VBox basicSection = createFormSection("Información Básica",
                createFormField("Nombre *", nombreField),
                createFormField("Descripción", descripcionField)
        );

        // Sección de detalles
        VBox detailsSection = createFormSection("Detalles Adicionales",
                createFormField("Estado", estadoCombo),
                createFormField("Fecha de Creación", fechaPicker)
        );

        // Contenedor principal del formulario
        VBox formContent = new VBox(24);
        formContent.getChildren().addAll(basicSection, detailsSection, buttonPanel);
        formContent.setPadding(new Insets(32));
        applyFormPanelStyle(formContent);

        VBox formPanel = new VBox(16);
        formPanel.getChildren().addAll(formTitle, formContent);
        formPanel.setPrefWidth(420);

        return formPanel;
    }

    public VBox createStatsPanel(Label statsLabel) {
        VBox statsPanel = new VBox(12);
        statsPanel.setPadding(new Insets(16, 24, 16, 24));
        applyStatsPanelStyle(statsPanel);

        Label statsTitle = new Label("Estadísticas Rápidas");
        statsTitle.setStyle("-fx-font-size: 14; -fx-font-weight: 600; -fx-text-fill: #2d3748;");

        statsPanel.getChildren().addAll(statsTitle, statsLabel);
        return statsPanel;
    }

    public HBox createStatusBar(Label statusLabel, Label statsLabel) {
        HBox statusBar = new HBox();
        statusBar.setPadding(new Insets(8, 16, 8, 16));
        applyStatusBarStyle(statusBar);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusBar.getChildren().addAll(statusLabel, spacer, statsLabel);
        return statusBar;
    }

    // Creación de tabla
    public TableView<DepartamentoDTO> createDepartamentoTable() {
        TableView<DepartamentoDTO> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(500);
        table.setPlaceholder(createEmptyStatePane());
        applyTableStyle(table);
        return table;
    }

    public TableRow<DepartamentoDTO> createTableRow(Consumer<DepartamentoDTO> onDoubleClick) {
        return new TableRow<DepartamentoDTO>() {
            @Override
            protected void updateItem(DepartamentoDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setStyle("");
                    setOnMouseClicked(null);
                } else {
                    // Alternar colores de fila
                    if (getIndex() % 2 == 0) {
                        setStyle("-fx-background-color: rgba(247, 250, 252, 0.5);");
                    } else {
                        setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");
                    }

                    // Doble clic
                    setOnMouseClicked(e -> {
                        if (e.getClickCount() == 2) {
                            onDoubleClick.accept(item);
                        }
                    });
                }
            }
        };
    }

    // Columnas de tabla especializadas
    public TableColumn<DepartamentoDTO, Integer> createIdColumn() {
        TableColumn<DepartamentoDTO, Integer> column = new TableColumn<>();
        column.setGraphic(createColumnHeader("ID", FontAwesomeSolid.HASHTAG, "#64748b"));
        column.setCellValueFactory(new PropertyValueFactory<>("id"));
        column.setPrefWidth(80);

        column.setCellFactory(col -> new TableCell<DepartamentoDTO, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    HBox cell = new HBox(8);
                    cell.setAlignment(Pos.CENTER_LEFT);

                    Circle indicator = new Circle(4);
                    indicator.setFill(Color.web(styles.PRIMARY_COLOR));

                    Label idLabel = new Label(item.toString());
                    idLabel.setStyle("-fx-font-weight: 600; -fx-text-fill: #374151;");

                    cell.getChildren().addAll(indicator, idLabel);
                    setGraphic(cell);
                }
            }
        });

        return column;
    }

    public TableColumn<DepartamentoDTO, String> createNombreColumn() {
        TableColumn<DepartamentoDTO, String> column = new TableColumn<>();
        column.setGraphic(createColumnHeader("Nombre", FontAwesomeSolid.BUILDING, styles.PRIMARY_COLOR));
        column.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        column.setCellFactory(col -> new TableCell<DepartamentoDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-font-weight: 600; -fx-text-fill: #1a202c;");
                }
            }
        });

        return column;
    }

    public TableColumn<DepartamentoDTO, String> createDescripcionColumn() {
        TableColumn<DepartamentoDTO, String> column = new TableColumn<>();
        column.setGraphic(createColumnHeader("Descripción", FontAwesomeSolid.INFO_CIRCLE, "#6366f1"));
        column.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        column.setCellFactory(col -> new TableCell<DepartamentoDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    String displayText = item.length() > 50 ? item.substring(0, 47) + "..." : item;
                    setText(displayText);
                    setTooltip(new Tooltip(item));
                    setStyle("-fx-text-fill: #4a5568;");
                }
            }
        });

        return column;
    }

    public TableColumn<DepartamentoDTO, String> createEstadoColumn() {
        TableColumn<DepartamentoDTO, String> column = new TableColumn<>();
        column.setGraphic(createColumnHeader("Estado", FontAwesomeSolid.CIRCLE, styles.SUCCESS_COLOR));
        column.setCellValueFactory(new PropertyValueFactory<>("estado"));
        column.setPrefWidth(100);

        column.setCellFactory(col -> new TableCell<DepartamentoDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    HBox cell = new HBox(6);
                    cell.setAlignment(Pos.CENTER_LEFT);

                    Circle statusIndicator = new Circle(5);
                    String color = getStatusColor(item);
                    statusIndicator.setFill(Color.web(color));

                    Label statusLabel = new Label(item);
                    statusLabel.setStyle("-fx-font-weight: 500; -fx-text-fill: " + color + ";");

                    cell.getChildren().addAll(statusIndicator, statusLabel);
                    setGraphic(cell);
                }
            }
        });

        return column;
    }

    public TableColumn<DepartamentoDTO, String> createResponsableColumn() {
        TableColumn<DepartamentoDTO, String> column = new TableColumn<>();
        column.setGraphic(createColumnHeader("Responsable", FontAwesomeSolid.USER, styles.WARNING_COLOR));
        column.setCellValueFactory(new PropertyValueFactory<>("responsable"));
        column.setPrefWidth(150);
        return column;
    }

    public TableColumn<DepartamentoDTO, Double> createPresupuestoColumn() {
        TableColumn<DepartamentoDTO, Double> column = new TableColumn<>();
        column.setGraphic(createColumnHeader("Presupuesto", FontAwesomeSolid.DOLLAR_SIGN, styles.SUCCESS_COLOR));
        column.setCellValueFactory(new PropertyValueFactory<>("presupuesto"));
        column.setPrefWidth(120);

        column.setCellFactory(col -> new TableCell<DepartamentoDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                    setStyle("-fx-text-fill: " + styles.SUCCESS_COLOR + "; -fx-font-weight: 600;");
                }
            }
        });

        return column;
    }

    public TableColumn<DepartamentoDTO, LocalDateTime> createFechaColumn() {
        TableColumn<DepartamentoDTO, LocalDateTime> column = new TableColumn<>();
        column.setGraphic(createColumnHeader("Fecha", FontAwesomeSolid.CALENDAR, styles.GRAY_COLOR));
        column.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));
        column.setPrefWidth(120);

        column.setCellFactory(col -> new TableCell<DepartamentoDTO, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    setStyle("-fx-text-fill: #4a5568;");
                }
            }
        });

        return column;
    }

    public TableColumn<DepartamentoDTO, Void> createActionColumn(
            Consumer<DepartamentoDTO> onEdit,
            Consumer<DepartamentoDTO> onDelete,
            Consumer<DepartamentoDTO> onDuplicate) {

        TableColumn<DepartamentoDTO, Void> column = new TableColumn<>();
        column.setGraphic(createColumnHeader("Acciones", FontAwesomeSolid.COG, styles.GRAY_COLOR));
        column.setPrefWidth(140);

        column.setCellFactory(col -> new TableCell<DepartamentoDTO, Void>() {
            private final HBox actionBox = new HBox(4);
            private final JFXButton editBtn;
            private final JFXButton deleteBtn;
            private final JFXButton duplicateBtn;

            {
                editBtn = createMiniButton(FontAwesomeSolid.EDIT, styles.PRIMARY_COLOR, "Editar");
                deleteBtn = createMiniButton(FontAwesomeSolid.TRASH_ALT, styles.DANGER_COLOR, "Eliminar");
                duplicateBtn = createMiniButton(FontAwesomeSolid.COPY, styles.WARNING_COLOR, "Duplicar");

                editBtn.setOnAction(e -> {
                    DepartamentoDTO dept = getTableView().getItems().get(getIndex());
                    onEdit.accept(dept);
                });

                deleteBtn.setOnAction(e -> {
                    DepartamentoDTO dept = getTableView().getItems().get(getIndex());
                    onDelete.accept(dept);
                });

                duplicateBtn.setOnAction(e -> {
                    DepartamentoDTO dept = getTableView().getItems().get(getIndex());
                    onDuplicate.accept(dept);
                });

                actionBox.setAlignment(Pos.CENTER);
                actionBox.getChildren().addAll(editBtn, deleteBtn, duplicateBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionBox);
            }
        });

        return column;
    }

    // Diálogos
    public Dialog<String> createExportDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Exportar Datos");
        dialog.setHeaderText("Seleccione el formato de exportación");

        ButtonType exportButtonType = new ButtonType("Exportar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(exportButtonType, ButtonType.CANCEL);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        ToggleGroup formatGroup = new ToggleGroup();
        RadioButton csvRadio = new RadioButton("CSV (Comma Separated Values)");
        RadioButton excelRadio = new RadioButton("Excel (XLSX)");
        RadioButton pdfRadio = new RadioButton("PDF (Portable Document Format)");
        RadioButton jsonRadio = new RadioButton("JSON (JavaScript Object Notation)");

        csvRadio.setToggleGroup(formatGroup);
        excelRadio.setToggleGroup(formatGroup);
        pdfRadio.setToggleGroup(formatGroup);
        jsonRadio.setToggleGroup(formatGroup);

        csvRadio.setSelected(true);

        content.getChildren().addAll(csvRadio, excelRadio, pdfRadio, jsonRadio);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == exportButtonType) {
                RadioButton selected = (RadioButton) formatGroup.getSelectedToggle();
                if (selected == csvRadio) return "csv";
                if (selected == excelRadio) return "excel";
                if (selected == pdfRadio) return "pdf";
                if (selected == jsonRadio) return "json";
            }
            return null;
        });

        return dialog;
    }

    public Dialog<File> createImportDialog() {
        Dialog<File> dialog = new Dialog<>();
        dialog.setTitle("Importar Datos");
        dialog.setHeaderText("Seleccione el archivo a importar");

        ButtonType importButtonType = new ButtonType("Importar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(importButtonType, ButtonType.CANCEL);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        Label fileLabel = new Label("Archivo seleccionado: Ninguno");
        JFXButton selectFileBtn = new JFXButton("Seleccionar Archivo");
        selectFileBtn.setStyle("-fx-background-color: " + styles.PRIMARY_COLOR + "; -fx-text-fill: white;");

        final File[] selectedFile = {null};

        selectFileBtn.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Seleccionar archivo");
            fileChooser.getExtensionFilters().addAll(
                    new javafx.stage.FileChooser.ExtensionFilter("Archivos CSV", "*.csv"),
                    new javafx.stage.FileChooser.ExtensionFilter("Archivos Excel", "*.xlsx", "*.xls"),
                    new javafx.stage.FileChooser.ExtensionFilter("Archivos JSON", "*.json"),
                    new javafx.stage.FileChooser.ExtensionFilter("Todos los archivos", "*.*")
            );

            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                selectedFile[0] = file;
                fileLabel.setText("Archivo seleccionado: " + file.getName());
            }
        });

        content.getChildren().addAll(selectFileBtn, fileLabel);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == importButtonType) {
                return selectedFile[0];
            }
            return null;
        });

        return dialog;
    }

    public Dialog<String> createReportDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Generar Reporte");
        dialog.setHeaderText("Seleccione el tipo de reporte");

        ButtonType generateButtonType = new ButtonType("Generar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(generateButtonType, ButtonType.CANCEL);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        ToggleGroup reportGroup = new ToggleGroup();
        RadioButton summaryRadio = new RadioButton("Reporte Resumen");
        RadioButton detailedRadio = new RadioButton("Reporte Detallado");
        RadioButton statisticsRadio = new RadioButton("Reporte Estadístico");

        summaryRadio.setToggleGroup(reportGroup);
        detailedRadio.setToggleGroup(reportGroup);
        statisticsRadio.setToggleGroup(reportGroup);

        summaryRadio.setSelected(true);

        content.getChildren().addAll(summaryRadio, detailedRadio, statisticsRadio);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == generateButtonType) {
                RadioButton selected = (RadioButton) reportGroup.getSelectedToggle();
                if (selected == summaryRadio) return "summary";
                if (selected == detailedRadio) return "detailed";
                if (selected == statisticsRadio) return "statistics";
            }
            return null;
        });

        return dialog;
    }

    public Dialog<Void> createConfigDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Configuración");
        dialog.setHeaderText("Configuración de la aplicación");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));

        CheckBox autoRefreshCheck = new CheckBox("Actualización automática");
        CheckBox notificationsCheck = new CheckBox("Mostrar notificaciones");
        CheckBox animationsCheck = new CheckBox("Habilitar animaciones");

        content.getChildren().addAll(autoRefreshCheck, notificationsCheck, animationsCheck);
        dialog.getDialogPane().setContent(content);

        return dialog;
    }

    public Dialog<Void> createDetailDialog(DepartamentoDTO departamento) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalles del Departamento");
        dialog.setHeaderText("Información completa: " + departamento.getNombre());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);

        // Información detallada
        addDetailRow(content, "ID:", String.valueOf(departamento.getId()));
        addDetailRow(content, "Nombre:", departamento.getNombre());
        addDetailRow(content, "Descripción:", departamento.getDescripcion());

        dialog.getDialogPane().setContent(content);
        return dialog;
    }

    // Métodos auxiliares
    public FontIcon createIcon(FontAwesomeSolid iconType, String color) {
        FontIcon icon = new FontIcon(iconType);
        icon.setIconSize(16);
        icon.setIconColor(Color.web(color));
        return icon;
    }

    public void setFieldValidationState(Control field, String state) {
        String borderColor;
        switch (state) {
            case "error":
                borderColor = styles.DANGER_COLOR;
                break;
            case "warning":
                borderColor = styles.WARNING_COLOR;
                break;
            case "success":
                borderColor = styles.SUCCESS_COLOR;
                break;
            default:
                borderColor = "#e2e8f0";
        }

        String currentStyle = field.getStyle();
        String newStyle = currentStyle.replaceAll("-fx-border-color: [^;]+;", "") +
                "-fx-border-color: " + borderColor + "; -fx-border-width: 2;";
        field.setStyle(newStyle);
    }

    private VBox createColumnHeader(String text, FontAwesomeSolid icon, String color) {
        FontIcon headerIcon = createIcon(icon, color);
        Label headerLabel = new Label(text);
        headerLabel.setStyle("-fx-text-fill: #374151; -fx-font-weight: 700; -fx-font-size: 12;");

        VBox header = new VBox(2);
        header.setAlignment(Pos.CENTER);
        header.getChildren().addAll(headerIcon, headerLabel);
        return header;
    }

    private VBox createEmptyStatePane() {
        FontIcon emptyIcon = createIcon(FontAwesomeSolid.INBOX, styles.GRAY_COLOR);
        emptyIcon.setIconSize(48);

        Label emptyLabel = new Label("No hay departamentos registrados");
        emptyLabel.setStyle("-fx-text-fill: #718096; -fx-font-size: 16; -fx-font-weight: 500;");

        Label emptySubLabel = new Label("Comienza agregando un nuevo departamento");
        emptySubLabel.setStyle("-fx-text-fill: #a0aec0; -fx-font-size: 12;");

        VBox emptyState = new VBox(12);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.getChildren().addAll(emptyIcon, emptyLabel, emptySubLabel);
        emptyState.setPadding(new Insets(40));

        return emptyState;
    }

    private VBox createFormSection(String title, VBox... fields) {
        Label sectionTitle = new Label(title);
        sectionTitle.setStyle("-fx-font-size: 14; -fx-font-weight: 600; -fx-text-fill: #2d3748;");

        VBox section = new VBox(12);
        section.getChildren().add(sectionTitle);

        for (VBox field : fields) {
            section.getChildren().add(field);
        }

        return section;
    }

    private VBox createFormField(String labelText, Control field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #2d3748; -fx-font-weight: 600; -fx-font-size: 13;");

        VBox fieldContainer = new VBox(6);
        fieldContainer.getChildren().addAll(label, field);
        return fieldContainer;
    }

    private void addDetailRow(VBox container, String label, String value) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);

        Label labelControl = new Label(label);
        labelControl.setStyle("-fx-font-weight: 600; -fx-text-fill: #4a5568;");
        labelControl.setPrefWidth(120);

        Label valueControl = new Label(value != null ? value : "N/A");
        valueControl.setStyle("-fx-text-fill: #2d3748;");

        row.getChildren().addAll(labelControl, valueControl);
        container.getChildren().add(row);
    }

    private String getStatusColor(String status) {
        if (status == null) return styles.GRAY_COLOR;

        switch (status.toLowerCase()) {
            case "activo":
                return styles.SUCCESS_COLOR;
            case "inactivo":
                return styles.DANGER_COLOR;
            case "en revisión":
                return styles.WARNING_COLOR;
            case "suspendido":
                return styles.GRAY_COLOR;
            default:
                return styles.PRIMARY_COLOR;
        }
    }

    // Métodos de aplicación de estilos
    private void applyTextFieldStyle(CustomTextField field) {
        field.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 14 16;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: 500;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 4, 0, 0, 1);"
        );
    }

    private void applySearchFieldStyle(CustomTextField field) {
        field.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 20;" +
                        "-fx-padding: 8 16;" +
                        "-fx-font-size: 13;"
        );
    }

    private void applyTextAreaStyle(JFXTextArea area) {
        area.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 14;" +
                        "-fx-font-size: 14;" +
                        "-fx-font-weight: 500;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 4, 0, 0, 1);"
        );
    }

    private void applyJFXTextFieldStyle(JFXTextField field) {
        field.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 14 16;" +
                        "-fx-font-size: 14;"
        );
    }

    private void applyDatePickerStyle(JFXDatePicker picker) {
        picker.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 14 16;"
        );
    }

    private void applyComboBoxStyle(JFXComboBox<?> comboBox) {
        comboBox.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 14 16;"
        );
    }

    private void applyActionButtonStyle(JFXButton button, String color) {
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: 600;" +
                        "-fx-font-size: 13;" +
                        "-fx-background-radius: 10;" +
                        "-fx-padding: 12 20;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);"
        );
    }

    private void applyToolbarButtonStyle(JFXButton button) {
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 8;" +
                        "-fx-cursor: hand;"
        );
    }

    private void applyMiniButtonStyle(JFXButton button, String color) {
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-background-radius: 6;" +
                        "-fx-min-width: 28;" +
                        "-fx-min-height: 28;" +
                        "-fx-max-width: 28;" +
                        "-fx-max-height: 28;" +
                        "-fx-cursor: hand;"
        );
    }

    private void applyToolbarStyle(HBox toolbar) {
        toolbar.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                        "-fx-background-radius: 12 12 0 0;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 0 0 1 0;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);"
        );
    }

    private void applyFormPanelStyle(VBox panel) {
        panel.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                        "-fx-background-radius: 16;" +
                        "-fx-border-color: rgba(226, 232, 240, 0.8);" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 16;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 20, 0, 0, 4);"
        );
    }

    private void applyStatsPanelStyle(VBox panel) {
        panel.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.9);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 8, 0, 0, 2);"
        );
    }

    private void applyStatusBarStyle(HBox statusBar) {
        statusBar.setStyle(
                "-fx-background-color: rgba(247, 250, 252, 0.9);" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 1 0 0 0;"
        );
    }

    private void applyTableStyle(TableView<?> table) {
        table.setStyle(
                "-fx-background-color: rgba(255, 255, 255, 0.95);" +
                        "-fx-background-radius: 0 0 12 12;" +
                        "-fx-border-color: #e2e8f0;" +
                        "-fx-border-width: 0 1 1 1;" +
                        "-fx-border-radius: 0 0 12 12;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2);" +
                        "-fx-table-cell-border-color: #f1f5f9;" +
                        "-fx-selection-bar: " + styles.PRIMARY_COLOR + ";" +
                        "-fx-selection-bar-non-focused: " + styles.GRAY_COLOR + ";"
        );
    }
}