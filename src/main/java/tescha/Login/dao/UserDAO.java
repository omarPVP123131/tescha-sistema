package tescha.Login.dao;

import tescha.Login.dto.UserDTO;
import java.util.List;

public interface UserDAO {
    List<UserDTO> getAllUsers();
    UserDTO getUserByUsername(String username);
    boolean addUser(UserDTO user);
    boolean updateUser(UserDTO user);
    boolean deleteUser(String username);
}