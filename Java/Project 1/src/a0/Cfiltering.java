// **********************************************************
// Assignment0:
// UTOR user_name: chohanmu
// UT Student #: 1002008105
// Author: Muhammad Ammar Chohan
//
//
// Honor Code: I pledge that this program represents my own
// program code and that I have coded on my own. I received
// help from no one in designing and debugging my program.
// I have also read the plagiarism section in the course info
// sheet of CSC 207 and understand the consequences.
// *********************************************************
package a0;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Cfiltering {
//Cfiltering class variables
  public int userMovieMatrix[][];
  public float userUserMatrix[][];
  private int numUsers, numMovies;
  private DecimalFormat convert = new DecimalFormat("0.0000");;

//this is default contructor which is not used.
  public Cfiltering() {
    //this.userMovieMatrix = new int[numUsers][numMovies];
    //this.userUserMatrix = new float[1][1];
  }

//constructor that is mainly used with parameters
  public Cfiltering(int numberOfUsers, int numberOfMovies) {
    numUsers = numberOfUsers;
    numMovies = numberOfMovies;
    this.userMovieMatrix = new int[numUsers][numMovies];
    this.userUserMatrix = new float[numUsers][numUsers];
  }

//CFiltering class method to return number of users/rows
  public int getNumUsers(){
    return numUsers;
  }

//CFiltering class method to return number of movies/colums
  public int getNumMovies(){
    return numMovies;
  }

//CFiltering class method to insert data is userUserMatrix
  public void populateUserMovieMatrix(int rowNumber, int columnNumber,
      int ratingValue){
    userMovieMatrix[rowNumber][columnNumber] = ratingValue;
  }

//CFiltering class method to calculate similarity among users rating
  public void calculateSimilarityScore() {
    int movie=0;
    int counter =0;
    int first=0;
    int second=0;
    double distance =0;
    this.userUserMatrix = new float[this.getNumUsers()][this.getNumUsers()];
    int UUMCounter1=0, UUMCounter2=0;
    
    while(counter<(this.getNumUsers() * this.getNumUsers())){
      while(second<this.getNumUsers()){
        while(movie<this.getNumMovies()){
            distance += Math.pow((this.userMovieMatrix[first][movie] - 
                this.userMovieMatrix[second][movie]), 2);
            movie++;            
        }
        distance = Math.sqrt(distance); 
        userUserMatrix[UUMCounter1][UUMCounter2] = 
            Float.parseFloat(convert.format((1/(1+distance))));
        UUMCounter2++;
        second++;
        counter++;
        movie=0;
        distance=0;
      }
      UUMCounter1++;
      UUMCounter2=0;
      first++;
      second=0;
    }
  }

//CFiltering class method to print userUserMatrix
  public void printUserUserMatrix() {
  int i=0;
  int j=0;
  System.out.println("\n"+"\n"+"\n"+"userUserMatrix is:");
  while(i<this.getNumUsers()){
    System.out.print("[");
    while (j<this.getNumUsers()){
      if(j==(this.getNumUsers()-1)){
        System.out.print(convert.format(this.userUserMatrix[i][j]));
        j++;
      }
      else{System.out.print(convert.format(this.userUserMatrix[i][j]) + ", ");}
      j++;
    }
    System.out.println("]");
    i++;
    j=0;
  }

  }

//CFiltering class method to calculate and return highest similarity with users
  public void findMostSimilarPairOfUsers() {
    int i=0; //row
    int j=i+1; //coluom

    int highestRow=0, highestCol=0;
    int highestRowA[]= new int[this.getNumUsers()*this.getNumUsers()];
    int highestColA[]= new int[this.getNumUsers()*this.getNumUsers()];
    float highestArray[] = new float[this.getNumUsers()*this.getNumUsers()];
    int highestArrayCounter=0;
    
    while(i<this.getNumUsers()){
       while (j<this.getNumUsers()){
         if(i!=j){
         if(this.userUserMatrix[i][j]>highestArray[highestArrayCounter]){
           highestRowA[highestRow]=i;
           highestColA[highestCol]=j;
           highestArray[highestArrayCounter]=this.userUserMatrix[i][j];
         }
         else if(this.userUserMatrix[i][j]==highestArray[highestArrayCounter]){
           highestRow++;
           highestCol++;
           highestArrayCounter++;
           highestRowA[highestRow]=i;
           highestColA[highestCol]=j;
           highestArray[highestArrayCounter]=this.userUserMatrix[i][j];
         }
         }
         j++;
       }
       i++;
       j=i+1;
     }
    
    int a=0;
    int b=0;
    float compareTo = highestArray[0];
    highestArrayCounter=0;
    System.out.println("\n"+"\n"+"\n"+
    "The most similar pairs of users from above userUserMatrix are:");
    while(highestArray[highestArrayCounter]!=0 && 
        highestArrayCounter<this.getNumUsers()*this.getNumUsers()){
      while(compareTo==highestArray[highestArrayCounter]){
        System.out.println("User"+ (highestRowA[a]+1) +" and "+
              "User"+(highestColA[b]+1)+",");
        a++;
        b++;
        highestArrayCounter++;
      }
      System.out.println("with similarity score of "+ 
          convert.format(compareTo));
      compareTo=highestArray[highestArrayCounter];
    }
  }

//CFiltering class method to calculate and return least similarity with users
  public void findMostDissimilarPairOfUsers() {
    int i=0; //row
    int j=i+1; //coluom

    int lowestRow=0, lowestCol=0;
    int lowestRowA[]= new int[this.getNumUsers()*this.getNumUsers()];
    int lowestColA[]= new int[this.getNumUsers()*this.getNumUsers()];
    float lowestArray[] = new float[this.getNumUsers()*this.getNumUsers()];
    int lowestArrayCounter=0;
    lowestArray[lowestArrayCounter]=1;

    while(i<this.getNumUsers()){
       while (j<this.getNumUsers()){
         if(i!=j){
         if(this.userUserMatrix[i][j]<lowestArray[lowestArrayCounter]){
           lowestRowA[lowestRow]=i;
           lowestColA[lowestCol]=j;
           lowestArray[lowestArrayCounter]=this.userUserMatrix[i][j];
         }
         else if(this.userUserMatrix[i][j]==lowestArray[lowestArrayCounter]){
           lowestRow++;
           lowestCol++;
           lowestArrayCounter++;
           lowestRowA[lowestRow]=i;
           lowestColA[lowestCol]=j;
           lowestArray[lowestArrayCounter]=this.userUserMatrix[i][j];
         }
         }
         j++;
       }
       i++;
       j=i+1;
     }
    
    int a=0;
    int b=0;
    float compareTo = lowestArray[0];
    lowestArrayCounter=0;
    System.out.println("\n"+"\n"+"\n"+
        "The most dissimilar pairs of users from above userUserMatrix are:");
    
    while(lowestArray[lowestArrayCounter]!=0 && 
        lowestArrayCounter<this.getNumUsers()*this.getNumUsers()){
      while(compareTo==lowestArray[lowestArrayCounter]){
        System.out.println("User"+ (lowestRowA[a]+1) +
            " and "+ "User"+(lowestColA[b]+1)+",");
        a++;
        b++;
        lowestArrayCounter++;
      }
      System.out.println("with similarity score of "+ 
          convert.format(compareTo));
      compareTo=lowestArray[lowestArrayCounter];
    }
  }

//main method
  public static void main(String args[]){
    String fileName;
    String line;
    FileInputStream fStream = null;
    Cfiltering CFilterObject = null;
    BufferedReader br;
    StringTokenizer token;
    int col=0, row=0;
    
    Scanner in = new Scanner(System.in);
    System.out.println("Enter the name of input file? ");
    fileName = in.nextLine();
    
    try {
        fStream = new FileInputStream(fileName);
        br = new BufferedReader(new InputStreamReader(fStream));
        
        CFilterObject = new Cfiltering(
            Integer.parseInt(br.readLine()),
            Integer.parseInt(br.readLine()));
        
        line = br.readLine(); //reading empty line
        
        while(line != null){
          line = br.readLine();
          token = new StringTokenizer(line, " ");
            while(col<5 &&token.hasMoreTokens()){
              String number = token.nextToken();
              CFilterObject.populateUserMovieMatrix(
                  row, col, Integer.parseInt(number));
              
              col = col+1;
            }
          col=0;
          row = row +1;
        }
        
    } catch (Exception e) {e.getMessage();}
    CFilterObject.calculateSimilarityScore();
    CFilterObject.printUserUserMatrix();
    CFilterObject.findMostSimilarPairOfUsers();
    CFilterObject.findMostDissimilarPairOfUsers();
  }
}
