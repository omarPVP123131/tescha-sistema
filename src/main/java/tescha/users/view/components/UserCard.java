package tescha.users.view.components;

import animatefx.animation.*;
import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.*;
import javafx.scene.image.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.materialdesign2.MaterialDesignE;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import tescha.users.dto.UserDTO;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class UserCard extends ListCell<UserDTO> {
    private final UserActionHandler editHandler;
    private final UserActionHandler toggleHandler;
    private final UserActionHandler deleteHandler;
    private HBox card;

    public UserCard(UserActionHandler editHandler,
                    UserActionHandler toggleHandler,
                    UserActionHandler deleteHandler) {
        this.editHandler = editHandler;
        this.toggleHandler = toggleHandler;
        this.deleteHandler = deleteHandler;
    }

    @Override
    protected void updateItem(UserDTO user, boolean empty) {
        super.updateItem(user, empty);

        if (empty || user == null) {
            setText(null);
            setGraphic(null);
        } else {
            // Crear una tarjeta visual para el usuario
            card = new HBox();
            card.getStyleClass().add("user-card");
            card.setPadding(new Insets(12));
            card.setSpacing(16);
            card.setAlignment(Pos.CENTER_LEFT);

            if (!user.isActivo()) {
                card.getStyleClass().add("inactive-user");
            }

            // Imagen de perfil circular
            StackPane imageContainer = createUserAvatar(user);

            // Información del usuario
            VBox userInfo = new VBox(6);
            userInfo.setAlignment(Pos.CENTER_LEFT);

            HBox nameContainer = new HBox(8);
            nameContainer.setAlignment(Pos.CENTER_LEFT);

            Label nameLabel = new Label(user.getNombre());
            nameLabel.getStyleClass().add("user-name");

            // Status chip con ícono
            HBox statusChip = new HBox(4);
            statusChip.setAlignment(Pos.CENTER);
            statusChip.getStyleClass().add(user.isActivo() ? "status-active" : "status-inactive");

            FontIcon statusIcon = new FontIcon(user.isActivo() ?
                    MaterialDesignC.CHECK_CIRCLE_OUTLINE : MaterialDesignA.ACCOUNT_OFF_OUTLINE);
            statusIcon.getStyleClass().add("status-icon");

            Label statusLabel = new Label(user.isActivo() ? "Activo" : "Inactivo");
            statusChip.getChildren().addAll(statusIcon, statusLabel);

            nameContainer.getChildren().addAll(nameLabel, statusChip);

            HBox usernameContainer = new HBox(5);
            usernameContainer.setAlignment(Pos.CENTER_LEFT);

            FontIcon userIcon = new FontIcon(MaterialDesignA.ACCOUNT);
            userIcon.getStyleClass().add("user-icon");

            Label usernameLabel = new Label("@" + user.getUsername());
            usernameLabel.getStyleClass().add("user-username");

            usernameContainer.getChildren().addAll(userIcon, usernameLabel);

            // Rol con icono
            HBox roleContainer = new HBox(5);
            roleContainer.setAlignment(Pos.CENTER_LEFT);

            boolean isAdmin = user.getRol().equals("admin");
            FontIcon roleIcon = new FontIcon(isAdmin ?
                    MaterialDesignA.ACCOUNT_KEY : MaterialDesignA.ACCOUNT_OUTLINE);
            roleIcon.getStyleClass().add("role-icon");

            Label roleLabel = new Label(isAdmin ? "Administrador" : "Usuario");
            roleLabel.getStyleClass().add("user-role");
            if (isAdmin) {
                roleLabel.getStyleClass().add("admin-role");
            }

            roleContainer.getChildren().addAll(roleIcon, roleLabel);

            // Detalles adicionales (opcional)
            HBox detailsContainer = new HBox(10);
            detailsContainer.setAlignment(Pos.CENTER_LEFT);

            if (user.getDepartamento() != null && !user.getDepartamento().isEmpty()) {
                HBox deptContainer = new HBox(4);
                FontIcon deptIcon = new FontIcon(MaterialDesignD.DOMAIN);
                deptIcon.getStyleClass().add("detail-icon");
                Label deptLabel = new Label(user.getDepartamento());
                deptLabel.getStyleClass().add("user-detail");
                deptContainer.getChildren().addAll(deptIcon, deptLabel);
                detailsContainer.getChildren().add(deptContainer);
            }

            if (user.getTelefono() != null && !user.getTelefono().isEmpty()) {
                HBox phoneContainer = new HBox(4);
                FontIcon phoneIcon = new FontIcon(MaterialDesignC.CELLPHONE);
                phoneIcon.getStyleClass().add("detail-icon");
                Label phoneLabel = new Label(user.getTelefono());
                phoneLabel.getStyleClass().add("user-detail");
                phoneContainer.getChildren().addAll(phoneIcon, phoneLabel);
                detailsContainer.getChildren().add(phoneContainer);
            }

            userInfo.getChildren().addAll(nameContainer, usernameContainer, roleContainer);
            if (!detailsContainer.getChildren().isEmpty()) {
                userInfo.getChildren().add(detailsContainer);
            }

            // Región espaciadora
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Botones de acción con iconos
            HBox actions = new HBox(8);
            actions.setAlignment(Pos.CENTER_RIGHT);
            actions.getStyleClass().add("action-buttons-container");

            // Botón Editar con ícono
            JFXButton editBtn = new JFXButton();
            editBtn.getStyleClass().addAll("action-button", "edit-button");
            FontIcon editIcon = new FontIcon(MaterialDesignE.ELLIPSE_OUTLINE);
            editIcon.getStyleClass().add("button-icon");
            editBtn.setGraphic(editIcon);
            editBtn.setOnAction(e -> {
                new Pulse(editBtn).play();
                editHandler.handle(user);
            });
            Tooltip.install(editBtn, "Editar usuario");

            // Botón Activar/Desactivar con ícono
            JFXButton toggleBtn = new JFXButton();
            toggleBtn.getStyleClass().addAll("action-button",
                    user.isActivo() ? "deactivate-button" : "activate-button");
            FontIcon toggleIcon = new FontIcon(user.isActivo() ?
                    MaterialDesignA.ACCOUNT_OFF : MaterialDesignA.ACCOUNT_CHECK);
            toggleIcon.getStyleClass().add("button-icon");
            toggleBtn.setGraphic(toggleIcon);
            toggleBtn.setOnAction(e -> {
                new Pulse(toggleBtn).play();
                toggleHandler.handle(user);
            });
            Tooltip.install(toggleBtn, user.isActivo() ? "Desactivar usuario" : "Activar usuario");

            // Botón Eliminar con ícono
            JFXButton deleteBtn = new JFXButton();
            deleteBtn.getStyleClass().addAll("action-button", "delete-button");
            FontIcon deleteIcon = new FontIcon(MaterialDesignD.DELETE);
            deleteIcon.getStyleClass().add("button-icon");
            deleteBtn.setGraphic(deleteIcon);
            deleteBtn.setOnAction(e -> {
                new Shake(deleteBtn).play();
                deleteHandler.handle(user);
            });
            Tooltip.install(deleteBtn, "Eliminar usuario");

            actions.getChildren().addAll(editBtn, toggleBtn, deleteBtn);

            card.getChildren().addAll(imageContainer, userInfo, spacer, actions);

            // Agregar efecto de sombra a la tarjeta
            DropShadow shadow = new DropShadow();
            shadow.setRadius(8.0);
            shadow.setOffsetX(0.0);
            shadow.setOffsetY(2.0);
            shadow.setColor(Color.color(0.0, 0.0, 0.0, 0.15));
            card.setEffect(shadow);

            setGraphic(card);

            // Añadir animación de entrada
            new FadeIn(card).setSpeed(1.8).play();
        }
    }

    private StackPane createUserAvatar(UserDTO user) {
        // Crear círculo para recortar la imagen
        ImageView imgView = new ImageView();
        imgView.setFitHeight(55);
        imgView.setFitWidth(55);
        imgView.setPreserveRatio(true);

        try {
            if (user.getImagen() != null && !user.getImagen().isEmpty()) {
                imgView.setImage(new Image(user.getImagen()));
            } else {
                imgView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
            }
        } catch (Exception e) {
            imgView.setImage(new Image(getClass().getResourceAsStream("/images/placeholder.png")));
        }

        // Contenedor circular para la imagen
        Circle clip = new Circle(27.5, 27.5, 27.5);
        imgView.setClip(clip);

        // Contenedor para la imagen con borde
        StackPane imageContainer = new StackPane(imgView);
        imageContainer.getStyleClass().add("avatar-container");

        // Badge para administrador
        if (user.getRol().equals("admin")) {
            StackPane adminBadge = new StackPane();
            adminBadge.getStyleClass().add("admin-badge");
            FontIcon crownIcon = new FontIcon(MaterialDesignC.CROWN);
            crownIcon.getStyleClass().add("badge-icon");
            adminBadge.getChildren().add(crownIcon);

            // Posicionar el badge en la esquina superior derecha
            StackPane.setAlignment(adminBadge, Pos.TOP_RIGHT);
            StackPane.setMargin(adminBadge, new Insets(-5, -5, 0, 0));

            imageContainer.getChildren().add(adminBadge);
        }

        return imageContainer;
    }

    private static class Tooltip {
        public static void install(JFXButton button, String text) {
            javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(text);
            tooltip.setShowDelay(Duration.millis(300));
            javafx.scene.control.Tooltip.install(button, tooltip);
        }
    }

    @FunctionalInterface
    public interface UserActionHandler {
        void handle(UserDTO user);
    }
}