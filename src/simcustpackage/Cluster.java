/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simcustpackage;

import java.util.*;

/**
 *
 * @author Asus
 */
public class Cluster {
    
	//DELIVERABLE
    public static KMeans kmeans;
    
    public static String[] prod;
    public static Customer[] cust;
    public static int[][] custmatrix;
    public static double[][] centroids;
    public static int num;
    public static int[] labels;
    public static int[] counts;
    
    public static KMeans generateClusters(int[][] custmatrix){
        HashMap<Integer, HashMap<Integer, int[]>> clusters = 
                new HashMap<Integer, HashMap<Integer, int[]>>();
        
        int length = custmatrix.length;
        num = (int) (Math.log(length)/Math.log(2));
        
        KMeans k = new KMeans(custmatrix);
        k.clustering(num, num*10, null);
        centroids = k.getCentroids();
        for(int i=0; i<centroids.length; i++){
            for(int j=0; j<centroids[i].length; j++){
                System.out.printf("%.2f ", centroids[i][j]);
            }
            System.out.println();
        }
        labels = k.getLabel();
        counts = k.getCounts();
        for(int j=0; j<labels.length; j++){
                System.out.println(labels[j]);
        }
        /*for(int j=0; j<counts.length; j++){
                System.out.println(counts[j]);
        }*/
        
        k.printResults();
        
        return k;
    }
    
    public static void initialize(){
    	Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.add(Calendar.DATE, -20);
        end.add(Calendar.DATE, -10);
        HashMap<String, ArrayList<String>> prod1 = new HashMap<>();
        ArrayList<String> a1 = new ArrayList<>(),
        		a2 = new ArrayList<>();
        a1.add("Arrow");
        a1.add("BlackBerrys");
        a1.add("fulls");
        a2.add("ccc");
        prod1.put("ss", a2);
        prod1.put("shirt", a1);
        
        main(start, end, prod1);
    }
    
    //CALL THIS FUNCTION.
    public static void main(Calendar start, Calendar end, 
    		HashMap<String, ArrayList<String>> prod1){
    	DatabaseRetriever2.connectDB(start, end, prod1);
        prod = DatabaseRetriever2.retrieveProd();
        cust = DatabaseRetriever2.retrieveCustList();
        custmatrix = DatabaseRetriever2.retrieveCustMatrix(prod, cust);
        SimCustMethods.printProd(prod);
        
        for(int i=0; i<cust.length; i++){
            System.out.print("\n"+cust[i]);
        }
        System.out.print("\n\n");
        SimCustMethods.printCustMatrix(custmatrix);
        DatabaseRetriever2.closeDB();
        
        kmeans = generateClusters(custmatrix);
    }
    
    public static void main(String[] args){
        initialize();
    }
    
}
