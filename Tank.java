/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tanks;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.sql.*;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
/**
 *
 * @author adarb
 */
public class Tank {
    
        private final String URL = "jdbc:derby://localhost:1527/Tanks";
        private final String USER = "Darby";
        private final String PASSWORD = "Daisy";
        private Connection connection = null;
        private PreparedStatement getWellData = null; // temp use for testing purposes, full 
        // statement will get all necessary data in one select and use to populate GUI
        private PreparedStatement updateWellData = null; 
        Statement statement = null;
        ResultSet rs = null;
        ResultSet ts = null;
     
        
    public Tank(){
        
           try{
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            statement = connection.createStatement();
            
            if (connection.isValid(0)){
                System.out.println("Connection successful.");
            }
            
            rs = statement.executeQuery("Select * FROM WELLS");
            ResultSetMetaData md = rs.getMetaData();
            int numColumns = md.getColumnCount();
            System.out.println("Wells Table of Tanks DB: ");
            
            for (int i = 1; i <= numColumns; i++){
                System.out.printf("%-8s\t", md.getColumnName(i));
               
            }
             System.out.println();
            
            while ( rs.next()){
                for(int i = 1; i <= numColumns; i++){
                    System.out.printf("%-8s\t", rs.getObject(i));
              
                }
                      System.out.println();
            }
             }catch(SQLException sqlException){
                sqlException.printStackTrace();
                System.exit(1);
            }
           
//           try {
//               rs.close();
//               statement.close();
//               connection.close();
//           }catch( Exception exception){
//               exception.printStackTrace();
//           }
           
    }
    
    // just testing out some stuff
    public void fillGUI(JTextArea jta){
       jta.setText("Now tx is this.");
    }
    

    /*
    method that takes input from GUI when select button clicked, populates GUI fields for data from 
    selected well
    */
    public void popGUI(String well, JTextField tSize, JTextField tNum, JCheckBox onOff, JTextField t1Ft, 
            JTextField t1In, JComboBox brineBox, JTextField lastOilFt, JTextField lastOilIn,
            JCheckBox calledIn, JComboBox action, JTextArea treat) {
        
        try{
        //getWellData = connection.Statement(ResultSet.TYPE_SCROLL_INSENSITIVE,
         //                             ResultSet.CONCUR_UPDATABLE);
        String name = well.toUpperCase();
        // this is the prepared statement that gets all data from the selected well
        getWellData = connection.prepareStatement("SELECT * FROM WELLS WHERE NAME = ?");
        getWellData.setString(1, name);
        ts = getWellData.executeQuery();                //execute the query and set result to ts(result set)
        ResultSetMetaData meta = ts.getMetaData();      // create a metadata obj for the result set
        int n = meta.getColumnCount();                  // retrieve column count from metadata
        ts.next();
        System.out.println(meta.getColumnName(1));
        System.out.println(ts.getString(5));
        
        // section sets the tank size
        tSize.setText((String) ts.getString(5));
        
        // section sets tank number
        int num = ts.getInt(2);
        String tankNum = Integer.toString(num);
        tNum.setText(tankNum);
        
        // section sets selects or deselects the on/off box according to data from db
        String status = ts.getString(3);
        if (status.equalsIgnoreCase(status)){
            onOff.setSelected(true);
        }
        else onOff.setSelected(false);
        
        // section sets selects or deselects the called in box according to data from db
        Boolean called = ts.getBoolean(15);
        if (called){
            calledIn.setSelected(true);
        }
        else onOff.setSelected(false);
        
        // section to set feet for tank1
        int ft1 = ts.getInt(7);
        String ftT1 = Integer.toString(ft1);
        t1Ft.setText(ftT1);
        
        // section to set in for tank1
        int in1 = ts.getInt(8);
        String inT1 = Integer.toString(in1);
        t1In.setText(inT1);
        
        // section sets brine tank level from DB
        String brLevel = ts.getString(4).toUpperCase();
        brineBox.setSelectedItem(brLevel);
  
        // section to set last oil feet
        int oFt = ts.getInt(11);
        String oilFt = Integer.toString(oFt);
        lastOilFt.setText(oilFt);
        
        // section to set last oil feet
        int oIn = ts.getInt(12);
        String oilIn = Integer.toString(oIn);
        lastOilIn.setText(oilIn);
        
        // section to set action needed JComboBox
        String wAction = ts.getString(6);
        action.setSelectedItem(wAction);
        
        // section to set Treatment
        String treatment = ts.getString(14);
        treat.setText(treatment);
        
        
        }catch (SQLException sqlException){
                sqlException.printStackTrace();
      
        }
    }
    
    public void updateDB(String well, JTextField tSize, JTextField tNum, JCheckBox onOff, JTextField t1Ft, 
            JTextField t1In, JComboBox brineBox, JTextField lastOilFt, JTextField lastOilIn,
            JCheckBox calledIn, JComboBox action, JTextArea treat){
        try{
            
            // set up/grab all variables needed to perform an update on the selected well
            String name = well.toUpperCase();
            int size = Integer.parseInt(tSize.getText());       // get tank size for update
            int num = Integer.parseInt(tNum.getText());         // get tank number for update
           
            // deal with on off conversion to string
            Boolean on = onOff.isSelected();                    // get check box value for on/off status
            String onOrOff;
            if (on == true){
                onOrOff = "ON";
            }
            else 
                onOrOff = "OFF";
            
            int t1Feet = Integer.parseInt(t1Ft.getText());      // get tank one feet 
            int t1Inch = Integer.parseInt(t1In.getText());      // get tank one inches
            int lOilFt = Integer.parseInt(lastOilFt.getText()); // get last oil feet level
            int lOilIn = Integer.parseInt(lastOilIn.getText()); // get last oil inches level
            Boolean called = calledIn.isSelected();             // get called in yes/no
            String brineTank = brineBox.getSelectedItem().toString();   // get brine tank level
            String act = action.getSelectedItem().toString();   // get needed action 
            String treatment = treat.getText();                 // get treatment description
            //System.out.println(on);
             
            // this area will pull out all the data and put it into manageable variables '
            // to pass to the UPDate Statement.
             
            // prepared statement that will update all the well info in the database
            updateWellData = connection.prepareStatement("UPDATE WELLS SET TANK_SIZE = ?"
                    + ", NUMBER = ?, ON_OFF = ?, TANK1_FT = ?, TANK1_IN = ?, LAST_OIL_FT = ?,"
                    + " LAST_OIL_IN = ?, CALLED_IN = ?, BRINE_LEV = ?, "
                    + " ACTION = ?, TREAT = ?  WHERE NAME = ? ");
            
            // set all data for update statement
            updateWellData.setInt(1, size);
            updateWellData.setInt(2, num);
            updateWellData.setString(3, onOrOff);
            updateWellData.setInt(4, t1Feet);
            updateWellData.setInt(5, t1Inch);
            updateWellData.setInt(6, lOilFt);
            updateWellData.setInt(7, lOilIn);
            updateWellData.setBoolean(8, called);
            updateWellData.setString(9, brineTank);
            updateWellData.setString(10, act);
            updateWellData.setString(11, treatment);
            updateWellData.setString(12, name);
            updateWellData.execute();      //execute the prepared statement
             
        }catch (SQLException sqlException){
                sqlException.printStackTrace();
      
        }
    }
}