/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trashpackage;

import java.awt.Color;
import java.awt.Graphics;
import java.util.*;
import javax.swing.JPanel;

/**
 *
 * @author Asus
 */
public class GraphPanel extends JPanel{
    
    private HashMap<Integer, HashMap<Calendar, ArrayList<String[]>>> daypeaks1;
    private HashMap<String, HashMap<Calendar, ArrayList<String[]>>> weekpeaks1; 
    private HashMap<Calendar, ArrayList<String[]>> hol1;
    private HashMap<Calendar, ArrayList<String[]>> cmp1;
    private int type = 0;
    
    private ArrayList<String> graphnames = new ArrayList<String>();
    private ArrayList<Color> graphcolors = new ArrayList<Color>();
    
    private int maxTrans = 1000;
    
    public GraphPanel(HashMap<Integer, HashMap<Calendar, ArrayList<String[]>>> daypeaks1, 
            HashMap<String, HashMap<Calendar, ArrayList<String[]>>> weekpeaks1, 
            HashMap<Calendar, ArrayList<String[]>> hol1, 
            HashMap<Calendar, ArrayList<String[]>> cmp1,
            Integer type, Integer maxTrans){
        
        this.daypeaks1 = daypeaks1;
        this.weekpeaks1 = weekpeaks1;
        this.hol1 = hol1;
        this.cmp1 = cmp1;
        
        if(type!=null){
            this.type = type;}
        if(maxTrans!=null){
            this.maxTrans = maxTrans;
        }
    }
    
    public ArrayList<String> getNames(){
        return this.graphnames;
    }
    
    public ArrayList<Color> getColors(){
        return this.graphcolors;
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        //g.drawOval(10, 10, 10, 10);
        switch(type){
            case 0: paint0(g, daypeaks1);
                break;
            case 1: paint0(g, weekpeaks1);
                break;
            
            default:
        }
    }
    
    //Day Peaks
    public <T> void paint0(Graphics g, 
            HashMap<T, HashMap<Calendar, ArrayList<String[]>>> peaks){ 
        
        HashMap<T, ArrayList<Double>> dwpeaks = getDWPeaks(peaks);
        
        int width = this.getWidth();
        int height = this.getHeight();
        
        int x1=0, x2=0, y1=0, y2=0;
        int index = 0;
        
        Set<T> dwset = dwpeaks.keySet();
        Iterator<T> dwit = dwset.iterator();
        while(dwit.hasNext()){
            T key = dwit.next();
            ArrayList<Double> peak1 = dwpeaks.get(key);
            double thick = ((width-10)/peak1.size());
            
            Iterator<Double> it1 = peak1.iterator();
            g.setColor(graphcolors.get(index));
            if(it1.hasNext()){
                y1 = height-(int) (it1.next()*1);
                x1 = 10;
                g.fillOval(x1, y1, (int)3, (int)3);
                System.out.println("Point 1 drawn: "+x1+","+y1);
            }
            while(it1.hasNext()){
                y2 = height-(int) (it1.next()*1);
                x2 = x1+(int)thick;
                
                g.fillOval(x2, y2, (int)3, (int)3);
                System.out.println("Point 2 drawn: "+x2+","+y2);
                g.drawLine(x1, y1, x2, y2);
                System.out.println("Line drawn");
                x1 = x2;
                y1 = y2;
            }
            index++;
        }
    }
    
    public <T> HashMap<T, ArrayList<Double>> getDWPeaks(
            HashMap<T, HashMap<Calendar, ArrayList<String[]>>> peaks){
        HashMap<T, ArrayList<Double>> dwpeaks = new HashMap<T, ArrayList<Double>>();
        
        int height = this.getHeight();
        
        Random rand = new Random();
        Set<T> keys = peaks.keySet();
        Iterator<T> it = keys.iterator();
        
        //ArrayList<Calendar> dates = getDates(peaks);
        //System.out.println("Starting iteration");
        while(it.hasNext()){
            T key = it.next();
            String name = ""+key;
            Color color = new Color(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
            
            graphnames.add(name);
            graphcolors.add(color);
            System.out.println("Added "+name+", "+color);
            
            HashMap<Calendar, ArrayList<String[]>> map = peaks.get(key);
            
            ArrayList<Double> newlist = new ArrayList<Double>();
            
            Set<Calendar> keys1 = map.keySet();
            List<Calendar> keylist = new ArrayList(keys1);
            Collections.sort(keylist);
            Iterator<Calendar> keyit = keylist.iterator();
            
            while(keyit.hasNext()){
                Calendar keycal = keyit.next();
                ArrayList<String[]> list = map.get(keycal);
                //System.out.println("\t"+keycal.getTime().toString()+": "+list.size());
                
                double size = 0;
                if(list!=null){
                    size = list.size();
                    if(size>maxTrans){
                        size=maxTrans;
                    }
                }
                newlist.add(size/maxTrans*height);
            }
            dwpeaks.put(key, newlist);
        }
        return dwpeaks;
    }
    
    public static <T> ArrayList<Calendar> getDates(
            HashMap<T, HashMap<Calendar, ArrayList<String[]>>> peaks){
                ArrayList<Calendar> dates = new ArrayList<Calendar>();
                
                Set<T> keys = peaks.keySet();
                Iterator<T> it = keys.iterator();
        
                while(it.hasNext()){
                    T key = it.next();
                    HashMap<Calendar, ArrayList<String[]>> map = peaks.get(key);
                    
                    Set<Calendar> calset = map.keySet();
                    dates.addAll(calset);
                }
                
                Collections.sort(dates);
                
                /*Iterator<Calendar> itd = dates.iterator();
                while(itd.hasNext()){
                    System.out.println(itd.next().getTime().toString());
                }*/
                
                return dates;
    }
    
}
