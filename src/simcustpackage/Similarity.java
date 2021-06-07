/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simcustpackage;

/**
 *
 * @author Asus
 */
public class Similarity {
    
    public static double getDistance1(int[] set1, int[] set2){
        int length1 = set1.length, length2 = set2.length, mainlength = 0;
        if(length1>length2){
            mainlength = length2;
        }
        else{
            mainlength = length1;
        }
        
        double dist = 0;
        for(int i=0; i<mainlength; i++){
            dist+=Math.pow(set1[i]-set2[i], 2);
        }
        return dist;
    }
    
}
