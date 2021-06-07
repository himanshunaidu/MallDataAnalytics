/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simcustpackage;

import mainpackage.DatabaseRetriever1;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Asus
 */
public class DatabaseRetriever2 {
    
    public static final String DATABASE_URL = DatabaseRetriever1.DATABASE_URL;
    public static boolean connected = false;
    public static Connection connection = null;
    public static Statement statement = null;
    public static PreparedStatement pstatement = null;
    public static PreparedStatement rstatement = null;
    public static ResultSet resultset = null;
    public static ResultSetMetaData metadata = null;
    
    //Use credit first and then other to maintain uniformity
    public static final String creditCustomerquery =  "select first_name, contact_no, BillNo, "
            + "Date, ItemName, Quantity, CategoryName "
            + "from customer_details "
            + "Inner Join creditcustomerbill "
            + "on customer_details.pk_customer_id=creditcustomerbill.fkCrediCustId ";
    
    public static final String creditCustextra = " group by first_name, contact_no ";
    
    public static final String creditCountquery =  "select count(*) "
            + "from customer_details "
            + "Inner Join creditcustomerbill "
            + "on customer_details.pk_customer_id=creditcustomerbill.fkCrediCustId ";
    
    public static final String otherCustomerquery = "select credit_Customer_Name, mobile_No, BillNo, "
            + "Date, ItemName, Quantity, CategoryName "
            +"from otherbill ";
    
    public static final String otherCustextra = " group by credit_Customer_Name, mobile_No ";
    
    public static final String otherCountquery = "select count(*) "
            +"from otherbill ";
    
    public static String prodlengthquery = "Select count(ItemName) from stock_details ";
    public static String prodquery = "Select ItemName, CategoryName from stock_details ";
    
    public static String datelimitquery = "";
    public static final int DLQ = 1;
    public static String prodlimitquery = "";
    public static final int PLQ = 2;
    
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
    
    /*public static String[] retrieveProd(){
        return DatabaseRetriever1.retrieveProd();
    }*/
    
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
        
