/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inventorypackage;

import java.util.*;

/**
 *
 * @author Asus
 */
public class Regression {
    
    //y = ax+b
    //Slope
    private double a=0;
    //y-intercept
    private double b=0;
    
    //Never to be used outside as it may not be existent
    private ArrayList<Integer> pop = new ArrayList<>();
    private int n=0;
    private int sxy=0, sx=0, sy=0, sx2=0;
    //y=mx+c
    
    public Regression(){}
    
    public Regression(ArrayList<Integer> pop1){
        setVariables(pop1);
    }
    
    public Regression(ArrayList<Integer> n1, ArrayList<Regression> r1){
        int rlength = r1.size(), nlength = n1.size(), count=0;
        if(rlength==nlength){
            for(int i=0; i<rlength; i++){
                a += r1.get(i).getA();
                b += r1.get(i).getB();
                count+=n1.get(i);
            }
            a/=count;
            b/=count;
        }
        
    }
    
    private void setVariables(ArrayList<Integer> pop1){
        n = pop1.size();
        if(n==0){
            a = 0;
            b = 0;
            return;
        }
        
        int[] x = new int[n], y = new int[n];
        for(int i=0; i<n; i++){
            x[i] = i;
            y[i] = pop1.get(i);
        }
        
        for(int i=0; i<n; i++){
            sx+=x[i];//3
            sy+=y[i];//2
            sxy+=(x[i]*y[i]);//3
            sx2+=(x[i]*x[i]);//5
        }
        
        if(((n*sx2)-(sx*sx))==0){
            a = 0;}
        else{
            a = ((double)(n*sxy)-sx*sy)/((n*sx2)-(sx*sx));}
        //String s1 = String.format("%d, %d, %d, %d (%d)", sx, sy, sxy, sx2, n);
        b = (sy - a*sx)/n;
    }
    
    public int gt(Regression r1){
        double a1 = this.getA();
        double a2 = r1.getA();
        double b1 = this.getB();
        double b2 = r1.getB();
        if(a1>a2){
            return 1;
        }
        else if(a1==a2){
            return 0;
        }
        else{
            return -1;
        }
    }
    
    public double getA(){
        return a;
    }
    
    public double getB(){
        return b;
    }
    
    @Override
    public String toString(){
        String s = a+"*x+("+b+")";
        String s1 = String.format("%d, %d, %d, %d (%d)", sx, sy, sxy, sx2, n);
        return s;
    }
    
}
