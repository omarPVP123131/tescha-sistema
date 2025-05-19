package tescha.inventario.dao;

import tescha.inventario.dto.CategoriaDTO;
import tescha.inventario.dto.EquipoDTO;
import java.util.List;

public interface InventarioDAO {
    // Operaciones CRUD para equipos
    void agregarEquipo(EquipoDTO equipo);
    void actualizarEquipo(EquipoDTO equipo);
    void eliminarEquipo(int id);
    EquipoDTO obtenerEquipoPorId(int id);
    List<EquipoDTO> obtenerTodosLosEquipos();
    List<EquipoDTO> buscarPorNombre(String nombre);
    List<EquipoDTO> buscarPorCategoria(String categoria);
    List<EquipoDTO> obtenerEquiposConStockBajo();
    void registrarMovimiento(int equipoId, String tipo, String descripcion, String usuario);
    List<String> obtenerHistorialEquipo(int equipoId);

    // Operaciones CRUD para categorías
    List<CategoriaDTO> obtenerTodasCategorias();
    boolean agregarCategoria(CategoriaDTO categoria);
    boolean actualizarCategoria(CategoriaDTO categoria);
    boolean eliminarCategoria(int id);
}