/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package associationpackage;
import java.util.*;
/**
 *
 * @author Asus
 */
public class QuantAssociationMiner {
    
    public static HashMap<HashMap<String, Integer>, HashMap<HashMap<String, Integer>, Double>> 
            quantassociation = 
            new HashMap<HashMap<String, Integer>, HashMap<HashMap<String, Integer>, Double>>();
    
    //Take parameters associations, transquant, trans to get quantassociations
    public static HashMap<HashMap<String, Integer>, HashMap<HashMap<String, Integer>, Double>> 
            getAllQuantAssociations(HashMap<String[], HashMap<String[], Double>> association,
                    HashMap<Integer, HashMap<String, Integer>> tq, String[][] t){
                
                HashMap<HashMap<String, Integer>, HashMap<HashMap<String, Integer>, Double>> 
                        quantassociation1= 
                new HashMap<HashMap<String, Integer>, HashMap<HashMap<String, Integer>, Double>>();
                
                //New parameters from already given parameters
                int tlength = t.length;
                
                //temporary variables
                Set<String> set = new HashSet<String>();
                HashMap<String[], Double> subass = new HashMap<String[], Double>();
                HashMap<HashMap<String, Integer>, Double> qavalue1 = 
                                    new HashMap<HashMap<String, Integer>, Double>();
                
                //Keeps track of each item in both sets of each association
                HashMap<String, Integer> quantasscount = new HashMap<String, Integer>();
                HashMap<String, Integer> set1count = new HashMap<String, Integer>();
                HashMap<String, Integer> set2count = new HashMap<String, Integer>();
                
                //For Key value pair of quantassociation
                HashMap<HashMap<String, Integer>, Double> qavalue = 
                                    new HashMap<HashMap<String, Integer>, Double>();
                
                //keys to iterate the association
                Set<String[]> keys = association.keySet();
                Iterator<String[]> it = keys.iterator();
                
                //Check Each Key of Association (Set 1 of Associations)
                while(it.hasNext()){
                    //key is set1
                    String[] key = it.next();
                    int length = key.length;
                    
                    subass = association.get(key);
                    Set<String[]> keys1 = subass.keySet();
                    Iterator<String[]> it1 = keys1.iterator();
                    
                    //Check Each Association
                    while(it1.hasNext()){ 
                        //key1 is set2
                        String[] key1 = it1.next();
                        int length1 = key1.length;
                        
                        double ass = subass.get(key1);
                        
                        //Add all the Association items to our temp set
                        for(int i=0; i<length; i++){
                            set.add(key[i]);
                        }
                        for(int i=0; i<length1; i++){
                            set.add(key1[i]);
                        }
                        
                        //Check the Association itemset in each transaction to:
                        //Add the count of each item in the itemset
                        for(int i=0; i<tlength; i++){
                            String[] setstring = set.toArray(new String[0]);
                            int sslength = setstring.length;
                            //Check prescence of itemset in each transaction
                            if(AssMethods.findPresent(setstring, t[i])){
                                //Get Map of item to count for the transaction
                                HashMap<String, Integer> subtq = tq.get(i);
                                
                                Set<String> subtqkeys = subtq.keySet();
                                Iterator<String> subtqit = subtqkeys.iterator();
                                while(subtqit.hasNext()){
                                    String subtqkey = subtqit.next();
                                    
                                    if(quantasscount.get(subtqkey)==null){
                                        quantasscount.put(subtqkey, subtq.get(subtqkey));
                                    }
                                    else{
                                        quantasscount.put(subtqkey, 
                                                quantasscount.get(subtqkey)+subtq.get(subtqkey));
                                    }
                                }
                            }
                        }
                        
                        for(int i=0; i<length; i++){
                            set1count.put(key[i], quantasscount.get(key[i]));
                        }
                        for(int i=0; i<length1; i++){
                            set2count.put(key1[i], quantasscount.get(key1[i]));
                        }
                        
                        if(quantassociation1.get(set1count)==null){
                            qavalue.put(set2count, ass);
                            quantassociation1.put(set1count, qavalue);
                        }
                        else{
                            qavalue1 = quantassociation1.get(set1count);
                            qavalue1.put(set2count, ass);
                            quantassociation1.put(set1count, qavalue1);
                        }
                        set = new HashSet<String>();
                        qavalue = new HashMap<HashMap<String, Integer>, Double>();
                        set1count = new HashMap<String, Integer>();
                        set2count = new HashMap<String, Integer>();
                        quantasscount = new HashMap<String, Integer>();
                    }
                }                
                
                return quantassociation1;             
                
    }
            
    
}
