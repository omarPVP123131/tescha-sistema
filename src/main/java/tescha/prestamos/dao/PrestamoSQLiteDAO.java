package tescha.prestamos.dao;

import tescha.prestamos.dto.PrestamoDTO;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación SQLite del DAO de préstamos
 */
public class PrestamoSQLiteDAO implements PrestamoDAO {

    private final Connection connection;

    public PrestamoSQLiteDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int crearPrestamo(PrestamoDTO prestamo) throws SQLException {
        String sql = """
            INSERT INTO prestamos (
                fecha, hora, fecha_devolucion, solicitante_id, id_equipo, cantidad,
                comentarios, condiciones, entrega, entregado_por, devuelto,
                fecha_devuelto, hora_devuelto, devuelto_por, recibido_por,
                estado_devuelto, tipo_entrega
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, prestamo.getFecha() != null ? prestamo.getFecha().toString() : null);
            stmt.setString(2, prestamo.getHora() != null ? prestamo.getHora().toString() : null);
            stmt.setString(3, prestamo.getFechaDevolucion() != null ? prestamo.getFechaDevolucion().toString() : null);
            stmt.setInt(4, prestamo.getSolicitanteId());
            stmt.setInt(5, prestamo.getIdEquipo());
            stmt.setInt(6, prestamo.getCantidad());
            stmt.setString(7, prestamo.getComentarios());
            stmt.setString(8, prestamo.getCondiciones());
            stmt.setString(9, prestamo.getEntrega());
            stmt.setString(10, prestamo.getEntregadoPor());
            stmt.setInt(11, prestamo.isDevuelto() ? 1 : 0);
            stmt.setString(12, prestamo.getFechaDevuelto() != null ? prestamo.getFechaDevuelto().toString() : null);
            stmt.setString(13, prestamo.getHoraDevuelto() != null ? prestamo.getHoraDevuelto().toString() : null);
            stmt.setString(14, prestamo.getDevueltoPor());
            stmt.setString(15, prestamo.getRecibidoPor());
            stmt.setString(16, prestamo.getEstadoDevuelto());
            stmt.setString(17, prestamo.getTipoEntrega());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Error al crear préstamo, no se insertaron filas.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Error al crear préstamo, no se obtuvo ID.");
                }
            }
        }
    }

    @Override
    public PrestamoDTO obtenerPrestamoPorId(int id) throws SQLException {
        String sql = """
            SELECT p.*, u.nombre as solicitante_nombre, u.username as solicitante_username,
                   i.nombre as equipo_nombre, i.marca as equipo_marca, i.modelo as equipo_modelo
            FROM prestamos p
            LEFT JOIN usuarios u ON p.solicitante_id = u.id
            LEFT JOIN inventario i ON p.id_equipo = i.id
            WHERE p.id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPrestamo(rs);
                }
                return null;
            }
        }
    }

    @Override
    public List<PrestamoDTO> obtenerTodosLosPrestamos() throws SQLException {
        String sql = """
            SELECT p.*, u.nombre as solicitante_nombre, u.username as solicitante_username,
                   i.nombre as equipo_nombre, i.marca as equipo_marca, i.modelo as equipo_modelo
            FROM prestamos p
            LEFT JOIN usuarios u ON p.solicitante_id = u.id
            LEFT JOIN inventario i ON p.id_equipo = i.id
            ORDER BY p.fecha DESC, p.hora DESC
        """;

        List<PrestamoDTO> prestamos = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                prestamos.add(mapResultSetToPrestamo(rs));
            }
        }

        return prestamos;
    }

    @Override
    public boolean actualizarPrestamo(PrestamoDTO prestamo) throws SQLException {
        String sql = """
            UPDATE prestamos SET
                fecha = ?, hora = ?, fecha_devolucion = ?, solicitante_id = ?,
                id_equipo = ?, cantidad = ?, comentarios = ?, condiciones = ?,
                entrega = ?, entregado_por = ?, devuelto = ?, fecha_devuelto = ?,
                hora_devuelto = ?, devuelto_por = ?, recibido_por = ?,
                estado_devuelto = ?, tipo_entrega = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, prestamo.getFecha() != null ? prestamo.getFecha().toString() : null);
            stmt.setString(2, prestamo.getHora() != null ? prestamo.getHora().toString() : null);
            stmt.setString(3, prestamo.getFechaDevolucion() != null ? prestamo.getFechaDevolucion().toString() : null);
            stmt.setInt(4, prestamo.getSolicitanteId());
            stmt.setInt(5, prestamo.getIdEquipo());
            stmt.setInt(6, prestamo.getCantidad());
            stmt.setString(7, prestamo.getComentarios());
            stmt.setString(8, prestamo.getCondiciones());
            stmt.setString(9, prestamo.getEntrega());
            stmt.setString(10, prestamo.getEntregadoPor());
            stmt.setInt(11, prestamo.isDevuelto() ? 1 : 0);
            stmt.setString(12, prestamo.getFechaDevuelto() != null ? prestamo.getFechaDevuelto().toString() : null);
            stmt.setString(13, prestamo.getHoraDevuelto() != null ? prestamo.getHoraDevuelto().toString() : null);
            stmt.setString(14, prestamo.getDevueltoPor());
            stmt.setString(15, prestamo.getRecibidoPor());
            stmt.setString(16, prestamo.getEstadoDevuelto());
            stmt.setString(17, prestamo.getTipoEntrega());
            stmt.setInt(18, prestamo.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminarPrestamo(int id) throws SQLException {
        String sql = "DELETE FROM prestamos WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean marcarComoDevuelto(int prestamoId, String devueltoPor, String recibidoPor, String estadoDevuelto) throws SQLException {
        String sql = """
            UPDATE prestamos SET
                devuelto = 1,
                fecha_devuelto = ?,
                hora_devuelto = ?,
                devuelto_por = ?,
                recibido_por = ?,
                estado_devuelto = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, LocalDate.now().toString());
            stmt.setString(2, LocalTime.now().toString());
            stmt.setString(3, devueltoPor);
            stmt.setString(4, recibidoPor);
            stmt.setString(5, estadoDevuelto);
            stmt.setInt(6, prestamoId);

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public List<PrestamoDTO> obtenerPrestamosActivos() throws SQLException {
        String sql = """
            SELECT p.*, u.nombre as solicitante_nombre, u.username as solicitante_username,
                   i.nombre as equipo_nombre, i.marca as equipo_marca, i.modelo as equipo_modelo
            FROM prestamos p
            LEFT JOIN usuarios u ON p.solicitante_id = u.id
            LEFT JOIN inventario i ON p.id_equipo = i.id
            WHERE p.devuelto = 0
            ORDER BY p.fecha_devolucion ASC
        """;

        return executeQueryAndMapResults(sql);
    }

    @Override
    public List<PrestamoDTO> obtenerPrestamosVencidos() throws SQLException {
        String sql = """
            SELECT p.*, u.nombre as solicitante_nombre, u.username as solicitante_username,
                   i.nombre as equipo_nombre, i.marca as equipo_marca, i.modelo as equipo_modelo
            FROM prestamos p
            LEFT JOIN usuarios u ON p.solicitante_id = u.id
            LEFT JOIN inventario i ON p.id_equipo = i.id
            WHERE p.devuelto = 0 AND p.fecha_devolucion < ?
            ORDER BY p.fecha_devolucion ASC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, LocalDate.now().toString());
            return executeQueryAndMapResults(stmt);
        }
    }

    @Override
    public List<PrestamoDTO> obtenerPrestamosDevueltos() throws SQLException {
        String sql = """
            SELECT p.*, u.nombre as solicitante_nombre, u.username as solicitante_username,
                   i.nombre as equipo_nombre, i.marca as equipo_marca, i.modelo as equipo_modelo
            FROM prestamos p
            LEFT JOIN usuarios u ON p.solicitante_id = u.id
            LEFT JOIN inventario i ON p.id_equipo = i.id
            WHERE p.devuelto = 1
            ORDER BY p.fecha_devuelto DESC
        """;

        return executeQueryAndMapResults(sql);
    }

    @Override
    public List<PrestamoDTO> obtenerPrestamosPorUsuario(int usuarioId) throws SQLException {
        String sql = """
            SELECT p.*, u.nombre as solicitante_nombre, u.username as solicitante_username,
                   i.nombre as equipo_nombre, i.marca as equipo_marca, i.modelo as equipo_modelo
            FROM prestamos p
            LEFT JOIN usuarios u ON p.solicitante_id = u.id
            LEFT JOIN inventario i ON p.id_equipo = i.id
            WHERE p.solicitante_id = ?
            ORDER BY p.fecha DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            return executeQueryAndMapResults(stmt);
        }
    }

    @Override
    public List<PrestamoDTO> obtenerPrestamosPorEquipo(int equipoId) throws SQLException {
        String sql = """
            SELECT p.*, u.nombre as solicitante_nombre, u.username as solicitante_username,
                   i.nombre as equipo_nombre, i.marca as equipo_marca, i.modelo as equipo_modelo
            FROM prestamos p
            LEFT JOIN usuarios u ON p.solicitante_id = u.id
            LEFT JOIN inventario i ON p.id_equipo = i.id
            WHERE p.id_equipo = ?
            ORDER BY p.fecha DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, equipoId);
            return executeQueryAndMapResults(stmt);
        }
    }

    @Override
    public List<PrestamoDTO> obtenerPrestamosQueVencenHoy() throws SQLException {
        String sql = """
            SELECT p.*, u.nombre as solicitante_nombre, u.username as solicitante_username,
                   i.nombre as equipo_nombre, i.marca as equipo_marca, i.modelo as equipo_modelo
            FROM prestamos p
            LEFT JOIN usuarios u ON p.solicitante_id = u.id
            LEFT JOIN inventario i ON p.id_equipo = i.id
            WHERE p.devuelto = 0 AND p.fecha_devolucion = ?
            ORDER BY p.hora ASC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, LocalDate.now().toString());
            return executeQueryAndMapResults(stmt);
        }
    }

    @Override
    public List<PrestamoDTO> obtenerPrestamosPorRangoFecha(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = """
            SELECT p.*, u.nombre as solicitante_nombre, u.username as solicitante_username,
                   i.nombre as equipo_nombre, i.marca as equipo_marca, i.modelo as equipo_modelo
            FROM prestamos p
            LEFT JOIN usuarios u ON p.solicitante_id = u.id
            LEFT JOIN inventario i ON p.id_equipo = i.id
            WHERE p.fecha BETWEEN ? AND ?
            ORDER BY p.fecha DESC
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fechaInicio.toString());
            stmt.setString(2, fechaFin.toString());
            return executeQueryAndMapResults(stmt);
        }
    }

    @Override
    public int contarPrestamosActivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE devuelto = 0";
        return executeCountQuery(sql);
    }

    @Override
    public int contarPrestamosVencidos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE devuelto = 0 AND fecha_devolucion < ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, LocalDate.now().toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public int contarPrestamosDevueltos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE devuelto = 1";
        return executeCountQuery(sql);
    }

    @Override
    public int contarPrestamosPorUsuario(int usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE solicitante_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public boolean equipoDisponible(int equipoId, int cantidadSolicitada) throws SQLException {
        // Obtener cantidad total del equipo
        String sqlInventario = "SELECT cantidad FROM inventario WHERE id = ?";
        int cantidadTotal = 0;

        try (PreparedStatement stmt = connection.prepareStatement(sqlInventario)) {
            stmt.setInt(1, equipoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cantidadTotal = rs.getInt("cantidad");
                }
            }
        }

        // Obtener cantidad prestada actualmente
        int cantidadPrestada = obtenerCantidadPrestadaEquipo(equipoId);

        // Verificar disponibilidad
        return (cantidadTotal - cantidadPrestada) >= cantidadSolicitada;
    }

    @Override
    public boolean usuarioTienePrestamosVencidos(int usuarioId) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM prestamos 
            WHERE solicitante_id = ? AND devuelto = 0 AND fecha_devolucion < ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setString(2, LocalDate.now().toString());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public int obtenerCantidadPrestadaEquipo(int equipoId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(cantidad), 0) FROM prestamos WHERE id_equipo = ? AND devuelto = 0";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, equipoId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    @Override
    public List<PrestamoDTO> buscarPrestamos(String termino) throws SQLException {
        String sql = """
            SELECT p.*, u.nombre as solicitante_nombre, u.username as solicitante_username,
                   i.nombre as equipo_nombre, i.marca as equipo_marca, i.modelo as equipo_modelo
            FROM prestamos p
            LEFT JOIN usuarios u ON p.solicitante_id = u.id
            LEFT JOIN inventario i ON p.id_equipo = i.id
            WHERE LOWER(u.nombre) LIKE LOWER(?) 
               OR LOWER(u.username) LIKE LOWER(?)
               OR LOWER(i.nombre) LIKE LOWER(?)
               OR LOWER(i.marca) LIKE LOWER(?)
               OR LOWER(i.modelo) LIKE LOWER(?)
               OR LOWER(p.comentarios) LIKE LOWER(?)
            ORDER BY p.fecha DESC
        """;

        String searchTerm = "%" + termino + "%";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 6; i++) {
                stmt.setString(i, searchTerm);
            }
            return executeQueryAndMapResults(stmt);
        }
    }

    // Métodos auxiliares privados

    private PrestamoDTO mapResultSetToPrestamo(ResultSet rs) throws SQLException {
        PrestamoDTO prestamo = new PrestamoDTO();

        prestamo.setId(rs.getInt("id"));

        // Fechas y horas
        String fechaStr = rs.getString("fecha");
        if (fechaStr != null) prestamo.setFecha(LocalDate.parse(fechaStr));

        String horaStr = rs.getString("hora");
        if (horaStr != null) prestamo.setHora(LocalTime.parse(horaStr));

        String fechaDevolucionStr = rs.getString("fecha_devolucion");
        if (fechaDevolucionStr != null) prestamo.setFechaDevolucion(LocalDate.parse(fechaDevolucionStr));

        String fechaDevueltoStr = rs.getString("fecha_devuelto");
        if (fechaDevueltoStr != null) prestamo.setFechaDevuelto(LocalDate.parse(fechaDevueltoStr));

        String horaDevueltoStr = rs.getString("hora_devuelto");
        if (horaDevueltoStr != null) prestamo.setHoraDevuelto(LocalTime.parse(horaDevueltoStr));

        // IDs y datos básicos
        prestamo.setSolicitanteId(rs.getInt("solicitante_id"));
        prestamo.setSolicitanteNombre(rs.getString("solicitante_nombre"));
        prestamo.setIdEquipo(rs.getInt("id_equipo"));
        prestamo.setNombreEquipo(rs.getString("equipo_nombre"));
        prestamo.setMarcaEquipo(rs.getString("equipo_marca"));
        prestamo.setModeloEquipo(rs.getString("equipo_modelo"));
        prestamo.setCantidad(rs.getInt("cantidad"));

        // Campos de texto
        prestamo.setComentarios(rs.getString("comentarios"));
        prestamo.setCondiciones(rs.getString("condiciones"));
        prestamo.setEntrega(rs.getString("entrega"));
        prestamo.setEntregadoPor(rs.getString("entregado_por"));
        prestamo.setDevueltoPor(rs.getString("devuelto_por"));
        prestamo.setRecibidoPor(rs.getString("recibido_por"));
        prestamo.setEstadoDevuelto(rs.getString("estado_devuelto"));
        prestamo.setTipoEntrega(rs.getString("tipo_entrega"));

        // Estado de devolución
        prestamo.setDevuelto(rs.getInt("devuelto") == 1);

        return prestamo;
    }

    private List<PrestamoDTO> executeQueryAndMapResults(String sql) throws SQLException {
        List<PrestamoDTO> prestamos = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                prestamos.add(mapResultSetToPrestamo(rs));
            }
        }
        return prestamos;
    }

    private List<PrestamoDTO> executeQueryAndMapResults(PreparedStatement stmt) throws SQLException {
        List<PrestamoDTO> prestamos = new ArrayList<>();
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                prestamos.add(mapResultSetToPrestamo(rs));
            }
        }
        return prestamos;
    }

    private int executeCountQuery(String sql) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}