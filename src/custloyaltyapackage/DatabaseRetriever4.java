/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custloyaltyapackage;

import associationpackage.AssMethods;
import java.sql.*;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import mainpackage.DatabaseRetriever1;
import peakpackage.PeakMethods;
import simcustpackage.Customer;
import simcustpackage.DatabaseRetriever2;
import static simcustpackage.DatabaseRetriever2.creditCountquery;
import static simcustpackage.DatabaseRetriever2.creditCustextra;
import static simcustpackage.DatabaseRetriever2.creditCustomerquery;
import static simcustpackage.DatabaseRetriever2.otherCountquery;
import static simcustpackage.DatabaseRetriever2.otherCustextra;
import static simcustpackage.DatabaseRetriever2.otherCustomerquery;

/**
 *
 * @author Asus
 */
public class DatabaseRetriever4 {
    
    public static final String DATABASE_URL = DatabaseRetriever1.DATABASE_URL;
    public static boolean connected = false;
    public static Connection connection = null;
    public static Statement statement = null;
    public static PreparedStatement pstatement = null;
    public static PreparedStatement rstatement = null;
    public static ResultSet resultset = null;
    public static ResultSetMetaData metadata = null;
    
    public static final String creditProdquery = "select first_name, contact_no, DateTime, "
            + "ItemName, CategoryName, Quantity from customer_details "
            + "inner join creditcustomerbill "
            + "on customer_details.pk_customer_id=creditcustomerbill.fkCrediCustId ";
    
    public static final String otherProdquery = " select credit_Customer_Name, mobile_No, DateTime, "
            + "ItemName, CategoryName, Quantity "
            + "from otherbill ";
    
    public static final String prodExpquery = "select ItemName, CategoryName, ExpiryDays "
            + "from stock_details ";
    
    public static String datelimitquery = "";
    public static final int DLQ = 1;
    public static String custlimitquerycredit = "";
    public static String custlimitqueryother = "";
    public static final int CLQ = 2;
    
    //Map from customer to {Map of Date of a transaction by customer to 
    //{Map of product bought in that transaction to the product's quantity}}
    public static HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> custprod = 
            new HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>>();
    
    public static HashMap<String, Integer> prodexp = new HashMap<String, Integer>();
    
