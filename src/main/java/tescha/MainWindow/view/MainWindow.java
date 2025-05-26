package tescha.MainWindow.view;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import animatefx.animation.*;
import javafx.animation.*;
import javafx.scene.chart.PieChart;
import org.kordamp.ikonli.fontawesome5.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material.Material;
import com.jfoenix.effects.JFXDepthManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import tescha.Components.AlertUtils;
import tescha.configuracion.controller.ConfiguracionController;
import tescha.configuracion.dao.ConfiguracionDAO;
import tescha.configuracion.dao.ConfiguracionSQLiteDAO;
import tescha.configuracion.service.ConfiguracionService;
import tescha.configuracion.view.ConfiguracionView;
import tescha.database.DatabaseManager;
import tescha.departamento.controller.DepartamentoController;
import tescha.departamento.dao.DepartamentoDAO;
import tescha.departamento.dao.DepartamentoSQLiteDAO;
import tescha.departamento.service.DepartamentoService;
import tescha.departamento.view.DepartamentoView;
import tescha.inventario.controller.InventarioController;
import tescha.inventario.dao.InventarioDAO;
import tescha.inventario.dao.InventarioSQLiteDAO;
import tescha.inventario.dto.EquipoDTO;
import tescha.inventario.service.InventarioService;
import tescha.inventario.view.InventarioView;
import tescha.users.controller.UserController;
import tescha.users.service.UserService;
import tescha.users.dao.*;
import tescha.users.view.UserListView;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainWindow {
    private Stage stage;
    private String username;
    private String role;
    private Image avatar;
    private String imagePath;

    private BorderPane root;
    private StackPane contentArea;
    private JFXDrawer drawer;
    private boolean isDrawerOpen = true;
    private VBox userProfileSection;

    // Paleta de colores moderna
    private final String PRIMARY_COLOR    = "#1976D2";
    private final String SECONDARY_COLOR  = "#E91E63";
    private final String BACKGROUND_COLOR = "#ECEFF1";
    private final String CARD_COLOR       = "#FFFFFF";
    private final String TEXT_PRIMARY     = "#212121";
    private final String TEXT_SECONDARY   = "#455A64";
    private final String SUCCESS_COLOR    = "#388E3C";
    private final String WARNING_COLOR    = "#F57C00";
    private final String ERROR_COLOR      = "#D32F2F";
    private final String INFO_COLOR       = "#0288D1";
    private final String CARD_COLOR_DARKER = "#212121";

    // Botones del menú
    private JFXButton dashboardButton;
    private JFXButton inventoryButton;
    private JFXButton departamentosButton;
    private JFXButton loansButton;
    private JFXButton usersButton;
    private JFXButton reportsButton;
    private JFXButton settingsButton;
    private JFXButton helpButton;

    public MainWindow(String username, String role, Image avatar) {
        this.username = username;
        this.role = role;
        this.avatar = avatar;
        stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        setupUI();
    }
    private void setupUI() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f8f9fa, #e9ecef, #dee2e6);");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));

        // Barra superior con efecto de elevación
        HBox topBar = createTopBar();
        JFXDepthManager.setDepth(topBar, 2);
        root.setTop(topBar);

        // Sidebar y drawer
        setupDrawerAndSidebar();

        // Área de contenido principal
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-container");
        contentArea.setPadding(new Insets(15));
        contentArea.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        // Vista inicial (Dashboard)
        showDashboard();

        root.setCenter(contentArea);

        // Barra de estado inferior
        root.setBottom(createStatusBar());

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Hacer la ventana arrastrable
        makeDraggable(topBar);

        // Añadir sombra al borde de la ventana
        root.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.5)));

        stage.setScene(scene);
        stage.setTitle("Sistema de Gestión de Inventario y Préstamos - " + username);

        // Configurar actualización automática
        setupAutoRefresh();
    }


    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 15, 5, 15));
        statusBar.setStyle("-fx-background-color: " + CARD_COLOR + ";");

        Label dbStatus = new Label("DB: Conectado");
        dbStatus.setGraphic(new FontIcon(FontAwesomeSolid.DATABASE));

        Label lastUpdate = new Label("Actualizado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusBar.getChildren().addAll(dbStatus, spacer, lastUpdate);
        return statusBar;
    }
    private HBox createTopBar() {
        // Ícono de hamburguesa con animación mejorada
        JFXHamburger hamburger = new JFXHamburger();
        hamburger.getStyleClass().add("hamburger-icon");
        hamburger.setStyle("-fx-cursor: hand;");

        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.setOnMouseClicked(e -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (isDrawerOpen) {
                new SlideOutLeft(drawer).play();
                drawer.close();
            } else {
                drawer.open();
                new SlideInRight(drawer).play();
            }
            isDrawerOpen = !isDrawerOpen;
        });

        // Título de la aplicación con ícono
        FontIcon appIcon = new FontIcon(FontAwesomeSolid.LAYER_GROUP);
        appIcon.setIconSize(24);
        appIcon.setIconColor(Color.web(PRIMARY_COLOR));

        Label appTitle = new Label("Sistema de Gestión");
        appTitle.getStyleClass().add("app-title");
        appTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");

        HBox titleBox = new HBox(10, appIcon, appTitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Fecha actual con ícono
        FontIcon calendarIcon = new FontIcon(FontAwesomeSolid.CALENDAR_ALT);
        calendarIcon.setIconSize(16);
        calendarIcon.setIconColor(Color.web(TEXT_SECONDARY));

        LocalDate today = LocalDate.now();
        Label dateLabel = new Label(today.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        dateLabel.getStyleClass().add("date-label");
        dateLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + ";");

        HBox dateBox = new HBox(8, calendarIcon, dateLabel);
        dateBox.setAlignment(Pos.CENTER);

        // Sección de perfil de usuario
        HBox userProfile = createUserProfile();

        // Controles de ventana
        HBox windowControls = createWindowControls();

        // Construir la barra superior
        HBox leftSection = new HBox(15, hamburger, titleBox);
        leftSection.setAlignment(Pos.CENTER_LEFT);

        HBox centerSection = new HBox(dateBox);
        centerSection.setAlignment(Pos.CENTER);
        HBox.setHgrow(centerSection, Priority.ALWAYS);

        HBox rightSection = new HBox(15, userProfile, windowControls);
        rightSection.setAlignment(Pos.CENTER_RIGHT);

        HBox topBar = new HBox(leftSection, centerSection, rightSection);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setAlignment(Pos.CENTER);
        topBar.getStyleClass().add("top-bar");
        topBar.setStyle("-fx-background-color: " + CARD_COLOR + ";");

        return topBar;
    }

    private HBox createUserProfile() {
        // Avatar del usuario con borde
        StackPane avatarContainer = new StackPane();

        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(40);
        avatarView.setFitHeight(40);

        // Clip circular para el avatar
        Circle clip = new Circle(20, 20, 20);
        avatarView.setClip(clip);

        // Borde para el avatar
        Circle border = new Circle(20, 20, 21);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.web(PRIMARY_COLOR));
        border.setStrokeWidth(2);

        avatarContainer.getChildren().addAll(avatarView, border);

        // Información del usuario con estilo mejorado
        VBox userInfo = new VBox(2);
        Label usernameLabel = new Label(username);
        usernameLabel.getStyleClass().add("username-label");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");

        Label roleLabel = new Label(role);
        roleLabel.getStyleClass().add("role-label");
        roleLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + ";");

        userInfo.getChildren().addAll(usernameLabel, roleLabel);
        userInfo.setAlignment(Pos.CENTER_LEFT);

        // Indicador de estado (online)
        Circle statusIndicator = new Circle(5, Color.web(SUCCESS_COLOR));

        // Perfil de usuario
        HBox userProfile = new HBox(15, userInfo, avatarContainer);
        userProfile.setAlignment(Pos.CENTER);
        userProfile.getStyleClass().add("user-profile");

        // Efecto al pasar el mouse
        userProfile.setOnMouseEntered(e -> {
            userProfile.setStyle("-fx-cursor: hand; -fx-background-color: rgba(0,0,0,0.05); -fx-background-radius: 30;");
            new Pulse(userProfile).play();
        });

        userProfile.setOnMouseExited(e -> {
            userProfile.setStyle("-fx-background-color: transparent;");
        });

        return userProfile;
    }

    private HBox createWindowControls() {
        // Botones de control de ventana modernos con íconos
        JFXButton minimizeBtn = new JFXButton();
        FontIcon minimizeIcon = new FontIcon(FontAwesomeSolid.WINDOW_MINIMIZE);
        minimizeIcon.setIconColor(Color.web(TEXT_SECONDARY));
        minimizeBtn.setGraphic(minimizeIcon);
        minimizeBtn.getStyleClass().add("window-control-button");
        minimizeBtn.setOnAction(e -> stage.setIconified(true));

        JFXButton maximizeBtn = new JFXButton();
        FontIcon maximizeIcon = new FontIcon(FontAwesomeSolid.WINDOW_MAXIMIZE);
        maximizeIcon.setIconColor(Color.web(TEXT_SECONDARY));
        maximizeBtn.setGraphic(maximizeIcon);
        maximizeBtn.getStyleClass().add("window-control-button");
        maximizeBtn.setOnAction(e -> {
            if (stage.isMaximized()) {
                stage.setMaximized(false);
                maximizeIcon.setIconCode(FontAwesomeSolid.WINDOW_MAXIMIZE);
            } else {
                stage.setMaximized(true);
                maximizeIcon.setIconCode(FontAwesomeSolid.WINDOW_RESTORE);
            }
        });

        JFXButton closeBtn = new JFXButton();
        FontIcon closeIcon = new FontIcon(FontAwesomeSolid.TIMES);
        closeIcon.setIconColor(Color.web(ERROR_COLOR));
        closeBtn.setGraphic(closeIcon);
        closeBtn.getStyleClass().add("window-control-button");
        closeBtn.getStyleClass().add("close-button");
        closeBtn.setOnAction(e -> {
            new FadeOut(root).setSpeed(2).play();
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setOnFinished(event -> stage.close());
            fadeOut.play();
        });

        // Aplicar efectos hover
        applyHoverEffect(minimizeBtn);
        applyHoverEffect(maximizeBtn);
        applyHoverEffect(closeBtn, ERROR_COLOR);

        HBox windowControls = new HBox(8, minimizeBtn, maximizeBtn, closeBtn);
        windowControls.setAlignment(Pos.CENTER);
        windowControls.getStyleClass().add("window-controls");

        return windowControls;
    }

    private void applyHoverEffect(JFXButton button) {
        applyHoverEffect(button, PRIMARY_COLOR);
    }

    private void applyHoverEffect(JFXButton button, String hoverColor) {
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: rgba(0,0,0,0.1); -fx-background-radius: 30;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: transparent;");
        });
    }

    private void setupDrawerAndSidebar() {
        drawer = new JFXDrawer();
        drawer.setDefaultDrawerSize(260);
        drawer.setDirection(JFXDrawer.DrawerDirection.LEFT);
        drawer.setResizeContent(true);
        drawer.setOverLayVisible(false);
        drawer.setResizableOnDrag(false);

        // Contenido del sidebar con diseño mejorado
        VBox sidebar = createSidebar();
        JFXDepthManager.setDepth(sidebar, 1);
        drawer.setSidePane(sidebar);

        // Configurar el drawer para que el contenido principal ocupe el espacio al colapsar
        drawer.setOnDrawerOpening(e -> {
            root.setLeft(drawer);
            contentArea.getStyleClass().remove("content-expanded");
        });

        drawer.setOnDrawerClosed(e -> {
            root.setLeft(null);
            contentArea.getStyleClass().add("content-expanded");
        });

        // Añadir a la raíz
        root.setLeft(drawer);
    }

    private VBox createUserProfileSection() {
        // Fondo con gradiente para la sección de perfil
        VBox container = new VBox();
        container.setStyle("-fx-background-color: linear-gradient(to bottom right, " + PRIMARY_COLOR + ", #7986cb);");
        container.setPadding(new Insets(25, 15, 25, 15));

        // Avatar con borde destacado
        StackPane avatarContainer = new StackPane();
        avatarContainer.setPadding(new Insets(5));

        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(90);
        avatarView.setFitHeight(90);
        avatarView.setPreserveRatio(true);

        Circle clip = new Circle(45, 45, 45);
        avatarView.setClip(clip);

        // Borde para el avatar
        Circle border = new Circle(45, 45, 46);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.WHITE);
        border.setStrokeWidth(3);

        avatarContainer.getChildren().addAll(avatarView, border);

        // Efecto de sombra para el avatar
        avatarContainer.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.4)));

        // Información del usuario con estilo mejorado
        Label usernameLabel = new Label(username);
        usernameLabel.getStyleClass().add("sidebar-username");
        usernameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label roleLabel = new Label(role);
        roleLabel.getStyleClass().add("sidebar-role");
        roleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: rgba(255,255,255,0.8);");

        // Indicador de estado (online)
        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER);

        Circle statusIndicator = new Circle(5, Color.web("#4caf50"));
        Label statusLabel = new Label("En línea");
        statusLabel.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 12px;");

        statusBox.getChildren().addAll(statusIndicator, statusLabel);

        VBox userInfo = new VBox(8, avatarContainer, usernameLabel, roleLabel, statusBox);
        userInfo.setAlignment(Pos.CENTER);
        userInfo.getStyleClass().add("user-profile-section");

        container.getChildren().add(userInfo);
        return container;
    }

    private VBox createSidebar() {
        // Sección de perfil de usuario mejorada
        userProfileSection = createUserProfileSection();

        // Crear botones del menú con íconos
        dashboardButton = createMenuButton("Dashboard", new FontIcon(FontAwesomeSolid.TACHOMETER_ALT));
        inventoryButton = createMenuButton("Inventario", new FontIcon(FontAwesomeSolid.BOX));
        loansButton = createMenuButton("Préstamos", new FontIcon(FontAwesomeSolid.HAND_HOLDING));
        usersButton = createMenuButton("Usuarios", new FontIcon(FontAwesomeSolid.USERS));
        departamentosButton = createMenuButton("Departamentos", new FontIcon(FontAwesomeSolid.BUILDING));
        reportsButton = createMenuButton("Reportes", new FontIcon(FontAwesomeSolid.CHART_BAR));
        settingsButton = createMenuButton("Configuración", new FontIcon(FontAwesomeSolid.COG));
        helpButton = createMenuButton("Ayuda", new FontIcon(FontAwesomeSolid.QUESTION_CIRCLE));

        // Establecer dashboard como activo por defecto
        dashboardButton.getStyleClass().add("active-menu-button");

        // Manejadores de eventos
        dashboardButton.setOnAction(e -> {
            setActiveButton(dashboardButton);
            showDashboard();
        });

        inventoryButton.setOnAction(e -> {
            setActiveButton(inventoryButton);
            try {
                Connection connection = DatabaseManager.connect();
                InventarioDAO inventarioDAO = new InventarioSQLiteDAO(connection);
                InventarioService inventarioService = new InventarioService(inventarioDAO);
                InventarioController inventarioController = new InventarioController(inventarioService);
                InventarioView inventarioView = new InventarioView(inventarioController);

                setContent(inventarioView.getView());
            } catch (SQLException ex) {
                System.err.println("No se pudo conectar a la base de datos: " + ex.getMessage());
            }
        });

        loansButton.setOnAction(e -> {
            setActiveButton(loansButton);
            showPlaceholder("Módulo de Préstamos");
        });


        // Configurar visibilidad según el rol
        if (!"admin".equalsIgnoreCase(role)) {
            // Ocultar módulos sensibles para empleados
            usersButton.setVisible(false);
            usersButton.setManaged(false);
            reportsButton.setVisible(false);
            reportsButton.setManaged(false);
            departamentosButton.setManaged(false);
            departamentosButton.setVisible(false);
            settingsButton.setVisible(false);
            settingsButton.setManaged(false);
        } else {
            // Configurar acciones para admin
            usersButton.setOnAction(e -> checkAdminAccess(() -> {
                setActiveButton(usersButton);
                try {
                    Connection connection = DatabaseManager.connect();
                    UserDAO userDAO = new UserSQLiteDAO(connection);
                    UserService userService = new UserService(userDAO);
                    UserController userController = new UserController(userService);
                    UserListView userListView = new UserListView(userController);
                    setContent(userListView);

                } catch (SQLException ex) {
                    System.err.println("Error de conexión: " + ex.getMessage());
                    AlertUtils.showError("Error", "No se pudo conectar a la base de datos");
                } catch (Exception ex) {
                    System.err.println("Error al cargar módulo: " + ex.getMessage());
                    AlertUtils.showError("Error", "No se pudo cargar el módulo de usuarios");
                }
            }));
            departamentosButton.setOnAction(e -> {
                setActiveButton(departamentosButton);
                try {
                    Connection connection = DatabaseManager.connect();
                    DepartamentoDAO departamentoDAO = new DepartamentoSQLiteDAO(connection);
                    DepartamentoService departamentoService = new DepartamentoService(departamentoDAO);
                    DepartamentoController departamentoController = new DepartamentoController(departamentoService);
                    DepartamentoView departamentoView = new DepartamentoView(departamentoController);

                    setContent(departamentoView.getView());
                } catch (SQLException ex) {
                    System.err.println("No se pudo conectar a la base de datos: " + ex.getMessage());
                }
            });

            reportsButton.setOnAction(e -> checkAdminAccess(() -> {
                setActiveButton(reportsButton);
                showPlaceholder("Módulo de Reportes");
            }));

            settingsButton.setOnAction(e -> checkAdminAccess(() -> {
                setActiveButton(settingsButton);
                try {
                    Connection connection = DatabaseManager.connect();
                    ConfiguracionDAO configuracionDAO = new ConfiguracionSQLiteDAO(connection);
                    ConfiguracionService configuracionService = new ConfiguracionService(configuracionDAO);
                    ConfiguracionController configuracionController = new ConfiguracionController(configuracionService);
                    ConfiguracionView configuracionView = new ConfiguracionView(configuracionController);

                    setContent(configuracionView.getView());
                } catch (SQLException ex) {
                    System.err.println("No se pudo conectar a la base de datos: " + ex.getMessage());
                }
            }));
        }

        helpButton.setOnAction(e -> {
            setActiveButton(helpButton);
            showPlaceholder("Módulo de Ayuda");
        });

        // Botón de cerrar sesión mejorado
        FontIcon logoutIcon = new FontIcon(FontAwesomeSolid.SIGN_OUT_ALT);
        logoutIcon.setIconColor(Color.WHITE);
        JFXButton logoutButton = new JFXButton("Cerrar Sesión", logoutIcon);
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setButtonType(JFXButton.ButtonType.RAISED);
        logoutButton.setStyle("-fx-background-color: " + ERROR_COLOR + "; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setPadding(new Insets(12, 15, 12, 15)); // Más padding

        logoutButton.setOnAction(e -> {
            new FadeOut(root).setSpeed(2).play();
            stage.close();
        });

        // Contenedor de botones del menú con más espaciado
        VBox menuButtons = new VBox(8, // Aumentar espaciado entre botones
                dashboardButton,
                inventoryButton,
                loansButton
        );

        // Solo añadir estos botones si es admin
        if ("admin".equalsIgnoreCase(role)) {
            menuButtons.getChildren().addAll(
                    usersButton,
                    departamentosButton,
                    reportsButton,
                    settingsButton
            );
        }

        // Añadir botones comunes
        menuButtons.getChildren().addAll(
                helpButton
        );

        menuButtons.setPadding(new Insets(20, 15, 20, 15)); // Más padding general

        // Información de versión con estilo
        HBox versionBox = new HBox(8);
        versionBox.setAlignment(Pos.CENTER);

        FontIcon infoIcon = new FontIcon(FontAwesomeSolid.INFO_CIRCLE);
        infoIcon.setIconColor(Color.web(TEXT_SECONDARY));
        infoIcon.setIconSize(12);

        Label versionLabel = new Label("v1.0.1 ALPHA");
        versionLabel.getStyleClass().add("version-label");
        versionLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px;");

        versionBox.getChildren().addAll(infoIcon, versionLabel);
        versionBox.setPadding(new Insets(10, 0, 10, 0));

        // Layout completo del sidebar
        VBox sidebar = new VBox(0,
                userProfileSection,
                menuButtons,
                new Region(), // Espacio flexible
                versionBox,
                logoutButton
        );

        sidebar.getStyleClass().add("sidebar");
        sidebar.setStyle("-fx-background-color: " + CARD_COLOR_DARKER + ";"); // Color más oscuro para mejor contraste
        sidebar.setMinWidth(220); // Ancho mínimo mayor
        sidebar.setPrefWidth(220);
        // Hacer que los botones del menú ocupen el espacio disponible
        VBox.setVgrow(menuButtons, Priority.ALWAYS);

        return sidebar;
    }

    private void checkAdminAccess(Runnable action) {
        if ("admin".equalsIgnoreCase(role)) {
            action.run();
        } else {
            showAccessDeniedMessage();
        }
    }

    private void showAccessDeniedMessage() {
        VBox messageBox = new VBox(15);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(50));

        FontIcon accessDeniedIcon = new FontIcon(FontAwesomeSolid.LOCK);
        accessDeniedIcon.setIconSize(64);
        accessDeniedIcon.setIconColor(Color.web(ERROR_COLOR));

        Label title = new Label("Acceso denegado");
        title.getStyleClass().add("access-denied-title");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + ERROR_COLOR + ";");

        Label message = new Label("No tienes permisos para acceder a este módulo");
        message.getStyleClass().add("access-denied-message");
        message.setStyle("-fx-font-size: 16px; -fx-text-fill: " + TEXT_SECONDARY + ";");

        messageBox.getChildren().addAll(accessDeniedIcon, title, message);

        // Aplicar animación
        setContent(messageBox);
        new Shake(messageBox).play();
    }

    private JFXButton createMenuButton(String text, FontIcon icon) {
        icon.setIconSize(20); // Íconos más grandes
        icon.setIconColor(Color.web(TEXT_PRIMARY)); // Color más oscuro para mejor contraste

        JFXButton button = new JFXButton(text, icon);
        button.getStyleClass().add("menu-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(15, 20, 15, 20)); // Más padding
        button.setGraphicTextGap(20); // Más espacio entre ícono y texto
        button.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;"); // Texto más grande y en negrita

        // Efecto ripple materializado
        button.setRipplerFill(Color.web(PRIMARY_COLOR, 0.4)); // Efecto más visible

        // Efectos de hover más pronunciados
        button.setOnMouseEntered(e -> {
            if (!button.getStyleClass().contains("active-menu-button")) {
                button.setStyle("-fx-background-color: rgba(0,0,0,0.1); -fx-font-size: 14px; -fx-font-weight: bold;");
                icon.setIconColor(Color.web(PRIMARY_COLOR)); // Cambiar color del ícono al hacer hover
            }
        });

        button.setOnMouseExited(e -> {
            if (!button.getStyleClass().contains("active-menu-button")) {
                button.setStyle("-fx-background-color: transparent; -fx-font-size: 14px; -fx-font-weight: bold;");
                icon.setIconColor(Color.web(TEXT_PRIMARY)); // Restaurar color del ícono
            }
        });

        return button;
    }
    private void setupAutoRefresh() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(5), e -> refreshData()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void refreshData() {
        if (dashboardButton.getStyleClass().contains("active-menu-button")) {
            showDashboard(); // Recarga los datos
        }
    }
    private void setActiveButton(JFXButton activeButton) {
        // Remover clase activa y restaurar color de icono en todos los botones
        for (JFXButton button : new JFXButton[]{dashboardButton, inventoryButton, loansButton,
                usersButton,departamentosButton, reportsButton, settingsButton, helpButton}) {
            if (button != null) {
                button.getStyleClass().remove("active-menu-button");
                button.setStyle("-fx-background-color: transparent;");

                // Restaurar color de icono
                if (button.getGraphic() instanceof FontIcon) {
                    ((FontIcon) button.getGraphic()).setIconColor(Color.web(TEXT_SECONDARY));
                }
            }
        }

        // Añadir clase activa y cambiar color de icono al botón seleccionado
        activeButton.getStyleClass().add("active-menu-button");
        activeButton.setStyle("-fx-background-color: rgba(63, 81, 181, 0.2); -fx-text-fill: " + PRIMARY_COLOR + ";");

        // Cambiar color del icono
        if (activeButton.getGraphic() instanceof FontIcon) {
            ((FontIcon) activeButton.getGraphic()).setIconColor(Color.web(PRIMARY_COLOR));
        }

        // Aplicar animación al botón activo
        new Pulse(activeButton).play();
    }

    private void showDashboard() {
        JFXScrollPane scrollPane = new JFXScrollPane();
        scrollPane.getStyleClass().add("dashboard-scroll");

        VBox dashboardContent = new VBox(25);
        dashboardContent.setPadding(new Insets(30));
        dashboardContent.getStyleClass().add("dashboard-content");

        // Mensaje de bienvenida con ícono
        HBox welcomeBox = new HBox(15);
        welcomeBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon welcomeIcon = new FontIcon(FontAwesomeSolid.HOME);
        welcomeIcon.setIconSize(32);
        welcomeIcon.setIconColor(Color.web(PRIMARY_COLOR));

        Label welcomeLabel = new Label("Bienvenido al Sistema de Gestión");
        welcomeLabel.getStyleClass().add("welcome-label");
        welcomeLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");

        welcomeBox.getChildren().addAll(welcomeIcon, welcomeLabel);

        // Estadísticas rápidas con diseño moderno
        HBox quickStats = createQuickStats();

        // Gráficos estadísticos
        Node inventoryChart = createInventoryChart();

        // Añadir contenido al dashboard
        dashboardContent.getChildren().addAll(
                welcomeBox,
                quickStats,
                inventoryChart,
                createRecentActivitiesSection()
        );

        scrollPane.setContent(dashboardContent);
        setContent(scrollPane);

        // Animación de entrada
        new FadeInUp(dashboardContent).play();
    }


    private HBox createQuickStats() {
        try (Connection conn = DatabaseManager.connect()) {
            InventarioDAO inventarioDAO = new InventarioSQLiteDAO(conn);
            UserDAO userDAO = new UserSQLiteDAO(conn);

            int totalItems = inventarioDAO.obtenerTodosLosEquipos().size();
            int activeLoans = 0; // Implementar en PrestamoDAO
            int overdueLoans = 0; // Implementar en PrestamoDAO
            int totalUsers = userDAO.getAllUsers().size();

            VBox inventoryStats = createStatCard(
                    "Items en Inventario",
                    String.valueOf(totalItems),
                    INFO_COLOR,
                    new FontIcon(FontAwesomeSolid.BOXES),
                    () -> inventoryButton.fire()
            );

            VBox activeLoansCard = createStatCard(
                    "Préstamos Activos",
                    String.valueOf(activeLoans),
                    SUCCESS_COLOR,
                    new FontIcon(FontAwesomeSolid.CLIPBOARD_CHECK),
                    () -> loansButton.fire()
            );

            VBox overdueLoansCard = createStatCard(
                    "Préstamos Vencidos",
                    String.valueOf(overdueLoans),
                    ERROR_COLOR,
                    new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE),
                    () -> loansButton.fire() // Puedes cambiar esto si tienes botón específico para vencidos
            );

            VBox usersCount = createStatCard(
                    "Usuarios Registrados",
                    String.valueOf(totalUsers),
                    PRIMARY_COLOR,
                    new FontIcon(FontAwesomeSolid.USER_FRIENDS),
                    () -> usersButton.fire()
            );

            return new HBox(20, inventoryStats, activeLoansCard, overdueLoansCard, usersCount);
        } catch (SQLException e) {
            AlertUtils.showError("Error", "No se pudieron cargar las estadísticas");
            return new HBox();
        }
    }

    private VBox createStatCard(String title, String value, String color, FontIcon icon, Runnable onClickAction) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("stat-card");
        card.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 8px;");
        JFXDepthManager.setDepth(card, 1);

        icon.setIconSize(28);
        icon.setIconColor(Color.web(color));

        StackPane iconContainer = new StackPane(icon);
        iconContainer.setStyle("-fx-background-color: " + color + "22; -fx-background-radius: 50%;");
        iconContainer.setPrefSize(50, 50);

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        valueLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_SECONDARY + ";");

        card.getChildren().addAll(iconContainer, valueLabel, titleLabel);
        card.setAlignment(Pos.CENTER);

        // Hover animado
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 8px; -fx-cursor: hand;");
            new Pulse(card).play();
            JFXDepthManager.setDepth(card, 2);
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 8px;");
            JFXDepthManager.setDepth(card, 1);
        });

        // Acción al hacer clic
        card.setOnMouseClicked(e -> onClickAction.run());

        return card;
    }

    private Node createInventoryChart() {
        try (Connection conn = DatabaseManager.connect()) {
            InventarioDAO inventarioDAO = new InventarioSQLiteDAO(conn);
            List<EquipoDTO> equipos = inventarioDAO.obtenerTodosLosEquipos();

            // Agrupar por categoría
            Map<String, Long> countsByCategory = equipos.stream()
                    .collect(Collectors.groupingBy(EquipoDTO::getCategoria, Collectors.counting()));

            PieChart pieChart = new PieChart();
            countsByCategory.forEach((category, count) -> {
                PieChart.Data slice = new PieChart.Data(category + " (" + count + ")", count);
                pieChart.getData().add(slice);
            });

            pieChart.setTitle("Distribución de Inventario por Categoría");
            pieChart.setLegendVisible(true);
            pieChart.setLabelsVisible(true);
            pieChart.setStyle("-fx-font-size: 12px;");

            // Aplicar colores personalizados
            applyCustomColors(pieChart);

            VBox chartContainer = new VBox(pieChart);
            chartContainer.setPadding(new Insets(20));
            chartContainer.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 8px;");
            JFXDepthManager.setDepth(chartContainer, 1);

            return chartContainer;
        } catch (SQLException e) {
            Label errorLabel = new Label("Error al cargar gráfico de inventario");
            errorLabel.setStyle("-fx-text-fill: " + ERROR_COLOR + ";");
            return errorLabel;
        }
    }

    private void applyCustomColors(PieChart pieChart) {
        int i = 0;
        String[] colors = {PRIMARY_COLOR, SECONDARY_COLOR, SUCCESS_COLOR, WARNING_COLOR, INFO_COLOR};

        for (PieChart.Data data : pieChart.getData()) {
            String color = colors[i % colors.length];
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            i++;
        }
    }

    private VBox createRecentActivitiesSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20, 0, 0, 0));

        // Título de la sección
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        FontIcon activityIcon = new FontIcon(FontAwesomeSolid.HISTORY);
        activityIcon.setIconSize(18);
        activityIcon.setIconColor(Color.web(PRIMARY_COLOR));

        Label headerLabel = new Label("Actividad Reciente");
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");

        headerBox.getChildren().addAll(activityIcon, headerLabel);

        // Panel de actividades
        VBox activitiesPanel = new VBox(0);
        activitiesPanel.setStyle("-fx-background-color: " + CARD_COLOR + "; -fx-background-radius: 8px;");
        JFXDepthManager.setDepth(activitiesPanel, 1);

        // Obtener actividades recientes de la base de datos
        try (Connection conn = DatabaseManager.connect()) {
            InventarioDAO inventarioDAO = new InventarioSQLiteDAO(conn);
            List<String> historial = inventarioDAO.obtenerHistorialReciente(5);

            if (historial.isEmpty()) {
                Label emptyLabel = new Label("No hay actividad reciente");
                emptyLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-style: italic;");
                activitiesPanel.getChildren().add(emptyLabel);
            } else {
                for (String actividad : historial) {
                    activitiesPanel.getChildren().addAll(
                            createActivityItem(actividad),
                            createSeparator()
                    );
                }
                // Remover el último separador
                activitiesPanel.getChildren().remove(activitiesPanel.getChildren().size()-1);
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Error al cargar actividades recientes");
            errorLabel.setStyle("-fx-text-fill: " + ERROR_COLOR + ";");
            activitiesPanel.getChildren().add(errorLabel);
        }

        // Botón para ver más actividades
        JFXButton viewMoreButton = new JFXButton("Ver todas las actividades");
        viewMoreButton.setButtonType(JFXButton.ButtonType.FLAT);
        viewMoreButton.setTextFill(Color.web(PRIMARY_COLOR));
        viewMoreButton.setPadding(new Insets(10, 0, 10, 0));
        viewMoreButton.setMaxWidth(Double.MAX_VALUE);
        viewMoreButton.setStyle("-fx-font-size: 14px;");

        FontIcon arrowIcon = new FontIcon(FontAwesomeSolid.ARROW_RIGHT);
        arrowIcon.setIconColor(Color.web(PRIMARY_COLOR));
        viewMoreButton.setGraphic(arrowIcon);
        viewMoreButton.setGraphicTextGap(10);
        viewMoreButton.setAlignment(Pos.CENTER);

        section.getChildren().addAll(headerBox, activitiesPanel, viewMoreButton);
        return section;
    }

    private HBox createActivityItem(String actividad) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(15, 20, 15, 20));
        item.setAlignment(Pos.CENTER_LEFT);

        // Parsear la actividad (formato: [fecha hora] tipo - usuario: descripción)
        String[] parts = actividad.split(" - |: ");
        String fechaHora = parts.length > 0 ? parts[0] : "";
        String tipoUsuario = parts.length > 1 ? parts[1] : "";
        String descripcion = parts.length > 2 ? parts[2] : actividad;

        FontIcon icon = getIconForActivityType(tipoUsuario);
        String iconColor = getColorForActivityType(tipoUsuario);

        icon.setIconSize(16);
        icon.setIconColor(Color.web(iconColor));

        StackPane iconContainer = new StackPane(icon);
        iconContainer.setStyle("-fx-background-color: " + iconColor + "22; -fx-background-radius: 50%;");
        iconContainer.setPrefSize(35, 35);

        // Información de la actividad
        VBox infoBox = new VBox(3);
        Label actionLabel = new Label(descripcion);
        actionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");

        Label detailsLabel = new Label(fechaHora);
        detailsLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + ";");

        infoBox.getChildren().addAll(actionLabel, detailsLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        item.getChildren().addAll(iconContainer, infoBox);

        // Efecto hover
        item.setOnMouseEntered(e -> {
            item.setStyle("-fx-background-color: rgba(0,0,0,0.03); -fx-cursor: hand;");
        });

        item.setOnMouseExited(e -> {
            item.setStyle("-fx-background-color: transparent;");
        });

        return item;
    }

    private FontIcon getIconForActivityType(String tipo) {
        if (tipo.contains("CREACION")) return new FontIcon(FontAwesomeSolid.PLUS_CIRCLE);
        if (tipo.contains("ACTUALIZACION")) return new FontIcon(FontAwesomeSolid.EDIT);
        if (tipo.contains("ELIMINACION")) return new FontIcon(FontAwesomeSolid.TRASH);
        if (tipo.contains("PRESTAMO")) return new FontIcon(FontAwesomeSolid.HAND_HOLDING);
        return new FontIcon(FontAwesomeSolid.INFO_CIRCLE);
    }

    private String getColorForActivityType(String tipo) {
        if (tipo.contains("CREACION")) return SUCCESS_COLOR;
        if (tipo.contains("ACTUALIZACION")) return INFO_COLOR;
        if (tipo.contains("ELIMINACION")) return ERROR_COLOR;
        if (tipo.contains("PRESTAMO")) return PRIMARY_COLOR;
        return TEXT_SECONDARY;
    }

    private HBox createActivityItem(String action, String details, String time, FontIcon icon, String iconColor) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(15, 20, 15, 20));
        item.setAlignment(Pos.CENTER_LEFT);

        // Usa directamente el FontIcon que te pasan:
        icon.setIconSize(16);
        icon.setIconColor(Color.web(iconColor));

        StackPane iconContainer = new StackPane(icon);
        iconContainer.setStyle("-fx-background-color: " + iconColor + "22; -fx-background-radius: 50%;");
        iconContainer.setPrefSize(35, 35);

        // Información de la actividad
        VBox infoBox = new VBox(3);
        Label actionLabel = new Label(action);
        actionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");

        Label detailsLabel = new Label(details);
        detailsLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + ";");

        infoBox.getChildren().addAll(actionLabel, detailsLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        // Indicador de tiempo
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px;");

        item.getChildren().addAll(iconContainer, infoBox, timeLabel);

        // Efecto hover
        item.setOnMouseEntered(e -> {
            item.setStyle("-fx-background-color: rgba(0,0,0,0.03); -fx-cursor: hand;");
        });

        item.setOnMouseExited(e -> {
            item.setStyle("-fx-background-color: transparent;");
        });

        return item;
    }

    private Pane createSeparator() {
        Pane separator = new Pane();
        separator.setPrefHeight(1);
        separator.setMaxWidth(Double.MAX_VALUE);
        separator.setStyle("-fx-background-color: #EEEEEE;");
        return separator;
    }

    private void showPlaceholder(String moduleName) {
        VBox placeholder = new VBox();
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setSpacing(20);
        placeholder.setPadding(new Insets(50));

        // Icono representativo
        FontIcon icon = new FontIcon(FontAwesomeSolid.CODE);
        icon.setIconSize(80);
        icon.setIconColor(Color.web(PRIMARY_COLOR, 0.7));

        StackPane iconContainer = new StackPane(icon);
        iconContainer.setStyle("-fx-background-color: " + PRIMARY_COLOR + "22; -fx-background-radius: 50%;");
        iconContainer.setPrefSize(150, 150);

        Label titleLabel = new Label(moduleName);
        titleLabel.getStyleClass().add("placeholder-title");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");

        Label messageLabel = new Label("Este módulo está actualmente en desarrollo");
        messageLabel.getStyleClass().add("placeholder-message");
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + TEXT_SECONDARY + ";");

        // Botón de retorno
        JFXButton backButton = new JFXButton("Volver al Dashboard");
        backButton.setButtonType(JFXButton.ButtonType.RAISED);
        backButton.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white;");
        backButton.setPadding(new Insets(10, 20, 10, 20));
        backButton.setOnAction(e -> {
            setActiveButton(dashboardButton);
            showDashboard();
        });

        placeholder.getChildren().addAll(iconContainer, titleLabel, messageLabel, backButton);
        setContent(placeholder);

        // Animación
        new BounceIn(placeholder).play();
    }

    private void setContent(Node content) {
        // Limpiar contenido actual
        contentArea.getChildren().clear();

        // Contenedor para el contenido con padding
        StackPane contentContainer = new StackPane(content);
        contentContainer.setPadding(new Insets(5));

        // Añadir nuevo contenido
        contentArea.getChildren().add(contentContainer);

        // Aplicar animación de fade-in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void makeDraggable(Node node) {
        final double[] xOffset = {0};
        final double[] yOffset = {0};

        node.setOnMousePressed(event -> {
            xOffset[0] = event.getSceneX();
            yOffset[0] = event.getSceneY();
        });

        node.setOnMouseDragged(event -> {
            if (!stage.isMaximized()) {
                stage.setX(event.getScreenX() - xOffset[0]);
                stage.setY(event.getScreenY() - yOffset[0]);
            }
        });

        // Doble clic para maximizar/restaurar
        node.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (stage.isMaximized()) {
                    stage.setMaximized(false);
                } else {
                    stage.setMaximized(true);
                }
            }
        });
    }

    // Método para obtener la ruta de la imagen (para que otras clases puedan usarla)
    public String getImagePath() {
        return imagePath;
    }

    // Método para obtener la imagen (para que otras clases puedan usarla)
    public Image getAvatar() {
        return avatar;
    }

    public void show() {
        stage.show();

        // Abrir el drawer por defecto
        drawer.open();

        // Aplicar animaciones de entrada
        new FadeIn(root).setSpeed(1.5).play();

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), root);
        slideIn.setFromY(20);
        slideIn.setToY(0);

        ParallelTransition parallelTransition = new ParallelTransition(slideIn);
        parallelTransition.play();
    }
}