/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custloyaltyapackage;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import simcustpackage.Customer;

/**
 *
 * @author Asus
 */
public class CustLoyAMethods {
    
    public static HashMap<Calendar, HashMap<String, Integer>> findCustProdPresent(
            HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> custprod1, 
            Customer cust){
        Set<Customer> keys = custprod1.keySet();
        Iterator<Customer> it = keys.iterator();
        while(it.hasNext()){
            Customer cust1 = it.next();
            if(cust1.equals(cust)){
                return custprod1.get(cust1);
            }
        }
        return null;
    }
    
    public static void printCustProd(
            HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> custprod1){
        
        Set<Customer> keys = custprod1.keySet();
        Iterator<Customer> it = keys.iterator();
        HashMap<Calendar, HashMap<String, Integer>> subcustprod1 = 
                new HashMap<Calendar, HashMap<String, Integer>>();
        HashMap<String, Integer> subcustprod2 = new HashMap<String, Integer>();
        
        while(it.hasNext()){
            Customer prod = it.next();
            System.out.print(prod+": \n");
            
            subcustprod1 = custprod1.get(prod);
            Set<Calendar> keys1 = subcustprod1.keySet();
            Iterator<Calendar> it1 = keys1.iterator();
            while(it1.hasNext()){
                Calendar date = it1.next();
                System.out.println("\t"+date.getTime().toString()+"=>");
                
                subcustprod2 = subcustprod1.get(date);
                Set<String> keys2 = subcustprod2.keySet();
                Iterator<String> it2 = keys2.iterator();
                
                while(it2.hasNext()){
                    String item = it2.next();
                    System.out.println("\t\t"+item+subcustprod2.get(item));
                }
            }
        }
    }
    
    public static Calendar getDealDate(Calendar buy, int exp){
        //int add = (int)Math.sqrt(exp);
        int add = exp/2; //CHANGE DEAL DATE FUNCTION ACCORDING TO WISH
        Calendar deal = Calendar.getInstance();
        deal.set(buy.get(Calendar.YEAR), buy.get(Calendar.MONTH), 
                buy.get(Calendar.DAY_OF_MONTH), buy.get(Calendar.HOUR_OF_DAY), 
                buy.get(Calendar.MINUTE), buy.get(Calendar.SECOND));
        deal.add(Calendar.DATE, add);
        return deal;
    }
    
    public static void printLatest(
            HashMap<Customer, HashMap<String, Calendar>> lb){
        
        Set<Customer> keys = lb.keySet();
        Iterator<Customer> it = keys.iterator();
        HashMap<String, Calendar> sublb = 
                new HashMap<String, Calendar>();
        
        while(it.hasNext()){
            Customer prod = it.next();
            System.out.print(prod+": \n");
            
            sublb = lb.get(prod);
            Set<String> keys1 = sublb.keySet();
            Iterator<String> it1 = keys1.iterator();
            while(it1.hasNext()){
                String date = it1.next();
                System.out.println("\t"+date+"=>"+sublb.get(date).getTime().toString());
            }
        }
    }
    
    public static void printProdExpiry(HashMap<String, Integer> prodexp){
        Set<String> keys1 = prodexp.keySet();
        Iterator<String> it1 = keys1.iterator();
        while(it1.hasNext()){
            String prod = it1.next();
            System.out.println("\t"+prod+"=>"+prodexp.get(prod));
        }
    }
    
}
