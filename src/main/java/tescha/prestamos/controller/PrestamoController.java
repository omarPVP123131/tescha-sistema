package tescha.prestamos.controller;

import tescha.database.DatabaseManager;
import tescha.prestamos.dto.PrestamoDTO;
import tescha.prestamos.dto.PrestamoDetalleDTO;
import tescha.prestamos.service.PrestamoService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class PrestamoController {
    private PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }
    public Connection getConnection() throws SQLException {
        return DatabaseManager.connect();
    }

    public int crearPrestamo(PrestamoDTO prestamo) {
        return prestamoService.crearPrestamo(prestamo);
    }

    public boolean actualizarPrestamo(PrestamoDTO prestamo) {
        return prestamoService.actualizarPrestamo(prestamo);
    }

    public boolean eliminarPrestamo(int id) {
        return prestamoService.eliminarPrestamo(id);
    }

    public PrestamoDTO obtenerPrestamo(int id) {
        return prestamoService.obtenerPrestamo(id);
    }

    public List<PrestamoDTO> listarTodosLosPrestamos() {
        return prestamoService.listarTodosLosPrestamos();
    }

    public List<PrestamoDTO> listarPrestamosActivos() {
        return prestamoService.listarPrestamosActivos();
    }

    public boolean marcarComoDevuelto(int idPrestamo) {
        return prestamoService.marcarComoDevuelto(idPrestamo);
    }

    public int agregarEquipoAPrestamo(PrestamoDetalleDTO detalle) {
        return prestamoService.agregarEquipoAPrestamo(detalle);
    }

    public boolean eliminarEquipoDePrestamo(int idDetalle) {
        return prestamoService.eliminarEquipoDePrestamo(idDetalle);
    }

    public List<PrestamoDetalleDTO> obtenerEquiposDePrestamo(int idPrestamo) {
        return prestamoService.obtenerEquiposDePrestamo(idPrestamo);
    }
}