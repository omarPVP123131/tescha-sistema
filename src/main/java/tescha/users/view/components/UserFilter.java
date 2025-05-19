package tescha.users.view.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import tescha.users.dto.UserDTO;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class UserFilter extends HBox {
    private final ComboBox<String> roleFilter;
    private final ComboBox<String> statusFilter;
    private final Consumer<List<UserDTO>> onFilterChanged;
    private List<UserDTO> allUsers;

    public UserFilter(List<UserDTO> users, Consumer<List<UserDTO>> onFilterChanged) {
        super(10);
        this.allUsers = users;
        this.onFilterChanged = onFilterChanged;

        setPadding(new Insets(10, 0, 10, 0));
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("filter-container");

        // Filtro por rol
        Label roleLabel = new Label("Rol:");
        roleLabel.getStyleClass().add("filter-label");

        roleFilter = new ComboBox<>();
        roleFilter.getItems().addAll("Todos", "Administrador", "Usuario");
        roleFilter.setValue("Todos");
        roleFilter.getStyleClass().add("filter-combo");
        roleFilter.setOnAction(e -> applyFilters());

        // Filtro por estado
        Label statusLabel = new Label("Estado:");
        statusLabel.getStyleClass().add("filter-label");

        statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Todos", "Activo", "Inactivo");
        statusFilter.setValue("Todos");
        statusFilter.getStyleClass().add("filter-combo");
        statusFilter.setOnAction(e -> applyFilters());

        getChildren().addAll(roleLabel, roleFilter, statusLabel, statusFilter);
    }

    public void updateUserList(List<UserDTO> users) {
        this.allUsers = users;
        applyFilters();
    }

    private void applyFilters() {
        String selectedRole = roleFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        List<UserDTO> filteredUsers = allUsers.stream()
                .filter(user -> {
                    // Filter by role
                    if (!selectedRole.equals("Todos")) {
                        String userRole = user.getRol().equals("admin") ? "Administrador" : "Usuario";
                        if (!userRole.equals(selectedRole)) {
                            return false;
                        }
                    }

                    // Filter by status
                    if (!selectedStatus.equals("Todos")) {
                        boolean userActive = user.isActivo();
                        if ((selectedStatus.equals("Activo") && !userActive) ||
                                (selectedStatus.equals("Inactivo") && userActive)) {
                            return false;
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());

        onFilterChanged.accept(filteredUsers);
    }

    public void resetFilters() {
        roleFilter.setValue("Todos");
        statusFilter.setValue("Todos");
        applyFilters();
    }
}