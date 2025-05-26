package tescha.Login.dto;

public class UserDTO {
    private String username;
    private String password;
    private String role;
    private String last_access;

    public UserDTO(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.last_access = null;
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getLast_access() { return last_access; }

    // Setter
    public void setLast_access(String last_access) {
        this.last_access = last_access;
    }
}