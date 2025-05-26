package tescha.Login.model;

import tescha.Login.dto.UserDTO;
import tescha.database.DatabaseManager;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserModel {
    private Map<String, UserDTO> users = new HashMap<>();

    public UserModel() {
        loadUsersFromDB();
    }

    private void loadUsersFromDB() {
        String sql = "SELECT username, password, rol, ultimo_acceso FROM usuarios WHERE activo = 1";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                UserDTO user = new UserDTO(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("rol")
                );
                user.setLast_access(rs.getString("ultimo_acceso"));
                users.put(rs.getString("username"), user);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
    }

    public boolean validateLogin(String username, String password) {
        UserDTO user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            updateLastAccess(username);
            return true;
        }
        return false;
    }

    private void updateLastAccess(String username) {
        // Formato: dd-MM-yyyy-hh-mm-ss-a (ejemplo: 25-05-2024-03-45-22-PM)
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss-a");
        String formattedDate = sdf.format(new Date());

        String sql = "UPDATE usuarios SET ultimo_acceso = ? WHERE username = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, formattedDate);
            pstmt.setString(2, username);
            pstmt.executeUpdate();

            // Actualizar también en el objeto en memoria
            users.get(username).setLast_access(formattedDate);
        } catch (SQLException e) {
            System.err.println("Error al actualizar último acceso: " + e.getMessage());
        }
    }

    public Map<String, UserDTO> getUsers() {
        return users;
    }
}