package tescha;


import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tescha.Login.controller.LoginController;
import tescha.Login.model.UserModel;
import tescha.Login.view.LoginView;
import tescha.database.DatabaseManager;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));

        // Crear Los Componentes MVC
        UserModel model = new UserModel();
        LoginView view = new LoginView(primaryStage);
        LoginController controller = new LoginController(model, view);
        DatabaseManager.createTablesIfNotExist();
        // Mostrar La vista
        view.show();

    }

    @Override
    public void stop() {
        DatabaseManager.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }}