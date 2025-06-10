package tescha.prestamos.view;

import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import tescha.database.DatabaseManager;
import tescha.inventario.dao.InventarioSQLiteDAO;
import tescha.prestamos.dto.PrestamoDTO;
import tescha.Components.AlertUtils;
import tescha.inventario.dto.EquipoDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Diálogo para crear/editar préstamos
 */
public class PrestamoFormDialog extends Dialog<PrestamoDTO> {
    
    private final PrestamoDTO prestamo;
    private final boolean isEditing;
    
    // Campos del formulario
    private JFXTextField solicitanteField;
    private JFXComboBox<EquipoDTO> equipoCombo;
    private JFXTextField cantidadField;
    private JFXDatePicker fechaDevolucionPicker;
    private JFXTextArea comentariosArea;
    private JFXTextArea condicionesArea;
    private JFXTextField entregaField;
    private JFXTextField entregadoPorField;
    private JFXComboBox<String> tipoEntregaCombo;

    public PrestamoFormDialog(PrestamoDTO prestamo) {
        this.prestamo = prestamo;
        this.isEditing = prestamo != null;

        initializeDialog();
        createContent();
        setupResultConverter();  // Mover esto antes de setupValidation

        // Ahora podemos configurar la validación
        setupValidation();

        if (isEditing) {
            populateFields();
        }
    }
    private void initializeDialog() {
        setTitle(isEditing ? "Editar Préstamo" : "Nuevo Préstamo");
        setHeaderText(isEditing ? "Modificar información del préstamo" : "Crear un nuevo préstamo");
        
        // Configurar ícono del header
        FontIcon icon = new FontIcon(isEditing ? FontAwesomeSolid.EDIT : FontAwesomeSolid.PLUS);
        icon.setIconSize(24);
        setGraphic(icon);
        
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        
        // Botones
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        
        // Estilo del diálogo
        getDialogPane().getStyleClass().add("prestamo-form-dialog");
    }
    
