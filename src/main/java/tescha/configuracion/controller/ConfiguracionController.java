package tescha.configuracion.controller;

import tescha.configuracion.service.ConfiguracionService;
import tescha.configuracion.dto.ConfiguracionDTO;
import tescha.configuracion.dto.UsuarioDTO;
import java.util.List;

public class ConfiguracionController {
    private final ConfiguracionService configuracionService;

    public ConfiguracionController(ConfiguracionService configuracionService) {
        this.configuracionService = configuracionService;
    }

    // Métodos para configuración del sistema
    public ConfiguracionDTO obtenerConfiguracion() {
        return configuracionService.obtenerConfiguracion();
    }

    public boolean guardarConfiguracion(ConfiguracionDTO configuracion) {
        return configuracionService.guardarConfiguracion(configuracion);
    }

    // Métodos para gestión de usuarios
    public List<UsuarioDTO> listarUsuarios() {
        return configuracionService.listarUsuarios();
    }

    public UsuarioDTO obtenerUsuario(int id) {
        return configuracionService.obtenerUsuario(id);
    }

    public boolean agregarUsuario(UsuarioDTO usuario) {
        return configuracionService.agregarUsuario(usuario);
    }

    public boolean actualizarUsuario(UsuarioDTO usuario) {
        return configuracionService.actualizarUsuario(usuario);
    }

    public boolean eliminarUsuario(int id) {
        return configuracionService.eliminarUsuario(id);
    }

    public boolean cambiarEstadoUsuario(int id, boolean activo) {
        return configuracionService.cambiarEstadoUsuario(id, activo);
    }
}