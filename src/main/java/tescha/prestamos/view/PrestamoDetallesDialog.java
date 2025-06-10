package tescha.prestamos.view;

import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import tescha.prestamos.dto.PrestamoDTO;

import java.time.format.DateTimeFormatter;

/**
 * Diálogo para mostrar detalles completos de un préstamo
 */
public class PrestamoDetallesDialog extends Dialog<Void> {
    
    private final PrestamoDTO prestamo;
    
    // Colores del tema
    private final String PRIMARY_COLOR = "#1976D2";
    private final String SUCCESS_COLOR = "#388E3C";
    private final String WARNING_COLOR = "#F57C00";
    private final String ERROR_COLOR = "#D32F2F";
    private final String CARD_COLOR = "#FFFFFF";
    private final String TEXT_PRIMARY = "#212121";
    private final String TEXT_SECONDARY = "#455A64";
    
    public PrestamoDetallesDialog(PrestamoDTO prestamo) {
        this.prestamo = prestamo;
        
        initializeDialog();
        createContent();
    }
    
    private void initializeDialog() {
        setTitle("Detalles del Préstamo #" + prestamo.getId());
        setHeaderText("Información completa del préstamo");
        
        FontIcon icon = new FontIcon(FontAwesomeSolid.INFO_CIRCLE);
        icon.setIconSize(24);
        icon.setIconColor(Color.web(PRIMARY_COLOR));
        setGraphic(icon);
        
        initModality(Modality.APPLICATION_MODAL);
        setResizable(true);
        
        ButtonType closeButtonType = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().add(closeButtonType);
        
        getDialogPane().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }
    
    private void createContent() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.setPrefWidth(600);
        
        // Header con estado
        HBox headerSection = createHeaderSection();
        
        // Información básica
        VBox basicInfoSection = createBasicInfoSection();
        
        // Información del equipo
        VBox equipmentSection = createEquipmentSection();
        
        // Información de entrega
        VBox deliverySection = createDeliverySection();
        
        // Información de devolución (si aplica)
        if (prestamo.isDevuelto()) {
            VBox returnSection = createReturnSection();
            mainContent.getChildren().addAll(headerSection, basicInfoSection, equipmentSection, deliverySection, returnSection);
        } else {
            mainContent.getChildren().addAll(headerSection, basicInfoSection, equipmentSection, deliverySection);
        }
        
        ScrollPane scrollPane = new ScrollPane(mainContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        
        getDialogPane().setContent(scrollPane);
    }
    
    private HBox createHeaderSection() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: " + getStatusColor() + "22; -fx-background-radius: 8px;");
        
        // Ícono de estado
        FontIcon statusIcon = getStatusIcon();
        statusIcon.setIconSize(32);
        statusIcon.setIconColor(Color.web(getStatusColor()));
        
        StackPane iconContainer = new StackPane(statusIcon);
        iconContainer.setStyle("-fx-background-color: " + getStatusColor() + "44; -fx-background-radius: 50%;");
        iconContainer.setPrefSize(60, 60);
        
        // Información de estado
        VBox statusInfo = new VBox(5);
        
