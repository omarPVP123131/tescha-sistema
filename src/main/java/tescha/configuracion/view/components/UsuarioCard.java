package tescha.configuracion.view.components;

import tescha.configuracion.dto.UsuarioDTO;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;

public class UsuarioCard extends VBox {
    public UsuarioCard(UsuarioDTO usuario) {
        setSpacing(10);
        setPadding(new Insets(15));
        getStyleClass().add("usuario-card");

        Label lblTitulo = new Label("Detalles del Usuario");
        lblTitulo.getStyleClass().add("card-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        grid.add(new Label("Usuario:"), 0, 0);
        grid.add(new Label(usuario.getUsername()), 1, 0);

        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(new Label(usuario.getNombre()), 1, 1);

        grid.add(new Label("Teléfono:"), 0, 2);
        grid.add(new Label(usuario.getTelefono()), 1, 2);

        grid.add(new Label("Departamento:"), 0, 3);
        grid.add(new Label(usuario.getDepartamento()), 1, 3);

        grid.add(new Label("Rol:"), 0, 4);
        grid.add(new Label(usuario.getRol()), 1, 4);

        grid.add(new Label("Estado:"), 0, 5);
        grid.add(new Label(usuario.isActivo() ? "Activo" : "Inactivo"), 1, 5);

        if (usuario.getUltimoAcceso() != null) {
            grid.add(new Label("Último acceso:"), 0, 6);
            grid.add(new Label(usuario.getUltimoAcceso()), 1, 6);
        }

        getChildren().addAll(lblTitulo, grid);
    }
}