/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;

/**
 *
 * @author Michael - Uni
 */
public class Session {
        public static String currentUser = null;
        public static String currentRole = null;

        public static boolean isLoggedIn() {
            return currentUser != null;
        }

        public static void clear() {
            currentUser = null;
            currentRole = null;
    }
}
