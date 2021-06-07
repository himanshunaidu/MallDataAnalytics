/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supplierpackage;

import java.util.*;
import simcustpackage.Customer;

/**
 *
 * @author Asus
 */
public class SupplierMethods {
    
    public static void printCustProd(
            HashMap<Integer, HashMap<Calendar, HashMap<String, Integer>>> supptrans1, 
            HashMap<Integer, String> supp){
        
        Set<Integer> keys = supptrans1.keySet();
        Iterator<Integer> it = keys.iterator();
        HashMap<Calendar, HashMap<String, Integer>> subsupptrans1 = 
                new HashMap<Calendar, HashMap<String, Integer>>();
        HashMap<String, Integer> subsupptrans2 = new HashMap<String, Integer>();
        
        while(it.hasNext()){
            Integer prod = it.next();
            System.out.print(prod+"-"+supp.get(prod)+": \n");
            
            subsupptrans1 = supptrans1.get(prod);
            Set<Calendar> keys1 = subsupptrans1.keySet();
            Iterator<Calendar> it1 = keys1.iterator();
            while(it1.hasNext()){
                Calendar date = it1.next();
                System.out.println("\t"+date.getTime().toString()+"=>");
                
                subsupptrans2 = subsupptrans1.get(date);
                Set<String> keys2 = subsupptrans2.keySet();
                Iterator<String> it2 = keys2.iterator();
                
                while(it2.hasNext()){
                    String item = it2.next();
                    System.out.println("\t\t"+item+" "+subsupptrans2.get(item));
                }
            }
        }
    }
    
}
