package tescha.Login.model;

import tescha.Login.dto.UserDTO;
import tescha.database.DatabaseManager;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserModel {
    private Map<String, UserDTO> users = new HashMap<>();

    public UserModel() {
        loadUsersFromDB();
    }

    private void loadUsersFromDB() {
        String sql = "SELECT username, password, rol FROM usuarios WHERE activo = 1";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                users.put(rs.getString("username"),
                        new UserDTO(
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("rol")
                        ));
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
    }

    public boolean validateLogin(String username, String password) {
        UserDTO user = users.get(username);
        return user != null && user.getPassword().equals(password);
    }

    public Map<String, UserDTO> getUsers() {
        return users;
    }
}