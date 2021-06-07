/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custloyaltybpackage;

import inventorypackage.Regression;
import java.util.*;
import peakpackage.PeakMethods;
import simcustpackage.Customer;

/**
 *
 * @author Asus
 */
public class CustLoyBMethods {
    
    public static final int javamonths = 11;
    
    public static HashMap<Customer, Regression> getPattern(
        HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> custprod1, int type){
        
        HashMap<Customer, Regression> pattern = new HashMap<>();
        ArrayList<Calendar> dates = getDates(custprod1);
        Calendar first = dates.get(0);
        Calendar current = Calendar.getInstance();
        Calendar tempf = first;
        
        Set<Customer> keys = custprod1.keySet();
        Iterator<Customer> it = keys.iterator();
        while(it.hasNext()){
            Customer key = it.next();
            HashMap<Calendar, HashMap<String, Integer>> subcp = custprod1.get(key);
            
            switch(type){
                case 0: pattern.put(key, getDayPattern(subcp, dates));
                break;
                case 1: pattern.put(key, getWeekPattern(subcp, dates));
                break;
                case 2: pattern.put(key, getMonthPattern(subcp, dates));
                break;
                case 3: pattern.put(key, getYearPattern(subcp, dates));
                break;
                default: 
            }
        }
        
        return pattern;
    }
    
    public static ArrayList<Calendar> getDates(
            HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> custprod1){
        ArrayList<Calendar> dates = new ArrayList<>();
        
        Set<Customer> keys = custprod1.keySet();
        Iterator<Customer> it = keys.iterator();
        
        while(it.hasNext()){
            Customer key = it.next();
            
            HashMap<Calendar, HashMap<String, Integer>> subcp1 = custprod1.get(key);
            dates.addAll(subcp1.keySet());
        }
        
        Collections.sort(dates);
        ArrayList<Calendar> dates1 = new ArrayList<>();
        Iterator<Calendar> datesit = dates.iterator();
        while(datesit.hasNext()){
            dates1.add(PeakMethods.copyCal(datesit.next()));
        }
        return dates1;
    }
    
    public static Regression getDayPattern(HashMap<Calendar, HashMap<String, Integer>> hsubcp,
            ArrayList<Calendar> dates){
        //For sorting the given hashmap
        TreeMap<Calendar, HashMap<String, Integer>> subcp = new TreeMap<>();
        subcp.putAll(hsubcp);
        Regression r = null;
        
        ArrayList<Integer> dlist = new ArrayList<>();
        int count = 0;
        
        //Temporary Variable first
        Calendar first, key = Calendar.getInstance();
        first = PeakMethods.setDefCal(PeakMethods.copyCal(dates.get(0)));
        Calendar rlast = 
                PeakMethods.setDefCal(PeakMethods.copyCal(dates.get(dates.size()-1)));
        
        Set<Calendar> keys = subcp.keySet();
        Iterator<Calendar> it = keys.iterator();
                
        System.out.println("\n\nStarting Iteration");        
        while(it.hasNext()){
            key = PeakMethods.setDefCal(PeakMethods.copyCal(it.next()));
            
            System.out.println("First: "+first.getTime().toString());
            System.out.println("Key: "+key.getTime().toString());
            while(first.compareTo(key)==-1){
                System.out.println("First<Key");
                //Add previous count
                System.out.println("Added "+count);
                dlist.add(count);
                //Set count to 0 for next day
                count=0;
                //Increment first
                first.add(Calendar.DATE, 1);
            }
            count++;
        }
        System.out.println("Added "+count);
        dlist.add(count);
        count = 0;
        while(key.compareTo(rlast)<=0){
            System.out.println("RLast: "+rlast.getTime().toString());
            System.out.println("Key: "+key.getTime().toString());
            System.out.println("Added "+count);
            key.add(Calendar.DATE, 1);
            dlist.add(count);
            count = 0;
        }
        
        r = new Regression(dlist);
        
        return r;       
    }
    
