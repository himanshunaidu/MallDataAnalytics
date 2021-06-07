/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peakpackage;

import static associationpackage.AssociationMiner.main;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author Asus
 */
public class PeakMain {
    
    /*public static void main(String[] args){
        DayPeak.initDP();
        WeekPeak.initWP();
        HolidayPeak.initHP();
    }*/
    
    public static void main(String[] args){
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.add(Calendar.DATE, -200);
        end.add(Calendar.DATE, 10);
        HashMap<String, ArrayList<String>> prod = new HashMap<>();
        ArrayList<String> a1 = new ArrayList<>();
        a1.add("Arrow");
        a1.add("BlackBerrys");
        prod.put("shirt", a1);
        //DayPeak.initDP(start, end);
        //WeekPeak.initWP(start, end);
        HolidayPeak.initHP(start, end);
    }
    
}
