/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package associationpackage;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Asus
 */
public class Transactions {
    
    public static String[] products = {"l1", "l2", "l3", "l4", "l5"};
    
    public static String[][] trans = {{"l1", "l2", "l5"}, {"l2", "l4"}, 
        {"l2", "l3"}, {"l1", "l2", "l4"}, {"l1", "l3"}, {"l2", "l3"}, 
        {"l1", "l3"}, {"l1", "l2", "l3", "l5"}, {"l1", "l2", "l3"}};
    
    public static HashMap<Integer, HashMap<String, Integer>> transquant = 
            new HashMap<Integer, HashMap<String, Integer>>();
    
    public static String[][] trans1;
    
    //Array of date and timings of each transaction respectively to the array above
    public static String[] dates = {"2018-05-06 06:11:30", "2018-05-06 08:10:30",
        "2018-05-06 10:19:30", "2018-05-06 11:11:30", "2018-05-06 11:13:30", "2018-05-07 12:11:30",
        "2018-05-07 17:17:30", "2018-05-08 19:21:30", "2018-05-08 12:29:30"};
    
    public static Calendar[] ddates = new Calendar[dates.length];
    
    //Array of holidays
    public static String[] holidays ={"2018-05-06", "2018-05-08", "2018-07-07"};
    
    public static Calendar[] cholidays = new Calendar[holidays.length];
    
    //Arrays of start timings and end timings for each interval in daypeak
    public static String[] intimes = {"06:00:00", "10:00:00", "14:00:00",
        "18:00:00"};
    public static String[] fintimes = {"10:00:00", "14:00:00", "18:00:00",
        "22:00:00"};
    
    public static Calendar[] cintimes = new Calendar[intimes.length];
    public static Calendar[] cfintimes = new Calendar[fintimes.length];
    
    //Indexes of hours, minutes, seconds etc. for different arrays
    public static int yeardex = 0, mondex = 1, daydex = 2, hindex = 3,
            mindex = 4, secdex = 5;
    public static int finhour = 0, finmin = 1, finsec = 2;
    
    //Number of ---
    public static int numhours = 24, numdays = 7;
    
    //For Holidays: Compare with respective weekdays or just average of all days
    //or with average of nonholidays
    public static boolean cmpwd = true, cmpave = false, cmpavenh;
    
    public static void initTransactions(){
        DatabaseRetriever1.connectDB();
        products = DatabaseRetriever1.retrieveProd();
        trans = DatabaseRetriever1.retrieveTrans();
        transquant = DatabaseRetriever1.retrieveTransQuant();
        ddates = DatabaseRetriever1.retrieveDates();
        
        for(int i=0; i<holidays.length; i++){
            cholidays[i] = AssMethods.getCfromDate(holidays[i]);
        }
        
        for(int i=0; i<intimes.length; i++){
            cintimes[i] = AssMethods.getCfromTime(intimes[i]);
            cfintimes[i] = AssMethods.getCfromTime(fintimes[i]);
        }
        DatabaseRetriever1.closeDB();
    }
    
    /*public static void main(String[] args){
        initTransactions();
        DatabaseRetriever1.printTransQuant(transquant);
    }*/
    
}