    public static Regression getWeekPattern(HashMap<Calendar, HashMap<String, Integer>> hsubcp,
            ArrayList<Calendar> dates){
        //For sorting the given hashmap
        TreeMap<Calendar, HashMap<String, Integer>> subcp = new TreeMap<>();
        subcp.putAll(hsubcp);
        Regression r = null;
        
        //Main List for Regression Calculation
        ArrayList<Integer> wlist = new ArrayList<>();
        int count = 0;
        
        Calendar first, last, key = Calendar.getInstance();
        first = PeakMethods.setDefCal(PeakMethods.copyCal(dates.get(0)));
        last = PeakMethods.copyCal(first);
        int wd = last.get(Calendar.DAY_OF_WEEK);
        last.add(Calendar.DATE, 7-wd);
        
        Calendar rlast = 
                PeakMethods.setDefCal(PeakMethods.copyCal(dates.get(dates.size()-1)));
        
        Set<Calendar> keys = subcp.keySet();
        Iterator<Calendar> it = keys.iterator();
        
        System.out.println("Starting Iteration");
        while(it.hasNext()){
            key = PeakMethods.copyCal(it.next());
            key = PeakMethods.setDefCal(key);
            
            System.out.println("First: "+first.getTime().toString());
            System.out.println("Last: "+last.getTime().toString());
            System.out.println("Key: "+key.getTime().toString());
            while(last.compareTo(key)==-1){
                System.out.println("First<Key");
                //Add previous count
                System.out.println("Added "+count);
                wlist.add(count);
                //Set count to 0 for next week
                count=0;
                //Increment first and last
                first.add(Calendar.DATE, 7);
                last.add(Calendar.DATE, 7);
            }
            count++;
        }
        System.out.println("Added "+count);
        wlist.add(count);
        count = 0;
        while(last.compareTo(rlast)<=0){
            System.out.println("RLast: "+rlast.getTime().toString());
            System.out.println("Last: "+last.getTime().toString());
            System.out.println("Added "+count);
            
            wlist.add(count);
            count = 0;
            
            first.add(Calendar.DATE, 7);
            last.add(Calendar.DATE, 7);
        }
        
        r = new Regression(wlist);
        
        return r; 
    }
    
    public static Regression getMonthPattern(HashMap<Calendar, HashMap<String, Integer>> hsubcp,
            ArrayList<Calendar> dates){
        //For sorting the given hashmap
        TreeMap<Calendar, HashMap<String, Integer>> subcp = new TreeMap<>();
        subcp.putAll(hsubcp);
        Regression r = null;
        
        //Main List for Regression Calculation
        ArrayList<Integer> wlist = new ArrayList<>();
        int count = 0;
        
        Calendar first, last, key = Calendar.getInstance();
        first = PeakMethods.setDefCal(PeakMethods.copyCal(dates.get(0)));
        //Process last to get last day of the month in which first is present
        last = PeakMethods.copyCal(first);
        int firstdate = first.getActualMinimum(Calendar.DATE),
                lastdate = last.getActualMaximum(Calendar.DATE);
        last.set(Calendar.DATE, lastdate);
        
        Calendar rlast = 
                PeakMethods.setDefCal(PeakMethods.copyCal(dates.get(dates.size()-1)));
        
        Set<Calendar> keys = subcp.keySet();
        Iterator<Calendar> it = keys.iterator();
        
        System.out.println("Starting Iteration");
        while(it.hasNext()){
            key = PeakMethods.copyCal(it.next());
            key = PeakMethods.setDefCal(key);
            
            System.out.println("First: "+first.getTime().toString());
            System.out.println("Last: "+last.getTime().toString());
            System.out.println("Key: "+key.getTime().toString());
            while(last.compareTo(key)==-1){
                System.out.println("First<Key");
                //Add previous count
                System.out.println("Added "+count);
                wlist.add(count);
                //Set count to 0 for next week
                count=0;
                //Increment first and last
                first.add(Calendar.MONTH, 1);
                firstdate = first.getActualMinimum(Calendar.DATE);
                first.set(Calendar.DATE, firstdate);
                last.add(Calendar.MONTH, 1);
                lastdate = last.getActualMaximum(Calendar.DATE);
                last.set(Calendar.DATE, lastdate);
            }
            count++;
        }
        System.out.println("Added "+count);
        wlist.add(count);
        count = 0;
        while(last.compareTo(rlast)<=0){
            System.out.println("RLast: "+rlast.getTime().toString());
            System.out.println("Last: "+last.getTime().toString());
            System.out.println("Added "+count);
            
            wlist.add(count);
            count = 0;
            
            //Increment first and last
            first.add(Calendar.MONTH, 1);
            firstdate = first.getActualMinimum(Calendar.DATE);
            first.set(Calendar.DATE, firstdate);
            last.add(Calendar.MONTH, 1);
            lastdate = last.getActualMaximum(Calendar.DATE);
            last.set(Calendar.DATE, lastdate);
        }
        
        r = new Regression(wlist);
        
        return r;        
    }
    
