package tescha.configuracion.view.components;

import tescha.configuracion.controller.ConfiguracionController;
import tescha.configuracion.dto.UsuarioDTO;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class UsuarioForm extends Dialog<UsuarioDTO> {
    private final ConfiguracionController controller;
    private final UsuarioDTO usuario;

    private TextField campoUsername;
    private PasswordField campoPassword;
    private TextField campoPasswordVisible;
    private TextField campoNombre;
    private TextField campoTelefono;
    private TextField campoDepartamento;
    private ComboBox<String> comboRol;
    private CheckBox checkActivo;
    private boolean passwordVisible = false;

    public UsuarioForm(ConfiguracionController controller, UsuarioDTO usuario) {
        this.controller = controller;
        this.usuario = usuario;

        setTitle(usuario == null ? "Agregar Usuario" : "Editar Usuario");
        getDialogPane().getStylesheets().add(getClass().getResource("/styles/configuracion.css").toExternalForm());

        // Crear botones
        ButtonType botonGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(botonGuardar, botonCancelar);

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        campoUsername = new TextField();
        campoUsername.setPromptText("Nombre de usuario");

        campoPassword = new PasswordField();
        campoPassword.setPromptText("Contraseña");

        campoPasswordVisible = new TextField();
        campoPasswordVisible.setPromptText("Contraseña");
        campoPasswordVisible.setVisible(false);
        campoPasswordVisible.setManaged(false);

        // Botón para mostrar/ocultar contraseña
        Button togglePasswordButton = new Button();
        ImageView eyeIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.png")));
        eyeIcon.setFitWidth(16);
        eyeIcon.setFitHeight(16);
        togglePasswordButton.setGraphic(eyeIcon);
        togglePasswordButton.getStyleClass().add("toggle-password-button");
        togglePasswordButton.setOnAction(e -> togglePasswordVisibility());

        HBox passwordBox = new HBox(5, campoPassword, campoPasswordVisible, togglePasswordButton);
        passwordBox.setAlignment(Pos.CENTER_LEFT);

        campoNombre = new TextField();
        campoNombre.setPromptText("Nombre completo");

        campoTelefono = new TextField();
        campoTelefono.setPromptText("Teléfono");

        campoDepartamento = new TextField();
        campoDepartamento.setPromptText("Departamento");

        comboRol = new ComboBox<>();
        comboRol.getItems().addAll("admin", "usuario");
        comboRol.setValue("usuario");

        checkActivo = new CheckBox("Activo");
        checkActivo.setSelected(true);

        // Si estamos editando, cargar datos
        if (usuario != null) {
            campoUsername.setText(usuario.getUsername());
            campoPassword.setText(usuario.getPassword());
            campoPasswordVisible.setText(usuario.getPassword());
            campoNombre.setText(usuario.getNombre());
            campoTelefono.setText(usuario.getTelefono());
            campoDepartamento.setText(usuario.getDepartamento());
            comboRol.setValue(usuario.getRol());
            checkActivo.setSelected(usuario.isActivo());
            campoUsername.setDisable(true);
        }

        grid.add(new Label("Usuario:"), 0, 0);
        grid.add(campoUsername, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(passwordBox, 1, 1);
        grid.add(new Label("Nombre:"), 0, 2);
        grid.add(campoNombre, 1, 2);
        grid.add(new Label("Teléfono:"), 0, 3);
        grid.add(campoTelefono, 1, 3);
        grid.add(new Label("Departamento:"), 0, 4);
        grid.add(campoDepartamento, 1, 4);
        grid.add(new Label("Rol:"), 0, 5);
        grid.add(comboRol, 1, 5);
        grid.add(checkActivo, 1, 6);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == botonGuardar) {
                UsuarioDTO usuarioActualizado = usuario != null ? usuario : new UsuarioDTO();
                usuarioActualizado.setUsername(campoUsername.getText());
                String password = passwordVisible ? campoPasswordVisible.getText() : campoPassword.getText();
                usuarioActualizado.setPassword(password);
                usuarioActualizado.setNombre(campoNombre.getText());
                usuarioActualizado.setTelefono(campoTelefono.getText());
                usuarioActualizado.setDepartamento(campoDepartamento.getText());
                usuarioActualizado.setRol(comboRol.getValue());
                usuarioActualizado.setActivo(checkActivo.isSelected());

                try {
                    if (usuario == null) {
                        if (controller.agregarUsuario(usuarioActualizado)) {
                            mostrarAlerta("Usuario agregado correctamente", Alert.AlertType.INFORMATION);
                            return usuarioActualizado;
                        } else {
                            mostrarAlerta("Error al agregar usuario", Alert.AlertType.ERROR);
                            return null;
                        }
                    } else {
                        if (controller.actualizarUsuario(usuarioActualizado)) {
                            mostrarAlerta("Usuario actualizado correctamente", Alert.AlertType.INFORMATION);
                            return usuarioActualizado;
                        } else {
                            mostrarAlerta("Error al actualizar usuario", Alert.AlertType.ERROR);
                            return null;
                        }
                    }
                } catch (Exception e) {
                    mostrarAlerta("Error: " + e.getMessage(), Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            campoPasswordVisible.setText(campoPassword.getText());
            campoPasswordVisible.setVisible(true);
            campoPasswordVisible.setManaged(true);
            campoPassword.setVisible(false);
            campoPassword.setManaged(false);
        } else {
            campoPassword.setText(campoPasswordVisible.getText());
            campoPassword.setVisible(true);
            campoPassword.setManaged(true);
            campoPasswordVisible.setVisible(false);
            campoPasswordVisible.setManaged(false);
        }
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}