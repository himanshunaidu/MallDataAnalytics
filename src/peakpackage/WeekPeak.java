/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peakpackage;

import associationpackage.AssMethods;
import mainpackage.Transactions;
import java.util.*;

/**
 *
 * @author Asus
 */
public class WeekPeak {

    public static final String[] weekdays = {"", "Sunday", "Monday", "Tuesday",
        "Wednesday", "Thursday", "Friday", "Saturday"
    };
    
    //DELIVERABLE
    public static HashMap<String, HashMap<Calendar, ArrayList<String[]>>> weekpeaks = 
            new HashMap<String, HashMap<Calendar, ArrayList<String[]>>>();
    
    public static Calendar mtime;
    
    public static void initWP(Calendar start, Calendar end){
    //public static void main(String[] args){
        Transactions.initTransactions(start, end, null);
        
        weekpeaks = getWeekPeaks(Transactions.ddates);
        printWeekPeaks(weekpeaks);
    }
    
    public static int getWeekDay(String[] time){
        Calendar c = Calendar.getInstance();
        
        //Java Calender Format is different
        c.setTime(new Date(time[Transactions.mondex]
                +"/"+time[Transactions.daydex]+"/"+time[Transactions.yeardex]));
        int day = c.get(Calendar.DAY_OF_WEEK);
        /*System.out.println(time[Transactions.daydex]
                +"/"+time[Transactions.mondex]+"/"+time[Transactions.yeardex]
                +": "+weekdays[day]);*/
        
        return day;
    }
    
    //CALL THIS FUNCTION
    //Get Week Peaks
    //Structure: Week Day=>List of Transactions
    public static HashMap<String, HashMap<Calendar, ArrayList<String[]>>> getWeekPeaks(Calendar[] dates){
        //Map of Transaction number to time of transaction
        HashMap<Integer, Calendar> transdates = DayPeak.getDayTimes(dates);
        
        HashMap<String, HashMap<Calendar, ArrayList<String[]>>> weekpeaks1 = 
                new HashMap<>();
        
        Set<Integer> keys = transdates.keySet();
        Iterator<Integer> it = keys.iterator();
        
        while(it.hasNext()){
            int key = it.next();
            //Convert time to default day's time since time is not needed
            Calendar time = PeakMethods.setDefCal(transdates.get(key));
            int dayindex = time.get(Calendar.DAY_OF_WEEK);
            
            HashMap<Calendar, ArrayList<String[]>> map = weekpeaks1.get(weekdays[dayindex]);
            if(map==null){
                map = new HashMap<>();
                System.out.println("map is null");
            }
            
            ArrayList<String[]> list = map.get(time);
            if(list==null){
                list = new ArrayList<>();
                System.out.println("list is null");
            }
            list.add(Transactions.trans[key]);
            map.put(time, list);
            weekpeaks1.put(weekdays[dayindex], map);
            
        }
        return weekpeaks1;
    }
    
    public static void printWeekPeaks(HashMap<String, HashMap<Calendar, ArrayList<String[]>>> wp){
        Set<String> keys = wp.keySet();
        Iterator<String> it = keys.iterator();
        while(it.hasNext()){
            String arr = it.next();
            System.out.print(arr+"=>");
            HashMap<Calendar, ArrayList<String[]>> map = wp.get(arr);
            
            Set<Calendar> keys1 = map.keySet();
            Iterator<Calendar> it1 = keys1.iterator();
            while(it1.hasNext()){
                Calendar key = it1.next();
                System.out.print("\n\t"+key.getTime().toString()+": ");
                
                ArrayList<String[]> list = map.get(key);
                Iterator<String[]> it2 = list.iterator();
                
                while(it2.hasNext()){
                    AssMethods.printArray(it2.next());
                }
            }
            System.out.print("\n");
        }
    }
    
}
