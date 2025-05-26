package tescha.departamento.dao;

import tescha.departamento.dto.DepartamentoDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoSQLiteDAO implements DepartamentoDAO {
    private Connection connection;

    public DepartamentoSQLiteDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<DepartamentoDTO> obtenerTodos() throws SQLException {
        List<DepartamentoDTO> departamentos = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion FROM departamentos ORDER BY nombre";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                DepartamentoDTO departamento = new DepartamentoDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
                departamentos.add(departamento);
            }
        }
        return departamentos;
    }

    @Override
    public DepartamentoDTO obtenerPorId(int id) throws SQLException {
        String sql = "SELECT id, nombre, descripcion FROM departamentos WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new DepartamentoDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                );
            }
        }
        return null;
    }

    @Override
    public void agregar(DepartamentoDTO departamento) throws SQLException {
        String sql = "INSERT INTO departamentos(nombre, descripcion) VALUES(?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, departamento.getNombre());
            pstmt.setString(2, departamento.getDescripcion());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void actualizar(DepartamentoDTO departamento) throws SQLException {
        String sql = "UPDATE departamentos SET nombre = ?, descripcion = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, departamento.getNombre());
            pstmt.setString(2, departamento.getDescripcion());
            pstmt.setInt(3, departamento.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM departamentos WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    @Override
    public boolean existeNombre(String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM departamentos WHERE nombre = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
}