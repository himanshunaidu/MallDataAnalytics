/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peakpackage;

import mainpackage.Transactions;
import java.util.*;

/**
 *
 * @author Asus
 */
public class HolidayPeak {
    
	//DELIVERABLES (POSSIBLE)
    public static HashMap<Integer, String[]> holidays = new HashMap<Integer, String[]>();
    
    public static HashMap<Calendar, ArrayList<String[]>> tottrans;
    public static HashMap<Calendar, ArrayList<String[]>> holtrans;         
    public static HashMap<Calendar, ArrayList<String[]>> nonholtrans;
    
    //Splits holiday times to return map of holiday index to date array
    public static HashMap<Integer, Calendar> getHolidays(Calendar[] daytime){
        HashMap<Integer, Calendar> holidays1 = new HashMap<Integer, Calendar>();
        for(int i=0; i<daytime.length; i++){
            Calendar timeval = daytime[i];
            holidays1.put(i, timeval);
        }
        return holidays1;
    }
    
    //get Set of all Days of Transaction (for compare with average of all days)
    //Structure: Date=>List of Transactions
    public static HashMap<Calendar, ArrayList<String[]>> getTotTransDays(Calendar[] dates){
        int num = 0;
        HashMap<Calendar, ArrayList<String[]>> dateset = new HashMap<Calendar, ArrayList<String[]>>();
        
        HashMap<Integer, Calendar> daytimes = DayPeak.getDayTimes(dates);
        Set<Integer> keys = daytimes.keySet();
        Iterator<Integer> it = keys.iterator();
        while(it.hasNext()){
            int key = it.next();
            Calendar days1 = PeakMethods.setDefCal(daytimes.get(key));
            /*String day1 = days1[Transactions.yeardex]+"-"+
                    days1[Transactions.mondex]+"-"+days1[Transactions.daydex];*/
            ArrayList<String[]> list = dateset.get(days1);
            if(list==null){
                list = new ArrayList<String[]>();
            }
            list.add(Transactions.trans[key]);
            dateset.put(days1, list);
        }
        
        
        return dateset;
    }
    
    //Compare with average (reference)
    public static void cmpAve(Calendar[] dates){
        HashMap<Calendar, ArrayList<String[]>> holtrans = getHolTransDays(dates);
        
        HashMap<Calendar, ArrayList<String[]>> tottrans = getTotTransDays(dates);
        
        double totave = PeakMethods.getAverage(tottrans);
        
        Set<Calendar> keys = holtrans.keySet();
        Iterator<Calendar> it = keys.iterator();
        while(it.hasNext()){
            Calendar date = it.next();
            
            System.out.println(PeakMethods.getDateString(date)+" (Holiday): "+
                holtrans.get(date).size()+"\tTotal Average: "+totave);
        }
    }
    
    //Get Set of Transactions in Holidays
    //Structure: Date=>List of Transactions
    public static HashMap<Calendar, ArrayList<String[]>> getHolTransDays(Calendar[] dates){
        HashMap<Calendar, ArrayList<String[]>> totdateset = getTotTransDays(dates);
        HashMap<Calendar, ArrayList<String[]>> dateset = 
                new HashMap<Calendar, ArrayList<String[]>>();
        
        Calendar[] cholidays = Transactions.cholidays;
        
        Set<Calendar> keys = totdateset.keySet();
        Iterator<Calendar> it = keys.iterator();
        
        while(it.hasNext()){
            Calendar key = it.next();
            
            for(int i=0; i<cholidays.length; i++){
                if(PeakMethods.cmpCalendar(key, cholidays[i])){
                    dateset.put(key, totdateset.get(key));
                }
            }
        }
        return dateset;
    }
    
