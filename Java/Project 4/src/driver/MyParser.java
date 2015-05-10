package driver;
// **********************************************************
// Assignment3:
// UTORID user_name: chohanmu
//
// Author: Muhammad Ammar Chohan
//
//
// Honor Code: I pledge that this program represents my own
// program code and that I have coded on my own. I received
// help from no one in designing and debugging my program.
// *********************************************************

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import source.Presentor;

public class MyParser {

  private static Set<String> coAuthorsNamesSet = new HashSet<String>();
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    if(args.length >= 2){
      FileWriter outputFile;
      try {
        outputFile = new FileWriter(args[1]);
        outputFile.write(Presentor.getScholarInfo(args).toString());
        outputFile.close();
      } catch (IOException e) {
        System.out.print("Error: Cannot write to a file.");
      }
    }
    else{
      System.out.print(Presentor.getScholarInfo(args));
    }
  }
  
  /**
   * @return coAuthorsNamesSet
   */
  public static Set<String> getCoAuthorsNamesSet(){
    return coAuthorsNamesSet;
  }
}