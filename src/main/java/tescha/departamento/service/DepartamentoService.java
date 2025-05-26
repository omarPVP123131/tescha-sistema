package tescha.departamento.service;

import tescha.departamento.dao.DepartamentoDAO;
import tescha.departamento.dto.DepartamentoDTO;
import java.sql.SQLException;
import java.util.List;

public class DepartamentoService {
    private DepartamentoDAO departamentoDAO;

    public DepartamentoService(DepartamentoDAO departamentoDAO) {
        this.departamentoDAO = departamentoDAO;
    }

    public List<DepartamentoDTO> obtenerTodosLosDepartamentos() throws SQLException {
        return departamentoDAO.obtenerTodos();
    }

    public DepartamentoDTO obtenerDepartamentoPorId(int id) throws SQLException {
        return departamentoDAO.obtenerPorId(id);
    }

    public void agregarDepartamento(DepartamentoDTO departamento) throws SQLException {
        if (departamentoDAO.existeNombre(departamento.getNombre())) {
            throw new IllegalArgumentException("Ya existe un departamento con ese nombre");
        }
        departamentoDAO.agregar(departamento);
    }

    public void actualizarDepartamento(DepartamentoDTO departamento) throws SQLException {
        DepartamentoDTO existente = departamentoDAO.obtenerPorId(departamento.getId());

        if (existente == null) {
            throw new IllegalArgumentException("Departamento no encontrado");
        }

        // Solo verificar nombre si ha cambiado
        if (!existente.getNombre().equals(departamento.getNombre())) {
            if (departamentoDAO.existeNombre(departamento.getNombre())) {
                throw new IllegalArgumentException("Ya existe un departamento con ese nombre");
            }
        }

        departamentoDAO.actualizar(departamento);
    }


    public void eliminarDepartamento(int id) throws SQLException {
        departamentoDAO.eliminar(id);
    }
}