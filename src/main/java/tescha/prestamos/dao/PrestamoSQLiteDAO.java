package tescha.prestamos.dao;

import tescha.prestamos.dto.PrestamoDTO;
import tescha.prestamos.dto.PrestamoDetalleDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoSQLiteDAO implements PrestamoDAO {
    private Connection connection;

    public PrestamoSQLiteDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public int insertarPrestamo(PrestamoDTO prestamo) {
        String sql = "INSERT INTO prestamos(fecha_prestamo, fecha_devolucion, solicitante, " +
                "devuelto, fecha_devuelto, comentarios) VALUES(?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, prestamo.getFechaPrestamo().toString());
            pstmt.setString(2, prestamo.getFechaDevolucion() != null ?
                    prestamo.getFechaDevolucion().toString() : null);
            pstmt.setString(3, prestamo.getSolicitante());
            pstmt.setInt(4, prestamo.isDevuelto() ? 1 : 0);
            pstmt.setString(5, prestamo.getFechaDevuelto() != null ?
                    prestamo.getFechaDevuelto().toString() : null);
            pstmt.setString(6, prestamo.getComentarios());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar préstamo: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean actualizarPrestamo(PrestamoDTO prestamo) {
        String sql = "UPDATE prestamos SET fecha_prestamo = ?, fecha_devolucion = ?, " +
                "solicitante = ?, devuelto = ?, fecha_devuelto = ?, comentarios = ? " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, prestamo.getFechaPrestamo().toString());
            pstmt.setString(2, prestamo.getFechaDevolucion() != null ?
                    prestamo.getFechaDevolucion().toString() : null);
            pstmt.setString(3, prestamo.getSolicitante());
            pstmt.setInt(4, prestamo.isDevuelto() ? 1 : 0);
            pstmt.setString(5, prestamo.getFechaDevuelto() != null ?
                    prestamo.getFechaDevuelto().toString() : null);
            pstmt.setString(6, prestamo.getComentarios());
            pstmt.setInt(7, prestamo.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar préstamo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public PrestamoDTO obtenerPrestamoPorId(int id) {
        String sql = "SELECT * FROM prestamos WHERE id = ?";
        PrestamoDTO prestamo = null;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    prestamo = new PrestamoDTO();
                    prestamo.setId(rs.getInt("id"));
                    prestamo.setFechaPrestamo(LocalDate.parse(rs.getString("fecha_prestamo")));
                    prestamo.setFechaDevolucion(rs.getString("fecha_devolucion") != null ?
                            LocalDate.parse(rs.getString("fecha_devolucion")) : null);
                    prestamo.setSolicitante(rs.getString("solicitante"));
                    prestamo.setDevuelto(rs.getInt("devuelto") == 1);
                    prestamo.setFechaDevuelto(rs.getString("fecha_devuelto") != null ?
                            LocalDate.parse(rs.getString("fecha_devuelto")) : null);
                    prestamo.setComentarios(rs.getString("comentarios"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener préstamo por ID: " + e.getMessage());
        }
        return prestamo;
    }
    @Override
    public boolean eliminarPrestamo(int id) {
        String sql = "DELETE FROM prestamos WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar préstamo: " + e.getMessage());
            return false;
        }
    }


    @Override
    public List<PrestamoDTO> obtenerTodosLosPrestamos() {
        List<PrestamoDTO> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos ORDER BY fecha_prestamo DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PrestamoDTO prestamo = new PrestamoDTO();
                prestamo.setId(rs.getInt("id"));
                prestamo.setFechaPrestamo(LocalDate.parse(rs.getString("fecha_prestamo")));
                prestamo.setFechaDevolucion(rs.getString("fecha_devolucion") != null ?
                        LocalDate.parse(rs.getString("fecha_devolucion")) : null);
                prestamo.setSolicitante(rs.getString("solicitante"));
                prestamo.setDevuelto(rs.getInt("devuelto") == 1);
                prestamo.setFechaDevuelto(rs.getString("fecha_devuelto") != null ?
                        LocalDate.parse(rs.getString("fecha_devuelto")) : null);
                prestamo.setComentarios(rs.getString("comentarios"));

                prestamos.add(prestamo);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los préstamos: " + e.getMessage());
        }
        return prestamos;
    }

    @Override
    public List<PrestamoDTO> obtenerPrestamosActivos() {
        List<PrestamoDTO> prestamos = new ArrayList<>();
        String sql = "SELECT * FROM prestamos WHERE devuelto = 0 ORDER BY fecha_prestamo DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PrestamoDTO prestamo = new PrestamoDTO();
                prestamo.setId(rs.getInt("id"));
                prestamo.setFechaPrestamo(LocalDate.parse(rs.getString("fecha_prestamo")));
                prestamo.setFechaDevolucion(rs.getString("fecha_devolucion") != null ?
                        LocalDate.parse(rs.getString("fecha_devolucion")) : null);
                prestamo.setSolicitante(rs.getString("solicitante"));
                prestamo.setDevuelto(rs.getInt("devuelto") == 1);
                prestamo.setFechaDevuelto(rs.getString("fecha_devuelto") != null ?
                        LocalDate.parse(rs.getString("fecha_devuelto")) : null);
                prestamo.setComentarios(rs.getString("comentarios"));

                prestamos.add(prestamo);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener préstamos activos: " + e.getMessage());
        }
        return prestamos;
    }

    @Override
    public boolean marcarComoDevuelto(int idPrestamo) {
        String sql = "UPDATE prestamos SET devuelto = 1, fecha_devuelto = ? WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, LocalDate.now().toString());
            pstmt.setInt(2, idPrestamo);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al marcar préstamo como devuelto: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int insertarDetallePrestamo(PrestamoDetalleDTO detalle) {
        String sql = "INSERT INTO prestamos_detalle(prestamo_id, id_equipo, cantidad) VALUES(?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, detalle.getPrestamoId());
            pstmt.setInt(2, detalle.getIdEquipo());
            pstmt.setInt(3, detalle.getCantidad());

            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar detalle de préstamo: " + e.getMessage());
        }
        return -1;
    }

    @Override
    public boolean eliminarDetallePrestamo(int idDetalle) {
        String sql = "DELETE FROM prestamos_detalle WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idDetalle);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar detalle de préstamo: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<PrestamoDetalleDTO> obtenerDetallesPorPrestamo(int idPrestamo) {
        List<PrestamoDetalleDTO> detalles = new ArrayList<>();
        String sql = "SELECT * FROM prestamos_detalle WHERE prestamo_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, idPrestamo);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PrestamoDetalleDTO detalle = new PrestamoDetalleDTO();
                    detalle.setId(rs.getInt("id"));
                    detalle.setPrestamoId(rs.getInt("prestamo_id"));
                    detalle.setIdEquipo(rs.getInt("id_equipo"));
                    detalle.setCantidad(rs.getInt("cantidad"));

                    detalles.add(detalle);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles de préstamo: " + e.getMessage());
        }
        return detalles;
    }
}