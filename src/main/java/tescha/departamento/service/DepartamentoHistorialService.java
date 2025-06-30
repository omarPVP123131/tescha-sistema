package tescha.departamento.service;

import tescha.database.DatabaseManager;
import tescha.departamento.dao.DepartamentoHistorialDAO;
import tescha.departamento.dto.DepartamentoHistorialDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Servicio para manejar el historial de cambios de departamentos.
 */
public class DepartamentoHistorialService {
    private final DepartamentoHistorialDAO historialDAO;

    public DepartamentoHistorialService(DepartamentoHistorialDAO historialDAO) {
        this.historialDAO = historialDAO;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseManager.connect();
    }

    /**
     * Registra un nuevo cambio en el historial.
     * @param entry información del cambio a registrar.
     * @throws SQLException en caso de error de base de datos.
     */
    public void registrarCambio(DepartamentoHistorialDTO entry) throws SQLException {
        try (Connection conn = getConnection()) {
            historialDAO.registrarCambio(conn, entry);
        }
    }

    /**
     * Obtiene todo el historial de cambios para un departamento.
     * @param departamentoId ID del departamento.
     * @return lista de cambios.
     * @throws SQLException en caso de error de base de datos.
     */
    public List<DepartamentoHistorialDTO> obtenerHistorialPorDepartamento(int departamentoId) throws SQLException {
        try (Connection conn = getConnection()) {
            return historialDAO.obtenerPorDepartamento(conn, departamentoId);
        }
    }

    /**
     * Obtiene todo el historial de cambios.
     * @return lista completa del historial.
     * @throws SQLException en caso de error de base de datos.
     */
    public List<DepartamentoHistorialDTO> obtenerTodoElHistorial() throws SQLException {
        try (Connection conn = getConnection()) {
            return historialDAO.obtenerTodos(conn);
        }
    }

    /**
     * Devuelve el usuario actual que realiza cambios.
     * Implementar según contexto de autenticación de tu aplicación.
     */
    public String getUsuarioActual() {
        // TODO: Integrar con el sistema de autenticación real.
        return "sistema";
    }
}
