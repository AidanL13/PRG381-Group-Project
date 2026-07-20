package project;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Mzing
 */
public class User extends person {
 
    private String username;
    private String password; // should be stored hashed in the database
    private Role role;
 
    public User() {
        super();
    }
 
    public User(int id, String firstName, String lastName, String email, String phone,
                String username, String password, Role role) {
        super(id, firstName, lastName, email, phone);
        this.username = username;
        this.password = password;
        this.role = role;
    }
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getPassword() {
        return password;
    }
 
    public void setPassword(String password) {
        this.password = password;
    }
 
    public Role getRole() {
        return role;
    }
 
    public void setRole(Role role) {
        this.role = role;
    }
 
    public boolean isSupervisor() {
        return role == Role.SUPERVISOR;
    }
 
    /** Polymorphic override required by the abstract Person class. */
    @Override
    public String describeRole() {
        return "Staff member with role: " + role;
    }
 
    @Override
    public String toString() {
        return getFullName() + " [" + username + " - " + role + "]";
    }
}
