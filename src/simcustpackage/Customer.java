/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simcustpackage;

/**
 *
 * @author Asus
 */
public class Customer {
    
    private String name = "xyz";
    private String contact = "0123456789";
    
    public Customer(String name, String contact){
        this.name = name;
        this.contact = contact;
    }
    
    public String getName(){
        return name;
    }
    
    public String getContact(){
        return contact;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setContact(String contact){
        this.contact = contact;
    }
    
    public boolean equals(Customer c){
        
        Customer c1 = (Customer)c;
        if(name.equals(c1.getName())&&contact.equals(c1.getContact())){
            return true;
        }
        return false;
    }
    
    @Override
    public String toString(){
        return name+"-"+contact;
    }
    
}
