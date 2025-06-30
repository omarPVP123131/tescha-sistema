package tescha.Components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import java.util.Optional;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AlertUtils {
    private static final String ERROR_COLOR = "#e74c3c";
    private static final String WARNING_COLOR = "#f39c12";
    private static final String INFO_COLOR = "#3498db";
    private static final String SUCCESS_COLOR = "#2ecc71";
    private static final String CONFIRM_COLOR = "#9b59b6";

    private static final String BASE_STYLE =
            "-fx-background-radius: 8px; " +
                    "-fx-font-family: 'System'; " +
                    "-fx-padding: 15; " +
                    "-fx-background-color: white; " +
                    "-fx-border-width: 2px; " +
                    "-fx-border-radius: 8px; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);";

    private static final String BUTTON_STYLE =
            "-fx-background-radius: 4px; " +
                    "-fx-padding: 8 16; " +
                    "-fx-font-weight: bold; " +
                    "-fx-cursor: hand;";

    // Métodos simples (solo mensaje)
    public static void showError(String message) {
        showStyledAlert("Error", message, ERROR_COLOR, FontAwesomeIcon.TIMES_CIRCLE, false);
    }
    public static void showWarning(String message) {
        showStyledAlert("Advertencia", message, WARNING_COLOR, FontAwesomeIcon.EXCLAMATION_TRIANGLE, false);
    }
    public static void showInfo(String message) {
        showStyledAlert("Información", message, INFO_COLOR, FontAwesomeIcon.INFO_CIRCLE, false);
    }
    public static void showSuccess(String message) {
        showStyledAlert("Éxito", message, SUCCESS_COLOR, FontAwesomeIcon.CHECK_CIRCLE, false);
    }

    // Sobrecargas con título y mensaje separados
    public static void showError(String title, String message) {
        showStyledAlert(title, message, ERROR_COLOR, FontAwesomeIcon.TIMES_CIRCLE, false);
    }
    public static void showWarning(String title, String message) {
        showStyledAlert(title, message, WARNING_COLOR, FontAwesomeIcon.EXCLAMATION_TRIANGLE, false);
    }
    public static void showInfo(String title, String message) {
        showStyledAlert(title, message, INFO_COLOR, FontAwesomeIcon.INFO_CIRCLE, false);
    }
    public static void showSuccess(String title, String message) {
        showStyledAlert(title, message, SUCCESS_COLOR, FontAwesomeIcon.CHECK_CIRCLE, false);
    }

    public static boolean showConfirmation(String title, String header, String message) {
        Alert alert = createBaseAlert(title, header, CONFIRM_COLOR, FontAwesomeIcon.QUESTION_CIRCLE);
        alert.setContentText(message);

        ButtonType confirmButton = new ButtonType("Confirmar", ButtonData.OK_DONE);
        ButtonType cancelButton  = new ButtonType("Cancelar",  ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        // Estilado de botones (idéntico al otro método)
        Node confirmNode = alert.getDialogPane().lookupButton(confirmButton);
        confirmNode.setStyle(BUTTON_STYLE + "-fx-background-color: " + CONFIRM_COLOR + "; -fx-text-fill: white;");
        Node cancelNode = alert.getDialogPane().lookupButton(cancelButton);
        cancelNode.setStyle(BUTTON_STYLE + "-fx-background-color: transparent; -fx-text-fill: #555; -fx-border-color: #ddd;");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == confirmButton;
    }

    private static void showStyledAlert(String title, String message, String color, FontAwesomeIcon icon, boolean autoClose) {
        Alert alert = createBaseAlert(title, message, color, icon);

        if (autoClose) {
            setupAutoClose(alert);
        } else {
            ButtonType okButton = new ButtonType("Aceptar", ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(okButton);
            Node okNode = alert.getDialogPane().lookupButton(okButton);
            okNode.setStyle(BUTTON_STYLE + "-fx-background-color: " + color + "; -fx-text-fill: white;");
        }

        alert.showAndWait();
    }

    private static Alert createBaseAlert(String title, String message, String color, FontAwesomeIcon icon) {
        Alert alert = new Alert(AlertType.NONE);
        alert.setTitle(title);
        alert.setHeaderText(null);

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().clear();
        stage.initStyle(StageStyle.TRANSPARENT);

        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setSize("32");
        iconView.setFill(Color.web(color));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web(color));

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(400);

        HBox iconBox = new HBox(iconView);
        iconBox.setAlignment(Pos.CENTER);

        VBox textBox = new VBox(5, titleLabel, messageLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);

        HBox contentBox = new HBox(15, iconBox, textBox);
        contentBox.setAlignment(Pos.CENTER_LEFT);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(contentBox);
        dialogPane.setStyle(BASE_STYLE + "-fx-border-color: " + color + ";");

        try {
            String cssPath = AlertUtils.class.getResource("/styles/alerts.css").toExternalForm();
            dialogPane.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("No se pudo cargar el archivo CSS. Asegúrese de que alerts.css esté en resources/styles/");
        }

        return alert;
    }

    public static Optional<String> showTextInputDialog(String title, String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().clear();
        stage.initStyle(StageStyle.TRANSPARENT);

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setStyle(BASE_STYLE + "-fx-border-color: " + INFO_COLOR + ";");

        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.EDIT);
        iconView.setSize("24");
        iconView.setFill(Color.web(INFO_COLOR));

        HBox iconBox = new HBox(iconView);
        iconBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web(INFO_COLOR));

        Label contentLabel = new Label(content);
        contentLabel.setWrapText(true);

        VBox textBox = new VBox(5, titleLabel, contentLabel);
        textBox.setAlignment(Pos.CENTER_LEFT);

        HBox headerBox = new HBox(15, iconBox, textBox);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        dialogPane.setHeader(headerBox);

        try {
            String cssPath = AlertUtils.class.getResource("/styles/alerts.css").toExternalForm();
            dialogPane.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("No se pudo cargar el archivo CSS");
        }

        Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        okButton.setStyle(BUTTON_STYLE + "-fx-background-color: " + INFO_COLOR + "; -fx-text-fill: white;");

        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setStyle(BUTTON_STYLE + "-fx-background-color: transparent; -fx-text-fill: #555; -fx-border-color: #ddd;");

        return dialog.showAndWait();
    }

    private static void setupAutoClose(Alert alert) {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getButtonTypes().clear();

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), dialogPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.seconds(3));
        delay.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), dialogPane);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> ((Stage) dialogPane.getScene().getWindow()).close());
            fadeOut.play();
        });
        delay.play();
    }
}