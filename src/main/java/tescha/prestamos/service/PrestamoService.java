package tescha.prestamos.service;

import tescha.prestamos.dao.PrestamoDAO;
import tescha.prestamos.dto.PrestamoDTO;
import tescha.prestamos.dto.PrestamoDetalleDTO;

import java.sql.Connection;
import java.util.List;

public class PrestamoService {
    private PrestamoDAO prestamoDAO;

    public PrestamoService(PrestamoDAO prestamoDAO) {
        this.prestamoDAO = prestamoDAO;
    }

    public int crearPrestamo(PrestamoDTO prestamo) {
        return prestamoDAO.insertarPrestamo(prestamo);
    }

    public boolean actualizarPrestamo(PrestamoDTO prestamo) {
        return prestamoDAO.actualizarPrestamo(prestamo);
    }

    public boolean eliminarPrestamo(int id) {
        return prestamoDAO.eliminarPrestamo(id);
    }

    public PrestamoDTO obtenerPrestamo(int id) {
        return prestamoDAO.obtenerPrestamoPorId(id);
    }

    public List<PrestamoDTO> listarTodosLosPrestamos() {
        return prestamoDAO.obtenerTodosLosPrestamos();
    }

    public List<PrestamoDTO> listarPrestamosActivos() {
        return prestamoDAO.obtenerPrestamosActivos();
    }

    public boolean marcarComoDevuelto(int idPrestamo) {
        return prestamoDAO.marcarComoDevuelto(idPrestamo);
    }

    public int agregarEquipoAPrestamo(PrestamoDetalleDTO detalle) {
        return prestamoDAO.insertarDetallePrestamo(detalle);
    }

    public boolean eliminarEquipoDePrestamo(int idDetalle) {
        return prestamoDAO.eliminarDetallePrestamo(idDetalle);
    }

    public List<PrestamoDetalleDTO> obtenerEquiposDePrestamo(int idPrestamo) {
        return prestamoDAO.obtenerDetallesPorPrestamo(idPrestamo);
    }
}