    public static void genQueries(Calendar start, Calendar end, 
            HashMap<String, String> customers){
        //Create the required queries
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String start1 = df.format(start.getTime()), end1 = df.format(end.getTime());
                
                datelimitquery = " (DateTime BETWEEN \""+start1+"\" AND \""+
                        end1+"\") ";
                
                if(customers!=null){
                if(!customers.isEmpty()){
                    Set<String> keys = customers.keySet();
                    Iterator<String> it = keys.iterator();
                    String key = it.next();
                    
                    int index = 0;
                    custlimitquerycredit+=" ((first_name LIKE \"%"+key+"%\" AND contact_no LIKE \"%"
                            +customers.get(key)+"%\")";
                    custlimitqueryother+=" ((credit_Customer_Name LIKE \"%"+
                            key+"%\" AND mobile_No LIKE \"%"
                            +customers.get(key)+"%\")";
                    
                    while(it.hasNext()){
                        key = it.next();
                        custlimitquerycredit+="OR (first_name LIKE \"%"+key+"%\" AND contact_no LIKE \"%"
                            +customers.get(key)+"%\")";
                        custlimitqueryother+="OR (credit_Customer_Name LIKE \"%"+key
                                +"%\" AND mobile_No LIKE \"%"
                            +customers.get(key)+"%\")";
                    }
                    custlimitquerycredit+=")";
                    custlimitqueryother+=")";
                    
                }
                else{
                    custlimitquerycredit+=" (first_name LIKE \"%%\" AND contact_no LIKE \"%%\")";
                    custlimitqueryother+=" (credit_Customer_Name LIKE \"%%\" AND mobile_No LIKE \"%%\")";
                }
                }
                else{
                    custlimitquerycredit+=" (first_name LIKE \"%%\" AND contact_no LIKE \"%%\")";
                    custlimitqueryother+=" (credit_Customer_Name LIKE \"%%\" AND mobile_No LIKE \"%%\")";
                }
    }
    
    
    public static void connectDB(Calendar start, Calendar end, 
            HashMap<String, String> customers){
        if(start.after(end)){
            Calendar temp = start;
            start = end;
            end = temp;
        }
        datelimitquery = "";
        custlimitquerycredit = "";
        custlimitqueryother = "";
        genQueries(start, end, customers);
        
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
    
    public static Customer[] retrieveCustList(){
        ArrayList<Customer> custlist = new ArrayList<Customer>();
        //Temporary variable
        String[] custlisttemp = new String[2];
        //Set would be inserted into this (if necessary)
        Customer[] custliststr = new Customer[0];
        int index= 0;
        
        try{
            resultset = statement.executeQuery(creditCountquery+" where "+custlimitquerycredit+
                    creditCustextra);
            int creditlength = 0;
            if(resultset.next()){
            creditlength = resultset.getInt(1);}
            resultset = statement.executeQuery(otherCountquery+" where "+custlimitqueryother+
                    otherCustextra);
            int otherlength = 0;
            if(resultset.next()){
            otherlength = resultset.getInt(1);}
            
            int mainlength = creditlength+otherlength;
            
            resultset = statement.executeQuery(creditCustomerquery+" where "+custlimitquerycredit+
                    creditCustextra);
            while(resultset.next()){
                custlist.add(new Customer(resultset.getString(1), resultset.getString(2)));
            }
            resultset = statement.executeQuery(otherCustomerquery+" where "+custlimitqueryother+
                    otherCustextra);
            while(resultset.next()){
                custlist.add(new Customer(resultset.getString(1), resultset.getString(2)));
            }
            
        }
        catch(SQLException ex){
            Logger.getLogger(DatabaseRetriever2.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return custlist.toArray(new Customer[0]);
    }
    
    public static HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> getCustProd
            (){
                HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> custprod1 = 
                        new HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>>();
                
                //Temp variables of custprod1
                //Map of Date to Map of product to quantity
                HashMap<Calendar, HashMap<String, Integer>> subcustprod1 = 
                        new HashMap<Calendar, HashMap<String, Integer>>();
                //Map of Product in a date to its quantity
                HashMap<String, Integer> sub2custprod1 = new HashMap<String, Integer>();
                
                Customer custname;
                String name, contact, item, category, itemname;
                Calendar date;
                int quantity;
                
                String[] prodquery = {creditProdquery, otherProdquery};
                String[] prodlimitquery = {custlimitquerycredit, custlimitqueryother};
                
                try{
                    for(int i=0; i<prodquery.length; i++){
                    resultset = statement.executeQuery(prodquery[i]+" where "+prodlimitquery[i]);
                    while(resultset.next()){
                        name = resultset.getString(1);
                        contact = resultset.getString(2);
                        custname = new Customer(name, contact);
                        date = AssMethods.getCalendar(resultset.getString(3));
                        item = resultset.getString(4);
                        category = resultset.getString(5);
                        itemname = item+"("+category+")";
                        quantity = resultset.getInt(6);
                        
                        if(CustLoyAMethods.findCustProdPresent(custprod1, custname)==null){
                            subcustprod1 = new HashMap<Calendar, HashMap<String, Integer>>();
                            sub2custprod1 = new HashMap<String, Integer>();
                        
                            sub2custprod1.put(itemname, quantity);
                            subcustprod1.put(date, sub2custprod1);
                            custprod1.put(custname, subcustprod1);
                        }
                        else{
                            subcustprod1 = CustLoyAMethods.findCustProdPresent(custprod1, custname);
                            if(subcustprod1.get(date)==null){
                                sub2custprod1 = new HashMap<String, Integer>();
                                sub2custprod1.put(itemname, quantity);
                            }
                            else{
                                sub2custprod1 = subcustprod1.get(date);
                                if(sub2custprod1.get(itemname)==null){
                                    sub2custprod1.put(itemname, quantity);
                                }
                                else{
                                    sub2custprod1.put(itemname, 
                                            sub2custprod1.get(itemname)+quantity);
                                }
                            }
                            subcustprod1.put(date, sub2custprod1);
                            
                            Set<Customer> keys = custprod1.keySet();
                            Iterator<Customer> it = keys.iterator();
                            while(it.hasNext()){
                                Customer key = it.next();
                                if(key.equals(custname)){
                                    custprod1.put(key, subcustprod1);
                                }
                            }
                        }
                    }
                    }
                }
                catch(SQLException ex){
                    Logger.getLogger(DatabaseRetriever2.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
                
                return custprod1;
    }
            
    
    public static HashMap<String, Integer> getProdExpiry(){
        HashMap<String, Integer> prodexp1 = new HashMap<String, Integer>();
        
        try{
            resultset = statement.executeQuery(prodExpquery);
            
            while(resultset.next()){
                String prod = resultset.getString(1)+"("+resultset.getString(2)+")";
                int exp = resultset.getInt(3);
                prodexp1.put(prod, exp);
            }
            
        } 
        catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever4.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        return prodexp1;
    }        
            
    
            
    
}
