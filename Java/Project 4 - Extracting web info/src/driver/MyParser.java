package driver;

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