    public static Customer[] retrieveCustList(){
        ArrayList<Customer> custlist = new ArrayList<Customer>();
        //Temporary variable
        String[] custlisttemp = new String[2];
        //Set would be inserted into this (if necessary)
        Customer[] custliststr = new Customer[0];
        int index= 0;
        
        try{
            resultset = statement.executeQuery(creditCountquery+" where "+
                    prodlimitquery+creditCustextra);
            resultset.next();
            int creditlength = resultset.getInt(1);
            resultset = statement.executeQuery(otherCountquery+" where "+
                    prodlimitquery+otherCustextra);
            resultset.next();
            int otherlength = resultset.getInt(1);
            
            int mainlength = creditlength+otherlength;
            
            resultset = statement.executeQuery(creditCustomerquery+" where "+
                    prodlimitquery+creditCustextra);
            while(resultset.next()){
                custlist.add(new Customer(resultset.getString(1), resultset.getString(2)));
            }
            resultset = statement.executeQuery(otherCustomerquery+" where "+
                    prodlimitquery+otherCustextra);
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
    
    public static int[][] retrieveCustMatrix(String[] prod, Customer[] cust){
        if((prod==null)||(cust==null)){
            return null;
        }
        int[][] custmatrix = new int[0][];
        Customer[] creditcust, othercust;
        int[] creditbill, otherbill;
        String[] creditdate, otherdate;
        String[] credititem, otheritem;
        int[] creditquant, otherquant;
        
        int creditlength=0, otherlength=0, mainlength=0;
        
        try{
            //System.out.println(creditCountquery+ prodlimitquery);
            resultset = statement.executeQuery(creditCountquery+ " where "+prodlimitquery);
            resultset.next();
            creditlength = resultset.getInt(1);
            resultset = statement.executeQuery(otherCountquery+ " where "+ prodlimitquery);
            resultset.next();
            otherlength = resultset.getInt(1);
            
            mainlength = creditlength+otherlength;
            creditcust = new Customer[creditlength];
            othercust = new Customer[otherlength];
            creditbill = new int[creditlength];
            otherbill = new int[otherlength];
            creditdate = new String[creditlength];
            otherdate = new String[otherlength];
            credititem = new String[creditlength];
            otheritem = new String[otherlength];
            creditquant = new int[creditlength];
            otherquant = new int[otherlength];
            
            resultset = statement.executeQuery(creditCustomerquery+ " where "+ prodlimitquery);
            int i=0;
            while(resultset.next()){
                creditcust[i] = new Customer(resultset.getString(1), 
                        resultset.getString(2));
                creditbill[i] = resultset.getInt(3);
                creditdate[i] = resultset.getString(4);
                credititem[i] = resultset.getString(5)+"("+resultset.getString(7)+")";
                creditquant[i] = resultset.getInt(6);
                i++;
                //System.out.println(creditname[i]+", "+creditcont[i]+" "+creditbill[i]+" "
                //    +creditdate[i]+" "+credititem[i]+" "+creditquant[i]);
            }
            
            resultset = statement.executeQuery(otherCustomerquery+ " where "+prodlimitquery);
            i=0;
            while(resultset.next()){
                othercust[i] = new Customer(resultset.getString(1), 
                        resultset.getString(2));
                otherbill[i] = resultset.getInt(3);
                otherdate[i] = resultset.getString(4);
                otheritem[i] = resultset.getString(5)+"("+resultset.getString(7)+")";
                otherquant[i] = resultset.getInt(6);
                i++;
                //System.out.println(othername[i]+", "+othercont[i]+" "+otherbill[i]+" "
                //    +otherdate[i]+" "+otheritem[i]+" "+otherquant[i]);
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever2.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        int custlength = cust.length, prodlength = prod.length;
        creditlength = creditbill.length;
        otherlength = otherbill.length;
        custmatrix = new int[custlength][prodlength];
        
        //Initialize custmatrix
        for(int i=0; i<custlength; i++){
            for(int j=0; j<prodlength; j++){
                custmatrix[i][j] = 0;
            }
        }
        
        //Temporary variables
        Customer tempcust;
        String tempprod = "";
        for(int i=0; i<custlength; i++){
            for(int j=0; j<prodlength; j++){
                tempcust = new Customer(cust[i].getName(), cust[i].getContact());
                tempprod = prod[j];
                
                for(int k=0; k<creditlength; k++){
                    /*System.out.println(tempname+" "+creditname[k]+"\t"
                    +tempcont+" "+creditcont[k]+"\t"
                    +tempprod+" "+credititem[k]+" "+creditquant[k]+" "
                    +custmatrix[i][j]);*/
                    if((tempcust.equals(creditcust[k]))&&
                        (tempprod.equals(credititem[k]))){
                        //System.out.println(tempname+" "+tempcont+" "+tempprod);
                        custmatrix[i][j]+=creditquant[k];
                    }
                }
            }
        }
        for(int i=0; i<custlength; i++){
            for(int j=0; j<prodlength; j++){
                tempcust = new Customer(cust[i].getName(), cust[i].getContact());
                tempprod = prod[j];
                for(int k=0; k<otherlength; k++){
                    /*System.out.println(tempname+" "+othername[k]+"\t"
                    +tempcont+" "+othercont[k]+"\t"
                    +tempprod+" "+otheritem[k]);*/
                    if((tempcust.equals(othercust[k]))&&
                        (tempprod.equals(otheritem[k]))){
                        //System.out.println(tempname+" "+tempcont+" "+tempprod);
                        custmatrix[i][j]+=otherquant[k];
                    }
                }
            }
        }
        
        return custmatrix;
    }
    
    //Speicalized Cluster Matrix
    //Dim 1: Customer, Dim 2: Product buying frequency
    //DIm 3: Average number of product bought per transaction
    public static double[][][] retrieveSpecCustMatrix(String[] prod, Customer[] cust){
        if((prod==null)||(cust==null)){
            return null;
        }
        double[][][] custmatrix = new double[0][][];
        Customer[] creditcust, othercust;
        int[] creditbill, otherbill;
        String[] creditdate, otherdate;
        String[] credititem, otheritem;
        int[] creditquant, otherquant;
        
        int creditlength=0, otherlength=0, mainlength=0;
        
        try{
            resultset = statement.executeQuery(creditCountquery+ " where "+ prodlimitquery);
            resultset.next();
            creditlength = resultset.getInt(1);
            resultset = statement.executeQuery(otherCountquery+ " where "+ prodlimitquery);
            resultset.next();
            otherlength = resultset.getInt(1);
            
            mainlength = creditlength+otherlength;
            creditcust = new Customer[creditlength];
            othercust = new Customer[otherlength];
            creditbill = new int[creditlength];
            otherbill = new int[otherlength];
            creditdate = new String[creditlength];
            otherdate = new String[otherlength];
            credititem = new String[creditlength];
            otheritem = new String[otherlength];
            creditquant = new int[creditlength];
            otherquant = new int[otherlength];
            
            resultset = statement.executeQuery(creditCustomerquery+ " where "+prodlimitquery);
            int i=0;
            while(resultset.next()){
                creditcust[i] = new Customer(resultset.getString(1), 
                        resultset.getString(2));
                creditbill[i] = resultset.getInt(3);
                creditdate[i] = resultset.getString(4);
                credititem[i] = resultset.getString(5)+"("+resultset.getString(7)+")";
                creditquant[i] = resultset.getInt(6);
                i++;
                //System.out.println(creditname[i]+", "+creditcont[i]+" "+creditbill[i]+" "
                //    +creditdate[i]+" "+credititem[i]+" "+creditquant[i]);
            }
            
            resultset = statement.executeQuery(otherCustomerquery+ " where "+prodlimitquery);
            i=0;
            while(resultset.next()){
                othercust[i] = new Customer(resultset.getString(1), 
                        resultset.getString(2));
                otherbill[i] = resultset.getInt(3);
                otherdate[i] = resultset.getString(4);
                otheritem[i] = resultset.getString(5)+"("+resultset.getString(7)+")";
                otherquant[i] = resultset.getInt(6);
                i++;
                //System.out.println(othername[i]+", "+othercont[i]+" "+otherbill[i]+" "
                //    +otherdate[i]+" "+otheritem[i]+" "+otherquant[i]);
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever2.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        int custlength = cust.length, prodlength = prod.length;
        creditlength = creditbill.length;
        otherlength = otherbill.length;
        custmatrix = new double[custlength][prodlength][2];
        
        //Count matrix
        int[][] countmatrix = new int[custlength][prodlength];
        
        //Initialize custmatrix
        for(int i=0; i<custlength; i++){
            for(int j=0; j<prodlength; j++){
                //0 position is for keeping track of number of transactions
                custmatrix[i][j][0] = 0;
                //1 position is for keeping track of average number of product per transaction
                custmatrix[i][j][1] = 0;
                countmatrix[i][j] = 0;
            }
        }
        
        //Temporary variables
        Customer tempcust;
        String tempprod = "";
        for(int i=0; i<custlength; i++){
            for(int j=0; j<prodlength; j++){
                tempcust = new Customer(cust[i].getName(), cust[i].getContact());
                tempprod = prod[j];
                
                for(int k=0; k<creditlength; k++){
                    /*System.out.println(tempname+" "+creditname[k]+"\t"
                    +tempcont+" "+creditcont[k]+"\t"
                    +tempprod+" "+credititem[k]+" "+creditquant[k]+" "
                    +custmatrix[i][j]);*/
                    if((tempcust.equals(creditcust[k]))&&
                        (tempprod.equals(credititem[k]))){
                        //System.out.println(tempname+" "+tempcont+" "+tempprod);
                        custmatrix[i][j][1]+=creditquant[k];
                        custmatrix[i][j][0]++;
                    }
                    countmatrix[i][j]++;
                }
            }
        }
        for(int i=0; i<custlength; i++){
            for(int j=0; j<prodlength; j++){
                tempcust = new Customer(cust[i].getName(), cust[i].getContact());
                tempprod = prod[j];
                for(int k=0; k<otherlength; k++){
                    /*System.out.println(tempname+" "+othername[k]+"\t"
                    +tempcont+" "+othercont[k]+"\t"
                    +tempprod+" "+otheritem[k]);*/
                    if((tempcust.equals(othercust[k]))&&
                        (tempprod.equals(otheritem[k]))){
                        //System.out.println(tempname+" "+tempcont+" "+tempprod);
                        custmatrix[i][j][1]+=otherquant[k];
                        custmatrix[i][j][0]++;
                    }
                    countmatrix[i][j]++;
                }
            }
        }
        
        for(int i=0; i<custlength; i++){
            for(int j=0; j<prodlength; j++){
                custmatrix[i][j][0]/=countmatrix[i][j];
                custmatrix[i][j][1]/=countmatrix[i][j];
            }
        }
        
        return custmatrix;
    }
    
}
