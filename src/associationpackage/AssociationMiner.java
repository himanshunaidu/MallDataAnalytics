/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package associationpackage;

import mainpackage.Transactions;
import java.util.*;
/**
 *
 * @author Asus
 */
public class AssociationMiner {
    
    public static final int sup = 1;
    public static final double con = 0.5;
    
    public static int mainLength = 0;
    public static ArrayList<String[]> itemsets= new ArrayList<String[]>();
    public static HashMap<String[], Integer> frequent = new HashMap<String[], Integer>();
    public static ArrayList<String[]> transactions = new ArrayList<String[]>();
    
    //DELIVERABLE
    public static HashMap<String[], HashMap<String[], Double>> associations =
            new HashMap<String[], HashMap<String[], Double>>();
    //DELIVERABLE
    public static HashMap<HashMap<String, Integer>, HashMap<HashMap<String, Integer>, Double>> 
            quantassociations = 
            new HashMap<HashMap<String, Integer>, HashMap<HashMap<String, Integer>, Double>>();
    
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
    
    //CALL THIS FUNCTION. SET PARAMETERS IN HERE
    public static void main(Calendar start, Calendar end, 
            HashMap<String, ArrayList<String>> prod){
        Transactions.initTransactions(start, end, prod);
        
        /*AssMethods.printArray(Transactions.products);
        System.out.println();
        for (String[] tran : Transactions.trans) {
            AssMethods.printArray(tran);
            System.out.println();
        }*/
        itemsets = Apriori.initItems1(Transactions.products);
        transactions = Apriori.initTrans1(Transactions.trans);
        frequent = Apriori.getSet();
        //System.out.println("\n\nPrinting Frequent Items List");
        //AssMethods.printFrequent(frequent);
        
        mainLength = Transactions.trans.length;
        associations = getAllAssociations(frequent);
        System.out.println("\n\nPrinting all associations");
        AssMethods.printAllAssociations(associations);
        
        System.out.println("\n\nPrinting all quant associations");
        quantassociations = 
                QuantAssociationMiner.getAllQuantAssociations(associations, Transactions.transquant, 
                Transactions.trans);
        AssMethods.printQuantAssociations(quantassociations);
    }
        
    private static HashMap<String[], HashMap<String[], Double>> 
        getAllAssociations(HashMap<String[], Integer> freq){
            HashMap<String[], HashMap<String[], Double>> ass =
                    new HashMap<String[], HashMap<String[], Double>>();
            
            Set<String[]> set = freq.keySet();
            Iterator<String[]> it = set.iterator();
            while(it.hasNext()){
                getAllSubsets(it.next(), ass);
            }
            
            return ass;
    }
        
    private static void getAllSubsets(String[] arr, HashMap<String[], HashMap<String[], Double>> ass) {
        int size = arr.length;
        int binaryLimit = (1 << size) - 1;
        Set<String[]> subsets = new HashSet<String[]>();
        Set<String> arrset = new HashSet<String>();
        double confidence = 0;
        
        //Convert String array arr to Set
        for(int i=0; i<arr.length; i++){
            arrset.add(arr[i]);
        }
        
        for (int i = 1; i <= binaryLimit; i++) {
            int index = size - 1;
            int num = i;
            Set<String> el = new HashSet<String>();
            Set<String> ell = new HashSet<String>();
            while (num > 0) {
                //Get subset in el and its association in ell
                if ((num & 1) == 1) {
                    el.add(arr[index]);
                    //System.out.print(arr[index]);
                }
                
                
                index--;
                num >>= 1;
                
            }
            
            //Association: el->ell=confidence; confidence=sup(el+ell)/sup(el);
            ell.addAll(arrset);
            ell.removeAll(el);
            
            if(el.size()==arr.length){
                continue;
            }
            //Extract from Frequent
            if(extractFromFrequent(frequent, el.toArray(new String[el.size()]))==-1){
                System.out.println(Arrays.toString(el.toArray(new String[el.size()])));
                continue;
            }
            confidence = extractFromFrequent(frequent, arr)/
                    extractFromFrequent(frequent, el.toArray(new String[el.size()]));
            //confidence = frequent.get(arr)/frequent.get(el.toArray(new String[el.size()]));
            //System.out.println(confidence+"");
            if(confidence<con){
                continue;
            }
            if(ass.get(el.toArray(new String[el.size()]))!=null){
                ass.get(el.toArray(new String[el.size()])).
                        put(ell.toArray(new String[ell.size()]), confidence);
            }
            else{
                HashMap<String[], Double> ass1 = new HashMap<String[], Double>();
                ass1.put(ell.toArray(new String[ell.size()]), confidence);
                ass.put(el.toArray(new String[el.size()]), ass1);
            }
        }
    }
    
    public static double extractFromFrequent(HashMap<String[], Integer> freq, String[] arr){
        Set<String[]> keys = freq.keySet();
        Iterator<String[]> it = keys.iterator();
        while(it.hasNext()){
            String[] arr2 = it.next();
            if(AssMethods.findEqual(arr2, arr)){
                return freq.get(arr2);
            }
        }
        return -1;
    }
    
}
