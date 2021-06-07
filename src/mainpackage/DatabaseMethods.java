/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage;

import inventorypackage.Regression;
import java.util.*;

/**
 *
 * @author Asus
 */
public class DatabaseMethods {
    public static final String DATABASE_URL = "jdbc:mysql://localhost:3306/cloth?useSSL=false";    
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

    public static void printPop(HashMap<String, Regression> pop1) {
        Set<String> keys = pop1.keySet();
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            String prod = it.next();
            System.out.println(prod + ": " + pop1.get(prod));
        }
    }

    public static void printProdAss(HashMap<String, HashMap<Calendar, Integer>> prodass1) {
        Set<String> keys = prodass1.keySet();
        Iterator<String> it = keys.iterator();
        HashMap<Calendar, Integer> subprodass1 = new HashMap<Calendar, Integer>();
        while (it.hasNext()) {
            String prod = it.next();
            System.out.print(prod + ": \n");
            subprodass1 = prodass1.get(prod);
            Set<Calendar> keys1 = subprodass1.keySet();
            Iterator<Calendar> it1 = keys1.iterator();
            while (it1.hasNext()) {
                Calendar date = it1.next();
                System.out.println(date.getTime().toString() + "=> " + subprodass1.get(date));
            }
        }
    }
    
}
