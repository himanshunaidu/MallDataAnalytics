/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simcustpackage;

import associationpackage.AssMethods;

/**
 *
 * @author Asus
 */
public class SimCustMethods {
    
    public static void printProd(String[] c){
        AssMethods.printArray(c);
    }
    
    public static void printCustMatrix(int[][] cm){
        int lengthi = cm.length, lengthj = cm[0].length;
        for(int i=0; i<lengthi; i++){
            for(int j=0; j<lengthj; j++){
                System.out.print(cm[i][j]+" ");
            }
            System.out.print("\n");
        }
    }
    
}
