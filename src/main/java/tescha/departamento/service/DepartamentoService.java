package tescha.departamento.service;

import tescha.database.DatabaseManager;
import tescha.departamento.dao.DepartamentoDAO;
import tescha.departamento.dao.DepartamentoSQLiteDAO;
import tescha.departamento.dto.DepartamentoDTO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DepartamentoService {
    private DepartamentoDAO departamentoDAO;

    public DepartamentoService(DepartamentoDAO departamentoDAO) {
        this.departamentoDAO = departamentoDAO;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseManager.connect();
    }

    public List<DepartamentoDTO> obtenerTodosLosDepartamentos() throws SQLException {
        try (Connection connection = getConnection()) {
            return departamentoDAO.obtenerTodos(connection);
        }
    }

    public DepartamentoDTO obtenerDepartamentoPorId(int id) throws SQLException {
        try (Connection connection = getConnection()) {
            return departamentoDAO.obtenerPorId(connection, id);
        }
    }

    public DepartamentoDTO agregarDepartamento(DepartamentoDTO departamento) throws SQLException {
        try (Connection connection = getConnection()) {
            if (departamentoDAO.existeNombre(connection, departamento.getNombre())) {
                throw new IllegalArgumentException("Ya existe un departamento con ese nombre");
            }
            if (departamento.getEstado() == null) {
                departamento.setEstado("Activo");
            }
            if (departamento.getFecha() == null) {
                departamento.setFecha(java.time.LocalDateTime.now());
            }

            // Modificamos esta línea para obtener el ID generado
            int idGenerado = departamentoDAO.agregar(connection, departamento);
            departamento.setId(idGenerado);

            return departamento; // Devolvemos el DTO con el ID asignado
        }
    }

    public void actualizarDepartamento(DepartamentoDTO departamento) throws SQLException {
        try (Connection connection = getConnection()) {
            DepartamentoDTO existente = departamentoDAO.obtenerPorId(connection, departamento.getId());

            if (existente == null) {
                throw new IllegalArgumentException("Departamento no encontrado");
            }

            if (!existente.getNombre().equals(departamento.getNombre())) {
                if (departamentoDAO.existeNombre(connection, departamento.getNombre())) {
                    throw new IllegalArgumentException("Ya existe un departamento con ese nombre");
                }
            }

            departamento.setFecha(java.time.LocalDateTime.now());
            departamentoDAO.actualizar(connection, departamento);
        }
    }

    public void eliminarDepartamento(int id) throws SQLException {
        try (Connection connection = getConnection()) {
            departamentoDAO.eliminar(connection, id);
        }
    }
}