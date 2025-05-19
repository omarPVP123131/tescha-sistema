package tescha.users.view;

import animatefx.animation.*;
import com.jfoenix.controls.*;
import com.jfoenix.effects.JFXDepthManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tescha.users.controller.UserController;
import tescha.users.dto.UserDTO;
import tescha.users.view.components.UserCard;
import tescha.users.view.components.UserForm;
import tescha.Components.AlertUtils;

import java.util.List;

public class UserListView extends StackPane {
    private final UserController controller;
    private ObservableList<UserDTO> users;
    private JFXListView<UserDTO> userListView;
    private Label statusLabel;
    private JFXComboBox<String> filterRoleBox;
    private JFXComboBox<String> filterStatusBox;
    private JFXTextField searchField;
    private StackPane contentContainer;

    public UserListView(UserController controller) {
        this.controller = controller;
        initializeUI();
        showLoadingAnimation();

        // Simular carga para mostrar la animación
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(1500),
                ae -> Platform.runLater(this::loadUsers)
        ));
        timeline.play();
    }

    private void initializeUI() {
        // Contenedor principal con efecto de profundidad
        BorderPane mainLayout = new BorderPane();
        mainLayout.getStyleClass().add("user-list-main-container");
        JFXDepthManager.setDepth(mainLayout, 1);

        // Header con título y búsqueda
        HBox headerBox = createHeaderSection();
        mainLayout.setTop(headerBox);

        // Contenedor central para lista y filtros
        VBox centerContent = new VBox(15);
        centerContent.setPadding(new Insets(15));

        // Sección de filtros
        HBox filterBox = createFilterSection();

        // Lista de usuarios con JFoenix
        userListView = new JFXListView<>();
        userListView.setCellFactory(param -> new UserCard(
                this::handleEditUser,
                this::handleToggleStatus,
                this::handleDeleteUser
        ));
        userListView.getStyleClass().add("users-list-view");
        VBox.setVgrow(userListView, Priority.ALWAYS);

        // Efecto de elevación para la lista
        JFXDepthManager.setDepth(userListView, 1);

        // Barra de estado
        statusLabel = new Label("Cargando usuarios...");
        statusLabel.getStyleClass().add("status-label");

        centerContent.getChildren().addAll(filterBox, userListView, statusLabel);
        mainLayout.setCenter(centerContent);

        // Botón de agregar flotante con JFoenix
        JFXButton addButton = createFloatingActionButton();

        // Contenedor para mostrar animación de carga
        contentContainer = new StackPane();
        contentContainer.getChildren().add(mainLayout);

        // Agregar todo al layout principal
        this.getChildren().addAll(contentContainer, addButton);
        StackPane.setAlignment(addButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(addButton, new Insets(0, 25, 25, 0));

        // Agregar estilos
        this.getStylesheets().add(getClass().getResource("/styles/users.css").toExternalForm());

        // Tamaño mínimo para el contenedor
        this.setMinSize(800, 600);
        this.setPadding(new Insets(10));
    }

    private HBox createHeaderSection() {
        // Título con icono
        FontAwesomeIconView userIcon = new FontAwesomeIconView(FontAwesomeIcon.USERS);
        userIcon.setGlyphSize(24);
        userIcon.setFill(Color.valueOf("#3f51b5"));

        Label titleLabel = new Label("Gestión de Usuarios");
        titleLabel.getStyleClass().add("view-title");

        HBox titleBox = new HBox(10, userIcon, titleLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Campo de búsqueda con JFoenix
        searchField = new JFXTextField();
        searchField.setPromptText("Buscar usuarios...");
        searchField.getStyleClass().add("search-field");
        searchField.setPrefWidth(250);

        // Icono de búsqueda
        FontAwesomeIconView searchIcon = new FontAwesomeIconView(FontAwesomeIcon.SEARCH);
        searchIcon.setGlyphSize(16);

        // Botón para limpiar la búsqueda
        JFXButton clearButton = new JFXButton("");
        clearButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.TIMES_CIRCLE));
        clearButton.getStyleClass().add("clear-button");
        clearButton.setOnAction(e -> {
            searchField.clear();
            loadUsers();
        });

        // Efecto al escribir en el campo de búsqueda
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            handleSearch(newVal);
        });

        HBox searchBox = new HBox(5, searchIcon, searchField, clearButton);
        searchBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        HBox headerBox = new HBox(20, titleBox, searchBox);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(searchBox, Priority.ALWAYS);
        headerBox.getStyleClass().add("header-box");
        headerBox.setPadding(new Insets(15, 15, 15, 15));

        // Efecto de elevación para el header
        JFXDepthManager.setDepth(headerBox, 2);

        return headerBox;
    }

    private HBox createFilterSection() {
        // Filtro por rol con JFoenix
        Label roleLabel = new Label("Rol:");
        roleLabel.getStyleClass().add("filter-label");

        filterRoleBox = new JFXComboBox<>();
        filterRoleBox.getItems().addAll("Todos", "Administrador", "Usuario");
        filterRoleBox.setValue("Todos");
        filterRoleBox.getStyleClass().add("filter-combo");
        filterRoleBox.setOnAction(e -> applyFiltersWithAnimation());

        // Filtro por estado con JFoenix
        Label statusLabel = new Label("Estado:");
        statusLabel.getStyleClass().add("filter-label");

        filterStatusBox = new JFXComboBox<>();
        filterStatusBox.getItems().addAll("Todos", "Activo", "Inactivo");
        filterStatusBox.setValue("Todos");
        filterStatusBox.getStyleClass().add("filter-combo");
        filterStatusBox.setOnAction(e -> applyFiltersWithAnimation());

        HBox filterBox = new HBox(15, roleLabel, filterRoleBox, statusLabel, filterStatusBox);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.getStyleClass().add("filter-box");
        filterBox.setPadding(new Insets(10, 10, 10, 10));

        // Efecto de elevación para los filtros
        StackPane filterContainer = new StackPane(filterBox);
        JFXDepthManager.setDepth(filterBox, 1);

        return filterBox;
    }

    private JFXButton createFloatingActionButton() {
        // Icono para el botón
        FontAwesomeIconView addIcon = new FontAwesomeIconView(FontAwesomeIcon.PLUS);
        addIcon.setGlyphSize(24);
        addIcon.setFill(Color.WHITE);

        // Botón flotante con JFoenix
        JFXButton addButton = new JFXButton();
        addButton.setGraphic(addIcon);
        addButton.getStyleClass().add("fab-button");
        addButton.setButtonType(JFXButton.ButtonType.RAISED);
        addButton.setRipplerFill(Color.valueOf("#7986cb"));

        // Efecto de sombra para el botón
        addButton.setEffect(new DropShadow(10, Color.valueOf("#00000080")));

        // Animación al hacer hover
        addButton.setOnMouseEntered(e -> new Pulse(addButton).play());

        // Acción del botón con animación
        addButton.setOnAction(e -> {
            new Tada(addButton).play();
            showUserForm(null);
        });

        return addButton;
    }

    private void showLoadingAnimation() {
        // Indicador de progreso con JFoenix
        JFXSpinner spinner = new JFXSpinner();
        spinner.setPrefSize(80, 80);
        spinner.setRadius(20);

        Label loadingLabel = new Label("Cargando usuarios...");
        loadingLabel.getStyleClass().add("loading-label");

        VBox loadingBox = new VBox(20, spinner, loadingLabel);
        loadingBox.setAlignment(Pos.CENTER);

        // Reemplazar contenido con animación de carga
        StackPane overlay = new StackPane(loadingBox);
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);");

        contentContainer.getChildren().add(overlay);

        // Animar entrada del spinner
        new FadeIn(loadingBox).play();
    }

    private void removeLoadingAnimation() {
        if (contentContainer.getChildren().size() > 1) {
            StackPane overlay = (StackPane) contentContainer.getChildren().get(1);

            // Animar salida
            FadeOut fadeOut = new FadeOut(overlay);
            fadeOut.setOnFinished(e -> contentContainer.getChildren().remove(overlay));
            fadeOut.play();
        }
    }

    private void loadUsers() {
        List<UserDTO> userList = controller.loadAllUsers();
        users = FXCollections.observableArrayList(userList);
        userListView.setItems(users);
        updateStatusLabel(userList.size());

        // Quitar animación de carga
        removeLoadingAnimation();

        // Animar entrada de la lista
        new FadeInUp(userListView).play();
    }

    private void updateStatusLabel(int count) {
        String text = count + " usuario" + (count != 1 ? "s" : "") + " encontrado" + (count != 1 ? "s" : "");

        // Animar el cambio de texto
        FadeOut fadeOut = new FadeOut(statusLabel);
        fadeOut.setOnFinished(e -> {
            statusLabel.setText(text);
            new FadeIn(statusLabel).play();
        });
        fadeOut.play();
    }

    private void handleSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            applyFiltersWithAnimation();
        } else {
            List<UserDTO> filtered = controller.searchUsers(searchTerm);
            applyFiltersToListWithAnimation(filtered);
        }
    }

    private void applyFiltersWithAnimation() {
        // Animar salida de la lista
        FadeOut fadeOut = new FadeOut(userListView);
        fadeOut.setSpeed(2.5);
        fadeOut.setOnFinished(e -> {
            List<UserDTO> allUsers = controller.loadAllUsers();
            applyFiltersToList(allUsers);

            // Animar entrada de la lista filtrada
            new FadeIn(userListView).setSpeed(2.5).play();
        });
        fadeOut.play();
    }

    private void applyFiltersToListWithAnimation(List<UserDTO> userList) {
        // Animar salida de la lista
        FadeOut fadeOut = new FadeOut(userListView);
        fadeOut.setSpeed(2.5);
        fadeOut.setOnFinished(e -> {
            applyFiltersToList(userList);

            // Animar entrada de la lista filtrada
            new FadeIn(userListView).setSpeed(2.5).play();
        });
        fadeOut.play();
    }

    private void applyFiltersToList(List<UserDTO> userList) {
        // Aplicar filtro de rol
        String roleFilter = filterRoleBox.getValue();
        String statusFilter = filterStatusBox.getValue();

        List<UserDTO> filteredUsers = userList.stream()
                .filter(user -> {
                    // Filtro por rol
                    if (!roleFilter.equals("Todos")) {
                        String userRole = user.getRol().equals("admin") ? "Administrador" : "Usuario";
                        if (!userRole.equals(roleFilter)) {
                            return false;
                        }
                    }

                    // Filtro por estado
                    if (!statusFilter.equals("Todos")) {
                        boolean userActive = user.isActivo();
                        if ((statusFilter.equals("Activo") && !userActive) ||
                                (statusFilter.equals("Inactivo") && userActive)) {
                            return false;
                        }
                    }

                    return true;
                })
                .toList();

        users.setAll(filteredUsers);
        updateStatusLabel(filteredUsers.size());
    }

    private void showUserForm(UserDTO user) {
        UserForm userForm = new UserForm(user, controller);

        // Configurar stage con estilo moderno
        Stage formStage = new Stage();
        formStage.initStyle(StageStyle.TRANSPARENT);

        Scene scene = new Scene(userForm, 500, 520);
        scene.setFill(Color.TRANSPARENT);
        formStage.setScene(scene);

        // Mostrar con animación
        formStage.show();
        new ZoomIn(userForm).play();

        // Recargar usuarios después de cerrar el formulario
        formStage.setOnHidden(e -> {
            loadUsers();
            applyFiltersWithAnimation();
        });
    }

    private void handleEditUser(UserDTO user) {
        showUserForm(user);
    }

    private void handleToggleStatus(UserDTO user) {
        boolean newStatus = !user.isActivo();
        if (controller.toggleUserStatus(user.getId(), newStatus)) {
            user.setActivo(newStatus);
            userListView.refresh();

            // Mostrar alerta con animación
            showCustomAlert("Estado cambiado",
                    "El usuario " + user.getNombre() + " ha sido " +
                            (newStatus ? "activado" : "desactivado"),
                    true);
        } else {
            showCustomAlert("Error", "No se pudo cambiar el estado del usuario", false);
        }
    }

    private void handleDeleteUser(UserDTO user) {
        // Diálogo de confirmación con JFoenix
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Label("Eliminar Usuario"));
        content.setBody(new Label("¿Estás seguro de eliminar a " + user.getNombre() + "?"));

        JFXButton cancelButton = new JFXButton("Cancelar");
        JFXButton confirmButton = new JFXButton("Eliminar");
        confirmButton.getStyleClass().add("dialog-confirm");
        cancelButton.getStyleClass().add("dialog-cancel");

        content.setActions(cancelButton, confirmButton);

        JFXDialog dialog = new JFXDialog(this, content, JFXDialog.DialogTransition.CENTER);

        cancelButton.setOnAction(e -> dialog.close());
        confirmButton.setOnAction(e -> {
            dialog.close();

            if (controller.deleteUser(user.getId())) {
                // Animar la eliminación de la fila
                UserCard card = (UserCard) userListView.lookup("#user-card-" + user.getId());
                if (card != null) {
                    FadeOutRight fadeOutRight = new FadeOutRight(card);
                    fadeOutRight.setOnFinished(event -> {
                        users.remove(user);
                        updateStatusLabel(users.size());
                    });
                    fadeOutRight.play();
                } else {
                    users.remove(user);
                    updateStatusLabel(users.size());
                }

                showCustomAlert("Usuario eliminado",
                        "El usuario ha sido eliminado correctamente",
                        true);
            } else {
                showCustomAlert("Error", "No se pudo eliminar el usuario", false);
            }
        });

        // Animar apertura del diálogo
        dialog.show();
        new ZoomIn(content).play();
    }

    private void showCustomAlert(String title, String message, boolean success) {
        // Crear diálogo personalizado con JFoenix
        JFXDialogLayout content = new JFXDialogLayout();

        // Título con icono
        FontAwesomeIconView icon = new FontAwesomeIconView(
                success ? FontAwesomeIcon.CHECK_CIRCLE : FontAwesomeIcon.EXCLAMATION_TRIANGLE);
        icon.setGlyphSize(24);
        icon.setFill(success ? Color.valueOf("#4caf50") : Color.valueOf("#f44336"));

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("alert-title");

        HBox headerBox = new HBox(10, icon, titleLabel);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        content.setHeading(headerBox);
        content.setBody(new Label(message));

        JFXButton okButton = new JFXButton("Aceptar");
        okButton.getStyleClass().add(success ? "dialog-confirm" : "dialog-cancel");
        content.setActions(okButton);

        JFXDialog dialog = new JFXDialog(this, content, JFXDialog.DialogTransition.TOP);

        okButton.setOnAction(e -> dialog.close());

        // Mostrar con animación
        dialog.show();
        new FadeIn(content).play();

        // Cerrar automáticamente después de un tiempo
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(3),
                ae -> dialog.close()
        ));
        timeline.play();
    }
}