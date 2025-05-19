package tescha.MainWindow.components;

import javafx.scene.control.TextField;

public class CustomTextField extends javafx.scene.control.TextField {
    public CustomTextField() {
        super();
        this.getStyleClass().add("modern-text-field");
    }
}