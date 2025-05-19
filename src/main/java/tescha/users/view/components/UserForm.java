package tescha.users.view.components;

import animatefx.animation.*;
import com.jfoenix.controls.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import javafx.scene.*;
import javafx.util.Duration;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;
import tescha.users.dto.UserDTO;
import tescha.users.controller.UserController;
import tescha.Components.AlertUtils;
import java.io.File;

public class UserForm extends StackPane {
    private final UserDTO user;
    private final UserController controller;
    private JFXTextField usernameField;
    private JFXPasswordField passwordField;
    private JFXTextField nombreField;
    private JFXTextField telefonoField;
    private JFXTextField departamentoField;
    private JFXComboBox<String> rolCombo;
    private JFXToggleButton activoToggle;
    private ImageView imageView;
    private String imagePath;
    private BorderPane mainContainer;
    private Stage currentStage;
    private boolean isNewUser;

    public UserForm(UserDTO user, UserController controller) {
        this.user = user != null ? user : new UserDTO();
        this.controller = controller;
        this.isNewUser = user == null || user.getId() == null;

        // Configurar estilos y tamaño
        getStyleClass().add("user-form-container");
        setPrefWidth(550);
        setPrefHeight(650);

        // Inicializar el contenedor principal
        mainContainer = new BorderPane();
        mainContainer.getStyleClass().add("user-form");

        // Crear los componentes de la UI
        createHeader();
        createForm();
        createFooter();

        // Añadir el contenedor principal a esta vista
        getChildren().add(mainContainer);

        // Añadir sombra al form
        setEffect(new javafx.scene.effect.DropShadow(20, Color.rgb(0, 0, 0, 0.2)));

        // Cargar estilos CSS
        getStylesheets().add(getClass().getResource("/styles/users.css").toExternalForm());

        // Animar la entrada del formulario
        setOpacity(0);
    }

