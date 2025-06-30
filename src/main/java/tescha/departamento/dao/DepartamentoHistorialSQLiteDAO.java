package tescha.departamento.dao;

import tescha.departamento.dto.DepartamentoHistorialDTO;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DepartamentoHistorialSQLiteDAO implements DepartamentoHistorialDAO {
    @Override
    public void registrarCambio(Connection connection, DepartamentoHistorialDTO e) throws SQLException {
        String sql = "INSERT INTO departamentos_historial(departamento_id, nombre, descripcion, estado, fecha_cambio, usuario, accion) "
                + "VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setInt(1, e.getDepartamentoId());
            p.setString(2, e.getNombre());
            p.setString(3, e.getDescripcion());
            p.setString(4, e.getEstado());
            p.setString(5, e.getFechaCambio().toString());
            p.setString(6, e.getUsuario());
            p.setString(7, e.getAccion());
            p.executeUpdate();
        }
    }

    @Override
    public List<DepartamentoHistorialDTO> obtenerPorDepartamento(Connection connection, int deptId) throws SQLException {
        String sql = "SELECT * FROM departamentos_historial WHERE departamento_id=? ORDER BY fecha_cambio DESC";
        List<DepartamentoHistorialDTO> list = new ArrayList<>();
        try (PreparedStatement p = connection.prepareStatement(sql)) {
            p.setInt(1, deptId);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    DepartamentoHistorialDTO e = new DepartamentoHistorialDTO(
                            rs.getInt("id"),
                            rs.getInt("departamento_id"),
                            rs.getString("nombre"),
                            rs.getString("descripcion"),
                            rs.getString("estado"),
                            LocalDateTime.parse(rs.getString("fecha_cambio")),
                            rs.getString("usuario"),
                            rs.getString("accion")
                    );
                    list.add(e);
                }
            }
        }
        return list;
    }

    @Override
    public List<DepartamentoHistorialDTO> obtenerTodos(Connection connection) throws SQLException {
        String sql = "SELECT * FROM departamentos_historial ORDER BY fecha_cambio DESC";
        List<DepartamentoHistorialDTO> list = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new DepartamentoHistorialDTO(
                        rs.getInt("id"),
                        rs.getInt("departamento_id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("estado"),
                        LocalDateTime.parse(rs.getString("fecha_cambio")),
                        rs.getString("usuario"),
                        rs.getString("accion")
                ));
            }
        }
        return list;
    }
}
