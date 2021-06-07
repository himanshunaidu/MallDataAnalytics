/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supplierpackage;

import associationpackage.AssMethods;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import mainpackage.DatabaseRetriever1;
import peakpackage.PeakMethods;

/**
 *
 * @author Asus
 */
public class DatabaseRetriever5 {
    
    public static final String DATABASE_URL = DatabaseRetriever1.DATABASE_URL;
    public static boolean connected = false;
    public static Connection connection = null;
    public static Statement statement = null;
    public static PreparedStatement pstatement = null;
    public static PreparedStatement rstatement = null;
    public static ResultSet resultset = null;
    public static ResultSetMetaData metadata = null;
    
    public static final String suppNamequery = "select supplier_id, supplier_name "
            + "from supplier_details ";
    
    public static final String suppGoodquery = "select FksuppId, DateTime, ItemName, "
            +"CategoryName, Quantity from goodreceive ";
    
    public static String datelimitquery = "";
    public static final int DLQ = 1;
    public static String supplimitquery = "";
    public static String suppgoodlimitquery="";
    public static final int SLQ = 2;
    
    public static void genQueries(Calendar start, Calendar end, 
            ArrayList<Integer> suppliers){
        //Create the required queries
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String start1 = df.format(start.getTime()), end1 = df.format(end.getTime());
                
                datelimitquery = " (DateTime BETWEEN \""+start1+"\" AND \""+
                        end1+"\") ";
                
                if(suppliers!=null){
                if(!suppliers.isEmpty()){
                    Iterator<Integer> it = suppliers.iterator();
                    Integer key = it.next();
                    
                    int index = 0;
                    supplimitquery+=" ((supplier_id="+key+")";
                    suppgoodlimitquery+=" ((FksuppId="+key+")";
                    
                    while(it.hasNext()){
                        key = it.next();
                        supplimitquery+=" OR (supplier_id="+key+")";
                        suppgoodlimitquery+=" OR (FksuppId="+key+")";
                    }
                    supplimitquery+=")";
                    suppgoodlimitquery+=")";
                    
                }
                else{
                    supplimitquery+="1=1";
                    suppgoodlimitquery+="1=1";
                }
                }
                else{
                    supplimitquery+="1=1";
                    suppgoodlimitquery+="1=1";
                }
    }
    
    public static void connectDB(Calendar start, Calendar end, 
            ArrayList<Integer> suppliers){
        if(start.after(end)){
            Calendar temp = start;
            start = end;
            end = temp;
        }
        datelimitquery = "";
        supplimitquery = "";
        suppgoodlimitquery = "";
        genQueries(start, end, suppliers);
        
        try {
    		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(DATABASE_URL, "root", "root");
			
		String message="";
		statement = connection.createStatement();
		connected = true;
			
		} 
        catch (SQLException e) {
		JOptionPane.showMessageDialog(null, "Database Connection Failed", "Error",
				JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
		} 
        catch (ClassNotFoundException e) {
		closeDB();
		JOptionPane.showMessageDialog(null, "Database Class Failed", "Error",
				JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
            }
    }
    
    public static void closeDB(){
        if(connected==true){
    		try {
			//resultset.close();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "Could not close database", "Error",
						JOptionPane.ERROR_MESSAGE);
		}    		
    	}
    }
    
    public static HashMap<Integer, String> getSupp(){
        HashMap<Integer, String> supp = new HashMap<Integer, String>();
        
        try{
            resultset = statement.executeQuery(suppNamequery+" where "+supplimitquery);
            System.out.println(suppNamequery+" where "+supplimitquery);
            while(resultset.next()){
                supp.put(resultset.getInt(1), resultset.getString(2));
            }
            
            return supp;
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever5.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    //Transactions with each supplier
    public static HashMap<Integer, HashMap<Calendar, HashMap<String, Integer>>> getSuppTrans(){
        HashMap<Integer, HashMap<Calendar, HashMap<String, Integer>>> supptrans = 
                new HashMap<Integer, HashMap<Calendar, HashMap<String, Integer>>>();
        
        //Sub-Map from date of transaction to map of product to its quantity in transaction
        HashMap<Calendar, HashMap<String, Integer>> subsupptrans = 
                new HashMap<Calendar, HashMap<String, Integer>>();
        //Map from product to its quantity in transaction
        HashMap<String, Integer> sub2supptrans = new HashMap<String, Integer>();
        
        try{
            resultset = statement.executeQuery(suppGoodquery+" where "+datelimitquery+
                    " AND "+suppgoodlimitquery);
            System.out.println(suppGoodquery+" where "+datelimitquery+
                    " AND "+suppgoodlimitquery);
            
            while(resultset.next()){
                int supp = resultset.getInt(1);
                Calendar date = PeakMethods.setDefCal(
                        AssMethods.getCalendar(resultset.getString(2)));
                String prod = resultset.getString(3)+"("+resultset.getString(4)+")";
                int quant = resultset.getInt(5);
                
                if(supptrans.get(supp)==null){
                    sub2supptrans = new HashMap<String, Integer>();
                    sub2supptrans.put(prod, quant);
                    subsupptrans = new HashMap<Calendar, HashMap<String, Integer>>();                    
                }
                else{
                    subsupptrans = supptrans.get(supp);
                    if(subsupptrans.get(date)==null){
                        sub2supptrans = new HashMap<String, Integer>();
                        sub2supptrans.put(prod, quant);                        
                    }
                    else{
                        sub2supptrans = subsupptrans.get(date);
                        if(sub2supptrans.get(prod)==null){
                            sub2supptrans.put(prod, quant);
                        }
                        else{
                            sub2supptrans.put(prod, sub2supptrans.get(prod)+quant);
                        }
                    }
                }
                subsupptrans.put(date, sub2supptrans);
                supptrans.put(supp, subsupptrans);
                
            }
            return supptrans;
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever5.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return null;
    }
    
    /*public static void main(String[] args){
        connectDB();
        HashMap<Integer, String> supp = getSupp();
        HashMap<Integer, HashMap<Calendar, HashMap<String, Integer>>> supptrans = 
                getSuppTrans();
        System.out.println("Printing Supplier Transactions\n");
        SupplierMethods.printCustProd(supptrans, supp);
        closeDB();
    }*/
    
}
