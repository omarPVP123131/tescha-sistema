package tescha.Login.view;

import javafx.application.Platform;
import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.effects.JFXDepthManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class LoginView {
    private Stage primaryStage;

    // Controles del login
    private JFXComboBox<String> userComboBox;
    private JFXPasswordField passwordField;
    private Label roleLabel;
    private JFXButton loginButton;
    private Label messageLabel;
    private JFXSpinner loadingSpinner;

    // Validación y tooltips
    private FontAwesomeIconView userValidationIcon;
    private FontAwesomeIconView passwordValidationIcon;
    private Tooltip userTooltip;
    private Tooltip passwordTooltip;

    // Contenedores de controles con íconos
    private HBox userComboBoxContainer;
    private HBox passwordFieldContainer;

    // Control para mostrar el avatar predefinido
    private ImageView avatarImageView;

    // Otros contenedores
    private StackPane rootPane;
    private VBox loginCard;
    private HBox headerBox;

    // Animaciones de fondo
    private Canvas backgroundCanvas;
    private AnimationTimer backgroundAnimation;
    private List<Particle> particles;
    private Timeline gradientAnimation;

    // Propiedades de validación
    private BooleanProperty userValid = new SimpleBooleanProperty(false);
    private BooleanProperty passwordValid = new SimpleBooleanProperty(false);

    // Clase para partículas de fondo
    private static class Particle {
        double x, y, vx, vy, size, opacity;
        Color color;

        Particle(double width, double height) {
            Random rand = new Random();
            x = rand.nextDouble() * width;
            y = rand.nextDouble() * height;
            vx = (rand.nextDouble() - 0.5) * 0.5;
            vy = (rand.nextDouble() - 0.5) * 0.5;
            size = rand.nextDouble() * 3 + 1;
            opacity = rand.nextDouble() * 0.3 + 0.1;

            // Colores sutiles que complementen el tema
            Color[] colors = {
                    Color.rgb(63, 81, 181, opacity),
                    Color.rgb(103, 58, 183, opacity),
                    Color.rgb(156, 39, 176, opacity),
                    Color.rgb(233, 30, 99, opacity)
            };
            color = colors[rand.nextInt(colors.length)];
        }

        void update(double width, double height) {
            x += vx;
            y += vy;

            if (x < 0 || x > width) vx *= -1;
            if (y < 0 || y > height) vy *= -1;

            x = Math.max(0, Math.min(width, x));
            y = Math.max(0, Math.min(height, y));
        }
    }

    public Image getSelectedAvatar() {
        return avatarImageView.getImage();
    }

    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeParticles();
        initializeComponents();
        setupValidation();
        setupLayout();
        setupScene();
    }

    /**
     * Inicializa las partículas para el fondo animado.
     */
    private void initializeParticles() {
        particles = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            particles.add(new Particle(450, 650));
        }
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
        loadingSpinner = createLoadingSpinner();
        avatarImageView = createAvatarImageView();
        backgroundCanvas = createBackgroundCanvas();
    }

    /**
     * Crea el canvas para las animaciones de fondo.
     */
    private Canvas createBackgroundCanvas() {
        Canvas canvas = new Canvas(450, 650);
        return canvas;
    }

    /**
     * Crea un spinner de carga.
     */
    private JFXSpinner createLoadingSpinner() {
        JFXSpinner spinner = new JFXSpinner();
        spinner.setRadius(15);
        spinner.setVisible(false);
        spinner.setManaged(false);
        return spinner;
    }

    /**
     * Crea un HBox que contiene un ícono de usuario y un JFXComboBox con validación.
     */
    private HBox createUserComboBoxContainer() {
        JFXComboBox<String> comboBox = new JFXComboBox<>();
        comboBox.setPromptText("Selecciona un usuario");
        comboBox.setPrefWidth(230);
        comboBox.setFocusColor(Color.valueOf("#3f51b5"));
        comboBox.setUnFocusColor(Color.valueOf("#b0bec5"));
        comboBox.getStyleClass().add("jfx-combo-box");

        FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
        userIcon.getStyleClass().add("custom-icon");
        userIcon.setSize("18");

        // Ícono de validación
        userValidationIcon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_CIRCLE);
        userValidationIcon.setFill(Color.valueOf("#e74c3c"));
        userValidationIcon.setSize("16");
        userValidationIcon.setVisible(false);

        // Tooltip
        userTooltip = new Tooltip("Por favor selecciona un usuario");
        userTooltip.getStyleClass().add("error-tooltip");
        Tooltip.install(userValidationIcon, userTooltip);

        HBox container = new HBox(10, userIcon, comboBox, userValidationIcon);
        container.getStyleClass().add("field-container");
        container.setAlignment(Pos.CENTER_LEFT);
        this.userComboBox = comboBox;
        return container;
    }

    /**
     * Crea un HBox que contiene un ícono de candado y un JFXPasswordField con validación.
     */
    private HBox createPasswordFieldContainer() {
        JFXPasswordField passwordField = new JFXPasswordField();
        passwordField.setPromptText("Contraseña");
        passwordField.setPrefWidth(230);
        passwordField.setFocusColor(Color.valueOf("#3f51b5"));
        passwordField.setUnFocusColor(Color.valueOf("#b0bec5"));
        passwordField.getStyleClass().add("jfx-password-field");

        FontAwesomeIconView lockIcon = new FontAwesomeIconView(FontAwesomeIcon.LOCK);
        lockIcon.getStyleClass().add("custom-icon");
        lockIcon.setSize("18");

        // Ícono de validación
        passwordValidationIcon = new FontAwesomeIconView(FontAwesomeIcon.EXCLAMATION_CIRCLE);
        passwordValidationIcon.setFill(Color.valueOf("#e74c3c"));
        passwordValidationIcon.setSize("16");
        passwordValidationIcon.setVisible(false);

        HBox container = new HBox(10, lockIcon, passwordField, passwordValidationIcon);
        container.getStyleClass().add("field-container");
        container.setAlignment(Pos.CENTER_LEFT);
        this.passwordField = passwordField;
        return container;
    }

    /**
     * Configura la validación en tiempo real.
     */
    private void setupValidation() {
        // Validación del combo box de usuario
        userComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isValid = newVal != null && !newVal.trim().isEmpty();
            userValid.set(isValid);

            if (isValid) {
                hideValidationError(userValidationIcon, userComboBoxContainer);
                // Mostrar rol cuando se seleccione usuario
                showUserRole(newVal);
            } else {
                showValidationError(userValidationIcon, userComboBoxContainer, "Usuario requerido");
            }
        });
    }

    /**
     * Muestra error de validación con animación.
     */
    private void showValidationError(FontAwesomeIconView icon, HBox container, String message) {
        if (!icon.isVisible()) {
            icon.setVisible(true);
            new FadeIn(icon).setSpeed(2.0).play();
            new Shake(container).play();
        }

        // Actualizar tooltip
        Tooltip tooltip = (Tooltip) icon.getProperties().get("javafx.scene.control.Tooltip");
        if (tooltip == null) {
            if (icon == userValidationIcon) {
                userTooltip.setText(message);
            } else {
                passwordTooltip.setText(message);
            }
        }
    }

    /**
     * Oculta error de validación con animación.
     */
    private void hideValidationError(FontAwesomeIconView icon, HBox container) {
        if (icon.isVisible()) {
            FadeOut fadeOut = new FadeOut(icon);
            fadeOut.setOnFinished(e -> icon.setVisible(false));
            fadeOut.setSpeed(2.0).play();
        }
    }

    /**
     * Muestra el rol del usuario con animación mejorada.
     */
    private void showUserRole(String username) {
        // Simular obtención de rol (en aplicación real vendría de BD)
        String role = username.toLowerCase().contains("admin") ? "Administrador" : "Empleado";
        updateRoleLabel(role);
    }

    /**
     * Crea y configura la etiqueta que mostrará el rol del usuario.
     */
    private Label createRoleLabel() {
        Label roleLabel = new Label();
        roleLabel.getStyleClass().add("role-label");
        roleLabel.setVisible(false);
        roleLabel.setManaged(false);

        // Agregar efecto de brillo sutil
        DropShadow glowEffect = new DropShadow();
        glowEffect.setColor(Color.valueOf("#3f51b5"));
        glowEffect.setRadius(10);
        glowEffect.setSpread(0.3);
        roleLabel.setEffect(glowEffect);

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
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
        return messageLabel;
    }

    /**
     * Crea y configura el botón de inicio de sesión con efectos mejorados.
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

        // Efectos hover mejorados
        loginButton.setOnMouseEntered(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), loginButton);
            scaleUp.setToX(1.05);
            scaleUp.setToY(1.05);
            scaleUp.play();

            // Efecto de brillo
            DropShadow glow = new DropShadow();
            glow.setColor(Color.valueOf("#3f51b5"));
            glow.setRadius(15);
            glow.setSpread(0.4);
            loginButton.setEffect(glow);
        });

        loginButton.setOnMouseExited(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), loginButton);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
            loginButton.setEffect(null);
        });

        return loginButton;
    }

    /**
     * Crea un ImageView para mostrar el avatar con efectos mejorados.
     */
    private ImageView createAvatarImageView() {
        ImageView avatarView = new ImageView();
        avatarView.setFitWidth(100);
        avatarView.setFitHeight(100);
        avatarView.setPreserveRatio(true);
        avatarView.setSmooth(true);

        // Aplicar un círculo al avatar con borde
        Circle clip = new Circle(50, 50, 50);
        avatarView.setClip(clip);

        // Efectos visuales mejorados
        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setOffsetX(3);
        shadow.setOffsetY(3);
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        avatarView.setEffect(shadow);

        avatarView.getStyleClass().add("user-avatar");
        avatarView.setVisible(false);
        avatarView.setManaged(false);

        return avatarView;
    }

    /**
     * Actualiza la imagen del avatar según el rol del usuario con animaciones mejoradas.
     */
    public void updateAvatarBasedOnRole(String role) {
        String imagePath = "admin".equalsIgnoreCase(role) ? "/admin.png" : "/empleado.png";

        try {
            Image avatar = new Image(getClass().getResourceAsStream(imagePath));
            avatarImageView.setImage(avatar);

            avatarImageView.setVisible(true);
            avatarImageView.setManaged(true);

            // Animación de entrada más sofisticada
            ParallelTransition entrance = new ParallelTransition();

            FadeIn fadeIn = new FadeIn(avatarImageView);
            fadeIn.setSpeed(1.5);

            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(600), avatarImageView);
            scaleIn.setFromX(0);
            scaleIn.setFromY(0);
            scaleIn.setToX(1);
            scaleIn.setToY(1);
            scaleIn.setInterpolator(Interpolator.EASE_OUT);

            entrance.getChildren().addAll(fadeIn.getTimeline(), scaleIn);
            entrance.play();

            // Efecto de pulso sutil y continuo
            startAvatarPulse();

        } catch (Exception e) {
            System.err.println("No se pudo cargar el avatar para el rol: " + role);
        }
    }

    /**
     * Inicia un efecto de pulso sutil en el avatar.
     */
    private void startAvatarPulse() {
        Timeline pulse = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(avatarImageView.scaleXProperty(), 1.0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(avatarImageView.scaleXProperty(), 1.02)),
                new KeyFrame(Duration.seconds(4), new KeyValue(avatarImageView.scaleXProperty(), 1.0))
        );
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    /**
     * Crea y configura el logo de la aplicación con efectos mejorados.
     */
    private ImageView createLogo() {
        try {
            Image logo = new Image(getClass().getResourceAsStream("/logo.png"));
            ImageView logoView = new ImageView(logo);
            logoView.setFitWidth(120);
            logoView.setPreserveRatio(true);

            // Efectos mejorados
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(8.0);
            dropShadow.setOffsetX(4.0);
            dropShadow.setOffsetY(4.0);
            dropShadow.setColor(Color.rgb(0, 0, 0, 0.3));

            InnerShadow innerShadow = new InnerShadow();
            innerShadow.setRadius(5);
            innerShadow.setColor(Color.rgb(255, 255, 255, 0.2));

            dropShadow.setInput(innerShadow);
            logoView.setEffect(dropShadow);

            return logoView;
        } catch (Exception e) {
            System.err.println("Logo no pudo ser cargado");
            return new ImageView();
        }
    }

    /**
     * Inicia las animaciones de fondo.
     */
    private void startBackgroundAnimations() {
        // Animación de partículas
        backgroundAnimation = new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawParticles();
            }
        };
        backgroundAnimation.start();

        // Animación de gradiente de fondo
        startGradientAnimation();
    }

    /**
     * Dibuja las partículas en el canvas.
     */
    private void drawParticles() {
        GraphicsContext gc = backgroundCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());

        for (Particle particle : particles) {
            particle.update(backgroundCanvas.getWidth(), backgroundCanvas.getHeight());

            gc.setFill(particle.color);
            gc.fillOval(particle.x - particle.size/2, particle.y - particle.size/2,
                    particle.size, particle.size);

            // Conectar partículas cercanas con líneas sutiles
            for (Particle other : particles) {
                double distance = Math.sqrt(Math.pow(particle.x - other.x, 2) +
                        Math.pow(particle.y - other.y, 2));
                if (distance < 100) {
                    gc.setStroke(Color.rgb(63, 81, 181, 0.1));
                    gc.setLineWidth(0.5);
                    gc.strokeLine(particle.x, particle.y, other.x, other.y);
                }
            }
        }
    }

    /**
     * Inicia la animación del gradiente de fondo.
     */
    private void startGradientAnimation() {
        gradientAnimation = new Timeline();

        // Cambio gradual de colores del fondo
        KeyFrame kf1 = new KeyFrame(Duration.seconds(0),
                e -> updateBackgroundGradient(0));
        KeyFrame kf2 = new KeyFrame(Duration.seconds(10),
                e -> updateBackgroundGradient(0.5));
        KeyFrame kf3 = new KeyFrame(Duration.seconds(20),
                e -> updateBackgroundGradient(1.0));

        gradientAnimation.getKeyFrames().addAll(kf1, kf2, kf3);
        gradientAnimation.setCycleCount(Timeline.INDEFINITE);
        gradientAnimation.setAutoReverse(true);
        gradientAnimation.play();
    }

    /**
     * Actualiza el gradiente de fondo.
     */
    private void updateBackgroundGradient(double progress) {
        // Colores base que cambian sutilmente
        Color color1 = Color.rgb(
                (int)(240 + 15 * Math.sin(progress * Math.PI)),
                (int)(248 + 7 * Math.cos(progress * Math.PI)),
                (int)(255 - 10 * Math.sin(progress * Math.PI))
        );

        Color color2 = Color.rgb(
                (int)(230 + 20 * Math.cos(progress * Math.PI)),
                (int)(230 + 20 * Math.sin(progress * Math.PI)),
                (int)(250 - 5 * Math.cos(progress * Math.PI))
        );

        LinearGradient gradient = new LinearGradient(0, 0, 1, 1, true,
                CycleMethod.NO_CYCLE,
                new Stop(0, color1),
                new Stop(1, color2)
        );

        rootPane.setBackground(new Background(new BackgroundFill(gradient, null, null)));
    }

    /**
     * Configura el layout del login con mejoras visuales.
     */
    private void setupLayout() {
        // Header con logo y título
        ImageView logoView = createLogo();
        Text titleText = new Text("TITEC");
        titleText.getStyleClass().add("login-title");

        // Efecto de texto brillante
        DropShadow textGlow = new DropShadow();
        textGlow.setColor(Color.valueOf("#3f51b5"));
        textGlow.setRadius(5);
        textGlow.setSpread(0.3);
        titleText.setEffect(textGlow);

        headerBox = new HBox(15, logoView, titleText);
        headerBox.setAlignment(Pos.CENTER);

        // Contenedor tipo "card" para el formulario de login
        loginCard = new VBox(20);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(30, 40, 30, 40));
        loginCard.getStyleClass().add("login-card");

        // Separador personalizado con efecto gradiente mejorado
        Separator separator = new Separator();
        separator.getStyleClass().add("separator");

        // Crear contenedor para el avatar con animación
        StackPane avatarContainer = new StackPane(avatarImageView);
        avatarContainer.getStyleClass().add("avatar-container");

        // Link de contraseña olvidada con efecto hover
        Hyperlink forgotPasswordLink = new Hyperlink("¿Olvidaste tu contraseña?");
        forgotPasswordLink.getStyleClass().add("forgot-password-link");

        forgotPasswordLink.setOnMouseEntered(e -> {
            new Pulse(forgotPasswordLink).setSpeed(2.0).play();
        });

        // Contenedor para botón y spinner de carga
        StackPane buttonContainer = new StackPane();
        buttonContainer.getChildren().addAll(loginButton, loadingSpinner);
        buttonContainer.setAlignment(Pos.CENTER);

        // Agregar componentes al loginCard
        loginCard.getChildren().addAll(
                headerBox,
                separator,
                userComboBoxContainer,
                passwordFieldContainer,
                roleLabel,
                avatarContainer,
                buttonContainer,
                forgotPasswordLink,
                messageLabel
        );

        JFXDepthManager.setDepth(loginCard, 5); // Mayor profundidad

        // Fondo con canvas para partículas
        StackPane backgroundPane = new StackPane();
        backgroundPane.getChildren().add(backgroundCanvas);
        backgroundPane.getStyleClass().add("background-pane");

        rootPane = new StackPane(backgroundPane, loginCard);
    }

    /**
     * Configura la escena y estilos de la ventana.
     */
    private void setupScene() {
        Scene scene = new Scene(rootPane, 450, 650);

        // Cargar estilos externos mejorados
        scene.getStylesheets().add(getClass().getResource("/login-styles.css").toExternalForm());
        scene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap");
        startBackgroundAnimations();

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Punto de Venta - Login");
        primaryStage.setResizable(false);
        addWindowControls();
    }

    /**
     * Agrega controles de ventana mejorados.
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
        closeButton.setOnAction(e -> {
            boolean hayDatos =
                    userComboBox.getValue() != null &&
                            !passwordField.getText().isEmpty();

            Runnable cerrarConAnimacion = () -> {
                FadeOut anim = new FadeOut(rootPane);
                anim.setOnFinished(evt -> primaryStage.close());
                anim.play();
            };

            if (hayDatos) {
                Alert confirmDialog = new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "¿Estás seguro de que deseas cerrar? Los datos ingresados se perderán.",
                        ButtonType.YES, ButtonType.NO
                );
                confirmDialog.setHeaderText("Confirmar cierre");
                confirmDialog.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        cerrarConAnimacion.run();
                    }
                });
            } else {
                cerrarConAnimacion.run();
            }
        });


        JFXButton minimizeButton = new JFXButton();
        minimizeButton.setGraphic(minimizeIcon);
        minimizeButton.getStyleClass().add("window-button");
        minimizeButton.setOnAction(e -> primaryStage.setIconified(true));

        // Efectos hover para botones de ventana
        addButtonHoverEffect(closeButton);
        addButtonHoverEffect(minimizeButton);

        HBox windowControls = new HBox(10, minimizeButton, closeButton);
        windowControls.setAlignment(Pos.CENTER_RIGHT);
        loginCard.getChildren().add(0, windowControls);
        makeDraggable();
    }

    /**
     * Añade efectos hover a los botones de ventana.
     */
    private void addButtonHoverEffect(JFXButton button) {
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setToX(1.2);
            scale.setToY(1.2);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
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
    }

    /**
     * Muestra el spinner de carga durante el login.
     */
    public void showLoadingSpinner() {
        loginButton.setVisible(false);
        loginButton.setManaged(false);
        loadingSpinner.setVisible(true);
        loadingSpinner.setManaged(true);

        // Animación de entrada del spinner
        new FadeIn(loadingSpinner).setSpeed(2.0).play();
    }

    /**
     * Oculta el spinner de carga.
     */
    public void hideLoadingSpinner() {
        FadeOut fadeOut = new FadeOut(loadingSpinner);
        fadeOut.setOnFinished(e -> {
            loadingSpinner.setVisible(false);
            loadingSpinner.setManaged(false);
            loginButton.setVisible(true);
            loginButton.setManaged(true);
            new FadeIn(loginButton).setSpeed(2.0).play();
        });
        fadeOut.play();
    }

    /**
     * Muestra un mensaje de error con animación mejorada.
     */
    public void showErrorMessage(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("success-label");
        messageLabel.getStyleClass().add("error-label");

        messageLabel.setVisible(true);
        messageLabel.setManaged(true);

        // Animaciones múltiples para mayor impacto
        ParallelTransition errorAnimation = new ParallelTransition();

        Shake shake = new Shake(messageLabel);
        shake.setSpeed(1.5);

        FadeIn fadeIn = new FadeIn(messageLabel);
        fadeIn.setSpeed(2.0);

        // Efecto de vibración en toda la tarjeta
        Shake cardShake = new Shake(loginCard);
        cardShake.setSpeed(1.2);

        errorAnimation.getChildren().addAll(
                shake.getTimeline(),
                fadeIn.getTimeline(),
                cardShake.getTimeline()
        );
        errorAnimation.play();

        // Efecto de color rojo temporal en el borde de la tarjeta
        Timeline colorPulse = new Timeline(
                new KeyFrame(Duration.millis(0),
                        e -> loginCard.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;")),
                new KeyFrame(Duration.millis(1000),
                        e -> loginCard.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;"))
        );
        colorPulse.play();
    }

    /**
     * Muestra un mensaje de éxito con animación mejorada.
     */
    public void showSuccessMessage(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().removeAll("error-label");
        messageLabel.getStyleClass().add("success-label");

        messageLabel.setVisible(true);
        messageLabel.setManaged(true);

        // Animaciones de éxito más elaboradas
        ParallelTransition successAnimation = new ParallelTransition();

        FadeIn fadeIn = new FadeIn(messageLabel);
        fadeIn.setSpeed(1.5);

        Pulse cardPulse = new Pulse(loginCard);
        cardPulse.setSpeed(1.2);

        // Efecto de zoom sutil
        ScaleTransition zoom = new ScaleTransition(Duration.millis(400), loginCard);
        zoom.setToX(1.02);
        zoom.setToY(1.02);
        zoom.setAutoReverse(true);
        zoom.setCycleCount(2);

        successAnimation.getChildren().addAll(
                fadeIn.getTimeline(),
                cardPulse.getTimeline(),
                zoom
        );
        successAnimation.play();

        // Efecto de color verde temporal en el borde
        Timeline colorPulse = new Timeline(
                new KeyFrame(Duration.millis(0),
                        e -> loginCard.setStyle("-fx-border-color: #27ae60; -fx-border-width: 2px;")),
                new KeyFrame(Duration.millis(2000),
                        e -> loginCard.setStyle("-fx-border-color: transparent; -fx-border-width: 0px;"))
        );
        colorPulse.play();

        // Efecto de partículas de celebración
        showCelebrationParticles();
    }

    /**
     * Muestra partículas de celebración temporalmente.
     */
    private void showCelebrationParticles() {
        List<Circle> celebrationParticles = new ArrayList<>();
        Random rand = new Random();

        for (int i = 0; i < 15; i++) {
            Circle particle = new Circle(2, Color.rgb(39, 174, 96, 0.8));
            particle.setCenterX(loginCard.getBoundsInParent().getCenterX());
            particle.setCenterY(loginCard.getBoundsInParent().getCenterY());

            rootPane.getChildren().add(particle);
            celebrationParticles.add(particle);

            // Animación de explosión de partículas
            TranslateTransition move = new TranslateTransition(Duration.millis(1000 + rand.nextInt(500)), particle);
            move.setByX((rand.nextDouble() - 0.5) * 200);
            move.setByY((rand.nextDouble() - 0.5) * 200);

            FadeTransition fade = new FadeTransition(Duration.millis(1500), particle);
            fade.setToValue(0);

            ParallelTransition particleAnimation = new ParallelTransition(move, fade);
            particleAnimation.setOnFinished(e -> rootPane.getChildren().remove(particle));
            particleAnimation.play();
        }
    }

    /**
     * Actualiza la etiqueta del rol con animaciones mejoradas.
     */
    public void updateRoleLabel(String role) {
        roleLabel.setText("Rol: " + role);

        roleLabel.setVisible(true);
        roleLabel.setManaged(true);

        // Animación de entrada más sofisticada
        ParallelTransition roleAnimation = new ParallelTransition();

        FadeIn fadeIn = new FadeIn(roleLabel);
        fadeIn.setSpeed(1.8);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(600), roleLabel);
        slideIn.setFromY(-20);
        slideIn.setToY(0);
        slideIn.setInterpolator(Interpolator.EASE_OUT);

        roleAnimation.getChildren().addAll(fadeIn.getTimeline(), slideIn);
        roleAnimation.play();

        // Actualiza el avatar según el rol
        updateAvatarBasedOnRole(role);
    }

    /**
     * Aplica un efecto de focus mejorado a los campos.
     */
    public void enhanceFieldFocus() {
        // Efecto focus para combo box
        userComboBox.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), userComboBoxContainer);
                scale.setToX(1.02);
                scale.setToY(1.02);
                scale.play();

                // Efecto de brillo
                DropShadow glow = new DropShadow();
                glow.setColor(Color.valueOf("#3f51b5"));
                glow.setRadius(10);
                glow.setSpread(0.3);
                userComboBoxContainer.setEffect(glow);
            } else {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), userComboBoxContainer);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
                userComboBoxContainer.setEffect(null);
            }
        });

        // Efecto focus para password field
        passwordField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), passwordFieldContainer);
                scale.setToX(1.02);
                scale.setToY(1.02);
                scale.play();

                DropShadow glow = new DropShadow();
                glow.setColor(Color.valueOf("#3f51b5"));
                glow.setRadius(10);
                glow.setSpread(0.3);
                passwordFieldContainer.setEffect(glow);
            } else {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), passwordFieldContainer);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
                passwordFieldContainer.setEffect(null);
            }
        });
    }

    /**
     * Efecto de entrada de la aplicación completa.
     */
    public void showEntranceAnimation() {
        // Animación de entrada de la tarjeta principal
        loginCard.setScaleX(0.8);
        loginCard.setScaleY(0.8);
        loginCard.setOpacity(0);

        ParallelTransition entrance = new ParallelTransition();

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), loginCard);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Scale in
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(800), loginCard);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);
        scaleIn.setInterpolator(Interpolator.EASE_OUT);

        // Slide in desde arriba
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(800), loginCard);
        slideIn.setFromY(-50);
        slideIn.setToY(0);
        slideIn.setInterpolator(Interpolator.EASE_OUT);

        entrance.getChildren().addAll(fadeIn, scaleIn, slideIn);
        entrance.setDelay(Duration.millis(200)); // Pequeño delay para mejor efecto
        entrance.play();
    }

    /**
     * Limpia recursos y detiene animaciones.
     */
    public void cleanup() {
        if (backgroundAnimation != null) {
            backgroundAnimation.stop();
        }
        if (gradientAnimation != null) {
            gradientAnimation.stop();
        }
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

    public BooleanProperty getUserValidProperty() {
        return userValid;
    }

    public BooleanProperty getPasswordValidProperty() {
        return passwordValid;
    }

    /**
     * Muestra la ventana de login con animación de entrada mejorada.
     */
    public void show() {
        primaryStage.show();

        // Configurar efectos de focus después de mostrar
        enhanceFieldFocus();

        // Animación de entrada
        showEntranceAnimation();

        // Focus inicial en el campo de usuario
        Platform.runLater(() -> userComboBox.requestFocus());
    }

    /**
     * Cierra la ventana con animación de salida.
     */
    public void closeWithAnimation() {
        ParallelTransition exit = new ParallelTransition();

        FadeOut fadeOut = new FadeOut(rootPane);
        fadeOut.setSpeed(2.0);

        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(400), loginCard);
        scaleOut.setToX(0.8);
        scaleOut.setToY(0.8);

        exit.getChildren().addAll(fadeOut.getTimeline(), scaleOut);
        exit.setOnFinished(e -> {
            cleanup();
            primaryStage.close();
        });
        exit.play();
    }
}