package tescha.departamento.dao;

import tescha.departamento.dto.DepartamentoHistorialDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DepartamentoHistorialDAO {
    void registrarCambio(Connection connection, DepartamentoHistorialDTO entry) throws SQLException;
    List<DepartamentoHistorialDTO> obtenerPorDepartamento(Connection connection, int departamentoId) throws SQLException;
    List<DepartamentoHistorialDTO> obtenerTodos(Connection connection) throws SQLException;
}