        Label statusLabel = new Label(prestamo.getStatusText());
        statusLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + getStatusColor() + ";");
        
        Label idLabel = new Label("Préstamo #" + prestamo.getId());
        idLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        
        statusInfo.getChildren().addAll(statusLabel, idLabel);
        
        // Días restantes (si aplica)
        if (!prestamo.isDevuelto() && prestamo.getDiasRestantes() >= 0) {
            Label diasLabel = new Label(prestamo.getDiasRestantes() + " días restantes");
            diasLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + ";");
            statusInfo.getChildren().add(diasLabel);
        }
        
        header.getChildren().addAll(iconContainer, statusInfo);
        return header;
    }
    
    private VBox createBasicInfoSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 8px;");
        JFXDepthManager.setDepth(section, 1);
        
        Label sectionTitle = new Label("Información Básica");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        
        addDetailRow(grid, 0, "Solicitante:", prestamo.getSolicitanteNombre());
        addDetailRow(grid, 1, "Fecha de Préstamo:", formatDate(prestamo.getFecha()));
        addDetailRow(grid, 2, "Hora de Préstamo:", formatTime(prestamo.getHora()));
        addDetailRow(grid, 3, "Fecha de Devolución:", formatDate(prestamo.getFechaDevolucion()));
        addDetailRow(grid, 4, "Cantidad:", String.valueOf(prestamo.getCantidad()));
        
        section.getChildren().addAll(sectionTitle, grid);
        return section;
    }
    
    private VBox createEquipmentSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 8px;");
        JFXDepthManager.setDepth(section, 1);
        
        Label sectionTitle = new Label("Información del Equipo");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        
        addDetailRow(grid, 0, "Nombre:", prestamo.getNombreEquipo());
        addDetailRow(grid, 1, "Marca:", prestamo.getMarcaEquipo());
        addDetailRow(grid, 2, "Modelo:", prestamo.getModeloEquipo());
        
        // Condiciones del equipo
        if (prestamo.getCondiciones() != null && !prestamo.getCondiciones().trim().isEmpty()) {
            Label condicionesLabel = new Label("Condiciones al Préstamo:");
            condicionesLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
            
            TextArea condicionesArea = new TextArea(prestamo.getCondiciones());
            condicionesArea.setEditable(false);
            condicionesArea.setPrefRowCount(3);
            condicionesArea.setWrapText(true);
            condicionesArea.setStyle("-fx-background-color: #f5f5f5;");
            
            section.getChildren().addAll(grid, condicionesLabel, condicionesArea);
        } else {
            section.getChildren().addAll(sectionTitle, grid);
        }
        
        return section;
    }
    
    private VBox createDeliverySection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 8px;");
        JFXDepthManager.setDepth(section, 1);
        
        Label sectionTitle = new Label("Información de Entrega");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        
        addDetailRow(grid, 0, "Lugar de Entrega:", prestamo.getEntrega());
        addDetailRow(grid, 1, "Entregado Por:", prestamo.getEntregadoPor());
        addDetailRow(grid, 2, "Tipo de Entrega:", prestamo.getTipoEntrega());
        
        // Comentarios
        VBox content = new VBox(10);
        content.getChildren().addAll(sectionTitle, grid);
        
        if (prestamo.getComentarios() != null && !prestamo.getComentarios().trim().isEmpty()) {
            Label comentariosLabel = new Label("Comentarios:");
            comentariosLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
            
            TextArea comentariosArea = new TextArea(prestamo.getComentarios());
            comentariosArea.setEditable(false);
            comentariosArea.setPrefRowCount(3);
            comentariosArea.setWrapText(true);
            comentariosArea.setStyle("-fx-background-color: #f5f5f5;");
            
            content.getChildren().addAll(comentariosLabel, comentariosArea);
        }
        
        section.getChildren().add(content);
        return section;
    }
    
    private VBox createReturnSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setStyle("-fx-background-color: " + SUCCESS_COLOR + "11; -fx-background-radius: 8px; -fx-border-color: " + SUCCESS_COLOR + "; -fx-border-width: 1; -fx-border-radius: 8px;");
        
        Label sectionTitle = new Label("Información de Devolución");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + SUCCESS_COLOR + ";");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        
        addDetailRow(grid, 0, "Fecha de Devolución:", formatDate(prestamo.getFechaDevuelto()));
        addDetailRow(grid, 1, "Hora de Devolución:", formatTime(prestamo.getHoraDevuelto()));
        addDetailRow(grid, 2, "Devuelto Por:", prestamo.getDevueltoPor());
        addDetailRow(grid, 3, "Recibido Por:", prestamo.getRecibidoPor());
        
        VBox content = new VBox(10);
        content.getChildren().addAll(sectionTitle, grid);
        
        if (prestamo.getEstadoDevuelto() != null && !prestamo.getEstadoDevuelto().trim().isEmpty()) {
            Label estadoLabel = new Label("Estado del Equipo Devuelto:");
            estadoLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
            
            TextArea estadoArea = new TextArea(prestamo.getEstadoDevuelto());
            estadoArea.setEditable(false);
            estadoArea.setPrefRowCount(2);
            estadoArea.setWrapText(true);
            estadoArea.setStyle("-fx-background-color: #f5f5f5;");
            
            content.getChildren().addAll(estadoLabel, estadoArea);
        }
        
        section.getChildren().add(content);
        return section;
    }
    
    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        Label valueNode = new Label(value != null ? value : "N/A");
        valueNode.setStyle("-fx-text-fill: " + TEXT_SECONDARY + ";");
        valueNode.setWrapText(true);
        
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }
    
    private String getStatusColor() {
        return prestamo.getStatusColor();
    }
    
    private FontIcon getStatusIcon() {
        switch (prestamo.getStatus()) {
            case "ACTIVO":
                return new FontIcon(FontAwesomeSolid.PLAY_CIRCLE);
            case "VENCIDO":
                return new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
            case "VENCE_HOY":
                return new FontIcon(FontAwesomeSolid.CLOCK);
            case "DEVUELTO":
                return new FontIcon(FontAwesomeSolid.CHECK_CIRCLE);
            default:
                return new FontIcon(FontAwesomeSolid.QUESTION_CIRCLE);
        }
    }
    
    private String formatDate(java.time.LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
    }
    
    private String formatTime(java.time.LocalTime time) {
        return time != null ? time.format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A";
    }
}
