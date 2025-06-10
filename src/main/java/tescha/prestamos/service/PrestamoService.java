package tescha.prestamos.service;

import tescha.prestamos.dao.PrestamoDAO;
import tescha.prestamos.dto.PrestamoDTO;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la lógica de negocio de préstamos
 */
public class PrestamoService {
    
    private final PrestamoDAO prestamoDAO;
    
    public PrestamoService(PrestamoDAO prestamoDAO) {
        this.prestamoDAO = prestamoDAO;
    }
    
    // Operaciones CRUD
    public int crearPrestamo(PrestamoDTO prestamo) throws SQLException {
        validarPrestamo(prestamo);
        
        // Verificar disponibilidad del equipo
        if (!prestamoDAO.equipoDisponible(prestamo.getIdEquipo(), prestamo.getCantidad())) {
            throw new IllegalArgumentException("El equipo no tiene suficiente cantidad disponible");
        }
        
        // Verificar que el usuario no tenga préstamos vencidos
        if (prestamoDAO.usuarioTienePrestamosVencidos(prestamo.getSolicitanteId())) {
            throw new IllegalArgumentException("El usuario tiene préstamos vencidos pendientes");
        }
        
        return prestamoDAO.crearPrestamo(prestamo);
    }
    
    public PrestamoDTO obtenerPrestamoPorId(int id) throws SQLException {
        PrestamoDTO prestamo = prestamoDAO.obtenerPrestamoPorId(id);
        if (prestamo == null) {
            throw new IllegalArgumentException("Préstamo no encontrado con ID: " + id);
        }
        return prestamo;
    }
    
    public List<PrestamoDTO> obtenerTodosLosPrestamos() throws SQLException {
        return prestamoDAO.obtenerTodosLosPrestamos();
    }
    
    public boolean actualizarPrestamo(PrestamoDTO prestamo) throws SQLException {
        validarPrestamo(prestamo);
        return prestamoDAO.actualizarPrestamo(prestamo);
    }
    
    public boolean eliminarPrestamo(int id) throws SQLException {
        return prestamoDAO.eliminarPrestamo(id);
    }
    
    // Operaciones específicas
    public boolean procesarDevolucion(int prestamoId, String devueltoPor, String recibidoPor, String estadoDevuelto) throws SQLException {
        if (devueltoPor == null || devueltoPor.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar quién devuelve el equipo");
        }
        if (recibidoPor == null || recibidoPor.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar quién recibe el equipo");
        }
        if (estadoDevuelto == null || estadoDevuelto.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar el estado del equipo devuelto");
        }
        
        return prestamoDAO.marcarComoDevuelto(prestamoId, devueltoPor, recibidoPor, estadoDevuelto);
    }
    
    // Consultas filtradas
    public List<PrestamoDTO> obtenerPrestamosActivos() throws SQLException {
        return prestamoDAO.obtenerPrestamosActivos();
    }
    
    public List<PrestamoDTO> obtenerPrestamosVencidos() throws SQLException {
        return prestamoDAO.obtenerPrestamosVencidos();
    }
    
    public List<PrestamoDTO> obtenerPrestamosDevueltos() throws SQLException {
        return prestamoDAO.obtenerPrestamosDevueltos();
    }
    
    public List<PrestamoDTO> obtenerPrestamosPorUsuario(int usuarioId) throws SQLException {
        return prestamoDAO.obtenerPrestamosPorUsuario(usuarioId);
    }
    
    public List<PrestamoDTO> obtenerPrestamosPorEquipo(int equipoId) throws SQLException {
        return prestamoDAO.obtenerPrestamosPorEquipo(equipoId);
    }
    
    public List<PrestamoDTO> obtenerPrestamosQueVencenHoy() throws SQLException {
        return prestamoDAO.obtenerPrestamosQueVencenHoy();
    }
    
    public List<PrestamoDTO> obtenerPrestamosPorRangoFecha(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return prestamoDAO.obtenerPrestamosPorRangoFecha(fechaInicio, fechaFin);
    }
    
    // Estadísticas
    public EstadisticasPrestamos obtenerEstadisticas() throws SQLException {
        return new EstadisticasPrestamos(
            prestamoDAO.contarPrestamosActivos(),
            prestamoDAO.contarPrestamosVencidos(),
            prestamoDAO.contarPrestamosDevueltos()
        );
    }
    
    // Búsquedas
    public List<PrestamoDTO> buscarPrestamos(String termino) throws SQLException {
        if (termino == null || termino.trim().isEmpty()) {
            return obtenerTodosLosPrestamos();
        }
        return prestamoDAO.buscarPrestamos(termino.trim());
    }
    
    public List<PrestamoDTO> filtrarPorEstado(String estado) throws SQLException {
        switch (estado.toUpperCase()) {
            case "ACTIVO":
                return obtenerPrestamosActivos();
            case "VENCIDO":
                return obtenerPrestamosVencidos();
            case "DEVUELTO":
                return obtenerPrestamosDevueltos();
            case "VENCE_HOY":
                return obtenerPrestamosQueVencenHoy();
            default:
                return obtenerTodosLosPrestamos();
        }
    }
    
    // Validaciones
    private void validarPrestamo(PrestamoDTO prestamo) {
        if (prestamo == null) {
            throw new IllegalArgumentException("El préstamo no puede ser nulo");
        }
        
        if (prestamo.getSolicitanteId() <= 0) {
            throw new IllegalArgumentException("Debe especificar un solicitante válido");
        }
        
        if (prestamo.getIdEquipo() <= 0) {
            throw new IllegalArgumentException("Debe especificar un equipo válido");
        }
        
        if (prestamo.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        
        if (prestamo.getFechaDevolucion() != null && prestamo.getFechaDevolucion().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de devolución no puede ser anterior a hoy");
        }
    }
    
    // Clase interna para estadísticas
    public static class EstadisticasPrestamos {
        private final int activos;
        private final int vencidos;
        private final int devueltos;
        
        public EstadisticasPrestamos(int activos, int vencidos, int devueltos) {
            this.activos = activos;
            this.vencidos = vencidos;
            this.devueltos = devueltos;
        }
        
        public int getActivos() { return activos; }
        public int getVencidos() { return vencidos; }
        public int getDevueltos() { return devueltos; }
        public int getTotal() { return activos + vencidos + devueltos; }
    }
}
