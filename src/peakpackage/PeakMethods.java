/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peakpackage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author Asus
 */
public class PeakMethods {
    
    //Get averge transactions of all days/ or holidays/ or non-holidays
    public static double getAverage(HashMap<Calendar, ArrayList<String[]>> ttd){
        Set<Calendar> keys = ttd.keySet();
        Iterator<Calendar> it = keys.iterator();
        
        double totsize = 0;
        while(it.hasNext()){
            Calendar key = it.next();
            totsize += ttd.get(key).size();
        }
        
        int daysize = ttd.size();
        return totsize/daysize;
    }
    
    //Check if dates match in both Calendar datatypes
    public static boolean cmpCalendar(Calendar c1, Calendar c2){
        int m1 = c1.get(Calendar.MONTH), d1 = c1.get(Calendar.DAY_OF_MONTH);
        int m2 = c2.get(Calendar.MONTH), d2 = c2.get(Calendar.DAY_OF_MONTH);
        
        if((m1==m2)&&(d1==d2)){
            return true;
        }
        
        return false;
    }
    
    //Print only Calendar Date
    public static String getDateString(Calendar c){
        String s = c.getTime().toString();
        return s;
    }
    
    //Set Default calendar time (For those calendars whose time we don't want to consider
    public static Calendar setDefCal(Calendar c){
        c.set(Calendar.HOUR_OF_DAY, 1);
        c.set(Calendar.MINUTE, 1);
        c.set(Calendar.SECOND, 1);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }
    
    public static Calendar copyCal(Calendar c){
        Calendar c1 = Calendar.getInstance();
        c1.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), 
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                c.get(Calendar.SECOND));
        c1.set(Calendar.MILLISECOND, 0);
        return c1;
    }
    
}
