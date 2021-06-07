/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cpassociationpackage;

import simcustpackage.Cluster;
import simcustpackage.Customer;

/**
 *
 * @author Asus
 */
public class CPAssociationMiner {
    
    public static double[][] cpass;
    public static int[] counts; 
    
    public static void main(String[] args){
        Cluster.initialize();
        cpass = getCPAssociations(Cluster.centroids, Cluster.labels, Cluster.counts,
                Cluster.prod, Cluster.cust, Cluster.custmatrix);
        int cplength = cpass.length, cplength1 = cpass[0].length;
        System.out.println("\n\n");
        for(int i=0; i<cplength; i++){
            for(int j=0; j<cplength1; j++){
                System.out.printf("%.2f ", cpass[i][j]);
            }
            System.out.printf("\t(%d)\n", Cluster.counts[i]);
        }
        counts = Cluster.counts;
    }
    
    public static double[][] getCPAssociations(double[][] centroids, int[] labels, int[] counts,
            String[] prod, Customer[] cust, int[][] custmatrix){
        if(centroids==null){
            return null;
        }
        double[][] cpass1 = new double[centroids.length][centroids[0].length];
        
        //Initialize cpass1
        int cplength = cpass1.length, cplength1 = cpass1[0].length;
        for(int i=0; i<cplength; i++){
            for(int j=0; j<cplength1; j++){
                cpass1[i][j] = 0;
            }
        }
        
        int lablength = labels.length, index = 0, cenlength = centroids.length,
                censlength = centroids[0].length;
        for(int i=0; i<lablength; i++){
            index = labels[i];
            for(int j=0; j<censlength; j++){
                cpass1[index][j]+=custmatrix[i][j];
            }
        }
        
        for(int i=0; i<cenlength; i++){
            for(int j=0; j<censlength; j++){
                cpass1[i][j] = cpass1[i][j]/counts[i];
            }
        }
        
        
        return cpass1;
    }
    
}
