/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inventorypackage;

import java.util.Comparator;

/**
 *
 * @author Asus
 */
public class SortRegression implements Comparator<Regression>{

    @Override
    public int compare(Regression o1, Regression o2) {
        return o1.gt(o2);
    }
    
}
