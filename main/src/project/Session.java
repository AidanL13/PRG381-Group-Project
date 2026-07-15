/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;

/**
 *
 * @author Michael - Uni
 */
public final class Session {
 
    public static String currentUser;
    public static String currentRole;
 
    private Session() {
      
    }
 
    public static boolean isSupervisor() {
        return "SUPERVISOR".equalsIgnoreCase(currentRole);
    }
 
    public static void clear() {
        currentUser = null;
        currentRole = null;
    }
}
 
