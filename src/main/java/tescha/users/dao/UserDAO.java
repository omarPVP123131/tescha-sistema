package tescha.users.dao;

import tescha.users.dto.UserDTO;
import java.util.List;

public interface UserDAO {
    List<UserDTO> getAllUsers();
    UserDTO getUserById(int id);
    UserDTO getUserByUsername(String username);
    boolean insertUser(UserDTO user);
    boolean updateUser(UserDTO user);
    boolean deleteUser(int id);
    boolean toggleUserStatus(int id, boolean activo);
    List<UserDTO> filterUsers(String searchTerm);
    boolean usernameExists(String username);
    boolean updatePassword(int userId, String newPassword);
}