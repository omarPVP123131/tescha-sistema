package tescha.inventario.controller;

import tescha.inventario.dto.CategoriaDTO;
import tescha.inventario.dto.EquipoDTO;
import tescha.inventario.service.InventarioService;
import java.util.List;

public class InventarioController {
    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    // Métodos para equipos
    public void agregarEquipo(EquipoDTO equipo) {
        inventarioService.agregarEquipo(equipo);
    }

    public void actualizarEquipo(EquipoDTO equipo) {
        inventarioService.actualizarEquipo(equipo);
    }

    public void eliminarEquipo(int id) {
        inventarioService.eliminarEquipo(id);
    }

    public EquipoDTO obtenerEquipo(int id) {
        return inventarioService.obtenerEquipoPorId(id);
    }

    public List<EquipoDTO> listarEquipos() {
        return inventarioService.obtenerTodosLosEquipos();
    }

    public List<EquipoDTO> buscarEquipos(String criterio) {
        return inventarioService.buscarEquipos(criterio);
    }

    public List<EquipoDTO> buscarEquiposPorCategoria(String categoria) {
        return inventarioService.buscarPorCategoria(categoria);
    }

    public List<EquipoDTO> listarStockBajo() {
        return inventarioService.obtenerEquiposConStockBajo();
    }

    public void registrarMovimiento(int equipoId, String tipo, String descripcion, String usuario) {
        inventarioService.registrarMovimiento(equipoId, tipo, descripcion, usuario);
    }

    public List<String> obtenerHistorial(int equipoId) {
        return inventarioService.obtenerHistorialEquipo(equipoId);
    }

    // Métodos para categorías
    public List<CategoriaDTO> listarCategorias() {
        return inventarioService.obtenerTodasCategorias();
    }

    public boolean agregarCategoria(CategoriaDTO categoria) {
        return inventarioService.agregarCategoria(categoria);
    }

    public boolean actualizarCategoria(CategoriaDTO categoria) {
        return inventarioService.actualizarCategoria(categoria);
    }

    public boolean eliminarCategoria(int id) {
        return inventarioService.eliminarCategoria(id);
    }
}