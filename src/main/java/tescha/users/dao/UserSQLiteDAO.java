package tescha.users.dao;

import tescha.users.dto.UserDTO;
import tescha.database.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class UserSQLiteDAO implements UserDAO {
    private Connection connection;

    public UserSQLiteDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUserDTO(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los usuarios: " + e.getMessage());
        }
        return users;
    }

    @Override
    public UserDTO getUserById(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUserDTO(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        String sql = "SELECT * FROM usuarios WHERE username = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUserDTO(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por username: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertUser(UserDTO user) {
        String sql = "INSERT INTO usuarios(username, password, nombre, telefono, " +
                "departamento, rol, activo, imagen, fecha_registro) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getNombre());
            pstmt.setString(4, user.getTelefono());
            pstmt.setString(5, user.getDepartamento());
            pstmt.setString(6, user.getRol());
            pstmt.setInt(7, user.isActivo() ? 1 : 0);
            pstmt.setString(8, user.getImagen());
            pstmt.setString(9, user.getFechaRegistro());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateUser(UserDTO user) {
        String sql = "UPDATE usuarios SET username = ?, nombre = ?, telefono = ?, " +
                "departamento = ?, rol = ?, activo = ?, imagen = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getNombre());
            pstmt.setString(3, user.getTelefono());
            pstmt.setString(4, user.getDepartamento());
            pstmt.setString(5, user.getRol());
            pstmt.setInt(6, user.isActivo() ? 1 : 0);
            pstmt.setString(7, user.getImagen());
            pstmt.setInt(8, user.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean toggleUserStatus(int id, boolean activo) {
        String sql = "UPDATE usuarios SET activo = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, activo ? 1 : 0);
            pstmt.setInt(2, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado de usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<UserDTO> filterUsers(String searchTerm) {
        List<UserDTO> filteredUsers = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE username LIKE ? OR nombre LIKE ? OR departamento LIKE ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            pstmt.setString(3, likeTerm);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                filteredUsers.add(mapResultSetToUserDTO(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al filtrar usuarios: " + e.getMessage());
        }
        return filteredUsers;
    }

    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM usuarios WHERE username = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error al verificar username: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE usuarios SET password = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
            return false;
        }
    }

    private UserDTO mapResultSetToUserDTO(ResultSet rs) throws SQLException {
        UserDTO user = new UserDTO();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setNombre(rs.getString("nombre"));
        user.setTelefono(rs.getString("telefono"));
        user.setDepartamento(rs.getString("departamento"));
        user.setRol(rs.getString("rol"));
        user.setActivo(rs.getInt("activo") == 1);
        user.setUltimoAcceso(rs.getString("ultimo_acceso"));
        user.setImagen(rs.getString("imagen"));
        user.setFechaRegistro(rs.getString("fecha_registro"));
        return user;
    }
}