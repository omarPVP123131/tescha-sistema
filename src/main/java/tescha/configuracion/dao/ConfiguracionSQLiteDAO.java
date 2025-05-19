package tescha.configuracion.dao;

import tescha.configuracion.dto.ConfiguracionDTO;
import tescha.configuracion.dto.UsuarioDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracionSQLiteDAO implements ConfiguracionDAO {
    private final Connection connection;

    public ConfiguracionSQLiteDAO(Connection connection) {
        this.connection = connection;
        crearTablasSiNoExisten();
    }

    private void crearTablasSiNoExisten() {
        try (Statement stmt = connection.createStatement()) {
            // Tabla de configuración
            stmt.execute("CREATE TABLE IF NOT EXISTS configuracion (" +
                    "id INTEGER PRIMARY KEY," +
                    "respaldos_automaticos INTEGER DEFAULT 0," +
                    "frecuencia_respaldo TEXT," +
                    "ruta_respaldo TEXT)");

            // Tabla de usuarios (ya existe según tu código)
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY," +
                    "username TEXT UNIQUE," +
                    "password TEXT," +
                    "nombre TEXT," +
                    "telefono TEXT," +
                    "departamento TEXT," +
                    "rol TEXT DEFAULT 'usuario' CHECK (rol IN ('admin', 'usuario'))," +
                    "activo INTEGER DEFAULT 1," +
                    "ultimo_acceso TEXT)");

            // Insertar configuración inicial si no existe
            if (!existeConfiguracion()) {
                stmt.execute("INSERT INTO configuracion (respaldos_automaticos) VALUES (0)");
            }
        } catch (SQLException e) {
            System.err.println("Error al crear tablas: " + e.getMessage());
        }
    }

    private boolean existeConfiguracion() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM configuracion")) {
            return rs.getInt(1) > 0;
        }
    }

    @Override
    public ConfiguracionDTO obtenerConfiguracion() {
        String sql = "SELECT * FROM configuracion LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                ConfiguracionDTO config = new ConfiguracionDTO();
                config.setId(rs.getInt("id"));
                config.setRespaldosAutomaticos(rs.getBoolean("respaldos_automaticos"));
                config.setFrecuenciaRespaldo(rs.getString("frecuencia_respaldo"));
                config.setRutaRespaldo(rs.getString("ruta_respaldo"));
                return config;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener configuración: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean guardarConfiguracion(ConfiguracionDTO configuracion) {
        String sql = "UPDATE configuracion SET respaldos_automaticos = ?, frecuencia_respaldo = ?, ruta_respaldo = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, configuracion.isRespaldosAutomaticos());
            pstmt.setString(2, configuracion.getFrecuenciaRespaldo());
            pstmt.setString(3, configuracion.getRutaRespaldo());
            pstmt.setInt(4, configuracion.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar configuración: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<UsuarioDTO> listarUsuarios() {
        List<UsuarioDTO> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                UsuarioDTO usuario = new UsuarioDTO();
                usuario.setId(rs.getInt("id"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPassword(rs.getString("password"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setDepartamento(rs.getString("departamento"));
                usuario.setRol(rs.getString("rol"));
                usuario.setActivo(rs.getBoolean("activo"));
                usuario.setUltimoAcceso(rs.getString("ultimo_acceso"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    @Override
    public UsuarioDTO obtenerUsuario(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    UsuarioDTO usuario = new UsuarioDTO();
                    usuario.setId(rs.getInt("id"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setTelefono(rs.getString("telefono"));
                    usuario.setDepartamento(rs.getString("departamento"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setActivo(rs.getBoolean("activo"));
                    usuario.setUltimoAcceso(rs.getString("ultimo_acceso"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean agregarUsuario(UsuarioDTO usuario) {
        String sql = "INSERT INTO usuarios (username, password, nombre, telefono, departamento, rol, activo) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getUsername());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(3, usuario.getNombre());
            pstmt.setString(4, usuario.getTelefono());
            pstmt.setString(5, usuario.getDepartamento());
            pstmt.setString(6, usuario.getRol());
            pstmt.setBoolean(7, usuario.isActivo());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarUsuario(UsuarioDTO usuario) {
        String sql = "UPDATE usuarios SET password = ?, nombre = ?, telefono = ?, " +
                "departamento = ?, rol = ?, activo = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getPassword());
            pstmt.setString(2, usuario.getNombre());
            pstmt.setString(3, usuario.getTelefono());
            pstmt.setString(4, usuario.getDepartamento());
            pstmt.setString(5, usuario.getRol());
            pstmt.setBoolean(6, usuario.isActivo());
            pstmt.setInt(7, usuario.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean cambiarEstadoUsuario(int id, boolean activo) {
        String sql = "UPDATE usuarios SET activo = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, activo);
            pstmt.setInt(2, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cambiar estado de usuario: " + e.getMessage());
            return false;
        }
    }
}