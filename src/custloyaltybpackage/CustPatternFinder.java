/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custloyaltybpackage;

import custloyaltyapackage.CustLoyAMethods;
import custloyaltyapackage.DatabaseRetriever4;
import inventorypackage.Regression;
import java.util.*;
import simcustpackage.Customer;

/**
 *
 * @author Asus
 */
public class CustPatternFinder {
	
	//DELIVERABLE
	public static HashMap<Customer, Regression> pattern = new HashMap<>();
    
    public static void main(String[] args){
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.add(Calendar.DATE, -20);
        end.add(Calendar.DATE, -10);
        HashMap<String, String> cust = new HashMap<>();
        cust.put("Amit", "9876543210");
        cust.put("shilpa", "1234567890");
        cust.put("ssss", "1234567890");
        cust.put("Amit", "987654321");
        main(start, end, cust);
    }
    
    //CALL THIS FUNCTION
    public static void main(Calendar start, Calendar end, 
            HashMap<String, String> customers){
        DatabaseRetriever4.connectDB(start, end, customers);
        Customer[] cust = DatabaseRetriever4.retrieveCustList();
        HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> custprod = 
                DatabaseRetriever4.getCustProd();
        CustLoyAMethods.printCustProd(custprod);
        
        //HashMap<Customer, Regression> pattern = CustLoyBMethods.getPattern(custprod, 1);
        pattern = CustLoyBMethods.getPattern(custprod, 1);
        CustLoyBMethods.printPattern(pattern);
        //HashMap<Customer, Double> pattern = getPattern(custprod);
    }
    
}
