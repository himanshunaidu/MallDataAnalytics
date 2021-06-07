/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inventorypackage;

import mainpackage.Transactions;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author Asus
 */
public class PopularityFinder {
    
    
    //Association of Product to {Association of Date to Sales}
    public static HashMap<String, HashMap<Calendar, Integer>> prodass = 
            new HashMap<String, HashMap<Calendar, Integer>>();
    public static ArrayList<Calendar> dates = new ArrayList<Calendar>();
    
    //DELIVERABLE
    public static HashMap<String, Regression> pop = new HashMap<>();
    
    public static Calendar start;
    
    public static void main(String[] args){
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.add(Calendar.DATE, -200);
        end.add(Calendar.DATE, 10);
        HashMap<String, ArrayList<String>> prod = new HashMap<>();
        ArrayList<String> a1 = new ArrayList<>(),
        		a2 = new ArrayList<>();
        a1.add("Arrow");
        a1.add("BlackBerrys");
        a1.add("fulls");
        a2.add("ccc");
        prod.put("ss", a2);
        prod.put("shirt", a1);
        main(start, end, prod);
    }
    
    //CALL THIS FUNCTION 
    public static void main(Calendar start, Calendar end, 
            HashMap<String, ArrayList<String>> prod){
        DatabaseRetriever3.connectDB(start, end, prod);
        
        prodass = DatabaseRetriever3.getProdAssociations();
        DatabaseRetriever3.printProdAss(prodass);
        
        dates = getDates(prodass);
        
        System.out.println("\n");
        pop = getPopMap(prodass, dates);
        sortPopMap(pop);
        DatabaseRetriever3.printPop(pop);
        
        //get start date
        start = Calendar.getInstance();
        start.set(Calendar.YEAR, 2015);
        
        
    }
    
    public static HashMap<String, Regression> getPopMap(
        HashMap<String, HashMap<Calendar, Integer>> pa, ArrayList<Calendar> dates1){
        
        HashMap<String, Regression> pop1 = new HashMap<String, Regression>();
        
        Set<String> keys = pa.keySet();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()){
            String key = it.next();
            
            HashMap<Calendar, Integer> subpa = pa.get(key);
            
            ArrayList<Integer> list = processPop(subpa, dates1);
            Regression r = getPop(list);
            pop1.put(key, r);
        }
        
        return pop1;
    }
    
    public static ArrayList<Calendar> getDates(HashMap<String, HashMap<Calendar, Integer>> pa){
        HashSet<Calendar> dates2 = new HashSet<>();
        
        Set<String> keys = pa.keySet();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()){
            String key = it.next();
            HashMap<Calendar, Integer> subpa = pa.get(key);
            Set<Calendar> keys1 = subpa.keySet();   
            dates2.addAll(keys1);
        }
        
        ArrayList<Calendar> dates1 = new ArrayList<>(dates2);
        Collections.sort(dates1);
        return dates1;
    }
    
    public static void sortPopMap(HashMap<String, Regression> popmap){
        Set<Entry<String, Regression>> set = popmap.entrySet();
        List<Entry<String, Regression>> list = new ArrayList<>(set);
        
        Collections.sort(list, new Comparator<Map.Entry<String, Regression>>(){
            @Override
            public int compare(Map.Entry<String, Regression> o1, Map.Entry<String, Regression> o2) {
                return o1.getValue().gt(o2.getValue());
            }
            
        });
        
        Iterator<Entry<String, Regression>> it = list.iterator();
        while(it.hasNext()){
            Entry<String, Regression> en = it.next();
            //System.out.println(en.getKey()+" "+en.getValue());
        }
    }
    
    public static ArrayList<Integer> processPop(HashMap<Calendar, Integer> subpa, ArrayList<Calendar> d){
        double value = 0;
        ArrayList<Integer> pp = new ArrayList<Integer>();
        
        Iterator<Calendar> it = d.iterator();
        while(it.hasNext()){
            Calendar d1 = it.next();
            Integer i1 = subpa.get(d1);
            if(i1==null){
                pp.add(0);
            }
            else{
                pp.add(i1);
            }
        }
        
        return pp;
    }
    
    public static Regression getPop(ArrayList<Integer> pp1){
        Regression r = new Regression(pp1);
        return r;
    }
    
}
