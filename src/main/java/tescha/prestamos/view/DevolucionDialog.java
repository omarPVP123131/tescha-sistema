package tescha.prestamos.view;

import com.jfoenix.controls.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import tescha.prestamos.dto.PrestamoDTO;

/**
 * Diálogo para procesar devoluciones
 */
public class DevolucionDialog extends Dialog<DevolucionDialog.DevolucionData> {
    
    private final PrestamoDTO prestamo;
    
    private JFXTextField devueltoPorField;
    private JFXTextField recibidoPorField;
    private JFXComboBox<String> estadoCombo;
    private JFXTextArea observacionesArea;
    
    public DevolucionDialog(PrestamoDTO prestamo) {
        this.prestamo = prestamo;
        
        initializeDialog();
        createContent();
        setupResultConverter();
    }
    
    private void initializeDialog() {
        setTitle("Procesar Devolución");
        setHeaderText("Registrar la devolución del equipo: " + prestamo.getEquipoCompleto());
        
        FontIcon icon = new FontIcon(FontAwesomeSolid.UNDO);
        icon.setIconSize(24);
        setGraphic(icon);
        
        initModality(Modality.APPLICATION_MODAL);
        
        ButtonType processButtonType = new ButtonType("Procesar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(processButtonType, cancelButtonType);
    }
    
    private void createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(400);
        
        // Información del préstamo
        VBox infoSection = createInfoSection();
        
        // Formulario de devolución
        VBox formSection = createFormSection();
        
        content.getChildren().addAll(infoSection, formSection);
        getDialogPane().setContent(content);
    }
    
    private VBox createInfoSection() {
        VBox section = new VBox(10);
        section.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label titleLabel = new Label("Información del Préstamo");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label solicitanteLabel = new Label("Solicitante: " + prestamo.getSolicitanteNombre());
        Label equipoLabel = new Label("Equipo: " + prestamo.getEquipoCompleto());
        Label cantidadLabel = new Label("Cantidad: " + prestamo.getCantidad());
        Label fechaLabel = new Label("Fecha de Préstamo: " + prestamo.getFecha());
        
        section.getChildren().addAll(titleLabel, solicitanteLabel, equipoLabel, cantidadLabel, fechaLabel);
        return section;
    }
    
    private VBox createFormSection() {
        VBox section = new VBox(15);
        
        Label titleLabel = new Label("Datos de Devolución");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        
        // Devuelto por
        Label devueltoPorLabel = new Label("Devuelto por:");
        devueltoPorField = new JFXTextField();
        devueltoPorField.setPromptText("Nombre de quien devuelve");
        devueltoPorField.setText(prestamo.getSolicitanteNombre()); // Pre-llenar con el solicitante
        
        // Recibido por
        Label recibidoPorLabel = new Label("Recibido por:");
        recibidoPorField = new JFXTextField();
        recibidoPorField.setPromptText("Nombre de quien recibe");
        
        // Estado del equipo
        Label estadoLabel = new Label("Estado del equipo:");
        estadoCombo = new JFXComboBox<>();
        estadoCombo.getItems().addAll(
            "Excelente - Sin daños",
            "Bueno - Desgaste normal",
            "Regular - Daños menores",
            "Malo - Daños significativos",
            "Dañado - Requiere reparación"
        );
        estadoCombo.setValue("Bueno - Desgaste normal");
        
        grid.add(devueltoPorLabel, 0, 0);
        grid.add(devueltoPorField, 1, 0);
        grid.add(recibidoPorLabel, 0, 1);
        grid.add(recibidoPorField, 1, 1);
        grid.add(estadoLabel, 0, 2);
        grid.add(estadoCombo, 1, 2);
        
        // Observaciones
        Label observacionesLabel = new Label("Observaciones:");
        observacionesArea = new JFXTextArea();
        observacionesArea.setPromptText("Observaciones adicionales sobre la devolución...");
        observacionesArea.setPrefRowCount(3);
        observacionesArea.setWrapText(true);
        
        section.getChildren().addAll(titleLabel, grid, observacionesLabel, observacionesArea);
        return section;
    }
    
    private void setupResultConverter() {
        setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                String devueltoPor = devueltoPorField.getText().trim();
                String recibidoPor = recibidoPorField.getText().trim();
                String estado = estadoCombo.getValue();
                String observaciones = observacionesArea.getText().trim();
                
                if (devueltoPor.isEmpty() || recibidoPor.isEmpty()) {
                    return null; // Validación básica
                }
                
                String estadoFinal = estado;
                if (!observaciones.isEmpty()) {
                    estadoFinal += " - " + observaciones;
                }
                
                return new DevolucionData(devueltoPor, recibidoPor, estadoFinal);
            }
            return null;
        });
    }
    
    // Clase interna para los datos de devolución
    public static class DevolucionData {
        private final String devueltoPor;
        private final String recibidoPor;
        private final String estadoDevuelto;
        
        public DevolucionData(String devueltoPor, String recibidoPor, String estadoDevuelto) {
            this.devueltoPor = devueltoPor;
            this.recibidoPor = recibidoPor;
            this.estadoDevuelto = estadoDevuelto;
        }
        
        public String getDevueltoPor() { return devueltoPor; }
        public String getRecibidoPor() { return recibidoPor; }
        public String getEstadoDevuelto() { return estadoDevuelto; }
    }
}
