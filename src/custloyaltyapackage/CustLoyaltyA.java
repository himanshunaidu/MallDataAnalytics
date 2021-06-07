/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custloyaltyapackage;

import java.util.*;
import simcustpackage.Customer;

/**
 *
 * @author Asus
 */
public class CustLoyaltyA {
	
	//DELIVERABLE
	public static HashMap<Customer, HashMap<String, Calendar>> ld = new HashMap<>();
    
    public static void main(String[] args){
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.add(Calendar.DATE, -200);
        end.add(Calendar.DATE, 10);
        HashMap<String, String> cust = new HashMap<>();
        cust.put("Amit", "9876543210");
        cust.put("shilpa", "1234567890");
        main(start, end, cust);
    }
    
    //CALL THIS FUNCTION
    public static void main(Calendar start, Calendar end, 
            HashMap<String, String> customers){
        DatabaseRetriever4.connectDB(start, end, customers);
        Customer[] cust = DatabaseRetriever4.retrieveCustList();
        HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> custprod = 
                DatabaseRetriever4.getCustProd();
        HashMap<String, Integer> prodexp = DatabaseRetriever4.getProdExpiry();
        CustLoyAMethods.printProdExpiry(prodexp);
        System.out.println("\n\n");
        
        CustLoyAMethods.printCustProd(custprod);
        System.out.println("\n\n");
        
        HashMap<Customer, HashMap<String, Calendar>> lb = 
                getLatestBuy(custprod);
        CustLoyAMethods.printLatest(lb);
        System.out.println("\n\n");
        
        System.out.println("Deal Dates");
        //HashMap<Customer, HashMap<String, Calendar>> ld =
        ld = getDealDates(custprod, prodexp);
        CustLoyAMethods.printLatest(ld);
        
        DatabaseRetriever4.closeDB();
    }
    
    //Get the final deal dates
    public static HashMap<Customer, HashMap<String, Calendar>> getDealDates(
        HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> custprod1,
            HashMap<String, Integer> prodexp1){
        //final deal dates
        HashMap<Customer, HashMap<String, Calendar>> dealdates = 
                new HashMap<Customer, HashMap<String, Calendar>>();
        //sub map to deal dates
        HashMap<String, Calendar> subdd = new HashMap<String, Calendar>();
        
        //Latest Buy
        HashMap<Customer, HashMap<String, Calendar>> lb = 
                getLatestBuy(custprod1);
        
        Set<Customer> keys = lb.keySet();
        Iterator<Customer> it = keys.iterator();
        while(it.hasNext()){
            subdd = new HashMap<String, Calendar>();
            
            Customer key = it.next();
            HashMap<String, Calendar> sublb = lb.get(key);
            
            Set<String> keys1 = sublb.keySet();
            Iterator<String> it1 = keys1.iterator();
            while(it1.hasNext()){
                String key1 = it1.next();
                
                Calendar buy = sublb.get(key1);
             
                Calendar deal = CustLoyAMethods.getDealDate(buy, 
                        prodexp1.get(key1));
                
                //System.out.println(buy.getTime().toString()+"->"+prodexp1.get(key1));
                
                subdd.put(key1, deal);
            }
            dealdates.put(key, subdd);
        }
        
        return dealdates;
        
    }
    
    public static HashMap<Customer, HashMap<String, Calendar>> getLatestBuy(
        HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> custprod1){
        HashMap<Customer, HashMap<String, Calendar>> latestbuy = 
                new HashMap<Customer, HashMap<String, Calendar>>();
        
        Set<Customer> keys = custprod1.keySet();
        Iterator<Customer> it = keys.iterator();
        while(it.hasNext()){
            Customer key = it.next();
            HashMap<Calendar, HashMap<String, Integer>> subcustprod1 = 
                    custprod1.get(key);
            HashMap<String, Calendar> sublb = latestbuy.get(key);
            
            
            Set<Calendar> keys1 = subcustprod1.keySet();
            Iterator<Calendar> it1 = keys1.iterator();
            while(it1.hasNext()){
                Calendar key1 = it1.next();
                HashMap<String, Integer> subcust2prod1 = subcustprod1.get(key1);
                
                Set<String> keys2 = subcust2prod1.keySet();
                Iterator<String> it2 = keys2.iterator();
                
                while(it2.hasNext()){
                    String key2 = it2.next();
                    
                    if(sublb==null){
                        sublb = new HashMap<String, Calendar>();
                        sublb.put(key2, key1);
                    }
                    else{
                    if(sublb.get(key2)==null){
                        sublb.put(key2, key1);
                    }
                    else{
                        if(key1.compareTo(sublb.get(key2))>=0){
                            sublb.put(key2, key1);
                        }
                    }}
                    latestbuy.put(key, sublb);
                }
            }
        }
        
        return latestbuy;
    }
    
    
    
}
