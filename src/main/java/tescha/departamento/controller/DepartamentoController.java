package tescha.departamento.controller;

import tescha.departamento.dto.DepartamentoDTO;
import tescha.departamento.dto.DepartamentoHistorialDTO;
import tescha.departamento.service.DepartamentoService;
import tescha.departamento.service.*;

import java.sql.SQLException;
import java.util.List;

public class DepartamentoController {
    private final DepartamentoService departamentoService;
    private final DepartamentoHistorialService historialService;

    public DepartamentoController(DepartamentoService departamentoService,
                                  DepartamentoHistorialService historialService) {
        this.departamentoService = departamentoService;
        this.historialService   = historialService;
    }

    // ——— Departamentos ———

    public List<DepartamentoDTO> obtenerTodosLosDepartamentos() throws SQLException {
        return departamentoService.obtenerTodosLosDepartamentos();
    }

    public DepartamentoDTO obtenerDepartamentoPorId(int id) throws SQLException {
        return departamentoService.obtenerDepartamentoPorId(id);
    }

    public DepartamentoDTO agregarDepartamento(DepartamentoDTO departamento) throws SQLException {
        // servicio ya asigna estado/fecha por defecto
        DepartamentoDTO departamentoConId = departamentoService.agregarDepartamento(departamento);

        // registrar en historial
        historialService.registrarCambio(
                new DepartamentoHistorialDTO(
                        0,                      // id se autogenera
                        departamentoConId.getId(), // Usamos el ID generado
                        departamentoConId.getNombre(),
                        departamentoConId.getDescripcion(),
                        departamentoConId.getEstado(),
                        departamentoConId.getFecha(),
                        historialService.getUsuarioActual(), // método que devuelva el username
                        "CREATE"
                )
        );

        return departamentoConId; // Devolvemos el DTO con el ID generado
    }
    public void actualizarDepartamento(DepartamentoDTO departamento) throws SQLException {
        // actualiza fecha internamente
        departamentoService.actualizarDepartamento(departamento);
        // registrar en historial
        historialService.registrarCambio(
                new DepartamentoHistorialDTO(
                        0,
                        departamento.getId(),
                        departamento.getNombre(),
                        departamento.getDescripcion(),
                        departamento.getEstado(),
                        departamento.getFecha(),
                        historialService.getUsuarioActual(),
                        "UPDATE"
                )
        );
    }

    public void eliminarDepartamento(int id) throws SQLException {
        DepartamentoDTO antes = departamentoService.obtenerDepartamentoPorId(id);
        departamentoService.eliminarDepartamento(id);
        // registrar en historial
        historialService.registrarCambio(
                new DepartamentoHistorialDTO(
                        0,
                        id,
                        antes.getNombre(),
                        antes.getDescripcion(),
                        antes.getEstado(),
                        java.time.LocalDateTime.now(),
                        historialService.getUsuarioActual(),
                        "DELETE"
                )
        );
    }

    // ——— Historial ———

    /** Devuelve todo el historial de cambios de un departamento */
    public List<DepartamentoHistorialDTO> obtenerHistorial(int departamentoId)
            throws SQLException {
        return historialService.obtenerHistorialPorDepartamento(departamentoId);
    }
}
