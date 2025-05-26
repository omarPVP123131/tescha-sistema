package tescha.departamento.dao;

import tescha.departamento.dto.DepartamentoDTO;
import java.sql.SQLException;
import java.util.List;

public interface DepartamentoDAO {
    List<DepartamentoDTO> obtenerTodos() throws SQLException;
    DepartamentoDTO obtenerPorId(int id) throws SQLException;
    void agregar(DepartamentoDTO departamento) throws SQLException;
    void actualizar(DepartamentoDTO departamento) throws SQLException;
    void eliminar(int id) throws SQLException;
    boolean existeNombre(String nombre) throws SQLException;
}