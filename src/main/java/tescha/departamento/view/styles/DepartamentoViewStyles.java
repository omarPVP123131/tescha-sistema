package tescha.departamento.view.styles;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

public class DepartamentoViewStyles {

    // Paleta de colores
    public static final String PRIMARY_COLOR = "#667eea";
    public static final String PRIMARY_DARK = "#5a67d8";
    public static final String SUCCESS_COLOR = "#48bb78";
    public static final String SUCCESS_DARK = "#38a169";
    public static final String DANGER_COLOR = "#f56565";
    public static final String DANGER_DARK = "#e53e3e";
    public static final String WARNING_COLOR = "#ed8936";
    public static final String WARNING_DARK = "#dd6b20";
    public static final String GRAY_COLOR = "#a0aec0";
    public static final String GRAY_DARK = "#718096";
    public static final String INFO_COLOR = "#4299e1";
    public static final String INFO_DARK = "#3182ce";

    // Gradientes
    public Background createGradientBackground() {
        return new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.web("#f5f7fa")),
                        new Stop(1, Color.web("#c3cfe2"))),
                CornerRadii.EMPTY,
                Insets.EMPTY
        ));
    }

    // Estilos de contenedor principal
    public String getMainContainerStyle() {
        return "-fx-font-family: 'Segoe UI', 'Roboto', sans-serif;";
    }

    // Estilos de sombras
    public String getCardShadow() {
        return "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);";
    }

    public String getButtonShadow() {
        return "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 6, 0, 0, 3);";
    }

    // Estilos de transición
    public String getHoverTransition() {
        return "-fx-transition: all 0.3s ease;";
    }
}