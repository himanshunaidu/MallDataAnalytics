/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trashpackage;

import java.awt.Dimension;
import javax.swing.JFrame;

/**
 *
 * @author Asus
 */
public class MainFrame {
    
    public static void main(String[] args){
        PeakFrame pf = new PeakFrame();
        pf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pf.setVisible(true);
        pf.setSize(new Dimension(250, 250));
    }
    
}
