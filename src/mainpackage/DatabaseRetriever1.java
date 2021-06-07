/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage;

import associationpackage.AssMethods;
import associationpackage.AssMethods;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Asus
 */
public class DatabaseRetriever1 {
    
    public static final String DATABASE_URL = "jdbc:mysql://localhost:3306/h_cloth?useSSL=false";
    public static boolean connected = false;
    public static Connection connection = null;
    public static Statement statement = null;
    public static PreparedStatement pstatement = null;
    public static PreparedStatement rstatement = null;
    public static ResultSet resultset = null;
    public static ResultSetMetaData metadata = null;
    
    public static final String[] tables = {"creditcustomerbill", "otherbill"};
    
    static int[] id, num, bills;
    static double[] lat, lon;
    static String[] date, time;
    
    public static String translengthquery = "Select max(BillNo) from ";
    public static String transquery = "select BillNo, ItemName, CategoryName from ";
    
    public static String datelimitquery = "";
    public static final int DLQ = 1;
    public static String prodlimitquery = "";
    public static final int PLQ = 2;
    
    public static String transquantquery = "select BillNo, ItemName, Quantity, CategoryName from ";
    
    public static String prodlengthquery = "Select count(ItemName) from stock_details ";
    public static String prodquery = "Select ItemName, CategoryName from stock_details ";
    
    public static String datesquery = "select BillNo, DateTime from ";
    
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
    
