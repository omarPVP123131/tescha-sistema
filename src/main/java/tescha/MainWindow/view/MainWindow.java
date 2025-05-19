package tescha.MainWindow.view;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import tescha.inventario.controller.InventarioController;
import tescha.inventario.dao.InventarioDAO;
import tescha.inventario.dao.InventarioSQLiteDAO;
import tescha.inventario.service.InventarioService;
import tescha.inventario.view.InventarioView;
import java.sql.Connection;
import java.sql.SQLException;
import tescha.users.controller.UserController;
import tescha.users.service.UserService;
import tescha.users.dao.*;
import tescha.users.view.UserListView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

    // Botones del menú
    private JFXButton dashboardButton;
    private JFXButton inventoryButton;
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
        stage.initStyle(StageStyle.UNDECORATED);
        setupUI();
    }

    private void setupUI() {
        root = new BorderPane();
        root.getStyleClass().add("main-container");

        // Barra superior
        HBox topBar = createTopBar();
        root.setTop(topBar);

        // Sidebar y drawer
        setupDrawerAndSidebar();

        // Área de contenido principal
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-container");
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        // Vista inicial (Dashboard)
        showDashboard();

        root.setCenter(contentArea);

        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Hacer la ventana arrastrable
        makeDraggable(topBar);

        stage.setScene(scene);
        stage.setTitle("Sistema de Gestión de Inventario y Préstamos - " + username);
    }

    private HBox createTopBar() {
        // Ícono de hamburguesa para alternar el sidebar
        JFXHamburger hamburger = new JFXHamburger();
        hamburger.getStyleClass().add("hamburger-icon");

        HamburgerBackArrowBasicTransition transition = new HamburgerBackArrowBasicTransition(hamburger);
        transition.setRate(-1);
        hamburger.setOnMouseClicked(e -> {
            transition.setRate(transition.getRate() * -1);
            transition.play();

            if (isDrawerOpen) {
                drawer.close();
            } else {
                drawer.open();
            }
            isDrawerOpen = !isDrawerOpen;
        });

        // Título de la aplicación
        Label appTitle = new Label("Sistema de Gestión");
        appTitle.getStyleClass().add("app-title");

        // Fecha actual
        LocalDate today = LocalDate.now();
        Label dateLabel = new Label(today.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")));
        dateLabel.getStyleClass().add("date-label");

        // Sección de perfil de usuario
        HBox userProfile = createUserProfile();

        // Controles de ventana (minimizar, maximizar, cerrar)
        HBox windowControls = createWindowControls();

        // Construir la barra superior
        HBox leftSection = new HBox(15, hamburger, appTitle);
        leftSection.setAlignment(Pos.CENTER_LEFT);

        HBox centerSection = new HBox(dateLabel);
        centerSection.setAlignment(Pos.CENTER);
        HBox.setHgrow(centerSection, Priority.ALWAYS);

        HBox rightSection = new HBox(15, userProfile, windowControls);
        rightSection.setAlignment(Pos.CENTER_RIGHT);

        HBox topBar = new HBox(leftSection, centerSection, rightSection);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setAlignment(Pos.CENTER);
        topBar.getStyleClass().add("top-bar");

        return topBar;
    }

    private HBox createUserProfile() {
        // Avatar del usuario
        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(40);
        avatarView.setFitHeight(40);

        // Clip circular para el avatar
        Circle clip = new Circle(20, 20, 20);
        avatarView.setClip(clip);

        // Información del usuario
        VBox userInfo = new VBox(2);
        Label usernameLabel = new Label(username);
        usernameLabel.getStyleClass().add("username-label");
        Label roleLabel = new Label(role);
        roleLabel.getStyleClass().add("role-label");

        userInfo.getChildren().addAll(usernameLabel, roleLabel);
        userInfo.setAlignment(Pos.CENTER_LEFT);

        // Perfil de usuario
        HBox userProfile = new HBox(15, userInfo, avatarView);
        userProfile.setAlignment(Pos.CENTER);
        userProfile.getStyleClass().add("user-profile");

        return userProfile;
    }

    private HBox createWindowControls() {
        // Botón minimizar
        JFXButton minimizeBtn = new JFXButton("-");
        minimizeBtn.getStyleClass().add("window-control-button");
        minimizeBtn.setOnAction(e -> stage.setIconified(true));

        // Botón maximizar/restaurar
        JFXButton maximizeBtn = new JFXButton("□");
        maximizeBtn.getStyleClass().add("window-control-button");
        maximizeBtn.setOnAction(e -> {
            if (stage.isMaximized()) {
                stage.setMaximized(false);
            } else {
                stage.setMaximized(true);
            }
        });

        // Botón cerrar
        JFXButton closeBtn = new JFXButton("×");
        closeBtn.getStyleClass().add("window-control-button");
        closeBtn.getStyleClass().add("close-button");
        closeBtn.setOnAction(e -> stage.close());

        HBox windowControls = new HBox(5, minimizeBtn, maximizeBtn, closeBtn);
        windowControls.setAlignment(Pos.CENTER);
        windowControls.getStyleClass().add("window-controls");

        return windowControls;
    }

    private void setupDrawerAndSidebar() {
        drawer = new JFXDrawer();
        drawer.setDefaultDrawerSize(250);
        drawer.setDirection(JFXDrawer.DrawerDirection.LEFT);
        drawer.setResizeContent(true);
        drawer.setOverLayVisible(false);
        drawer.setResizableOnDrag(false);

        // Contenido del sidebar
        VBox sidebar = createSidebar();
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
        // Avatar con clip circular
        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(80);
        avatarView.setFitHeight(80);
        avatarView.setPreserveRatio(true);

        Circle clip = new Circle(40, 40, 40);
        avatarView.setClip(clip);

        // Información del usuario
        Label usernameLabel = new Label(username);
        usernameLabel.getStyleClass().add("sidebar-username");

        Label roleLabel = new Label(role);
        roleLabel.getStyleClass().add("sidebar-role");

        VBox userInfo = new VBox(5, avatarView, usernameLabel, roleLabel);
        userInfo.setAlignment(Pos.CENTER);
        userInfo.getStyleClass().add("user-profile-section");

        return userInfo;
    }

    private VBox createSidebar() {
        // Sección de perfil de usuario
        userProfileSection = createUserProfileSection();

        // Crear botones del menú
        dashboardButton = createMenuButton("Dashboard", "dashboard-icon");
        inventoryButton = createMenuButton("Inventario", "inventory-icon");
        loansButton = createMenuButton("Préstamos", "loans-icon");
        usersButton = createMenuButton("Usuarios", "users-icon");
        reportsButton = createMenuButton("Reportes", "reports-icon");
        settingsButton = createMenuButton("Configuración", "settings-icon");
        helpButton = createMenuButton("Ayuda", "help-icon");

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

        // Botón de cerrar sesión
        JFXButton logoutButton = createMenuButton("Cerrar Sesión", "logout-icon");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> stage.close());

        // Contenedor de botones del menú
        VBox menuButtons = new VBox(5,
                dashboardButton,
                inventoryButton,
                loansButton
        );

        // Solo añadir estos botones si es admin
        if ("admin".equalsIgnoreCase(role)) {
            menuButtons.getChildren().addAll(
                    usersButton,
                    reportsButton,
                    settingsButton
            );
        }

        // Añadir botones comunes
        menuButtons.getChildren().addAll(
                helpButton
        );

        menuButtons.setPadding(new Insets(10, 15, 10, 15));

        // Información de versión
        Label versionLabel = new Label("v1.0.1 ALPHA");
        versionLabel.getStyleClass().add("version-label");

        VBox versionBox = new VBox(versionLabel);
        versionBox.setAlignment(Pos.CENTER);
        versionBox.setPadding(new Insets(10, 0, 10, 0));

        // Layout completo del sidebar
        VBox sidebar = new VBox(10,
                userProfileSection,
                menuButtons,
                new Region(), // Espacio flexible
                versionBox,
                logoutButton
        );
        sidebar.getStyleClass().add("sidebar");

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
        VBox messageBox = new VBox(10);
        messageBox.setAlignment(Pos.CENTER);

        Label title = new Label("Acceso denegado");
        title.getStyleClass().add("access-denied-title");

        Label message = new Label("No tienes permisos para acceder a este módulo");
        message.getStyleClass().add("access-denied-message");

        messageBox.getChildren().addAll(title, message);
        setContent(messageBox);
    }
    private JFXButton createMenuButton(String text, String iconClass) {
        JFXButton button = new JFXButton(text);
        button.getStyleClass().add("menu-button");
        button.getStyleClass().add(iconClass);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        return button;
    }

    private void setActiveButton(JFXButton activeButton) {
        // Remover clase activa de todos los botones
        dashboardButton.getStyleClass().remove("active-menu-button");
        inventoryButton.getStyleClass().remove("active-menu-button");
        loansButton.getStyleClass().remove("active-menu-button");
        usersButton.getStyleClass().remove("active-menu-button");
        reportsButton.getStyleClass().remove("active-menu-button");
        settingsButton.getStyleClass().remove("active-menu-button");
        helpButton.getStyleClass().remove("active-menu-button");

        // Añadir clase activa al botón seleccionado
        activeButton.getStyleClass().add("active-menu-button");
    }

    private void showDashboard() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("dashboard-scroll");

        VBox dashboardContent = new VBox(20);
        dashboardContent.setPadding(new Insets(20));
        dashboardContent.getStyleClass().add("dashboard-content");

        // Mensaje de bienvenida
        Label welcomeLabel = new Label("Bienvenido al Sistema de Gestión de Inventario y Préstamos");
        welcomeLabel.getStyleClass().add("welcome-label");

        // Estadísticas rápidas (placeholder)
        HBox quickStats = createQuickStats();

        // Añadir contenido al dashboard
        dashboardContent.getChildren().addAll(
                welcomeLabel,
                quickStats
        );

        scrollPane.setContent(dashboardContent);
        setContent(scrollPane);
    }

    private HBox createQuickStats() {
        // Estos son placeholders - se implementarán con datos reales más adelante
        VBox inventoryStats = createStatCard("Items en Inventario", "0", "#3498db");
        VBox activeLoans = createStatCard("Préstamos Activos", "0", "#2ecc71");
        VBox overdueLoans = createStatCard("Préstamos Vencidos", "0", "#e74c3c");
        VBox usersCount = createStatCard("Usuarios Registrados", "0", "#9b59b6");

        HBox statsContainer = new HBox(20, inventoryStats, activeLoans, overdueLoans, usersCount);
        statsContainer.setAlignment(Pos.CENTER);

        // Hacer que las tarjetas se expandan uniformemente
        HBox.setHgrow(inventoryStats, Priority.ALWAYS);
        HBox.setHgrow(activeLoans, Priority.ALWAYS);
        HBox.setHgrow(overdueLoans, Priority.ALWAYS);
        HBox.setHgrow(usersCount, Priority.ALWAYS);

        return statsContainer;
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.getStyleClass().add("stat-card");
        card.setStyle("-fx-border-color: " + color + ";");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stat-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stat-value");
        valueLabel.setStyle("-fx-text-fill: " + color + ";");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private void showPlaceholder(String moduleName) {
        VBox placeholder = new VBox();
        placeholder.setAlignment(Pos.CENTER);
        placeholder.setSpacing(20);

        Label titleLabel = new Label(moduleName);
        titleLabel.getStyleClass().add("placeholder-title");

        Label messageLabel = new Label("Este módulo está en desarrollo");
        messageLabel.getStyleClass().add("placeholder-message");

        placeholder.getChildren().addAll(titleLabel, messageLabel);
        setContent(placeholder);
    }

    private void setContent(Node content) {
        // Limpiar contenido actual
        contentArea.getChildren().clear();

        // Añadir nuevo contenido con animación
        contentArea.getChildren().add(content);

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

        // Aplicar animación de entrada
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), root);
        slideIn.setFromY(20);
        slideIn.setToY(0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        fadeIn.play();
        slideIn.play();
    }
}