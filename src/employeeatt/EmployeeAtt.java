
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package employeeatt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Renzo
 */
public class EmployeeAtt {

    /**
     * @param args the command line arguments
     */
    static Connection con;
    
    public static Connection getConnection() {          
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:4306/employee_attendance_db","root","");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(EmployeeAtt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeAtt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }      
}
