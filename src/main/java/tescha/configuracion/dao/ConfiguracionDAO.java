package tescha.configuracion.dao;

import tescha.configuracion.dto.ConfiguracionDTO;
import tescha.configuracion.dto.UsuarioDTO;
import java.util.List;

public interface ConfiguracionDAO {
    // Métodos para configuración del sistema
    ConfiguracionDTO obtenerConfiguracion();
    boolean guardarConfiguracion(ConfiguracionDTO configuracion);

    // Métodos para gestión de usuarios
    List<UsuarioDTO> listarUsuarios();
    UsuarioDTO obtenerUsuario(int id);
    boolean agregarUsuario(UsuarioDTO usuario);
    boolean actualizarUsuario(UsuarioDTO usuario);
    boolean eliminarUsuario(int id);
    boolean cambiarEstadoUsuario(int id, boolean activo);
}