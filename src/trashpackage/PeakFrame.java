/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trashpackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import peakpackage.DayPeak;
import peakpackage.HolidayPeak;
import peakpackage.WeekPeak;

/**
 *
 * @author Asus
 */
public class PeakFrame extends JFrame{
    
    private GraphPanel gp;
    private JPanel jp;
    private JButton jb;
    
    public PeakFrame(){
        //synchronized(this){
        /*DayPeak.initDP();
        WeekPeak.initWP();
        HolidayPeak.initHP();*/
        
        gp = new GraphPanel(DayPeak.daypeaks, WeekPeak.weekpeaks, 
            HolidayPeak.holtrans, HolidayPeak.tottrans, 0, 10);
        
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER, gp);
        
        jb = new JButton("Get Legend");
        jb.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                initJPanel();
                }
        
        });
        this.add(BorderLayout.SOUTH, jb);
    }
    
    public void initJPanel(){
        ArrayList<String> gpnames = gp.getNames();
        ArrayList<Color> gpcolors = gp.getColors();
        
        System.out.println("\n\nGP Names is: "+gpnames.size());
        
        int laylength = (int)(gpnames.size()*1.0/5+1), laywidth = 5;
        jp = new JPanel();
        jp.setLayout(new GridLayout(laylength, laywidth));
        
        for(int i=0; i<gpnames.size(); i++){
            JLabel l1 = new JLabel(gpnames.get(i));
            JLabel l2 = new JLabel();
            l2.setBackground(gpcolors.get(i));
            JPanel p1 = new JPanel();
            p1.setLayout(new GridLayout(2, 1));
            p1.add(l1);
            p1.add(l2);
            System.out.println(gpnames.get(i)+": "+gpcolors.get(i));
            jp.add(p1);
        }
        this.add(BorderLayout.WEST, jp);
    }
    
    
    
}
