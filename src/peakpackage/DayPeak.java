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
public class DayPeak {
    
    public static HashMap<Integer, Calendar> daytimes = new HashMap<Integer, Calendar>();
    public static int[] intimeint, fintimeint, transtimeint;
    
    //DELIVERABLE
    public static HashMap<Integer, HashMap<Calendar, ArrayList<String[]>>> daypeaks = 
            new HashMap<Integer, HashMap<Calendar, ArrayList<String[]>>>();
    
    //get HashMap of daytimes from Transactions.dates
    public static HashMap<Integer, Calendar> getDayTimes(Calendar[] daytime){
        HashMap<Integer, Calendar> times1 = new HashMap<Integer, Calendar>();
        for(int i=0; i<daytime.length; i++){
            Calendar timeval = daytime[i];
            times1.put(i, timeval);
        }
        return times1;
    }
    
    //get Array of timeints from HashMap of daytimes
    public static int[] getTfromDT(HashMap<Integer, Calendar> dt){
        Set<Integer> keys = dt.keySet();
        Iterator<Integer> it = keys.iterator();
        Calendar[] times = new Calendar[keys.size()];
        int i=0;
        while(it.hasNext()){
            int key = it.next();
            Calendar times1 = dt.get(key);
            times[i] = times1;
            i++;
        }
        return getTimes(times, Transactions.finhour, Transactions.finmin,
                Transactions.finsec);
    }
    
    //get Array of timeints from Array of String times
    public static int[] getTimes(Calendar[] times, int h, int m, int s){
        int length = times.length;
        int[] timeints = new int[length];
        for (int i=0; i<length; i++) {
            Calendar timeval = times[i];
            int timeint = 0;
            
            timeint+=timeval.get(Calendar.HOUR_OF_DAY)*3600;
            timeint+=timeval.get(Calendar.MINUTE)*60;
            timeint+=timeval.get(Calendar.SECOND);
            timeints[i] = timeint;
        }
        return timeints;
    }
    
    //get Day Peaks
    //Structure: Range Index=>List of Transactions
    public static HashMap<Integer, HashMap<Calendar, ArrayList<String[]>>> getDayPeaks(int[] intimes, 
            int[] outimes, int[] timings, HashMap<Integer, Calendar> daytimes){
        HashMap<Integer, HashMap<Calendar, ArrayList<String[]>>> daypeaks1 = 
                new HashMap<Integer, HashMap<Calendar, ArrayList<String[]>>>();
        
        int timelength = timings.length;
        int inlength = intimes.length;
        for(int i=0; i<timelength; i++){
            int time = timings[i];
            Calendar daytime = PeakMethods.setDefCal(daytimes.get(i));
            
            for(int j=0; j<inlength; j++){
                if(checkRange(time, intimes[j], outimes[j])==true){
                    System.out.println("Successful Catch");
                    
                    HashMap<Calendar, ArrayList<String[]>> map = daypeaks1.get(j);
                    if(map==null){
                        map = new HashMap<Calendar, ArrayList<String[]>>();
                    }
                    
                    ArrayList<String[]> list = map.get(daytime);
                    if(list==null){
                        list = new ArrayList<String[]>();
                    }
                    
                    list.add(Transactions.trans[i]);
                    
                    map.put(daytime, list);
                    daypeaks1.put(j, map);
                    
                    break;
                }
            }
        }
                
        return daypeaks1;
    }
    
    public static boolean checkRange(int time, int in, int fin){
        if(in>fin){
            int temp = in;
            in = fin;
            fin = temp;
        }
        if((time>=in)&&(time<fin)){
            return true;
        }
        return false;
    }
    
    public static void printDayPeaks(HashMap<Integer, HashMap<Calendar, ArrayList<String[]>>> dp){
        Set<Integer> keys = dp.keySet();
        Iterator<Integer> it = keys.iterator();
        while(it.hasNext()){
            int arr = it.next();
            System.out.print("Range "+arr+"=>");
            HashMap<Calendar, ArrayList<String[]>> map = dp.get(arr);
            
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
    
    //CALL THIS FUNCTION. 
    public static void initDP(Calendar start, Calendar end){
    //public static void main(String[] args){
        Transactions.initTransactions(start, end, null);
        if(Transactions.intimes.length!=Transactions.fintimes.length){
            System.out.println("Illegal Time Ranges");
            System.exit(1);
        }
        intimeint = getTimes(Transactions.cintimes, Transactions.finhour
                , Transactions.finmin, Transactions.finsec);
        fintimeint = getTimes(Transactions.cfintimes, Transactions.finhour
                , Transactions.finmin, Transactions.finsec);
        
        daytimes = getDayTimes(Transactions.ddates);
        transtimeint = getTfromDT(daytimes);
        
        for(int i=0; i<intimeint.length; i++){
            System.out.print(intimeint[i]+"->"+fintimeint[i]+"\n");
        }
        for(int i=0; i<transtimeint.length; i++){
            System.out.print(transtimeint[i]+"\n");
        }
        
        daypeaks = getDayPeaks(intimeint, fintimeint, transtimeint, daytimes);
        System.out.println("Printing Day Peaks");
        printDayPeaks(daypeaks);
    }
    
}
