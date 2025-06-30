package tescha.departamento.view.animations;

import animatefx.animation.*;
import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.util.Duration;
import javafx.application.Platform;

public class DepartamentoViewAnimations {

    public void initializeAnimations(Node rootNode) {
        // Configurar animaciones globales si es necesario
    }

    public void playEntryAnimations(Node leftPanel, Node rightPanel) {
        new FadeInLeft(leftPanel).setSpeed(1.5).play();
        new FadeInRight(rightPanel).setSpeed(1.5).setDelay(Duration.millis(200)).play();
    }

    public void playFocusAnimation(Control field) {
        new FadeInUp(field).setSpeed(2.0).play();
    }

    public void playSelectionAnimation(Control... fields) {
        for (Control field : fields) {
            new Flash(field).play();
        }
    }

    public void playClearAnimation(Control... fields) {
        for (Control field : fields) {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.3), field);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(e -> {
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.3), field);
                fadeIn.setToValue(1);
                fadeIn.play();
            });
            fadeOut.play();
        }
    }

    public void playSuccessAnimation(Node node) {
        new Bounce(node).play();
    }

    public void playErrorAnimation(Node node) {
        new Shake(node).play();
    }

    public void playRefreshAnimation(Node node) {
        new RotateIn(node).setSpeed(2.0).play();
    }

    public void playFilterAnimation(TableView<?> table) {
        new FadeIn(table).setSpeed(2.0).play();
    }

    public void playTableLoadAnimation(TableView<?> table) {
        new FadeInUp(table).setSpeed(1.5).play();
    }

    public void playStatusAnimation(Node statusLabel) {
        new FadeIn(statusLabel).setSpeed(3.0).play();
    }
}