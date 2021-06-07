/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainpackage;

import trashpackage.DatabaseRetriever5;
import trashpackage.DatabaseRetriever2;
import trashpackage.DatabaseRetriever3;
import trashpackage.DatabaseRetriever4;
import associationpackage.AssMethods;
import custloyaltyapackage.CustLoyAMethods;
import java.sql.*;
import java.util.*;
import java.text.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import peakpackage.PeakMethods;
import simcustpackage.Customer;

/**
 *
 * @author Asus
 */
public class MainRetriever {

    //Main objects for mysql connection
    public static final String DATABASE_URL = "jdbc:mysql://localhost:3306/cloth?useSSL=false";
    public static boolean connected = false;
    public static Connection connection = null;
    public static Statement statement = null;
    public static PreparedStatement pstatement = null;
    public static PreparedStatement rstatement = null;
    public static ResultSet resultset = null;
    public static ResultSetMetaData metadata = null;

    //Main tables for transaction details
    public static final String[] tables = {"creditcustomerbill", "otherbill"};

    //QUERIES FOR ASSOCIATION RULES AND PEAKS
    //Queries for transaction from creditcustomerbill and otherbill
    public static String translengthquery = "Select max(BillNo) from ";
    public static String transquery = "select BillNo, ItemName, CategoryName from ";

    //Extra queries for limits
    public static String datelimitquery = "";
    public static String prodlimitquery = "";

    //Query for transquant
    public static String transquantquery = "select BillNo, ItemName, Quantity, CategoryName from ";

    //Queries for product details
    public static String prodlengthquery = "Select count(ItemName) from stock_details ";
    public static String prodquery = "Select ItemName, CategoryName from stock_details ";

    //Query for date details of each transaction of creditcustomerbill and otherbill
    public static String datesquery = "select BillNo, DateTime from ";

    //Generates all queries
    public static void genQueries(Calendar start, Calendar end,
            HashMap<String, ArrayList<String>> prod,
            HashMap<String, String> customers,
            ArrayList<Integer> suppliers) {
        //Create the required queries

        //Compare start and end so that end is after start
        if (start.after(end)) {
            Calendar temp = start;
            start = end;
            end = temp;
        }
        //Empty the limit queries
        datelimitquery = "";
        prodlimitquery = "";

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start1 = df.format(start.getTime()), end1 = df.format(end.getTime());

        //Make date limit query
        datelimitquery = " (DateTime BETWEEN \"" + start1 + "\" AND \""
                + end1 + "\") ";

        //Make prod limit query
        if (prod != null) {
            if (!prod.isEmpty()) {
                Set<String> keys = prod.keySet();
                Iterator<String> it = keys.iterator();
                String key = it.next();

                ArrayList<String> arr = prod.get(key);
                int index = 0;
                prodlimitquery += " ((CategoryName LIKE \"%" + key + "%\" AND ItemName LIKE \"%"
                        + arr.get(0) + "%\")";
                for (index = 1; index < arr.size(); index++) {
                    prodlimitquery += "OR (CategoryName LIKE \"%" + key + "%\" AND ItemName LIKE \"%"
                            + arr.get(index) + "%\")";
                }
                while (it.hasNext()) {
                    key = it.next();
                    arr = prod.get(key);
                    for (index = 1; index < arr.size(); index++) {
                        prodlimitquery += "OR (CategoryName LIKE \"%" + key + "%\" AND ItemName LIKE \"%"
                                + arr.get(index) + "%\")";
                    }
                }
                prodlimitquery += ")";

            } else {
                prodlimitquery += " (CategoryName LIKE \"%%\" AND ItemName LIKE \"%%\")";
            }
        } else {
            prodlimitquery += " (CategoryName LIKE \"%%\" AND ItemName LIKE \"%%\")";
        }

        //Empty customer limit queries
        custlimitquerycredit = "";
        custlimitqueryother = "";

        if (customers != null) {
            if (!customers.isEmpty()) {
                Set<String> keys = customers.keySet();
                Iterator<String> it = keys.iterator();
                String key = it.next();

                int index = 0;
                custlimitquerycredit += " ((first_name LIKE \"%" + key + "%\" AND contact_no LIKE \"%"
                        + customers.get(key) + "%\")";
                custlimitqueryother += " ((credit_Customer_Name LIKE \"%"
                        + key + "%\" AND mobile_No LIKE \"%"
                        + customers.get(key) + "%\")";

                while (it.hasNext()) {
                    key = it.next();
                    custlimitquerycredit += "OR (first_name LIKE \"%" + key + "%\" AND contact_no LIKE \"%"
                            + customers.get(key) + "%\")";
                    custlimitqueryother += "OR (credit_Customer_Name LIKE \"%" + key
                            + "%\" AND mobile_No LIKE \"%"
                            + customers.get(key) + "%\")";
                }
                custlimitquerycredit += ")";
                custlimitqueryother += ")";

            } else {
                custlimitquerycredit += " (first_name LIKE \"%%\" AND contact_no LIKE \"%%\")";
                custlimitqueryother += " (credit_Customer_Name LIKE \"%%\" AND mobile_No LIKE \"%%\")";
            }
        } else {
            custlimitquerycredit += " (first_name LIKE \"%%\" AND contact_no LIKE \"%%\")";
            custlimitqueryother += " (credit_Customer_Name LIKE \"%%\" AND mobile_No LIKE \"%%\")";
        }
        
        supplimitquery = "";
        suppgoodlimitquery = "";

        if (suppliers != null) {
            if (!suppliers.isEmpty()) {
                Iterator<Integer> it = suppliers.iterator();
                Integer key = it.next();

                int index = 0;
                supplimitquery += " ((supplier_id=" + key + ")";
                suppgoodlimitquery += " ((FksuppId=" + key + ")";

                while (it.hasNext()) {
                    key = it.next();
                    supplimitquery += " OR (supplier_id=" + key + ")";
                    suppgoodlimitquery += " OR (FksuppId=" + key + ")";
                }
                supplimitquery += ")";
                suppgoodlimitquery += ")";

            } else {
                supplimitquery += "1=1";
                suppgoodlimitquery += "1=1";
            }
        } else {
            supplimitquery += "1=1";
            suppgoodlimitquery += "1=1";
        }
    }

