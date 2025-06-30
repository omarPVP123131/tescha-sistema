package tescha.departamento.dao;

import tescha.departamento.dto.DepartamentoDTO;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoSQLiteDAO implements DepartamentoDAO {
    @Override
    public List<DepartamentoDTO> obtenerTodos(Connection connection) throws SQLException {
        List<DepartamentoDTO> departamentos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, estado, fecha FROM departamentos ORDER BY nombre";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                DepartamentoDTO d = new DepartamentoDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("estado"),
                        LocalDateTime.parse(rs.getString("fecha"))
                );
                departamentos.add(d);
            }
        }
        return departamentos;
    }

    @Override
    public DepartamentoDTO obtenerPorId(Connection connection, int id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion, estado, fecha FROM departamentos WHERE id = ?";
        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setInt(1, id);
            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    return new DepartamentoDTO(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getString("estado"),
                            LocalDateTime.parse(rs.getString("fecha"))
                    );
                }
            }
        }
        return null;
    }

    @Override
    public int agregar(Connection connection, DepartamentoDTO departamento) throws SQLException {
        String sql = "INSERT INTO departamentos(nombre, descripcion, estado, fecha) VALUES(?,?,?,?)";
        try (PreparedStatement p = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, departamento.getNombre());
            p.setString(2, departamento.getDescripcion());
            p.setString(3, departamento.getEstado());
            p.setString(4, departamento.getFecha().toString());
            p.executeUpdate();

            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("No se pudo obtener el ID generado");
        }
    }

    @Override
    public void actualizar(Connection connection, DepartamentoDTO departamento) throws SQLException {
        String sql = "UPDATE departamentos SET nombre=?, descripcion=?, estado=?, fecha=? WHERE id=?";
        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setString(1, departamento.getNombre());
            p.setString(2, departamento.getDescripcion());
            p.setString(3, departamento.getEstado());
            p.setString(4, departamento.getFecha().toString());
            p.setInt(5, departamento.getId());
            p.executeUpdate();
        }
    }

    @Override
    public void eliminar(Connection connection, int id) throws SQLException {
        String sql = "DELETE FROM departamentos WHERE id=?";
        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }

    @Override
    public boolean existeNombre(Connection connection, String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM departamentos WHERE nombre=?";
        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setString(1, nombre);
            try (ResultSet rs = p.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
