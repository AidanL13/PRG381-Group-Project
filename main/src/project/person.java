package project;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Mzing
 */
public abstract class person {
 
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
 
    public person() {
    }
 
    public person(int id, String firstName, String lastName, String email, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
 
    // ---------- Getters & Setters (Encapsulation) ----------
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public String getFirstName() {
        return firstName;
    }
 
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
 
    public String getLastName() {
        return lastName;
    }
 
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getPhone() {
        return phone;
    }
 
    public void setPhone(String phone) {
        this.phone = phone;
    }
 
    public String getFullName() {
        return firstName + " " + lastName;
    }
 
    /**
     * Abstract method - every subclass MUST provide its own version.
     * This is what enables polymorphism: calling describeRole() on a
     * Person reference will run the correct subclass's version at
     * runtime, whether it's a User or a Cleaner underneath.
     */
    public abstract String describeRole();
 
    @Override
    public String toString() {
        return getFullName() + " (" + email + ")";
    }
}