    public static void connectDB(Calendar start, Calendar end,
            HashMap<String, ArrayList<String>> prod,
            HashMap<String, String> customers,
            ArrayList<Integer> suppliers) {

        genQueries(start, end, prod, customers, suppliers);
        
        if(connection!=null){
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(DATABASE_URL, "root", "root");

            String message = "";
            statement = connection.createStatement();
            connected = true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed", "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            closeDB();
            JOptionPane.showMessageDialog(null, "Database Class Failed", "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void closeDB() {
        if (connected == true) {
            try {
                //resultset.close();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Could not close database", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static String[][] retrieveTrans() {
        int length = 0;
        String[][] trans;
        /*System.out.println(transquery+tables[0]+" where "+
                        datelimitquery+" AND "+prodlimitquery);*/
        try {
            //Length of the trans String 2-d array
            for (String s : tables) {
                resultset = statement.executeQuery(translengthquery + s + " where "
                        + datelimitquery + " AND " + prodlimitquery);
                resultset.next();
                length += resultset.getInt(1);
            }
            trans = new String[length][];

            //Store in each 1-d array in the trans String 2-d array
            Set<String> subtrans = new HashSet<String>();
            int index = 0, temp = -1;
            System.out.println(transquery + tables[0] + " where "
                    + datelimitquery + " AND " + prodlimitquery);
            for (String s : tables) {
                index = -1;
                resultset = statement.executeQuery(transquery + s + " where "
                        + datelimitquery + " AND " + prodlimitquery);
                while (resultset.next()) {
                    if (index != resultset.getInt(1)) {
                        index = resultset.getInt(1);
                        if (temp != -1) {
                            trans[temp] = subtrans.toArray(new String[0]);
                        }
                        temp++;
                        subtrans = new HashSet<String>();
                    }
                    subtrans.add(resultset.getString(2) + "(" + resultset.getString(3) + ")");
                }
            }
            if (temp != -1) {
                trans[temp] = subtrans.toArray(new String[0]);
            }
            return trans;

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static Calendar[] retrieveDates() {
        int length = 0;
        Calendar[] dates;
        try {
            //Length of the trans String 2-d array
            for (String s : tables) {
                resultset = statement.executeQuery(translengthquery + s
                        + " where " + datelimitquery + " AND " + prodlimitquery);
                resultset.next();
                length += resultset.getInt(1);
            }
            dates = new Calendar[length];

            //Store in each 1-d array in the trans String 2-d array
            Set<String> subdates = new HashSet<String>();
            int index = 0, temp = 0;
            for (String s : tables) {
                index = -1;
                resultset = statement.executeQuery(datesquery + s
                        + " where " + datelimitquery
                        + " AND " + prodlimitquery);
                while (resultset.next()) {
                    if (index != resultset.getInt(1)) {
                        index = resultset.getInt(1);
                        if (temp != -1) {
                            dates[temp] = AssMethods.getCalendar(resultset.getString(2));
                            //System.out.println(temp+"=>"+dates[temp]);
                        }
                        temp++;
                    }
                }
            }
            return dates;

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static HashMap<Integer, HashMap<String, Integer>> retrieveTransQuant() {
        int length = 0;
        String[][] trans = new String[0][];
        HashMap<Integer, HashMap<String, Integer>> transquant
                = new HashMap<Integer, HashMap<String, Integer>>();

        try {

            //Store in each bill number the product and its quantity
            HashMap<String, Integer> subtransquant = new HashMap<String, Integer>();
            int index = 0, temp = -1;
            for (String s : tables) {
                index = -1;
                resultset = statement.executeQuery(transquantquery
                        + s + " where " + datelimitquery + " AND " + prodlimitquery);
                while (resultset.next()) {
                    if (index != resultset.getInt(1)) {
                        index = resultset.getInt(1);
                        if (temp != -1) {
                            transquant.put(temp, subtransquant);
                        }
                        temp++;
                        subtransquant = new HashMap<String, Integer>();
                    }
                    if (subtransquant.get(resultset.getString(2) + "(" + resultset.getString(4) + ")") == null) {
                        subtransquant.put(resultset.getString(2) + "(" + resultset.getString(4) + ")",
                                resultset.getInt(3));
                    } else {
                        subtransquant.put(resultset.getString(2) + "(" + resultset.getString(4) + ")",
                                subtransquant.get(resultset.getString(2) + "(" + resultset.getString(4) + ")")
                                + resultset.getInt(3));
                    }
                }
            }
            transquant.put(temp, subtransquant);
            System.out.println("\n\n");
            return transquant;

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String[] retrieveProd() {
        String[] products;
        try {
            int length = 0;
            resultset = statement.executeQuery(prodlengthquery + " where " + prodlimitquery);
            resultset.next();
            length = resultset.getInt(1);
            products = new String[length];

            resultset = statement.executeQuery(prodquery + " where " + prodlimitquery);
            int index = 0;
            while (resultset.next()) {
                products[index] = resultset.getString(1) + "(" + resultset.getString(2) + ")";
                index++;
            }

            return products;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMethods.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //QUERIES FOR SIMILAR CLUSTERS 
    //Use credit first and then other to maintain uniformity
    public static final String creditCustomerquery = "select first_name, contact_no, BillNo, "
            + "Date, ItemName, Quantity, CategoryName "
            + "from customer_details "
            + "Inner Join creditcustomerbill "
            + "on customer_details.pk_customer_id=creditcustomerbill.fkCrediCustId ";

    public static final String creditCustextra = " group by first_name, contact_no ";

    public static final String creditCountquery = "select count(*) "
            + "from customer_details "
            + "Inner Join creditcustomerbill "
            + "on customer_details.pk_customer_id=creditcustomerbill.fkCrediCustId ";

    public static final String otherCustomerquery = "select credit_Customer_Name, mobile_No, BillNo, "
            + "Date, ItemName, Quantity, CategoryName "
            + "from otherbill ";

    public static final String otherCustextra = " group by credit_Customer_Name, mobile_No ";

    public static final String otherCountquery = "select count(*) "
            + "from otherbill ";

    public static Customer[] retrieveCustList() {
        ArrayList<Customer> custlist = new ArrayList<Customer>();
        //Temporary variable
        String[] custlisttemp = new String[2];
        //Set would be inserted into this (if necessary)
        Customer[] custliststr = new Customer[0];
        int index = 0;

        try {
            resultset = statement.executeQuery(creditCountquery + " where "
                    + prodlimitquery + creditCustextra);
            resultset.next();
            int creditlength = resultset.getInt(1);
            resultset = statement.executeQuery(otherCountquery + " where "
                    + prodlimitquery + otherCustextra);
            resultset.next();
            int otherlength = resultset.getInt(1);

            int mainlength = creditlength + otherlength;

            resultset = statement.executeQuery(creditCustomerquery + " where "
                    + prodlimitquery + creditCustextra);
            while (resultset.next()) {
                custlist.add(new Customer(resultset.getString(1), resultset.getString(2)));
            }
            resultset = statement.executeQuery(otherCustomerquery + " where "
                    + prodlimitquery + otherCustextra);
            while (resultset.next()) {
                custlist.add(new Customer(resultset.getString(1), resultset.getString(2)));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever2.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return custlist.toArray(new Customer[0]);
    }

    public static int[][] retrieveCustMatrix(String[] prod, Customer[] cust) {
        if ((prod == null) || (cust == null)) {
            return null;
        }
        int[][] custmatrix = new int[0][];
        Customer[] creditcust, othercust;
        int[] creditbill, otherbill;
        String[] creditdate, otherdate;
        String[] credititem, otheritem;
        int[] creditquant, otherquant;

        int creditlength = 0, otherlength = 0, mainlength = 0;

        try {
            //System.out.println(creditCountquery+ prodlimitquery);
            resultset = statement.executeQuery(creditCountquery + " where " + prodlimitquery);
            resultset.next();
            creditlength = resultset.getInt(1);
            resultset = statement.executeQuery(otherCountquery + " where " + prodlimitquery);
            resultset.next();
            otherlength = resultset.getInt(1);

            mainlength = creditlength + otherlength;
            creditcust = new Customer[creditlength];
            othercust = new Customer[otherlength];
            creditbill = new int[creditlength];
            otherbill = new int[otherlength];
            creditdate = new String[creditlength];
            otherdate = new String[otherlength];
            credititem = new String[creditlength];
            otheritem = new String[otherlength];
            creditquant = new int[creditlength];
            otherquant = new int[otherlength];

            resultset = statement.executeQuery(creditCustomerquery + " where " + prodlimitquery);
            int i = 0;
            while (resultset.next()) {
                creditcust[i] = new Customer(resultset.getString(1),
                        resultset.getString(2));
                creditbill[i] = resultset.getInt(3);
                creditdate[i] = resultset.getString(4);
                credititem[i] = resultset.getString(5) + "(" + resultset.getString(7) + ")";
                creditquant[i] = resultset.getInt(6);
                i++;
                //System.out.println(creditname[i]+", "+creditcont[i]+" "+creditbill[i]+" "
                //    +creditdate[i]+" "+credititem[i]+" "+creditquant[i]);
            }

            resultset = statement.executeQuery(otherCustomerquery + " where " + prodlimitquery);
            i = 0;
            while (resultset.next()) {
                othercust[i] = new Customer(resultset.getString(1),
                        resultset.getString(2));
                otherbill[i] = resultset.getInt(3);
                otherdate[i] = resultset.getString(4);
                otheritem[i] = resultset.getString(5) + "(" + resultset.getString(7) + ")";
                otherquant[i] = resultset.getInt(6);
                i++;
                //System.out.println(othername[i]+", "+othercont[i]+" "+otherbill[i]+" "
                //    +otherdate[i]+" "+otheritem[i]+" "+otherquant[i]);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever2.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        int custlength = cust.length, prodlength = prod.length;
        creditlength = creditbill.length;
        otherlength = otherbill.length;
        custmatrix = new int[custlength][prodlength];

        //Initialize custmatrix
        for (int i = 0; i < custlength; i++) {
            for (int j = 0; j < prodlength; j++) {
                custmatrix[i][j] = 0;
            }
        }

        //Temporary variables
        Customer tempcust;
        String tempprod = "";
        for (int i = 0; i < custlength; i++) {
            for (int j = 0; j < prodlength; j++) {
                tempcust = new Customer(cust[i].getName(), cust[i].getContact());
                tempprod = prod[j];

                for (int k = 0; k < creditlength; k++) {
                    /*System.out.println(tempname+" "+creditname[k]+"\t"
                    +tempcont+" "+creditcont[k]+"\t"
                    +tempprod+" "+credititem[k]+" "+creditquant[k]+" "
                    +custmatrix[i][j]);*/
                    if ((tempcust.equals(creditcust[k]))
                            && (tempprod.equals(credititem[k]))) {
                        //System.out.println(tempname+" "+tempcont+" "+tempprod);
                        custmatrix[i][j] += creditquant[k];
                    }
                }
            }
        }
        for (int i = 0; i < custlength; i++) {
            for (int j = 0; j < prodlength; j++) {
                tempcust = new Customer(cust[i].getName(), cust[i].getContact());
                tempprod = prod[j];
                for (int k = 0; k < otherlength; k++) {
                    /*System.out.println(tempname+" "+othername[k]+"\t"
                    +tempcont+" "+othercont[k]+"\t"
                    +tempprod+" "+otheritem[k]);*/
                    if ((tempcust.equals(othercust[k]))
                            && (tempprod.equals(otheritem[k]))) {
                        //System.out.println(tempname+" "+tempcont+" "+tempprod);
                        custmatrix[i][j] += otherquant[k];
                    }
                }
            }
        }

        return custmatrix;
    }

    //Speicalized Cluster Matrix
    //Dim 1: Customer, Dim 2: Product buying frequency
    //DIm 3: Average number of product bought per transaction
    public static double[][][] retrieveSpecCustMatrix(String[] prod, Customer[] cust) {
        if ((prod == null) || (cust == null)) {
            return null;
        }
        double[][][] custmatrix = new double[0][][];
        Customer[] creditcust, othercust;
        int[] creditbill, otherbill;
        String[] creditdate, otherdate;
        String[] credititem, otheritem;
        int[] creditquant, otherquant;

        int creditlength = 0, otherlength = 0, mainlength = 0;

        try {
            resultset = statement.executeQuery(creditCountquery + " where " + prodlimitquery);
            resultset.next();
            creditlength = resultset.getInt(1);
            resultset = statement.executeQuery(otherCountquery + " where " + prodlimitquery);
            resultset.next();
            otherlength = resultset.getInt(1);

            mainlength = creditlength + otherlength;
            creditcust = new Customer[creditlength];
            othercust = new Customer[otherlength];
            creditbill = new int[creditlength];
            otherbill = new int[otherlength];
            creditdate = new String[creditlength];
            otherdate = new String[otherlength];
            credititem = new String[creditlength];
            otheritem = new String[otherlength];
            creditquant = new int[creditlength];
            otherquant = new int[otherlength];

            resultset = statement.executeQuery(creditCustomerquery + " where " + prodlimitquery);
            int i = 0;
            while (resultset.next()) {
                creditcust[i] = new Customer(resultset.getString(1),
                        resultset.getString(2));
                creditbill[i] = resultset.getInt(3);
                creditdate[i] = resultset.getString(4);
                credititem[i] = resultset.getString(5) + "(" + resultset.getString(7) + ")";
                creditquant[i] = resultset.getInt(6);
                i++;
                //System.out.println(creditname[i]+", "+creditcont[i]+" "+creditbill[i]+" "
                //    +creditdate[i]+" "+credititem[i]+" "+creditquant[i]);
            }

            resultset = statement.executeQuery(otherCustomerquery + " where " + prodlimitquery);
            i = 0;
            while (resultset.next()) {
                othercust[i] = new Customer(resultset.getString(1),
                        resultset.getString(2));
                otherbill[i] = resultset.getInt(3);
                otherdate[i] = resultset.getString(4);
                otheritem[i] = resultset.getString(5) + "(" + resultset.getString(7) + ")";
                otherquant[i] = resultset.getInt(6);
                i++;
                //System.out.println(othername[i]+", "+othercont[i]+" "+otherbill[i]+" "
                //    +otherdate[i]+" "+otheritem[i]+" "+otherquant[i]);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever2.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        int custlength = cust.length, prodlength = prod.length;
        creditlength = creditbill.length;
        otherlength = otherbill.length;
        custmatrix = new double[custlength][prodlength][2];

        //Count matrix
        int[][] countmatrix = new int[custlength][prodlength];

        //Initialize custmatrix
        for (int i = 0; i < custlength; i++) {
            for (int j = 0; j < prodlength; j++) {
                //0 position is for keeping track of number of transactions
                custmatrix[i][j][0] = 0;
                //1 position is for keeping track of average number of product per transaction
                custmatrix[i][j][1] = 0;
                countmatrix[i][j] = 0;
            }
        }

        //Temporary variables
        Customer tempcust;
        String tempprod = "";
        for (int i = 0; i < custlength; i++) {
            for (int j = 0; j < prodlength; j++) {
                tempcust = new Customer(cust[i].getName(), cust[i].getContact());
                tempprod = prod[j];

                for (int k = 0; k < creditlength; k++) {
                    /*System.out.println(tempname+" "+creditname[k]+"\t"
                    +tempcont+" "+creditcont[k]+"\t"
                    +tempprod+" "+credititem[k]+" "+creditquant[k]+" "
                    +custmatrix[i][j]);*/
                    if ((tempcust.equals(creditcust[k]))
                            && (tempprod.equals(credititem[k]))) {
                        //System.out.println(tempname+" "+tempcont+" "+tempprod);
                        custmatrix[i][j][1] += creditquant[k];
                        custmatrix[i][j][0]++;
                    }
                    countmatrix[i][j]++;
                }
            }
        }
        for (int i = 0; i < custlength; i++) {
            for (int j = 0; j < prodlength; j++) {
                tempcust = new Customer(cust[i].getName(), cust[i].getContact());
                tempprod = prod[j];
                for (int k = 0; k < otherlength; k++) {
                    /*System.out.println(tempname+" "+othername[k]+"\t"
                    +tempcont+" "+othercont[k]+"\t"
                    +tempprod+" "+otheritem[k]);*/
                    if ((tempcust.equals(othercust[k]))
                            && (tempprod.equals(otheritem[k]))) {
                        //System.out.println(tempname+" "+tempcont+" "+tempprod);
                        custmatrix[i][j][1] += otherquant[k];
                        custmatrix[i][j][0]++;
                    }
                    countmatrix[i][j]++;
                }
            }
        }

        for (int i = 0; i < custlength; i++) {
            for (int j = 0; j < prodlength; j++) {
                custmatrix[i][j][0] /= countmatrix[i][j];
                custmatrix[i][j][1] /= countmatrix[i][j];
            }
        }

        return custmatrix;
    }

    //QUERIES FOR INVENTORY MANAGEMENT
    //Queries for product transactions
    public static final String creditProdquery = "select ItemName, CategoryName, "
            + "DateTime, Quantity from ";

    public static final String otherProdquery = "select ItemName, CategoryName, "
            + "DateTime, Quantity from ";

    public static HashMap<String, HashMap<Calendar, Integer>> getProdAssociations() {
        HashMap<String, HashMap<Calendar, Integer>> prodass1
                = new HashMap<String, HashMap<Calendar, Integer>>();
        //Sub-HashMap of prodass
        HashMap<Calendar, Integer> subprodass1;

        try {
            for (int i = 0; i < tables.length; i++) {
                resultset = statement.executeQuery(creditProdquery + tables[i] + " where "
                        + datelimitquery + " AND " + prodlimitquery);
                while (resultset.next()) {
                    String prod = resultset.getString(1) + "(" + resultset.getString(2) + ")";
                    Calendar date = PeakMethods.setDefCal(
                            AssMethods.getCalendar(resultset.getString(3)));
                    int quant = resultset.getInt(4);

                    if (prodass1.get(prod) == null) {
                        subprodass1 = new HashMap<Calendar, Integer>();
                        subprodass1.put(date, quant);
                        prodass1.put(prod, subprodass1);
                    } else {
                        //Temporary variable
                        subprodass1 = prodass1.get(prod);
                        if (subprodass1.get(date) == null) {
                            subprodass1.put(date, quant);
                        } else {
                            subprodass1.put(date, subprodass1.get(date) + quant);
                        }
                        prodass1.put(prod, subprodass1);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever3.class.getName()).log(Level.SEVERE, null, ex);
        }

        return prodass1;
    }

    //QUERIES FOR CUSTOMER LOYALTY
    //creditprodquery and otherprodquery already defined above
    public static final String prodExpquery = "select ItemName, CategoryName, ExpiryDays "
            + "from stock_details ";

    //Limit queries for customers
    public static String custlimitquerycredit = "";
    public static String custlimitqueryother = "";

    public static Customer[] retrieveCustProdList() {
        ArrayList<Customer> custlist = new ArrayList<Customer>();
        //Temporary variable
        String[] custlisttemp = new String[2];
        //Set would be inserted into this (if necessary)
        Customer[] custliststr = new Customer[0];
        int index = 0;

        try {
            resultset = statement.executeQuery(creditCountquery + " where " + custlimitquerycredit
                    + creditCustextra);
            int creditlength = 0;
            if (resultset.next()) {
                creditlength = resultset.getInt(1);
            }
            resultset = statement.executeQuery(otherCountquery + " where " + custlimitqueryother
                    + otherCustextra);
            int otherlength = 0;
            if (resultset.next()) {
                otherlength = resultset.getInt(1);
            }

            int mainlength = creditlength + otherlength;

            resultset = statement.executeQuery(creditCustomerquery + " where " + custlimitquerycredit
                    + creditCustextra);
            while (resultset.next()) {
                custlist.add(new Customer(resultset.getString(1), resultset.getString(2)));
            }
            resultset = statement.executeQuery(otherCustomerquery + " where " + custlimitqueryother
                    + otherCustextra);
            while (resultset.next()) {
                custlist.add(new Customer(resultset.getString(1), resultset.getString(2)));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever2.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return custlist.toArray(new Customer[0]);
    }

    public static HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> getCustProd() {
        HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>> custprod1
                = new HashMap<Customer, HashMap<Calendar, HashMap<String, Integer>>>();

        //Temp variables of custprod1
        //Map of Date to Map of product to quantity
        HashMap<Calendar, HashMap<String, Integer>> subcustprod1
                = new HashMap<Calendar, HashMap<String, Integer>>();
        //Map of Product in a date to its quantity
        HashMap<String, Integer> sub2custprod1 = new HashMap<String, Integer>();

        Customer custname;
        String name, contact, item, category, itemname;
        Calendar date;
        int quantity;

        String[] prodquery = {creditProdquery, otherProdquery};
        String[] prodlimitquery = {custlimitquerycredit, custlimitqueryother};

        try {
            for (int i = 0; i < prodquery.length; i++) {
                resultset = statement.executeQuery(prodquery[i] + " where " + prodlimitquery[i]);
                while (resultset.next()) {
                    name = resultset.getString(1);
                    contact = resultset.getString(2);
                    custname = new Customer(name, contact);
                    date = AssMethods.getCalendar(resultset.getString(3));
                    item = resultset.getString(4);
                    category = resultset.getString(5);
                    itemname = item + "(" + category + ")";
                    quantity = resultset.getInt(6);

                    if (CustLoyAMethods.findCustProdPresent(custprod1, custname) == null) {
                        subcustprod1 = new HashMap<Calendar, HashMap<String, Integer>>();
                        sub2custprod1 = new HashMap<String, Integer>();

                        sub2custprod1.put(itemname, quantity);
                        subcustprod1.put(date, sub2custprod1);
                        custprod1.put(custname, subcustprod1);
                    } else {
                        subcustprod1 = CustLoyAMethods.findCustProdPresent(custprod1, custname);
                        if (subcustprod1.get(date) == null) {
                            sub2custprod1 = new HashMap<String, Integer>();
                            sub2custprod1.put(itemname, quantity);
                        } else {
                            sub2custprod1 = subcustprod1.get(date);
                            if (sub2custprod1.get(itemname) == null) {
                                sub2custprod1.put(itemname, quantity);
                            } else {
                                sub2custprod1.put(itemname,
                                        sub2custprod1.get(itemname) + quantity);
                            }
                        }
                        subcustprod1.put(date, sub2custprod1);

                        Set<Customer> keys = custprod1.keySet();
                        Iterator<Customer> it = keys.iterator();
                        while (it.hasNext()) {
                            Customer key = it.next();
                            if (key.equals(custname)) {
                                custprod1.put(key, subcustprod1);
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever2.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return custprod1;
    }

    public static HashMap<String, Integer> getProdExpiry() {
        HashMap<String, Integer> prodexp1 = new HashMap<String, Integer>();

        try {
            resultset = statement.executeQuery(prodExpquery);

            while (resultset.next()) {
                String prod = resultset.getString(1) + "(" + resultset.getString(2) + ")";
                int exp = resultset.getInt(3);
                prodexp1.put(prod, exp);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever4.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return prodexp1;
    }

    //QUERIES FOR SUPPLIER PATTERNS
    public static final String suppNamequery = "select supplier_id, supplier_name "
            + "from supplier_details ";

    public static final String suppGoodquery = "select FksuppId, DateTime, ItemName, "
            + "CategoryName, Quantity from goodreceive ";

    public static String supplimitquery = "";
    public static String suppgoodlimitquery = "";
    
    public static HashMap<Integer, String> getSupp(){
        HashMap<Integer, String> supp = new HashMap<Integer, String>();
        
        try{
            resultset = statement.executeQuery(suppNamequery+" where "+supplimitquery);
            while(resultset.next()){
                supp.put(resultset.getInt(1), resultset.getString(2));
            }
            
            return supp;
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever5.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    //Transactions with each supplier
    public static HashMap<Integer, HashMap<Calendar, HashMap<String, Integer>>> getSuppTrans(){
        HashMap<Integer, HashMap<Calendar, HashMap<String, Integer>>> supptrans = 
                new HashMap<Integer, HashMap<Calendar, HashMap<String, Integer>>>();
        
        //Sub-Map from date of transaction to map of product to its quantity in transaction
        HashMap<Calendar, HashMap<String, Integer>> subsupptrans = 
                new HashMap<Calendar, HashMap<String, Integer>>();
        //Map from product to its quantity in transaction
        HashMap<String, Integer> sub2supptrans = new HashMap<String, Integer>();
        
        try{
            resultset = statement.executeQuery(suppGoodquery+" where "+datelimitquery+
                    " AND "+suppgoodlimitquery);
            
            while(resultset.next()){
                int supp = resultset.getInt(1);
                Calendar date = PeakMethods.setDefCal(
                        AssMethods.getCalendar(resultset.getString(2)));
                String prod = resultset.getString(3)+"("+resultset.getString(4)+")";
                int quant = resultset.getInt(5);
                
                if(supptrans.get(supp)==null){
                    sub2supptrans = new HashMap<String, Integer>();
                    sub2supptrans.put(prod, quant);
                    subsupptrans = new HashMap<Calendar, HashMap<String, Integer>>();                    
                }
                else{
                    subsupptrans = supptrans.get(supp);
                    if(subsupptrans.get(date)==null){
                        sub2supptrans = new HashMap<String, Integer>();
                        sub2supptrans.put(prod, quant);                        
                    }
                    else{
                        sub2supptrans = subsupptrans.get(date);
                        if(sub2supptrans.get(prod)==null){
                            sub2supptrans.put(prod, quant);
                        }
                        else{
                            sub2supptrans.put(prod, sub2supptrans.get(prod)+quant);
                        }
                    }
                }
                subsupptrans.put(date, sub2supptrans);
                supptrans.put(supp, subsupptrans);
                
            }
            return supptrans;
        }
        catch (SQLException ex) {
            Logger.getLogger(DatabaseRetriever5.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
        return null;
    }
    
}
