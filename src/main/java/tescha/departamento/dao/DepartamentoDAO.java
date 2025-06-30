package tescha.departamento.dao;

import tescha.departamento.dto.DepartamentoDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DepartamentoDAO {
    List<DepartamentoDTO> obtenerTodos(Connection connection) throws SQLException;
    DepartamentoDTO obtenerPorId(Connection connection, int id) throws SQLException;
    int agregar(Connection connection, DepartamentoDTO departamento) throws SQLException;
    void actualizar(Connection connection, DepartamentoDTO departamento) throws SQLException;
    void eliminar(Connection connection, int id) throws SQLException;
    boolean existeNombre(Connection connection, String nombre) throws SQLException;
}