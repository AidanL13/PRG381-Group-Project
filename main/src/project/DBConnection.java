/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;
import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Michael - Uni
 */
public class DBConnection {
    
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
        
    private static final String JDBC_URL = "jdbc:derby:invSystemDB;create=true";
        
    Connection con;
    
    public DBConnection(){
            
    }
    
    public void connect() throws ClassNotFoundException {
        try{
            Class.forName(DRIVER);
            this.con = DriverManager.getConnection(JDBC_URL);
            if(this.con != null){
                System.out.println("Connected to database");
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    //create user table (only runs once)
    public void userTable(){
        try{
            String query = "Create Table Users(Username varchar(20), " + "Email varchar(20), Password varchar(20), Role varchar(20))";
            this.con.createStatement().execute(query);
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    //adds user to database
    public void addUser(String username, String email, String password, String role){
        try{
            String query = "INSERT INTO Users VALUES('"+username+"', '"+email+"', '"+password+"', '"+role+"' )";
            this.con.createStatement().execute(query);
            System.out.println("User Registered");
        }catch(SQLException ex){
            ex.printStackTrace();
            System.out.println("Error registering user");
        }
    }
    
    public String login(String username, String password) {
        String role = null;
        try {
            String query = "SELECT * FROM Users WHERE Username=? AND Password=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                role = rs.getString("Role");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return role;
    }
}
