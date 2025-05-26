package tescha.departamento.controller;

import tescha.departamento.service.DepartamentoService;
import tescha.departamento.dto.DepartamentoDTO;
import java.sql.SQLException;
import java.util.List;

public class DepartamentoController {
    private DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    public List<DepartamentoDTO> obtenerTodosLosDepartamentos() throws SQLException {
        return departamentoService.obtenerTodosLosDepartamentos();
    }

    public DepartamentoDTO obtenerDepartamentoPorId(int id) throws SQLException {
        return departamentoService.obtenerDepartamentoPorId(id);
    }

    public void agregarDepartamento(DepartamentoDTO departamento) throws SQLException {
        departamentoService.agregarDepartamento(departamento);
    }

    public void actualizarDepartamento(DepartamentoDTO departamento) throws SQLException {
        departamentoService.actualizarDepartamento(departamento);
    }

    public void eliminarDepartamento(int id) throws SQLException {
        departamentoService.eliminarDepartamento(id);
    }
}