    private void createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);
        
        // Sección de información básica
        VBox basicInfoSection = createBasicInfoSection();
        
        // Sección de detalles
        VBox detailsSection = createDetailsSection();
        
        // Sección de entrega
        VBox deliverySection = createDeliverySection();
        
        content.getChildren().addAll(basicInfoSection, detailsSection, deliverySection);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(600);
        
        getDialogPane().setContent(scrollPane);
    }
    
    private VBox createBasicInfoSection() {
        VBox section = new VBox(15);
        
        Label sectionTitle = new Label("Información Básica");
        sectionTitle.getStyleClass().add("section-title");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Grid para los campos
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        
        // Solicitante
        Label solicitanteLabel = new Label("Solicitante:");
        solicitanteField = new JFXTextField();
        solicitanteField.setPromptText("Nombre del solicitante");
        solicitanteField.setPrefWidth(200);

        Label equipoLabel = new Label("Equipo:");
        equipoCombo = new JFXComboBox<>();
        equipoCombo.setPromptText("Seleccione un equipo");
        equipoCombo.setPrefWidth(300);

        // Cargar los items del inventario
        try {
            Connection conn = DatabaseManager.connect();
            InventarioSQLiteDAO inventarioDAO = new InventarioSQLiteDAO(conn);
            equipoCombo.getItems().addAll(inventarioDAO.obtenerTodosLosEquipos());
        } catch (SQLException e) {
            AlertUtils.showError("Error", "No se pudieron cargar los items del inventario");
        }

        // Cantidad
        Label cantidadLabel = new Label("Cantidad:");
        cantidadField = new JFXTextField();
        cantidadField.setPromptText("1");
        cantidadField.setPrefWidth(100);
        
        // Fecha de devolución
        Label fechaDevolucionLabel = new Label("Fecha de Devolución:");
        fechaDevolucionPicker = new JFXDatePicker();
        fechaDevolucionPicker.setValue(LocalDate.now().plusDays(7)); // Valor por defecto
        fechaDevolucionPicker.setPrefWidth(200);
        
        grid.add(solicitanteLabel, 0, 0);
        grid.add(solicitanteField, 1, 0);
        grid.add(equipoLabel, 0, 1);
        grid.add(equipoCombo, 1, 1);
        grid.add(cantidadLabel, 0, 2);
        grid.add(cantidadField, 1, 2);
        grid.add(fechaDevolucionLabel, 0, 3);
        grid.add(fechaDevolucionPicker, 1, 3);
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }
    
    private VBox createDetailsSection() {
        VBox section = new VBox(15);
        
        Label sectionTitle = new Label("Detalles del Préstamo");
        sectionTitle.getStyleClass().add("section-title");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Comentarios
        Label comentariosLabel = new Label("Comentarios:");
        comentariosArea = new JFXTextArea();
        comentariosArea.setPromptText("Comentarios adicionales sobre el préstamo...");
        comentariosArea.setPrefRowCount(3);
        comentariosArea.setWrapText(true);
        
        // Condiciones
        Label condicionesLabel = new Label("Condiciones del Equipo:");
        condicionesArea = new JFXTextArea();
        condicionesArea.setPromptText("Estado y condiciones del equipo al momento del préstamo...");
        condicionesArea.setPrefRowCount(3);
        condicionesArea.setWrapText(true);
        
        section.getChildren().addAll(
            sectionTitle,
            comentariosLabel, comentariosArea,
            condicionesLabel, condicionesArea
        );
        
        return section;
    }
    
    private VBox createDeliverySection() {
        VBox section = new VBox(15);
        
        Label sectionTitle = new Label("Información de Entrega");
        sectionTitle.getStyleClass().add("section-title");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        
        // Lugar de entrega
        Label entregaLabel = new Label("Lugar de Entrega:");
        entregaField = new JFXTextField();
        entregaField.setPromptText("Ubicación donde se entrega el equipo");
        entregaField.setPrefWidth(300);
        
        // Entregado por
        Label entregadoPorLabel = new Label("Entregado Por:");
        entregadoPorField = new JFXTextField();
        entregadoPorField.setPromptText("Nombre de quien entrega");
        entregadoPorField.setPrefWidth(200);
        
        // Tipo de entrega
        Label tipoEntregaLabel = new Label("Tipo de Entrega:");
        tipoEntregaCombo = new JFXComboBox<>();
        tipoEntregaCombo.getItems().addAll("Manual", "Automática", "Programada");
        tipoEntregaCombo.setValue("Manual");
        tipoEntregaCombo.setPrefWidth(150);
        
        grid.add(entregaLabel, 0, 0);
        grid.add(entregaField, 1, 0);
        grid.add(entregadoPorLabel, 0, 1);
        grid.add(entregadoPorField, 1, 1);
        grid.add(tipoEntregaLabel, 0, 2);
        grid.add(tipoEntregaCombo, 1, 2);
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }

    private void setupValidation() {
        // Primero asegúrate de que el diálogo esté completamente configurado
        Platform.runLater(() -> {
            Button saveButton = (Button) getDialogPane().lookupButton(ButtonType.OK);

            if (saveButton != null) {
                // Validación en tiempo real
                solicitanteField.textProperty().addListener((obs, oldText, newText) -> validateForm(saveButton));
                equipoCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateForm(saveButton));
                cantidadField.textProperty().addListener((obs, oldText, newText) -> validateForm(saveButton));
                fechaDevolucionPicker.valueProperty().addListener((obs, oldDate, newDate) -> validateForm(saveButton));

                // Validación inicial
                validateForm(saveButton);
            }
        });
    }

    private void validateForm(Button saveButton) {
        if (saveButton == null) return;

        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        // Validar solicitante
        if (solicitanteField.getText() == null || solicitanteField.getText().trim().isEmpty()) {
            isValid = false;
            errors.append("- El solicitante es requerido\n");
        }

        // Validar equipo (ahora validamos el ComboBox)
        if (equipoCombo.getSelectionModel().getSelectedItem() == null) {
            isValid = false;
            errors.append("- Debe seleccionar un equipo\n");
        }

        // Validar cantidad
        try {
            int cantidad = Integer.parseInt(cantidadField.getText());
            if (cantidad <= 0) {
                isValid = false;
                errors.append("- La cantidad debe ser mayor a 0\n");
            }

            // Validar disponibilidad del equipo
            EquipoDTO itemSeleccionado = equipoCombo.getSelectionModel().getSelectedItem();
            if (itemSeleccionado != null && cantidad > itemSeleccionado.getCantidad()) {
                isValid = false;
                errors.append("- No hay suficientes unidades disponibles\n");
            }
        } catch (NumberFormatException e) {
            isValid = false;
            errors.append("- La cantidad debe ser un número válido\n");
        }

        // Validar fecha de devolución
        if (fechaDevolucionPicker.getValue() != null && fechaDevolucionPicker.getValue().isBefore(LocalDate.now())) {
            isValid = false;
            errors.append("- La fecha de devolución no puede ser anterior a hoy\n");
        }

        saveButton.setDisable(!isValid);

        if (!isValid && errors.length() > 0) {
            Tooltip tooltip = new Tooltip(errors.toString());
            saveButton.setTooltip(tooltip);
        } else {
            saveButton.setTooltip(null);
        }
    }

    private void populateFields() {
        if (prestamo != null) {
            solicitanteField.setText(prestamo.getSolicitanteNombre());

            // Buscar y seleccionar el equipo en el ComboBox
            try {
                Connection conn = DatabaseManager.connect();
                InventarioSQLiteDAO inventarioDAO = new InventarioSQLiteDAO(conn);
                EquipoDTO item = inventarioDAO.obtenerEquipoPorId(prestamo.getIdEquipo());
                if (item != null) {
                    equipoCombo.getSelectionModel().select(item);
                }
            } catch (SQLException e) {
                AlertUtils.showError("Error", "No se pudo cargar la información del equipo");
            }

            cantidadField.setText(String.valueOf(prestamo.getCantidad()));
            fechaDevolucionPicker.setValue(prestamo.getFechaDevolucion());
            comentariosArea.setText(prestamo.getComentarios());
            condicionesArea.setText(prestamo.getCondiciones());
            entregaField.setText(prestamo.getEntrega());
            entregadoPorField.setText(prestamo.getEntregadoPor());
            tipoEntregaCombo.setValue(prestamo.getTipoEntrega());
        }
    }

    private void setupResultConverter() {
        setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    PrestamoDTO result = isEditing ? prestamo : new PrestamoDTO();

                    // Mapear campos básicos
                    result.setSolicitanteNombre(solicitanteField.getText().trim());

                    // Obtener el equipo seleccionado del ComboBox
                    EquipoDTO itemSeleccionado = equipoCombo.getSelectionModel().getSelectedItem();
                    if (itemSeleccionado == null) {
                        throw new IllegalArgumentException("Debe seleccionar un equipo");
                    }
                    result.setIdEquipo(itemSeleccionado.getId());
                    result.setNombreEquipo(itemSeleccionado.getNombre());
                    result.setMarcaEquipo(itemSeleccionado.getMarca());
                    result.setModeloEquipo(itemSeleccionado.getModelo());

                    result.setCantidad(Integer.parseInt(cantidadField.getText()));
                    result.setFechaDevolucion(fechaDevolucionPicker.getValue());
                    result.setComentarios(comentariosArea.getText());
                    result.setCondiciones(condicionesArea.getText());
                    result.setEntrega(entregaField.getText());
                    result.setEntregadoPor(entregadoPorField.getText());
                    result.setTipoEntrega(tipoEntregaCombo.getValue().toLowerCase());

                    // Si es nuevo préstamo, establecer fecha y hora actuales
                    if (!isEditing) {
                        result.setFecha(LocalDate.now());
                        result.setHora(LocalTime.now());
                    }

                    return result;

                } catch (Exception e) {
                    AlertUtils.showError("Error de Validación", "Por favor, verifique los datos ingresados: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });
    }
}
