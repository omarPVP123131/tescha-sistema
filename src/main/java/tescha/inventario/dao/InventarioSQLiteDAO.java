package tescha.inventario.dao;

import tescha.inventario.dto.CategoriaDTO;
import tescha.inventario.dto.EquipoDTO;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InventarioSQLiteDAO implements InventarioDAO {
    private Connection connection;

    public InventarioSQLiteDAO(Connection connection) {
        this.connection = connection;
        insertarDatosIniciales();
    }

    private void insertarDatosIniciales() {
        try (Statement stmt = connection.createStatement()) {
            // Insertar categorías por defecto si no existen
            stmt.execute("""
                INSERT OR IGNORE INTO categorias (id, nombre, descripcion, color, icono) 
                VALUES 
                (1, 'Electronica', 'Dispositivos electrónicos', '#3498db', 'desktop'),
                (2, 'Mobiliario', 'Muebles y equipamiento', '#2ecc71', 'chair'),
                (3, 'Herramientas', 'Herramientas de trabajo', '#e74c3c', 'tools'),
                (4, 'Vehiculos', 'Vehiculos y transporte', '#f39c12', 'car')
                """);
        } catch (SQLException e) {
            System.err.println("Error al insertar datos iniciales: " + e.getMessage());
        }
    }

    @Override
    public void agregarEquipo(EquipoDTO equipo) {
        String sql = """
    INSERT INTO inventario (
        nombre, categoria, subcategoria, cantidad, cantidad_minima, status,
        ubicacion, numero_serie, marca, modelo, notas, imagen
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, equipo.getNombre());
            pstmt.setString(2, equipo.getCategoria());
            pstmt.setString(3, equipo.getSubcategoria());
            pstmt.setInt(4, equipo.getCantidad());
            pstmt.setInt(5, equipo.getCantidadMinima());
            pstmt.setString(6, equipo.getStatus());
            pstmt.setString(7, equipo.getUbicacion());
            pstmt.setString(8, equipo.getNumeroSerie());
            pstmt.setString(9, equipo.getMarca());
            pstmt.setString(10, equipo.getModelo());
            pstmt.setString(11, equipo.getNotas());
            pstmt.setString(12, equipo.getImagen());

            pstmt.executeUpdate();

            // Obtener el ID generado
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    equipo.setId(generatedKeys.getInt(1));
                }
            }

            registrarMovimiento(equipo.getId(), "CREACION", "Nuevo equipo agregado", "Sistema");
        } catch (SQLException e) {
            System.err.println("Error al agregar equipo: " + e.getMessage());
            throw new RuntimeException("Failed to add equipment", e);
        }
    }

    @Override
    public void actualizarEquipo(EquipoDTO equipo) {
        String sql = """
        UPDATE inventario SET 
            nombre = ?, categoria = ?, subcategoria = ?, cantidad = ?, cantidad_minima = ?, 
            status = ?, ubicacion = ?, numero_serie = ?, marca = ?, modelo = ?, 
            notas = ?, imagen = ?
        WHERE id = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, equipo.getNombre());
            pstmt.setString(2, equipo.getCategoria());
            pstmt.setString(3, equipo.getSubcategoria());
            pstmt.setInt(4, equipo.getCantidad());
            pstmt.setInt(5, equipo.getCantidadMinima());
            pstmt.setString(6, equipo.getStatus());
            pstmt.setString(7, equipo.getUbicacion());
            pstmt.setString(8, equipo.getNumeroSerie());
            pstmt.setString(9, equipo.getMarca());
            pstmt.setString(10, equipo.getModelo());
            pstmt.setString(11, equipo.getNotas());
            pstmt.setString(12, equipo.getImagen());
            pstmt.setInt(13, equipo.getId());

            pstmt.executeUpdate();

            registrarMovimiento(equipo.getId(), "ACTUALIZACION", "Equipo actualizado", "Sistema");
        } catch (SQLException e) {
            System.err.println("Error al actualizar equipo: " + e.getMessage());
        }
    }



    @Override
    public void eliminarEquipo(int id) {
        String sql = "DELETE FROM inventario WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            registrarMovimiento(id, "ELIMINACION", "Equipo eliminado", "Sistema");
        } catch (SQLException e) {
            System.err.println("Error al eliminar equipo: " + e.getMessage());
        }
    }

    @Override
    public EquipoDTO obtenerEquipoPorId(int id) {
        String sql = "SELECT * FROM inventario WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapearEquipo(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener equipo por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<EquipoDTO> obtenerTodosLosEquipos() {
        List<EquipoDTO> equipos = new ArrayList<>();
        String sql = "SELECT * FROM inventario ORDER BY nombre";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                equipos.add(mapearEquipo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los equipos: " + e.getMessage());
        }
        return equipos;
    }

    @Override
    public List<String> obtenerHistorialReciente(int limit) {
        List<String> historial = new ArrayList<>();
        String sql = """
            SELECT m.fecha, m.hora, m.tipo, m.usuario, m.descripcion, i.nombre as item_nombre 
            FROM mov_hist m
            LEFT JOIN inventario i ON m.id_item = i.id
            ORDER BY m.fecha DESC, m.hora DESC
            LIMIT ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String registro = String.format("[%s %s] %s - %s: %s (%s)",
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getString("tipo"),
                        rs.getString("usuario"),
                        rs.getString("descripcion"),
                        rs.getString("item_nombre"));
                historial.add(registro);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial reciente: " + e.getMessage());
        }
        return historial;
    }
    @Override
    public List<EquipoDTO> buscarPorNombre(String nombre) {
        List<EquipoDTO> equipos = new ArrayList<>();
        String sql = "SELECT * FROM inventario WHERE nombre LIKE ? ORDER BY nombre";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nombre + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                equipos.add(mapearEquipo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar equipos por nombre: " + e.getMessage());
        }
        return equipos;
    }

    @Override
    public List<EquipoDTO> buscarPorCategoria(String categoria) {
        List<EquipoDTO> equipos = new ArrayList<>();
        String sql = "SELECT * FROM inventario WHERE categoria = ? ORDER BY nombre";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categoria);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                equipos.add(mapearEquipo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar equipos por categoría: " + e.getMessage());
        }
        return equipos;
    }

    @Override
    public List<EquipoDTO> obtenerEquiposConStockBajo() {
        List<EquipoDTO> equipos = new ArrayList<>();
        String sql = "SELECT * FROM inventario WHERE cantidad <= cantidad_minima ORDER BY nombre";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                equipos.add(mapearEquipo(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener equipos con stock bajo: " + e.getMessage());
        }
        return equipos;
    }

    @Override
    public void registrarMovimiento(int equipoId, String tipo, String descripcion, String usuario) {
        String sql = "INSERT INTO mov_hist (id_item, tipo, qty, usuario, fecha, hora, descripcion) VALUES (?, ?, ?, ?, date('now'), time('now'), ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, equipoId);
            pstmt.setString(2, tipo);
            pstmt.setInt(3, 0); // Cantidad por defecto
            pstmt.setString(4, usuario);
            pstmt.setString(5, descripcion);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al registrar movimiento: " + e.getMessage());
        }
    }

    @Override
    public List<String> obtenerHistorialEquipo(int equipoId) {
        List<String> historial = new ArrayList<>();
        String sql = "SELECT fecha, hora, tipo, usuario, descripcion FROM mov_hist WHERE id_item = ? ORDER BY fecha DESC, hora DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, equipoId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String registro = String.format("[%s %s] %s - %s: %s",
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getString("tipo"),
                        rs.getString("usuario"),
                        rs.getString("descripcion"));
                historial.add(registro);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial del equipo: " + e.getMessage());
        }
        return historial;
    }


    @Override
    public List<CategoriaDTO> obtenerTodasCategorias() {
        List<CategoriaDTO> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categorias ORDER BY nombre";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                categorias.add(new CategoriaDTO(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("color"),
                        rs.getString("icono")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener categorías: " + e.getMessage());
        }
        return categorias;
    }

    @Override
    public boolean agregarCategoria(CategoriaDTO categoria) {
        String sql = "INSERT INTO categorias (nombre, descripcion, color, icono) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categoria.getNombre());
            pstmt.setString(2, categoria.getDescripcion());
            pstmt.setString(3, categoria.getColor());
            pstmt.setString(4, categoria.getIcono());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar categoría: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarCategoria(CategoriaDTO categoria) {
        String sql = "UPDATE categorias SET nombre = ?, descripcion = ?, color = ?, icono = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, categoria.getNombre());
            pstmt.setString(2, categoria.getDescripcion());
            pstmt.setString(3, categoria.getColor());
            pstmt.setString(4, categoria.getIcono());
            pstmt.setInt(5, categoria.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar categoría: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarCategoria(int id) {
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar categoría: " + e.getMessage());
            return false;
        }
    }

    private EquipoDTO mapearEquipo(ResultSet rs) throws SQLException {
        EquipoDTO equipo = new EquipoDTO();
        equipo.setId(rs.getInt("id"));
        equipo.setNombre(rs.getString("nombre"));
        equipo.setCategoria(rs.getString("categoria"));
        equipo.setSubcategoria(rs.getString("subcategoria"));
        equipo.setCantidad(rs.getInt("cantidad"));
        equipo.setCantidadMinima(rs.getInt("cantidad_minima"));
        equipo.setStatus(rs.getString("status"));
        equipo.setUbicacion(rs.getString("ubicacion"));
        equipo.setNumeroSerie(rs.getString("numero_serie"));
        equipo.setMarca(rs.getString("marca"));
        equipo.setModelo(rs.getString("modelo"));
        equipo.setNotas(rs.getString("notas"));
        equipo.setImagen(rs.getString("imagen"));
        return equipo;
    }
}