    public static String[][] retrieveTrans(){
        int length = 0;
        String[][] trans;
        /*System.out.println(transquery+tables[0]+" where "+
                        datelimitquery+" AND "+prodlimitquery);*/
        try{
            //Length of the trans String 2-d array
            for(String s: tables){
                resultset = statement.executeQuery(translengthquery+s+" where "+
                        datelimitquery+" AND "+prodlimitquery);
                resultset.next();
                length+=resultset.getInt(1);
            }
            trans = new String[length][];           
            
            //Length of each 1-d array in the trans String 2-d array
            /*int index = 0;
            for(String s: tables){
                resultset = statement.executeQuery("Select BillNo, count(ItemName) from "
                    +s+" group by BillNo;");
                while(resultset.next()){
                    int length1 = resultset.getInt(2);
                    trans[index]=new String[length1];
                    System.out.println(length1);
                    index++;
                }
            }*/
            
            //Store in each 1-d array in the trans String 2-d array
            Set<String> subtrans = new HashSet<String>();
            int index = 0, temp=-1;
            System.out.println(transquery+tables[0]+" where "+
                        datelimitquery+" AND "+prodlimitquery);
            for(String s: tables){
                index = -1;
                resultset = statement.executeQuery(transquery+s+" where "+
                        datelimitquery+" AND "+prodlimitquery);
                while(resultset.next()){
                    if(index!=resultset.getInt(1)){
                        index = resultset.getInt(1);
                        if(temp!=-1){
                            trans[temp] = subtrans.toArray(new String[0]);
                        }
                        temp++;
                        subtrans = new HashSet<String>();
                    }
                    /*if(checkDistinct(subtrans, subindex, resultset.getString(2))){
                        System.out.println(temp+" "+subindex+" "+resultset.getString(2));
                        subtrans[subindex] = resultset.getString(2);
                        subindex++;
                    }*/
                    subtrans.add(resultset.getString(2)+"("+resultset.getString(3)+")");
                }
            }
            if(temp!=-1){
            trans[temp] = subtrans.toArray(new String[0]);} 
            return trans;
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static Calendar[] retrieveDates(){
        int length = 0;
        Calendar[] dates;
        try{
            //Length of the trans String 2-d array
            for(String s: tables){
                resultset = statement.executeQuery(translengthquery+s
                        +" where "+datelimitquery+" AND "+prodlimitquery);
                resultset.next();
                length+=resultset.getInt(1);
            }
            dates = new Calendar[length];    
            
            //Store in each 1-d array in the trans String 2-d array
            Set<String> subdates = new HashSet<String>();
            int index = 0, temp=0;
            for(String s: tables){
                index = -1;
                resultset = statement.executeQuery(datesquery+s+
                        " where "+datelimitquery+
                        " AND "+prodlimitquery);
                while(resultset.next()){
                    if(index!=resultset.getInt(1)){
                        index = resultset.getInt(1);
                        if(temp!=-1){
                            dates[temp] = AssMethods.getCalendar(resultset.getString(2));
                            //System.out.println(temp+"=>"+dates[temp]);
                        }
                        temp++;
                    }
                    /*if(checkDistinct(subtrans, subindex, resultset.getString(2))){
                        System.out.println(temp+" "+subindex+" "+resultset.getString(2));
                        subtrans[subindex] = resultset.getString(2);
                        subindex++;
                    }*/
                }
            }
            return dates;
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static HashMap<Integer, HashMap<String, Integer>> retrieveTransQuant(){
        int length = 0;
        String[][] trans = new String[0][];
        HashMap<Integer, HashMap<String, Integer>> transquant =
                new HashMap<Integer, HashMap<String, Integer>>();
        
        try{                                
            
            //Store in each bill number the product and its quantity
            HashMap<String, Integer> subtransquant = new HashMap<String, Integer>();
            int index = 0, temp=-1;
            for(String s: tables){
                index = -1;
                resultset = statement.executeQuery(transquantquery
                        +s+" where "+datelimitquery+" AND "+prodlimitquery);
                while(resultset.next()){
                    if(index!=resultset.getInt(1)){
                        index = resultset.getInt(1);
                        if(temp!=-1){
                            transquant.put(temp, subtransquant);
                        }
                        temp++;
                        subtransquant = new HashMap<String, Integer>();
                    }
                    if(subtransquant.get(resultset.getString(2)+"("+resultset.getString(4)+")")==null){
                        subtransquant.put(resultset.getString(2)+"("+resultset.getString(4)+")", 
                                resultset.getInt(3));
                    }
                    else{
                        subtransquant.put(resultset.getString(2)+"("+resultset.getString(4)+")", 
                                subtransquant.get(resultset.getString(2)+"("+resultset.getString(4)+")")
                                        +resultset.getInt(3));
                    }
                }
            }
            transquant.put(temp, subtransquant);
            System.out.println("\n\n");
            return transquant;
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /*public static void main(String[] args){
        connectDB();
        String[][] trans = retrieveTrans();
        HashMap<Integer, HashMap<String, Integer>> transquant = retrieveTransQuant();
        String[] prod = retrieveProd();
        closeDB();
        AssMethods.printArray(prod);
        System.out.println("\n\n");
        printTrans(trans);
        System.out.println("\n\n");
        printTransQuant(transquant);
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
    
    public static boolean checkDistinct(String[] array, int index, String ele){
        int i=0;
        for(i=0; i<index; i++){
            if(ele.equals(array[i])){
                return false;
            }
        }
        return true;
    }
    
    public static void printTrans(String[][] trans){
        int i=0, j=0;
        try{
        for(i=0; i<trans.length; i++){
            for(j=0; j<trans[i].length; j++){
                System.out.print(trans[i][j]+" ");
            }
            System.out.print("\n");
        }
        }
        catch(NullPointerException e){
            e.printStackTrace();
            System.out.println(i+" "+j);
        }
    }
    
    public static void printTransQuant(HashMap<Integer, HashMap<String, Integer>> tq){
        Set<Integer> keys = tq.keySet();
        Iterator<Integer> it = keys.iterator();
        while(it.hasNext()){
            int key = it.next();
            HashMap<String, Integer> q = tq.get(key);
            System.out.println(key+":");
            Set<String> keys1 = q.keySet();
            Iterator<String> it1 = keys1.iterator();
            while(it1.hasNext()){
                String key1 = it1.next();
                System.out.println(key1+"- "+q.get(key1));
            }
        }
    }
    
}
