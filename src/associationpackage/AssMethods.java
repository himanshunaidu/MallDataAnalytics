/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package associationpackage;

import mainpackage.Transactions;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Asus
 */
public class AssMethods {
    
    //Find if set1 is present in set2
    public static boolean findPresent(String[] set1, String[] set2){
        if(set1==null||set2==null){
            return false;
        }
        int length1 = set1.length, length2 = set2.length;
        int count1 = 0;
        for(int i=0; i<length1; i++){
            for(int j=0; j<length2; j++){
                if(set1[i].equals(set2[j])){
                    count1++;
                    break;
                }
            }
        }
        if(count1==length1){
            return true;
        }
        return false;
    }
    
    public static Calendar getCalendar(String sdate){
        String[] sdates = sdate.split(" |-|:|\\.");
        int[] adates = new int[sdates.length];
        for(int i=0; i<adates.length; i++){
            adates[i] = Integer.parseInt(sdates[i]);
        }
        Calendar d = Calendar.getInstance();
        d.set(adates[Transactions.yeardex], adates[Transactions.mondex]-1,
            adates[Transactions.daydex], 0, 0, 0);
        
        if(adates.length>3){
            d.set(Calendar.HOUR_OF_DAY, adates[Transactions.hindex]);
            d.set(Calendar.MINUTE, adates[Transactions.mindex]);
            d.set(Calendar.SECOND, adates[Transactions.secdex]);
        }
        d.set(Calendar.MILLISECOND, 0);
        
        return d;
    }
    
    public static Calendar getCfromTime(String time){
        String[] stimes = time.split(" |-|:|\\.");
        int[] atimes = new int[stimes.length];
        for(int i=0; i<atimes.length; i++){
            atimes[i] = Integer.parseInt(stimes[i]);
        }
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, atimes[Transactions.finhour]);
        c.set(Calendar.MINUTE, atimes[Transactions.finmin]);
        c.set(Calendar.SECOND, atimes[Transactions.finsec]);
        return c;
    }
    
    public static Calendar getCfromDate(String date){
        String[] sdates = date.split(" |-|:|\\.");
        int[] adates = new int[sdates.length];
        for(int i=0; i<adates.length; i++){
            adates[i] = Integer.parseInt(sdates[i]);
        }
        Calendar d = Calendar.getInstance();
        d.set(adates[Transactions.yeardex], adates[Transactions.mondex]-1,
            adates[Transactions.daydex]);
        
        return d;
    }
    
    public static boolean findEqual(String[] set1, String[] set2){
        int length1 = set1.length, length2 = set2.length;
        int count1 = 0;
        if(length1!=length2){
            return false;
        }
        for(int i=0; i<length1; i++){
            for(int j=0; j<length2; j++){
                if(set1[i].equals(set2[j])){
                    count1++;
                    break;
                }
            }
        }
        if(count1==length1){
            return true;
        }
        return false;
    }
    
    public static void printItemsets(ArrayList<String[]> items){
        Iterator<String[]> it = items.iterator();
        while(it.hasNext()){
            String[] arr = it.next();
            int length = arr.length;
            for(int i=0; i<length; i++){
                System.out.print(arr[i]+" ");
            }
            System.out.print("\n");
        }
    }
    
    public static void printFrequent(HashMap<String[], Integer> freq){
        Set<String[]> keys = freq.keySet();
        Iterator<String[]> it = keys.iterator();
        while(it.hasNext()){
            String[] arr = it.next();
            int length = arr.length;
            for(int i=0; i<length; i++){
                System.out.print(arr[i]+", ");
            }
            System.out.print("=> "+freq.get(arr));
            System.out.print("\n");
        }
    }
    
    public static void printAllAssociations(HashMap<String[], HashMap<String[], Double>> ass){
        Set<String[]> keys = ass.keySet();
        Iterator<String[]> it = keys.iterator();
        while(it.hasNext()){
            String[] arr = it.next();
            HashMap<String[], Double> ass1 = ass.get(arr);
            Set<String[]> keys2 = ass1.keySet();
            Iterator<String[]> it2 = keys2.iterator();
            while(it2.hasNext()){
                String[] arr2 = it2.next();
                double confidence = ass1.get(arr2);
                
                printArray(arr);
                System.out.print("=>");
                printArray(arr2);
                System.out.print(": "+confidence+"\n");
                //System.out.print(arr+"=>"+arr2+": "+confidence+"\n");
            }
        }
    }
    
    public static void printQuantAssociations
        (HashMap<HashMap<String, Integer>, HashMap<HashMap<String, Integer>, Double>> qa){
            Set<HashMap<String, Integer>> keys = qa.keySet();
            Iterator<HashMap<String, Integer>> it = keys.iterator();
            
            while(it.hasNext()){
                HashMap<String, Integer> key = it.next();
                
                HashMap<HashMap<String, Integer>, Double> value = qa.get(key);
                Set<HashMap<String, Integer>> keys1 = value.keySet();
                Iterator<HashMap<String, Integer>> it1 = keys1.iterator();
                
                while(it1.hasNext()){
                    HashMap<String, Integer> key1 = it1.next();
                    
                    Set<String> keykey = key.keySet();
                    Iterator<String> keyit = keykey.iterator();
                    while(keyit.hasNext()){
                        String keyitst = keyit.next();
                        System.out.print(keyitst+"["+key.get(keyitst)+"], ");
                    }
                    System.out.print(": ");
                    Set<String> keykey1 = key1.keySet();
                    Iterator<String> keyit1 = keykey1.iterator();
                    while(keyit1.hasNext()){
                        String keyitst1 = keyit1.next();
                        System.out.print(keyitst1+"["+key1.get(keyitst1)+"], ");
                    }
                    System.out.print(value.get(key1)+"\n");
                }
            }
        }
    
    public static void printArray(String[] arr){
        int length = arr.length;
        System.out.print("[");
        for(int i=0; i<(length); i++){
            System.out.print(arr[i]+", ");
        }
        System.out.print("]");
    }
}
