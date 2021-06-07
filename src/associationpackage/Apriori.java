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
public class Apriori {
    
    public static int sup = AssociationMiner.sup;
    public static double con = AssociationMiner.con;
    
    public static ArrayList<String[]> itemsets= new ArrayList<String[]>();
    public static HashMap<String[], Integer> frequent = new HashMap<String[], Integer>();
    public static ArrayList<String[]> transactions = new ArrayList<String[]>();
    //public static ArrayList<String[]> frequent = new ArrayList<String[]>();
    public static int mainLength = 0;
    
    /*public static void main(String[] args){
        getSet();
        printFrequent(frequent);
    }*/
    
    public static ArrayList<String[]> initItems1(String[] prod){
        ArrayList<String[]> items = new ArrayList<String[]>();
        for(int i=0; i<prod.length; i++){
            items.add(new String[]{prod[i]});
        }
        return items;
    }
    
    public static ArrayList<String[]> initTrans1(String[][] trans){
        ArrayList<String[]> tra = new ArrayList<String[]>();
        for(int i=0; i<trans.length; i++){
            tra.add(trans[i]);
        }
        return tra;
    }
    
    public static HashMap<String[], Integer> getSet(){
        itemsets = initItems1(Transactions.products);
        transactions = initTrans1(Transactions.trans);
        
        mainLength = Transactions.products.length;
        getFrequent(itemsets);
        AssMethods.printItemsets(itemsets);
        for(int i=0; i<mainLength; i++){
            createNewItemsetsFromPreviousOnes();
            getFrequent(itemsets);
            AssMethods.printItemsets(itemsets);
        }
        
        return frequent;
    }
    
    private static void createNewItemsetsFromPreviousOnes()
    {
    	// by construction, all existing itemsets have the same size
        if(itemsets.size()==0){
            return;
        }
    	int currentSizeOfItemsets = itemsets.get(0).length;
    	System.out.println("Creating itemsets of size "+(currentSizeOfItemsets+1)+" based on "+itemsets.size()+" itemsets of size "+currentSizeOfItemsets);
    		
    	HashMap<String, String[]> tempCandidates = new HashMap<String, String[]>(); //temporary candidates
    	
        // compare each pair of itemsets of size n-1
        for(int i=0; i<itemsets.size(); i++)
        {
            for(int j=i+1; j<itemsets.size(); j++)
            {
                String[] X = itemsets.get(i);
                String[] Y = itemsets.get(j);

                assert (X.length==Y.length);
                
                //make a string of the first n-2 tokens of the strings
                String[] newCand = new String[currentSizeOfItemsets+1];
                for(int s=0; s<newCand.length-1; s++) {
                	newCand[s] = X[s];
                }
                    
                int ndifferent = 0;
                // then we find the missing value
                for(int s1=0; s1<Y.length; s1++)
                {
                	boolean found = false;
                	// is Y[s1] in X?
                    for(int s2=0; s2<X.length; s2++) {
                    	if (X[s2].equals(Y[s1])) { 
                    		found = true;
                    		break;
                    	}
                	}
                	if (!found){ // Y[s1] is not in X
                		ndifferent++;
                		// we put the missing value at the end of newCand
                		newCand[newCand.length -1] = Y[s1];
                	}
            	
            	}
                
                // we have to find at least 1 different, otherwise it means that we have two times the same set in the existing candidates
                assert(ndifferent>0);
                
                
                if (ndifferent==1) {
                    // HashMap does not have the correct "equals" for int[] :-(
                    // I have to create the hash myself using a String :-(
                	// I use Arrays.toString to reuse equals and hashcode of String
                	Arrays.sort(newCand);
                	tempCandidates.put(Arrays.toString(newCand),newCand);
                }
            }
        }
        
        //set the new itemsets
        itemsets = new ArrayList<String[]>(tempCandidates.values());
    	System.out.println("Created "+itemsets.size()+" unique itemsets of size "+(currentSizeOfItemsets+1));

    }
    
    public static void getFrequent(ArrayList<String[]> items){
        String[] arr1, arr2;
        int count = 0;
        Iterator<String[]> it = items.iterator();
        ArrayList<String[]> disItems = new ArrayList<String[]>();
        while(it.hasNext()){
            count = 0;
            arr1 = it.next();
            Iterator<String[]> it1 = transactions.iterator();
            while(it1.hasNext()){
                arr2 = it1.next();
                if(AssMethods.findPresent(arr1, arr2)){
                    count++;
                }
            }
            if(count>=sup){
                frequent.put(arr1, count);
            }
            else{
                disItems.add(arr1);
            }
        }
        Iterator<String[]> it2 = disItems.iterator();
        while(it2.hasNext()){
            itemsets.remove(it2.next());
        }
    }
    
    
}
