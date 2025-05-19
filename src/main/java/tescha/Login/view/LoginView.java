package tescha.Login.view;

import tescha.Login.model.UserModel;
import animatefx.animation.FadeIn;
import animatefx.animation.Pulse;
import animatefx.animation.Shake;
import animatefx.animation.ZoomIn;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.effects.JFXDepthManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Set;

public class LoginView {
    private Stage primaryStage;
    // Controles del login
    private JFXComboBox<String> userComboBox;
    private JFXPasswordField passwordField;
    private Label roleLabel;
    private JFXButton loginButton;
    private Label messageLabel;
    // Contenedores de controles con íconos
    private HBox userComboBoxContainer;
    private HBox passwordFieldContainer;
    // Control para mostrar el avatar predefinido
    private ImageView avatarImageView;
    // Otros contenedores
    private StackPane rootPane;
    private VBox loginCard;
    private HBox headerBox;

    public Image getSelectedAvatar() {
        return avatarImageView.getImage();
    }

    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeComponents();
        setupLayout();
        setupScene();
    }

    /**
     * Inicializa los componentes de la interfaz.
     */
    private void initializeComponents() {
        userComboBoxContainer = createUserComboBoxContainer();
        passwordFieldContainer = createPasswordFieldContainer();
        roleLabel = createRoleLabel();
        loginButton = createLoginButton();
        messageLabel = createMessageLabel();
        avatarImageView = createAvatarImageView(); // Se asignará según el rol
    }

    /**
     * Crea un HBox que contiene un ícono de usuario y un JFXComboBox.
     */
    private HBox createUserComboBoxContainer() {
        JFXComboBox<String> comboBox = new JFXComboBox<>();
        comboBox.setPromptText("Selecciona un usuario");
        comboBox.setPrefWidth(250);
        comboBox.setFocusColor(Color.valueOf("#3f51b5"));
        comboBox.setUnFocusColor(Color.valueOf("#b0bec5"));
        comboBox.getStyleClass().add("jfx-combo-box");

        FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
        userIcon.getStyleClass().add("custom-icon");
        userIcon.setSize("18");

        HBox container = new HBox(10, userIcon, comboBox);
        container.getStyleClass().add("field-container");
        container.setAlignment(Pos.CENTER_LEFT);
        this.userComboBox = comboBox;
        return container;
    }

    /**
     * Crea un HBox que contiene un ícono de candado y un JFXPasswordField.
     */
    private HBox createPasswordFieldContainer() {
        JFXPasswordField passwordField = new JFXPasswordField();
        passwordField.setPromptText("Contraseña");
        passwordField.setPrefWidth(250);
        passwordField.setFocusColor(Color.valueOf("#3f51b5"));
        passwordField.setUnFocusColor(Color.valueOf("#b0bec5"));
        passwordField.getStyleClass().add("jfx-password-field");

        FontAwesomeIconView lockIcon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
        lockIcon.getStyleClass().add("custom-icon");
        lockIcon.setSize("18");

        HBox container = new HBox(10, lockIcon, passwordField);
        container.getStyleClass().add("field-container");
        container.setAlignment(Pos.CENTER_LEFT);
        this.passwordField = passwordField;
        return container;
    }

    /**
     * Crea y configura la etiqueta que mostrará el rol del usuario.
     */
    private Label createRoleLabel() {
        Label roleLabel = new Label();
        roleLabel.getStyleClass().add("role-label");
        roleLabel.setVisible(false); // Inicialmente oculta
        roleLabel.setManaged(false); // No reserva espacio cuando está oculta
        return roleLabel;
    }

    /**
     * Crea y configura la etiqueta para mostrar mensajes.
     */
    private Label createMessageLabel() {
        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(280);
        // Inicialmente oculta
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
        return messageLabel;
    }

    /**
     * Crea y configura el botón de inicio de sesión.
     */
    private JFXButton createLoginButton() {
        JFXButton loginButton = new JFXButton("INICIAR SESIÓN");
        loginButton.setPrefWidth(280);
        loginButton.setPrefHeight(45);
        loginButton.getStyleClass().add("login-button");
        loginButton.setButtonType(JFXButton.ButtonType.RAISED);

        FontAwesomeIconView loginIcon = new FontAwesomeIconView(FontAwesomeIcon.SIGN_IN);
        loginIcon.setFill(Color.WHITE);
        loginIcon.setSize("16");
        loginButton.setGraphic(loginIcon);
        loginButton.setGraphicTextGap(10);
        return loginButton;
    }

    /**
     * Crea un ImageView para mostrar el avatar.
     * Se asignará según el rol del usuario.
     */
    private ImageView createAvatarImageView() {
        // Se puede cargar una imagen por defecto o dejarlo vacío
        ImageView avatarView = new ImageView();
        avatarView.setFitWidth(80);
        avatarView.setFitHeight(80);
        avatarView.setPreserveRatio(true);
        avatarView.setSmooth(true);

        // Aplicar un círculo al avatar
        Circle clip = new Circle(40, 40, 40);
        avatarView.setClip(clip);

        // Estilo y clases
        avatarView.getStyleClass().add("user-avatar");

        // Oculto inicialmente
        avatarView.setVisible(false);
        avatarView.setManaged(false);

        return avatarView;
    }

    /**
     * Actualiza la imagen del avatar según el rol del usuario.
     * Se esperan dos imágenes en resources: /admin.jpg y /empleado.png.
     */
    public void updateAvatarBasedOnRole(String role) {
        String imagePath;
        if ("Administrador".equalsIgnoreCase(role)) {
            imagePath = "/admin.jpg";
        } else {
            imagePath = "/empleado.png";
        }
        try {
            Image avatar = new Image(getClass().getResourceAsStream(imagePath));
            avatarImageView.setImage(avatar);

            // Haciendo visible el avatar con animación
            avatarImageView.setVisible(true);
            avatarImageView.setManaged(true);

            // Aplicamos animación al avatar
            new FadeIn(avatarImageView).setSpeed(1.5).play();
        } catch (Exception e) {
            System.err.println("No se pudo cargar el avatar para el rol: " + role);
        }
    }

    /**
     * Crea y configura el logo de la aplicación.
     */
    private ImageView createLogo() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/logo.png"));
            ImageView logoView = new ImageView(logo);
            logoView.setFitWidth(120);
            logoView.setPreserveRatio(true);

            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(5.0);
            dropShadow.setOffsetX(3.0);
            dropShadow.setOffsetY(3.0);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));
            logoView.setEffect(dropShadow);

            return logoView;
        } catch (Exception e) {
            System.err.println("Logo no pudo ser cargado");
            return new ImageView();
        }
    }

    /**
     * Configura el layout del login.
     */
    private void setupLayout() {
        // Header con logo y título
        ImageView logoView = createLogo();
        Text titleText = new Text("TITEC");
        titleText.getStyleClass().add("login-title");
        headerBox = new HBox(15, logoView, titleText);
        headerBox.setAlignment(Pos.CENTER);

        // Contenedor tipo "card" para el formulario de login
        loginCard = new VBox(20);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(30, 40, 30, 40));
        loginCard.getStyleClass().add("login-card");

        // Separador personalizado con efecto gradiente
        Separator separator = new Separator();
        separator.getStyleClass().add("separator");

        // Crear contenedor para el avatar
        StackPane avatarContainer = new StackPane(avatarImageView);
        avatarContainer.getStyleClass().add("avatar-container");

        Hyperlink forgotPasswordLink = new Hyperlink("¿Olvidaste tu contraseña?");
        forgotPasswordLink.getStyleClass().add("forgot-password-link");

        // Agregar componentes al loginCard (incluye avatar)
        loginCard.getChildren().addAll(
                headerBox,
                separator,
                userComboBoxContainer,
                passwordFieldContainer,
                roleLabel,
                avatarContainer,
                loginButton,
                forgotPasswordLink,
                messageLabel
        );

        JFXDepthManager.setDepth(loginCard, 4); // Efecto de profundidad

        StackPane backgroundPane = new StackPane();
        backgroundPane.getStyleClass().add("background-pane");

        rootPane = new StackPane(backgroundPane, loginCard);
    }

    /**
     * Configura la escena y estilos de la ventana.
     */
    private void setupScene() {
        Scene scene = new Scene(rootPane, 450, 650);

        // Cargar estilos externos
        scene.getStylesheets().add(getClass().getResource("/login-styles.css").toExternalForm());
        scene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap");

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Punto de Venta - Login");
        primaryStage.setResizable(false);
        addWindowControls();
    }

    /**
     * Agrega controles de ventana (cerrar, minimizar) y hace la ventana arrastrable.
     */
    private void addWindowControls() {
        FontAwesomeIconView closeIcon = new FontAwesomeIconView(FontAwesomeIcon.TIMES);
        closeIcon.setFill(Color.valueOf("#e74c3c"));
        closeIcon.setSize("16");
        FontAwesomeIconView minimizeIcon = new FontAwesomeIconView(FontAwesomeIcon.MINUS);
        minimizeIcon.setFill(Color.valueOf("#3498db"));
        minimizeIcon.setSize("16");

        JFXButton closeButton = new JFXButton();
        closeButton.setGraphic(closeIcon);
        closeButton.getStyleClass().add("window-button");
        closeButton.setOnAction(e -> primaryStage.close());

        JFXButton minimizeButton = new JFXButton();
        minimizeButton.setGraphic(minimizeIcon);
        minimizeButton.getStyleClass().add("window-button");
        minimizeButton.setOnAction(e -> primaryStage.setIconified(true));

        HBox windowControls = new HBox(10, minimizeButton, closeButton);
        windowControls.setAlignment(Pos.CENTER_RIGHT);
        loginCard.getChildren().add(0, windowControls);
        makeDraggable();
    }

    /**
     * Permite mover la ventana arrastrándola.
     */
    private void makeDraggable() {
        final double[] xOffset = {0};
        final double[] yOffset = {0};
        rootPane.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });
        rootPane.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset[0]);
            primaryStage.setY(event.getScreenY() - yOffset[0]);
        });
    }

    /**
     * Permite poblar el ComboBox con nombres de usuario.
     */
    public void populateUserComboBox(Set<String> usernames) {
        userComboBox.getItems().addAll(usernames);

        // Establece un listener para mostrar el rol cuando se seleccione un usuario
        userComboBox.setOnAction(e -> {
            String selectedUser = userComboBox.getValue();
            if (selectedUser != null && !selectedUser.isEmpty()) {
                // Aquí normalmente obtendrías el rol de la base de datos
                // Pero para ejemplo, mostraremos algo ficticio
                updateRoleLabel("Empleado"); // Por defecto, mostrar como empleado
            }
        });
    }

    /**
     * Muestra un mensaje de error con animación.
     */
    public void showErrorMessage(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("success-label");
        messageLabel.getStyleClass().add("error-label");

        // Hacemos visible el mensaje
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);

        new Shake(messageLabel).play();
    }

    /**
     * Muestra un mensaje de éxito con animación.
     */
    public void showSuccessMessage(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("error-label");
        messageLabel.getStyleClass().add("success-label");

        // Hacemos visible el mensaje
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);

        new FadeIn(messageLabel).play();
        new Pulse(loginCard).play();
    }

    /**
     * Actualiza la etiqueta del rol y el avatar según el rol.
     */
    public void updateRoleLabel(String role) {
        roleLabel.setText("Rol: " + role);

        // Hacemos visible la etiqueta
        roleLabel.setVisible(true);
        roleLabel.setManaged(true);

        new FadeIn(roleLabel).play();

        // Actualiza el avatar según el rol predefinido
        updateAvatarBasedOnRole(role);
    }

    // Getters para los controles necesarios en el controlador
    public JFXComboBox<String> getUserComboBox() {
        return userComboBox;
    }

    public JFXPasswordField getPasswordField() {
        return passwordField;
    }

    public JFXButton getLoginButton() {
        return loginButton;
    }

    /**
     * Muestra la ventana de login con animación de entrada.
     */
    public void show() {
        primaryStage.show();
        new ZoomIn(loginCard).setSpeed(1.3).play();
    }
}