/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package supplierpackage;

import inventorypackage.DatabaseRetriever3;
import inventorypackage.PopularityFinder;
import inventorypackage.Regression;
import java.util.*;

/**
 *
 * @author Asus
 */
public class SPopularityFinder {
    
    public static HashMap<Integer, String> supp;
    public static HashMap<Integer, HashMap<Calendar, HashMap<String, Integer>>> supptrans;
    
    //DELIVERABLE
    public static HashMap<Integer, Regression> supppop;
    public static HashMap<String, Regression> custpop = new HashMap<>();
    
    public static void main(String[] args){
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.add(Calendar.DATE, -200);
        end.add(Calendar.DATE, 10);
        ArrayList<Integer> supp = new ArrayList<>();
        supp.add(1);
        supp.add(2);
        supp.add(3);
        supp.add(4);
        main(start, end, supp);
    }
    
    //CALL THIS FUNCTION
    public static void main(Calendar start, Calendar end, 
            ArrayList<Integer> suppliers){
        DatabaseRetriever5.connectDB(start, end, suppliers);
        supp = DatabaseRetriever5.getSupp();
        supptrans = DatabaseRetriever5.getSuppTrans();
        System.out.println("Printing Supplier Transactions\n");
        SupplierMethods.printCustProd(supptrans, supp);
        DatabaseRetriever5.closeDB();
        
        DatabaseRetriever3.connectDB(start, end, null);
        
        PopularityFinder.prodass = DatabaseRetriever3.getProdAssociations();
        DatabaseRetriever3.printProdAss(PopularityFinder.prodass);
        
        PopularityFinder.dates = PopularityFinder.getDates(PopularityFinder.prodass);
        
        System.out.println("\n");
        custpop = PopularityFinder.getPopMap(PopularityFinder.prodass, 
                PopularityFinder.dates);
        
        supppop = getSuppPop(supptrans);
    }
    
    public static HashMap<Integer, Regression> getSuppPop(
            HashMap<Integer, HashMap<Calendar, HashMap<String, Integer>>> st1){
        
        HashMap<Integer, Regression> sp = new HashMap<>();
        
        Set<Integer> stkeys = st1.keySet();
        Iterator<Integer> stit = stkeys.iterator();
        while(stit.hasNext()){
            int stkey = stit.next();
            HashMap<Calendar, HashMap<String, Integer>> subst = st1.get(stkey);
            
            System.out.println(stkey+"->"+getPop(subst, custpop));         
        }
        
        return sp;
    }
    
    public static Regression getPop(HashMap<Calendar, HashMap<String, Integer>> sst,
            HashMap<String, Regression> cpop){
        if((cpop==null)||(sst==null)){
            return null;
        }
        
        //Dummy ArrayList for those products with no regression
        ArrayList<Integer> dummy = new ArrayList<>();
        
        Regression r = null;
        ArrayList<Integer> n1 = new ArrayList<>();
        ArrayList<Regression> r1 = new ArrayList<>();
        
        Set<Calendar> keys = sst.keySet();
        Iterator<Calendar> it = keys.iterator();
        while(it.hasNext()){
            Calendar key = it.next();
            
            HashMap<String, Integer> subsst = sst.get(key);
            
            Set<String> subkeys = subsst.keySet();
            Iterator<String> subit = subkeys.iterator();
            while(subit.hasNext()){
                String subkey = subit.next();
                n1.add(subsst.get(subkey));
                if(cpop.get(subkey)==null){
                    r1.add(new Regression(dummy));
                }
                else{
                    r1.add(cpop.get(subkey));
                }
                //System.out.println(subkey+" -> "+cpop.get(subkey));
            }
        }
        
        r = new Regression(n1, r1);
        
        return r;
    }
    
}
