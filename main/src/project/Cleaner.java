/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;


public class Cleaner extends person {
 
    private String department; // optional, per the brief
 
    public Cleaner() {
        super();
    }
 
    public Cleaner(int id, String firstName, String lastName, String email, String phone, String department) {
        super(id, firstName, lastName, email, phone);
        this.department = department;
    }
 
    public String getDepartment() {
        return department;
    }
 
    public void setDepartment(String department) {
        this.department = department;
    }
 
    /** Polymorphic override required by the abstract Person class. */
    @Override
    public String describeRole() {
        return "Cleaner" + (department != null ? " assigned to " + department : " (unassigned)");
    }
 
    @Override
    public String toString() {
        return getFullName() + (department != null ? " - " + department : "");
    }
}
