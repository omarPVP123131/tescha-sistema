package tescha.prestamos.dao;

import tescha.prestamos.dto.PrestamoDTO;
import tescha.prestamos.dto.PrestamoDetalleDTO;

import java.sql.Connection;
import java.util.List;

public interface PrestamoDAO {
    Connection getConnection();
    int insertarPrestamo(PrestamoDTO prestamo);
    boolean actualizarPrestamo(PrestamoDTO prestamo);
    boolean eliminarPrestamo(int id);
    tescha.prestamos.dto.PrestamoDTO obtenerPrestamoPorId(int id);
    List<PrestamoDTO> obtenerTodosLosPrestamos();
    List<PrestamoDTO> obtenerPrestamosActivos();
    boolean marcarComoDevuelto(int idPrestamo);

    // Métodos para el detalle
    int insertarDetallePrestamo(PrestamoDetalleDTO detalle);
    boolean eliminarDetallePrestamo(int idDetalle);
    List<PrestamoDetalleDTO> obtenerDetallesPorPrestamo(int idPrestamo);
}