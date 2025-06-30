package tescha.departamento.view;

import io.github.palexdev.materialfx.enums.NotificationPos;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.util.StringConverter;
import tescha.database.DatabaseManager;
import tescha.departamento.controller.DepartamentoController;
import tescha.departamento.dto.DepartamentoDTO;
import tescha.departamento.view.components.DepartamentoViewComponents;
import tescha.departamento.view.services.DepartamentoExportService;
import tescha.departamento.view.animations.DepartamentoViewAnimations;
import tescha.departamento.view.styles.DepartamentoViewStyles;
import tescha.Components.AlertUtils;
import animatefx.animation.*;
import com.jfoenix.controls.*;
import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.controlsfx.control.textfield.CustomTextField;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DepartamentoView {

    // Servicios y componentes
    private final DepartamentoController controller;
    private final DepartamentoViewComponents components;
    private final DepartamentoExportService exportService;
    private final DepartamentoViewAnimations animations;
    private final DepartamentoViewStyles styles;

    // Datos y estado
    private ObservableList<DepartamentoDTO> departamentos;
    private ObservableList<DepartamentoDTO> filteredDepartamentos;
    private DepartamentoDTO selectedDepartamento;
    private boolean isEditMode = false;

    // Componentes UI principales
    private BorderPane view;
    private TableView<DepartamentoDTO> table;
    private CustomTextField nombreField;
    private CustomTextField searchField;
    private JFXTextArea descripcionField;
    private JFXDatePicker fechaCreacionPicker;
    private JFXComboBox<String> estadoComboBox;


    // Botones
    private JFXButton agregarBtn;
    private JFXButton actualizarBtn;
    private JFXButton eliminarBtn;
    private JFXButton limpiarBtn;
    private JFXButton refreshBtn;
    private JFXButton exportBtn;
    private JFXButton importBtn;
    private JFXButton reportBtn;
    private JFXButton configBtn;

    // Paneles
    private VBox leftPanel;
    private VBox rightPanel;
    private HBox toolbarBox;
    private VBox statsPanel;

    // Indicadores y estado
    private Label statusLabel;
    private ProgressIndicator loadingIndicator;
    private Label countLabel;
    private Label statsLabel;

    // Configuración de teclado
    private final KeyCodeCombination[] shortcuts = {
            new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), // Nuevo
            new KeyCodeCombination(KeyCode.U, KeyCombination.CONTROL_DOWN), // Actualizar
            new KeyCodeCombination(KeyCode.DELETE), // Eliminar
            new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), // Limpiar
            new KeyCodeCombination(KeyCode.F5), // Refrescar
            new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), // Buscar
            new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN), // Exportar
            new KeyCodeCombination(KeyCode.I, KeyCombination.CONTROL_DOWN), // Importar
    };

    public DepartamentoView(DepartamentoController controller) {
        this.controller = controller;
        this.components = new DepartamentoViewComponents();
        this.exportService = new DepartamentoExportService();
        this.animations = new DepartamentoViewAnimations();
        this.styles = new DepartamentoViewStyles();

        initializeData();
        initializeUI();
        setupEventHandlers();
        setupKeyboardShortcuts();
        loadInitialData();
    }

    private void initializeData() {
        departamentos = FXCollections.observableArrayList();
        filteredDepartamentos = FXCollections.observableArrayList();
    }

    private void initializeUI() {
        view = new BorderPane();
        applyMainStyles();

        createComponents();
        createToolbar();
        createTable();
        createFormPanel();
        createStatsPanel();
        createStatusBar();
        setupLayout();

        animations.initializeAnimations(view);
    }

    private void applyMainStyles() {
        view.setBackground(styles.createGradientBackground());
        view.setStyle(styles.getMainContainerStyle());
    }

    private void createComponents() {
        // Primero crea los campos del formulario
        createFormFields();

        // Luego los botones (que podrían depender de los campos)
        createButtons();

        // Después los indicadores
        createIndicators();

        // Finalmente los paneles (que contienen los otros componentes)
        createPanels();
    }

    private void createFormFields() {
        // Campo nombre con validación mejorada
        nombreField = components.createStyledTextField("Nombre del departamento", FontAwesomeSolid.BUILDING);

        // Campo descripción expandido
        descripcionField = components.createStyledTextArea("Descripción detallada", 4);

        // 1. Primero inicializa el JFXDatePicker
        fechaCreacionPicker = new JFXDatePicker(LocalDate.now()); // Inicializar con fecha actual

        // 2. Configura las propiedades básicas
        fechaCreacionPicker.setPromptText("Fecha de creación");
        fechaCreacionPicker.setDefaultColor(Color.valueOf("#3f51b5"));

        // 3. Ahora configura el converter (ya no será null)
        fechaCreacionPicker.setConverter(new StringConverter<LocalDate>() {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public String toString(LocalDate date) {
                return (date != null) ? dateFormatter.format(date) : dateFormatter.format(LocalDate.now());
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return (string != null && !string.isEmpty())
                            ? LocalDate.parse(string, dateFormatter)
                            : LocalDate.now(); // Siempre devolver fecha actual si es nulo
                } catch (Exception e) {
                    return LocalDate.now(); // Valor por defecto si hay error de parsing
                }
            }
        });

        // Listener para evitar valores nulos
        fechaCreacionPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                Platform.runLater(() -> fechaCreacionPicker.setValue(LocalDate.now()));
            }
        });

        estadoComboBox = components.createComboBox("Estado",
                List.of("Activo", "Inactivo", "En revisión", "Suspendido"));

        // Campo de búsqueda mejorado
        searchField = components.createSearchField("Buscar departamentos...");

        setupFieldValidations();
    }

    private void createButtons() {
        // Botones principales
        agregarBtn = components.createActionButton("Agregar", FontAwesomeSolid.PLUS,
                styles.SUCCESS_COLOR, "Ctrl+N");
        actualizarBtn = components.createActionButton("Actualizar", FontAwesomeSolid.EDIT,
                styles.PRIMARY_COLOR, "Ctrl+U");
        eliminarBtn = components.createActionButton("Eliminar", FontAwesomeSolid.TRASH_ALT,
                styles.DANGER_COLOR, "Delete");
        limpiarBtn = components.createActionButton("Limpiar", FontAwesomeSolid.BROOM,
                styles.GRAY_COLOR, "Ctrl+R");

        // Botones de toolbar
        refreshBtn = components.createToolbarButton("Refrescar", FontAwesomeSolid.SYNC_ALT, "F5");
        exportBtn = components.createToolbarButton("Exportar", FontAwesomeSolid.DOWNLOAD, "Ctrl+E");
        importBtn = components.createToolbarButton("Importar", FontAwesomeSolid.UPLOAD, "Ctrl+I");
        reportBtn = components.createToolbarButton("Reportes", FontAwesomeSolid.CHART_BAR, "Ctrl+R");
        configBtn = components.createToolbarButton("Configuración", FontAwesomeSolid.COG, "Ctrl+,");

        setupButtonStates();
    }

    private void createIndicators() {
        loadingIndicator = components.createLoadingIndicator();
        statusLabel = components.createStatusLabel();
        countLabel = components.createCountLabel();
        statsLabel = components.createStatsLabel();
    }

    private void createPanels() {
        leftPanel = new VBox();
        rightPanel = new VBox();
        statsPanel = createStatsPanel();
    }

    private void createToolbar() {
        toolbarBox = components.createToolbar(
                searchField, refreshBtn, exportBtn, importBtn,
                reportBtn, configBtn
        );
    }

    private void createTable() {
        table = components.createDepartamentoTable();
        setupTableColumns();
        setupTableBehavior();
    }

    private void setupTableColumns() {
        // Columna ID con indicador
        TableColumn<DepartamentoDTO, Integer> idCol = components.createIdColumn();

        // Columna Nombre con validación visual
        TableColumn<DepartamentoDTO, String> nombreCol = components.createNombreColumn();

        // Columna Descripción con tooltip
        TableColumn<DepartamentoDTO, String> descCol = components.createDescripcionColumn();

        // Nueva columna Estado con colores
        TableColumn<DepartamentoDTO, String> estadoCol = components.createEstadoColumn();


        // Nueva columna Fecha
        TableColumn<DepartamentoDTO, LocalDateTime> fechaCol = components.createFechaColumn();

        // Columna de acciones mejorada
        TableColumn<DepartamentoDTO, Void> actionCol = components.createActionColumn(
                this::selectDepartamento, this::eliminarDepartamento, this::duplicarDepartamento
        );

        table.getColumns().addAll(idCol, nombreCol, descCol, estadoCol,
             fechaCol, actionCol);
    }

    private void createFormPanel() {
        rightPanel = components.createFormPanel(
                nombreField, descripcionField, fechaCreacionPicker,
                estadoComboBox, createButtonPanel()
        );
    }

    private VBox createStatsPanel() {
        return components.createStatsPanel(statsLabel);
    }

    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(12);
        buttonPanel.setAlignment(Pos.CENTER_LEFT);
        buttonPanel.getChildren().addAll(agregarBtn, actualizarBtn, eliminarBtn, limpiarBtn);
        return buttonPanel;
    }

    private void createStatusBar() {
        HBox statusBar = components.createStatusBar(statusLabel, statsLabel);
        view.setBottom(statusBar);
    }

    private void setupLayout() {
        // Panel izquierdo con tabla y estadísticas
        VBox tableContainer = new VBox();
        tableContainer.getChildren().addAll(toolbarBox, table, statsPanel);

        leftPanel.getChildren().add(tableContainer);
        leftPanel.setPrefWidth(800);

        // Layout principal
        HBox mainContent = new HBox(24);
        mainContent.getChildren().addAll(leftPanel, rightPanel);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPadding(new Insets(20));

        view.setCenter(mainContent);

        // Aplicar animaciones de entrada
        Platform.runLater(() -> animations.playEntryAnimations(leftPanel, rightPanel));
    }

    private void setupEventHandlers() {
        // Eventos de botones principales
        agregarBtn.setOnAction(e -> agregarDepartamento());
        actualizarBtn.setOnAction(e -> actualizarDepartamento());
        eliminarBtn.setOnAction(e -> eliminarDepartamento());
        limpiarBtn.setOnAction(e -> limpiarFormulario());

        // Eventos de toolbar
        refreshBtn.setOnAction(e -> refreshData());
        exportBtn.setOnAction(e -> showExportDialog());
        importBtn.setOnAction(e -> showImportDialog());
        reportBtn.setOnAction(e -> showReportDialog());
        configBtn.setOnAction(e -> showConfigDialog());

        // Eventos de tabla
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            handleTableSelection(newVal);
        });

    }

    private void setupFieldValidations() {
        // Validación del nombre
        nombreField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateNombreField(newVal);
        });

        // Efectos de focus
        setupFocusEffects();
    }

    private void validateNombreField(String text) {
        if (text == null || text.trim().isEmpty()) {
            components.setFieldValidationState(nombreField, "error");
        } else if (text.trim().length() < 3) {
            components.setFieldValidationState(nombreField, "warning");
        } else {
            components.setFieldValidationState(nombreField, "success");
        }
    }


    private void setupFocusEffects() {
        // Efectos visuales al enfocar campos
        nombreField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                animations.playFocusAnimation(nombreField);
            }
        });

        descripcionField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                animations.playFocusAnimation(descripcionField);
            }
        });
    }

    private void setupButtonStates() {
        actualizarBtn.setDisable(true);
        eliminarBtn.setDisable(true);
    }

    private void setupTableBehavior() {
        table.setItems(filteredDepartamentos);
        table.setRowFactory(tv -> components.createTableRow(this::handleRowDoubleClick));
    }

    private void setupKeyboardShortcuts() {
        view.setOnKeyPressed(e -> {
            if (shortcuts[0].match(e)) agregarDepartamento();
            else if (shortcuts[1].match(e) && !actualizarBtn.isDisable()) actualizarDepartamento();
            else if (shortcuts[2].match(e) && !eliminarBtn.isDisable()) eliminarDepartamento();
            else if (shortcuts[3].match(e)) limpiarFormulario();
            else if (shortcuts[4].match(e)) refreshData();
            else if (shortcuts[5].match(e)) searchField.requestFocus();
            else if (shortcuts[6].match(e)) showExportDialog();
            else if (shortcuts[7].match(e)) showImportDialog();
        });
    }

    // Métodos de acción principales
    private void agregarDepartamento() {
        if (!validarFormulario()) return;

        DepartamentoDTO dto = createDTOFromForm();

        showLoadingState(true);
        updateStatus("Agregando departamento...");

        CompletableFuture.supplyAsync(() -> {
                    try (Connection connection = DatabaseManager.connect()) {
                        DepartamentoDTO nuevo = controller.agregarDepartamento(dto);
                        return nuevo; // Devuelve el departamento con ID generado
                    } catch (SQLException ex) {
                        throw new RuntimeException("Error al agregar departamento", ex);
                    }
                }, Executors.newCachedThreadPool())
                .thenAccept(nuevoDepartamento -> {
                    Platform.runLater(() -> {
                        // Agrega directamente a la lista observable
                        departamentos.add(nuevoDepartamento);
                        filteredDepartamentos.setAll(departamentos);

                        showLoadingState(false);
                        updateStatus("Departamento agregado exitosamente");
                        showSuccessNotification("Departamento agregado",
                                "El departamento se ha registrado correctamente");
                        limpiarFormulario();
                        animations.playSuccessAnimation(agregarBtn);
                        updateCountLabel();
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        showLoadingState(false);
                        updateStatus("Error al agregar departamento");
                        AlertUtils.showError("Error", "No se pudo agregar el departamento: " +
                                ex.getCause().getMessage());
                    });
                    return null;
                });
    }

    private void actualizarDepartamento() {
        if (selectedDepartamento == null || !validarFormulario()) return;

        updateDTOFromForm(selectedDepartamento);

        showLoadingState(true);
        updateStatus("Actualizando departamento...");

        CompletableFuture.runAsync(() -> {
                    try (Connection connection = DatabaseManager.connect()) {
                        controller.actualizarDepartamento(selectedDepartamento);
                    } catch (SQLException ex) {
                        throw new RuntimeException("Error al actualizar departamento", ex);
                    }
                }, Executors.newCachedThreadPool())
                .thenRun(() -> {
                    Platform.runLater(() -> {
                        showLoadingState(false);
                        updateStatus("Departamento actualizado exitosamente");
                        showSuccessNotification("Departamento actualizado",
                                "Los cambios se han guardado correctamente");
                        limpiarFormulario();
                        loadInitialData();
                        animations.playSuccessAnimation(actualizarBtn);
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        showLoadingState(false);
                        updateStatus("Error al actualizar departamento");
                        AlertUtils.showError("Error", "No se pudo actualizar el departamento: " +
                                ex.getCause().getMessage());
                    });
                    return null;
                });
    }

    private void eliminarDepartamento() {
        if (selectedDepartamento == null) return;
        eliminarDepartamento(selectedDepartamento);
    }

    private void eliminarDepartamento(DepartamentoDTO departamento) {
        boolean confirmado = AlertUtils.showConfirmation(
                "Confirmar eliminación",
                "¿Está seguro de que desea eliminar el departamento '" + departamento.getNombre() + "'?",
                "Esta acción no se puede deshacer."
        );

        if (!confirmado) return;

        showLoadingState(true);
        updateStatus("Eliminando departamento...");

        Supplier<Boolean> eliminarTask = () -> {
            try {
                controller.eliminarDepartamento(departamento.getId());
                return true;
            } catch (SQLException ex) {
                throw new RuntimeException("Error al eliminar departamento", ex);
            }
        };

        CompletableFuture.supplyAsync(eliminarTask, Executors.newCachedThreadPool())
                .thenAccept(result -> {
                    Platform.runLater(() -> {
                        showLoadingState(false);
                        updateStatus("Departamento eliminado exitosamente");
                        showSuccessNotification("Departamento eliminado",
                                "El departamento se ha eliminado correctamente");
                        limpiarFormulario();
                        loadInitialData();
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        showLoadingState(false);
                        updateStatus("Error al eliminar departamento");
                        AlertUtils.showError("Error", "No se pudo eliminar el departamento: " +
                                ex.getCause().getMessage());
                    });
                    return null;
                });
    }

    private void duplicarDepartamento(DepartamentoDTO departamento) {
        DepartamentoDTO duplicado = new DepartamentoDTO();
        duplicado.setNombre(departamento.getNombre() + " (Copia)");
        duplicado.setDescripcion(departamento.getDescripcion());
        // Copiar otros campos...

        fillFormFromDTO(duplicado);
        isEditMode = false;
        updateButtonStates();
    }

    // Métodos de diálogo
    private void showExportDialog() {
        Dialog<String> dialog = components.createExportDialog();
        dialog.showAndWait().ifPresent(format -> {
            exportData(format);
        });
    }

    private void showImportDialog() {
        Dialog<File> dialog = components.createImportDialog();
        dialog.showAndWait().ifPresent(file -> {
            importData(file);
        });
    }

    private void showReportDialog() {
        Dialog<String> dialog = components.createReportDialog();
        dialog.showAndWait().ifPresent(reportType -> {
            generateReport(reportType);
        });
    }

    private void showConfigDialog() {
        Dialog<Void> dialog = components.createConfigDialog();
        dialog.showAndWait();
    }

    // Métodos de exportación e importación
    private void exportData(String format) {
        showLoadingState(true);
        updateStatus("Exportando datos...");

        CompletableFuture.supplyAsync(() -> {
            try {
                switch (format.toLowerCase()) {
                    case "csv":
                        return exportService.exportToCSV(departamentos);
                    case "excel":
                        return exportService.exportToExcel(departamentos);
                    case "pdf":
                        return exportService.exportToPDF(departamentos);
                    case "json":
                        return exportService.exportToJSON(departamentos);
                    default:
                        throw new IllegalArgumentException("Formato no soportado: " + format);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).thenAccept(file -> {
            Platform.runLater(() -> {
                showLoadingState(false);
                updateStatus("Datos exportados exitosamente");
                showSuccessNotification("Exportación completada",
                        "Los datos se han exportado a: " + file.getAbsolutePath());
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                showLoadingState(false);
                updateStatus("Error al exportar datos");
                AlertUtils.showError("Error de exportación", ex.getMessage());
            });
            return null;
        });
    }

    private void importData(File file) {
        showLoadingState(true);
        updateStatus("Importando datos...");

        CompletableFuture.supplyAsync(() -> {
            try {
                return exportService.importFromFile(file);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).thenAccept(importedData -> {
            Platform.runLater(() -> {
                showLoadingState(false);
                updateStatus("Datos importados exitosamente");
                showSuccessNotification("Importación completada",
                        "Se han importado " + importedData.size() + " registros");

                // Agregar datos importados
                departamentos.addAll(importedData);
                filteredDepartamentos.setAll(departamentos);
                updateCountLabel();
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                showLoadingState(false);
                updateStatus("Error al importar datos");
                AlertUtils.showError("Error de importación", ex.getMessage());
            });
            return null;
        });
    }

    private void generateReport(String reportType) {
        showLoadingState(true);
        updateStatus("Generando reporte...");

        CompletableFuture.supplyAsync(() -> {
            try {
                return exportService.generateReport(departamentos, reportType);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }).thenAccept(reportFile -> {
            Platform.runLater(() -> {
                showLoadingState(false);
                updateStatus("Reporte generado exitosamente");
                showSuccessNotification("Reporte completado",
                        "El reporte se ha generado: " + reportFile.getAbsolutePath());
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                showLoadingState(false);
                updateStatus("Error al generar reporte");
                AlertUtils.showError("Error de reporte", ex.getMessage());
            });
            return null;
        });
    }

    // Métodos utilitarios
    private void loadInitialData() {
        showLoadingState(true);
        updateStatus("Cargando departamentos...");

        CompletableFuture.supplyAsync(() -> {
            try (Connection connection = DatabaseManager.connect()) {
                return controller.obtenerTodosLosDepartamentos();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }).thenAccept(data -> {
            Platform.runLater(() -> {
                departamentos.clear();
                departamentos.addAll(data);
                filteredDepartamentos.setAll(data);

                updateCountLabel();
                updateStatus("Listo - " + data.size() + " departamentos cargados");
                showLoadingState(false);

                animations.playTableLoadAnimation(table);
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                showLoadingState(false);
                updateStatus("Error al cargar datos");
                AlertUtils.showError("Error de base de datos",
                        "No se pudieron cargar los departamentos: " + ex.getMessage());
            });
            return null;
        });
    }

    private void refreshData() {
        animations.playRefreshAnimation(refreshBtn);
        loadInitialData();
    }

    private void handleTableSelection(DepartamentoDTO departamento) {
        selectedDepartamento = departamento;
        boolean hasSelection = departamento != null;

        actualizarBtn.setDisable(!hasSelection);
        eliminarBtn.setDisable(!hasSelection);

        if (hasSelection) {
            fillFormFromDTO(departamento);
            isEditMode = true;
            animations.playSelectionAnimation(nombreField, descripcionField);
        }
    }

    private void handleRowDoubleClick(DepartamentoDTO departamento) {
        if (departamento != null) {
            // Abrir ventana de detalles o edición avanzada
            showDetailDialog(departamento);
        }
    }

    private void showDetailDialog(DepartamentoDTO departamento) {
        Dialog<Void> dialog = components.createDetailDialog(departamento);
        dialog.showAndWait();
    }

    private DepartamentoDTO createDTOFromForm() {
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setNombre(nombreField.getText().trim());
        dto.setDescripcion(descripcionField.getText().trim());
        dto.setEstado(estadoComboBox.getValue());
        dto.setFechaFromLocalDate(fechaCreacionPicker.getValue());
        return dto;
    }
    private void updateDTOFromForm(DepartamentoDTO dto) {
        dto.setNombre(nombreField.getText().trim());
        dto.setDescripcion(descripcionField.getText().trim());
           dto.setEstado( estadoComboBox.getValue() );
           dto.setFecha( fechaCreacionPicker.getValue().atStartOfDay() );
    }


    private void fillFormFromDTO(DepartamentoDTO dto) {
        nombreField.setText(dto.getNombre());
        descripcionField.setText(dto.getDescripcion());
        estadoComboBox.setValue(dto.getEstado());
        fechaCreacionPicker.setValue(dto.getFechaAsLocalDate());
    }

    private void limpiarFormulario() {
        nombreField.clear();
        descripcionField.clear();
        estadoComboBox.setValue(null);
        fechaCreacionPicker.setValue(LocalDate.now());

        table.getSelectionModel().clearSelection();
        selectedDepartamento = null;
        isEditMode = false;

        updateButtonStates();
        animations.playClearAnimation(nombreField, descripcionField);
        updateStatus("Formulario limpio");
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();
        boolean esValido = true;

        // Validación del nombre
        if (nombreField.getText() == null || nombreField.getText().trim().isEmpty()) {
            errores.append("• El nombre es obligatorio\n");
            esValido = false;
        } else if (nombreField.getText().trim().length() < 3) {
            errores.append("• El nombre debe tener al menos 3 caracteres\n");
            esValido = false;
        }

        // Validación de la fecha (aunque el listener ya maneja nulos)
        if (fechaCreacionPicker.getValue() == null) {
            fechaCreacionPicker.setValue(LocalDate.now());
        }

        if (!esValido) {
            AlertUtils.showError("Datos inválidos", errores.toString());
            return false;
        }

        return true;
    }

    private void updateButtonStates() {
        actualizarBtn.setDisable(!isEditMode || selectedDepartamento == null);
        eliminarBtn.setDisable(!isEditMode || selectedDepartamento == null);
    }

    private void showLoadingState(boolean loading) {
        loadingIndicator.setVisible(loading);
        agregarBtn.setDisable(loading);
        actualizarBtn.setDisable(loading || selectedDepartamento == null);
        eliminarBtn.setDisable(loading || selectedDepartamento == null);
        refreshBtn.setDisable(loading);
        exportBtn.setDisable(loading);
        importBtn.setDisable(loading);
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
        animations.playStatusAnimation(statusLabel);
    }

    private void updateCountLabel() {
        int total = departamentos.size();
        int filtered = filteredDepartamentos.size();

        if (total == filtered) {
            countLabel.setText("Total: " + total);
        } else {
            countLabel.setText("Mostrando: " + filtered + " de " + total);
        }
    }
    private void showSuccessNotification(String title, String message) {
        Notifications.create()
                .title(title)
                .text(message)
                .graphic(components.createIcon(FontAwesomeSolid.CHECK_CIRCLE, styles.SUCCESS_COLOR))
                .hideAfter(Duration.seconds(4))
                .position(Pos.TOP_RIGHT)
                .show();
    }

    // Métodos públicos de la API
    public BorderPane getView() {
        return view;
    }

    public void focusSearch() {
        searchField.requestFocus();
    }

    public void refresh() {
        refreshData();
    }

    public void prepareForNewRecord() {
        limpiarFormulario();
        nombreField.requestFocus();
    }

    public void selectDepartamento(DepartamentoDTO departamento) {
        table.getSelectionModel().select(departamento);
        handleTableSelection(departamento);
    }
}