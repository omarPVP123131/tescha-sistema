package tescha.inventario.service;

import tescha.inventario.dao.InventarioDAO;
import tescha.inventario.dto.CategoriaDTO;
import tescha.inventario.dto.EquipoDTO;
import java.util.List;

public class InventarioService {
    private final InventarioDAO inventarioDAO;

    public InventarioService(InventarioDAO inventarioDAO) {
        this.inventarioDAO = inventarioDAO;
    }

    // Métodos para equipos
    public void agregarEquipo(EquipoDTO equipo) {
        inventarioDAO.agregarEquipo(equipo);
    }

    public void actualizarEquipo(EquipoDTO equipo) {
        inventarioDAO.actualizarEquipo(equipo);
    }

    public void eliminarEquipo(int id) {
        inventarioDAO.eliminarEquipo(id);
    }

    public EquipoDTO obtenerEquipoPorId(int id) {
        return inventarioDAO.obtenerEquipoPorId(id);
    }

    public List<EquipoDTO> obtenerTodosLosEquipos() {
        return inventarioDAO.obtenerTodosLosEquipos();
    }

    public List<EquipoDTO> buscarEquipos(String criterio) {
        return inventarioDAO.buscarPorNombre(criterio);
    }

    public List<EquipoDTO> buscarPorCategoria(String categoria) {
        return inventarioDAO.buscarPorCategoria(categoria);
    }

    public List<EquipoDTO> obtenerEquiposConStockBajo() {
        return inventarioDAO.obtenerEquiposConStockBajo();
    }

    public void registrarMovimiento(int equipoId, String tipo, String descripcion, String usuario) {
        inventarioDAO.registrarMovimiento(equipoId, tipo, descripcion, usuario);
    }

    public List<String> obtenerHistorialEquipo(int equipoId) {
        return inventarioDAO.obtenerHistorialEquipo(equipoId);
    }

    // Métodos para categorías
    public List<CategoriaDTO> obtenerTodasCategorias() {
        return inventarioDAO.obtenerTodasCategorias();
    }

    public boolean agregarCategoria(CategoriaDTO categoria) {
        return inventarioDAO.agregarCategoria(categoria);
    }

    public boolean actualizarCategoria(CategoriaDTO categoria) {
        return inventarioDAO.actualizarCategoria(categoria);
    }

    public boolean eliminarCategoria(int id) {
        return inventarioDAO.eliminarCategoria(id);
    }
}