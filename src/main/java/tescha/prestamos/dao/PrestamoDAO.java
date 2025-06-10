package tescha.prestamos.dao;

import tescha.prestamos.dto.PrestamoDTO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz DAO para operaciones de préstamos
 */
public interface PrestamoDAO {

    // Operaciones CRUD básicas
    int crearPrestamo(PrestamoDTO prestamo) throws SQLException;
    PrestamoDTO obtenerPrestamoPorId(int id) throws SQLException;
    List<PrestamoDTO> obtenerTodosLosPrestamos() throws SQLException;
    boolean actualizarPrestamo(PrestamoDTO prestamo) throws SQLException;
    boolean eliminarPrestamo(int id) throws SQLException;

    // Operaciones específicas de préstamos
    boolean marcarComoDevuelto(int prestamoId, String devueltoPor, String recibidoPor,
                               String estadoDevuelto) throws SQLException;

    // Consultas filtradas
    List<PrestamoDTO> obtenerPrestamosActivos() throws SQLException;
    List<PrestamoDTO> obtenerPrestamosVencidos() throws SQLException;
    List<PrestamoDTO> obtenerPrestamosDevueltos() throws SQLException;
    List<PrestamoDTO> obtenerPrestamosPorUsuario(int usuarioId) throws SQLException;
    List<PrestamoDTO> obtenerPrestamosPorEquipo(int equipoId) throws SQLException;
    List<PrestamoDTO> obtenerPrestamosQueVencenHoy() throws SQLException;
    List<PrestamoDTO> obtenerPrestamosPorRangoFecha(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException;

    // Estadísticas
    int contarPrestamosActivos() throws SQLException;
    int contarPrestamosVencidos() throws SQLException;
    int contarPrestamosDevueltos() throws SQLException;
    int contarPrestamosPorUsuario(int usuarioId) throws SQLException;

    // Validaciones
    boolean equipoDisponible(int equipoId, int cantidadSolicitada) throws SQLException;
    boolean usuarioTienePrestamosVencidos(int usuarioId) throws SQLException;
    int obtenerCantidadPrestadaEquipo(int equipoId) throws SQLException;

    // Búsquedas
    List<PrestamoDTO> buscarPrestamos(String termino) throws SQLException;
}
