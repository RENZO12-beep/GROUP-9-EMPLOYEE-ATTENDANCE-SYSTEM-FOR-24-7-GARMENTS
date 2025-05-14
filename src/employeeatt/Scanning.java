/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package employeeatt;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Date;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Renzo
 */
public class Scanning extends javax.swing.JFrame {

    /**
     * Creates new form Scanning
     */
    
    Connection con;
    PreparedStatement pst;
    ResultSet rs;
    
    public Scanning() {
        initComponents();
         loadAttendanceToTable();
    }
    
    private void saveAttendanceToDatabase() {
        DefaultTableModel model = (DefaultTableModel) AttendanceTable.getModel();

        try (Connection con = EmployeeAtt.getConnection()) {
            String insertQuery = "INSERT INTO attendance (employee_id, fullname, date, time_in, time_out, status, total_hours) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(insertQuery);

            for (int i = 0; i < model.getRowCount(); i++) {
                int employeeId = (int) model.getValueAt(i, 0);
                String fullname = model.getValueAt(i, 1) != null ? model.getValueAt(i, 1).toString() : "";
                String date = model.getValueAt(i, 2) != null ? model.getValueAt(i, 2).toString() : "";
                String timeIn = model.getValueAt(i, 3) != null ? model.getValueAt(i, 3).toString() : "";
                String timeOut = model.getValueAt(i, 4) != null ? model.getValueAt(i, 4).toString() : "";
                String status = model.getValueAt(i, 5) != null ? model.getValueAt(i, 5).toString() : "Absent";
                String totalHours = model.getValueAt(i, 6) != null ? model.getValueAt(i, 6).toString() : "0";

                pst.setInt(1, employeeId);
                pst.setString(2, fullname);
                pst.setString(3, date);

                
                if (timeIn.isEmpty()) {
                    pst.setNull(4, java.sql.Types.TIME);
                } else {
                    pst.setTime(4, java.sql.Time.valueOf(timeIn));
                }

                if (timeOut.isEmpty()) {
                    pst.setNull(5, java.sql.Types.TIME);
                } else {
                    pst.setTime(5, java.sql.Time.valueOf(timeOut));
                }

                pst.setString(6, status);
                pst.setString(7, totalHours);

                pst.addBatch();
            }

            pst.executeBatch();
            JOptionPane.showMessageDialog(null, " Attendance (including absents) saved to database!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, " Error saving to database.");
        }
    }

    public boolean checkAdminCredentials(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        con = EmployeeAtt.getConnection();
        try (   
            PreparedStatement pst = con.prepareStatement(query)) {

            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();
            return rs.next(); 

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void openAdminDashboard() {
        Admin admin = new Admin();
        admin.setVisible(true);
        setVisible(false);
    }      

    
    public void showAdminLogin() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Admin Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (checkAdminCredentials(username, password)) {
                JOptionPane.showMessageDialog(null, "Login successful!");
                openAdminDashboard();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials. Try again.");
            }
        }
    }
    
    private void markEmployeeAttendance(String scannedQR) {
        DefaultTableModel model = (DefaultTableModel) AttendanceTable.getModel();
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        try (Connection con = EmployeeAtt.getConnection()) {
            String query = "SELECT employee_id, fullname FROM employees WHERE LOWER(qr_code) = LOWER(?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, scannedQR.trim());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                int empID = rs.getInt("employee_id");
                String fullname = rs.getString("fullname");
                boolean updated = false;

                for (int i = 0; i < model.getRowCount(); i++) {
                    if ((int) model.getValueAt(i, 0) == empID) {
                        String timeInStr = model.getValueAt(i, 3).toString();
                        String timeOutStr = model.getValueAt(i, 4).toString();

                        if (timeInStr.isEmpty()) {
                            // TIME IN
                            String formattedIn = now.format(timeFormatter);
                            model.setValueAt(formattedIn, i, 3);

                            String status;
                            if (now.isBefore(LocalTime.of(8, 1))) {
                                status = "On Time";
                            } else if (now.isBefore(LocalTime.of(12, 0))) {
                                status = "Late";
                            } else {
                                status = "Half Day";
                            }
                            model.setValueAt(status, i, 5);
                            lblStatus.setText(" Time In: " + status);

                        } else if (timeOutStr.isEmpty()) {
                            // TIME OUT
                            String formattedOut = now.format(timeFormatter);
                            model.setValueAt(formattedOut, i, 4);

                            LocalTime timeIn = LocalTime.parse(timeInStr, timeFormatter);
                            Duration duration = Duration.between(timeIn, now);

                            long hours = duration.toHours();
                            long minutes = duration.toMinutes() % 60;
                            long seconds = duration.getSeconds() % 60;
                            String total = hours + " hours " + minutes + " minutes " + seconds + " seconds";

                            model.setValueAt(total, i, 6);
                            lblStatus.setText(" Time Out: Total Time - " + total);
                        } else {
                            lblStatus.setText("ℹ️ Already timed in and out.");
                        }

                        scannedResult.setText(fullname);
                        updated = true;
                        break;
                    }
                }

                if (!updated) {
                    lblStatus.setText("️ Employee not in table.");
                }
                scannedResult.setText("");
            } else {
                lblStatus.setText(" QR not recognized.");
                scannedResult.setText(scannedQR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("️ Error processing QR.");
        }
    }

    public void loadAttendanceToTable() {
        DefaultTableModel model = (DefaultTableModel) AttendanceTable.getModel();
        model.setRowCount(0);

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

        try (Connection con = EmployeeAtt.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT employee_id, fullname FROM employees")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("employee_id"),
                    rs.getString("fullname"),
                    today,
                    "", 
                    "", 
                    "Absent", 
                    "0"
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        scannedResult = new javax.swing.JTextField();
        bAdmin = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        AttendanceTable = new javax.swing.JTable();
        lblStatus = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(1920, 1080));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        scannedResult.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        scannedResult.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                scannedResultKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                scannedResultKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                scannedResultKeyTyped(evt);
            }
        });
        jPanel2.add(scannedResult, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 210, 760, 230));

        bAdmin.setFont(new java.awt.Font("Tahoma", 1, 48)); // NOI18N
        bAdmin.setText("ADMIN");
        bAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAdminActionPerformed(evt);
            }
        });
        jPanel2.add(bAdmin, new org.netbeans.lib.awtextra.AbsoluteConstraints(1700, 20, -1, -1));

        AttendanceTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        AttendanceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Employee ID", "Fullname", "Date", "Time In", "Time Out", "Status", "Total Hours"
            }
        ));
        jScrollPane1.setViewportView(AttendanceTable);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 550, 1900, 520));

        lblStatus.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lblStatus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblStatus.setText("SCAN HERE");
        jPanel2.add(lblStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 160, 760, -1));

        jButton1.setBackground(new java.awt.Color(0, 51, 255));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Save to database");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel2.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 460, 200, 40));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void bAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAdminActionPerformed
        // TODO add your handling code here:
        showAdminLogin();
    }//GEN-LAST:event_bAdminActionPerformed

    private void scannedResultKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scannedResultKeyReleased
        // TODO add your handling code here:
        
    }//GEN-LAST:event_scannedResultKeyReleased

    private void scannedResultKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scannedResultKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_scannedResultKeyPressed

    private void scannedResultKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scannedResultKeyTyped
        // TODO add your handling code here:
        String scannedQR = scannedResult.getText(); // ← ito dapat yung text field for QR input mo
        markEmployeeAttendance(scannedQR);
    }//GEN-LAST:event_scannedResultKeyTyped

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    // TODO add your handling code here:
        saveAttendanceToDatabase();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Scanning.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Scanning.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Scanning.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Scanning.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Scanning().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable AttendanceTable;
    private javax.swing.JButton bAdmin;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTextField scannedResult;
    // End of variables declaration//GEN-END:variables
}