    //Get Set of Transactions in Non-holidays
    //Structure: Date=>List of Transactions
    public static HashMap<Calendar, ArrayList<String[]>> getNonHolTransDays(Calendar[] dates){
        HashMap<Calendar, ArrayList<String[]>> totdateset = getTotTransDays(dates);
        HashMap<Calendar, ArrayList<String[]>> dateset = 
                new HashMap<Calendar, ArrayList<String[]>>();
        
        Calendar[] cholidays = Transactions.cholidays;
        boolean add = true;
        
        Set<Calendar> keys = totdateset.keySet();
        Iterator<Calendar> it = keys.iterator();
        
        while(it.hasNext()){
            add = true;
            Calendar key = it.next();
            
            for(int i=0; i<cholidays.length; i++){
                if(PeakMethods.cmpCalendar(key, cholidays[i])){
                    add = false;
                    break;
                }
            }
            if(add==true){
                dateset.put(key, totdateset.get(key));
            }
        }
        return dateset;
    }
    
    //Compare with non-holidays
    public static void cmpAveNonHol(Calendar[] dates){
        HashMap<Calendar, ArrayList<String[]>> holtrans = getHolTransDays(dates);
        
        HashMap<Calendar, ArrayList<String[]>> nholtrans = getNonHolTransDays(dates);
        
        double totave = PeakMethods.getAverage(nholtrans);
        
        Set<Calendar> keys = holtrans.keySet();
        Iterator<Calendar> it = keys.iterator();
        while(it.hasNext()){
            Calendar date = it.next();
            
            System.out.println(PeakMethods.getDateString(date)+" (Holiday): "+
                holtrans.get(date).size()+"\tAverage of Non-Holidays: "+totave);
        }
    }
    
    //Compare with weekdays
    public static void cmpWeekDays(Calendar[] dates){
        HashMap<Calendar, ArrayList<String[]>> holtrans = getHolTransDays(dates);
        
        HashMap<String, HashMap<Calendar, ArrayList<String[]>>> weekpeaks = 
                WeekPeak.getWeekPeaks(dates);
         
        Set<Calendar> keys = holtrans.keySet();
        Iterator<Calendar> it = keys.iterator();
        while(it.hasNext()){
            Calendar date = it.next();
            int wdindex = date.get(Calendar.DAY_OF_WEEK);
            //int wdindex = WeekPeak.getWeekDay(datearray);
            
            Set<String> keys1 = weekpeaks.keySet();
            Iterator<String> it1 = keys1.iterator();
            while(it1.hasNext()){
                String weekday1 = it1.next();
                if(WeekPeak.weekdays[wdindex].equals(weekday1)){
                    //Getting number of transactions in each weekday into size
                    double size = 0, size1 = 0;
                    HashMap<Calendar, ArrayList<String[]>> map = weekpeaks.get(weekday1);
                    Set<Calendar> keys2 = map.keySet();
                    size1 = keys2.size();
                    Iterator<Calendar> it2 = keys2.iterator();
                    while(it2.hasNext()){
                        Calendar key2 = it2.next();
                        ArrayList<String[]> list = map.get(key2);
                        size+=list.size();
                    }
                    
                    System.out.println(PeakMethods.getDateString(date)+" (Holiday): "+
                            holtrans.get(date).size()+"\tAverage on "+weekday1+
                            ": "+ size/size1);
                }
            }
        }
        
    }
    
    public static final int WD = 1;
    public static final int AVE = 2;
    public static final int NHAVE = 3;
    
    //public static void main(String[] args){
    public static void initHP(Calendar start, Calendar end){
        //System.out.print(getTotTransDays(Transactions.dates).toString());
        Transactions.initTransactions(start, end, null);
        tottrans = getTotTransDays(Transactions.ddates);
        holtrans = getHolTransDays(Transactions.ddates);
        nonholtrans = getNonHolTransDays(Transactions.ddates);
        
        cmpWeekDays(Transactions.ddates);
        cmpAve(Transactions.ddates);
        cmpAveNonHol(Transactions.ddates);
    }
    
}