    public static Regression getYearPattern(HashMap<Calendar, HashMap<String, Integer>> hsubcp,
            ArrayList<Calendar> dates){
        //For sorting the given hashmap
        TreeMap<Calendar, HashMap<String, Integer>> subcp = new TreeMap<>();
        subcp.putAll(hsubcp);
        Regression r = null;
        
        //Main List for Regression Calculation
        ArrayList<Integer> wlist = new ArrayList<>();
        int count = 0;
        
        Calendar first, last, key = Calendar.getInstance();
        //Process first to get first day of the year in which first is present
        first = PeakMethods.setDefCal(PeakMethods.copyCal(dates.get(0)));
        first.set(Calendar.MONTH, 1);
        first.set(Calendar.DAY_OF_MONTH, 1);
        //Process last to get last day of the year in which first is present
        last = PeakMethods.copyCal(first);
        last.set(Calendar.MONTH, 11);
        last.set(Calendar.DAY_OF_MONTH, 31);
        
        Calendar rlast = 
                PeakMethods.setDefCal(PeakMethods.copyCal(dates.get(dates.size()-1)));
        
        Set<Calendar> keys = subcp.keySet();
        Iterator<Calendar> it = keys.iterator();
        
        System.out.println("Starting Iteration");
        while(it.hasNext()){
            key = PeakMethods.copyCal(it.next());
            key = PeakMethods.setDefCal(key);
            
            System.out.println("First: "+first.getTime().toString());
            System.out.println("Last: "+last.getTime().toString());
            System.out.println("Key: "+key.getTime().toString());
            while(last.compareTo(key)==-1){
                System.out.println("First<Key");
                //Add previous count
                System.out.println("Added "+count);
                wlist.add(count);
                //Set count to 0 for next week
                count=0;
                //Increment first and last
                first.add(Calendar.YEAR, 1);
                last.add(Calendar.YEAR, 1);
            }
            count++;
        }
        System.out.println("Added "+count);
        wlist.add(count);
        count = 0;
        while(last.compareTo(rlast)<=0){
            System.out.println("RLast: "+rlast.getTime().toString());
            System.out.println("Last: "+last.getTime().toString());
            System.out.println("Added "+count);
            
            wlist.add(count);
            count = 0;
            
            first.add(Calendar.YEAR, 1);
            last.add(Calendar.YEAR, 1);
        }
        
        r = new Regression(wlist);
        
        return r;             
    }
    
    public static void printPattern(HashMap<Customer, Regression> cp){
        Set<Customer> keys = cp.keySet();
        Iterator<Customer> it = keys.iterator();
        
        while(it.hasNext()){
            Customer key = it.next();
            System.out.println(key+"->"+cp.get(key));
        }
    }
    
}
