/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;


public class Cleaner {
    
    private String cleanerID;
    private String name;
    private String surname;
    private String department;
    
    //Constructer
    public Cleaner(String cleanerID,String name,String surname,String department)
    {
      this.cleanerID=cleanerID;
      this.name=name;
      this.surname=surname;
      this.department=department;
    }
    
    //Getters and Setters for the cleaners
    public String getCleanerId() { return cleanerID; }
    public void setCleanerId(String cleanerId) { this.cleanerID= cleanerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
