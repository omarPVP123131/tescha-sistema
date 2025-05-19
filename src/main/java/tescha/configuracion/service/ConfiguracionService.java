package tescha.configuracion.service;

import tescha.configuracion.dao.ConfiguracionDAO;
import tescha.configuracion.dto.ConfiguracionDTO;
import tescha.configuracion.dto.UsuarioDTO;
import java.util.List;

public class ConfiguracionService {
    private final ConfiguracionDAO configuracionDAO;

    public ConfiguracionService(ConfiguracionDAO configuracionDAO) {
        this.configuracionDAO = configuracionDAO;
    }

    // Métodos para configuración del sistema
    public ConfiguracionDTO obtenerConfiguracion() {
        return configuracionDAO.obtenerConfiguracion();
    }

    public boolean guardarConfiguracion(ConfiguracionDTO configuracion) {
        return configuracionDAO.guardarConfiguracion(configuracion);
    }

    // Métodos para gestión de usuarios
    public List<UsuarioDTO> listarUsuarios() {
        return configuracionDAO.listarUsuarios();
    }

    public UsuarioDTO obtenerUsuario(int id) {
        return configuracionDAO.obtenerUsuario(id);
    }

    public boolean agregarUsuario(UsuarioDTO usuario) {
        return configuracionDAO.agregarUsuario(usuario);
    }

    public boolean actualizarUsuario(UsuarioDTO usuario) {
        return configuracionDAO.actualizarUsuario(usuario);
    }

    public boolean eliminarUsuario(int id) {
        return configuracionDAO.eliminarUsuario(id);
    }

    public boolean cambiarEstadoUsuario(int id, boolean activo) {
        return configuracionDAO.cambiarEstadoUsuario(id, activo);
    }
}