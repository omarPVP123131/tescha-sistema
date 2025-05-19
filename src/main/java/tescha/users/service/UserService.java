package tescha.users.service;

import tescha.users.dao.UserDAO;
import tescha.users.dao.UserSQLiteDAO;
import tescha.users.dto.UserDTO;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public List<UserDTO> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public UserDTO getUserById(int id) {
        return userDAO.getUserById(id);
    }

    public UserDTO getUserByUsername(String username) {
        return userDAO.getUserByUsername(username);
    }

    public boolean saveUser(UserDTO user) {
        if (user.getId() == null) {
            // Validar que el username no exista
            if (userDAO.usernameExists(user.getUsername())) {
                throw new IllegalArgumentException("El nombre de usuario ya existe");
            }
            return userDAO.insertUser(user);
        } else {
            return userDAO.updateUser(user);
        }
    }

    public boolean deleteUser(int id) {
        return userDAO.deleteUser(id);
    }

    public boolean toggleUserStatus(int id, boolean activo) {
        return userDAO.toggleUserStatus(id, activo);
    }

    public List<UserDTO> searchUsers(String term) {
        return userDAO.filterUsers(term);
    }

    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }

    public boolean updatePassword(int userId, String newPassword) {
        return userDAO.updatePassword(userId, newPassword);
    }

    public boolean validateCredentials(String username, String password) {
        UserDTO user = userDAO.getUserByUsername(username);
        if (user == null) return false;
        return user.getPassword().equals(password) && user.isActivo();
    }
}