    public void showAndWait() {
        if (currentStage == null) {
            currentStage = new Stage();
            currentStage.initModality(Modality.APPLICATION_MODAL);
            currentStage.setTitle(isNewUser ? "Nuevo Usuario" : "Editar Usuario");
            currentStage.setScene(new Scene(this));
            currentStage.setResizable(false);

            // Cerrar el form al hacer clic fuera
            currentStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    // closeWithAnimation();
                }
            });
        }

        // Mostrar el stage y animarlo
        currentStage.showAndWait();
        new FadeIn(this).setSpeed(1.5).play();
    }

    private void createHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("form-header");
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER_LEFT);

        // Icono para el formulario
        FontIcon formIcon = new FontIcon(isNewUser ?
                MaterialDesignA.ACCOUNT_PLUS : MaterialDesignA.ACCOUNT_EDIT);
        formIcon.setIconSize(24);
        formIcon.getStyleClass().add("form-icon");

        // Título del formulario
        Label titleLabel = new Label(isNewUser ? "Nuevo Usuario" : "Editar Usuario");
        titleLabel.getStyleClass().add("form-title");
        HBox.setMargin(titleLabel, new Insets(0, 0, 0, 10));

        // Botón de cerrar
        JFXButton closeButton = new JFXButton();
        closeButton.getStyleClass().add("close-button");
        FontIcon closeIcon = new FontIcon(MaterialDesignC.CLOSE);
        closeIcon.getStyleClass().add("close-icon");
        closeButton.setGraphic(closeIcon);
        closeButton.setOnAction(e -> closeWithAnimation());

        // Espaciador para alinear el botón a la derecha
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(formIcon, titleLabel, spacer, closeButton);
        mainContainer.setTop(header);
    }

    private void createForm() {
        JFXTabPane tabPane = new JFXTabPane();
        tabPane.getStyleClass().add("form-tabs");

        // Pestaña de información básica
        Tab basicInfoTab = new Tab("Información Básica");
        basicInfoTab.setGraphic(new FontIcon(MaterialDesignI.INFORMATION_OUTLINE));
        basicInfoTab.setClosable(false);

        // Pestaña de configuración
        Tab configTab = new Tab("Configuración");
        configTab.setGraphic(new FontIcon(MaterialDesignS.SETTINGS_HELPER));
        configTab.setClosable(false);

        // Contenido de la pestaña de información básica
        GridPane basicInfoGrid = new GridPane();
        basicInfoGrid.getStyleClass().add("form-grid");
        basicInfoGrid.setPadding(new Insets(25, 30, 20, 30));
        basicInfoGrid.setVgap(20);
        basicInfoGrid.setHgap(15);

        // Campos para información básica
        nombreField = createTextField("Nombre", user.getNombre(), MaterialDesignA.ACCOUNT_CIRCLE_OUTLINE);
        usernameField = createTextField("Nombre de usuario", user.getUsername(), MaterialDesignA.AT);
        telefonoField = createTextField("Teléfono", user.getTelefono(), MaterialDesignP.PHONE);
        departamentoField = createTextField("Departamento", user.getDepartamento(), MaterialDesignD.DOMAIN);

        // Agregar los campos a la grid
        basicInfoGrid.add(createFormGroup("Nombre", nombreField, MaterialDesignA.ACCOUNT_CIRCLE_OUTLINE), 0, 0, 2, 1);
        basicInfoGrid.add(createFormGroup("Usuario", usernameField, MaterialDesignA.AT), 0, 1, 2, 1);
        basicInfoGrid.add(createFormGroup("Teléfono", telefonoField, MaterialDesignP.PHONE), 0, 2);
        basicInfoGrid.add(createFormGroup("Departamento", departamentoField, MaterialDesignD.DOMAIN), 1, 2);

        // Configuración del avatar
        VBox avatarBox = new VBox(15);
        avatarBox.setAlignment(Pos.CENTER);
        avatarBox.setPadding(new Insets(20, 0, 10, 0));

        Label avatarLabel = new Label("Imagen de Perfil");
        avatarLabel.getStyleClass().add("section-label");

        // Contenedor del avatar
        StackPane avatarContainer = new StackPane();
        avatarContainer.getStyleClass().add("avatar-editor-container");

        imageView = new ImageView();
        imageView.setFitHeight(120);
        imageView.setFitWidth(120);
        imageView.setPreserveRatio(true);

        // Recortar la imagen en círculo
        Circle clip = new Circle(60, 60, 60);
        imageView.setClip(clip);

        // Cargar la imagen actual o placeholder
        updateImage();

        // Botón para cambiar la imagen
        JFXButton selectImageBtn = new JFXButton("Cambiar");
        selectImageBtn.getStyleClass().add("image-button");
        FontIcon cameraIcon = new FontIcon(MaterialDesignC.CAMERA);
        selectImageBtn.setGraphic(cameraIcon);
        selectImageBtn.setOnAction(e -> selectImage());

        avatarContainer.getChildren().addAll(imageView);
        avatarBox.getChildren().addAll(avatarLabel, avatarContainer, selectImageBtn);

        VBox basicInfoContent = new VBox(20);
        basicInfoContent.getChildren().addAll(avatarBox, basicInfoGrid);

        basicInfoTab.setContent(new ScrollPane(basicInfoContent));

        // Contenido de la pestaña de configuración
        GridPane configGrid = new GridPane();
        configGrid.getStyleClass().add("form-grid");
        configGrid.setPadding(new Insets(25, 30, 20, 30));
        configGrid.setVgap(25);
        configGrid.setHgap(15);

        // Campos para configuración
        passwordField = new JFXPasswordField();
        passwordField.setPromptText("Contraseña" + (isNewUser ? "" : " (dejar en blanco para mantener)"));
        passwordField.getStyleClass().add("jfx-password-field");

        // ComboBox para rol
        rolCombo = new JFXComboBox<>();
        rolCombo.getItems().addAll("usuario", "admin");
        rolCombo.setValue(user.getRol() != null ? user.getRol() : "usuario");
        rolCombo.getStyleClass().add("jfx-combo-box");
        rolCombo.setPromptText("Seleccione un rol");

        // Toggle para estado
        activoToggle = new JFXToggleButton();
        activoToggle.setText("Usuario activo");
        activoToggle.setSelected(user.isActivo());
        activoToggle.getStyleClass().add("jfx-toggle-button");

        // Agregar los campos a la grid
        configGrid.add(createFormGroup("Contraseña", passwordField, MaterialDesignK.KEY_OUTLINE), 0, 0, 2, 1);
        configGrid.add(createFormGroup("Rol de Usuario", rolCombo, MaterialDesignA.ACCOUNT_KEY), 0, 1, 2, 1);

        HBox toggleContainer = new HBox();
        toggleContainer.setAlignment(Pos.CENTER_LEFT);
        toggleContainer.getChildren().add(activoToggle);
        configGrid.add(toggleContainer, 0, 2, 2, 1);

        configTab.setContent(new ScrollPane(configGrid));

        // Añadir pestañas al tabPane
        tabPane.getTabs().addAll(basicInfoTab, configTab);

        mainContainer.setCenter(tabPane);
    }

    private Node createFormGroup(String labelText, Control field, Ikon iconCode) {
        VBox container = new VBox(3);
        container.getStyleClass().add("form-group");

        Label label = new Label(labelText);
        label.getStyleClass().add("field-label");

        HBox fieldContainer = new HBox(10);
        fieldContainer.setAlignment(Pos.CENTER_LEFT);

        // Ahora usamos directamente el Ikon
        FontIcon icon = new FontIcon(iconCode);
        icon.getStyleClass().add("field-icon");

        fieldContainer.getChildren().addAll(icon, field);
        HBox.setHgrow(field, Priority.ALWAYS);

        container.getChildren().addAll(label, fieldContainer);
        return container;
    }

    private JFXTextField createTextField(String prompt, String value, Enum<?> iconCode) {
        JFXTextField field = new JFXTextField(value != null ? value : "");
        field.setPromptText(prompt);
        field.getStyleClass().add("jfx-text-field");
        return field;
    }

    private void createFooter() {
        HBox footer = new HBox();
        footer.getStyleClass().add("form-footer");
        footer.setPadding(new Insets(15, 20, 15, 20));
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10);

        // Botón de cancelar
        JFXButton cancelBtn = new JFXButton("Cancelar");
        cancelBtn.getStyleClass().add("cancel-button");
        FontIcon cancelIcon = new FontIcon(MaterialDesignC.CLOSE);
        cancelBtn.setGraphic(cancelIcon);
        cancelBtn.setOnAction(e -> closeWithAnimation());

        // Botón de guardar
        JFXButton saveBtn = new JFXButton("Guardar");
        saveBtn.getStyleClass().add("save-button");
        FontIcon saveIcon = new FontIcon(MaterialDesignC.CONTENT_SAVE);
        saveBtn.setGraphic(saveIcon);
        saveBtn.setOnAction(e -> {
            // Animación al guardar
            new Pulse(saveBtn).play();
            saveUser();
        });

        footer.getChildren().addAll(cancelBtn, saveBtn);
        mainContainer.setBottom(footer);
    }

    private void updateImage() {
        try {
            if (user.getImagen() != null && !user.getImagen().isEmpty()) {
                File file = new File(user.getImagen());
                if (file.exists()) {
                    imageView.setImage(new Image(file.toURI().toString()));
                    imagePath = user.getImagen();
                } else {
                    setDefaultImage();
                }
            } else {
                setDefaultImage();
            }
        } catch (Exception e) {
            setDefaultImage();
        }
    }

    private void setDefaultImage() {
        imageView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
        imagePath = null;
    }

    private void selectImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();

            // Animación al cambiar la imagen
            FadeOutLeft fadeOut = new FadeOutLeft(imageView);
            fadeOut.setOnFinished(e -> {
                imageView.setImage(new Image(selectedFile.toURI().toString()));
                new FadeInRight(imageView).play();
            });
            fadeOut.play();
        }
    }

    private void saveUser() {
        try {
            // Validación básica
            if (nombreField.getText().isEmpty() || usernameField.getText().isEmpty()) {
                AlertUtils.showError("Error", "El nombre y el usuario son obligatorios");
                return;
            }

            user.setUsername(usernameField.getText());
            if (passwordField.getText() != null && !passwordField.getText().isEmpty()) {
                user.setPassword(passwordField.getText());
            }
            user.setNombre(nombreField.getText());
            user.setTelefono(telefonoField.getText());
            user.setDepartamento(departamentoField.getText());
            user.setRol(rolCombo.getValue());
            user.setActivo(activoToggle.isSelected());
            user.setImagen(imagePath);

            if (controller.saveUser(user)) {
                AlertUtils.showSuccess("Éxito", "Usuario guardado correctamente");
                closeWithAnimation();
            } else {
                AlertUtils.showError("Error", "No se pudo guardar el usuario");
            }
        } catch (IllegalArgumentException e) {
            AlertUtils.showError("Error", e.getMessage());
        }
    }

    private void closeWithAnimation() {
        FadeOut fadeOut = new FadeOut(this);
        fadeOut.setSpeed(1.8);
        fadeOut.setOnFinished(e -> {
            if (currentStage != null) {
                currentStage.close();
                currentStage = null;     // importante: descartar la referencia
                setOpacity(0);           // reset para el próximo open
            }
        });
        fadeOut.play();
    }

}