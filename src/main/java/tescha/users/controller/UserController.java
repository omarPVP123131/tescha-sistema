package tescha.users.controller;

import tescha.users.dto.UserDTO;
import tescha.users.service.UserService;
import java.util.List;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    public List<UserDTO> loadAllUsers() {
        return userService.getAllUsers();
    }

    public UserDTO getUserById(int id) {
        return userService.getUserById(id);
    }

    public UserDTO getUserByUsername(String username) {
        return userService.getUserByUsername(username);
    }

    public boolean saveUser(UserDTO user) {
        return userService.saveUser(user);
    }

    public boolean deleteUser(int id) {
        return userService.deleteUser(id);
    }

    public boolean toggleUserStatus(int id, boolean activo) {
        return userService.toggleUserStatus(id, activo);
    }

    public List<UserDTO> searchUsers(String term) {
        return userService.searchUsers(term);
    }

    public boolean usernameExists(String username) {
        return userService.usernameExists(username);
    }

    public boolean updatePassword(int userId, String newPassword) {
        return userService.updatePassword(userId, newPassword);
    }

    public boolean validateCredentials(String username, String password) {
        return userService.validateCredentials(username, password);
    }
}