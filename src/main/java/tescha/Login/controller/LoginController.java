package tescha.Login.controller;

import tescha.Login.model.UserModel;
import tescha.Login.view.LoginView;
import tescha.MainWindow.view.MainWindow;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class LoginController {
    private UserModel model;
    private LoginView view;

    public LoginController(UserModel model, LoginView view) {
        this.model = model;
        this.view = view;

        initView();
        setupEventHandlers();
    }

    private void initView() {
        view.populateUserComboBox(model.getUsers().keySet());

        view.getUserComboBox().setOnAction(e -> {
            String selectedUser = view.getUserComboBox().getValue();
            if (selectedUser != null && model.getUsers().containsKey(selectedUser)) {
                String role = model.getUsers().get(selectedUser).getRole();
                view.updateRoleLabel(role);
                view.updateAvatarBasedOnRole(role);
            } else {
                view.updateRoleLabel("Desconocido");
            }
        });
    }

    private void setupEventHandlers() {
        view.getLoginButton().setOnAction(e -> handleLogin());
        view.getPasswordField().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) handleLogin();
        });
    }

    private void handleLogin() {
        String username = view.getUserComboBox().getValue();
        String password = view.getPasswordField().getText();

        if (username == null || username.isEmpty()) {
            view.showErrorMessage("Seleccione un usuario");
            return;
        }

        if (password == null || password.isEmpty()) {
            view.showErrorMessage("Ingrese una contraseña");
            return;
        }

        if (model.validateLogin(username, password)) {
            String role = model.getUsers().get(username).getRole();
            String lastAccess = model.getUsers().get(username).getLast_access();

            String welcomeMessage = "Bienvenido " + username;
            if (lastAccess != null) {
                welcomeMessage += "\nÚltimo acceso: " + lastAccess;
            }

            view.showSuccessMessage(welcomeMessage);
            openMainWindow(username, role);
        } else {
            view.showErrorMessage("Credenciales incorrectas");
        }
    }
    private void openMainWindow(String username, String role) {
        Stage loginStage = (Stage) view.getLoginButton().getScene().getWindow();
        loginStage.close();

        // Obtener el avatar seleccionado desde la vista
        javafx.scene.image.Image avatar = view.getSelectedAvatar();

        MainWindow mainWindow = new MainWindow(username, role, avatar);
        Stage mainStage = new Stage();
        mainWindow.show();
    }
}