package simcustpackage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.StringReader;

public class KMeans 
{
  // Data members
  private double [][] _data; // Array of all records in dataset
  private int [] _label;  // generated cluster labels
  private int [] _withLabel; // if original labels exist, load them to _withLabel
                              // by comparing _label and _withLabel, we can compute accuracy. 
                              // However, the accuracy function is not defined yet.
  private double [][] _centroids; // centroids: the center of clusters
  private int _nrows, _ndims; // the number of rows and dimensions
  private int _numClusters; // the number of clusters;
  private int[] counts;

  // Constructor; loads records from file <fileName>. 
  // if labels do not exist, set labelname to null
  public KMeans(int[][] clust) 
  {
	  int trueLength = (int) (clust.length), sublength=0;
	  _data = new double[trueLength][];
	  for(int i=0; i<trueLength; i++){
              sublength = clust[i].length;
              _data[i] = new double[sublength];
              for(int j=0; j<sublength; j++){
                  _data[i][j]=clust[i][j];
              }
	  }
	  _nrows = trueLength;
	  _ndims = sublength;

  }
  
  // Perform k-means clustering with the specified number of clusters and
  // Eucliden distance metric. 
  // niter is the maximum number of iterations. If it is set to -1, the kmeans iteration is only terminated by the convergence condition.
  // centroids are the initial centroids. It is optional. If set to null, the initial centroids will be generated randomly.
  public void clustering(int numClusters, int niter, double [][] centroids) 
  {
      _numClusters = numClusters;
      if (centroids !=null)
          _centroids = centroids;
      else{
        // randomly selected centroids
        _centroids = new double[_numClusters][];

        ArrayList idx= new ArrayList();
        for (int i=0; i<numClusters; i++){
          int c;
          do{
            c = (int) (Math.random()*_nrows);
          }while(idx.contains(c)); // avoid duplicates
          idx.add(c);

          // copy the value from _data[c]
          _centroids[i] = new double[_ndims];
          for (int j=0; j<_ndims; j++)
            _centroids[i][j] = _data[c][j];
        }
        System.out.println("selected random centroids");

      }

      double [][] c1 = _centroids;
      double threshold = 0.001;
      int round=0;

      while (true){
        // update _centroids with the last round results
        _centroids = c1;

        //assign record to the closest centroid
        _label = new int[_nrows];
        for (int i=0; i<_nrows; i++){
          _label[i] = closest(_data[i]);
        }
        
        // recompute centroids based on the assignments  
        c1 = updateCentroids();
        round ++;
        if ((niter >0 && round >=niter) || converge(_centroids, c1, threshold))
          break;
      }

      System.out.println("Clustering converges at round " + round);
  }

  // find the closest centroid for the record v 
  private int closest(double [] v){
    double mindist = dist(v, _centroids[0]);
    int label =0;
    for (int i=1; i<_numClusters; i++){
      double t = dist(v, _centroids[i]);
      if (mindist>t){
        mindist = t;
        label = i;
      }
    }
    return label;
  }

  // compute Euclidean distance between two vectors v1 and v2
  private double dist(double [] v1, double [] v2){
    double sum=0;
    for (int i=0; i<_ndims; i++){
      double d = v1[i]-v2[i];
      sum += d*d;
    }
    return Math.sqrt(sum);
  }

  // according to the cluster labels, recompute the centroids 
  // the centroid is updated by averaging its members in the cluster.
  // this only applies to Euclidean distance as the similarity measure.

  private double [][] updateCentroids(){
    // initialize centroids and set to 0
    double [][] newc = new double [_numClusters][]; //new centroids 
    counts = new int[_numClusters]; // sizes of the clusters

    // intialize
    for (int i=0; i<_numClusters; i++){
      counts[i] =0;
      newc[i] = new double [_ndims];
      for (int j=0; j<_ndims; j++)
        newc[i][j] =0;
    }


    for (int i=0; i<_nrows; i++){
      int cn = _label[i]; // the cluster membership id for record i
      for (int j=0; j<_ndims; j++){
        newc[cn][j] += _data[i][j]; // update that centroid by adding the member data record
      }
      counts[cn]++;
    }

    // finally get the average
    for (int i=0; i< _numClusters; i++){
      for (int j=0; j<_ndims; j++){
        newc[i][j]/= counts[i];
      }
    } 

    return newc;
  }

  // check convergence condition
  // max{dist(c1[i], c2[i]), i=1..numClusters < threshold
  private boolean converge(double [][] c1, double [][] c2, double threshold){
    // c1 and c2 are two sets of centroids 
    double maxv = 0;
    for (int i=0; i< _numClusters; i++){
        double d= dist(c1[i], c2[i]);
        if (maxv<d)
            maxv = d;
    } 

    if (maxv <threshold)
      return true;
    else
      return false;
    
  }
  public double[][] getCentroids()
  {
    return _centroids;
  }
  
  public int[] getCounts(){
	  return counts;
  }

  public int [] getLabel()
  {
    return _label;
  }

  public int nrows(){
    return _nrows;
  }

  public void printResults(){
      System.out.println("Label:");
     for (int i=0; i<_nrows; i++)
        System.out.println(_label[i]);
      System.out.println("Centroids:");
     for (int i=0; i<_numClusters; i++){
    	System.out.print((i+1)+": ");
        for(int j=0; j<_ndims; j++)
           System.out.print(_centroids[i][j] + " ");
         System.out.println(counts[i]);
     }

  }


  /*public static void main( String[] astrArgs ) 
  {
    
     KMeans KM = new KMeans();
     KM.clustering(2, 10, null); // 2 clusters, maximum 10 iterations
     KM.printResults();

     /** using CSVHelper to parse strings
     CSVHelper csv = new CSVHelper();
     StringReader r= new StringReader("x,y,z");
     try{
       ArrayList<String> ss = csv.parseLine(r);
       for (String v:ss)
        System.out.println(v);
     }catch(Exception e){
       System.err.println(e);
     }
     

  }*/
}
