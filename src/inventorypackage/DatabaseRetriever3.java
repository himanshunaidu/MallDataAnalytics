/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inventorypackage;

import associationpackage.AssMethods;
import mainpackage.DatabaseRetriever1;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import peakpackage.PeakMethods;

/**
 *
 * @author Asus
 */
public class DatabaseRetriever3 {
    
    public static final String DATABASE_URL = DatabaseRetriever1.DATABASE_URL;
    public static boolean connected = false;
    public static Connection connection = null;
    public static Statement statement = null;
    public static PreparedStatement pstatement = null;
    public static PreparedStatement rstatement = null;
    public static ResultSet resultset = null;
    public static ResultSetMetaData metadata = null;
    
    //Tables for querying the product transactions
    public static final String[] tables = {"creditcustomerbill", "otherbill"};
    
    //Queries for product transactions
    public static final String creditProdquery = "select ItemName, CategoryName, "
            + "DateTime, Quantity from ";
    
    public static final String otherProdquery = "select ItemName, CategoryName, "
            + "DateTime, Quantity from ";
    
    public static String datelimitquery = "";
    public static final int DLQ = 1;
    public static String prodlimitquery = "";
    public static final int PLQ = 2;
    
    public static String prodlengthquery = "Select count(ItemName) from stock_details ";
    public static String prodquery = "Select ItemName, CategoryName from stock_details ";
    
    public static void genQueries(Calendar start, Calendar end, 
            HashMap<String, ArrayList<String>> prod){
        //Create the required queries
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String start1 = df.format(start.getTime()), end1 = df.format(end.getTime());
                
                datelimitquery = " (DateTime BETWEEN \""+start1+"\" AND \""+
                        end1+"\") ";
                
                if(prod!=null){
                if(!prod.isEmpty()){
                    Set<String> keys = prod.keySet();
                    Iterator<String> it = keys.iterator();
                    String key = it.next();
                    
                    ArrayList<String> arr = prod.get(key);
                    int index = 0;
                    prodlimitquery+=" ((CategoryName LIKE \"%"+key+"%\" AND ItemName LIKE \"%"
                            +arr.get(0)+"%\")";
                    for(index = 1; index<arr.size(); index++){
                        prodlimitquery+="OR (CategoryName LIKE \"%"+key+"%\" AND ItemName LIKE \"%"
                            +arr.get(index)+"%\")";
                    }
                    while(it.hasNext()){
                        key = it.next();
                        arr = prod.get(key);
                        for(index = 0; index<arr.size(); index++){
                        prodlimitquery+="OR (CategoryName LIKE \"%"+key+"%\" AND ItemName LIKE \"%"
                            +arr.get(index)+"%\")";
                    }
                    }
                    prodlimitquery+=")";
                    
                }
                else{
                    prodlimitquery+=" (CategoryName LIKE \"%%\" AND ItemName LIKE \"%%\")";
                }
                }
                else{
                    prodlimitquery+=" (CategoryName LIKE \"%%\" AND ItemName LIKE \"%%\")";
                }
    }
    
    public static void connectDB(Calendar start, Calendar end, 
            HashMap<String, ArrayList<String>> prod){
        if(start.after(end)){
            Calendar temp = start;
            start = end;
            end = temp;
        }
        datelimitquery = "";
        prodlimitquery = "";
        genQueries(start, end, prod);
        
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
    
    public static String[] retrieveProd(){
        String[] products;
        try{
            int length = 0;
            resultset = statement.executeQuery(prodlengthquery+" where "+prodlimitquery);
            resultset.next();
            length = resultset.getInt(1);
            products = new String[length];
            
            resultset = statement.executeQuery(prodquery+" where "+prodlimitquery);
            int index = 0;
            while(resultset.next()){
                products[index] = resultset.getString(1)+"("+resultset.getString(2)+")";
                index++;
            }
            
            return products;
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
        
    public static HashMap<String, HashMap<Calendar, Integer>> getProdAssociations(){
        HashMap<String, HashMap<Calendar, Integer>> prodass1 = 
            new HashMap<String, HashMap<Calendar, Integer>>();
        //Sub-HashMap of prodass
        HashMap<Calendar, Integer> subprodass1;
        
        try{
            for(int i=0; i<tables.length; i++){
                resultset = statement.executeQuery(creditProdquery+tables[i]+" where "+
                        datelimitquery+" AND "+prodlimitquery);
                while(resultset.next()){
                    String prod = resultset.getString(1)+"("+resultset.getString(2)+")";
                    Calendar date = PeakMethods.setDefCal(
                            AssMethods.getCalendar(resultset.getString(3)));
                    int quant = resultset.getInt(4);
                    
                    if(prodass1.get(prod)==null){
                        subprodass1 = new HashMap<Calendar, Integer>();
                        subprodass1.put(date, quant);
                        prodass1.put(prod, subprodass1);
                    }
                    else{
                        //Temporary variable
                        subprodass1 = prodass1.get(prod);
                        if(subprodass1.get(date)==null){
                            subprodass1.put(date, quant);
                        }
                        else{
                            subprodass1.put(date, subprodass1.get(date)+quant);
                        }
                        prodass1.put(prod, subprodass1);
                    }
                }
            }
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever3.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return prodass1;
    }
    
    public static void printProdAss(HashMap<String, HashMap<Calendar, Integer>> prodass1){
        Set<String> keys = prodass1.keySet();
        Iterator<String> it = keys.iterator();
        HashMap<Calendar, Integer> subprodass1 = new HashMap<Calendar, Integer>();
        
        while(it.hasNext()){
            String prod = it.next();
            System.out.print(prod+": \n");
            
            subprodass1 = prodass1.get(prod);
            Set<Calendar> keys1 = subprodass1.keySet();
            Iterator<Calendar> it1 = keys1.iterator();
            while(it1.hasNext()){
                Calendar date = it1.next();
                System.out.println(date.getTime().toString()+"=> "+subprodass1.get(date));
            }
        }
    }
    
    public static void printPop(HashMap<String, Regression> pop1){
        Set<String> keys = pop1.keySet();
        Iterator<String> it = keys.iterator();
        
        while(it.hasNext()){
            String prod = it.next();
            System.out.println(prod+": "+pop1.get(prod));
        }
    }
}
